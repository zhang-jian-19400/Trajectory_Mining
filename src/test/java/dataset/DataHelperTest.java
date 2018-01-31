package dataset;

import dataset.Model.PeopleModel;
import dataset.Model.TrajectModel;

public class DataHelperTest {
	public static void main(String args[]){
		double number=0;
		int trajectnumber=0;
		long starttime = System.currentTimeMillis();
		DataHelper datahelper = new DataHelper();
/*		String dirPath = "D:/学习资料/城市轨迹数据/Geolife Trajectories 1.3/Geolife Trajectories 1.3/Data";
		datahelper.initPeoTrajectDBByGeolife(dirPath,"plt");
*/		
		String dirPath = "D:/java_project/stormdata/hurricane";
		datahelper.initTraject(dirPath,"dat");
		System.out.println(datahelper.allPeople.size());
		for(PeopleModel people:datahelper.allPeople){
			for(String  str:people.getTrajects().keySet()){
				TrajectModel traject = people.getTrajects().get(str);
				trajectnumber++;
				number+=traject.getPoints().size();
			}
		}
		System.out.println("轨迹点一共有"+number+"个,"+trajectnumber+"条轨迹");
		long endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);
	}
}
