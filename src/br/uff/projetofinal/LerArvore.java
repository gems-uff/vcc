package br.uff.projetofinal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import br.uff.projetofinal.util.Suggestion;

public class LerArvore
{
    private static Collection<Suggestion> suggestions      = new ArrayList<Suggestion>();

    private static ArrayList<String>      actualSuggestion = new ArrayList<String>();

    private static List<String>           methods;

    public static Collection<Suggestion> searchNodeInTree(List<String> methods)
    {
        ObjectInputStream ois;
        suggestions = new ArrayList<Suggestion>();
        LerArvore.methods = methods;

        try
        {
            ois = new ObjectInputStream(new FileInputStream("C:\\ProjetoFinal\\arvore.obj"));

            MethodCallNode node = (MethodCallNode) ois.readObject();

            for (String methodName : methods)
            {
                node = node.getMethodChildren().get(methodName);
                if (node == null)
                    return null;
            }

            HashMap<String, MethodCallNode> childNodes = node.getMethodChildren();

            if (childNodes.size() == 0)
                return null;

            //System.out.println("\n\nDicas:");
            //System.out.print("Geralmente usuários que chamam o método: ");
            for (Iterator<String> iterator = methods.iterator(); iterator.hasNext();)
            {
                String methodName = iterator.next();
                //System.out.println(methodName + ",");
            }
            //System.out.println(" também chamam logo em seguida: ");

            for (Iterator<String> it = childNodes.keySet().iterator(); it.hasNext();)
            {
                readSuggestion(childNodes.get(it.next()));
            }

            ois.close();

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
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return suggestions;
    }

    private static void readSuggestion(MethodCallNode method)
    {
        actualSuggestion.add(method.getMethodSignature());
        
        Suggestion suggestion = new Suggestion(methods, (Collection<String>) actualSuggestion.clone(), method.getSupport(), method.getConfidences()[method.getConfidences().length - 1]);
        suggestions.add(suggestion);

        HashMap<String, MethodCallNode> childNodes = method.getMethodChildren();
        if (childNodes.size() != 0)
            for (Iterator<String> it = childNodes.keySet().iterator(); it.hasNext();)
                readSuggestion(childNodes.get(it.next()));
        
        actualSuggestion.remove(method.getMethodSignature());
    }
}
