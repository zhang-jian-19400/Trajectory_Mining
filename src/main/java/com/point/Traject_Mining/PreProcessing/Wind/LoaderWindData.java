package com.point.Traject_Mining.PreProcessing.Wind;

import com.point.Traject_Mining.PreProcessing.BasicDetect;
import com.point.Traject_Mining.PreProcessing.LoaderData;

import dataset.DataHelper;

public class LoaderWindData implements LoaderData{

	@Override
	public void load() {
		// TODO Auto-generated method stub
//		String dirPath = "D:/学习资料/城市轨迹数据/Geolife Trajectories 1.3/Geolife Trajectories 1.3/Data";
		String dirPath = "D:/学习资料/数据集/大西洋飓风数据/hurricane";  //该目录下的文件刚好是轨迹对象的个体。name/traject.file
		//读取数据到内存中
		datahelper.initTraject(dirPath,"dat");
	}
	
}
