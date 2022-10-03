package javax.management;

public class AttributeChangeNotification extends Notification
{
    private static final long serialVersionUID = 535176054565814134L;
    public static final String ATTRIBUTE_CHANGE = "jmx.attribute.change";
    private String attributeName;
    private String attributeType;
    private Object oldValue;
    private Object newValue;
    
    public AttributeChangeNotification(final Object o, final long n, final long n2, final String s, final String attributeName, final String attributeType, final Object oldValue, final Object newValue) {
        super("jmx.attribute.change", o, n, n2, s);
        this.attributeName = null;
        this.attributeType = null;
        this.oldValue = null;
        this.newValue = null;
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public String getAttributeType() {
        return this.attributeType;
    }
    
    public Object getOldValue() {
        return this.oldValue;
    }
    
    public Object getNewValue() {
        return this.newValue;
    }
}
