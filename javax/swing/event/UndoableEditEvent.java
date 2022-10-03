package javax.swing.event;

import javax.swing.undo.UndoableEdit;
import java.util.EventObject;

public class UndoableEditEvent extends EventObject
{
    private UndoableEdit myEdit;
    
    public UndoableEditEvent(final Object o, final UndoableEdit myEdit) {
        super(o);
        this.myEdit = myEdit;
    }
    
    public UndoableEdit getEdit() {
        return this.myEdit;
    }
}
