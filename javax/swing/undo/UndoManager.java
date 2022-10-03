package javax.swing.undo;

import javax.swing.event.UndoableEditEvent;
import javax.swing.UIManager;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.event.UndoableEditListener;

public class UndoManager extends CompoundEdit implements UndoableEditListener
{
    int indexOfNextAdd;
    int limit;
    
    public UndoManager() {
        this.indexOfNextAdd = 0;
        this.limit = 100;
        this.edits.ensureCapacity(this.limit);
    }
    
    public synchronized int getLimit() {
        return this.limit;
    }
    
    public synchronized void discardAllEdits() {
        final Iterator<UndoableEdit> iterator = this.edits.iterator();
        while (iterator.hasNext()) {
            iterator.next().die();
        }
        this.edits = new Vector<UndoableEdit>();
        this.indexOfNextAdd = 0;
    }
    
    protected void trimForLimit() {
        if (this.limit >= 0) {
            final int size = this.edits.size();
            if (size > this.limit) {
                final int n = this.limit / 2;
                int n2 = this.indexOfNextAdd - 1 - n;
                int n3 = this.indexOfNextAdd - 1 + n;
                if (n3 - n2 + 1 > this.limit) {
                    ++n2;
                }
                if (n2 < 0) {
                    n3 -= n2;
                    n2 = 0;
                }
                if (n3 >= size) {
                    final int n4 = size - n3 - 1;
                    n3 += n4;
                    n2 += n4;
                }
                this.trimEdits(n3 + 1, size - 1);
                this.trimEdits(0, n2 - 1);
            }
        }
    }
    
    protected void trimEdits(final int i, final int n) {
        if (i <= n) {
            for (int n2 = n; i <= n2; --n2) {
                this.edits.elementAt(n2).die();
                this.edits.removeElementAt(n2);
            }
            if (this.indexOfNextAdd > n) {
                this.indexOfNextAdd -= n - i + 1;
            }
            else if (this.indexOfNextAdd >= i) {
                this.indexOfNextAdd = i;
            }
        }
    }
    
    public synchronized void setLimit(final int limit) {
        if (!this.inProgress) {
            throw new RuntimeException("Attempt to call UndoManager.setLimit() after UndoManager.end() has been called");
        }
        this.limit = limit;
        this.trimForLimit();
    }
    
    protected UndoableEdit editToBeUndone() {
        int i = this.indexOfNextAdd;
        while (i > 0) {
            final UndoableEdit undoableEdit = this.edits.elementAt(--i);
            if (undoableEdit.isSignificant()) {
                return undoableEdit;
            }
        }
        return null;
    }
    
    protected UndoableEdit editToBeRedone() {
        final int size = this.edits.size();
        int i = this.indexOfNextAdd;
        while (i < size) {
            final UndoableEdit undoableEdit = this.edits.elementAt(i++);
            if (undoableEdit.isSignificant()) {
                return undoableEdit;
            }
        }
        return null;
    }
    
    protected void undoTo(final UndoableEdit undoableEdit) throws CannotUndoException {
        UndoableEdit undoableEdit2;
        for (int i = 0; i == 0; i = ((undoableEdit2 == undoableEdit) ? 1 : 0)) {
            final Vector<UndoableEdit> edits = this.edits;
            final int indexOfNextAdd = this.indexOfNextAdd - 1;
            this.indexOfNextAdd = indexOfNextAdd;
            undoableEdit2 = edits.elementAt(indexOfNextAdd);
            undoableEdit2.undo();
        }
    }
    
    protected void redoTo(final UndoableEdit undoableEdit) throws CannotRedoException {
        UndoableEdit undoableEdit2;
        for (int i = 0; i == 0; i = ((undoableEdit2 == undoableEdit) ? 1 : 0)) {
            undoableEdit2 = this.edits.elementAt(this.indexOfNextAdd++);
            undoableEdit2.redo();
        }
    }
    
    public synchronized void undoOrRedo() throws CannotRedoException, CannotUndoException {
        if (this.indexOfNextAdd == this.edits.size()) {
            this.undo();
        }
        else {
            this.redo();
        }
    }
    
    public synchronized boolean canUndoOrRedo() {
        if (this.indexOfNextAdd == this.edits.size()) {
            return this.canUndo();
        }
        return this.canRedo();
    }
    
    @Override
    public synchronized void undo() throws CannotUndoException {
        if (this.inProgress) {
            final UndoableEdit editToBeUndone = this.editToBeUndone();
            if (editToBeUndone == null) {
                throw new CannotUndoException();
            }
            this.undoTo(editToBeUndone);
        }
        else {
            super.undo();
        }
    }
    
    @Override
    public synchronized boolean canUndo() {
        if (this.inProgress) {
            final UndoableEdit editToBeUndone = this.editToBeUndone();
            return editToBeUndone != null && editToBeUndone.canUndo();
        }
        return super.canUndo();
    }
    
    @Override
    public synchronized void redo() throws CannotRedoException {
        if (this.inProgress) {
            final UndoableEdit editToBeRedone = this.editToBeRedone();
            if (editToBeRedone == null) {
                throw new CannotRedoException();
            }
            this.redoTo(editToBeRedone);
        }
        else {
            super.redo();
        }
    }
    
    @Override
    public synchronized boolean canRedo() {
        if (this.inProgress) {
            final UndoableEdit editToBeRedone = this.editToBeRedone();
            return editToBeRedone != null && editToBeRedone.canRedo();
        }
        return super.canRedo();
    }
    
    @Override
    public synchronized boolean addEdit(final UndoableEdit undoableEdit) {
        this.trimEdits(this.indexOfNextAdd, this.edits.size() - 1);
        boolean addEdit = super.addEdit(undoableEdit);
        if (this.inProgress) {
            addEdit = true;
        }
        this.indexOfNextAdd = this.edits.size();
        this.trimForLimit();
        return addEdit;
    }
    
    @Override
    public synchronized void end() {
        super.end();
        this.trimEdits(this.indexOfNextAdd, this.edits.size() - 1);
    }
    
    public synchronized String getUndoOrRedoPresentationName() {
        if (this.indexOfNextAdd == this.edits.size()) {
            return this.getUndoPresentationName();
        }
        return this.getRedoPresentationName();
    }
    
    @Override
    public synchronized String getUndoPresentationName() {
        if (!this.inProgress) {
            return super.getUndoPresentationName();
        }
        if (this.canUndo()) {
            return this.editToBeUndone().getUndoPresentationName();
        }
        return UIManager.getString("AbstractUndoableEdit.undoText");
    }
    
    @Override
    public synchronized String getRedoPresentationName() {
        if (!this.inProgress) {
            return super.getRedoPresentationName();
        }
        if (this.canRedo()) {
            return this.editToBeRedone().getRedoPresentationName();
        }
        return UIManager.getString("AbstractUndoableEdit.redoText");
    }
    
    @Override
    public void undoableEditHappened(final UndoableEditEvent undoableEditEvent) {
        this.addEdit(undoableEditEvent.getEdit());
    }
    
    @Override
    public String toString() {
        return super.toString() + " limit: " + this.limit + " indexOfNextAdd: " + this.indexOfNextAdd;
    }
}
