package com.point.Traject_Mining.PreProcessing.Wind;

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

public class WindPersistantOperation implements PersistantOperation{
	/*@param input ：Vector<PeopleModel> allpeople
	 * @ output： 一个文件，包含有类别也轨迹断点额信息 [{"color0":[[{},{},{}...],[{},{},{}...]]},{"color1":[..].}..]
	 * 根据聚类轨迹的类别将按照原始轨迹数据排布的轨迹，按照聚成的类别将数据输入
	 */
	public void showAllPointsByCluster(Vector<PeopleModel> allpeople){
		Vector <GeoPointModel> Allpoint= new Vector <GeoPointModel>();
		String Rootdir = System.getProperty("user.dir");
		File trajectfile = new File(Rootdir+"\\trajectsall.json");
		if(!trajectfile.exists())
			try {
				trajectfile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}	
		JSONArray allarray = new JSONArray();
		for(int i=0;i<=6;i++){
			JSONArray jsonarray = new JSONArray();
			for(PeopleModel people:allpeople)
				{	
					for(String name:people.getTrajects().keySet())
					{  
						JSONArray array = new JSONArray();
						for(SegmentModel segment:people.getTrajects().get(name).getSegments())
							if(segment.getClusterId()==i) //如果属于当前类
								{  
										Vector<GeoPointModel> points = people.getTrajects().get(name).getPoints();
			//							for(GeoPointModel geo:points) //为了访问到所有的点,将所有的点加入进来了
										if(segment.isError()==true)
										{	int id =segment.getClusterId();
										for(GeoPointModel geo:segment.toPoints(points))
										{
										Allpoint.add(geo);//添加轨迹点到最后结果后
										List<Double> list = new ArrayList<Double>();
										list.add(geo.getLongitude());
										list.add(geo.getLatitude());
										HashMap<String,List<Double>> coordination = new HashMap<String,List<Double>>();
										coordination.put("coord",list);
										JSONObject json = JSONObject.fromObject(coordination);
										array.add(json);
										}
									}
								} 
						if(array.size()!=0)
						jsonarray.add(array);
					}
				}
			HashMap<String,JSONArray> map = new HashMap<String,JSONArray>();
			map.put("color"+i,jsonarray);
			JSONObject jsoncluster = JSONObject.fromObject(map);
			allarray.add(jsoncluster);
		
		}
			try {
				FileWriter writer= new FileWriter(trajectfile);
				writer.write(allarray.toString());
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		
	}
	public void pointToJson(Vector<GeoPointModel> points){
		String Rootdir = System.getProperty("user.dir");
		File trajectfile = new File(Rootdir+"\\onetraject.json");
		if(!trajectfile.exists())
			try {
				trajectfile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		JSONArray array = new JSONArray();
		for(GeoPointModel geo:points)
		{
		List<Double> list = new ArrayList<Double>();
		list.add(geo.getLongitude());
		list.add(geo.getLatitude());
		HashMap<String,List<Double>> coordination = new HashMap<String,List<Double>>();
		coordination.put("coord",list);
		JSONObject json = JSONObject.fromObject(coordination);
		array.add(json);
		}
		try {
			FileWriter writer= new FileWriter(trajectfile);
			writer.write(array.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	@Override
	public void Persistant(Vector<PeopleModel> allpeople) {
		showAllPointsByCluster(allpeople);
	}
}
