package com.rj.business.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Form {
    String value();

    String type() default "text";

    String maxlength() default "255";

    String minlength() default "0";

    String pattern() default "";

    String placeholder() default "";

    boolean autofocus() default false;

    boolean required() default false;

    boolean hideValueInTable() default false;

    boolean visible() default true;
}
