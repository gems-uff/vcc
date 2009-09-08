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
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import de.vogella.jdt.astsimple.handler.MethodInvocationVisitor;
import de.vogella.jdt.astsimple.handler.MethodVisitor;

public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		BufferedWriter out = null;

		try 
		{
			out = new BufferedWriter(new FileWriter("c:\\projetoFinal.txt"));
			
			for (IProject project : projects) 
			{
				if (!project.isNatureEnabled("org.eclipse.jdt.core.javanature")) 
					continue;
				
				IPackageFragment[] packages = JavaCore.create(project)
						.getPackageFragments();
				// parse(JavaCore.create(project));
				for (IPackageFragment mypackage : packages)
				{	
					if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE)
					{
						for (ICompilationUnit unit : mypackage.getCompilationUnits()) 
						{
							// Now create the AST for the ICompilationUnits
							CompilationUnit parse = parse(unit);
							MethodVisitor visitor = new MethodVisitor();
							parse.accept(visitor);
							
							for (MethodDeclaration method : visitor.getMethods())
							{
								out.write("Declaração do método: ");
								printMethod(method.resolveBinding(), out);
								
    							MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
								method.getBody().accept(visitor2);
								out.write("Métodos invocados: \n");
								for (MethodInvocation methodInvocation : visitor2.getMethods()) 
									printMethod(methodInvocation.resolveMethodBinding(), out);
									
								out.write("\n");
							}
						}
					}
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (CoreException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				out.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	private void printParametersType(ITypeBinding[] parametersType, BufferedWriter out) throws IOException 
	{
		for (int i = 0; i < parametersType.length; i++) {
		if(i != 0)
			out.write(", ");
			out.write(parametersType[i].getBinaryName());
		}
	}

	/**
	 * Imprime a declaração completa da chamada de um método
	 * @param methodInvocation
	 * @param out
	 * @throws IOException
	 */
	private void printMethod(IMethodBinding methodBinding, BufferedWriter out) throws IOException
	{
		methodBinding.getDeclaringClass().getBinaryName();
		
		out.write(methodBinding.getDeclaringClass().getPackage().getName() + "." + methodBinding.getDeclaringClass().getName() + "." + methodBinding.getName() + "(");
		printParametersType(methodBinding.getParameterTypes(), out);
		out.write(")\n");
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
