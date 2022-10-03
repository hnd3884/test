package sun.awt;

import java.awt.Component;
import java.awt.event.FocusEvent;

public class CausedFocusEvent extends FocusEvent
{
    private final Cause cause;
    
    public Cause getCause() {
        return this.cause;
    }
    
    @Override
    public String toString() {
        return "java.awt.FocusEvent[" + super.paramString() + ",cause=" + this.cause + "] on " + this.getSource();
    }
    
    public CausedFocusEvent(final Component component, final int n, final boolean b, final Component component2, Cause unknown) {
        super(component, n, b, component2);
        if (unknown == null) {
            unknown = Cause.UNKNOWN;
        }
        this.cause = unknown;
    }
    
    public static FocusEvent retarget(final FocusEvent focusEvent, final Component component) {
        if (focusEvent == null) {
            return null;
        }
        return new CausedFocusEvent(component, focusEvent.getID(), focusEvent.isTemporary(), focusEvent.getOppositeComponent(), (focusEvent instanceof CausedFocusEvent) ? ((CausedFocusEvent)focusEvent).getCause() : Cause.RETARGETED);
    }
    
    public enum Cause
    {
        UNKNOWN, 
        MOUSE_EVENT, 
        TRAVERSAL, 
        TRAVERSAL_UP, 
        TRAVERSAL_DOWN, 
        TRAVERSAL_FORWARD, 
        TRAVERSAL_BACKWARD, 
        MANUAL_REQUEST, 
        AUTOMATIC_TRAVERSE, 
        ROLLBACK, 
        NATIVE_SYSTEM, 
        ACTIVATION, 
        CLEAR_GLOBAL_FOCUS_OWNER, 
        RETARGETED;
    }
}
