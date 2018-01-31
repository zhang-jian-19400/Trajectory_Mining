package com.point.Traject_Mining.PreProcessing;

import dataset.DataHelper;
import dataset.Model.GeoPointModel;
import dataset.Model.PeopleModel;
import dataset.Model.TrajectModel;

public class BasicDetectTest {
	public static void main(String args[]){
		BasicDetect bd = new BasicDetect();
		GeoPointModel dp1 = new GeoPointModel(30.2811204101,-97.7452111244);
		GeoPointModel dp2 = new GeoPointModel(30.2691029532,-97.7493953705);
		System.out.println(bd.GetDistance(dp1, dp2));
	}
}
