package br.uff.projetofinal;

import java.io.IOException;
import java.util.List;

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
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import de.vogella.jdt.astsimple.handler.MethodInvocationVisitor;
import de.vogella.jdt.astsimple.handler.MethodVisitor;

public class SearchPatterns extends AbstractHandler
{
    private void searchPatterns()
    {
    	
    	//TESTE
        
    	IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    	
    
    	ITextSelection textSelection = null;
    	    	       
    	if (editor instanceof ITextEditor) {
	    	ISelectionProvider selectionProvider = ((ITextEditor)editor).getSelectionProvider();
	    	
	    	ISelection selection = selectionProvider.getSelection();
	
	    	if (selection instanceof ITextSelection) {
		    	textSelection = (ITextSelection)selection;
		    	
		    	System.out.println("offset: " + textSelection.getOffset() + " start line: " + textSelection.getStartLine() + " end line: " + textSelection.getEndLine());
		    	
		  	}
    	
    	}
        
   
    	System.out.println("tooltip: " + editor.getTitleToolTip());
    	
    	String editorTitleToolTip[] = editor.getTitleToolTip().split("/");

    	String nameProject = editorTitleToolTip[0];;
    	String packageTooolTip = editorTitleToolTip[1];
    	String className = editor.getTitle();
    	
    	
    	System.out.println("nameProject: " + nameProject + " Package: " + packageTooolTip + " unit: " + className);
    	
    	//FIM DO TESTE
    	
    	
    	
    	IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject[] projects = root.getProjects();
        
          
        try
        {

            for (IProject project : projects)
            {
            
                if (!project.isOpen() || !project.isNatureEnabled("org.eclipse.jdt.core.javanature"))
                    continue;
                 
   
                IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
                // parse(JavaCore.create(project));
                for (IPackageFragment mypackage : packages)              	
                {
           
                	for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
                		
                		
                		//ICompilationUnit unit = mypackage.getCompilationUnit(className);
                		
                		if (unit.getElementName().equals(className)){
                			
                			CompilationUnit parse = parse(unit);
    	                    MethodVisitor visitor = new MethodVisitor();  
    	                    parse.accept(visitor);
    	                    
    	                    List<MethodDeclaration> methodsDeclarations = visitor.getMethods(); 
    	                    
    	                    for (int i = 0; i < methodsDeclarations.size(); i++)
    	                    {
                                  MethodDeclaration method = methodsDeclarations.get(i);
                                  //System.out.println("nome do metodo: " + method.getName());
                                  
                                  
                                  
                                  MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
                                  Block methodBody = method.getBody();
                                  if (methodBody == null)
                                      continue;
                                  method.getBody().accept(visitor2);
                                 
                                  for (MethodInvocation methodInvocation : visitor2.getMethods())
                                  {
                                	  if(methodInvocation.getStartPosition() > (textSelection.getOffset() - 200)  && methodInvocation.getStartPosition() < textSelection.getOffset()){
                                		  
                                		  String completeMethodInvocation = getCompleteMethodName(methodInvocation.resolveMethodBinding());
                                		  
                                		  LerArvore.SearchNodeInThree(completeMethodInvocation);
                                		  
                                		  System.out.println("Métodos invocados: " + completeMethodInvocation);
                                	  }
                                  }

    	                    }
                		
                		
                		}
                		                              
                              

                	}
                	
                }
            }
        }
        
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    	
    }
    
    private static CompilationUnit parse(ICompilationUnit unit)
    {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(unit);
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null); // parse
    }
    
    private String getParametersType(ITypeBinding[] parametersType) throws IOException
    {
        String parametersTypes = "";
        for (int i = 0; i < parametersType.length; i++)
        {
            if (i != 0)
                parametersTypes += ", ";
            parametersTypes += parametersType[i].getBinaryName();
        }

        return parametersTypes;
    }
    
    private String getMethodSignature(IMethodBinding methodBinding) throws IOException
    {
        methodBinding.getDeclaringClass().getBinaryName();

        String methodName = methodBinding.getName();

        String parametersTypes = getParametersType(methodBinding.getParameterTypes());

        return methodName + "(" + parametersTypes + ")";
    }
    
    
    private String getCompleteMethodName(IMethodBinding methodBinding) throws IOException
    {
        methodBinding.getDeclaringClass().getBinaryName();

        String methodNameSpace = methodBinding.getDeclaringClass().getPackage().getName() + "." + methodBinding.getDeclaringClass().getName() + "." + methodBinding.getName();

        String parametersTypes = getParametersType(methodBinding.getParameterTypes());

        return methodNameSpace + "(" + parametersTypes + ")";
    }
    
    

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		searchPatterns();
	
		return null;
	}

}
