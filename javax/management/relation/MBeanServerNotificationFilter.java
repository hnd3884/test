package javax.management.relation;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import com.sun.jmx.mbeanserver.Util;
import java.io.ObjectInputStream;
import javax.management.MBeanServerNotification;
import java.util.logging.Level;
import javax.management.Notification;
import java.util.Collection;
import com.sun.jmx.defaults.JmxProperties;
import java.util.Vector;
import javax.management.ObjectName;
import java.util.List;
import java.io.ObjectStreamField;
import javax.management.NotificationFilterSupport;

public class MBeanServerNotificationFilter extends NotificationFilterSupport
{
    private static final long oldSerialVersionUID = 6001782699077323605L;
    private static final long newSerialVersionUID = 2605900539589789736L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private List<ObjectName> selectedNames;
    private List<ObjectName> deselectedNames;
    
    public MBeanServerNotificationFilter() {
        this.selectedNames = new Vector<ObjectName>();
        this.deselectedNames = null;
        JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "MBeanServerNotificationFilter");
        this.enableType("JMX.mbean.registered");
        this.enableType("JMX.mbean.unregistered");
        JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "MBeanServerNotificationFilter");
    }
    
    public synchronized void disableAllObjectNames() {
        JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "disableAllObjectNames");
        this.selectedNames = new Vector<ObjectName>();
        this.deselectedNames = null;
        JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "disableAllObjectNames");
    }
    
    public synchronized void disableObjectName(final ObjectName objectName) throws IllegalArgumentException {
        if (objectName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "disableObjectName", objectName);
        if (this.selectedNames != null && this.selectedNames.size() != 0) {
            this.selectedNames.remove(objectName);
        }
        if (this.deselectedNames != null && !this.deselectedNames.contains(objectName)) {
            this.deselectedNames.add(objectName);
        }
        JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "disableObjectName");
    }
    
    public synchronized void enableAllObjectNames() {
        JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "enableAllObjectNames");
        this.selectedNames = null;
        this.deselectedNames = new Vector<ObjectName>();
        JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "enableAllObjectNames");
    }
    
    public synchronized void enableObjectName(final ObjectName objectName) throws IllegalArgumentException {
        if (objectName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "enableObjectName", objectName);
        if (this.deselectedNames != null && this.deselectedNames.size() != 0) {
            this.deselectedNames.remove(objectName);
        }
        if (this.selectedNames != null && !this.selectedNames.contains(objectName)) {
            this.selectedNames.add(objectName);
        }
        JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "enableObjectName");
    }
    
    public synchronized Vector<ObjectName> getEnabledObjectNames() {
        if (this.selectedNames != null) {
            return new Vector<ObjectName>(this.selectedNames);
        }
        return null;
    }
    
    public synchronized Vector<ObjectName> getDisabledObjectNames() {
        if (this.deselectedNames != null) {
            return new Vector<ObjectName>(this.deselectedNames);
        }
        return null;
    }
    
    @Override
    public synchronized boolean isNotificationEnabled(final Notification notification) throws IllegalArgumentException {
        if (notification == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", notification);
        if (!this.getEnabledTypes().contains(notification.getType())) {
            JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "Type not selected, exiting");
            return false;
        }
        final ObjectName mBeanName = ((MBeanServerNotification)notification).getMBeanName();
        boolean contains = false;
        if (this.selectedNames != null) {
            if (this.selectedNames.size() == 0) {
                JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "No ObjectNames selected, exiting");
                return false;
            }
            contains = this.selectedNames.contains(mBeanName);
            if (!contains) {
                JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName not in selected list, exiting");
                return false;
            }
        }
        if (!contains) {
            if (this.deselectedNames == null) {
                JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName not selected, and all names deselected, exiting");
                return false;
            }
            if (this.deselectedNames.contains(mBeanName)) {
                JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName explicitly not selected, exiting");
                return false;
            }
        }
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName selected, exiting");
        return true;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (MBeanServerNotificationFilter.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            this.selectedNames = (List<ObjectName>)Util.cast(fields.get("mySelectObjNameList", null));
            if (fields.defaulted("mySelectObjNameList")) {
                throw new NullPointerException("mySelectObjNameList");
            }
            this.deselectedNames = (List<ObjectName>)Util.cast(fields.get("myDeselectObjNameList", null));
            if (fields.defaulted("myDeselectObjNameList")) {
                throw new NullPointerException("myDeselectObjNameList");
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (MBeanServerNotificationFilter.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("mySelectObjNameList", this.selectedNames);
            putFields.put("myDeselectObjNameList", this.deselectedNames);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("mySelectObjNameList", Vector.class), new ObjectStreamField("myDeselectObjNameList", Vector.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("selectedNames", List.class), new ObjectStreamField("deselectedNames", List.class) };
        MBeanServerNotificationFilter.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            MBeanServerNotificationFilter.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (MBeanServerNotificationFilter.compat) {
            serialPersistentFields = MBeanServerNotificationFilter.oldSerialPersistentFields;
            serialVersionUID = 6001782699077323605L;
        }
        else {
            serialPersistentFields = MBeanServerNotificationFilter.newSerialPersistentFields;
            serialVersionUID = 2605900539589789736L;
        }
    }
}
