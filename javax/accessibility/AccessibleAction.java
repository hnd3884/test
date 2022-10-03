package javax.accessibility;

public interface AccessibleAction
{
    public static final String TOGGLE_EXPAND = new String("toggleexpand");
    public static final String INCREMENT = new String("increment");
    public static final String DECREMENT = new String("decrement");
    public static final String CLICK = new String("click");
    public static final String TOGGLE_POPUP = new String("toggle popup");
    
    int getAccessibleActionCount();
    
    String getAccessibleActionDescription(final int p0);
    
    boolean doAccessibleAction(final int p0);
}
