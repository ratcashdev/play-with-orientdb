package modules.orientdb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import modules.orientdb.actions.DBActionWrapper;
import play.mvc.With;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@With(DBActionWrapper.class)
public @interface ODBWrapper {

}
