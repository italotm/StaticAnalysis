package staticanalysis.handlers;

public class Variable {
	
	String name, typeInstantiated;
	int constantOperations, notConstantOperations;
	
	public Variable (String name) {
		this.name = name;
		this.constantOperations = 0;
		this.notConstantOperations = 0;
	}
	
	public Variable (String name, String typeInstantiated) {
		this.name = name;
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
}
