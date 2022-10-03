package java.awt.dnd;

public class DragSourceDropEvent extends DragSourceEvent
{
    private static final long serialVersionUID = -5571321229470821891L;
    private boolean dropSuccess;
    private int dropAction;
    
    public DragSourceDropEvent(final DragSourceContext dragSourceContext, final int dropAction, final boolean dropSuccess) {
        super(dragSourceContext);
        this.dropAction = 0;
        this.dropSuccess = dropSuccess;
        this.dropAction = dropAction;
    }
    
    public DragSourceDropEvent(final DragSourceContext dragSourceContext, final int dropAction, final boolean dropSuccess, final int n, final int n2) {
        super(dragSourceContext, n, n2);
        this.dropAction = 0;
        this.dropSuccess = dropSuccess;
        this.dropAction = dropAction;
    }
    
    public DragSourceDropEvent(final DragSourceContext dragSourceContext) {
        super(dragSourceContext);
        this.dropAction = 0;
        this.dropSuccess = false;
    }
    
    public boolean getDropSuccess() {
        return this.dropSuccess;
    }
    
    public int getDropAction() {
        return this.dropAction;
    }
}
