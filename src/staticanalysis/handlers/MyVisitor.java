package staticanalysis.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.text.Document;

public class MyVisitor extends ASTVisitor {
	List<MethodInvocation> methodsInvo = new ArrayList<MethodInvocation>();
	List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
	private Document doc;
	
	public MyVisitor(Document doc) {
		this.doc = doc;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.fragments().get(0);
		IVariableBinding binding = fragment.resolveBinding();
		
		searchVariable(binding);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.fragments().get(0);
		IVariableBinding binding = fragment.resolveBinding();
		
		searchVariable(binding);
		return super.visit(node);
	}
	
	private void searchVariable(IVariableBinding binding) {
		SearchEngine se = new SearchEngine();
		System.out.println("Field");
		SearchPattern pattern = SearchPattern.createPattern(binding.getJavaElement(),
				IJavaSearchConstants.REFERENCES);
		IJavaSearchScope scope = SearchEngine
				.createWorkspaceScope();
		MySearchRequestor requestor = new MySearchRequestor(doc);
		try {
			se.search(pattern, new SearchParticipant[] { SearchEngine
					.getDefaultSearchParticipant() }, scope, requestor,
					null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
