package priv.snail.validator.advice;

import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: Snail
 * @CreateTime: 18-10-31
 * @Description: 参数校验统一异常处理。
 */
@ControllerAdvice
@ResponseBody
public class ValidatorExceptionHandler{

    /**
     * 使用@ExceptionHandler注解，处理BindException异常。该异常是spring提供的参数校验失败时抛出的异常。
     * @param e 异常对象
     * @return 返回的http内容
     */
    @ExceptionHandler(value = BindException.class)
    public String exceptionHandler(BindException e){
        return e.getAllErrors().get(0).getDefaultMessage();  //参数校验会抛出多个异常，这里取第一个。而它的默认信息就是IsComplex注解中定义的message的默认值。
    }
}
