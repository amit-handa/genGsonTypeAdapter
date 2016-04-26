package com.ahanda.gson;

import java.util.ArrayList;

/**
 * Created by amit on 4/20/16.
 */
public class Point extends PPoint {
	private Integer x, y;
	private float[] z;
	private ArrayList<Integer> others;

	public Integer getX() { return x; }
	public void setX( int nx ) { x = nx; }

	public float[] getZ() { return z; }
	public void setZ( float[] nz ) { z = nz; }

	public int getY() { return y; }
	public void setY( int ny ) { y = ny; }

	public ArrayList<Integer> getOthers() { return others; }
	public void setOthers( ArrayList<Integer> nothers ) { others = nothers; }
}