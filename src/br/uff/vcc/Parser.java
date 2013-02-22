package br.uff.vcc;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.uff.vcc.plugin.visitors.MethodInvocationVisitor;
import br.uff.vcc.plugin.visitors.MethodVisitor;


public class Parser extends AbstractHandler
{
    private void parseMethod(String projectName, String packageName, String className, String methodSignature)
    {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get specified project in the workspace
        IProject project = root.getProject(projectName);
        IPackageFragmentRoot iPackage = JavaCore.create(project).getPackageFragmentRoot(packageName);
        IPackageFragment iPackageFragment = iPackage.getPackageFragment(packageName);
        ICompilationUnit comp = iPackageFragment.getCompilationUnit(className);
        CompilationUnit parse = parse(comp);
        MethodVisitor visitor = new MethodVisitor();

        parse.accept(visitor);

        try
        {
            for (MethodDeclaration method : visitor.getMethods())
            {
                if (getMethodSignature(method.resolveBinding()).equals(methodSignature))
                {
                    MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
                    Block methodBody = method.getBody();
                    if (methodBody == null)
                        break;
                    method.getBody().accept(visitor2);
                }
                
                System.out.println("nome do Metodo: " + method.getName());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getMethodSignature(IMethodBinding methodBinding) throws IOException
    {
        methodBinding.getDeclaringClass().getBinaryName();

        String methodName = methodBinding.getName();

        String parametersTypes = getParametersType(methodBinding.getParameterTypes());

        return methodName + "(" + parametersTypes + ")";
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

    /**
     * Reads a ICompilationUnit and creates the AST DOM for manipulating the
     * Java source file
     * 
     * @param unit
     * @return
     */

    private static CompilationUnit parse(ICompilationUnit unit)
    {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(unit);
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null); // parse
    }

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        parseMethod("Venda", "src", "Principal.java", "fecharCompra");
        return null;
    }
}
