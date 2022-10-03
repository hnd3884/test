package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

public class StateEdit extends AbstractUndoableEdit
{
    protected static final String RCSID = "$Id: StateEdit.java,v 1.6 1997/10/01 20:05:51 sandipc Exp $";
    protected StateEditable object;
    protected Hashtable<Object, Object> preState;
    protected Hashtable<Object, Object> postState;
    protected String undoRedoName;
    
    public StateEdit(final StateEditable stateEditable) {
        this.init(stateEditable, null);
    }
    
    public StateEdit(final StateEditable stateEditable, final String s) {
        this.init(stateEditable, s);
    }
    
    protected void init(final StateEditable object, final String undoRedoName) {
        this.object = object;
        this.preState = new Hashtable<Object, Object>(11);
        this.object.storeState(this.preState);
        this.postState = null;
        this.undoRedoName = undoRedoName;
    }
    
    public void end() {
        this.postState = new Hashtable<Object, Object>(11);
        this.object.storeState(this.postState);
        this.removeRedundantState();
    }
    
    @Override
    public void undo() {
        super.undo();
        this.object.restoreState(this.preState);
    }
    
    @Override
    public void redo() {
        super.redo();
        this.object.restoreState(this.postState);
    }
    
    @Override
    public String getPresentationName() {
        return this.undoRedoName;
    }
    
    protected void removeRedundantState() {
        final Vector vector = new Vector();
        final Enumeration<Object> keys = this.preState.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            if (this.postState.containsKey(nextElement) && this.postState.get(nextElement).equals(this.preState.get(nextElement))) {
                vector.addElement(nextElement);
            }
        }
        for (int i = vector.size() - 1; i >= 0; --i) {
            final Object element = vector.elementAt(i);
            this.preState.remove(element);
            this.postState.remove(element);
        }
    }
}
