package gulimall.shoppingcart.to;

import java.io.Serializable;

/**
 * @author 孙启新
 * <br>FileName: UserInfoVo
 * <br>Date: 2020/08/05 11:51:53
 */
public class UserInfoTo implements Serializable {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 临时用户的key
     */
    private String userKey;
    /**
     * 是否有临时用户
     */
    private Boolean tempUser = false;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Boolean getTempUser() {
        return tempUser;
    }

    public void setTempUser(Boolean tempUser) {
        this.tempUser = tempUser;
    }

    @Override
    public String toString() {
        return "UserInfoTo{" +
                "userId=" + userId +
                ", userKey='" + userKey + '\'' +
                ", tempUser=" + tempUser +
                '}';
    }
}
