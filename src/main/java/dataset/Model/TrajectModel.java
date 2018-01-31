package dataset.Model;

import java.util.Vector;

public class TrajectModel implements Model{
	private Vector<GeoPointModel> points;
	private Vector<SegmentModel> segments;
	public TrajectModel(){
		this.points = new Vector<GeoPointModel>();
		this.segments = new Vector<SegmentModel>();
	}
	public Vector<GeoPointModel> getPoints() {
		return points;
	}
	public void setPoints(Vector<GeoPointModel> points) {
		this.points = points;
	}
	public Vector<SegmentModel> getSegments() {
		return segments;
	}
	public void setSegments(Vector<SegmentModel> segments) {
		this.segments = segments;
	}
	public Vector<Model> GeoPointModeltoModels(){
		Vector<Model> geopoints = new Vector<Model>();
		for(GeoPointModel geopoint:this.points){
			geopoints.add(geopoint);
		}
		return geopoints;
	}
}
