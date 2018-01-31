package dataset.Model;

import java.util.HashMap;
import java.util.Vector;

public class PeopleModel implements Model,Cloneable {
	private String name;
	private boolean sex;
	private int age;
	private String job;
	private HashMap<String,TrajectModel> trajects; //pltfilename,trajects
	public PeopleModel(){
		this.name = "";	
		this.trajects = new HashMap();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, TrajectModel> getTrajects() {
		return trajects;
	}

	public void setTrajects(HashMap<String, TrajectModel> content) {
		trajects = content;
	}
	public Model toModel(){
		return (Model)this;
	}

	public Object clone() throws CloneNotSupportedException{
		PeopleModel model = (PeopleModel)super.clone();
		model.trajects = (HashMap)this.trajects.clone();
		return model;
	}	
}
