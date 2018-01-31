package dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataset.Model.GeoPointModel;
import dataset.Model.Model;
import dataset.Model.PeopleModel;
import dataset.Model.TrajectModel;


/*
 * 承担包括数据读入从文件读到数据库，
 * 从数据库加载到内存工作
 */
public class DataHelper {
	
	Vector<PeopleModel> allPeople = new Vector<PeopleModel>();
	PeopleModel people = new PeopleModel();
	
	public Vector<PeopleModel> getAllPeople() {
		return allPeople;
	}
	public void setAllPeople(Vector<PeopleModel> allPeople) {
		this.allPeople = allPeople;
	}
	/*
	 * 读取plt文件
	 * @param filename为要读取的文件内容
	 * @return 返回一个GeoPointModel的数据链表
	 */
	public Vector <GeoPointModel> readFromPltFile(String filename) throws IOException{
		File file = new File(filename);
		Vector <GeoPointModel> geoInfos = new Vector <GeoPointModel>();
		if(!file.exists()||file.isDirectory())
			throw new FileNotFoundException();
		FileReader filereader = new FileReader(file);
		BufferedReader br = new BufferedReader(filereader);
		String temp = null;
		StringBuffer sb = new StringBuffer();
		//将plt文件中的数据读到内存中。
		int linenum;
		for(linenum=0;linenum<7;linenum++){temp =br.readLine();}		
		while(temp!=null){
			//do somgting 
			String tempinfo[] = new String[7];
			tempinfo = temp.split(",");
			GeoPointModel geomodel = new GeoPointModel();
			geomodel.setLatitude(Double.parseDouble(tempinfo[0]));
			geomodel.setLongitude(Double.parseDouble(tempinfo[1]));
			geomodel.setTimedistance(Double.parseDouble(tempinfo[4]));
			geoInfos.addElement(geomodel);
			temp = br.readLine();
		}
		return geoInfos;
	}
	
	public Vector <GeoPointModel> readFromDatFile(String filename)  throws IOException{
		File file = new File(filename);
		Vector <GeoPointModel> geoInfos = new Vector <GeoPointModel>();
		if(!file.exists()||file.isDirectory())
			throw new FileNotFoundException();
		FileReader filereader = new FileReader(file);
		BufferedReader br = new BufferedReader(filereader);
		String temp = null;
		StringBuffer sb = new StringBuffer();
		//将dat文件中的数据读到内存中。
		int linenum;
		for(linenum=0;linenum<4;linenum++){temp =br.readLine();}		
		while(temp!=null){
			//do somgting 
			temp = dup(temp," ");
			String tempinfo[];
			tempinfo = temp.split(" ");
			String time[]=tempinfo[4].split("/");
			if(time.length==3){
			GeoPointModel geomodel = new GeoPointModel();
			
			
			String timestacke;
			timestacke = 1970+time[0]+time[1]+" "+time[2].substring(0,2)+"00"+"00";
			String timetormat = "yyyyMMdd HHmmss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timetormat);
			try {
				Date date = simpleDateFormat.parse(timestacke);
				double ts = date.getTime();
				geomodel.setLatitude(Double.parseDouble(tempinfo[2]));  //纬度
				geomodel.setLongitude(Double.parseDouble(tempinfo[3]));	//经度
				geomodel.setTimedistance(ts);	//时间
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			geoInfos.addElement(geomodel);
			}
			temp = br.readLine();
			
		}
		return geoInfos;
	}
	/*
	 * 以轨迹文件为单位写入数据库中
	 * @param 表名哪些列，数 	 据
	 */
	public boolean writeToDatabase(String tablename,Vector<Model> peoInfos){
		DatasetOperation.connect();
		boolean bool = DatasetOperation.insert(tablename,peoInfos);		
		DatasetOperation.close();
		return bool;
	}
	/*
	 * 分析个人数据，以个人为单位上传个人轨迹数据
	 * @param dirname个人一级的目录名
	 */
	public void peoFilesToBase(String dirname){
		int numer=0;
		File file = new File(dirname);
		if(file.exists()&&file.isDirectory()){
			for(File subfile:file.listFiles()){
				String name=subfile.getName();
//				if(numer++==20) break;
				if(subfile.isFile()&&name.endsWith("plt")){
					//以文件为单位写入到数据库中
					try {
						Vector<GeoPointModel> peoInfos = readFromPltFile(dirname+"/"+name);
						TrajectModel traject = new TrajectModel();
						traject.setPoints(peoInfos);
						people.getTrajects().put(dirname+"/"+name, traject);
/*						if(false==writeToDatabase("traject_"+people.getName(),traject.GeoPointModeltoModels())){
							System.out.println("轨迹数据插入错误！");	
						}*/	
					}catch(IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(subfile.isFile()&&name.endsWith("dat")){
					try {
						Vector<GeoPointModel> peoInfos = readFromDatFile(dirname+"/"+name);
						TrajectModel traject = new TrajectModel();
						traject.setPoints(peoInfos);
						people.getTrajects().put(dirname+"/"+name, traject);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else
				{
					peoFilesToBase(dirname+"/"+name);
				}
			}
		}
	}
	/*
	 * dirname到data那一级
	 * D:\学习资料\城市轨迹数据\Geolife Trajectories 1.3\Geolife Trajectories 1.3\Data
	 * type = dat plt
	 */
	public void initTraject(String dirname,String type){
		switch(type){
		case "plt":{initTrajectDBByGeolife(dirname);}break;
		case "dat":{initTrajectDBByDatFile(dirname);}break;
		default:break;
		}
	}
	public void initTrajectDBByGeolife(String dirname){
		File file = new File(dirname);
		int number=0;
		if(file.exists()&&file.isDirectory()){
			for(File subfile:file.listFiles()){
				if(number++==10)break; //只读前50人
				String name=subfile.getName();	//这一级name就是名字
				people = new PeopleModel();		//清空people中的内容
				people.setName(name);				
//				Vector person = new Vector();
//				person.addElement(people.toModel());
				//如果在数据库中，插入新的用户信息不成功，那么则不能创建新的表项
//				if(writeToDatabase("person_info",person)==true){
				//写入到人的数据库中
				peoFilesToBase(dirname+"/"+name);
				try {
					allPeople.add((PeopleModel)people.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void initTrajectDBByDatFile(String dirname){
		File file = new File(dirname);
		int number=0;
		if(file.exists()&&file.isDirectory()){
			for(File subfile:file.listFiles()){
				if(number++==10)break; //只读前50人
				String name=subfile.getName();	//这一级name就是名字
				people = new PeopleModel();		//清空people中的内容
				people.setName(name);				
				Vector person = new Vector();
				person.addElement(people.toModel());
				peoFilesToBase(dirname+"/"+name);
				try {
					allPeople.add((PeopleModel)people.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public String dup(String string,String tag){
		Pattern p = Pattern.compile("\\s+");
		 Matcher m = p.matcher(string);
         string = m.replaceAll(" ");
		return string;
	}
}
