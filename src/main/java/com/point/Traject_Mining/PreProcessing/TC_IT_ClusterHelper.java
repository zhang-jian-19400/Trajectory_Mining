package com.point.Traject_Mining.PreProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.internal.EntryDefault;

import dataset.Model.GeoPointModel;
import dataset.Model.PeopleModel;
import dataset.Model.SegmentModel;
import dataset.Model.TrajectModel;

public class TC_IT_ClusterHelper {
	//计算转角*************************** computeTurnAngle，pointToangle，geoDistance
	public Vector <GeoPointModel> computeTurnAngle(Vector <GeoPointModel> geoInfos){
		int number = geoInfos.size();
		GeoPointModel point1,point2,point3;
		double ab,ac,bc,angle,latitude;//单位是meter.
		for(int i = 1;i<number-1;i++){
			point1 = geoInfos.get(i-1);
			point2 = geoInfos.get(i);
			point3 = geoInfos.get(i+1);
			angle = pointToangle(point2,point1,point3);
			point2.setTransitionangle((float)angle);
		}
		return geoInfos;
	}
	
	public double pointToangle(GeoPointModel point2,GeoPointModel point1,GeoPointModel point3){
		double ab,ac,bc,angle,latitude;
		ab = geoDistance(point1,point2);
		bc = geoDistance(point2,point3);
		ac = geoDistance(point1,point3);
		double crossproduct = (point2.getLongitude()-point1.getLongitude())*(point3.getLatitude()-point2.getLatitude());
		crossproduct = crossproduct-(point3.getLongitude()-point2.getLongitude())*(point2.getLatitude()-point1.getLatitude());
		//判断正负
		if(Math.abs(ab+bc-ac)<0.00000000000001)
			angle = 180;
		else
			{
			if((ab*ab+bc*bc-ac*ac)/(2*ab*bc)<-1)
				angle = 180;
			else if((ab*ab+bc*bc-ac*ac)/(2*ab*bc)>1)
				angle = 0;
			else{
			angle = Math.acos((ab*ab+bc*bc-ac*ac)/(2*ab*bc));
				angle = Math.abs(angle*(180/Math.PI));//弧度变角度
			}
			}
		angle = crossproduct<0? (float)angle-180:180-(float)angle;
		return angle;
	}
	public double geoDistance(GeoPointModel point1,GeoPointModel point2){
		double distance,A,B;
		double PI = 3.1415926;
		double Earth_Radius = 6378.137; //km
	    double radLat1 = (double)point1.getLatitude()*PI/ 180.0;
	    double radLat2 = (double)point2.getLatitude()*PI/ 180.0;
	    double b = (double)point1.getLongitude()*PI/180.0 - (double)point2.getLongitude()*PI/180.0;
	    double s;
	    if(Math.cos(radLat2)*Math.cos(radLat1)*Math.cos(b)+Math.sin(radLat2)*Math.sin(radLat1)>1)
	    s = Math.acos(1);
	    else if(Math.cos(radLat2)*Math.cos(radLat1)*Math.cos(b)+Math.sin(radLat2)*Math.sin(radLat1)<-1)
	    s = Math.acos(-1);
	    	else s=Math.acos(Math.cos(radLat2)*Math.cos(radLat1)*Math.cos(b)+Math.sin(radLat2)*Math.sin(radLat1));
	    s = s*Earth_Radius;
	    s = 1000*s;
	    return s;//m is the unit of measure
	}
	//****************************************
	/*
	 * 遍历每个点处的转角，判断如果角度的变化超过一个阈值，则将这段放入到一个子段中。
	 * 需要重新编写
	 */
	public Vector<SegmentModel> divideSegments(String trajectname,Vector<GeoPointModel> geoInfos,double threshold){		
		Vector<SegmentModel> segments = new Vector<SegmentModel>();
		String[] name = trajectname.split("_");
		int start=0,end=-1,length,position=0;
		Vector trajects =new Vector <GeoPointModel[]> (); 
		for(GeoPointModel obj:geoInfos){
			end++;
			if(Math.abs(obj.getTransitionangle())>threshold)	//停止向前扫描，将数据放入到trajectors中。
				{length = end - start;
				SegmentModel segment = new SegmentModel();
				segment.setParent_Trajectory(name[1]); //1950_D:/java_project/stormdata/hurricane/1950/16.dat
				segment.setPeopleName(name[0]);
				segment.setStartPosition(start);
				segment.setOffset(length);
				segment.setPosition(position++);
				segments.add(segment);
				start = end;
				}
		}
		//最后的收尾工作
		length = end - start;
		if(length!=0){
		SegmentModel segment = new SegmentModel();
		segment.setParent_Trajectory(name[1]);
		segment.setPeopleName(name[0]);
		segment.setStartPosition(start);
		segment.setOffset(length);
		segment.setPosition(position++);
		segments.add(segment);
		start = end;
		}
/*		if(trajectname.equals("000_"+"D:/学习资料/城市轨迹数据/Geolife Trajectories 1.3/Geolife Trajectories 1.3/Data/000/Trajectory/20090629173335.plt"))
		System.out.println("000_"+"D:/学习资料/城市轨迹数据/Geolife Trajectories".length());*/
		return segments;
	}
	
