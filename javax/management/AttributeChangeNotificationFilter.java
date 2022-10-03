package javax.management;

import java.util.Vector;

public class AttributeChangeNotificationFilter implements NotificationFilter
{
    private static final long serialVersionUID = -6347317584796410029L;
    private Vector<String> enabledAttributes;
    
    public AttributeChangeNotificationFilter() {
        this.enabledAttributes = new Vector<String>();
    }
    
    @Override
    public synchronized boolean isNotificationEnabled(final Notification notification) {
        final String type = notification.getType();
        return type != null && type.equals("jmx.attribute.change") && notification instanceof AttributeChangeNotification && this.enabledAttributes.contains(((AttributeChangeNotification)notification).getAttributeName());
    }
    
    public synchronized void enableAttribute(final String s) throws IllegalArgumentException {
        if (s == null) {
            throw new IllegalArgumentException("The name cannot be null.");
        }
        if (!this.enabledAttributes.contains(s)) {
            this.enabledAttributes.addElement(s);
        }
    }
    
    public synchronized void disableAttribute(final String s) {
        this.enabledAttributes.removeElement(s);
    }
    
    public synchronized void disableAllAttributes() {
        this.enabledAttributes.removeAllElements();
    }
    
    public synchronized Vector<String> getEnabledAttributes() {
        return this.enabledAttributes;
    }
}
