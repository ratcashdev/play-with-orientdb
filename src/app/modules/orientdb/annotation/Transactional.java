package modules.orientdb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;

import modules.orientdb.ODB.DBTYPE;
import modules.orientdb.actions.ODBTransactionWrapper;

import com.orientechnologies.orient.core.tx.OTransaction.TXTYPE;

/**
 * This annotation does not do anything else just 
 * adds Model.db().begin() statement to start a transaction.
 * The transaction will be committed automatically as soon as the method annotated with 
 * @Transactional ends, unless there was an exception, in which case it gets rolled back.
 * 
 */
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
@With(ODBTransactionWrapper.class)
public @interface Transactional {
    public DBTYPE db() default DBTYPE.OBJECT;

    public TXTYPE type() default TXTYPE.OPTIMISTIC;
}
