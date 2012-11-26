package modules.orientdb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import modules.orientdb.ODB.DBTYPE;

import com.orientechnologies.orient.core.tx.OTransaction.TXTYPE;

/**
 * This does not work yet.
 * Instead use the Model.db().begin() statement to start a transaction.
 * The transaction will be commited automatically as soon as the method annotated with @DBWrapper ends.
 * 
 * Short answer
 * @author matyas.bene
 *
 */
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
@Deprecated
public @interface Transactional {
    public DBTYPE db() default DBTYPE.OBJECT;

    public TXTYPE type() default TXTYPE.OPTIMISTIC;
}
