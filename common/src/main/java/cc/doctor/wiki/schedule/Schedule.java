package cc.doctor.wiki.schedule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by doctor on 2017/3/8.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Schedule {
    long duration();
    int priority() default 10;  //优先级1-10逐级降低
    String name() default "";
}
