package controllers;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
  
	public static Result version() {
	  	//return ok(Play.application().configuration().root().toString());
		return ok("OrientDB plugin version is 1.0.-SNAPSHOT");
	}
	
}