package com.point.Traject_Mining.PreProcessing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dataset.Model.GeoPointModel;


public class BasicDetect implements OutlierDetect{

	public List<GeoPointModel> TrajectoryProcess(List<GeoPointModel> gpsPoints){
		// 计算轨迹点之间的时间间隙
		GetTimeintervalAndDistOfPoints0(gpsPoints);
		// 轨迹去重处理
		RemoveRepeatPoint(gpsPoints);
		// 根据时隙进行轨迹分段
//		DepartTrajectoryToSegment(gpsPoints);
		// 轨迹异常点初步处理
		ChangeOutlierPoint(gpsPoints);
		// 轨迹中值滤波处理
		MidValueFilter(gpsPoints);
		// 计算轨迹点之间的时间间隙
	    GetTimeintervalAndDistOfPoints(gpsPoints);
		// 计算轨迹点瞬时速度
		GetSpeedOfGPSPoint(gpsPoints);	
		return gpsPoints;
	}
	
	public void GetTimeintervalAndDistOfPoints0(List<GeoPointModel> gpsPoints){
		// 计算轨迹点之间的时间间隔
		try{
			float timeInterval = 0;
			gpsPoints.get(0).setTimeInterval(timeInterval);
			for(int i=1; i<gpsPoints.size(); i++){
				timeInterval = (float)(gpsPoints.get(i).getTimedistance()-gpsPoints.get(i-1).getTimedistance()*24*3600);
				
				gpsPoints.get(i).setTimeInterval(timeInterval);
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("Successfully! Get time interval");
		
		// 计算轨迹点之间的距离间隔 	
		double dist = 0;
		gpsPoints.get(0).setDist(dist);
		for(int i=1; i<gpsPoints.size(); i++){
			dist = GetDistance(gpsPoints.get(i), gpsPoints.get(i-1));
			gpsPoints.get(i).setDist(dist);
		}
		
		System.out.println("Successfully! Get distance");
	}
	
	public void GetTimeintervalAndDistOfPoints(List<GeoPointModel> gpsPoints){
		// 计算轨迹点之间的时间间隔
		try{
			float timeInterval = 0;
			gpsPoints.get(0).setTimeInterval(timeInterval);
			for(int i=1; i<gpsPoints.size(); i++){
				if(gpsPoints.get(i).getSegment() == gpsPoints.get(i-1).getSegment()){
					
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					timeInterval = (float)(gpsPoints.get(i).getTimedistance()-gpsPoints.get(i-1).getTimedistance()*24*3600);
					
					gpsPoints.get(i).setTimeInterval(timeInterval);
				}
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("Successfully! Get time interval");
		
		// 计算轨迹点之间的距离间隔 	
		double dist = 0;
		gpsPoints.get(0).setDist(dist);
		for(int i=1; i<gpsPoints.size(); i++){
			if(gpsPoints.get(i).getSegment() == gpsPoints.get(i-1).getSegment()){
				dist = GetDistance(gpsPoints.get(i), gpsPoints.get(i-1));
				gpsPoints.get(i).setDist(dist);
			}
		}
		
		System.out.println("Successfully! Get distance After filter");
	}
	
	public void GetSpeedOfGPSPoint(List<GeoPointModel> gpsPoints){
		for(int i=0; i<gpsPoints.size(); i++){
			if(gpsPoints.get(i).getTimeInterval() == 0){
				gpsPoints.get(i).setSpeed(0);
			}else{
				double speed = gpsPoints.get(i).getDist() / gpsPoints.get(i).getTimeInterval();
				gpsPoints.get(i).setSpeed(speed);
			}
		}
	}
	
	public void MidValueFilter(List<GeoPointModel> gpsPoints){
		// 中值滤波
		for(int i=1; i<gpsPoints.size()-1; i++){
			double dist = GetDistance(gpsPoints.get(i-1), gpsPoints.get(i+1));
			double lat0 = 0.5*(gpsPoints.get(i-1).getLatitude() + gpsPoints.get(i+1).getLatitude());
			double lng0 = 0.5*(gpsPoints.get(i-1).getLongitude()+gpsPoints.get(i+1).getLongitude());
			double dist0 = GetDistance(gpsPoints.get(i), lat0, lng0);
			if(dist0 > 0.5*dist && gpsPoints.get(i).getSegment()==gpsPoints.get(i+1).getSegment()
					&& gpsPoints.get(i).getSegment()==gpsPoints.get(i-1).getSegment()){
				gpsPoints.get(i).setLatitude(lat0);
				gpsPoints.get(i).setLongitude(lng0);			
				gpsPoints.get(i).setDist(dist*0.5);
			}
		}
	}
	
	public double GetDistance(GeoPointModel dp1, double lat, double lng) {
		double PI = 3.1415926;
		double Earth_Radius = 6378137; // m
	    double radLat1 = (double)dp1.getLatitude()*PI / 180.0;
	    double radLat2 = lat*PI / 180.0;
	    double a = radLat1 - radLat2;
	    double b = (double)dp1.getLongitude()*PI/180.0 - lng*PI/180.0;

	    double s = 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2), 2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2), 2)));
	    s = s*Earth_Radius;
	    s = Math.round(s*10000)/10000;

