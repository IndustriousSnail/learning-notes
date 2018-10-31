package priv.snail.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ValidatorApplication {

    public static void main(String[] args) {
        /**
         * 自定义校验器的实现。
         * 1. 定义注解。 （@IsComplex）
         * 2. 定义注解校验器类。 （IsComplexValidator.class）
         * 3. 定义统一异常拦截器。 （ValidatorExceptionHandler）
         */
        SpringApplication.run(ValidatorApplication.class, args);
    }
}
