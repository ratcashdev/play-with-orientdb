package conf;

import play.Configuration;

public class DBConfiguration {
	public static final String PREFIX = "orientdb";
	public static final String ORIENTDB_PLUGIN_ENABLED = "orientdbplugin";
	
	public enum ConfigKey {
		/** URL to the OrientDB instance */
	    DB_URL("db.url"), /**/
	    DB_USERNAME("db.username"), /**/
	    DB_PASSWORD("db.password"), /**/
	    DB_GRAPH_URL("db.graph.url"), /**/
	    DB_CONFIG("db.config.file"), /**/
	    MODEL_PACKAGE_PREFIX("db.entities.package"), /**/
	    OPEN_IN_VIEW_DOCDB("db.open-in-view.documentdb"), /**/
	    OPEN_IN_VIEW_OBJECTDB("db.open-in-view.objectdb"), /**/
	    OPEN_IN_VIEW_GRAPHDB("db.open-in-view.graphdb"), /**/
	    LOGGER("logger");
	    
	    private final String key;

	    private ConfigKey(String key) {
	        this.key = key;
	    }

	    public String getKey() {
	        return key;
	    }
	    
	}
	
    public static final int OIV_DOCUMENT_DB = 0x0001;
    public static final int OIV_OBJECT_DB = 0x002;
    public static final int OIV_GRAPH_DB = 0x004; // 8, 16

	public String url;
	public String username;
	public String password;
	public String graphurl;
	public String configFile;
	
	/**
	 * The package prefix of all the classes that should be interesting for OrientDB's mapper
	 * Out of these only those classes are registered, which have the @Entity annotation
	 */
	public String packagePrefix;
	public int openInView;
	
	public void readConfig() {
		Configuration orientConf = Configuration.root().getConfig(PREFIX);
		
    	url = readString(orientConf, ConfigKey.DB_URL, "memory:temp");
    	username = readString(orientConf, ConfigKey.DB_USERNAME, "admin");
    	password = readString(orientConf, ConfigKey.DB_PASSWORD, "admin");
    	graphurl = readString(orientConf, ConfigKey.DB_GRAPH_URL, null);
    	configFile = readString(orientConf, ConfigKey.DB_CONFIG, "/play/modules/orientdb/db.config");
    	
    	packagePrefix = readString(orientConf, ConfigKey.MODEL_PACKAGE_PREFIX, "models.*");
    	
    	boolean view = readBoolean(orientConf, ConfigKey.OPEN_IN_VIEW_DOCDB, true);
    	openInView |= view? OIV_DOCUMENT_DB : 0;
    	
    	view = readBoolean(orientConf, ConfigKey.OPEN_IN_VIEW_OBJECTDB, true);
    	openInView |= view? OIV_OBJECT_DB : 0;
    	
    	view = readBoolean(orientConf, ConfigKey.OPEN_IN_VIEW_GRAPHDB, true);
    	openInView |= view? OIV_GRAPH_DB : 0;
	}
	
	public boolean isServerRemote() {
		return url.startsWith("remote");
	}
	
    private static String readString(Configuration cfg, String key, String defValue) {
    	if(cfg == null)
    		return defValue;
    	
    	String val = cfg.getString(key);
    	return (val == null) ? defValue : val;
    }
    
    private static String readString(Configuration cfg, ConfigKey key, String defValue) {
    	if(cfg == null)
    		return defValue;
    	
    	String val = cfg.getString(key.getKey());
    	return (val == null) ? defValue : val;
    }
    
    private static boolean readBoolean(Configuration cfg, String key, boolean defValue) {
    	if(cfg == null)
    		return defValue;
    	
    	Boolean val = cfg.getBoolean(key);
    	return (val == null) ? defValue : val;
    }
    
    private static boolean readBoolean(Configuration cfg, ConfigKey key, boolean defValue) {
    	if(cfg == null)
    		return defValue;
    	
    	Boolean val = cfg.getBoolean(key.getKey());
    	return (val == null) ? defValue : val;
    }
}
