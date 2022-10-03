package sun.management;

import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.Notification;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Iterator;
import javax.management.Descriptor;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanOperationInfo;
import java.util.HashMap;
import javax.management.MBeanParameterInfo;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;
import javax.management.MBeanInfo;
import javax.management.AttributeList;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanNotificationInfo;
import java.util.Map;
import com.sun.management.DiagnosticCommandMBean;

class DiagnosticCommandImpl extends NotificationEmitterSupport implements DiagnosticCommandMBean
{
    private final VMManagement jvm;
    private volatile Map<String, Wrapper> wrappers;
    private static final String strClassName;
    private static final String strArrayClassName;
    private final boolean isSupported;
    private static final String notifName = "javax.management.Notification";
    private static final String[] diagFramNotifTypes;
    private MBeanNotificationInfo[] notifInfo;
    private static long seqNumber;
    
    @Override
    public Object getAttribute(final String s) throws AttributeNotFoundException, MBeanException, ReflectionException {
        throw new AttributeNotFoundException(s);
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        throw new AttributeNotFoundException(attribute.getName());
    }
    
    @Override
    public AttributeList getAttributes(final String[] array) {
        return new AttributeList();
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList list) {
        return new AttributeList();
    }
    
    DiagnosticCommandImpl(final VMManagement jvm) {
        this.wrappers = null;
        this.notifInfo = null;
        this.jvm = jvm;
        this.isSupported = jvm.isRemoteDiagnosticCommandsSupported();
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        final TreeSet set = new TreeSet((Comparator<? super E>)new OperationInfoComparator());
        Map map;
        if (!this.isSupported) {
            map = Collections.EMPTY_MAP;
        }
        else {
            try {
                final String[] diagnosticCommands = this.getDiagnosticCommands();
                final DiagnosticCommandInfo[] diagnosticCommandInfo = this.getDiagnosticCommandInfo(diagnosticCommands);
                final MBeanParameterInfo[] array = { new MBeanParameterInfo("arguments", DiagnosticCommandImpl.strArrayClassName, "Array of Diagnostic Commands Arguments and Options") };
                map = new HashMap();
                for (int i = 0; i < diagnosticCommands.length; ++i) {
                    final String transform = transform(diagnosticCommands[i]);
                    try {
                        final Wrapper wrapper = new Wrapper(transform, diagnosticCommands[i], diagnosticCommandInfo[i]);
                        map.put(transform, wrapper);
                        set.add(new MBeanOperationInfo(wrapper.name, wrapper.info.getDescription(), (MBeanParameterInfo[])((wrapper.info.getArgumentsInfo() == null || wrapper.info.getArgumentsInfo().isEmpty()) ? null : array), DiagnosticCommandImpl.strClassName, 2, this.commandDescriptor(wrapper)));
                    }
                    catch (final InstantiationException ex) {}
                }
            }
            catch (final IllegalArgumentException | UnsupportedOperationException ex2) {
                map = Collections.EMPTY_MAP;
            }
        }
        this.wrappers = (Map<String, Wrapper>)Collections.unmodifiableMap((Map<?, ?>)map);
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("immutableInfo", "false");
        hashMap.put("interfaceClassName", "com.sun.management.DiagnosticCommandMBean");
        hashMap.put("mxbean", "false");
        return new MBeanInfo(this.getClass().getName(), "Diagnostic Commands", null, null, (MBeanOperationInfo[])set.toArray(new MBeanOperationInfo[set.size()]), this.getNotificationInfo(), new ImmutableDescriptor(hashMap));
    }
    
