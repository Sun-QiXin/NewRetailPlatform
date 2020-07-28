package gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import gulimall.product.service.CategoryBrandRelationService;
import gulimall.product.vo.catagory2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import gulimall.common.utils.PageUtils;
import gulimall.common.utils.Query;

import gulimall.product.dao.CategoryDao;
import gulimall.product.entity.CategoryEntity;
import gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


/**
 * @author x3626
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询所有分类以及子分类，以树形列表展示
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查询出所有分类
        List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);

        //2、组装成父子的树形结构
        //2.1)找到所有的一级分类
        List<CategoryEntity> level1Menus = categoryEntityList.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0
        ).map(menu -> {
            //2.2)找到所有的一级分类的子菜单
            menu.setChildren(getChildren(menu, categoryEntityList));
            return menu;
        }).sorted((menu1, menu2) -> {
            //2.3)找到所有的子菜单并排序
            return menu1.getSort() - menu2.getSort();
        }).collect(Collectors.toList());
        return level1Menus;
    }

    /**
     * 递归查找每个主菜单的子菜单
     *
     * @param root 当前主菜单
     * @param all  所有菜单
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //再次查找当前菜单的子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //排序
            return menu1.getSort() - menu2.getSort();
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 删除时需要查看是否有其他地方使用它
     *
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 删除时需要查看是否有其他地方使用它
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 根据id查询整个分类的完整路径【父/子/孙】
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        //调用方法
        paths = findParentPath(catelogId, paths);
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }

    /**
     * @param catelogId
     * @param paths
     * @return
     */
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 级联更新所有关联数据
     * <br>@CacheEvict  缓存失效模式(一旦执行此方法，就会删除指定空间下指定key的缓存)
     * <br>@Caching 组合注解，用于执行多个其他缓存注解
     *
     * @param category
     */

    @Caching(evict = {
            @CacheEvict(cacheNames = "category", key = "'getLeve1Categorys'"),
            @CacheEvict(cacheNames = "category", key = "'getCatalogJson'")
    })
    //@CacheEvict(cacheNames = "category",allEntries = true)  //删除整个空间下的缓存
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 查出所有的1级分类
     *
     * <br>每一个需要缓存的数据我们都来指定要放到那个名字的缓存。【缓存的分区】
     * <br>@Cacheable 代表当前方法的结果需要缓存，如果缓存中有，方法不用调用。如果缓存中没有，会调用方法，最后将方法的结果再放入缓存
     * <br>3、默认行为：
     * <br>1)、如累缓存中有。方法不用调用。
     * <br> 2)、key默认白动生成，缓存的名字::SimpleKey(自主生成的key值)
     * <br>3)、缓存的value值。默认使用jdk序列化机制，将序列化后的数据存到redis
     * <br>4)默认ttl时间-1(永不失效)
     * <br>自定义：
     * <br>1)指定生成的key   使用注解的key属性指定，接收一个spel表达式
     * <br>2)指定生存的数据的ttl时间  配置文件中修改ttl时间
     * <br>3)将数据容存为json格式
     *
     * @return
     */
    @Cacheable(cacheNames = "category", key = "#root.methodName")
    @Override
    public List<CategoryEntity> getLeve1Categorys() {
        List<CategoryEntity> categoryEntities = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * <br>获取2级3级分类的json，加入redis缓存
     * <br>TODO 压力测试时会出现OutOfMemoryError 堆外内存溢出
     * <br>1)、springboot2.o以后默认使用Lettuce作为操作redis的客户端。它使用netty进行网络通信。
     * <br>2).lettuce的bug导致netty堆外内存溢出-Xmx300m; netty如果没有指定堆外内存，默认使用-Xmx300m
     * <br>可以通过-Dio.netty.maxDirectMemory进行设置
     * <br>解决方案:不能使用-Dio.netty.maxDirectMemory只去调大堆外内存,这样只会延缓出现的时间
     * <br>1)、升级Lettuce客户端。
     * <br>2）、切换使用jedis
     * <br>分布式锁：
     * <br>只要是同一把锁，就能锁住需要这个锁的所有线程
     * <br>1、synchronized (this):SpringBoot所有的组件在容器中都是单例的。
     * <br>2、本地锁:synchronized，uc(Lock)，在分布式情况下，想要锁住所有，必须使用分布式锁
     * <br> setIfAbsent就是SET NX，设置之前会查看有没有，没有才会保存成功（保存成功表示得到锁）
     *
     * <br> 最终我们使用redisson提供的分布式锁
     * <br>//1)、不指定过期时间会自动续期，如果业务超长，运行期间自动给锁续上新的30s。不用担心业务时间长，锁自动过期被删掉
     * * //2)、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除。
     *
     * @return
     */
    @Cacheable(cacheNames = "category", key = "#root.methodName")
    @Override
    public Map<String, List<catagory2Vo>> getCatalogJson() {
        //一、获取一把锁，只要锁的名字一样，就是同一把锁
        RLock lock = redissonClient.getLock("catalogJsonLock");
        //二、阻塞式等待。默认加的锁都是30s时间。
        //{1}、加锁:解决缓存击穿
        lock.lock(30, TimeUnit.SECONDS);
        try {
            //1、查出所有分类及子分类
            List<CategoryEntity> categoryEntityList = this.list();
            //System.out.println("查询数据库");

            //所有的1级菜单
            List<CategoryEntity> leve1Categorys = getParent_cid(categoryEntityList, 0L);

            //2、封装数据
            Map<String, List<catagory2Vo>> collect = leve1Categorys.stream().collect(Collectors.toMap(key -> key.getCatId().toString()
                    , value -> {
                        //查出这个1级分类的二级分类
                        List<CategoryEntity> category2Entities = getParent_cid(categoryEntityList, value.getCatId());
                        //封装上面的结果
                        List<catagory2Vo> catagory2Vos = null;
                        if (category2Entities != null && category2Entities.size() > 0) {
                            catagory2Vos = category2Entities.stream().map(category2Entity -> {
                                //找到当前遍历二级分类的三级分类信息
                                List<CategoryEntity> category3Entities = getParent_cid(categoryEntityList, category2Entity.getCatId());
                                List<catagory2Vo.catalog3Vo> catalog3Vos = null;
                                if (category3Entities != null && category3Entities.size() > 0) {
                                    //封装成catalog3Vo
                                    catalog3Vos = category3Entities.stream().map(category3Entity -> {
                                        catagory2Vo.catalog3Vo catalog3Vo = new catagory2Vo.catalog3Vo(category2Entity.getCatId().toString(), category3Entity.getCatId(), category3Entity.getName());
                                        return catalog3Vo;
                                    }).collect(Collectors.toList());
                                }

                                //封装成catagory2Vo
                                catagory2Vo catagory2Vo = new catagory2Vo(value.getCatId().toString(), catalog3Vos, category2Entity.getCatId(), category2Entity.getName());
                                return catagory2Vo;
                            }).collect(Collectors.toList());
                        }
                        return catagory2Vos;
                    }));
            return collect;
        } finally {
            //解锁
            lock.unlock();
            //System.out.println("删除分布式锁成功");
        }
    }

    /**
     * 从集合中找出parent_cid等于指定的id的菜单
     *
     * @param categoryEntityList
     * @param parent_cid
     * @return
     */
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> categoryEntityList, Long parent_cid) {
        List<CategoryEntity> collect = categoryEntityList.stream().filter(item -> item.getParentCid().equals(parent_cid)).collect(Collectors.toList());
        return collect;
    }
}