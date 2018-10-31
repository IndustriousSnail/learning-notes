package priv.snail.validator.VO;

import org.hibernate.validator.constraints.Length;
import priv.snail.validator.validator.annotation.IsComplex;

import javax.validation.constraints.NotNull;

/**
 * @Author: Snail
 * @CreateTime: 18-10-31
 * @Description:
 */
public class LoginVO {

    @NotNull  //参数校验，不能为空
    private String username;

    @NotNull
    @Length(min = 6)
    @IsComplex //增加自定义的验证器
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
