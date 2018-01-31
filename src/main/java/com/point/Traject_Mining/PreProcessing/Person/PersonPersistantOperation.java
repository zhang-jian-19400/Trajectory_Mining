package com.point.Traject_Mining.PreProcessing.Person;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.point.Traject_Mining.PreProcessing.PersistantOperation;

import dataset.Model.GeoPointModel;
import dataset.Model.PeopleModel;
import dataset.Model.SegmentModel;
import dataset.Model.TrajectModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PersonPersistantOperation implements PersistantOperation{
	/*
	 * 把用户的轨迹按照原来的单个轨迹文件的形式存放到外存中
	 */
	public void oneTrajectToJson(TrajectModel trajectdata,String peopleName,String Trajectname){
		String Rootdir = System.getProperty("user.dir");
		File peoplefile = new File(Rootdir+"\\"+peopleName);
		File trajectfile = new File(Rootdir+"\\"+peopleName+"\\trajects");
		File traject = new File(Rootdir+"\\"+peopleName+"\\trajects\\"+Trajectname+".json");
		if(!peoplefile.exists())  peoplefile.mkdirs();
		if(!trajectfile.exists()) trajectfile.mkdirs();
		if(!traject.exists())
			try {
				traject.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		JSONArray jsonarray = new JSONArray();	
		JSONArray array = new JSONArray();
//		for(SegmentModel segment:trajectdata.getSegments()){
			//下面两行是在轨迹分段聚类中需要的代码 
//			if(segment.isError()==true)
//			for(GeoPointModel geo:segment.toTrajectPoints(trajectdata.getPoints()))
			for(GeoPointModel geo:trajectdata.getPoints()) //为了访问到所有的点,将所有的点加入进来了 ，此处是用于未分段的情况
			{
				List<Double> list = new ArrayList<Double>();
				list.add(geo.getLongitude());
				list.add(geo.getLatitude());
				HashMap<String,List<Double>> map = new HashMap<String,List<Double>>();
				map.put("coord",list);
				JSONObject json = JSONObject.fromObject(map);
				array.add(json);
			}
//		}
			jsonarray.add(array);		
			
		try {
			FileWriter writer= new FileWriter(traject);
			writer.write(jsonarray.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  	
	}
	/*
	 * @传入一个PeopleModel对象，将其所有的轨迹写到一个轨迹文件中。
	 */
	public Vector <GeoPointModel> onePeopleTrajectToJson(PeopleModel people){
		Vector <GeoPointModel> Allpoint= new Vector <GeoPointModel>();
		String Rootdir = System.getProperty("user.dir");
		File peoplefile = new File(Rootdir+"\\"+people.getName());
		File trajectfile = new File(Rootdir+"\\"+people.getName()+"\\trajects"+2+".json");
		if(!peoplefile.exists())  peoplefile.mkdirs();
		if(!trajectfile.exists())
		try {
				trajectfile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}	
		JSONArray jsonarray = new JSONArray();
		for(String name:people.getTrajects().keySet())
		{
			JSONArray array = new JSONArray();
			for(SegmentModel segment:people.getTrajects().get(name).getSegments())
					{   
						Vector<GeoPointModel> points = people.getTrajects().get(name).getPoints();
//						for(GeoPointModel geo:points) //为了访问到所有的点,将所有的点加入进来了 ，用于未分段的情况
						if(segment.isError()==true)
						{	
						for(GeoPointModel geo:segment.toPoints(points))
						{
						Allpoint.add(geo);//添加轨迹点到最后结果后
						List<Double> list = new ArrayList<Double>();
						list.add(geo.getLongitude());
						list.add(geo.getLatitude());
						HashMap<String,List<Double>> map = new HashMap<String,List<Double>>();
						map.put("coord",list);
						JSONObject json = JSONObject.fromObject(map);
						array.add(json);
						}
					}
			} 
			if(array.size()!=0)
			jsonarray.add(array);
		}
		try {
			FileWriter writer= new FileWriter(trajectfile);
			writer.write(jsonarray.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return Allpoint;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see com.point.Traject_Mining.PreProcessing.PersistantOperation#Persistant(java.util.Vector)
	 * 单纯只是把数据以[{'coord':[xx.xx,yy.yy]},,,]
	 */
	public void Persistant(Vector<PeopleModel> allpeople) {
		// TODO Auto-generated method stub
		int number=0;
		for(PeopleModel people:allpeople)
		{
			for(String key:people.getTrajects().keySet())
			{
				TrajectModel traject = people.getTrajects().get(key);
				oneTrajectToJson(traject,people.getName()+"new",""+number++);
			}
			Vector<GeoPointModel> points = onePeopleTrajectToJson(people);  //写入一个人的文件中
		}
	}
}
