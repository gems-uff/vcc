package de.vogella.jdt.astsimple.handlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import de.vogella.jdt.astsimple.handler.MethodInvocationVisitor;
import de.vogella.jdt.astsimple.handler.MethodVisitor;

public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("projetoFinal.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Loop over all projects
		for (IProject project : projects) {
			try {
				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {

					IPackageFragment[] packages = JavaCore.create(project)
							.getPackageFragments();
					// parse(JavaCore.create(project));
					for (IPackageFragment mypackage : packages) {
						
						if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
							for (ICompilationUnit unit : mypackage
									.getCompilationUnits()) {
								// Now create the AST for the ICompilationUnits
								CompilationUnit parse = parse(unit);
								MethodVisitor visitor = new MethodVisitor();
								parse.accept(visitor);
								
								for (MethodDeclaration method : visitor.getMethods()) 
								{
									//System.out.println("mypackage.toString() " + mypackage.toString());
									System.out.println("Pacote: " + mypackage.getElementName() + " " + "Method name: " + method.getName());
									//System.out.println("Method name: " + method.getName());
        							MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
									method.getBody().accept(visitor2);
									for (MethodInvocation methodInvocation : visitor2.getMethods()) {
										System.out.println("Method invocation name: "  + methodInvocation.getName());
										//System.out.println("Method to string: " + methodInvocation.toString());
										
																
								    try {
										out.write("Pacote: " + mypackage.getElementName() + " " + "Method name: " + method.getName() + "\n");
										out.newLine();
										out.write("Method invocation name: "  + methodInvocation.getName() + "\n");
										out.newLine();
								    } catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
										
									}
									
								}

							}
						}

					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Reads a ICompilationUnit and creates the AST DOM for manipulating the
	 * Java source file
	 * 
	 * @param unit
	 * @return
	 */

	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
