package br.uff.vcc.plugin.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import br.uff.vcc.entity.MethodCallNode;
import br.uff.vcc.plugin.visitors.IfStatementVisitor;
import br.uff.vcc.plugin.visitors.MethodInvocationVisitor;
import br.uff.vcc.plugin.visitors.MethodVisitor;
import br.uff.vcc.plugin.visitors.SwitchStatementVisitor;
import br.uff.vcc.plugin.visitors.TryCatchStatementVisitor;
import br.uff.vcc.plugin.visitors.entity.CustomSwitchStatement;
import br.uff.vcc.plugin.visitors.entity.SplittedCaseStatement;
import br.uff.vcc.util.PropertiesUtil;

public class GenerateTreeHandler extends AbstractHandler {
	
	private int idMethod;
	private double minimumSupport;
	
	private HashMap<String, Integer> hash;
	private HashMap<Integer, String> invertedHash;
	
	private BufferedWriter out;
	private BufferedWriter nameToHash;
	
	public static final String METHOD_LIBRARY_FILES_PATH = "c:\\VCC\\methodLibrary.txt";
	public static final String TEST_DATA_FILE_PATH = "c:\\VCC\\plwapcode\\test.data";
	public static final String RESULT_DATA_FILE_PATH = "c:\\VCC\\plwapcode\\result_plwap.data";
	public static final String RESULT_NAMES_FILE_PATH = "c:\\VCC\\result.txt";
	public static final String NAME_TO_HASH_FILE_PATH = "c:\\VCC\\nameToHash.txt";

	private MethodCallNode rootNode;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		hash = new HashMap<String, Integer>();
		invertedHash = new HashMap<Integer, String>();
		idMethod = 0;
		
		showDialogWait();
		
		parseProjects();
		
		minimumSupport = PropertiesUtil.readMinimumSupport();
		
