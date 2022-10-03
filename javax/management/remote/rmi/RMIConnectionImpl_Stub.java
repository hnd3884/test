package javax.management.remote.rmi;

import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import java.util.Set;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.remote.NotificationResult;
import javax.management.MBeanRegistrationException;
import javax.management.ReflectionException;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ObjectInstance;
import java.rmi.UnexpectedException;
import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import javax.security.auth.Subject;
import java.rmi.MarshalledObject;
import javax.management.ObjectName;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.server.RemoteStub;

public final class RMIConnectionImpl_Stub extends RemoteStub implements RMIConnection
{
    private static final long serialVersionUID = 2L;
    private static Method $method_addNotificationListener_0;
    private static Method $method_addNotificationListeners_1;
    private static Method $method_close_2;
    private static Method $method_createMBean_3;
    private static Method $method_createMBean_4;
    private static Method $method_createMBean_5;
    private static Method $method_createMBean_6;
    private static Method $method_fetchNotifications_7;
    private static Method $method_getAttribute_8;
    private static Method $method_getAttributes_9;
    private static Method $method_getConnectionId_10;
    private static Method $method_getDefaultDomain_11;
    private static Method $method_getDomains_12;
    private static Method $method_getMBeanCount_13;
    private static Method $method_getMBeanInfo_14;
    private static Method $method_getObjectInstance_15;
    private static Method $method_invoke_16;
    private static Method $method_isInstanceOf_17;
    private static Method $method_isRegistered_18;
    private static Method $method_queryMBeans_19;
    private static Method $method_queryNames_20;
    private static Method $method_removeNotificationListener_21;
    private static Method $method_removeNotificationListener_22;
    private static Method $method_removeNotificationListeners_23;
    private static Method $method_setAttribute_24;
    private static Method $method_setAttributes_25;
    private static Method $method_unregisterMBean_26;
    static /* synthetic */ Class class$javax$management$remote$rmi$RMIConnection;
    static /* synthetic */ Class class$javax$management$ObjectName;
    static /* synthetic */ Class class$java$rmi$MarshalledObject;
    static /* synthetic */ Class class$javax$security$auth$Subject;
    static /* synthetic */ Class array$Ljavax$management$ObjectName;
    static /* synthetic */ Class array$Ljava$rmi$MarshalledObject;
    static /* synthetic */ Class array$Ljavax$security$auth$Subject;
    static /* synthetic */ Class class$java$lang$AutoCloseable;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class array$Ljava$lang$String;
    static /* synthetic */ Class array$Ljava$lang$Integer;
    
