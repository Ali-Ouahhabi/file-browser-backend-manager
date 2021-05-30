package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.request;

public class RenameReq {

	private boolean isFile; 
	private String name;
	private String path;
	private String newName;
	private String newPath;
	
	public RenameReq(boolean isFile ,String name, String path, String newName, String newPath) {
		super();
		this.isFile = isFile;
		this.name = name;
		this.path = path;
		this.newName = newName;
		this.newPath = newPath;
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

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewPath() {
		return newPath;
	}

	public void setNewPath(String newPath) {
		this.newPath = newPath;
	}
	
	
}
