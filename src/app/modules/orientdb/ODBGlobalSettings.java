package modules.orientdb;

import java.lang.reflect.Method;

import modules.orientdb.annotation.Transactional;
import play.GlobalSettings;
import play.mvc.Action;
import play.mvc.Http.Request;

/**
 * Do not use this. Is only temporary
 * @author matyas.bene
 *
 */
public class ODBGlobalSettings extends GlobalSettings {
	
	@Override
	public Action<?> onRequest(Request request, Method actionMethod) {
		if (actionMethod.isAnnotationPresent(Transactional.class)) {
	        Transactional annotation = actionMethod.getAnnotation(Transactional.class);
	        ODB.begin(annotation.type(), annotation.db());
	    }
		return super.onRequest(request, actionMethod);
	}
}
