package br.uff.vcc.util;

import java.util.ArrayList;

public class ComparableList<E> extends ArrayList<E> implements Comparable<ComparableList<E>>
{
    /**
     * 
     */
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
    
}
