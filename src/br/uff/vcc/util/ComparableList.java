package br.uff.vcc.util;

import java.util.ArrayList;
import java.util.List;

public class ComparableList<E> extends ArrayList<E> implements Comparable<ComparableList<E>>
{
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(ComparableList<E> o)
    {
        if(this.size() > o.size())
            return 1;
        else
            if(this.size() < o.size())
                return -1;
        return 0;
    }
    
    
    
    @Override
    public ComparableList<E> subList(int fromIndex, int toIndex) {
    	// TODO Auto-generated method stub
    	List<E> auxList = super.subList(fromIndex, toIndex);
    	ComparableList<E> subList = new ComparableList<E>();
    	subList.addAll(auxList);
    	
    	return subList;
    }
    
}
