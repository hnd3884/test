package java.awt.dnd;

public class DragSourceDragEvent extends DragSourceEvent
{
    private static final long serialVersionUID = 481346297933902471L;
    private static final int JDK_1_3_MODIFIERS = 63;
    private static final int JDK_1_4_MODIFIERS = 16320;
    private int targetActions;
    private int dropAction;
    private int gestureModifiers;
    private boolean invalidModifiers;
    
    public DragSourceDragEvent(final DragSourceContext dragSourceContext, final int dropAction, final int targetActions, final int gestureModifiers) {
        super(dragSourceContext);
        this.targetActions = 0;
        this.dropAction = 0;
        this.gestureModifiers = 0;
        this.targetActions = targetActions;
        this.gestureModifiers = gestureModifiers;
        this.dropAction = dropAction;
        if ((gestureModifiers & 0xFFFFC000) != 0x0) {
            this.invalidModifiers = true;
        }
        else if (this.getGestureModifiers() != 0 && this.getGestureModifiersEx() == 0) {
            this.setNewModifiers();
        }
        else if (this.getGestureModifiers() == 0 && this.getGestureModifiersEx() != 0) {
            this.setOldModifiers();
        }
        else {
            this.invalidModifiers = true;
        }
    }
    
    public DragSourceDragEvent(final DragSourceContext dragSourceContext, final int dropAction, final int targetActions, final int gestureModifiers, final int n, final int n2) {
        super(dragSourceContext, n, n2);
        this.targetActions = 0;
        this.dropAction = 0;
        this.gestureModifiers = 0;
        this.targetActions = targetActions;
        this.gestureModifiers = gestureModifiers;
        this.dropAction = dropAction;
        if ((gestureModifiers & 0xFFFFC000) != 0x0) {
            this.invalidModifiers = true;
        }
        else if (this.getGestureModifiers() != 0 && this.getGestureModifiersEx() == 0) {
            this.setNewModifiers();
        }
        else if (this.getGestureModifiers() == 0 && this.getGestureModifiersEx() != 0) {
            this.setOldModifiers();
        }
        else {
            this.invalidModifiers = true;
        }
    }
    
    public int getTargetActions() {
        return this.targetActions;
    }
    
    public int getGestureModifiers() {
        return this.invalidModifiers ? this.gestureModifiers : (this.gestureModifiers & 0x3F);
    }
    
    public int getGestureModifiersEx() {
        return this.invalidModifiers ? this.gestureModifiers : (this.gestureModifiers & 0x3FC0);
    }
    
    public int getUserAction() {
        return this.dropAction;
    }
    
    public int getDropAction() {
        return this.targetActions & this.getDragSourceContext().getSourceActions();
    }
    
    private void setNewModifiers() {
        if ((this.gestureModifiers & 0x10) != 0x0) {
            this.gestureModifiers |= 0x400;
        }
        if ((this.gestureModifiers & 0x8) != 0x0) {
            this.gestureModifiers |= 0x800;
        }
        if ((this.gestureModifiers & 0x4) != 0x0) {
            this.gestureModifiers |= 0x1000;
        }
        if ((this.gestureModifiers & 0x1) != 0x0) {
            this.gestureModifiers |= 0x40;
        }
        if ((this.gestureModifiers & 0x2) != 0x0) {
            this.gestureModifiers |= 0x80;
        }
        if ((this.gestureModifiers & 0x20) != 0x0) {
            this.gestureModifiers |= 0x2000;
        }
    }
    
    private void setOldModifiers() {
        if ((this.gestureModifiers & 0x400) != 0x0) {
            this.gestureModifiers |= 0x10;
        }
        if ((this.gestureModifiers & 0x800) != 0x0) {
            this.gestureModifiers |= 0x8;
        }
        if ((this.gestureModifiers & 0x1000) != 0x0) {
            this.gestureModifiers |= 0x4;
        }
        if ((this.gestureModifiers & 0x40) != 0x0) {
            this.gestureModifiers |= 0x1;
        }
        if ((this.gestureModifiers & 0x80) != 0x0) {
            this.gestureModifiers |= 0x2;
        }
        if ((this.gestureModifiers & 0x2000) != 0x0) {
            this.gestureModifiers |= 0x20;
        }
    }
}
