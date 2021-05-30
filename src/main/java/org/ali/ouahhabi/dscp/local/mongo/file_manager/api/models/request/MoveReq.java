package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.request;

public class MoveReq {
	
	private String name;
	private String from;
	private String to;
	
	public MoveReq(String name, String from, String to) {
		super();
		this.name = name;
		this.from = from;
		this.to = to;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	

}
