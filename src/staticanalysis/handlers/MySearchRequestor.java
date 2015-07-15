package staticanalysis.handlers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class MySearchRequestor extends SearchRequestor {
	
	private Document doc;
	private Variable variable;
	
	public MySearchRequestor(Document doc, Variable variable) {
		this.doc = doc;
		this.variable = variable;
	}
	
	@Override
	public void beginReporting() {
		super.beginReporting();
	}
	
	@Override
	public void endReporting() {
		super.endReporting();
	}
	
	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		
		try {
			int line = doc.getLineOfOffset(match.getOffset());
			int length = doc.getLineLength(line);
			String codeLine = doc.get(match.getOffset(), length);
			checkType(codeLine);
			checkMethod(codeLine);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkMethod(String codeLine) {
		if (variable.getTypeInstantiated() != null) {
			if (variable.getTypeInstantiated().equals("ArrayList")) {
				if (codeLine.contains(".size(") || codeLine.contains(".isEmpty(") || codeLine.contains(".get(") || codeLine.contains(".set(") || codeLine.contains(".iterator(") || codeLine.contains(".listIterator(")) {
					variable.incrementConstantOperations();
				} else if (codeLine.contains(".add(") || codeLine.contains(".remove(")) {
					variable.incrementNotConstantOperations();
				}
			} else if (variable.getTypeInstantiated().equals("LinkedList")) {
				if (codeLine.contains(".add(") || codeLine.contains(".remove(") ||  codeLine.contains(".iterator(") || codeLine.contains(".listIterator(")) {
					variable.incrementConstantOperations();
				} else if (codeLine.contains(".get(") || codeLine.contains(".remove(")) {
					variable.incrementNotConstantOperations();
				}
			}
		}
	}

	private void checkType(String codeLine) {
		if (variable.getTypeInstantiated() == null) {
			if (codeLine.contains("new ArrayList")) {
				variable.setTypeInstantiated("ArrayList");
			} else if (codeLine.contains("new LinkedList")) {
				variable.setTypeInstantiated("LinkedList");
			}
		}
	}
}
