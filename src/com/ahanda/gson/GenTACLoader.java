package com.ahanda.gson;

import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeParameter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Created by amit on 4/19/16.
 */
public class GenTACLoader extends URLClassLoader {
	private static Logger l = LoggerFactory.getLogger( GenTACLoader.class );

	public GenTACLoader() {
		super( new URL[]{} );
	}

	public Map<String, List<Class<?>>> loadAndScanJar(File jarFile)
			throws ClassNotFoundException, IOException {

		// Load the jar file into the JVM
		// You can remove this if the jar file already loaded.
		super.addURL(jarFile.toURI().toURL());

		Map<String, List<Class<?>>> classes = new HashMap<>();

		List<Class<?>> interfaces = new ArrayList<>();
		List<Class<?>> clazzes = new ArrayList<>();
		List<Class<?>> enums = new ArrayList<>();
		List<Class<?>> annotations = new ArrayList<>();

		classes.put("interfaces", interfaces);
		classes.put("classes", clazzes);
		classes.put("annotations", annotations);
		classes.put("enums", enums);

		// Count the classes loaded
		int count = 0;

		// Your jar file
		JarFile jar = new JarFile(jarFile);
		// Getting the files into the jar
		Enumeration<? extends JarEntry> enumeration = jar.entries();

		// Iterates into the files in the jar file
		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			// Is this a class?
			if (zipEntry.getName().endsWith(".class")) {

				// Relative path of file into the jar.
				String className = zipEntry.getName();

				// Complete class name
				className = className.replace(".class", "").replace("/", ".");
				// Load class definition from JVM
				Class<?> clazz = this.loadClass(className);
				classSpy( clazz );

				try {
					// Verify the type of the "class"
					if (clazz.isInterface()) {
						interfaces.add(clazz);
					} else if (clazz.isAnnotation()) {
						annotations.add(clazz);
					} else if (clazz.isEnum()) {
						enums.add(clazz);
					} else {
						clazzes.add(clazz);
					}

					count++;
				} catch (ClassCastException e) {
					System.out.println( "Couldnt find ....");
				}
			}
		}

		System.out.println("Total: " + count);

		return classes;
	}

	public static void classSpy( Class<?> c ) {
		StringBuilder sb = new StringBuilder();
		HashMap fields = new HashMap< String, Class<?> >();
		sb.append( "class {cname}TA extends TypeAdapter<{cname}> {" +
				"public {cname} read( JsonReader js ) throws IOException {" +
				"if( js.peek() === JsonToken.NULL ) {" +
				"js.nextNull();" +
				"return null;" +
				"}" );

		for( Field f : c.getFields() ) {
			Type fc = f.getGenericType();
			if( fc instanceof ParameterizedType ) {
				Type[] typeargs = ((ParameterizedType) fc).getActualTypeArguments();
				l.info( "HHHHHHHH ", f.getName(), typeargs );
			} else l.info( "field {} {} {}", f.getName(), fc );
		}

		for( Method m : c.getMethods() ) {
			String mname = m.getName();
			l.info( "method {}", mname );
			String fname = null;
			if( mname.startsWith( "get") )
				fname = Character.toLowerCase( mname.charAt(3) ) + mname.substring( 4 );

			if( fname == null )
				continue;

			Type rtype = m.getGenericReturnType();
			Class<?> rclass = m.getReturnType();
			if( rtype instanceof ParameterizedType ) {
				Type[] typeargs = ((ParameterizedType) rtype).getActualTypeArguments();
				l.info( "MMMM {} {}", rtype.getTypeName(), typeargs );
			}
			l.info( "rtype {} {} {} {} {} {}", fname, rtype, rclass, rclass.isPrimitive(), rclass.isArray(), rclass.isEnum() );
			fields.put( fname, rtype );
		}

		l.info( "all fields {}", fields );
	}


	public static void main( String[] args ) {
		/*GsonBuilder cgson = new GsonBuilder();
		cgson.registerTypeAdapter(Point.class, new PointTA());
		Gson gson = cgson.create();

		Point pt1 = new Point();
		pt1.setX(1);
		pt1.setY(2);
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add( 3 );
		a.add( 4 );
		a.add( 5 );
		a.add( 6 );
		pt1.setOthers( a );
		String s = gson.toJson(pt1);
		l.info("toJson {} {}", s, "1,2");

		Point pt = gson.fromJson(s, Point.class);
		l.info("fromJson {}", pt);*/

		GenTACLoader cloader = new GenTACLoader();
		try {
			Class<?> PointClass = cloader.loadClass("com.ahanda.gson.Point");
			GenTACLoader.classSpy(PointClass);
		} catch( ClassNotFoundException e ) {
			l.error( "classnotfound {}", e.getStackTrace() );
		}
	}
}
