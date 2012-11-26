package modules.orientdb;

import java.util.ArrayList;
import java.util.List;

import play.api.UnexpectedException;
import scala.Option;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.tx.OTransaction.TXTYPE;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import conf.DBConfiguration;

/**
 * This class wraps top level operations to work with an OrientDB Database (Object, Graph, Document)
 */
public class ODB {

    public enum DBTYPE {
        DOCUMENT, OBJECT
    };

    public static final List<Class<? extends ODatabaseListener>> listeners = new ArrayList<Class<? extends ODatabaseListener>>();
    public static final List<Class<? extends ORecordHook>> hooks = new ArrayList<Class<? extends ORecordHook>>();

    static final ThreadLocal<OObjectDatabaseTx> localObjectTx = new ThreadLocal<OObjectDatabaseTx>();
    static final ThreadLocal<ODatabaseDocumentTx> localDocumentTx = new ThreadLocal<ODatabaseDocumentTx>();
    static final ThreadLocal<OGraphDatabase> localGraphTx = new ThreadLocal<OGraphDatabase>();

    /**
     * This opens the requested database type (Document or Object) and starts a transaction
     * @param type - Transaction type
     * @param db - DatabaseType to open
     */
    public static void begin(TXTYPE type, DBTYPE db) {
        if (db == DBTYPE.DOCUMENT) {
            openDocumentDB().begin(type);
        } else {
            openObjectDB().begin(type);
        }
    }

    /**
     * Closes (or returns to the pool) all the opened databases (Document, Object, Graph) for the current thread
     */
    public static void close() {
        closeDocument();
        closeObject();
        closeGraph();
    }

    /**
     * Close the DocumentDatabase, if opened for the current thread.
     */
    public static void closeDocument() {
        if (hasDocumentTx()) {
            localDocumentTx.get().close();
            localDocumentTx.set(null);
        }
    }

    /**
     * Close the GraphDatabase, if opened for the current thread.
     */
    public static void closeGraph() {
        if (hasGraphTx()) {
            localGraphTx.get().close();
            localGraphTx.set(null);
        }
    }

    /**
     * Close the ObjectDatabase, if opened for the current thread.
     */
    public static void closeObject() {
        if (hasObjectTx()) {
            localObjectTx.get().close();
            localObjectTx.set(null);
        }
    }

    /**
     * Commits the transaction. Normally you will not need to call this.
     * Every method annotated with @Transactional will do the transaction initiation, commit and rollback
     * automatically
     */
    public static void commit() {
        if (hasObjectTx() && localObjectTx.get().getTransaction().isActive()) {
            localObjectTx.get().commit();
        }
        if (hasDocumentTx() && localDocumentTx.get().getTransaction().isActive()) {
            localDocumentTx.get().commit();
        }
    }

    /**
     * Creates a new or acquires an existing DocumentDatabase from the pool for the current Thread.
     * @return
     */
    public static ODatabaseDocumentTx openDocumentDB() {
    	DBConfiguration pluginConf = ODBPlugin.getInstance().getConf();
        if (!hasDocumentTx()) {
            ODatabaseDocumentTx db = ODatabaseDocumentPool.global().acquire(pluginConf.url, pluginConf.username,
            		pluginConf.password);
            localDocumentTx.set(db);
            registerListeners(db);
        }
        return localDocumentTx.get();
    }

    /**
     * Creates a new or acquires an existing GraphDatabase from the pool for the current Thread.
     * @return
     */
    public static OGraphDatabase openGraphDB() {
    	DBConfiguration pluginConf = ODBPlugin.getInstance().getConf();
        if (!hasGraphTx()) {
            OGraphDatabase db = OGraphDatabasePool.global().acquire(
                    (pluginConf.graphurl == null) ? pluginConf.url : pluginConf.graphurl, 
                    		pluginConf.username, pluginConf.password);
            localGraphTx.set(db);
            registerListeners(db);
        }
        return localGraphTx.get();
    }

    /**
     * Creates a new or acquires an existing ObjectDatabase from the pool for the current Thread.
     * @return
     */
    public static OObjectDatabaseTx openObjectDB() {
        if (!hasObjectTx() || localObjectTx.get().isClosed()) {
        	DBConfiguration pluginConf = ODBPlugin.getInstance().getConf();
        	OObjectDatabaseTx db = OObjectDatabasePool.global()
                    .acquire(pluginConf.url, pluginConf.username, pluginConf.password);
            localObjectTx.set(db);
            registerListeners(db);
            registerHooks(db);
        }
        return localObjectTx.get();
    }

    /**
     * Rolls back a transaction - if any was active
     */
    public static void rollback() {
        if (hasObjectTx() && localObjectTx.get().getTransaction().isActive()) {
            localObjectTx.get().rollback();
        }
        if (hasDocumentTx() && localDocumentTx.get().getTransaction().isActive()) {
            localDocumentTx.get().rollback();
        }
    }

    private static boolean hasDocumentTx() {
        return localDocumentTx.get() != null;
    }

    private static boolean hasGraphTx() {
        return localGraphTx.get() != null;
    }

    private static boolean hasObjectTx() {
        return localObjectTx.get() != null;
    }

    // XXX expensive operation, find a better solution
    private static void registerHooks(OObjectDatabaseTx db) {
        for (Class<? extends ORecordHook> hook : hooks) {
            db.registerHook((ORecordHook) newInstance(hook));
        }
    }

    // XXX expensive operation
    private static void registerListeners(ODatabase db) {
        for (Class<? extends ODatabaseListener> listener : listeners) {
            db.registerListener((ODatabaseListener) newInstance(listener));
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T newInstance(Class<?> appClass) {
        try {
            return (T) appClass.newInstance();
        } catch (InstantiationException e) {
        	throw new UnexpectedException(Option.apply(e.getMessage()), Option.apply(e.getCause()));
		} catch (IllegalAccessException e) {
			throw new UnexpectedException(Option.apply(e.getMessage()), Option.apply(e.getCause()));
		}
    }
}
