package nl.limesco.cserv.lib.quartz.annotations.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RepeatInterval {
    
    long MILLISECOND = 1;
    long SECOND = 1000 * MILLISECOND;
    long MINUTE = 60 * SECOND;
    long HOUR = 60 * MINUTE;
    long DAY = 24 * HOUR;

    long value();
    long period() default SECOND;
    
}