    static {
        try {
            RMIConnectionImpl_Stub.$method_addNotificationListener_0 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("addNotificationListener", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_addNotificationListeners_1 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("addNotificationListeners", (RMIConnectionImpl_Stub.array$Ljavax$management$ObjectName != null) ? RMIConnectionImpl_Stub.array$Ljavax$management$ObjectName : (RMIConnectionImpl_Stub.array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")), (RMIConnectionImpl_Stub.array$Ljava$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.array$Ljava$rmi$MarshalledObject : (RMIConnectionImpl_Stub.array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")), (RMIConnectionImpl_Stub.array$Ljavax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.array$Ljavax$security$auth$Subject : (RMIConnectionImpl_Stub.array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")));
            RMIConnectionImpl_Stub.$method_close_2 = ((RMIConnectionImpl_Stub.class$java$lang$AutoCloseable != null) ? RMIConnectionImpl_Stub.class$java$lang$AutoCloseable : (RMIConnectionImpl_Stub.class$java$lang$AutoCloseable = class$("java.lang.AutoCloseable"))).getMethod("close", (Class[])new Class[0]);
            RMIConnectionImpl_Stub.$method_createMBean_3 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("createMBean", (RMIConnectionImpl_Stub.class$java$lang$String != null) ? RMIConnectionImpl_Stub.class$java$lang$String : (RMIConnectionImpl_Stub.class$java$lang$String = class$("java.lang.String")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.array$Ljava$lang$String != null) ? RMIConnectionImpl_Stub.array$Ljava$lang$String : (RMIConnectionImpl_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_createMBean_4 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("createMBean", (RMIConnectionImpl_Stub.class$java$lang$String != null) ? RMIConnectionImpl_Stub.class$java$lang$String : (RMIConnectionImpl_Stub.class$java$lang$String = class$("java.lang.String")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.array$Ljava$lang$String != null) ? RMIConnectionImpl_Stub.array$Ljava$lang$String : (RMIConnectionImpl_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_createMBean_5 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("createMBean", (RMIConnectionImpl_Stub.class$java$lang$String != null) ? RMIConnectionImpl_Stub.class$java$lang$String : (RMIConnectionImpl_Stub.class$java$lang$String = class$("java.lang.String")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_createMBean_6 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("createMBean", (RMIConnectionImpl_Stub.class$java$lang$String != null) ? RMIConnectionImpl_Stub.class$java$lang$String : (RMIConnectionImpl_Stub.class$java$lang$String = class$("java.lang.String")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_fetchNotifications_7 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("fetchNotifications", Long.TYPE, Integer.TYPE, Long.TYPE);
            RMIConnectionImpl_Stub.$method_getAttribute_8 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getAttribute", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$lang$String != null) ? RMIConnectionImpl_Stub.class$java$lang$String : (RMIConnectionImpl_Stub.class$java$lang$String = class$("java.lang.String")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_getAttributes_9 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getAttributes", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.array$Ljava$lang$String != null) ? RMIConnectionImpl_Stub.array$Ljava$lang$String : (RMIConnectionImpl_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_getConnectionId_10 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getConnectionId", (Class[])new Class[0]);
            RMIConnectionImpl_Stub.$method_getDefaultDomain_11 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getDefaultDomain", (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_getDomains_12 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getDomains", (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_getMBeanCount_13 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getMBeanCount", (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_getMBeanInfo_14 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getMBeanInfo", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_getObjectInstance_15 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getObjectInstance", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_invoke_16 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("invoke", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$lang$String != null) ? RMIConnectionImpl_Stub.class$java$lang$String : (RMIConnectionImpl_Stub.class$java$lang$String = class$("java.lang.String")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.array$Ljava$lang$String != null) ? RMIConnectionImpl_Stub.array$Ljava$lang$String : (RMIConnectionImpl_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_isInstanceOf_17 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("isInstanceOf", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$lang$String != null) ? RMIConnectionImpl_Stub.class$java$lang$String : (RMIConnectionImpl_Stub.class$java$lang$String = class$("java.lang.String")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_isRegistered_18 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("isRegistered", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_queryMBeans_19 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("queryMBeans", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_queryNames_20 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("queryNames", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_removeNotificationListener_21 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("removeNotificationListener", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_removeNotificationListener_22 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("removeNotificationListener", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_removeNotificationListeners_23 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("removeNotificationListeners", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.array$Ljava$lang$Integer != null) ? RMIConnectionImpl_Stub.array$Ljava$lang$Integer : (RMIConnectionImpl_Stub.array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_setAttribute_24 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("setAttribute", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_setAttributes_25 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("setAttributes", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject != null) ? RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject : (RMIConnectionImpl_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
            RMIConnectionImpl_Stub.$method_unregisterMBean_26 = ((RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection : (RMIConnectionImpl_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("unregisterMBean", (RMIConnectionImpl_Stub.class$javax$management$ObjectName != null) ? RMIConnectionImpl_Stub.class$javax$management$ObjectName : (RMIConnectionImpl_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")), (RMIConnectionImpl_Stub.class$javax$security$auth$Subject != null) ? RMIConnectionImpl_Stub.class$javax$security$auth$Subject : (RMIConnectionImpl_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
        }
        catch (final NoSuchMethodException ex) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }
    
    public RMIConnectionImpl_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    public void addNotificationListener(final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final MarshalledObject marshalledObject2, final Subject subject) throws IOException, InstanceNotFoundException {
        try {
            super.ref.invoke(this, RMIConnectionImpl_Stub.$method_addNotificationListener_0, new Object[] { objectName, objectName2, marshalledObject, marshalledObject2, subject }, -8578317696269497109L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public Integer[] addNotificationListeners(final ObjectName[] array, final MarshalledObject[] array2, final Subject[] array3) throws IOException, InstanceNotFoundException {
        try {
            return (Integer[])super.ref.invoke(this, RMIConnectionImpl_Stub.$method_addNotificationListeners_1, new Object[] { array, array2, array3 }, -5321691879380783377L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public void close() throws IOException {
        try {
            super.ref.invoke(this, RMIConnectionImpl_Stub.$method_close_2, null, -4742752445160157748L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws IOException, InstanceAlreadyExistsException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
        try {
            return (ObjectInstance)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_createMBean_3, new Object[] { s, objectName, marshalledObject, array, subject }, 4867822117947806114L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceAlreadyExistsException ex3) {
            throw ex3;
        }
        catch (final MBeanException ex4) {
            throw ex4;
        }
        catch (final NotCompliantMBeanException ex5) {
            throw ex5;
        }
        catch (final ReflectionException ex6) {
            throw ex6;
        }
        catch (final Exception ex7) {
            throw new UnexpectedException("undeclared checked exception", ex7);
        }
    }
    
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws IOException, InstanceAlreadyExistsException, InstanceNotFoundException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
        try {
            return (ObjectInstance)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_createMBean_4, new Object[] { s, objectName, objectName2, marshalledObject, array, subject }, -6604955182088909937L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceAlreadyExistsException ex3) {
            throw ex3;
        }
        catch (final InstanceNotFoundException ex4) {
            throw ex4;
        }
        catch (final MBeanException ex5) {
            throw ex5;
        }
        catch (final NotCompliantMBeanException ex6) {
            throw ex6;
        }
        catch (final ReflectionException ex7) {
            throw ex7;
        }
        catch (final Exception ex8) {
            throw new UnexpectedException("undeclared checked exception", ex8);
        }
    }
    
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final Subject subject) throws IOException, InstanceAlreadyExistsException, InstanceNotFoundException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
        try {
            return (ObjectInstance)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_createMBean_5, new Object[] { s, objectName, objectName2, subject }, -8679469989872508324L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceAlreadyExistsException ex3) {
            throw ex3;
        }
        catch (final InstanceNotFoundException ex4) {
            throw ex4;
        }
        catch (final MBeanException ex5) {
            throw ex5;
        }
        catch (final NotCompliantMBeanException ex6) {
            throw ex6;
        }
        catch (final ReflectionException ex7) {
            throw ex7;
        }
        catch (final Exception ex8) {
            throw new UnexpectedException("undeclared checked exception", ex8);
        }
    }
    
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final Subject subject) throws IOException, InstanceAlreadyExistsException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
        try {
            return (ObjectInstance)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_createMBean_6, new Object[] { s, objectName, subject }, 2510753813974665446L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceAlreadyExistsException ex3) {
            throw ex3;
        }
        catch (final MBeanException ex4) {
            throw ex4;
        }
        catch (final NotCompliantMBeanException ex5) {
            throw ex5;
        }
        catch (final ReflectionException ex6) {
            throw ex6;
        }
        catch (final Exception ex7) {
            throw new UnexpectedException("undeclared checked exception", ex7);
        }
    }
    
    public NotificationResult fetchNotifications(final long n, final int n2, final long n3) throws IOException {
        try {
            return (NotificationResult)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_fetchNotifications_7, new Object[] { new Long(n), new Integer(n2), new Long(n3) }, -5037523307973544478L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public Object getAttribute(final ObjectName objectName, final String s, final Subject subject) throws IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
        try {
            return super.ref.invoke(this, RMIConnectionImpl_Stub.$method_getAttribute_8, new Object[] { objectName, s, subject }, -1089783104982388203L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final AttributeNotFoundException ex3) {
            throw ex3;
        }
        catch (final InstanceNotFoundException ex4) {
            throw ex4;
        }
        catch (final MBeanException ex5) {
            throw ex5;
        }
        catch (final ReflectionException ex6) {
            throw ex6;
        }
        catch (final Exception ex7) {
            throw new UnexpectedException("undeclared checked exception", ex7);
        }
    }
    
    public AttributeList getAttributes(final ObjectName objectName, final String[] array, final Subject subject) throws IOException, InstanceNotFoundException, ReflectionException {
        try {
            return (AttributeList)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_getAttributes_9, new Object[] { objectName, array, subject }, 6285293806596348999L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final ReflectionException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public String getConnectionId() throws IOException {
        try {
            return (String)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_getConnectionId_10, null, -67907180346059933L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public String getDefaultDomain(final Subject subject) throws IOException {
        try {
            return (String)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_getDefaultDomain_11, new Object[] { subject }, 6047668923998658472L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public String[] getDomains(final Subject subject) throws IOException {
        try {
            return (String[])super.ref.invoke(this, RMIConnectionImpl_Stub.$method_getDomains_12, new Object[] { subject }, -6662314179953625551L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public Integer getMBeanCount(final Subject subject) throws IOException {
        try {
            return (Integer)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_getMBeanCount_13, new Object[] { subject }, -2042362057335820635L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public MBeanInfo getMBeanInfo(final ObjectName objectName, final Subject subject) throws IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
        try {
            return (MBeanInfo)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_getMBeanInfo_14, new Object[] { objectName, subject }, -7404813916326233354L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final IntrospectionException ex4) {
            throw ex4;
        }
        catch (final ReflectionException ex5) {
            throw ex5;
        }
        catch (final Exception ex6) {
            throw new UnexpectedException("undeclared checked exception", ex6);
        }
    }
    
    public ObjectInstance getObjectInstance(final ObjectName objectName, final Subject subject) throws IOException, InstanceNotFoundException {
        try {
            return (ObjectInstance)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_getObjectInstance_15, new Object[] { objectName, subject }, 6950095694996159938L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public Object invoke(final ObjectName objectName, final String s, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws IOException, InstanceNotFoundException, MBeanException, ReflectionException {
        try {
            return super.ref.invoke(this, RMIConnectionImpl_Stub.$method_invoke_16, new Object[] { objectName, s, marshalledObject, array, subject }, 1434350937885235744L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final MBeanException ex4) {
            throw ex4;
        }
        catch (final ReflectionException ex5) {
            throw ex5;
        }
        catch (final Exception ex6) {
            throw new UnexpectedException("undeclared checked exception", ex6);
        }
    }
    
    public boolean isInstanceOf(final ObjectName objectName, final String s, final Subject subject) throws IOException, InstanceNotFoundException {
        try {
            return (boolean)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_isInstanceOf_17, new Object[] { objectName, s, subject }, -2147516868461740814L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public boolean isRegistered(final ObjectName objectName, final Subject subject) throws IOException {
        try {
            return (boolean)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_isRegistered_18, new Object[] { objectName, subject }, 8325683335228268564L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public Set queryMBeans(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws IOException {
        try {
            return (Set)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_queryMBeans_19, new Object[] { objectName, marshalledObject, subject }, 2915881009400597976L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public Set queryNames(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws IOException {
        try {
            return (Set)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_queryNames_20, new Object[] { objectName, marshalledObject, subject }, 9152567528369059802L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final MarshalledObject marshalledObject2, final Subject subject) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
        try {
            super.ref.invoke(this, RMIConnectionImpl_Stub.$method_removeNotificationListener_21, new Object[] { objectName, objectName2, marshalledObject, marshalledObject2, subject }, 2578029900065214857L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final ListenerNotFoundException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final Subject subject) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
        try {
            super.ref.invoke(this, RMIConnectionImpl_Stub.$method_removeNotificationListener_22, new Object[] { objectName, objectName2, subject }, 6604721169198089513L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final ListenerNotFoundException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public void removeNotificationListeners(final ObjectName objectName, final Integer[] array, final Subject subject) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
        try {
            super.ref.invoke(this, RMIConnectionImpl_Stub.$method_removeNotificationListeners_23, new Object[] { objectName, array, subject }, 2549120024456183446L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final ListenerNotFoundException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public void setAttribute(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws IOException, AttributeNotFoundException, InstanceNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        try {
            super.ref.invoke(this, RMIConnectionImpl_Stub.$method_setAttribute_24, new Object[] { objectName, marshalledObject, subject }, 6738606893952597516L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final AttributeNotFoundException ex3) {
            throw ex3;
        }
        catch (final InstanceNotFoundException ex4) {
            throw ex4;
        }
        catch (final InvalidAttributeValueException ex5) {
            throw ex5;
        }
        catch (final MBeanException ex6) {
            throw ex6;
        }
        catch (final ReflectionException ex7) {
            throw ex7;
        }
        catch (final Exception ex8) {
            throw new UnexpectedException("undeclared checked exception", ex8);
        }
    }
    
    public AttributeList setAttributes(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws IOException, InstanceNotFoundException, ReflectionException {
        try {
            return (AttributeList)super.ref.invoke(this, RMIConnectionImpl_Stub.$method_setAttributes_25, new Object[] { objectName, marshalledObject, subject }, -230470228399681820L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final ReflectionException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public void unregisterMBean(final ObjectName objectName, final Subject subject) throws IOException, InstanceNotFoundException, MBeanRegistrationException {
        try {
            super.ref.invoke(this, RMIConnectionImpl_Stub.$method_unregisterMBean_26, new Object[] { objectName, subject }, -159498580868721452L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final InstanceNotFoundException ex3) {
            throw ex3;
        }
        catch (final MBeanRegistrationException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
}
