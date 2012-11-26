package modules.orientdb.actions;

import modules.orientdb.ODB;
import modules.orientdb.ODBPlugin;
import modules.orientdb.annotation.ODBWrapper;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class DBActionWrapper extends Action<ODBWrapper>
{
    @Override
    public Result call(Http.Context context) throws Throwable
    {
    	Result res = ok("Did nothing");
    	//internalServerError();
    	try {
	    	beforeInvocation();
	        res = delegate.call(context);
	        onInvocationSuccess();
    	} catch (Exception e) {
    		System.out.println("exception happened.");
    		e.printStackTrace();
    		onInvocationException(e);
    	} finally {
    		System.out.println("cleanup");
    		invocationFinally();
    	}
    	return res;
    }
    
    public void beforeInvocation() {
    	//int dbview = ODBPlugin.getInstance().getConf().openInView;
    	// TODO find out what this was supposed to do.
//        if (dbview != 0) {
//            Injector.inject(new DatabaseSource(dbview));
//        }
    }
    
    public void invocationFinally() {
        ODB.close();
    }
    
    public void onInvocationException(Throwable e) {
        ODB.rollback();
    }

    public void onInvocationSuccess() {
        ODB.commit();
    }

}