		try {
			Process gsp = Runtime.getRuntime().exec(
					"C:\\VCC\\plwapcode\\plwap.exe " + minimumSupport,
					null, new File("C:\\VCC\\plwapcode\\"));
			System.out.println("Minerando padrões frequentes.");

			System.out.println(Calendar.getInstance().getTime());
			gsp.waitFor();
			System.out.println(Calendar.getInstance().getTime());
			System.out.println("Mineração finalizada.");

		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		readMiningResult();

		ObjectOutputStream oos = null;
		try {
			FileOutputStream fos = new FileOutputStream("arvore.ser");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(rootNode);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally{
			if(oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			BufferedWriter treeWriter = new BufferedWriter(new FileWriter(
					"C:\\VCC\\arvore.txt"));
			writeTree(rootNode, treeWriter);
			treeWriter.close();

			oos = new ObjectOutputStream(
					new FileOutputStream("C:\\VCC\\arvore.obj"));
			oos.writeObject(rootNode);			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Árvore de padrões gerada com sucesso.");
		return null;
	}

	private void writeTree(MethodCallNode node, BufferedWriter writer)
			throws IOException {
		for (int i = 0; i < node.getDepth(); i++)
			writer.write("\t");
		writer.write(node.getMethodSignature() + "\t");
		for (int i = 0; i < node.getConfidences().length; i++) {
			writer.write(node.getConfidences()[i] + ",");
		}
		writer.write("\n");

		for (Iterator<MethodCallNode> iterator = node.getMethodChildren()
				.values().iterator(); iterator.hasNext();) {
			MethodCallNode child = (MethodCallNode) iterator.next();
			writeTree(child, writer);
		}
	}

	private void readMiningResult() {
		BufferedReader in = null;
		BufferedWriter result = null;
		rootNode = new MethodCallNode(null, null, null);
		
		try {
			in = new BufferedReader(new FileReader(RESULT_DATA_FILE_PATH));
			result = new BufferedWriter(new FileWriter(
					RESULT_NAMES_FILE_PATH));
			String str;
			result.write("Sequências frequentemente chamadas: ");

			while (in.ready()) {
				str = in.readLine();
				String[] ids = str.split(";");

				MethodCallNode parentNode = rootNode;
				if (ids.length - 1 > rootNode.getMaxTreeDepth())
					rootNode.setMaxTreeDepth(ids.length - 1);

				for (int i = 0; i < ids.length - 1; i++) {
					if (i != 0)
						result.write(" , ");
					String methodSignature = invertedHash.get(Integer
							.parseInt(ids[i]));
					result.write(methodSignature);
					if (i != ids.length - 2)
						parentNode = parentNode.getMethodChildren().get(
								methodSignature);
					else {
						double support = Double.parseDouble(ids[i + 1]);
						parentNode.addChild(createMethodCallNode(
								methodSignature, parentNode, support));
					}
				}
				result.write("\n");
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				result.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private MethodCallNode createMethodCallNode(String methodSignature,
			MethodCallNode parentNode, double support) {
		double[] confidences = new double[parentNode.getDepth() + 1];
		int index = confidences.length - 1;
		MethodCallNode actualNode = parentNode;
		while (actualNode != null) {
			confidences[index--] = support / actualNode.getSupport();
			actualNode = actualNode.getParentNode();
		}

		return new MethodCallNode(methodSignature, confidences, parentNode);
	}

	private void parseProjects() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();

		BufferedWriter saidaMap = null;

		try {
			out = new BufferedWriter(new FileWriter(METHOD_LIBRARY_FILES_PATH));

			saidaMap = new BufferedWriter(new FileWriter(TEST_DATA_FILE_PATH));

			nameToHash = new BufferedWriter(new FileWriter(NAME_TO_HASH_FILE_PATH));

			int methodDeclarationId = 1;
			int transactionId = 1;

			for (IProject project : projects) {

				if (!project.isOpen() //|| !project.getName().equals("tomcat-7")
						|| !project
								.isNatureEnabled("org.eclipse.jdt.core.javanature"))
					continue;

				IPackageFragment[] packages = JavaCore.create(project)
						.getPackageFragments();
				// parse(JavaCore.create(project));
				for (IPackageFragment mypackage : packages) {

					for (ICompilationUnit unit : mypackage
							.getCompilationUnits()) {

						// Now create the AST for the ICompilationUnits
						CompilationUnit parse = parse(unit);
						MethodVisitor visitor = new MethodVisitor();

						parse.accept(visitor);

						List<MethodDeclaration> methodsDeclarations = visitor
								.getMethods();
						for (int i = 0; i < methodsDeclarations.size(); i++) {
							MethodDeclaration method = methodsDeclarations
									.get(i);
							out.write(methodDeclarationId + " - Declaração do método: ");

							String methodName = getCompleteMethodName(method
									.resolveBinding());
							out.write(methodName + "\n");

							Block methodBody = method.getBody();
							
							if (methodBody == null || emptyMethodBody(methodBody))
								continue;
							
							out.write("Métodos invocados: \n");
							List<List<Integer>> methodsList = new ArrayList<List<Integer>>();
							methodsList.add(new ArrayList<Integer>());
							visitMethodInvocations(methodBody, methodsList, 0);
							
							for (int j = 0; j < methodsList.size(); j++) {
								List<Integer> transaction = methodsList.get(j);
								// imprime o numero da transação
								saidaMap.write((transactionId++) + " ");
								// imprime o identificador do metodo
								saidaMap.write(methodDeclarationId + " ");
								// imprime o tamanho da transação
								saidaMap.write(transaction.size() + " ");
								for (Integer methodID : transaction) {
									saidaMap.write(methodID + " ");
								}
								
								saidaMap.write("\n");
							}
							methodDeclarationId++;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				saidaMap.close();
				nameToHash.flush();
				nameToHash.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean emptyMethodBody(Block methodBody) {
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();
		methodBody.accept(methodInvocationVisitor);
		List<MethodInvocation> methodInvocations = methodInvocationVisitor.getMethods();
		return (methodInvocations.size() == 0);
	}

	/**
	 * this method is responsible for parse a method body, identifying different conditional invocations paths.
	 * @param statement
	 * @param methodsList
	 * @param methodListIndex
	 * @throws IOException
	 */
	private int visitMethodInvocations(Statement statement, List<List<Integer>> methodsList, int methodListIndex) throws IOException {
		
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();
		statement.accept(methodInvocationVisitor);
		List<MethodInvocation> methodInvocations = methodInvocationVisitor.getMethods();
		
		List<IfStatement> ifStatements = readRelevantIfStatements(statement);
		List<TryStatement> tryStatements = readRelevantTryStatements(statement);
		List<CustomSwitchStatement> switchStatements = readRelevantSwitchStatements(statement);
		
		
		int indIfStatements = 0;
		IfStatement nextIfStatement = null;
		if(ifStatements.size() > 0){
			nextIfStatement = ifStatements.get(indIfStatements);
		}
		
		int indTryStatements = 0;
		TryStatement nextTryStatement = null;
		if(tryStatements.size() > 0){
			nextTryStatement = tryStatements.get(indTryStatements);
		}
		
		int indSwitchStatements = 0;
		CustomSwitchStatement nextSwitchStatement = null;
		if(switchStatements.size() > 0){
			nextSwitchStatement = switchStatements.get(indSwitchStatements);
		}
		
		int parsedMethods = 0;
		
		for (int i = 0; i < methodInvocations.size(); i++) {
			MethodInvocation methodInvocation = methodInvocations.get(i);
			Integer methodId = getMethodCallId(methodInvocation);
			if((nextIfStatement == null || (methodInvocation.getStartPosition() < nextIfStatement.getThenStatement().getStartPosition()))
				&& (nextTryStatement == null || (methodInvocation.getStartPosition() < nextTryStatement.getStartPosition()))
				&& (nextSwitchStatement == null || (methodInvocation.getStartPosition() < nextSwitchStatement.getSwitchStatement().getStartPosition()))){
				for (int j = methodListIndex; j < methodsList.size(); j++) {
					List<Integer> list = methodsList.get(j);
					list.add(methodId);
				}
			} else{
				i--;
				if(nextTryStatement != null && 
						(nextIfStatement == null || nextTryStatement.getStartPosition() < nextIfStatement.getStartPosition()) &&
						(nextSwitchStatement == null || nextTryStatement.getStartPosition() < nextSwitchStatement.getSwitchStatement().getStartPosition())){
					parsedMethods = visitMethodInvocations(nextTryStatement.getBody(), methodsList, methodListIndex);
					
					List<List<Integer>> latestCatchesMethodsList = new ArrayList<List<Integer>>();
					for (int k = methodListIndex; k < methodsList.size(); k++){
						//cloning object
						List<Integer> methodListAux = new ArrayList<Integer>();
						methodListAux.addAll(methodsList.get(k));
						
						latestCatchesMethodsList.add(methodListAux);
					}
					
					List<CatchClause> catchClauses = nextTryStatement.catchClauses();
					
					//visit first catch and then branch a new path for each other catch
					parsedMethods = parsedMethods + visitMethodInvocations(catchClauses.get(0).getBody(), methodsList, methodListIndex);
					
					for(int j = 1; j < catchClauses.size(); j++){
					
						//cloning latestCatchesMethodList
						List<List<Integer>> cloneLatestCatchesMethodsList = new ArrayList<List<Integer>>();
						for (int k = 0; k < latestCatchesMethodsList.size(); k++){
							//cloning object
							List<Integer> methodListAux = new ArrayList<Integer>();
							methodListAux.addAll(latestCatchesMethodsList.get(k));
							
							cloneLatestCatchesMethodsList.add(methodListAux);
						}
						
						methodsList.addAll(cloneLatestCatchesMethodsList);
						parsedMethods = parsedMethods + visitMethodInvocations(catchClauses.get(j).getBody(), methodsList, methodsList.size() - cloneLatestCatchesMethodsList.size());
					}
					if(nextTryStatement.getFinally() != null){
						parsedMethods = parsedMethods + visitMethodInvocations(nextTryStatement.getFinally(), methodsList, methodListIndex);
					}
					
					if(++indTryStatements < tryStatements.size()){
						nextTryStatement = tryStatements.get(indTryStatements);
					}else{
						nextTryStatement = null;
					}
				}else if(nextIfStatement != null && 
						(nextSwitchStatement == null || nextIfStatement.getStartPosition() < nextSwitchStatement.getSwitchStatement().getStartPosition())){
					List<List<Integer>> elseMethodsList = new ArrayList<List<Integer>>();
					for (int j = methodListIndex; j < methodsList.size(); j++){
						//cloning object
						List<Integer> methodListAux = new ArrayList<Integer>();
						methodListAux.addAll(methodsList.get(j));
						
						elseMethodsList.add(methodListAux);
					}
					parsedMethods = visitMethodInvocations(nextIfStatement.getThenStatement(), methodsList, methodListIndex);
					methodsList.addAll(elseMethodsList);
					parsedMethods = parsedMethods + visitMethodInvocations(nextIfStatement.getElseStatement(), methodsList, methodsList.size() - elseMethodsList.size());
					
					if(++indIfStatements < ifStatements.size()){
						nextIfStatement = ifStatements.get(indIfStatements);
					}else{
						nextIfStatement = null;
					}
				}else{
					
					List<List<Integer>> latestCasesMethodsList = new ArrayList<List<Integer>>();
					for (int k = methodListIndex; k < methodsList.size(); k++){
						//cloning object
						List<Integer> methodListAux = new ArrayList<Integer>();
						methodListAux.addAll(methodsList.get(k));
						
						latestCasesMethodsList.add(methodListAux);
					}
					
					List<SplittedCaseStatement> caseStatements = nextSwitchStatement.getCaseStatements();
					
					//visit first statements and then branch a new path for each other case
					
					for (Statement caseStatement : caseStatements.get(0).getStatements()) {
						parsedMethods = parsedMethods + visitMethodInvocations(caseStatement, methodsList, methodListIndex);
					}
					
					for(int j = 1; j < caseStatements.size(); j++){
						//cloning latestCatchesMethodList
						List<List<Integer>> cloneLatestCaseMethodsList = new ArrayList<List<Integer>>();
						for (int k = 0; k < latestCasesMethodsList.size(); k++){
							//cloning object
							List<Integer> methodListAux = new ArrayList<Integer>();
							methodListAux.addAll(latestCasesMethodsList.get(k));
							
							cloneLatestCaseMethodsList.add(methodListAux);
						}
						
						methodsList.addAll(cloneLatestCaseMethodsList);
						for (Statement caseStatement : caseStatements.get(j).getStatements()) {
							parsedMethods = parsedMethods + visitMethodInvocations(caseStatement, methodsList, methodsList.size() - cloneLatestCaseMethodsList.size());
						}
					}
					
					if(++indSwitchStatements < switchStatements.size()){
						nextSwitchStatement = switchStatements.get(indSwitchStatements);
					}else{
						nextSwitchStatement = null;
					}
				}
				
				
				
				//checking how many methods were parsed inside the if statement
				i = i + parsedMethods;
				
				if(i + 1 < methodInvocations.size()){
					//Advancing if-else statements index according to what was already parsed
					for(int j = indIfStatements; j < ifStatements.size(); j++){
						IfStatement ifStatement = ifStatements.get(j);
						MethodInvocation nextMethodInvocation = methodInvocations.get(i+1);
						if(ifStatement != null && (nextMethodInvocation.getStartPosition() > ifStatement.getThenStatement().getStartPosition())){
							methodInvocationVisitor = new MethodInvocationVisitor();
							ifStatement.accept(methodInvocationVisitor);
							List<MethodInvocation> statementMethods = methodInvocationVisitor.getMethods();
							if(statementMethods.size() > 0 && statementMethods.get(0).getStartPosition() == nextMethodInvocation.getStartPosition()){
								break;
							}
							if(++indIfStatements < ifStatements.size()){
								nextIfStatement = ifStatements.get(indIfStatements);
							}else{
								nextIfStatement = null;
							}
						}else{
							break;
						}
					}
					
					//Advancing try-catch statements index according to what was already parsed
					for(int j = indTryStatements; j < tryStatements.size(); j++){
						TryStatement tryStatement = tryStatements.get(j);
						MethodInvocation nextMethodInvocation = methodInvocations.get(i+1);
						if(tryStatement != null && (nextMethodInvocation.getStartPosition() > tryStatement.getBody().getStartPosition())){
							methodInvocationVisitor = new MethodInvocationVisitor();
							tryStatement.accept(methodInvocationVisitor);
							List<MethodInvocation> statementMethods = methodInvocationVisitor.getMethods();
							if(statementMethods.size() > 0 && statementMethods.get(0).getStartPosition() == nextMethodInvocation.getStartPosition()){
								break;
							}
							if(++indTryStatements < tryStatements.size()){
								nextTryStatement = tryStatements.get(indTryStatements);
							}else{
								nextTryStatement = null;
							}
						}else{
							break;
						}
					}
					
					
					//Advancing switch-case statements index according to what was already parsed
					for(int j = indSwitchStatements; j < switchStatements.size(); j++){
						CustomSwitchStatement switchStatement = switchStatements.get(j);
						MethodInvocation nextMethodInvocation = methodInvocations.get(i+1);
						if(switchStatement != null && (nextMethodInvocation.getStartPosition() > switchStatement.getSwitchStatement().getStartPosition())){
							methodInvocationVisitor = new MethodInvocationVisitor();
							switchStatement.getSwitchStatement().accept(methodInvocationVisitor);
							List<MethodInvocation> statementMethods = methodInvocationVisitor.getMethods();
							if(statementMethods.size() > 0 && statementMethods.get(0).getStartPosition() == nextMethodInvocation.getStartPosition()){
								break;
							}
							if(++indSwitchStatements < switchStatements.size()){
								nextSwitchStatement = switchStatements.get(indSwitchStatements);
							}else{
								nextSwitchStatement = null;
							}
						}else{
							break;
						}
					}
				}
			}
		}
		
		return methodInvocations.size();
		
	}

	private List<CustomSwitchStatement> readRelevantSwitchStatements(Statement statement) {
		SwitchStatementVisitor switchCaseStatementVisitor = new SwitchStatementVisitor();
		statement.accept(switchCaseStatementVisitor);
		List<SwitchStatement> switchStatements = switchCaseStatementVisitor.getStatements();
		List<CustomSwitchStatement> relevantSwitchStatements = new ArrayList<CustomSwitchStatement>();
		for (SwitchStatement switchStatement : switchStatements) {
			CustomSwitchStatement customSwitchStatement = new CustomSwitchStatement();
			SplittedCaseStatement caseStatement = new SplittedCaseStatement();
			
			List statements = switchStatement.statements();
			for (Object s : statements) {
				if(s instanceof BreakStatement){
					customSwitchStatement.getCaseStatements().add(caseStatement);
					caseStatement = new SplittedCaseStatement();
				}else{
					caseStatement.getStatements().add((Statement)s);
				}
			}
			customSwitchStatement.getCaseStatements().add(caseStatement);
			
			if(customSwitchStatement.getCaseStatements().size() > 1){
				customSwitchStatement.setSwitchStatement(switchStatement);
				relevantSwitchStatements.add(customSwitchStatement);
			}
		}
		
		return relevantSwitchStatements;
	}

	private List<IfStatement> readRelevantIfStatements(Statement statement) {
		IfStatementVisitor ifStatementVisitor = new IfStatementVisitor();
		statement.accept(ifStatementVisitor);
		List<IfStatement> ifStatements = ifStatementVisitor.getStatements();
		List<IfStatement> relevantiIfStatements = new ArrayList<IfStatement>();
		for (IfStatement ifStatement : ifStatements) {
			if(ifStatement.getElseStatement() != null){
				relevantiIfStatements.add(ifStatement);
			}
		}
		
		return relevantiIfStatements;
	}

	private List<TryStatement> readRelevantTryStatements(Statement statement) {
		TryCatchStatementVisitor tryStatementVisitor = new TryCatchStatementVisitor();
		statement.accept(tryStatementVisitor);
		List<TryStatement> tryStatements = tryStatementVisitor.getStatements();
		List<TryStatement> relevantTryStatements = new ArrayList<TryStatement>();
		for (TryStatement tryStatement : tryStatements) {
			List<CatchClause> clauses = tryStatement.catchClauses();
			if(clauses != null && clauses.size() > 1){
				relevantTryStatements.add(tryStatement);
			}
		}
		
		return relevantTryStatements;
	}

	private Integer getMethodCallId(MethodInvocation methodInvocation)
			throws IOException {
		String completeMethodInvocation = getCompleteMethodName(methodInvocation
				.resolveMethodBinding());
		
		out.write(completeMethodInvocation + "\n");

		if (!(hash.containsKey(completeMethodInvocation))) {
			hash.put(completeMethodInvocation, ++idMethod);
			nameToHash.write(idMethod + " - " + completeMethodInvocation + "\n");
			invertedHash.put(idMethod, completeMethodInvocation);
		}

		Integer methodId = hash.get(completeMethodInvocation);
		return methodId;
	}

	private String getParametersType(ITypeBinding[] parametersType)
			throws IOException {
		String parametersTypes = "";
		for (int i = 0; i < parametersType.length; i++) {
			if (i != 0)
				parametersTypes += ", ";
			parametersTypes += parametersType[i].getBinaryName();
		}

		return parametersTypes;
	}

	/**
	 * returns the complete method name
	 * 
	 * @param methodInvocation
	 * @param out
	 * @throws IOException
	 */
	private String getCompleteMethodName(IMethodBinding methodBinding)
			throws IOException {
		methodBinding.getDeclaringClass().getBinaryName();

		String methodNameSpace = methodBinding.getDeclaringClass().getPackage()
				.getName()
				+ "."
				+ methodBinding.getDeclaringClass().getName()
				+ "."
				+ methodBinding.getName();

		String parametersTypes = getParametersType(methodBinding
				.getParameterTypes());

		return methodNameSpace + "(" + parametersTypes + ")";
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
	
	private void showDialogWait(){
		Display display = Display.getDefault();

		final Shell shell = new Shell(display);
		shell.setText("Generating Tree");
		shell.setLayout(new FillLayout());
		shell.setSize(1000, 400);
		shell.addListener(SWT.Close, new Listener () {
	        public void handleEvent (Event event) {
//	            int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
//	            MessageBox messageBox = new MessageBox (shell, style);
//	            messageBox.setText ("Information");
//	            messageBox.setMessage ("Close the shell?");
//	            event.doit = messageBox.open () == SWT.YES;
	        	event.doit = true;
	        }
	    });
		
		MessageBox dialog = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.NONE);
		dialog.setText("Generating Tree");
		dialog.setMessage("Generating Tree... Please Wait.");
		dialog.open(); 
		
	    shell.pack();
	    shell.open();
//	    while (!shell.isDisposed ()) {
//	        if (!display.readAndDispatch ()) display.sleep ();
//	    }
//	    display.dispose ();
	}
}
