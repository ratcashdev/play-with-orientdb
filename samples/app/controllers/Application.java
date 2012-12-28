package controllers;

import java.util.ArrayList;

import models.DescribedAddress;
import models.DescribedString;
import models.MyClient;
import models.PostalAddress;
import modules.orientdb.Model;
import modules.orientdb.annotation.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.orientechnologies.orient.core.id.OClusterPositionLong;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;


public class Application extends Controller {
  
	public static Result index() {
		return ok(index.render("This is the OrientDB Plugin sample application.\nCheck the routes file to see possible URLs."));
	}
 
	/**
	 * 
	 * @param count
	 * @return
	 */
	public static Result createClients(Long count) {
		final int batch = 1000; //10000;
		long timeStarted = System.currentTimeMillis();
		int rounds = 0;
		int remaining;
		
		do {
			int newStart = rounds * batch;
			remaining = (newStart+batch < count) ? batch : (int) (count - newStart);
			doBatchInsert(newStart, remaining );
			rounds++;
		} while (remaining == batch);
		
		long delta = System.currentTimeMillis() - timeStarted;
		return ok(String.format("created %s clients! in %d seconds" , count, delta/1000));
	}
	
	@Transactional()
	public static void doBatchInsert(int start, int count) {
		PostalAddress pa;
		Model.db().declareIntent( new OIntentMassiveInsert() );
		//Model.db().begin();
		for(int i=start; i<(start+count); i++) {
			MyClient cl = Model.newInstance(MyClient.class);
			cl.firstName = "Name-" + i;
			cl.lastName = "LastName-" +i;
			
			cl.phoneNumbers = new ArrayList<DescribedString>();
			DescribedString ds = new DescribedString("+42190712376", "work");
			cl.phoneNumbers.add(ds);
			cl.phoneNumbers.add(new DescribedString("+1231293872", "home"));
			
			//pa = Model.newInstance(PostalAddress.class);
			cl.postalAddresses = new ArrayList<DescribedAddress> ();
			pa = new PostalAddress("Neexistujuca ulica 6"+i, null, "Bratislava", "811 03", "Slovakia");
			cl.postalAddresses.add(new DescribedAddress(pa, "home"));
			pa = new PostalAddress("Existujuca ulica 2"+i, null, "Kosice", "913 23", "Slovakia");
			cl.postalAddresses.add(new DescribedAddress(pa, "work"));

			// calling this makes sure OrientDB know about all of our changes
			// if we used setters, this would not be necessary
			MyClient.db().attach(cl);
			cl.save();
		}
		Model.db().declareIntent( null );
	}
	
	/**
	 * Returns a client based on the second part of the RID
	 * @param id
	 * @return
	 */
	public static Result showClient(Long id) {
		long timeStarted = System.currentTimeMillis();
		int clusterId = Model.db().getClusterIdByName("MyClient");
		ORecordId orid = new ORecordId();
		orid.clusterId = clusterId;
		orid.clusterPosition = new OClusterPositionLong(id);
		MyClient client = Model.db().load(orid);
		
		if(client == null)
			return ok("No clients found with this ID");
		long delta = System.currentTimeMillis() - timeStarted;
		String result = String.format("The number of clients matching this iD was: <br/>, " +
				"the first has a name of: %s. Search took %d milliseconds", client.firstName, delta);
		
		return ok(result);
	}
}