package br.uff.projetofinal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class LerArvore {
	
	public static void searchNodeInTree (String completeMethodInvocation) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(
					"C:\\ProjetoFinal\\arvore.obj"));
			
			
			MethodCallNode node = (MethodCallNode) ois.readObject();
			MethodCallNode child = node.getMethodChildren().get(completeMethodInvocation);

			if(child == null)
			    return;
			
			HashMap<String, MethodCallNode> childNodes = child.getMethodChildren();
			
			if(childNodes.size() == 0)
			    return;
			
			System.out.println("\n\nDicas:");
			System.out.println("Geralmente usuários que chamam o método: " + completeMethodInvocation + " também chamam logo em seguida: ");
			
			for (Iterator it = childNodes.keySet().iterator(); it.hasNext();) {
			    printSuggestion(childNodes.get(it.next()));
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

    private static void printSuggestion(MethodCallNode method)
    {
        HashMap<String, MethodCallNode> childNodes = method.getMethodChildren();
        if(childNodes.size() == 0)
            System.out.println(method.getMethodSignature()+ "  com suporte de " + method.getConfidences()[0] + "% e confiança de " + method.getConfidences()[method.getConfidences().length - 1] + "%\n") ;
        else
        {
            for (Iterator it = childNodes.keySet().iterator(); it.hasNext();) {
                System.out.println(method.getMethodSignature());
                printSuggestion(childNodes.get(it.next()));
            }
        }
    }
}
