package javax.swing.undo;

import java.util.Enumeration;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.util.Vector;

public class UndoableEditSupport
{
    protected int updateLevel;
    protected CompoundEdit compoundEdit;
    protected Vector<UndoableEditListener> listeners;
    protected Object realSource;
    
    public UndoableEditSupport() {
        this(null);
    }
    
    public UndoableEditSupport(final Object o) {
        this.realSource = ((o == null) ? this : o);
        this.updateLevel = 0;
        this.compoundEdit = null;
        this.listeners = new Vector<UndoableEditListener>();
    }
    
    public synchronized void addUndoableEditListener(final UndoableEditListener undoableEditListener) {
        this.listeners.addElement(undoableEditListener);
    }
    
    public synchronized void removeUndoableEditListener(final UndoableEditListener undoableEditListener) {
        this.listeners.removeElement(undoableEditListener);
    }
    
    public synchronized UndoableEditListener[] getUndoableEditListeners() {
        return this.listeners.toArray(new UndoableEditListener[0]);
    }
    
    protected void _postEdit(final UndoableEdit undoableEdit) {
        final UndoableEditEvent undoableEditEvent = new UndoableEditEvent(this.realSource, undoableEdit);
        final Enumeration elements = ((Vector)this.listeners.clone()).elements();
        while (elements.hasMoreElements()) {
            ((UndoableEditListener)elements.nextElement()).undoableEditHappened(undoableEditEvent);
        }
    }
    
    public synchronized void postEdit(final UndoableEdit undoableEdit) {
        if (this.updateLevel == 0) {
            this._postEdit(undoableEdit);
        }
        else {
            this.compoundEdit.addEdit(undoableEdit);
        }
    }
    
    public int getUpdateLevel() {
        return this.updateLevel;
    }
    
    public synchronized void beginUpdate() {
        if (this.updateLevel == 0) {
            this.compoundEdit = this.createCompoundEdit();
        }
        ++this.updateLevel;
    }
    
    protected CompoundEdit createCompoundEdit() {
        return new CompoundEdit();
    }
    
    public synchronized void endUpdate() {
        --this.updateLevel;
        if (this.updateLevel == 0) {
            this.compoundEdit.end();
            this._postEdit(this.compoundEdit);
            this.compoundEdit = null;
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " updateLevel: " + this.updateLevel + " listeners: " + this.listeners + " compoundEdit: " + this.compoundEdit;
    }
}
