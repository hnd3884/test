package javax.swing.undo;

import javax.swing.UIManager;
import java.io.Serializable;

public class AbstractUndoableEdit implements UndoableEdit, Serializable
{
    protected static final String UndoName = "Undo";
    protected static final String RedoName = "Redo";
    boolean hasBeenDone;
    boolean alive;
    
    public AbstractUndoableEdit() {
        this.hasBeenDone = true;
        this.alive = true;
    }
    
    @Override
    public void die() {
        this.alive = false;
    }
    
    @Override
    public void undo() throws CannotUndoException {
        if (!this.canUndo()) {
            throw new CannotUndoException();
        }
        this.hasBeenDone = false;
    }
    
    @Override
    public boolean canUndo() {
        return this.alive && this.hasBeenDone;
    }
    
    @Override
    public void redo() throws CannotRedoException {
        if (!this.canRedo()) {
            throw new CannotRedoException();
        }
        this.hasBeenDone = true;
    }
    
    @Override
    public boolean canRedo() {
        return this.alive && !this.hasBeenDone;
    }
    
    @Override
    public boolean addEdit(final UndoableEdit undoableEdit) {
        return false;
    }
    
    @Override
    public boolean replaceEdit(final UndoableEdit undoableEdit) {
        return false;
    }
    
    @Override
    public boolean isSignificant() {
        return true;
    }
    
    @Override
    public String getPresentationName() {
        return "";
    }
    
    @Override
    public String getUndoPresentationName() {
        final String presentationName = this.getPresentationName();
        String s;
        if (!"".equals(presentationName)) {
            s = UIManager.getString("AbstractUndoableEdit.undoText") + " " + presentationName;
        }
        else {
            s = UIManager.getString("AbstractUndoableEdit.undoText");
        }
        return s;
    }
    
    @Override
    public String getRedoPresentationName() {
        final String presentationName = this.getPresentationName();
        String s;
        if (!"".equals(presentationName)) {
            s = UIManager.getString("AbstractUndoableEdit.redoText") + " " + presentationName;
        }
        else {
            s = UIManager.getString("AbstractUndoableEdit.redoText");
        }
        return s;
    }
    
    @Override
    public String toString() {
        return super.toString() + " hasBeenDone: " + this.hasBeenDone + " alive: " + this.alive;
    }
}
