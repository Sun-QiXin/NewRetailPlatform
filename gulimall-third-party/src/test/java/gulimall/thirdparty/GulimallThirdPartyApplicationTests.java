package gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Resource
    private OSSClient ossClient;

    /**
     * 测试使用阿里云的对象oss文件上传
     * 使用阿里云的对象存储步骤
     * 1、引入oss-starter
     * 2、在yml文件配置key, endpoint相关信息即可
     * 3、使osSClient进行相关操作
     * @throws FileNotFoundException
     */
    @Test
    public void testUpload() throws FileNotFoundException {
        // 上传文件流。
        InputStream inputStream = new FileInputStream("E:\\图片\\壁纸\\ChMlWV5cfoGIADyDABB0Ie_Y2VUAANhjQNgkf0AEHQ5884.jpg");
        ossClient.putObject("sunqixin-gulimal", "test2.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功！");
    }
}
