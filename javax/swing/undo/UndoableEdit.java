package javax.swing.undo;

public interface UndoableEdit
{
    void undo() throws CannotUndoException;
    
    boolean canUndo();
    
    void redo() throws CannotRedoException;
    
    boolean canRedo();
    
    void die();
    
    boolean addEdit(final UndoableEdit p0);
    
    boolean replaceEdit(final UndoableEdit p0);
    
    boolean isSignificant();
    
    String getPresentationName();
    
    String getUndoPresentationName();
    
    String getRedoPresentationName();
}
