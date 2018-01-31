package dataset.Model;

public class GeoPointModel implements Cloneable,Model{
	double latitude;//γ��
	double longitude;//����
	double timedistance;//���� 12/30/1899������
	float transitionangle;//ת��
	
	//Ϊ�˼��ݴ���BasicDetect
	float timeInterval=0;
	double dist = 0;
	int segment ;
	double speed = 0;
	//
	public GeoPointModel(){}
	public GeoPointModel(double latitude,double longtitude)
	{
		this.latitude = latitude;
		this.longitude= longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}	
	public double getTimedistance() {
		return timedistance;
	}
	public void setTimedistance(double timedistance) {
		this.timedistance = timedistance;
	}
	public float getTransitionangle() {
		return transitionangle;
	}
	public void setTransitionangle(float transitionangle) {
		this.transitionangle = transitionangle;
	}
	public float getTimeInterval() {
		return timeInterval;
	}
	public void setTimeInterval(float timeInterval) {
		this.timeInterval = timeInterval;
	}
	public double getDist() {
		return dist;
	}
	public void setDist(double dist) {
		this.dist = dist;
	}
	public int getSegment() {
		return segment;
	}
	public void setSegment(int segment) {
		this.segment = segment;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speep) {
		this.speed = speep;
	}
	public Model toModel(){
		return (Model)this;
	}
	public Object clone() throws CloneNotSupportedException{
		GeoPointModel model = (GeoPointModel)super.clone();
		return model;
	}
	}
