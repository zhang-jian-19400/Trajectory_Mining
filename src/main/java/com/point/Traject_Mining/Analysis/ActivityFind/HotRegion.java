package com.point.Traject_Mining.Analysis.ActivityFind;

import java.util.HashMap;
import java.util.Vector;

public class HotRegion {
	private String filename;
	private static HashMap<Integer,Vector> Cluster = new HashMap<Integer,Vector>();
	private double timelength;
	public HotRegion(){
		this.Cluster = new HashMap();
	}

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public HashMap<Integer, Vector> getCluster() {
		return Cluster;
	}

	public void setCluster(HashMap<Integer, Vector> cluster) {
		Cluster = cluster;
	}

	public double getTimelength() {
		return timelength;
	}

	public void setTimelength(double timelength) {
		this.timelength = timelength;
	}
}

