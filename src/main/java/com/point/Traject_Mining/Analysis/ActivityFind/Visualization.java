package com.point.Traject_Mining.Analysis.ActivityFind;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import dataset.Model.GeoPointModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Visualization {
/*
 * 用于将处理的结果随时转为json数据。
 */
	public void getJsonByHotRegion(HotRegion regiones,String path) throws IOException{
		/*
		 * 将HotRegion中的点读出到region中去
		 */
		File file = new File(path);
		if(!file.exists()) file.createNewFile();
		JSONArray jsonarray = new JSONArray();
		for(int i:regiones.getCluster().keySet()){
			Vector<GeoPointModel>points=regiones.getCluster().get(i);
			JSONArray array = new JSONArray();
			for(GeoPointModel geo:points){
			List<Double> list = new ArrayList<Double>();
			list.add(geo.getLongitude());
			list.add(geo.getLatitude());
			HashMap<String,List<Double>> map = new HashMap<String,List<Double>>();
			map.put("coord",list);
			JSONObject json = JSONObject.fromObject(map);
			array.add(json);
			}
			if(array.size()!=0) jsonarray.add(array);
		}
		try {
			FileWriter writer= new FileWriter(path);
			writer.write(jsonarray.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