	public GeoPointModel computeCenterPoint(Vector<GeoPointModel> points){
		double x=0,y=0;
		for(GeoPointModel point:points){
			x+=point.getLongitude();
			y+=point.getLatitude();
		}
		int number = points.size();
		GeoPointModel point = new GeoPointModel(x/number,y/number);
		point.setLatitude(y/number);
		point.setLongitude(x/number);
		return point;
	}
	/*
	 * @param tree已经构造好的数据结构
	 * @param traject轨迹信息
	 * @param segment段信息
	 * @param radius是搜索区域的半径
	 * @return 返回一组大概是目标的轨迹段名词，人名+轨迹文件名称+位置
	 */
	public Vector<String> getSegmentsFromRtree(RTree<String, Geometry> tree,Vector<PeopleModel> people,SegmentModel segment,double radius){
		Vector<String> segmentsName = new Vector<String>();
		TrajectModel traject = new TrajectModel();
		String peoplename = segment.getPeopleName();
		String parent = segment.getParent_Trajectory();
		for(PeopleModel peo:people){
			if(peo.getName().equals(peoplename)){	
						traject = peo.getTrajects().get(parent);					
			}
		}	
		GeoPointModel point= computeCenterPoint(segment.toPoints(traject.getPoints()));
//		System.out.println("中心点的坐标为："+"("+point.getLongitude()+","+point.getLatitude()+")");
		Circle searchcircle = Circle.create(point.getLongitude(),point.getLatitude(),radius);
		List<Entry<String, Geometry>> entries =tree.search(searchcircle).toList().toBlocking().single();
		if(entries.size()==0)
			return segmentsName;
		else{
			Entry lastentry ;//= new EntryDefault<String,Point>("",Geometries.point(127.3,25.3));
			for(Entry<String, Geometry> entry:entries)
				{lastentry = entry;
				segmentsName.add(((Entry<String,Point>)lastentry).value());
				}
			return segmentsName;
	}
	}
	public Vector<SegmentModel> filterSegments(Vector<String>neighborsegments,SegmentModel coresegment,Vector<PeopleModel> people,double threshold){
		StructDistance structDistance = new StructDistance();
		int number=0;
		double sum=0;
		Vector<SegmentModel> segments = new Vector<SegmentModel>();
		Vector<GeoPointModel> corepoint = new Vector<GeoPointModel>();
		//********获取到核心轨迹段对应的轨迹的点
		for(PeopleModel p:people){
			if(p.getName().equals(coresegment.getPeopleName())){
			TrajectModel traject = p.getTrajects().get(coresegment.getParent_Trajectory());
			corepoint = coresegment.toPoints(traject.getPoints());
				break;
			}
		}
		//**********
		//****将邻居轨迹点中符合条件的放到	最后的轨迹段的集合中去
		for(String neighborsegment:neighborsegments){
			String neighborstr[] = neighborsegment.split("_");
			SegmentModel segment = new SegmentModel();
			segment.setPeopleName(neighborstr[0]);
			segment.setParent_Trajectory(neighborstr[1]);
			segment.setPosition(Integer.parseInt(neighborstr[2]));
			segment = getSegmentFromName(segment,people);
			for(PeopleModel p:people){
				if(p.getName().equals(segment.getPeopleName())){
				TrajectModel traject = p.getTrajects().get(segment.getParent_Trajectory());
					Vector<GeoPointModel> points = segment.toPoints(traject.getPoints());
					//不在同一条轨迹上的段，异常的段不参与
					if(!(segment.getParent_Trajectory().equals(coresegment.getParent_Trajectory()))){
						structDistance.setSegment(corepoint,points);
						double distance = structDistance.getStructDistance(0.25,0.25*3.6,0.25/1000,0.25/1000);
						sum+=distance;
						if(distance < threshold) segments.add(segment);
						break;
					}					
				}
			}	
		}
		System.out.println("平均结构距离为:"+sum/neighborsegments.size());
				return segments;
	}
	/*
	 * @param name是轨迹段的位置信息的的字符串用"_"连接的用户名，轨迹的存储路径信息，轨迹段在路径中的位置。
	 * @param people是总的数据集。用于自顶向下的取数据。
	 */
	public SegmentModel getSegmentFromName(SegmentModel segment,Vector<PeopleModel> people){
		SegmentModel seg = new SegmentModel();
			if(segment!=null){
					for(PeopleModel p:people){
						if(p.getName().equals(segment.getPeopleName())){
						TrajectModel traject = p.getTrajects().get(segment.getParent_Trajectory());
							if(traject!=null)
							for(SegmentModel segmenttemp:traject.getSegments()){
								if(segmenttemp.getPosition()==segment.getPosition()){
									seg = segmenttemp; return seg;
								}
							}
						}
					}
				}
		return seg;
	}
}
