package modules.orientdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import modules.orientdb.annotation.ODBEntity;
import modules.orientdb.conf.DBConfiguration;
import play.Configuration;
import play.Logger;
import play.Logger.ALogger;
import play.Play;
import play.Plugin;
import play.api.UnexpectedException;
import scala.Option;

import com.orientechnologies.orient.client.remote.OEngineRemote;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.command.OCommandManager;
import com.orientechnologies.orient.core.db.ODatabaseListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.core.entity.OEntityManager;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.sql.OCommandExecutorSQLDelegate;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;


/**
 * The Orient DB plugin
 */
// TODO implement getStatus
public class ODBPlugin extends Plugin {

    private final Map<String, OServer> servers = new HashMap<String,OServer>();
    private boolean isEnabled;
    DBConfiguration orientConf;
    private static ALogger logger = Logger.of(ODBPlugin.class);
    private play.api.Application application = null;
    private static ODBPlugin myInstance;
    
    public ODBPlugin(play.api.Application app) {
        this.application = app;
        
        myInstance = this;
    }
    
    /**
     * Returns an instance of this plugin in the running application
     * @return
     */
    public static ODBPlugin getInstance() {
    	return myInstance;
    	//return Play.application().plugin(ODBPlugin.class);
    }

    @Override
    public boolean enabled() {
        isEnabled = !"disabled".equals(new play.Application(application).configuration().
        		getString(DBConfiguration.ORIENTDB_PLUGIN_ENABLED));
        Logger.warn(String.format("OrientDB plugin is %s", isEnabled ? "enabled" : "disabled"));
        return isEnabled;
    } 
    
    
    @Override
    public synchronized void onStart() {
    	if (!isEnabled) {
            return;
        }
    	info("OrientDB Plugin enabled and starting up..");
    	super.onStart();
    	
    	// TODO is this really necessary?
    	OCommandManager cmdMan = OCommandManager.instance();
        cmdMan.registerExecutor(OSQLSynchPaginatedQuery.class, OCommandExecutorSQLDelegate.class);
        
    	if (servers.isEmpty()) {
        	orientConf = new DBConfiguration();
        	orientConf.readConfig();

            if (orientConf.isServerRemote()) {
                Orient.instance().registerEngine(new OEngineRemote());
            } else {
                runEmbeddedOrientDB();
            }

            registerClasses();
        }
    	info("OrientDB Plugin started.");
    }
    
