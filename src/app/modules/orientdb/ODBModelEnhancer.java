package modules.orientdb;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;


public class ODBModelEnhancer  {

    public static void enhanceThisClass(Class<?> clazz) throws Exception {
        CtClass ctClass;
        
        ClassPool classPool = ClassPool.getDefault();
//        CtClass cc = pool.get("test.Rectangle");
//        cc.setSuperclass(pool.get("test.Point"));
//        cc.writeFile();
        
        if(clazz.isInstance(Model.class)) {
        	String entityName = clazz.getName();
        	ctClass = classPool.get(entityName);
        	
        	ODBPlugin.trace("Enhancing ODB entity %s", null, entityName);

            // All
            CtMethod all = CtMethod
                    .make(String
                            .format("public static com.orientechnologies.orient.core.iterator.object.OObjectIteratorClassInterface<T> all() { return db().browseClass(play.Play.classloader.getClassIgnoreCase(\"%s\")); }",
                                    entityName), ctClass);
            ctClass.addMethod(all);

            // Count
            CtMethod count = CtMethod
                    .make(String
                            .format("public static long count() { return db().countClass(play.Play.classloader.getClassIgnoreCase(\"%s\")); }",
                                    entityName), ctClass);
            ctClass.addMethod(count);

            // Done.
            clazz = ctClass.toClass();
            ctClass.defrost();
        	
        }
    }

}
