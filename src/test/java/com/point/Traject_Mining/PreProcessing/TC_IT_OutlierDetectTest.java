package com.point.Traject_Mining.PreProcessing;

import dataset.DataHelper;

public class TC_IT_OutlierDetectTest {
	public static void main(String args[]){
		long starttime = System.currentTimeMillis();
		DataHelper datahelper = new DataHelper();
		String dirPath = "D:/学习资料/城市轨迹数据/Geolife Trajectories 1.3/Geolife Trajectories 1.3/Data";
		datahelper.initTraject(dirPath,"plt");
		TC_IT_Cluster tc_itoutlierdetect = new TC_IT_Cluster(datahelper.getAllPeople());
		tc_itoutlierdetect.cluster();
		long endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);	
	}
}
