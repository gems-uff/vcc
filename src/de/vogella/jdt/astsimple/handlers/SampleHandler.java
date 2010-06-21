package de.vogella.jdt.astsimple.handlers;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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

import br.uff.projetofinal.MethodCallNode;

import de.vogella.jdt.astsimple.handler.MethodInvocationVisitor;
import de.vogella.jdt.astsimple.handler.MethodVisitor;

public class SampleHandler extends AbstractHandler
{
    String                     nameSpaceComplete;

    String                     nameParameter;

    HashMap<String, Integer>   hash                      = new HashMap<String, Integer>();

    HashMap<Integer, String>   invertedHash              = new HashMap<Integer, String>();

    int                        idMetodo                  = 1;

    public static final String namesFilePath             = "c:\\ProjetoFinal\\projetoFinal.txt";

    public static final String mapFilePath               = "c:\\ProjetoFinal\\plwapcode\\test.data";

    public static final String miningResultFilePath      = "c:\\ProjetoFinal\\plwapcode\\result_plwap.data";

    public static final String miningResultNamesFilePath = "c:\\ProjetoFinal\\resultado.txt";

    public static final String nameToHashFilePath        = "c:\\ProjetoFinal\\nameToHash.txt";

    private MethodCallNode     rootNode;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
    	
    	
    	parseProjects();

        double suporteMinimo = 0.003;

