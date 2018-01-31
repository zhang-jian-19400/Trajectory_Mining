package dataset;

import java.util.Vector;

import dataset.Model.GeoPointModel;
import dataset.Model.PeopleModel;

public class DatasetOperationTest {
	public static  void main(String args[]){
		DatasetOperation Operation = new DatasetOperation();
		Operation.connect();
		PeopleModel peoplemodel = new PeopleModel();
		peoplemodel.setName("zhangjian");
		Vector people = new Vector();
		people.addElement(peoplemodel);
		Operation.insert("person_info",people);
		
		
		Vector points = new Vector();
		GeoPointModel point = new GeoPointModel();
		point.setLatitude(3.2554);
		point.setLongitude(3.2544);
		point.setTimedistance(5.2154);
		points.addElement(point);
		Operation.insert("traject_zhangjian",points);
		Operation.close();
	}
}