	    return s;
	}
	
	public void ChangeOutlierPoint(List<GeoPointModel> gpsPoints){
		
		int count = 0, count1 = 0;
		for(int i=1; i<gpsPoints.size()-1;){
			double dist = GetDistance(gpsPoints.get(i-1), gpsPoints.get(i+1));
			if(gpsPoints.get(i).getDist()>dist && gpsPoints.get(i).getSegment()==gpsPoints.get(i+1).getSegment()
					&& gpsPoints.get(i).getSegment()==gpsPoints.get(i-1).getSegment()){
				double lat = (gpsPoints.get(i-1).getLatitude() + gpsPoints.get(i+1).getLatitude())*0.5;
				double lng = (gpsPoints.get(i-1).getLongitude()+gpsPoints.get(i+1).getLongitude())*0.5;
				gpsPoints.get(i).setLatitude(lat);
				gpsPoints.get(i).setLongitude(lng);
				
				double dist0 = GetDistance(gpsPoints.get(i-1), gpsPoints.get(i));
				gpsPoints.get(i).setDist(dist0);
				double dist1 = GetDistance(gpsPoints.get(i), gpsPoints.get(i+1));
				gpsPoints.get(i+1).setDist(dist1);
				
				count1 ++; 
				i++;
			}else if(gpsPoints.get(i).getDist()>3*gpsPoints.get(i+1).getDist()
					&& gpsPoints.get(i).getSegment()==gpsPoints.get(i+1).getSegment()
					&& gpsPoints.get(i).getSegment()==gpsPoints.get(i-1).getSegment()){
				gpsPoints.remove(i-1);
				count ++;
			}else{
				i++;
			}
		}
		
		System.out.println("Remove point number is " + count + ", Filtering point is " + count1);
	}
	
	public void DepartTrajectoryToSegment(List<GeoPointModel> gpsDatas){
		// 利用时间间隙、轨迹点之间距离对轨迹进行分段处理
		// 时间间隙超过T=150s,距离间隙超过D=50m,轨迹进行分段
		int segment = 0;
		for(int i=0; i<gpsDatas.size(); i++){
			//if(gpsDatas.get(i).getTimeInterval()>150 || gpsDatas.get(i).getDist()>100){
			if(gpsDatas.get(i).getDist()>100 && gpsDatas.get(i).getTimeInterval()>10){
				// 距离超过阈值100m，且和前一个点之间的时间间隔超过阈值10s
				/*segment++;
				gpsDatas.get(i).setTimeInterval(0);
				gpsDatas.get(i).setDist(0);*/
				
				
			}
			gpsDatas.get(i).setSegment(segment);
		}
	}

	public void RemoveRepeatPoint(List<GeoPointModel> gpsDatas){
		// 用于去除重复轨迹点数据，并修改时间, 输出不重复的轨迹点数据 
		//int j = 0, k = 0, timebucket = 0;
		int count = 0;
		for(int i=1; i<gpsDatas.size();){
			if(gpsDatas.get(i).getLatitude() == gpsDatas.get(i-1).getLatitude()
					&& gpsDatas.get(i).getLongitude() == gpsDatas.get(i-1).getLongitude()){
				
				//k += gpsDatas.get(i).getTimeInterval();
				gpsDatas.remove(i);
				count ++;
			}else{
				//timebucket = gpsDatas.get(i).getTimeInterval() + k;
				//gpsDatas.get(i).setTimeInterval(timebucket);
				//k = 0;
				//j++;
				i++;
			}
		}	
		
		System.out.println("Repeat point number " + count);
	}

	public double GetDistance(GeoPointModel dp1, GeoPointModel dp2) {
		double PI = 3.1415926;
		double Earth_Radius = 6378137; // m
	    double radLat1 = (double)dp1.getLatitude()*PI / 180.0;
	    double radLat2 = (double)dp2.getLatitude()*PI / 180.0;
	    double a = radLat1 - radLat2;
	    double b = (double)dp1.getLongitude()*PI/180.0 - (double)dp2.getLongitude()*PI/180.0;
	    double s = 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2), 2)));
	    s = s*Earth_Radius;
	    s = Math.round(s*10000)/10000;
	    return s;
	}
}
