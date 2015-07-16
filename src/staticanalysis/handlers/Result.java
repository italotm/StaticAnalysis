package staticanalysis.handlers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class Result {
	
	private List<Variable> list;
	
	private static Result instance = null;
    private Result() {
    	list = new LinkedList<Variable>();
    }

    public static synchronized Result getInstance() {
        if (instance == null) {
            instance = new Result();
        }

        return instance;
    }
    
    public void addVariable(Variable variable) {
    	list.add(variable);
    }
    
    public void writeCSV() {
    	PrintWriter writer;
		try {
			writer = new PrintWriter("results.csv", "UTF-8");
			writer.println("variable,type,implementation,const,n-const,Sistema");
			for (Variable variable : list) {
				writer.println(variable.getName()+","+variable.getType()+","+variable.getTypeInstantiated()+","+variable.getConstantOperations()+","+variable.getNotConstantOperations()+","+variable.getProjectName());
			}
	    	writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
    }
    
    @Override
    public String toString() {
    	String result = "";
    	for (Variable variable : list) {
			result += "Name: " + variable.getName() + " Tipo: " + variable.getType() + " Const: " + variable.getConstantOperations() + " N-Const: " + variable.getNotConstantOperations() + " Project: " + variable.getProjectName();
		}
    	return result;
    }
}
