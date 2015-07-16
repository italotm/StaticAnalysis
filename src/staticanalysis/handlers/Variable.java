package staticanalysis.handlers;

public class Variable {
	
	String name, typeInstantiated, type, projectName;
	int constantOperations, notConstantOperations;
	
	public Variable (String name, String type, String projectName) {
		this.name = name;
		this.type = type;
		this.projectName = projectName;
		this.constantOperations = 0;
		this.notConstantOperations = 0;
	}
	
	public Variable (String name, String type, String projectName, String typeInstantiated) {
		this.name = name;
		this.type = type;
		this.projectName = projectName;
		this.typeInstantiated = typeInstantiated;
		this.constantOperations = 0;
		this.notConstantOperations = 0;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTypeInstantiated() {
		return typeInstantiated;
	}
	
	public void setTypeInstantiated(String typeInstantiated) {
		this.typeInstantiated = typeInstantiated;
	}
	
	public int getConstantOperations() {
		return constantOperations;
	}
	
	public int getNotConstantOperations() {
		return notConstantOperations;
	}
	
	public void incrementConstantOperations() {
		constantOperations++;
	}
	
	public void incrementNotConstantOperations() {
		notConstantOperations++;
	}
	
	public String getType() {
		return type;
	}
	
	public String getProjectName() {
		return projectName;
	}
}
