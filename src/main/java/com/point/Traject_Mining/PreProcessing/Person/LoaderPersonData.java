package com.point.Traject_Mining.PreProcessing.Person;

import com.point.Traject_Mining.PreProcessing.LoaderData;

public class LoaderPersonData implements LoaderData{

	@Override
	public void load() {
		String dirPath = "D:\\学习资料/城市轨迹数据\\Geolife Trajectories 1.3\\Geolife Trajectories 1.3\\Data"; //该目录下的文件刚好是轨迹对象的个体。name/traject.file
		//读取数据到内存中
		datahelper.initTraject(dirPath,"plt");
	}

}
