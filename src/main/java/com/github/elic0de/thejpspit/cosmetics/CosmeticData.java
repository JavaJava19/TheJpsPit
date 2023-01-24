package com.github.elic0de.thejpspit.cosmetics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CosmeticData {
    String id() default "";

    String name() default "UNKNOWN";

    String description() default "No description provided";

    double coin() default 0;
}
