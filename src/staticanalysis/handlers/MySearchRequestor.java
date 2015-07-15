package staticanalysis.handlers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class MySearchRequestor extends SearchRequestor {

	private int numberOfCalls = 0;
	
	private Document doc;
	
	public MySearchRequestor(Document doc) {
		this.doc = doc;
	}

	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		
		try {
			int line = doc.getLineOfOffset(match.getOffset());
			int length = doc.getLineLength(line);
			String codeLine = doc.get(match.getOffset(), length);
			System.out.println(codeLine);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		numberOfCalls++;
	}

	/**
	 * @return the numberOfCall
	 */
	public int getNumberOfCalls() {
		return numberOfCalls;
	}

}
