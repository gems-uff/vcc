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

    public static Collection<Suggestion> searchNodeInTree(List<String> methods, MethodCallNode rootNode)
    {
        suggestions = new ArrayList<Suggestion>();
        LerArvore.methods = methods;
        
        for (String methodName : methods)
        {
            rootNode = rootNode.getMethodChildren().get(methodName);
            if (rootNode == null)
                return null;
        }

        HashMap<String, MethodCallNode> childNodes = rootNode.getMethodChildren();

        if (childNodes.size() == 0)
            return null;

        for (Iterator<String> it = childNodes.keySet().iterator(); it.hasNext();)
        {
            readSuggestion(childNodes.get(it.next()));
        }

        return suggestions;
    }

    private static void readSuggestion(MethodCallNode method)
    {
        actualSuggestion.add(method.getMethodSignature());

        Suggestion suggestion = new Suggestion(methods, (Collection<String>) actualSuggestion.clone(), method.getSupport(), method.getConfidences()[methods.size()]);
        suggestions.add(suggestion);

        HashMap<String, MethodCallNode> childNodes = method.getMethodChildren();
        if (childNodes.size() != 0)
            for (Iterator<String> it = childNodes.keySet().iterator(); it.hasNext();)
                readSuggestion(childNodes.get(it.next()));

        actualSuggestion.remove(method.getMethodSignature());
    }
}
