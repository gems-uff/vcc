package br.uff.vcc.plugin.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import br.uff.vcc.LerArvore;
import br.uff.vcc.SortTreeListener;
import br.uff.vcc.entity.MethodCallNode;
import br.uff.vcc.plugin.visitors.MethodInvocationVisitor;
import br.uff.vcc.plugin.visitors.MethodVisitor;
import br.uff.vcc.util.ComparableList;
import br.uff.vcc.util.PropertiesUtil;
import br.uff.vcc.util.Suggestion;

public class SearchPatternsHandler extends AbstractHandler {
	private static Integer maxSizeCombinations = PropertiesUtil.readMaxSizeCombinations();
	private static Boolean useLastMethodAllSuggestionQueries = PropertiesUtil.readUseLastMethodAllSuggestionQueries();

	private void searchPatterns() {

		Long timeIni = System.currentTimeMillis();
		IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		ComparableList<String> methodNames = readMethodNames(editor);

		ArrayList<Suggestion> suggestions = searchInTree(methodNames);
		
		System.out.println("Total de sugestões: " + suggestions.size());
		
		System.out.println(System.currentTimeMillis() - timeIni);
		
		printResults(suggestions);
		
	}

	public static ArrayList<Suggestion> searchInTree(ComparableList<String> methodNames) {
		Set<Suggestion> suggestions = new HashSet<Suggestion>();
		
		String lastMethodCall = methodNames.get(methodNames.size()-1);
		
		MethodCallNode rootNode = GenerateTreeHandler.getRootNode();

		//Query single methods before generate combinations in order to avoid generation of combinations with non frequent patterns
		querySingleMethodCalls(methodNames, suggestions, rootNode);
		
		if(useLastMethodAllSuggestionQueries){
			if(!methodNames.contains(lastMethodCall)){
				return new ArrayList<Suggestion>();
			}else{
				List<Suggestion> suggestionList = new ArrayList<Suggestion>(suggestions);
				for (int i = 0; i < suggestionList.size(); i++) {
					List<String> invocatedMethods = suggestionList.get(i).getInvocatedMethods(); 
					if (!lastMethodCall.equals(invocatedMethods.get(invocatedMethods.size()-1))){
						suggestionList.remove(i);
						i--;
					}
				}
				suggestions = new HashSet<Suggestion>(suggestionList);
			}
		}
		
		ArrayList<ComparableList<String>> combinations = new ArrayList<ComparableList<String>>();
		for (int i = 2; i <= Math.min(maxSizeCombinations, Math.min(methodNames.size(), rootNode.getMaxTreeDepth() - 1)); i++) {
			combinations.addAll(generateCombinations(methodNames, i, lastMethodCall));
		}

		Long initialTime = System.currentTimeMillis();

		Collections.sort(combinations);
		
		for (int i = 0; i < combinations.size(); i++) {
			Collection<Suggestion> methodSug = LerArvore.searchNodeInTree(combinations.get(i), rootNode);
			if (methodSug != null)
				suggestions.addAll(methodSug);
			else
				poda(combinations, combinations.get(i), i + 1);
		}
		
		ArrayList<Suggestion> orderedSuggestions =  new ArrayList<Suggestion>(suggestions);
		Collections.sort(orderedSuggestions);
		
		/*if(useLastMethodAllSuggestionQueries){
			for (int i = 0; i < orderedSuggestions.size(); i++) {
				List<String> invocatedMethods = orderedSuggestions.get(i).getInvocatedMethods(); 
				if (!lastMethodCall.equals(invocatedMethods.get(invocatedMethods.size()-1))){
					orderedSuggestions.remove(i);
					i--;
				}
			}
		}*/
		
		System.out.println("Tempo total: " + (System.currentTimeMillis() - initialTime) / 1000 + " segundos");
		
		return orderedSuggestions;
	}

	public static void querySingleMethodCalls(ComparableList<String> methodNames, Set<Suggestion> suggestions, MethodCallNode rootNode) {
		for (int i = 0; i < methodNames.size();i++) {
			String methodName = methodNames.get(i);
			ComparableList<String> singleMethodQuery = new ComparableList<String>();
			singleMethodQuery.add(methodName);
			Collection<Suggestion> methodSug = LerArvore.searchNodeInTree(singleMethodQuery, rootNode);
			if (methodSug != null){
				suggestions.addAll(methodSug);
			}
			else{
				methodNames.remove(i);
				i--;
			}
		}
	}

