package gulimall.member.exception;

/**
 * 手机号已存在异常
 * @author 孙启新
 * <br>FileName: UsernameExistException
 * <br>Date: 2020/08/02 12:27:45
 */
public class PhoneExistException extends RuntimeException{
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public PhoneExistException() {
        super("手机号已存在已存在");
    }
}
