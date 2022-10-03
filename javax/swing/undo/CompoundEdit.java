package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;

public class CompoundEdit extends AbstractUndoableEdit
{
    boolean inProgress;
    protected Vector<UndoableEdit> edits;
    
    public CompoundEdit() {
        this.inProgress = true;
        this.edits = new Vector<UndoableEdit>();
    }
    
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        int size = this.edits.size();
        while (size-- > 0) {
            this.edits.elementAt(size).undo();
        }
    }
    
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        final Enumeration<UndoableEdit> elements = this.edits.elements();
        while (elements.hasMoreElements()) {
            elements.nextElement().redo();
        }
    }
    
    protected UndoableEdit lastEdit() {
        final int size = this.edits.size();
        if (size > 0) {
            return this.edits.elementAt(size - 1);
        }
        return null;
    }
    
    @Override
    public void die() {
        for (int i = this.edits.size() - 1; i >= 0; --i) {
            this.edits.elementAt(i).die();
        }
        super.die();
    }
    
    @Override
    public boolean addEdit(final UndoableEdit undoableEdit) {
        if (!this.inProgress) {
            return false;
        }
        final UndoableEdit lastEdit = this.lastEdit();
        if (lastEdit == null) {
            this.edits.addElement(undoableEdit);
        }
        else if (!lastEdit.addEdit(undoableEdit)) {
            if (undoableEdit.replaceEdit(lastEdit)) {
                this.edits.removeElementAt(this.edits.size() - 1);
            }
            this.edits.addElement(undoableEdit);
        }
        return true;
    }
    
    public void end() {
        this.inProgress = false;
    }
    
    @Override
    public boolean canUndo() {
        return !this.isInProgress() && super.canUndo();
    }
    
    @Override
    public boolean canRedo() {
        return !this.isInProgress() && super.canRedo();
    }
    
    public boolean isInProgress() {
        return this.inProgress;
    }
    
    @Override
    public boolean isSignificant() {
        final Enumeration<UndoableEdit> elements = this.edits.elements();
        while (elements.hasMoreElements()) {
            if (elements.nextElement().isSignificant()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getPresentationName() {
        final UndoableEdit lastEdit = this.lastEdit();
        if (lastEdit != null) {
            return lastEdit.getPresentationName();
        }
        return super.getPresentationName();
    }
    
    @Override
    public String getUndoPresentationName() {
        final UndoableEdit lastEdit = this.lastEdit();
        if (lastEdit != null) {
            return lastEdit.getUndoPresentationName();
        }
        return super.getUndoPresentationName();
    }
    
    @Override
    public String getRedoPresentationName() {
        final UndoableEdit lastEdit = this.lastEdit();
        if (lastEdit != null) {
            return lastEdit.getRedoPresentationName();
        }
        return super.getRedoPresentationName();
    }
    
    @Override
    public String toString() {
        return super.toString() + " inProgress: " + this.inProgress + " edits: " + this.edits;
    }
}
