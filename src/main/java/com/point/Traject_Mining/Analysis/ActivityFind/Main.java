package com.point.Traject_Mining.Analysis.ActivityFind;

import java.io.IOException;
import java.util.Vector;

import com.point.Traject_Mining.Analysis.ActivityFind.FindHotArea.FindHotRegionByDBHR;
import com.point.Traject_Mining.PreProcessing.CleanData;
import com.point.Traject_Mining.PreProcessing.Person.LoaderPersonData;
import com.point.Traject_Mining.PreProcessing.Person.PersonPersistantOperation;
import com.point.Traject_Mining.PreProcessing.Wind.LoaderWindData;

import dataset.DataHelper;
import dataset.Model.PeopleModel;
import dataset.Model.TrajectModel;

public class Main {
	public static void main(String args[]) throws IOException{
		Visualization obj = new Visualization();
		String Rootdir = System.getProperty("user.dir");
		PersonPersistantOperation persistant = new PersonPersistantOperation();
		FindHotRegionByDBHR findhotregion = new FindHotRegionByDBHR();
		Vector<HotRegion> hotregions =new Vector<HotRegion>();
		LoaderPersonData loaderpersondata = new LoaderPersonData();
		loaderpersondata.load();
		//用基本的轨迹处理方式先处理
		DataHelper datahelper = loaderpersondata.datahelper;
		
		CleanData cleandata=  new CleanData();
		cleandata.clean(datahelper);
		TrajectModel trajectory = new TrajectModel();
		for(PeopleModel people:datahelper.getAllPeople())
		{				
			hotregions = new Vector<HotRegion>();
			for(String name:people.getTrajects().keySet())
				{
				trajectory = people.getTrajects().get(name);
				
				if(trajectory.getPoints().size()>=600){
				persistant.oneTrajectToJson(trajectory, people.getName(),"1");
				//找出来了热点区域
				HotRegion region = findhotregion.find(trajectory.getPoints());
				obj.getJsonByHotRegion(region,Rootdir+"\\target\\result.txt");
				hotregions.add(region);
				}
				}
			//对每个热点区域处理		
		}
		
	}
	
}
