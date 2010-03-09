package br.uff.projetofinal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;

public class LerArvore {
	public static void main(String[] args) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(
					"C:\\ProjetoFinal\\arvore.obj"));
			String teste = "java.lang.Throwable.getMessage()";
			
			MethodCallNode node = (MethodCallNode) ois.readObject();
			MethodCallNode child = node.getMethodChildren().get(teste);
			

			HashMap<String, MethodCallNode> childNodes = child.getMethodChildren();
			 
			for (Iterator it = childNodes.keySet().iterator(); it.hasNext();) {
				
				child = childNodes.get(it.next());
				if(child != null){
					System.out.println(child.getMethodSignature() + "  " + child.getConfidences()[0]);
					
				}

				
			}
			
			
			ois.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
