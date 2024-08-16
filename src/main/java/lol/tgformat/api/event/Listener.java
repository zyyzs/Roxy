package lol.tgformat.api.event;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    byte value() default 2;
}
