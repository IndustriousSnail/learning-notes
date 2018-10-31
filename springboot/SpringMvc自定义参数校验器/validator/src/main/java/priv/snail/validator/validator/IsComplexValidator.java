package priv.snail.validator.validator;

import priv.snail.validator.validator.annotation.IsComplex;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author: Snail
 * @CreateTime: 18-10-31
 * @Description: IsComplex校验器的具体实现。
 * @implSpec ConstraintValidator<A extends Annotation, T>: A：注解， T：A注解修改的类型
 */
public class IsComplexValidator implements ConstraintValidator<IsComplex, String> {

    /**
     * 初始化， 整个生命周期只会执行一次，在第一次验证时执行。
     * @param constraintAnnotation
     */
    @Override
    public void initialize(IsComplex constraintAnnotation) {
        System.out.println("initialize");
    }

    /**
     * 通过该方法，对参数进行验证，看是否通过。
     * @param value 修饰字段的值。
     * @param context 上下文
     * @return true：验证通过。 false：验证不通过。
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //判断密码是否同时包含大小写
        boolean hasUpper=false,hasLower=false;
        for(byte b: value.getBytes()){
            if('A'<=b&&b<='Z') hasUpper=true;
            if('a'<=b&&b<='z') hasLower=true;
        }
        return hasUpper&&hasLower;  //如果同时包含大小写，则验证通过。
    }
}
