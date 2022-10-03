package javax.accessibility;

public class AccessibleRelation extends AccessibleBundle
{
    private Object[] target;
    public static final String LABEL_FOR;
    public static final String LABELED_BY;
    public static final String MEMBER_OF;
    public static final String CONTROLLER_FOR;
    public static final String CONTROLLED_BY;
    public static final String FLOWS_TO = "flowsTo";
    public static final String FLOWS_FROM = "flowsFrom";
    public static final String SUBWINDOW_OF = "subwindowOf";
    public static final String PARENT_WINDOW_OF = "parentWindowOf";
    public static final String EMBEDS = "embeds";
    public static final String EMBEDDED_BY = "embeddedBy";
    public static final String CHILD_NODE_OF = "childNodeOf";
    public static final String LABEL_FOR_PROPERTY = "labelForProperty";
    public static final String LABELED_BY_PROPERTY = "labeledByProperty";
    public static final String MEMBER_OF_PROPERTY = "memberOfProperty";
    public static final String CONTROLLER_FOR_PROPERTY = "controllerForProperty";
    public static final String CONTROLLED_BY_PROPERTY = "controlledByProperty";
    public static final String FLOWS_TO_PROPERTY = "flowsToProperty";
    public static final String FLOWS_FROM_PROPERTY = "flowsFromProperty";
    public static final String SUBWINDOW_OF_PROPERTY = "subwindowOfProperty";
    public static final String PARENT_WINDOW_OF_PROPERTY = "parentWindowOfProperty";
    public static final String EMBEDS_PROPERTY = "embedsProperty";
    public static final String EMBEDDED_BY_PROPERTY = "embeddedByProperty";
    public static final String CHILD_NODE_OF_PROPERTY = "childNodeOfProperty";
    
    public AccessibleRelation(final String key) {
        this.target = new Object[0];
        this.key = key;
        this.target = null;
    }
    
    public AccessibleRelation(final String key, final Object o) {
        this.target = new Object[0];
        this.key = key;
        (this.target = new Object[1])[0] = o;
    }
    
    public AccessibleRelation(final String key, final Object[] target) {
        this.target = new Object[0];
        this.key = key;
        this.target = target;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public Object[] getTarget() {
        if (this.target == null) {
            this.target = new Object[0];
        }
        final Object[] array = new Object[this.target.length];
        for (int i = 0; i < this.target.length; ++i) {
            array[i] = this.target[i];
        }
        return array;
    }
    
    public void setTarget(final Object o) {
        (this.target = new Object[1])[0] = o;
    }
    
    public void setTarget(final Object[] target) {
        this.target = target;
    }
    
    static {
        LABEL_FOR = new String("labelFor");
        LABELED_BY = new String("labeledBy");
        MEMBER_OF = new String("memberOf");
        CONTROLLER_FOR = new String("controllerFor");
        CONTROLLED_BY = new String("controlledBy");
    }
}
