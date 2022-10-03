package javax.swing.undo;

import java.util.Hashtable;

public interface StateEditable
{
    public static final String RCSID = "$Id: StateEditable.java,v 1.2 1997/09/08 19:39:08 marklin Exp $";
    
    void storeState(final Hashtable<Object, Object> p0);
    
    void restoreState(final Hashtable<?, ?> p0);
}
