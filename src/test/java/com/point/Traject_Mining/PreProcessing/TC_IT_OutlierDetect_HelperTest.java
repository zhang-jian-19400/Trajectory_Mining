package com.point.Traject_Mining.PreProcessing;

import dataset.Model.GeoPointModel;

public class TC_IT_OutlierDetect_HelperTest {
	public static void main(String arg[]){
		TC_IT_ClusterHelper help = new TC_IT_ClusterHelper();
		GeoPointModel  point1 = new GeoPointModel();
		point1.setLatitude(34.326);
		point1.setLongitude(127.325);
		GeoPointModel  point2 = new GeoPointModel();
		point1.setLatitude(33.342);
		point1.setLongitude(137.654);
	System.out.println(help.geoDistance(point1, point2));
		
		
	}
}
