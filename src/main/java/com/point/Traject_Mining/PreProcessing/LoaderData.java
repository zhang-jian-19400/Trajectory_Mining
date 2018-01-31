package com.point.Traject_Mining.PreProcessing;

import dataset.DataHelper;

public interface LoaderData {
	DataHelper datahelper = new DataHelper();
	BasicDetect basicdetect = new BasicDetect();
	public void load();
}
