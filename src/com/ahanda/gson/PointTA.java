package com.ahanda.gson;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

/**
 * Created by amit on 4/20/16.
 */
public class PointTA extends TypeAdapter<Point> {
	private Gson gson = new Gson();

	public Point read( JsonReader js ) throws IOException {
		if( js.peek() == JsonToken.NULL ) {
			js.nextNull();
			return null;
		}

		Point pt = new Point();
		js.beginObject();
		js.nextName();
		pt.setX( js.nextInt() );
		js.nextName();
		pt.setY( js.nextInt() );
		js.nextName();
		pt.setOthers( gson.fromJson( js, new TypeToken<List<Integer>>() {}.getType() ) );
		js.endObject();

		return pt;
	}

	public void write( JsonWriter js, Point pt ) throws IOException {
		if( pt == null ) {
			js.nullValue();
			return ;
		}

		js.beginObject();
		js.name( "x" );
		js.value( pt.getX() );
		js.name( "y" );
		js.value( pt.getY() );
		js.name( "others" );
		gson.toJson( gson.toJsonTree( pt.getOthers() ), js );
		js.endObject();
	}
}

