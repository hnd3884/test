package java.awt.dnd;

import java.util.EventObject;

public class DropTargetEvent extends EventObject
{
    private static final long serialVersionUID = 2821229066521922993L;
    protected DropTargetContext context;
    
    public DropTargetEvent(final DropTargetContext context) {
        super(context.getDropTarget());
        this.context = context;
    }
    
    public DropTargetContext getDropTargetContext() {
        return this.context;
    }
}
