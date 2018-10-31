<h1>Spring MVC中参数校验器的使用和自定义参数校验器</h1>

项目案例代码：

目录
* [一、参数校验器简介](#1)
* [二、自定义参数校验器案例](#2)
    * [2.1 场景介绍](#2.1)
    * [2.2 参数校验具体实现](#2.2)
        * [2.2.1 为LoginVO增加注解](#2.2.1)
        * [2.2.2 在需要校验的VO类型增加@Valid注解](#2.2.2)
        * [2.2.3 自定义校验注解](#2.2.3)
        * [2.2.4 校验器的具体实现](#2.2.4)
        * [2.2.5 校验失败统一异常处理](#2.2.5)
        * [2.2.6 验证结果](#2.2.6)

<h2 id="1">一、参数校验器简介</h2>

在Http请求中会传递一些参数，对于这些参数，后台需要经过一些校验，如果校验不通过，就没有必要进行接下来的处理。
比如，验证账号密码是否为空等等。


Spring为我们提供了一些默认的参数校验器，比如@NotNull, @Length 等。
但是这些并不能满足所有需求，所以需要自定义参数校验器。

<h2 id="2">二、自定义参数校验器案例</h2>
<h3 id="2.1">2.1 场景介绍</h3>
假设我们现在需要对登录的“用户名”和“密码进行校验”。 
我们现在有如下Controller:

    

    @RestController
    public class LoginController {
    
        @GetMapping("/login")
        public Boolean login(LoginVO loginVO){
            //todo 登录处理 ....
            return true;
        }
    }
    
    // public class LoginVO {
    //     private String username;
    //     private String password;
    // }
    
我们需要对 loginVO 中的 username 和 password 字段进行校验。
如果在login()方法中进行该逻辑的编写，也可以，但是如果还存在其他的方法（如修改密码）需要对loginVO进行校验，那么就又要再写一遍。
这样就出现了重复逻辑。所以需要对参数进行统一校验。

该案例，以判断password是否同时包含大小写为例。进行自定义参数校验器的实现。

<h3 id="2.2">2.2 参数校验具体实现</h3>
<h4 id="2.2.1">2.2.1 为LoginVO增加注解</h4>

    @NotNull  //参数校验，不能为空
    private String username;

    @NotNull
    @Length(min = 6)  //长度不小于6
    @IsComplex //增加自定义的验证器
    private String password;
    
<h4 id="2.2.2">2.2.2 在需要校验的VO类型增加@Valid注解</h4>

    //增加@Valid注解，校验loginVO对象
    public Boolean login(@Valid LoginVO loginVO){}
    
>此时重启程序，发现如果传递的参数不对，会返回400错误。说明参数校验已经生效。

<h4 id="2.2.3">2.2.3 自定义校验注解</h4>

    @Target({ElementType.FIELD})  //Target注解描述了该注解需要可以修饰哪些内容。 这里指明只能修饰字段。
    @Retention(RetentionPolicy.RUNTIME)  //Retention注解描述了该注解是在什么期间生效的。 这里指明是运行期生效。
    @Documented   //表明这个注解应该被 javadoc工具记录
    @Constraint(validatedBy = {IsComplexValidator.class})  //@Constraint注解就说明这个字段是要被经过验证的。 validateBy参数填入需要经过的验证器类。
    public @interface IsComplex {
    
        String message() default "密码不够复杂"; //当校验不通过时的提示信息。 必须有
    
        Class<?>[] groups() default { };  //必须有
    
        Class<? extends Payload>[] payload() default { }; //必须有
    }

>定义该校验注解，使用该注解修饰字段。

<h4 id="2.2.4">2.2.4 校验器的具体实现</h4>

    /**
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
    
>1. 实现ConstraintValidator接口，重写其方法。
>2. 在isValid写具体校验逻辑。通过返回true,不通过返回false

<h4 id="2.2.5">2.2.5 校验失败统一异常处理</h4>
当校验失败时，会抛出BindException异常，http会响应400。
如果要做统一异常处理，可以使用ControllerAdvice捕获异常。

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
    

    
<h4 id="2.2.6">2.2.6 验证结果</h4>
此时在浏览器中输入
    
    http://localhost:8080/login?username=1111&password=AAAAA
    
可以看到浏览器打印的是“长度需要在6和2147483647之间”。
这个信息是@Length注解的message信息。

输入:
    
    http://localhost:8080/login?username=1111&password=AAAAAAA
    
浏览器打印“密码不够复杂”


输入

    http://localhost:8080/login?username=1111&password=AAAAAAAaaa
    
浏览器打印“true”
    
