package com.rj.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) // The annotation will be available at runtime
public @interface Table {
    String name() default "";

    String value() default "";
}
