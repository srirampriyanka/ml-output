package com.example.demo.model;

public class Input {
	Feature feature;
	String json;
	
	public Input(Feature feature, String json) {
		super();
		this.feature = feature;
		this.json = json;
	}
	
	public Input() {
		super();
	}
	
	public Feature getFeature() {
		return feature;
	}
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}

}
