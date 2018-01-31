package dataset.Model;

import java.util.Vector;

public class SegmentModel implements Model{
	String peopleName;
	String Parent_Trajectory;
	int clusterId = -1;
	int position;
	int startPosition;
	int offset;
	boolean visited = false;//判断是否已经访问过
	boolean error = true;	//标识是否为异常点,false 为异常点，TRUE为正常点
	boolean delete = true; //标识是否删除了，false标识删除了，true标识没有删除
	public int getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public String getParent_Trajectory() {
		return Parent_Trajectory;
	}
	public void setParent_Trajectory(String parent_Trajectory) {
		Parent_Trajectory = parent_Trajectory;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}	
	public int getClusterId() {
		return clusterId;
	}
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public String getPeopleName() {
		return peopleName;
	}
	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
	}
	public Vector<GeoPointModel> toPoints(Vector<GeoPointModel> points){
		Vector<GeoPointModel> pointset =new Vector<GeoPointModel>();
		try{
		for(int i=startPosition;i<=startPosition+offset;i++) pointset.add(points.get(i));
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println();
		}
		return pointset;
	}
	public Vector<GeoPointModel> toTrajectPoints(Vector<GeoPointModel> points){
		Vector<GeoPointModel> pointset =new Vector<GeoPointModel>();
		try{
		for(int i=startPosition;i<startPosition+offset;i++) pointset.add(points.get(i));
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println();
		}
		return pointset;
	}
}
