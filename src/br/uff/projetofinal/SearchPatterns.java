package br.uff.projetofinal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import br.uff.projetofinal.util.ComparableList;
import br.uff.projetofinal.util.Suggestion;
import de.vogella.jdt.astsimple.handler.MethodInvocationVisitor;
import de.vogella.jdt.astsimple.handler.MethodVisitor;

public class SearchPatterns extends AbstractHandler
{
    private static Integer maxSizeCombinations = 4;

    private void searchPatterns()
    {
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

        ObjectInputStream ois;
        try
        {
            ois = new ObjectInputStream(new FileInputStream("C:\\ProjetoFinal\\arvore.obj"));

            MethodCallNode rootNode = (MethodCallNode) ois.readObject();

            ComparableList<String> methodNames = readMethodNames(editor);

            ArrayList<ComparableList<String>> combinations = new ArrayList<ComparableList<String>>();
            for (int i = 1; i <= Math.min(maxSizeCombinations, Math.min(methodNames.size(), rootNode.getMaxTreeDepth() - 1)); i++)
            {
                combinations.addAll(generateCombinations(methodNames, i));
            }

            Long initialTime = System.currentTimeMillis();

            Collections.sort(combinations);

            ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();

            for (int i = 0; i < combinations.size(); i++)
            {
                Collection<Suggestion> methodSug = LerArvore.searchNodeInTree(combinations.get(i), rootNode);
                if (methodSug != null)
                    suggestions.addAll(methodSug);
                else
                    poda(combinations, combinations.get(i), i + 1);
            }

            //Collections.sort(suggestions);
            System.out.println("Total de sugestões: " + suggestions.size());

            System.out.println("Tempo total: " + (System.currentTimeMillis() - initialTime) / 1000 + " segundos");

            printResults(suggestions);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void printResults(ArrayList<Suggestion> suggestions)
    {
        for (Iterator<Suggestion> iterator = suggestions.iterator(); iterator.hasNext();)
        {
            Suggestion suggestion = (Suggestion) iterator.next();
            System.out.println("Usuários que chamam os métodos: ");
            Collection<String> invocatedMethods = suggestion.getInvocatedMethods();
            for (Iterator<String> iterator2 = invocatedMethods.iterator(); iterator2.hasNext();)
            {
                System.out.print(iterator2.next() + " , ");
            }
            System.out.println("\nTambém chamam em seguida: ");
            Collection<String> methods = suggestion.getSuggestedMethods();
            for (Iterator<String> iterator2 = methods.iterator(); iterator2.hasNext();)
            {
                System.out.print(iterator2.next() + " , ");
            }
            System.out.println("\nSuporte: " + suggestion.getSupport() + " Confianca: " + suggestion.getConfidence() + "\n\n");

        }
        
    }

    private ComparableList<String> readMethodNames(IEditorPart editor)
    {
        int textSelectionOffset = getTextSelectionOffset(editor);

        String editorTitleToolTip[] = editor.getTitleToolTip().split("/");
        String nameProject = editorTitleToolTip[0];
        String packageTooolTip = editorTitleToolTip[1];
        String className = editor.getTitle();

        System.out.println("nameProject: " + nameProject + " Package: " + packageTooolTip + " unit: " + className);

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject[] projects = root.getProjects();

        ComparableList<String> methodNames = new ComparableList<String>();

        try
        {
            for (IProject project : projects)
            {

                if (!project.isOpen() || !project.isNatureEnabled("org.eclipse.jdt.core.javanature") || !project.getName().equals(nameProject))
                    continue;

                IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
                for (IPackageFragment mypackage : packages)
                {
                    for (ICompilationUnit unit : mypackage.getCompilationUnits())
                    {
                        if (unit.getElementName().equals(className))
                        {

                            CompilationUnit parse = parse(unit);
                            MethodVisitor visitor = new MethodVisitor();
                            parse.accept(visitor);

                            List<MethodDeclaration> methodsDeclarations = visitor.getMethods();

                            for (int i = 0; i < methodsDeclarations.size(); i++)
                            {
                                if (i < methodsDeclarations.size() - 1 && methodsDeclarations.get(i + 1).getStartPosition() < textSelectionOffset)
                                    continue;

                                MethodDeclaration method = methodsDeclarations.get(i);

                                MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
                                Block methodBody = method.getBody();
                                if (methodBody == null)
                                    break;
                                ;
                                method.getBody().accept(visitor2);

                                for (MethodInvocation methodInvocation : visitor2.getMethods())
                                {
                                    if (methodInvocation.getStartPosition() < textSelectionOffset)
                                        methodNames.add(getCompleteMethodName(methodInvocation.resolveMethodBinding()));
                                    else
                                        break;
                                }
                                break;

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

        return methodNames;
    }

    private int getTextSelectionOffset(IEditorPart editor)
    {
        ITextSelection textSelection = null;

        if (editor instanceof ITextEditor)
        {
            ISelectionProvider selectionProvider = ((ITextEditor) editor).getSelectionProvider();

            ISelection selection = selectionProvider.getSelection();

            if (selection instanceof ITextSelection)
            {
                textSelection = (ITextSelection) selection;

                System.out.println("offset: " + textSelection.getOffset() + " start line: " + textSelection.getStartLine() + " end line: " + textSelection.getEndLine());
                return textSelection.getOffset();
            }
        }

        return 0;
    }

    private void poda(ArrayList<ComparableList<String>> combinations, ComparableList<String> nonFrequencyList, int initialIndex)
    {
        for (int i = initialIndex; i < combinations.size(); i++)
        {
            if (combinations.get(i).containsAll(nonFrequencyList))
                combinations.remove(i);
        }
    }

    private Collection<ComparableList<String>> generateCombinations(ComparableList<String> methodNames, int size)
    {
        Collection<ComparableList<String>> combinations = new ArrayList<ComparableList<String>>();

        if (methodNames.size() == size)
        {
            combinations.add(methodNames);
            return combinations;
        }

        if (size == 1)
        {
            for (int i = 0; i < methodNames.size(); i++)
            {
                ComparableList<String> combination = new ComparableList<String>();
                combination.add(methodNames.get(i));
                combinations.add(combination);
            }
            return combinations;
        }

        for (int i = 0; i <= methodNames.size() - size; i++)
        {
            ComparableList<String> methodsClone = (ComparableList<String>) methodNames.clone();
            methodsClone.removeAll(methodNames.subList(0, i + 1));
            Collection<ComparableList<String>> elementCombinations = generateCombinations(methodsClone, size - 1);
            for (Iterator<ComparableList<String>> iterator = elementCombinations.iterator(); iterator.hasNext();)
            {
                iterator.next().add(0, methodNames.get(i));
            }
            combinations.addAll(elementCombinations);
        }

        return combinations;
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
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        searchPatterns();

        return null;
    }

}
