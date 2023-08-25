package com.example.demo.model;

public class Transforms {

	String name;
	boolean enabled;
	boolean useInML;
	String jsltExpression;

	public Transforms(String name, boolean enabled, boolean useInML, String jsltExpression) {
		super();
		this.name = name;
		this.enabled = enabled;
		this.useInML = useInML;
		this.jsltExpression = jsltExpression;
	}
	

	public Transforms() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isUseInML() {
		return useInML;
	}

	public void setUseInML(boolean useInML) {
		this.useInML = useInML;
	}

	public String getJsltExpression() {
		return jsltExpression;
	}

	public void setJsltExpression(String jsltExpression) {
		this.jsltExpression = jsltExpression;
	}

}
