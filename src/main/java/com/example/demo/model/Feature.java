package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Feature {
	
	Integer id;
	String name;
	
	public Feature(Integer id, String name, List<Transforms> transforms) {
		super();
		this.id = id;
		this.name = name;
		this.transforms = transforms;
	}
	
	public Feature() {
		super();
		// TODO Auto-generated constructor stub
	}

	List<Transforms> transforms = new ArrayList<Transforms>();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Transforms> getTransforms() {
		return transforms;
	}
	public void setTransforms(List<Transforms> transforms) {
		this.transforms = transforms;
	}
	
	

}
