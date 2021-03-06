package staticanalysis.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspace.ProjectOrder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.text.Document;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the root of the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		System.out.println("Number of projects: " + projects.length);
		// Loop over all projects
		for (IProject project : projects) {
			try {
				PrintProjectInfo(project);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		Result.getInstance().writeCSV();
		
		return null;
	}

	private void PrintProjectInfo(IProject project) throws CoreException,
			JavaModelException {
		System.out.println("Working in project " + project.getName());
		// check if we have a Java project
		IJavaProject javaProject = JavaCore.create(project);
		printPackageInfos(javaProject, project.getName());
	}

	private void printPackageInfos(IJavaProject javaProject, String string)
			throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			// Package fragments include all packages in the
			// classpath
			// We will only look at the package from the source
			// folder
			// K_BINARY would include also included JARS, e.g.
			// rt.jar
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				createAST(mypackage, javaProject.getElementName());
				iCompilationUnitInfo(mypackage, javaProject.getElementName());
			}
		}
	}

	private void iCompilationUnitInfo(IPackageFragment mypackage, String projectName)
			throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			iMethods(unit, projectName);
		}
	}

	private void iMethods(ICompilationUnit unit, String projectName) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		Document doc = new Document(unit.getSource());
		for (IType type : allTypes) {
			try {
				iMethodDetails(type, doc, projectName);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void iMethodDetails(IType type, Document doc, String projectName) throws CoreException {
		IMethod[] methods = type.getMethods();

		for (IMethod method : methods) {
			for (ILocalVariable ilocalVariable : method.getParameters()) {
				Variable variable = createVariable(ilocalVariable, doc, projectName);
				if (variable != null) {
					SearchEngine se = new SearchEngine();
					SearchPattern pattern = SearchPattern.createPattern(ilocalVariable,
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
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Variable createVariable(ILocalVariable ilocalVariable, Document doc, String projectName) {
		Variable variable = null;
		try {
			if (ilocalVariable.getSource().contains("ArrayList")) {
				variable = new Variable(ilocalVariable.getElementName(), "parameter", projectName, "ArrayList");
			} else if (ilocalVariable.getSource().contains("LinkedList")) {
				variable = new Variable(ilocalVariable.getElementName(), "parameter", projectName, "LinkedList");
			}
			
			if (ilocalVariable.getSource().contains("synchronizedList")) {
				variable.setSync(true);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return variable;
	}

	private void createAST(IPackageFragment mypackage, String projectName)
			throws JavaModelException {

		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			// now create the AST for the ICompilationUnits
			CompilationUnit parse = parse(unit);
			Document doc = new Document(unit.getSource());
			MyVisitor visitor = new MyVisitor(doc, projectName);
			parse.accept(visitor);
		}
	}

	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
