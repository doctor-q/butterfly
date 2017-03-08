package cc.doctor.wiki.annotation;

/**
 * Created by doctor on 2017/3/8.
 */
public @interface Schedule {
    long duration();
    int priority() default 10;  //优先级1-10逐级降低
    String name() default "";
}
