package dataset;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import dataset.Model.GeoPointModel;
import dataset.Model.Model;
import dataset.Model.PeopleModel;

/*
	 * 该类主要用于与数据库的交互，连接，断开，增删改查等操作。
	 */
public class DatasetOperation {
	static Connection conn = null;
	
	public static void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dburl = "jdbc:mysql://localhost:3306/trajects?useUnicode=true&characterEncoding=UTF-8";
			conn = DriverManager.getConnection(dburl, "root", "123456");
			System.out.println("connection built");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void close(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//插入到哪个表中的什么数据
	public static boolean insert(String tablename,Vector<Model> tabledata){
		int add=0;
		if(tablename.startsWith("traject")){
				try {
					//查看有没有这个数据库		
					String sql = "insert into "+tablename+"("+TrajectTable.Longtitude+","+TrajectTable.Latitude+","+TrajectTable.Time+")"+
								" values(?,?,?)";
					PreparedStatement pst = conn.prepareStatement(sql);
					for(Model model:tabledata){
						GeoPointModel geopointmodel = (GeoPointModel)model;
						pst.setDouble(1, geopointmodel.getLongitude());
						pst.setDouble(2, geopointmodel.getLatitude());
						pst.setDouble(3, geopointmodel.getTimedistance());		
						pst.addBatch();
						if(++add%2000==0) pst.executeBatch();    
						}
					//把多余的添加到数据库中
					pst.addBatch();
					pst.executeBatch();  
					//表示一次文件内容结尾,插入-1表示空。     
	//			     conn.commit();      
				     pst.clearBatch();
				     pst.close();
					} 	
				catch (SQLException e) {
				// TODO Auto-generated catch block
	//			e.printStackTrace();
				return false;
			}  	
		}
		else if(tablename.equals("person_info")){
			PeopleModel peoplemodel = (PeopleModel)tabledata.get(0);
			String creattablesql  = "CREATE TABLE IF NOT EXISTS "+"traject_"+peoplemodel.getName()+" ("+TrajectTable.Longtitude+" Double,"+TrajectTable.Latitude+" Double,"+TrajectTable.Time+" Double);";			
			String sql = "insert into person_info(name,peo_traject_table) values ('"+peoplemodel.getName()+"',"+"'traject_"+peoplemodel.getName()+"')";	
			try {
				Statement stmt = conn.createStatement();
				if(stmt.execute(creattablesql)==false)
					{
					String Initsql = "truncate traject_"+peoplemodel.getName();
					stmt.execute(Initsql);
					}
				else
				stmt.execute(sql);
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			finally{}
		}
		return true;
	}
	public static void delete(){}
	public static void search(){}
	
	//用于动态创建用户表
	public static void createTable(String tablename){
		
	}
}
