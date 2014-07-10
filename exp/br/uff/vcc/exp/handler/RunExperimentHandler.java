package br.uff.vcc.exp.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import br.uff.vcc.exp.git.RepositoryNode;

public class RunExperimentHandler extends AbstractHandler {

	private void sl4j() {

		Long timeIni = System.currentTimeMillis();
		
		RepositoryNode r= new RepositoryNode("sl4j", "C:\\Desenvolvimento\\repositorios\\slf4j\\");
		try {
			r.Parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - timeIni);
	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		sl4j();

		return null;

	}
}