    @Override
    public Object invoke(final String s, final Object[] array, final String[] array2) throws MBeanException, ReflectionException {
        if (!this.isSupported) {
            throw new UnsupportedOperationException();
        }
        if (this.wrappers == null) {
            this.getMBeanInfo();
        }
        final Wrapper wrapper = this.wrappers.get(s);
        if (wrapper != null) {
            if (wrapper.info.getArgumentsInfo().isEmpty() && (array == null || array.length == 0) && (array2 == null || array2.length == 0)) {
                return wrapper.execute(null);
            }
            if (array != null && array.length == 1 && array2 != null && array2.length == 1 && array2[0] != null && array2[0].compareTo(DiagnosticCommandImpl.strArrayClassName) == 0) {
                return wrapper.execute((String[])array[0]);
            }
        }
        throw new ReflectionException(new NoSuchMethodException(s));
    }
    
    private static String transform(final String s) {
        final StringBuilder sb = new StringBuilder();
        boolean b = true;
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '.' || char1 == '_') {
                b = false;
                n = 1;
            }
            else if (n != 0) {
                n = 0;
                sb.append(Character.toUpperCase(char1));
            }
            else if (b) {
                sb.append(Character.toLowerCase(char1));
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    private Descriptor commandDescriptor(final Wrapper wrapper) throws IllegalArgumentException {
        final HashMap hashMap = new HashMap();
        hashMap.put("dcmd.name", wrapper.info.getName());
        hashMap.put("dcmd.description", wrapper.info.getDescription());
        hashMap.put("dcmd.vmImpact", wrapper.info.getImpact());
        hashMap.put("dcmd.permissionClass", wrapper.info.getPermissionClass());
        hashMap.put("dcmd.permissionName", wrapper.info.getPermissionName());
        hashMap.put("dcmd.permissionAction", wrapper.info.getPermissionAction());
        hashMap.put("dcmd.enabled", wrapper.info.isEnabled());
        final StringBuilder sb = new StringBuilder();
        sb.append("help ");
        sb.append(wrapper.info.getName());
        hashMap.put("dcmd.help", this.executeDiagnosticCommand(sb.toString()));
        if (wrapper.info.getArgumentsInfo() != null && !wrapper.info.getArgumentsInfo().isEmpty()) {
            final HashMap hashMap2 = new HashMap();
            for (final DiagnosticCommandArgumentInfo diagnosticCommandArgumentInfo : wrapper.info.getArgumentsInfo()) {
                final HashMap<String, String> hashMap3 = new HashMap<String, String>();
                hashMap3.put("dcmd.arg.name", diagnosticCommandArgumentInfo.getName());
                hashMap3.put("dcmd.arg.type", diagnosticCommandArgumentInfo.getType());
                hashMap3.put("dcmd.arg.description", diagnosticCommandArgumentInfo.getDescription());
                hashMap3.put("dcmd.arg.isMandatory", (String)diagnosticCommandArgumentInfo.isMandatory());
                hashMap3.put("dcmd.arg.isMultiple", (String)diagnosticCommandArgumentInfo.isMultiple());
                final boolean option = diagnosticCommandArgumentInfo.isOption();
                hashMap3.put("dcmd.arg.isOption", (String)option);
                if (!option) {
                    hashMap3.put("dcmd.arg.position", (String)diagnosticCommandArgumentInfo.getPosition());
                }
                else {
                    hashMap3.put("dcmd.arg.position", (String)(-1));
                }
                hashMap2.put(diagnosticCommandArgumentInfo.getName(), new ImmutableDescriptor(hashMap3));
            }
            hashMap.put("dcmd.arguments", new ImmutableDescriptor(hashMap2));
        }
        return new ImmutableDescriptor(hashMap);
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        synchronized (this) {
            if (this.notifInfo == null) {
                (this.notifInfo = new MBeanNotificationInfo[1])[0] = new MBeanNotificationInfo(DiagnosticCommandImpl.diagFramNotifTypes, "javax.management.Notification", "Diagnostic Framework Notification");
            }
        }
        return this.notifInfo.clone();
    }
    
    private static long getNextSeqNumber() {
        return ++DiagnosticCommandImpl.seqNumber;
    }
    
    private void createDiagnosticFrameworkNotification() {
        if (!this.hasListeners()) {
            return;
        }
        Object instance = null;
        try {
            instance = ObjectName.getInstance("com.sun.management:type=DiagnosticCommand");
        }
        catch (final MalformedObjectNameException ex) {}
        final Notification notification = new Notification("jmx.mbean.info.changed", instance, getNextSeqNumber());
        notification.setUserData(this.getMBeanInfo());
        this.sendNotification(notification);
    }
    
    @Override
    public synchronized void addNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) {
        final boolean hasListeners = this.hasListeners();
        super.addNotificationListener(notificationListener, notificationFilter, o);
        final boolean hasListeners2 = this.hasListeners();
        if (!hasListeners && hasListeners2) {
            this.setNotificationEnabled(true);
        }
    }
    
