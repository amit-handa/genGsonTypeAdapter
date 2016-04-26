package com.ahanda.gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class GenTypeAdapter {
	private static Logger l = LoggerFactory.getLogger( GenTypeAdapter.class );

	public static void classSpy( String cname ) {
		StringBuilder sb = new StringBuilder();
		HashMap fields = new HashMap< String, Class<?> >();
		sb.append( "class {cname}TA extends TypeAdapter<{cname}> {" +
				"public {cname} read( JsonReader js ) throws IOException {" +
				"if( js.peek() === JsonToken.NULL ) {" +
				"js.nextNull();" +
				"return null;" +
				"}" );

		try {
			Class<?> c = Class.forName( cname );
			for( Field f : c.getFields() ) {
				Class<?> fc = f.getType();
				l.info( "field {} {}", f.getName(), fc );
			}
			for( Method m : c.getMethods() ) {
				String mname = m.getName();
				l.info( "method {}", mname );
				String fname = null;
				if( mname.startsWith( "set" ) || mname.startsWith( "get") )
					fname = Character.toLowerCase( mname.charAt(3) ) + mname.substring( 4 );

				if( fname == null )
					continue;

				Class<?> ret = m.getReturnType();
				l.info( "f {} {}", fname, ret.isPrimitive() );
				fields.put( fname, ret );
			}

			l.info( "all fields {}", fields );
		} catch( ClassNotFoundException e ) {
			l.error( "classnotfound {}", e.getStackTrace() );
		}
	}

	public static void main( String[] args ) {
		GsonBuilder cgson = new GsonBuilder();
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

		classSpy("com.ahanda.gson.Point");

		Point pt = gson.fromJson(s, Point.class);
		l.info("fromJson {}", pt);
	}
}