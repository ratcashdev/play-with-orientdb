package modules.orientdb;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import play.Play;
import play.api.Application;
import play.api.UnexpectedException;
import scala.Option;

public class ReflectionHelper {
	Application app;
	private ConfigurationBuilder mConfBuilder;
	Collection<Class<?>> allClasses;
	
	public ReflectionHelper(Application application) {
		app = application;
		
		/*
         * Retrieve all classes from the classpath.
         * This is a slow operation. To make it faster, you can use the following line
         */
		try {
			mConfBuilder = new ConfigurationBuilder().addUrls(
					Collections.list(app.classloader().getResources("*")));
		} catch (IOException e) {
			throw new UnexpectedException(Option.apply(e.getMessage()), Option.apply(e.getCause()));
		}
	}
	
	
	public Set<Class<?>> getTypesAnnotatedWith(String pkg_name, Class<? extends Annotation> annotation) {
		ConfigurationBuilder confBuilder = new ConfigurationBuilder().addUrls(
        		ClasspathHelper.forPackage(pkg_name, app.classloader()));
		
		// set the annotation scanner
		confBuilder.setScanners(new TypeAnnotationsScanner());
        return new Reflections(confBuilder).getTypesAnnotatedWith(annotation);
	}
	
	public <T> Set<Class<? extends T>> getAssignableClasses(Class<T> clazz) {
		return new Reflections(mConfBuilder).getSubTypesOf(clazz);

		// the latter one returns a String collection
		//return new Reflections(mConfBuilder).getStore().getSubTypesOf(clazz.getName());
	}
	
	public static List<Class<?>> getAssignableClasses(Class<?> clazz, Collection<URL> allClasses) 
			throws UnexpectedException {
		List<Class<?>> results = new ArrayList<Class<?>>();
		
		if(clazz == null)
			return results;
		
		Class<?> cl;
		for (URL url : allClasses) {
            try {
            	cl = Class.forName(url.toString());
                Play.application().classloader().loadClass(cl.getName());
            } catch (ClassNotFoundException e) {
            	throw new UnexpectedException(Option.apply(e.getMessage()), Option.apply(e.getCause()));
            }
            
            try {
                if (clazz.isAssignableFrom(cl) && !cl.getName().equals(clazz.getName())) {
                    results.add(cl);
                }
            } catch (Exception e) {
            	// ignore
            }
        }
		return results;
	}
	
}