    @Override
    public synchronized void removeNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        final boolean hasListeners = this.hasListeners();
        super.removeNotificationListener(notificationListener);
        final boolean hasListeners2 = this.hasListeners();
        if (hasListeners && !hasListeners2) {
            this.setNotificationEnabled(false);
        }
    }
    
    @Override
    public synchronized void removeNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        final boolean hasListeners = this.hasListeners();
        super.removeNotificationListener(notificationListener, notificationFilter, o);
        final boolean hasListeners2 = this.hasListeners();
        if (hasListeners && !hasListeners2) {
            this.setNotificationEnabled(false);
        }
    }
    
    private native void setNotificationEnabled(final boolean p0);
    
    private native String[] getDiagnosticCommands();
    
    private native DiagnosticCommandInfo[] getDiagnosticCommandInfo(final String[] p0);
    
    private native String executeDiagnosticCommand(final String p0);
    
    static {
        strClassName = "".getClass().getName();
        strArrayClassName = String[].class.getName();
        diagFramNotifTypes = new String[] { "jmx.mbean.info.changed" };
        DiagnosticCommandImpl.seqNumber = 0L;
    }
    
    private class Wrapper
    {
        String name;
        String cmd;
        DiagnosticCommandInfo info;
        Permission permission;
        
        Wrapper(final String name, final String cmd, final DiagnosticCommandInfo info) throws InstantiationException {
            this.name = name;
            this.cmd = cmd;
            this.info = info;
            this.permission = null;
            Throwable t = null;
            if (info.getPermissionClass() != null) {
                try {
                    final Class<?> forName = Class.forName(info.getPermissionClass());
                    if (info.getPermissionAction() == null) {
                        try {
                            this.permission = (Permission)forName.getConstructor(String.class).newInstance(info.getPermissionName());
                        }
                        catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                            t = (Throwable)ex;
                        }
                    }
                    if (this.permission == null) {
                        try {
                            this.permission = (Permission)forName.getConstructor(String.class, String.class).newInstance(info.getPermissionName(), info.getPermissionAction());
                        }
                        catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex2) {
                            t = (Throwable)ex2;
                        }
                    }
                }
                catch (final ClassNotFoundException ex3) {}
                if (this.permission == null) {
                    new InstantiationException("Unable to instantiate required permission").initCause(t);
                }
            }
        }
        
        public String execute(final String[] array) {
            if (this.permission != null) {
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    securityManager.checkPermission(this.permission);
                }
            }
            if (array == null) {
                return DiagnosticCommandImpl.this.executeDiagnosticCommand(this.cmd);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(this.cmd);
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new IllegalArgumentException("Invalid null argument");
                }
                sb.append(" ");
                sb.append(array[i]);
            }
            return DiagnosticCommandImpl.this.executeDiagnosticCommand(sb.toString());
        }
    }
    
    private static class OperationInfoComparator implements Comparator<MBeanOperationInfo>
    {
        @Override
        public int compare(final MBeanOperationInfo mBeanOperationInfo, final MBeanOperationInfo mBeanOperationInfo2) {
            return mBeanOperationInfo.getName().compareTo(mBeanOperationInfo2.getName());
        }
    }
}
