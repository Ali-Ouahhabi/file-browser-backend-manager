package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.request;

public class RemoveReq {
	
	private boolean isFile;
	private String name;
	private String path;
	
	public RemoveReq(boolean isFile, String name, String path) {
		super();
		this.isFile = isFile;
		this.name = name;
		this.path = path;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	
}
