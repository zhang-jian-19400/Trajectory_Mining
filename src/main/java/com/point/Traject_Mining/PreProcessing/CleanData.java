package com.point.Traject_Mining.PreProcessing;

import dataset.DataHelper;
import dataset.Model.PeopleModel;
import dataset.Model.TrajectModel;

/*
 * 清洗数据的类，可以封装多种清洗数据的方法
 */
public class CleanData {
	BasicDetect basicdetect = new BasicDetect();
	
	public void clean(DataHelper datahelper){
		for(PeopleModel people:datahelper.getAllPeople())
	{
		for(String key:people.getTrajects().keySet())
		{
			TrajectModel traject = people.getTrajects().get(key);			
			basicdetect.TrajectoryProcess(traject.getPoints());
		}
	}
	}
	
}
