package modules.orientdb.actions;

import modules.orientdb.Model;
import modules.orientdb.ODB;
import modules.orientdb.ODB.DBTYPE;
import modules.orientdb.annotation.Transactional;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class ODBTransactionWrapper extends Action<Transactional>
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
    	DBTYPE type = this.getClass().getAnnotation(Transactional.class).db();
    	if(type == DBTYPE.DOCUMENT || type == DBTYPE.OBJECT)
    		Model.objectDB().begin();

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
