package priv.snail.validator.validator.annotation;

import priv.snail.validator.validator.IsComplexValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


/**
 * @Author: Snail
 * @CreateTime: 18-10-31
 * @Description:  自定义验证器，判断是否是复杂字符串。 如果为大小写混合，则为复杂字符串。
 */
@Target({ElementType.FIELD})  //Target注解描述了该注解需要可以修饰哪些内容。 这里指明只能修饰字段。
@Retention(RetentionPolicy.RUNTIME)  //Retention注解描述了该注解是在什么期间生效的。 这里指明是运行期生效。
@Documented   //表明这个注解应该被 javadoc工具记录
@Constraint(validatedBy = {IsComplexValidator.class})  //@Constraint注解就说明这个字段是要被经过验证的。 validateBy参数填入需要经过的验证器类。
public @interface IsComplex {

    String message() default "密码不够复杂"; //当校验不通过时的提示信息。 必须有

    Class<?>[] groups() default { };  //必须有

    Class<? extends Payload>[] payload() default { }; //必须有
}
