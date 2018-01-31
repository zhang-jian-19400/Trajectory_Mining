package com.point.Traject_Mining.PreProcessing;

import java.util.Vector;
import dataset.Model.GeoPointModel;
// new StructDistance(GeoModel[] segment1,GeoModel[] segment2).getStructDistance()
/*
 * 此部分的四个距离均根据论文改写。
 */
public class StructDistance {
	TC_IT_ClusterHelper process = new TC_IT_ClusterHelper();
	private Vector<GeoPointModel> segment1;
	private Vector<GeoPointModel>  segment2;
	
	public int setSegment(Vector<GeoPointModel>  segment1,Vector<GeoPointModel>  segment2) {
		if(segment1.size()<2||segment2.size()<2)
			return -1;
		this.segment1 = segment1;
		this.segment2 = segment2;
		return 0;
	}
	class group<T>{
			T o1;
			T o2;
			T o3;
			public group(T o1,T o2,T o3){
				this.o1 = o1;
				this.o2 = o2;
				this.o3 = o3;
			}
			public group(T o1,T o2){
				this(o1,o2,null);
				
			}
			public group(){
				
			}
		}
	
	//第一个元素的转角为0
	public double calculateAngleDistance(){
		int size = this.segment1.size()<this.segment2.size()?this.segment1.size():this.segment2.size();
		double sum=0;
		if(size==1)return -1;
		for(int i=1;i<size;i++){
			double angle1 = segment1.get(i).getTransitionangle();
			double angle2 = segment2.get(i).getTransitionangle();
			if(Math.abs(angle2)+Math.abs(angle1)==0);
			else	
			 sum +=Math.abs(angle1-angle2)/(Math.abs(angle2)+Math.abs(angle1));
		}
		return sum/(size-1);
	}
	/*
	 * 已知两个轨迹各个点坐标，求出最长最短距离
	 * 先找出segment1到segment2中距离最小的点。然后在这些点中找到最长的距离。
	 */
	public double calculateLocDistance(){
		double min = Constant.inf;
		double max = Constant.min;
		double distance;
		for(int i=0;i<segment1.size();i++)
		{	
			min = Constant.inf;
			for(int j=0;j<segment2.size();j++){
				distance = process.geoDistance(segment1.get(i),segment2.get(j));
				min = min<distance?min:distance;
			}
			max = max > min ? max : min;
		}
		return max;
	}
	/*
	 * 已知两个轨迹段的起点，终点。得到两个方向角
	 * min(Li,Lj)*sin(x)  0=<x<=90
	 * min(Li,Lj)
	 */
	public double calculateDirDistance(){
		double dirdistance;
		GeoPointModel seg1Start = segment1.get(0);
		GeoPointModel seg1End   = segment1.get(segment1.size()-1);
		GeoPointModel seg2Start = segment2.get(0);
		GeoPointModel seg2End   = segment2.get(segment2.size()-1);
		GeoPointModel Point     = new GeoPointModel();
		//计算转角,将两条轨迹移动至同一个起点上。以seg2为参照
		Double y = seg2Start.getLatitude()-seg1Start.getLatitude();
		Double x = seg2Start.getLongitude()-seg1Start.getLongitude();
		Point.setLatitude(seg1End.getLatitude()+y);
		Point.setLongitude(seg1End.getLongitude()+x);		
		double angle = process.pointToangle(seg2Start,Point, seg2End);
		double segment1Length = process.geoDistance(seg1Start, seg1End);
		double segment2Length = process.geoDistance(seg2Start, seg2End);
		double length = segment1Length < segment2Length ? segment1Length : segment2Length;
		if((angle>=0)&&(angle<90))
			dirdistance = Math.sin(angle/180*3.1415)*length;
		else
			dirdistance = length;
		return dirdistance;
	}

	public group getThreeSpeed(Vector<GeoPointModel> segment)
	{
		double speedaverage =0;
		double minspeed = Constant.inf;
		double maxspeed = Constant.min;
		for(GeoPointModel point:segment){
			speedaverage +=point.getSpeed();
			minspeed=minspeed<point.getSpeed()?minspeed:point.getSpeed();
			maxspeed = maxspeed>point.getSpeed()?maxspeed:point.getSpeed();
		}
		speedaverage = speedaverage/segment.size();
		return new group(minspeed,speedaverage,maxspeed);
	}
	/*
	 * According the definition of speeddifference
	 */
	public double calculateSpeedDistance(){
		group<Double>g1 = getThreeSpeed(segment1);
		group<Double>g2 = getThreeSpeed(segment2);
		double SpeedDifference = (Math.abs(g1.o1-g2.o1)+Math.abs(g1.o2-g2.o2)+Math.abs(g1.o3-g2.o3));
		return SpeedDifference/3;	
	}
	
	public double getStructDistance(double... W){
		double angle = calculateAngleDistance();
		double speed = calculateSpeedDistance();
		double dir = calculateDirDistance();
		double loc = calculateLocDistance();
		if(angle<0||speed<0||dir<0||loc<0)
			return -1;
		else
			return  calculateAngleDistance()*W[0]+
					calculateSpeedDistance()*W[1]+
					calculateDirDistance()*W[2]+
					calculateLocDistance()*W[3];
		}		 
}