	private void printResults(ArrayList<Suggestion> suggestions) {

		Display display = Display.getDefault();

		final Shell shell = new Shell(display);
		shell.setText("Suggestions found");
		shell.setLayout(new FillLayout());
		shell.setSize(1000, 400);

		final Tree tree = new Tree(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);

		tree.setHeaderVisible(true);
		tree.addListener(SWT.MouseDoubleClick, new Listener() {
	      public void handleEvent(Event event) {
	        insertMethodCallsIntoEditor(event, tree);
	        shell.close();
	      }
	    });
		
		TreeColumn column1 = new TreeColumn(tree, SWT.NONE);
		column1.setText("Frequently invoked methods");
		column1.setWidth(600);
		column1.setAlignment(SWT.LEFT);

		TreeColumn column2 = new TreeColumn(tree, SWT.NONE);
		column2.setText("Support");
		column2.setWidth(200);
		column2.setAlignment(SWT.CENTER);
		column2.addSelectionListener(new SortTreeListener());
		

		TreeColumn column3 = new TreeColumn(tree, SWT.NONE);
		column3.setText("Confidence");
		column3.setWidth(200);
		column3.setAlignment(SWT.CENTER);
		column3.addSelectionListener(new SortTreeListener());

		for (Iterator<Suggestion> iterator = suggestions.iterator(); iterator.hasNext();) {
			Suggestion suggestion = (Suggestion) iterator.next();
			Collection<String> invocatedMethods = suggestion.getInvocatedMethods();

			StringBuffer nameMethod = new StringBuffer();

			for (Iterator<String> iterator2 = invocatedMethods.iterator(); iterator2.hasNext();) {
				nameMethod.append(iterator2.next() + ", ");
			}
			nameMethod.delete(nameMethod.lastIndexOf(", "), nameMethod.length());

			TreeItem treeItem = new TreeItem(tree, 0);
			treeItem.setText("Users that invoke the method: " + nameMethod.toString() + ", also invoke: ");

			// System.out.println("\nTambém chamam em seguida: ");
			Collection<String> methods = suggestion.getSuggestedMethods();

			StringBuffer sugestion = new StringBuffer();
			for (Iterator<String> iterator2 = methods.iterator(); iterator2
					.hasNext();) {
				sugestion.append(iterator2.next() + "; ");
			}
			sugestion = sugestion.delete(sugestion.lastIndexOf("; "), sugestion.length());

			TreeItem subTreeItem = new TreeItem(treeItem, SWT.NONE);
			subTreeItem.setText(new String[] {
					sugestion.toString(),
					String.valueOf(suggestion.getSupport()),
					String.valueOf(suggestion.getConfidence()) });
			

		}

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}
	