    /**
     * This method register classes annotated with @Entity.<br/>
     * Also registers all subclasses of ODatabaseListener and ORecordHook
     */
    private void registerClasses() {
    	OObjectDatabaseTx db = new OObjectDatabaseTx(orientConf.url);
        db.open(orientConf.username, orientConf.password);

        info("Registering Entities");
        OEntityManager em = db.getEntityManager();
        OSchema schema = db.getMetadata().getSchema();
        ReflectionHelper rf = new ReflectionHelper(application);
        
        
        /**
         * This reads the comma separated package names and class names
         */
        String[] toLoad = orientConf.packagePrefix.split(",");
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for(String load: toLoad) {
            load = load.trim();
            if(load.endsWith(".*")) {
            	/*
            	 * Add only classes annotated as @Entity
            	 */
            	Iterator<Class<?>> entitySet = rf.getTypesAnnotatedWith(load.substring(0, load.length()-2), 
            			javax.persistence.Entity.class).iterator();
//            	Iterator<Class<?>> entitySet = rf.getTypesAnnotatedWith(load.substring(0, load.length()-2), 
//            			ODBEntity.class).iterator();
    			while(entitySet.hasNext())
    				classes.add(entitySet.next());
            } else {
            	// this is a specific class name. Add it without further checks
                try {
					classes.add(Class.forName(load));
				} catch (ClassNotFoundException e) {
					throw new UnexpectedException(Option.apply(e.getMessage()), Option.apply(e.getCause()));
				}
            }
        }
        
        /*
         * Register all classes in the set for OrientDB for Entity handling
         */
        for(Class<?> entity : classes) {
        	String entityName = entity.getName();
        	info("Enhancing class: %s", entityName);
        	try {
				ODBModelEnhancer.enhanceThisClass(entity);
				info("Registering entity: %s", entityName);
	            em.registerEntityClass(entity);
	            
	            /* Create the same if does not exist yet */
	            if (!schema.existsClass(entity.getSimpleName())) {
	                info("Schema: %s", entityName);
	                schema.createClass(entity);
	                schema.save();
	            }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        // TODO filtered by package name?
        info("Registering Database Listeners");
        Set<Class<? extends ODatabaseListener>> listeners = rf.getAssignableClasses(ODatabaseListener.class);
        for (Class<? extends ODatabaseListener> listener : listeners) {
            info("Listener: %s", listener.getName());
            ODB.listeners.add(listener);
        }
        
        info("Registering Record Hooks");
        Set<Class<? extends ORecordHook>> hooks = rf.getAssignableClasses(ORecordHook.class);
        for (Class<? extends ORecordHook> hook :hooks) {
            info("Hook: %s", hook.getName());
            ODB.hooks.add(hook);
        }

        db.close();
    }
    
    /**
     * Starts the embedded database engine
     */
	private void runEmbeddedOrientDB() {
        try {
        	OServer server = OServerMain.create();
            File dbconf = Play.application().getFile(orientConf.configFile);

            info("Starting OrientDB embbeded");
            server.startup(dbconf);
            server.activate();
            servers.put("default", server);
            info("Embedded OrientDB started");
        } catch (Exception e) {
        	throw Configuration.root().reportError(DBConfiguration.ConfigKey.DB_CONFIG.toString(), 
        			e.getMessage(), e);
        }
    }
    
	// TODO move this to a specific binder class
	// Maybe we do not need this at all
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public Object bind(String name, Class clazz, java.lang.reflect.Type type, Annotation[] annotations,
//            Map<String, String[]> params) {
//    	
//        if (Model.class.isAssignableFrom(clazz)) {
//            String keyName = Model.Manager.factoryFor(clazz).keyName();
//            String idKey = name + "." + keyName;
//            if (params.containsKey(idKey) && params.get(idKey).length > 0 && params.get(idKey)[0] != null
//                    && params.get(idKey)[0].trim().length() > 0) {
//                String id = params.get(idKey)[0];
//                try {
//                    OSQLSynchQuery<Model> query = new OSQLSynchQuery<Model>("from " + clazz.getName() + " o where o."
//                            + keyName + " = ?");
//                    Object param = play.data.binding.Binder.directBind(name, annotations, id + "", Model.Manager
//                            .factoryFor(clazz).keyType());
//                    List<Model> res = ODB.openObjectDB().query(query, param);
//                    Object o = res.get(0);
//                    return Model.edit(o, name, params, annotations);
//                } catch (ORecordNotFoundException e) {
//                    // ok
//                } catch (Exception e) {
//                    throw new UnexpectedException(Option.apply(e.getMessage()), Option.apply(e.getCause()));
//                }
//            }
//            return Model.create(clazz, name, params, annotations);
//        }
//        return super.bind(name, clazz, type, annotations, params);
//    }
//
//    @Override
//    public Object bind(String name, Object o, Map<String, String[]> params) {
//        if (o instanceof Model) {
//            return Model.edit(o, name, params, null);
//        }
//        return null;
//    }
    
    @Override
    public void onStop() {
    	OServer server = servers.remove("default");
    	if (server != null) {
            if (Play.isDev()) {
                clearReferencesToStaleClasses();
            }
            server.shutdown();
            server = null;
        }
    	super.onStop();
    	info("ODB Plugin stopped.");
    }

    // TODO Make sure this is necessary. Otherwise we can delete this
    private void clearReferencesToStaleClasses() {
    	logger.debug("Cleaning stale classes", null);
        try {
            Field classes = OObjectSerializerHelper.class.getDeclaredField("classes");
            classes.setAccessible(true);
            classes.set(null, new HashMap<String, List<Field>>());
        } catch (Exception e) {
            // don't worry
        }
        try {
        	OObjectDatabasePool.global().getPools().clear();
        } catch (Exception e) {
            // don't worry
        }
        try {
            ODatabaseDocumentPool.global().getPools().clear();
        } catch (Exception e) {
            // don't worry
        }
        try {
            OGraphDatabasePool.global().getPools().clear();
        } catch (Exception e) {
            // don't worry
        }
    }
    
    public DBConfiguration getConf() {
    	return orientConf;
    }
    
    /**
     * Log info messages via Play's logger
     * @param msg
     */
    private static void info(String msg, Throwable throwable, Object ... args) {
    	logger.info(String.format(msg, args), throwable);
        
    }
    
    /**
     * Log info messages via Play's logger
     * @param msg
     */
    public static void trace(String msg, Throwable throwable, Object ... args) {
    	logger.trace(String.format(msg, args), throwable);
        
    }
    
    /**
     * Log info messages via Play's logger
     * @param msg
     */
    private static void debug(String msg, Object ... args) {
    	logger.debug(String.format(msg, args), null);
        
    }
    
    /**
     * Log info messages via Play's logger
     * @param msg
     */
    private static void info(String msg, Object ... args) {
    	info(msg, null, args);
    }

    private File writeConfigFile(String cfile) throws FileNotFoundException, IOException {
        File f = new File("db.config");
        if (f.exists())
            return f;

        InputStream in = ODBPlugin.class.getResourceAsStream(cfile);
        OutputStream out = new FileOutputStream(f);
        byte buf[] = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);

        out.close();
        in.close();

        return f;
    }
}
