package com.point.Traject_Mining.Analysis.ActivityFind;

import java.util.Vector;

import com.point.Traject_Mining.PreProcessing.TC_IT_ClusterHelper;

import dataset.Model.GeoPointModel;

public interface FindHotRegion {
	TC_IT_ClusterHelper helper = new TC_IT_ClusterHelper();
	public HotRegion find(Vector<GeoPointModel> trajectories);
}
