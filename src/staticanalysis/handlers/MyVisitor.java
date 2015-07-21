package staticanalysis.handlers;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.text.Document;

public class MyVisitor extends ASTVisitor {
	
	private Document doc;
	private String projectName;
	
	public MyVisitor(Document doc, String projectName) {
		this.doc = doc;
		this.projectName = projectName;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		try {
			if (isList(node.getType())) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.fragments().get(0);
				IVariableBinding binding = fragment.resolveBinding();
				
				if (binding != null) {
					searchVariable(binding, createVariable(node, binding));
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		try {
			if (isList(node.getType())) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.fragments().get(0);
				IVariableBinding binding = fragment.resolveBinding();
				
				if (binding != null) {
					searchVariable(binding, createVariable(node, binding));
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return super.visit(node);
	}

	private void searchVariable(IVariableBinding binding, Variable variable) {
		SearchEngine se = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern(binding.getJavaElement(),
				IJavaSearchConstants.REFERENCES);
		IJavaSearchScope scope = SearchEngine
				.createWorkspaceScope();
		MySearchRequestor requestor = new MySearchRequestor(doc, variable);
		try {
			se.search(pattern, new SearchParticipant[] { SearchEngine
					.getDefaultSearchParticipant() }, scope, requestor,
					null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	private boolean isList(Type node) {
		if ((node.toString().contains("List")) || (node.toString().contains("ArrayList")) || (node.toString().contains("LinkedList"))) {
			return true;
		} 
		return false;
	}
	
	private Variable createVariable(VariableDeclarationStatement node,
			IVariableBinding binding) {
		Variable variable;
		
		if (node.toString().contains("new ArrayList")) {
			variable = new Variable(binding.getName(), "local variable", projectName, "ArrayList");
		} else if (node.toString().contains("new LinkedList")) {
			variable = new Variable(binding.getName(), "local variable", projectName, "LinkedList");
		} else {
			variable = new Variable(binding.getName(), "local variable", projectName);
		}
		
		if (node.toString().contains("synchronizedList")) {
			variable.setSync(true);
		}
		
		return variable;
	}
	
	private Variable createVariable(FieldDeclaration node,
			IVariableBinding binding) {
		Variable variable;
		
		if (node.toString().contains("new ArrayList")) {
			variable = new Variable(binding.getName(), "field", projectName, "ArrayList");
		} else if (node.toString().contains("new LinkedList")) {
			variable = new Variable(binding.getName(), "field", projectName, "LinkedList");
		} else {
			variable = new Variable(binding.getName(), "field", projectName);
		}
		
		if (node.toString().contains("synchronizedList")) {
			variable.setSync(true);
		}
		
		return variable;
	}
}
