package com.point.Traject_Mining.Analysis.ActivityFind.FindHotArea;

import java.util.Vector;

import com.point.Traject_Mining.Analysis.ActivityFind.FindHotRegion;
import com.point.Traject_Mining.Analysis.ActivityFind.HotRegion;

import dataset.Model.GeoPointModel;

/*
 * 这个算法是参考的袁冠博士论文《移动对象轨迹数据挖掘方法研究》中的热点区域发现算法，DB_HR
 * 这个算法的弊端之处是当活动对象以较小的速度行走时，不会过滤，另一方面如果聚集的速度过大，会被过滤。
 */
public class FindHotRegionByDBHR implements FindHotRegion{	
	/*
	 * @Description: This function find region in a plt file.
	 * @param name refers to the file's name; trajectores is the content of plt file.
	 * minTime: the disdinguish boundary of time interval.as same as minSpeed.yibuxingnong is the param to adjust speed,>=1;
	 * @return: return the two hotregions;
	 */
	public HotRegion findHotRegion(Vector<GeoPointModel> trajectories,double minTime,double minSpeed,double epsinon){
		Vector region1=new Vector <GeoPointModel>();
		Vector region2 = new Vector <GeoPointModel>();
		boolean clusterOpen1 = false;
		boolean clusterOpen2 = false;
		int ClusterId=0;
		
		Vector cluster = new Vector();
		HotRegion hotregion = new HotRegion();
		for(int i=0;i<trajectories.size()-1;i++){
			//求出该点的停留时间，与速度
			double time = (trajectories.get(i+1).getTimedistance()-trajectories.get(i).getTimedistance())*3600*24;
			double speed = helper.geoDistance(trajectories.get(i+1),trajectories.get(i))/time;
			//目前主要的做法是通过速度与时间来划分两种热点区域省略了一些步骤
			if(time>minTime){
					if(clusterOpen2 == true)
					{
						clusterOpen2 = false;
						if(cluster.size()>20){
						hotregion.getCluster().put(ClusterId, (Vector)cluster.clone());
						ClusterId++;}
						cluster = new Vector();
					}
					if((speed<minSpeed*epsinon)) //belongs to region1室内,封闭场所
					{	//cluster.addElement(trajectories.get(i));	
						cluster.addElement(trajectories.get(i));
						if(clusterOpen1 == false)
						clusterOpen1 = true;
					}
					/*else if(clusterOpen1==true)
					{
						clusterOpen1 = false;
						cluster.addElement(trajectories.get(i+1));
						hotregion.getCluster().put(ClusterId,(Vector)cluster.clone());
						ClusterId++;
						cluster = new Vector(); 
					}*/
				}	
			else {
					if(clusterOpen1==true) {
					clusterOpen1 = false;
					cluster.addElement(trajectories.get(i));
					hotregion.getCluster().put(ClusterId,(Vector)cluster.clone());
					ClusterId++;
					cluster = new Vector();
					}  
					if((speed<minSpeed)) //belongs to region2 室外
					{
						cluster.addElement(trajectories.get(i));
						if(clusterOpen2 == false)
							clusterOpen2 = true;
					}
					else if(clusterOpen2 == true)
					{
						clusterOpen2 = false;
						if(cluster.size()>20){
						cluster.addElement(trajectories.get(i));
						hotregion.getCluster().put(ClusterId, (Vector)cluster.clone());
						ClusterId++;
						}
						cluster = new Vector();	
					}
				}
		}
		return hotregion;
	}

	@Override
	public HotRegion find(Vector<GeoPointModel> trajectories) {
		// TODO Auto-generated method stub
		return findHotRegion(trajectories,7,2,1.5);
	}
}
