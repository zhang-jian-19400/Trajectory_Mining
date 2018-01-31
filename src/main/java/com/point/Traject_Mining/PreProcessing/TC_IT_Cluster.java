package com.point.Traject_Mining.PreProcessing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;

import dataset.Model.GeoPointModel;
import dataset.Model.PeopleModel;
import dataset.Model.SegmentModel;
import dataset.Model.TrajectModel;

/*参照《2012轨迹数据挖掘系统设计》---作者袁冠
 * 使用袁冠的基于结构距离的轨迹聚类方式，对于轨迹进行聚类，发掘有异常的轨迹数据
 * 实验步骤：
 * （1）计算转角，把轨迹划分成段
 * （2）将轨迹段中只有一个元素的段标记为异常段,￥****还有那些远偏离的原来路径的点应该也需要去除。
 * （3）将众多段的几何中心构建一个索引树，便于查找段的近邻
 * （4）进行类dbscan的聚类，找出异常轨迹段
 *  	在计算段与近邻的结构距离，如果结构距离在一定阈值内的点的个数超过了一个阈值，那么该段为正常段。
 */
public class TC_IT_Cluster implements Cluster{
	private Vector<PeopleModel> allPeople;
	private TC_IT_ClusterHelper helper;
	public RTree<String, Geometry> tree = RTree.create();
	public TC_IT_Cluster(Vector<PeopleModel> allPeople){
		this.allPeople = allPeople;
		this.helper = new TC_IT_ClusterHelper();
	}

	public void assignTurnAngle(){
		TrajectModel traject;
		for(PeopleModel peoplemodel:allPeople)
		{
			for(String key:peoplemodel.getTrajects().keySet())
			{
				traject = peoplemodel.getTrajects().get(key);	
				helper.computeTurnAngle(traject.getPoints());
			}
		}
	}
	
	public void assignSegment(double threshold){
		TrajectModel traject;
		for(PeopleModel peoplemodel:allPeople)
			for(String key:peoplemodel.getTrajects().keySet())
			{
				traject = peoplemodel.getTrajects().get(key);		
				Vector<SegmentModel> segments = helper.divideSegments(peoplemodel.getName()+"_"+key,traject.getPoints(),threshold);
				traject.setSegments(segments);
			}
		}
	/*
	 * 删除少于3个点的轨迹段
	 */
	public void deleteSingleSegment(){
		TrajectModel traject;
		for(PeopleModel peoplemodel:allPeople)
			for(String key:peoplemodel.getTrajects().keySet())
			{
				traject = peoplemodel.getTrajects().get(key);	
				for(SegmentModel segment:traject.getSegments()){
					if(segment.getOffset()<=1){
						segment.setDelete(false);
					}
				}
			}
	}
	/*
	 * 但是索引树的节点存储的是轨迹段中几何距离的中心点
	 * 通过构造段的索引树，以便后期能快速匹配到给定段的近邻段。
	 */
	public void initSegmentRtree(){	
		double number=0,count=0,num=0;
		TrajectModel traject;
		for(PeopleModel peoplemodel:allPeople)
			for(String key:peoplemodel.getTrajects().keySet())
			{
				num++;
				traject = peoplemodel.getTrajects().get(key);	
				for(SegmentModel segment:traject.getSegments()){					
						String flag = peoplemodel.getName()+"_"+segment.getParent_Trajectory()+"_"+segment.getPosition();
						GeoPointModel point= helper.computeCenterPoint(segment.toPoints(traject.getPoints()));
						this.tree=this.tree.add(flag,Geometries.point(point.getLongitude(),point.getLatitude()));
						number++;
						count+=segment.getOffset();
				}
			}
		System.out.println("一共有效轨迹段:"+number+"条");
		System.out.println("一共轨迹数目："+num+"条");
		System.out.println("剩余轨迹点数:"+count+"个");
	}
	/*
	 * 算法逻辑：
	 * 	遍历每个轨迹段：
	 * 		if 段未被访问     且    段未被删除
	 * 			把该轨迹段加入到队列中
	 * 			 取出轨迹段，获取其密度近邻
	 * 				if 近邻数少于阈值20
	 * 					if 该段是队列的第一个元素      ------》认为该段有异常，是异常段
	 * 					else 段不是第一个元素  ---------》用队列中元素的属性归类该段。
	 * 				else 近邻个数多余阈值20
	 * 					为该段赋予类别标记，而且查找该断的近邻中，未被访问     而且     未被删除的轨迹段了
	 */
	public void TC_IT(Vector<PeopleModel> people){
		double threshold = 100;//这个是结构距离阈值
		int test=0;
		int number=0;
		TrajectModel traject;
		int ClusterId=0;
		boolean canCount=false;
		for(PeopleModel peoplemodel:people){
			for(String key:peoplemodel.getTrajects().keySet())
			{
				traject = peoplemodel.getTrajects().get(key);	
				int position=0;
				for(int i=0;i<traject.getSegments().size();i++){	
					SegmentModel segment = traject.getSegments().get(i);
					position++;
					if(segment.isVisited()==false)
					{	System.out.println("段"+number++);
						int first=0;					
						if(canCount==true) ClusterId++;	
						Queue<SegmentModel> queue = new LinkedList<SegmentModel>();	
						queue.offer(segment);
						segment.setVisited(true);
						while(!queue.isEmpty())
						{
							segment = queue.poll();
							Vector<String> segmentsName = helper.getSegmentsFromRtree(this.tree,people,segment,5);
							System.out.println("段的近邻数量"+segmentsName.size());
				//			if(segmentsName.size()>5){
							Vector<SegmentModel> segments = helper.filterSegments(segmentsName,segment,allPeople,threshold);
							System.out.println("修改后段的数量:"+segments.size());
							if(segments.size()<5)
								if(++first==1) 
								{
									segment.setError(false);
									canCount=false;
								}
								else
								{
								}
							else 
							{		
								canCount=true;
								segment.setClusterId(ClusterId);
								segment.setError(true);
								for(SegmentModel segmenttemp:segments) 
								{
									if(segmenttemp.isVisited()==false)				
										queue.offer(segmenttemp);	
									segmenttemp.setVisited(true);
									segmenttemp.setClusterId(ClusterId);
									segmenttemp.setError(true);
								}
								System.out.println("当前队列中有"+queue.size()+"段数");
							}	
							
						}
						}			
					}
				}	
			}
		System.out.println(ClusterId+"个类");
	}
	public void cluster(){
		assignTurnAngle();
		System.out.println("转角计算完成");
		assignSegment(30);//默认60度为其划分阈值
		System.out.println("轨迹段划分完成");
		initSegmentRtree();
		System.out.println("索引树构造完成");
		TC_IT(allPeople);
	}
}