        try
        {
            Process gsp = Runtime.getRuntime().exec("C:\\ProjetoFinal\\plwapcode\\plwap.exe " + suporteMinimo, null, new File("C:\\ProjetoFinal\\plwapcode\\"));
            System.out.println("Entrou-------------");
            //TODO sleep para que o algoritmo em C termine a sua execução. Deve ser alterado pra verificar se a execução terminou.

            System.out.println(Calendar.getInstance().getTime());
            gsp.waitFor();
            System.out.println(Calendar.getInstance().getTime());

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        readMiningResult();
        
        try
        {
            FileOutputStream fos = new FileOutputStream("arvore.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(rootNode);
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try
        {
            BufferedWriter treeWriter = new BufferedWriter(new FileWriter("C:\\ProjetoFinal\\arvore.txt"));
            writeTree(rootNode, treeWriter);
            treeWriter.close();
            
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\ProjetoFinal\\arvore.obj"));
            oos.writeObject(rootNode);
            oos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

    private void writeTree(MethodCallNode node, BufferedWriter writer) throws IOException
    {
        for (int i = 0; i < node.getDepth(); i++)
            writer.write("\t");
        writer.write(node.getMethodSignature() + "\t");
        for (int i = 0; i < node.getConfidences().length; i++)
        {
            writer.write(node.getConfidences()[i] + ",");
        }
        writer.write("\n");
        
        for (Iterator<MethodCallNode> iterator = node.getMethodChildren().values().iterator(); iterator.hasNext();)
        {
            MethodCallNode child = (MethodCallNode) iterator.next();
            writeTree(child, writer);
        }
    }

    private void readMiningResult()
    {
        BufferedReader in = null;
        BufferedWriter result = null;
        rootNode = new MethodCallNode(null, null, null);

        try
        {
            in = new BufferedReader(new FileReader(miningResultFilePath));
            result = new BufferedWriter(new FileWriter(miningResultNamesFilePath));
            String str;
            result.write("Sequências frequentemente chamadas: ");

            while (in.ready())
            {
                str = in.readLine();
                String[] ids = str.split(";");
                
                MethodCallNode parentNode = rootNode;
                if(ids.length - 1 > rootNode.getMaxTreeDepth())
                    rootNode.setMaxTreeDepth(ids.length - 1);

                for (int i = 0; i < ids.length - 1; i++)
                {
                    if (i != 0)
                        result.write(" , ");
                    String methodSignature = invertedHash.get(Integer.parseInt(ids[i]));
                    result.write(methodSignature);
                    if (i != ids.length - 2)
                        parentNode = parentNode.getMethodChildren().get(methodSignature);
                    else
                    {
                        double support = Double.parseDouble(ids[i + 1]);
                        parentNode.addChild(createMethodCallNode(methodSignature, parentNode, support));
                    }
                }
                result.write("\n");
            }
            in.close();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                in.close();
                result.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private MethodCallNode createMethodCallNode(String methodSignature, MethodCallNode parentNode, double support)
    {
        double[] confidences = new double[parentNode.getDepth() + 1];
        int index = confidences.length - 1;
        MethodCallNode actualNode = parentNode;
        while (actualNode != null)
        {
            confidences[index--] = support / actualNode.getSupport();
            actualNode = actualNode.getParentNode();
        }

        return new MethodCallNode(methodSignature, confidences, parentNode);
    }

    private void parseProjects()
    {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject[] projects = root.getProjects();

        BufferedWriter out = null;
        BufferedWriter saidaMap = null;
        BufferedWriter nameToHash = null;

        try
        {
            out = new BufferedWriter(new FileWriter(namesFilePath));

            saidaMap = new BufferedWriter(new FileWriter(mapFilePath));

            nameToHash = new BufferedWriter(new FileWriter(nameToHashFilePath));

            int userId = 1;

            for (IProject project : projects)
            {
            
                if (!project.isOpen() || !project.isNatureEnabled("org.eclipse.jdt.core.javanature"))
                    continue;

                IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
                // parse(JavaCore.create(project));
                for (IPackageFragment mypackage : packages)
                {
                
                    for (ICompilationUnit unit : mypackage.getCompilationUnits())
                    {
                    	
                        // Now create the AST for the ICompilationUnits
                        CompilationUnit parse = parse(unit);
                        MethodVisitor visitor = new MethodVisitor();
                        
                        parse.accept(visitor);
                        
                    	                       
                        List<MethodDeclaration> methodsDeclarations = visitor.getMethods(); 
                        for (int i = 0; i < methodsDeclarations.size(); i++)
                        {
                            MethodDeclaration method = methodsDeclarations.get(i);
                            out.write(userId + " - Declaração do método: ");
                            
                            String methodName = getCompleteMethodName(method.resolveBinding());
                            out.write(methodName + "\n");

                            MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
                            Block methodBody = method.getBody();
                            if (methodBody == null)
                                continue;
                            method.getBody().accept(visitor2);
                            out.write("Métodos invocados: \n");

                            //imprime o numero da transação
                            saidaMap.write(userId + " ");
                            userId++;

                            // imprime a quantidade de metodos chamados, ou seja, a quantidade de sequencias
                            saidaMap.write((visitor2.getMethods().size()) + " ");

                            for (MethodInvocation methodInvocation : visitor2.getMethods())
                            {
                                String completeMethodInvocation = getCompleteMethodName(methodInvocation.resolveMethodBinding());
                                     
                                out.write(completeMethodInvocation + "\n");

                                if (!(hash.containsKey(completeMethodInvocation)))
                                {
                                    nameToHash.write(idMetodo + " - " + completeMethodInvocation + "\n");
                                    hash.put(completeMethodInvocation, ++idMetodo);
                                    invertedHash.put(idMetodo, completeMethodInvocation);
                                }

                                saidaMap.write(hash.get(completeMethodInvocation) + " ");
                            }
                            if(i < methodsDeclarations.size() - 1)
                                saidaMap.write("\n");

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
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
                saidaMap.close();
                nameToHash.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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
     * Retorna a declaração completa da chamada de um método
     * @param methodInvocation
     * @param out
     * @throws IOException
     */
    private String getCompleteMethodName(IMethodBinding methodBinding) throws IOException
    {
        methodBinding.getDeclaringClass().getBinaryName();

        String methodNameSpace = methodBinding.getDeclaringClass().getPackage().getName() + "." + methodBinding.getDeclaringClass().getName() + "." + methodBinding.getName();

        String parametersTypes = getParametersType(methodBinding.getParameterTypes());

        return methodNameSpace + "(" + parametersTypes + ")";
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
}
