package com.point.Traject_Mining.PreProcessing;

import java.util.Vector;

import com.point.Traject_Mining.PreProcessing.Wind.LoaderWindData;
import com.point.Traject_Mining.PreProcessing.Wind.WindPersistantOperation;

import dataset.DataHelper;
import dataset.DatasetOperation;
import dataset.Model.GeoPointModel;
import dataset.Model.Model;
import dataset.Model.PeopleModel;
import dataset.Model.TrajectModel;

public class Main {
	public static void main(String args[]){
		int number=0;
		long starttime = System.currentTimeMillis();
		
		LoaderWindData loaderwinddata = new LoaderWindData();
		loaderwinddata.load();
		//用基本的轨迹处理方式先处理

		DataHelper datahelper = loaderwinddata.datahelper;
		
		CleanData cleandata=  new CleanData();
		cleandata.clean(datahelper);
		
		//用TC_IT轨迹聚类方式再处理
		TC_IT_Cluster tc_itcluster = new TC_IT_Cluster(datahelper.getAllPeople());
		tc_itcluster.cluster();
		
		WindPersistantOperation persistant = new WindPersistantOperation();
		persistant.Persistant(datahelper.getAllPeople());

		long endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);
	}
}
