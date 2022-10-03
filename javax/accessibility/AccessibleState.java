package javax.accessibility;

public class AccessibleState extends AccessibleBundle
{
    public static final AccessibleState ACTIVE;
    public static final AccessibleState PRESSED;
    public static final AccessibleState ARMED;
    public static final AccessibleState BUSY;
    public static final AccessibleState CHECKED;
    public static final AccessibleState EDITABLE;
    public static final AccessibleState EXPANDABLE;
    public static final AccessibleState COLLAPSED;
    public static final AccessibleState EXPANDED;
    public static final AccessibleState ENABLED;
    public static final AccessibleState FOCUSABLE;
    public static final AccessibleState FOCUSED;
    public static final AccessibleState ICONIFIED;
    public static final AccessibleState MODAL;
    public static final AccessibleState OPAQUE;
    public static final AccessibleState RESIZABLE;
    public static final AccessibleState MULTISELECTABLE;
    public static final AccessibleState SELECTABLE;
    public static final AccessibleState SELECTED;
    public static final AccessibleState SHOWING;
    public static final AccessibleState VISIBLE;
    public static final AccessibleState VERTICAL;
    public static final AccessibleState HORIZONTAL;
    public static final AccessibleState SINGLE_LINE;
    public static final AccessibleState MULTI_LINE;
    public static final AccessibleState TRANSIENT;
    public static final AccessibleState MANAGES_DESCENDANTS;
    public static final AccessibleState INDETERMINATE;
    public static final AccessibleState TRUNCATED;
    
    protected AccessibleState(final String key) {
        this.key = key;
    }
    
    static {
        ACTIVE = new AccessibleState("active");
        PRESSED = new AccessibleState("pressed");
        ARMED = new AccessibleState("armed");
        BUSY = new AccessibleState("busy");
        CHECKED = new AccessibleState("checked");
        EDITABLE = new AccessibleState("editable");
        EXPANDABLE = new AccessibleState("expandable");
        COLLAPSED = new AccessibleState("collapsed");
        EXPANDED = new AccessibleState("expanded");
        ENABLED = new AccessibleState("enabled");
        FOCUSABLE = new AccessibleState("focusable");
        FOCUSED = new AccessibleState("focused");
        ICONIFIED = new AccessibleState("iconified");
        MODAL = new AccessibleState("modal");
        OPAQUE = new AccessibleState("opaque");
        RESIZABLE = new AccessibleState("resizable");
        MULTISELECTABLE = new AccessibleState("multiselectable");
        SELECTABLE = new AccessibleState("selectable");
        SELECTED = new AccessibleState("selected");
        SHOWING = new AccessibleState("showing");
        VISIBLE = new AccessibleState("visible");
        VERTICAL = new AccessibleState("vertical");
        HORIZONTAL = new AccessibleState("horizontal");
        SINGLE_LINE = new AccessibleState("singleline");
        MULTI_LINE = new AccessibleState("multiline");
        TRANSIENT = new AccessibleState("transient");
        MANAGES_DESCENDANTS = new AccessibleState("managesDescendants");
        INDETERMINATE = new AccessibleState("indeterminate");
        TRUNCATED = new AccessibleState("truncated");
    }
}