    public void insertMethodCallsIntoEditor(Event event, Tree tree)
    {
        Point point = new Point(event.x, event.y);
        TreeItem selectedItem = tree.getItem(point);
        
        IEditorPart editor = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        
        if (selectedItem != null && (editor instanceof AbstractTextEditor)) {
            ITextEditor textEditor = (ITextEditor) editor;
            IDocumentProvider dp = textEditor.getDocumentProvider();
            IDocument doc = dp.getDocument(textEditor.getEditorInput());
            int offset = getTextSelectionOffset(editor);
            
            String methodCalls = "";
            String tabs = getIdentationSpaces(doc, offset);
            TreeItem parent = selectedItem;
            if(parent.getParentItem()!=null)
                parent = parent.getParentItem();
            
            TreeItem item = parent.getItem(0);
            String methods = item.getText();
            for (String method : methods.split("; "))
            {
                methodCalls += tabs + method + ";\n";
            }

            try {
                doc.replace(offset, 0, "\n" + methodCalls + "\n");
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
    
    private String getIdentationSpaces(IDocument doc, int offset)
    {
        String tabs = "\t";
        try
        {
            int line = doc.getLineOfOffset(offset);
            String lineValue = doc.get(doc.getLineOffset(line), doc.getLineOffset(line+1)-doc.getLineOffset(line));
            Pattern pattern = Pattern.compile("\\s+");
            Matcher m = pattern.matcher(lineValue);
            if(m.find())
                tabs = m.group();
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        
        return tabs;
    }

    private ComparableList<String> readMethodNames(IEditorPart editor) {
		int textSelectionOffset = getTextSelectionOffset(editor);

		String editorTitleToolTip[] = editor.getTitleToolTip().split("/");
		String nameProject = editorTitleToolTip[0];
		String packageTooolTip = editorTitleToolTip[1];
		String className = editor.getTitle();

		System.out.println("nameProject: " + nameProject + " Package: "
				+ packageTooolTip + " unit: " + className);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();

		ComparableList<String> methodNames = new ComparableList<String>();

		try {
			for (IProject project : projects) {

				if (!project.isOpen()
						|| !project
								.isNatureEnabled("org.eclipse.jdt.core.javanature")
						|| !project.getName().equals(nameProject))
					continue;

				IPackageFragment[] packages = JavaCore.create(project)
						.getPackageFragments();
				for (IPackageFragment mypackage : packages) {
					for (ICompilationUnit unit : mypackage
							.getCompilationUnits()) {
						if (unit.getElementName().equals(className)) {

							CompilationUnit parse = parse(unit);
							MethodVisitor visitor = new MethodVisitor();
							parse.accept(visitor);

							List<MethodDeclaration> methodsDeclarations = visitor
									.getMethods();

							for (int i = 0; i < methodsDeclarations.size(); i++) {
								if (i < methodsDeclarations.size() - 1
										&& methodsDeclarations.get(i + 1)
												.getStartPosition() < textSelectionOffset)
									continue;

								MethodDeclaration method = methodsDeclarations
										.get(i);

								MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
								Block methodBody = method.getBody();
								if (methodBody == null)
									break;
								;
								method.getBody().accept(visitor2);

								for (MethodInvocation methodInvocation : visitor2
										.getMethods()) {
									if (methodInvocation.getStartPosition() < textSelectionOffset)
										methodNames
												.add(GenerateTreeHandler.getCompleteMethodName(methodInvocation
														.resolveMethodBinding()));
									else
										break;
								}
								break;

							}
						}
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return methodNames;
	}

	private int getTextSelectionOffset(IEditorPart editor) {
		ITextSelection textSelection = null;

		if (editor instanceof ITextEditor) {
			ISelectionProvider selectionProvider = ((ITextEditor) editor)
					.getSelectionProvider();

			ISelection selection = selectionProvider.getSelection();

			if (selection instanceof ITextSelection) {
				textSelection = (ITextSelection) selection;

				System.out.println("offset: " + textSelection.getOffset()
						+ " start line: " + textSelection.getStartLine()
						+ " end line: " + textSelection.getEndLine());
				return textSelection.getOffset();
			}
		}

		return 0;
	}

	private static void poda(ArrayList<ComparableList<String>> combinations, ComparableList<String> nonFrequencyList, int initialIndex) {
		for (int i = initialIndex; i < combinations.size(); i++) {
			ComparableList<String> combination = combinations.get(i);
			int index = -1;
			for (Iterator<String> iterator = nonFrequencyList.iterator(); iterator.hasNext();) {
				String methodName = (String) iterator.next();
				index = combination.indexOf(methodName);
				if(index == -1){
					break;
				}else{
					combination = combination.subList(index+1, combination.size());
				}
			}
			if(index != -1){
				combinations.remove(i);
				i--;
			}
			
		}
	}

	private static ArrayList<ComparableList<String>> generateCombinations(
			ComparableList<String> methodNames, int size, String lastMethodCall) {
		Set<ComparableList<String>> combinations = new HashSet<ComparableList<String>>();

		if (methodNames.size() == size) {
			combinations.add(methodNames);
			return new ArrayList<ComparableList<String>>(combinations);
		}

		if (size == 1) {
			if(useLastMethodAllSuggestionQueries && lastMethodCall != null){
				//retorna apenas a combinacao com a ultima chamada de metodo
				ComparableList<String> combination = new ComparableList<String>();
				combination.add(methodNames.get(methodNames.size()-1));
				combinations.add(combination);
			}else{
				for (int i = 0; i < methodNames.size(); i++) {
					ComparableList<String> combination = new ComparableList<String>();
					combination.add(methodNames.get(i));
					combinations.add(combination);
				}
			}
			return new ArrayList<ComparableList<String>>(combinations);
		}

		ComparableList<String> otherMethods = (ComparableList<String>) methodNames.clone();
		
		if(useLastMethodAllSuggestionQueries && lastMethodCall != null){
			String fixElement = otherMethods.remove(otherMethods.size()-1);
			
			ArrayList<ComparableList<String>> otherMethodsCombination = generateCombinations(otherMethods, size - 1, null);
			
			for (ComparableList<String> combination : otherMethodsCombination) {
				combination.add(fixElement);
			}
			
			combinations.addAll(otherMethodsCombination);
		}else{
			for(int i = 0; i <= methodNames.size() - size; i++){
	
				String fixElement = otherMethods.remove(0);
				//gera as combinacoes com os demais elementos da lista
				ArrayList<ComparableList<String>> otherMethodsCombination = generateCombinations(otherMethods, size - 1, null);
				
				for (ComparableList<String> combination : otherMethodsCombination) {
						combination.add(0, fixElement);
				}
				
				combinations.addAll(otherMethodsCombination);
			}
			
		}
	/*		for (int i = 0; i < methodNames.size() - size; i++) {
				ComparableList<String> otherMethodsClone = (ComparableList<String>) otherMethods.clone();
				otherMethodsClone.remove(i);
				
				combinations.addAll(generateCombinations(otherMethodsClone, size - 1));
			}
		*/
		

		return new ArrayList<ComparableList<String>>(combinations);
	}

	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}


	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		searchPatterns();

		return null;

	}
}