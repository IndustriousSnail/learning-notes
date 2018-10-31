package priv.snail.validator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.snail.validator.VO.LoginVO;

import javax.validation.Valid;

/**
 * @Author: Snail
 * @CreateTime: 18-10-31
 * @Description:
 */
@RestController
public class LoginController {

    @GetMapping("/login")
    //增加@Valid注解，校验loginVO对象
    public Boolean login(@Valid LoginVO loginVO){
        //todo 登录处理 ....
        return true;
    }
}
