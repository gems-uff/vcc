package br.uff.projetofinal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class LerArvore {
	
	public static void SearchNodeInThree (String completeMethodInvocation) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(
					"C:\\ProjetoFinal\\arvore.obj"));
			
			
			MethodCallNode node = (MethodCallNode) ois.readObject();
			MethodCallNode child = node.getMethodChildren().get(completeMethodInvocation);

			HashMap<String, MethodCallNode> childNodes = child.getMethodChildren();
			 
			System.out.println("Dicas:");
			System.out.println("Geralmente usuários que chamam o método: " + completeMethodInvocation + " também chamam logo em seguida: ");
			
			for (Iterator it = childNodes.keySet().iterator(); it.hasNext();) {
				
				child = childNodes.get(it.next());
				
				if(child != null){

					System.out.println(child.getMethodSignature()+ "  com suporte de " + child.getConfidences()[0] + "% e confiança de " + child.getConfidences()[child.getConfidences().length - 1] + "%") ;
					
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
