package com.lagou.edu.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

    /**
     * transactionManager：默认实现的事务类型
     * @return
     */
    String value() default "transactionManager";

}