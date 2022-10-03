package javax.management.remote.rmi;

import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import java.io.ObjectInputStream;
import java.util.Set;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.remote.NotificationResult;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ReflectionException;
import javax.management.ObjectInstance;
import java.security.Permission;
import java.io.SerializablePermission;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import java.rmi.UnexpectedException;
import javax.management.InstanceNotFoundException;
import org.omg.CORBA.portable.InputStream;
import java.io.Serializable;
import org.omg.CORBA_2_3.portable.OutputStream;
import javax.rmi.CORBA.Util;
import java.io.IOError;
import java.io.IOException;
import javax.security.auth.Subject;
import java.rmi.MarshalledObject;
import javax.management.ObjectName;
import javax.rmi.CORBA.Stub;

public class _RMIConnection_Stub extends Stub implements RMIConnection
{
    private static final String[] _type_ids;
    private transient boolean _instantiated;
    static /* synthetic */ Class class$java$io$IOException;
    static /* synthetic */ Class class$javax$management$remote$rmi$RMIConnection;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$javax$management$ObjectName;
    static /* synthetic */ Class class$javax$security$auth$Subject;
    static /* synthetic */ Class class$javax$management$ObjectInstance;
    static /* synthetic */ Class class$javax$management$ReflectionException;
    static /* synthetic */ Class class$javax$management$InstanceAlreadyExistsException;
    static /* synthetic */ Class class$javax$management$MBeanRegistrationException;
    static /* synthetic */ Class class$javax$management$MBeanException;
    static /* synthetic */ Class class$javax$management$NotCompliantMBeanException;
    static /* synthetic */ Class class$javax$management$InstanceNotFoundException;
    static /* synthetic */ Class class$java$rmi$MarshalledObject;
    static /* synthetic */ Class array$Ljava$lang$String;
    static /* synthetic */ Class class$java$util$Set;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$javax$management$AttributeNotFoundException;
    static /* synthetic */ Class class$javax$management$AttributeList;
    static /* synthetic */ Class class$javax$management$InvalidAttributeValueException;
    static /* synthetic */ Class class$javax$management$MBeanInfo;
    static /* synthetic */ Class class$javax$management$IntrospectionException;
    static /* synthetic */ Class class$javax$management$ListenerNotFoundException;
    static /* synthetic */ Class array$Ljavax$management$ObjectName;
    static /* synthetic */ Class array$Ljava$rmi$MarshalledObject;
    static /* synthetic */ Class array$Ljavax$security$auth$Subject;
    static /* synthetic */ Class array$Ljava$lang$Integer;
    static /* synthetic */ Class class$javax$management$remote$NotificationResult;
    
    static {
        _type_ids = new String[] { "RMI:javax.management.remote.rmi.RMIConnection:0000000000000000" };
    }
    
    public _RMIConnection_Stub() {
        this(checkPermission());
        this._instantiated = true;
    }
    
    private _RMIConnection_Stub(final Void void1) {
        this._instantiated = false;
    }
    
    public String[] _ids() {
        return _RMIConnection_Stub._type_ids.clone();
    }
    
    public void addNotificationListener(final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final MarshalledObject marshalledObject2, final Subject subject) throws InstanceNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("addNotificationListener", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(objectName2, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(marshalledObject2, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    this._invoke(outputStream);
                }
                catch (final ApplicationException ex) {
                    inputStream = ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    this.addNotificationListener(objectName, objectName2, marshalledObject, marshalledObject2, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("addNotificationListener", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            this.addNotificationListener(objectName, objectName2, marshalledObject, marshalledObject2, subject);
            return;
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, objectName2, marshalledObject, marshalledObject2, subject }, this._orb());
            ((RMIConnection)servant_preinvoke.servant).addNotificationListener((ObjectName)copyObjects[0], (ObjectName)copyObjects[1], (MarshalledObject)copyObjects[2], (MarshalledObject)copyObjects[3], (Subject)copyObjects[4]);
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public Integer[] addNotificationListeners(final ObjectName[] array, final MarshalledObject[] array2, final Subject[] array3) throws InstanceNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("addNotificationListeners", true);
                    outputStream.write_value(this.cast_array(array), (_RMIConnection_Stub.array$Ljavax$management$ObjectName != null) ? _RMIConnection_Stub.array$Ljavax$management$ObjectName : (_RMIConnection_Stub.array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")));
                    outputStream.write_value(this.cast_array(array2), (_RMIConnection_Stub.array$Ljava$rmi$MarshalledObject != null) ? _RMIConnection_Stub.array$Ljava$rmi$MarshalledObject : (_RMIConnection_Stub.array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")));
                    outputStream.write_value(this.cast_array(array3), (_RMIConnection_Stub.array$Ljavax$security$auth$Subject != null) ? _RMIConnection_Stub.array$Ljavax$security$auth$Subject : (_RMIConnection_Stub.array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (Integer[])(Object)inputStream.read_value((_RMIConnection_Stub.array$Ljava$lang$Integer != null) ? _RMIConnection_Stub.array$Ljava$lang$Integer : (_RMIConnection_Stub.array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.addNotificationListeners(array, array2, array3);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("addNotificationListeners", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.addNotificationListeners(array, array2, array3);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { array, array2, array3 }, this._orb());
            return (Integer[])Util.copyObject(((RMIConnection)servant_preinvoke.servant).addNotificationListeners((ObjectName[])copyObjects[0], (MarshalledObject[])copyObjects[1], (Subject[])copyObjects[2]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    private Serializable cast_array(final Object o) {
        return (Serializable)o;
    }
    
    private static Void checkPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
        }
        return null;
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
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    this._invoke(this._request("close", true));
                }
                catch (final ApplicationException ex) {
                    inputStream = ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    this.close();
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("close", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            this.close();
            return;
        }
        try {
            ((RMIConnection)servant_preinvoke.servant).close();
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", true);
                    outputStream.write_value(s, (_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(this.cast_array(array), (_RMIConnection_Stub.array$Ljava$lang$String != null) ? _RMIConnection_Stub.array$Ljava$lang$String : (_RMIConnection_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (ObjectInstance)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ObjectInstance != null) ? _RMIConnection_Stub.class$javax$management$ObjectInstance : (_RMIConnection_Stub.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0")) {
                        throw (InstanceAlreadyExistsException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException != null) ? _RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException : (_RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                        throw (MBeanRegistrationException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanRegistrationException != null) ? _RMIConnection_Stub.class$javax$management$MBeanRegistrationException : (_RMIConnection_Stub.class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanEx:1.0")) {
                        throw (MBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanException != null) ? _RMIConnection_Stub.class$javax$management$MBeanException : (_RMIConnection_Stub.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/NotCompliantMBeanEx:1.0")) {
                        throw (NotCompliantMBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$NotCompliantMBeanException != null) ? _RMIConnection_Stub.class$javax$management$NotCompliantMBeanException : (_RMIConnection_Stub.class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.createMBean(s, objectName, marshalledObject, array, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.createMBean(s, objectName, marshalledObject, array, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { s, objectName, marshalledObject, array, subject }, this._orb());
            return (ObjectInstance)Util.copyObject(((RMIConnection)servant_preinvoke.servant).createMBean((String)copyObjects[0], (ObjectName)copyObjects[1], (MarshalledObject)copyObjects[2], (String[])copyObjects[3], (Subject)copyObjects[4]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof InstanceAlreadyExistsException) {
                throw (InstanceAlreadyExistsException)t2;
            }
            if (t2 instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)t2;
            }
            if (t2 instanceof MBeanException) {
                throw (MBeanException)t2;
            }
            if (t2 instanceof NotCompliantMBeanException) {
                throw (NotCompliantMBeanException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", true);
                    outputStream.write_value(s, (_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(objectName2, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(this.cast_array(array), (_RMIConnection_Stub.array$Ljava$lang$String != null) ? _RMIConnection_Stub.array$Ljava$lang$String : (_RMIConnection_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (ObjectInstance)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ObjectInstance != null) ? _RMIConnection_Stub.class$javax$management$ObjectInstance : (_RMIConnection_Stub.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0")) {
                        throw (InstanceAlreadyExistsException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException != null) ? _RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException : (_RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                        throw (MBeanRegistrationException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanRegistrationException != null) ? _RMIConnection_Stub.class$javax$management$MBeanRegistrationException : (_RMIConnection_Stub.class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanEx:1.0")) {
                        throw (MBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanException != null) ? _RMIConnection_Stub.class$javax$management$MBeanException : (_RMIConnection_Stub.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/NotCompliantMBeanEx:1.0")) {
                        throw (NotCompliantMBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$NotCompliantMBeanException != null) ? _RMIConnection_Stub.class$javax$management$NotCompliantMBeanException : (_RMIConnection_Stub.class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.createMBean(s, objectName, objectName2, marshalledObject, array, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.createMBean(s, objectName, objectName2, marshalledObject, array, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { s, objectName, objectName2, marshalledObject, array, subject }, this._orb());
            return (ObjectInstance)Util.copyObject(((RMIConnection)servant_preinvoke.servant).createMBean((String)copyObjects[0], (ObjectName)copyObjects[1], (ObjectName)copyObjects[2], (MarshalledObject)copyObjects[3], (String[])copyObjects[4], (Subject)copyObjects[5]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof InstanceAlreadyExistsException) {
                throw (InstanceAlreadyExistsException)t2;
            }
            if (t2 instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)t2;
            }
            if (t2 instanceof MBeanException) {
                throw (MBeanException)t2;
            }
            if (t2 instanceof NotCompliantMBeanException) {
                throw (NotCompliantMBeanException)t2;
            }
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final Subject subject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", true);
                    outputStream.write_value(s, (_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(objectName2, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (ObjectInstance)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ObjectInstance != null) ? _RMIConnection_Stub.class$javax$management$ObjectInstance : (_RMIConnection_Stub.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0")) {
                        throw (InstanceAlreadyExistsException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException != null) ? _RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException : (_RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                        throw (MBeanRegistrationException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanRegistrationException != null) ? _RMIConnection_Stub.class$javax$management$MBeanRegistrationException : (_RMIConnection_Stub.class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanEx:1.0")) {
                        throw (MBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanException != null) ? _RMIConnection_Stub.class$javax$management$MBeanException : (_RMIConnection_Stub.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/NotCompliantMBeanEx:1.0")) {
                        throw (NotCompliantMBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$NotCompliantMBeanException != null) ? _RMIConnection_Stub.class$javax$management$NotCompliantMBeanException : (_RMIConnection_Stub.class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.createMBean(s, objectName, objectName2, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.createMBean(s, objectName, objectName2, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { s, objectName, objectName2, subject }, this._orb());
            return (ObjectInstance)Util.copyObject(((RMIConnection)servant_preinvoke.servant).createMBean((String)copyObjects[0], (ObjectName)copyObjects[1], (ObjectName)copyObjects[2], (Subject)copyObjects[3]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof InstanceAlreadyExistsException) {
                throw (InstanceAlreadyExistsException)t2;
            }
            if (t2 instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)t2;
            }
            if (t2 instanceof MBeanException) {
                throw (MBeanException)t2;
            }
            if (t2 instanceof NotCompliantMBeanException) {
                throw (NotCompliantMBeanException)t2;
            }
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final Subject subject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject", true);
                    outputStream.write_value(s, (_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (ObjectInstance)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ObjectInstance != null) ? _RMIConnection_Stub.class$javax$management$ObjectInstance : (_RMIConnection_Stub.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0")) {
                        throw (InstanceAlreadyExistsException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException != null) ? _RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException : (_RMIConnection_Stub.class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                        throw (MBeanRegistrationException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanRegistrationException != null) ? _RMIConnection_Stub.class$javax$management$MBeanRegistrationException : (_RMIConnection_Stub.class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanEx:1.0")) {
                        throw (MBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanException != null) ? _RMIConnection_Stub.class$javax$management$MBeanException : (_RMIConnection_Stub.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/NotCompliantMBeanEx:1.0")) {
                        throw (NotCompliantMBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$NotCompliantMBeanException != null) ? _RMIConnection_Stub.class$javax$management$NotCompliantMBeanException : (_RMIConnection_Stub.class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.createMBean(s, objectName, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.createMBean(s, objectName, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { s, objectName, subject }, this._orb());
            return (ObjectInstance)Util.copyObject(((RMIConnection)servant_preinvoke.servant).createMBean((String)copyObjects[0], (ObjectName)copyObjects[1], (Subject)copyObjects[2]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof InstanceAlreadyExistsException) {
                throw (InstanceAlreadyExistsException)t2;
            }
            if (t2 instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)t2;
            }
            if (t2 instanceof MBeanException) {
                throw (MBeanException)t2;
            }
            if (t2 instanceof NotCompliantMBeanException) {
                throw (NotCompliantMBeanException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public NotificationResult fetchNotifications(final long n, final int n2, final long n3) throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final org.omg.CORBA.portable.OutputStream request = this._request("fetchNotifications", true);
                    request.write_longlong(n);
                    request.write_long(n2);
                    request.write_longlong(n3);
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(request);
                    return (NotificationResult)inputStream.read_value((_RMIConnection_Stub.class$javax$management$remote$NotificationResult != null) ? _RMIConnection_Stub.class$javax$management$remote$NotificationResult : (_RMIConnection_Stub.class$javax$management$remote$NotificationResult = class$("javax.management.remote.NotificationResult")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.fetchNotifications(n, n2, n3);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("fetchNotifications", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.fetchNotifications(n, n2, n3);
        }
        try {
            return (NotificationResult)Util.copyObject(((RMIConnection)servant_preinvoke.servant).fetchNotifications(n, n2, n3), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public Object getAttribute(final ObjectName objectName, final String s, final Subject subject) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("getAttribute", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(s, (_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return Util.readAny(inputStream);
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/MBeanEx:1.0")) {
                        throw (MBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanException != null) ? _RMIConnection_Stub.class$javax$management$MBeanException : (_RMIConnection_Stub.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/AttributeNotFoundEx:1.0")) {
                        throw (AttributeNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$AttributeNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$AttributeNotFoundException : (_RMIConnection_Stub.class$javax$management$AttributeNotFoundException = class$("javax.management.AttributeNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.getAttribute(objectName, s, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("getAttribute", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.getAttribute(objectName, s, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, s, subject }, this._orb());
            return Util.copyObject(((RMIConnection)servant_preinvoke.servant).getAttribute((ObjectName)copyObjects[0], (String)copyObjects[1], (Subject)copyObjects[2]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof MBeanException) {
                throw (MBeanException)t2;
            }
            if (t2 instanceof AttributeNotFoundException) {
                throw (AttributeNotFoundException)t2;
            }
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public AttributeList getAttributes(final ObjectName objectName, final String[] array, final Subject subject) throws InstanceNotFoundException, ReflectionException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("getAttributes", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(this.cast_array(array), (_RMIConnection_Stub.array$Ljava$lang$String != null) ? _RMIConnection_Stub.array$Ljava$lang$String : (_RMIConnection_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (AttributeList)inputStream.read_value((_RMIConnection_Stub.class$javax$management$AttributeList != null) ? _RMIConnection_Stub.class$javax$management$AttributeList : (_RMIConnection_Stub.class$javax$management$AttributeList = class$("javax.management.AttributeList")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.getAttributes(objectName, array, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("getAttributes", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.getAttributes(objectName, array, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, array, subject }, this._orb());
            return (AttributeList)Util.copyObject(((RMIConnection)servant_preinvoke.servant).getAttributes((ObjectName)copyObjects[0], (String[])copyObjects[1], (Subject)copyObjects[2]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public String getConnectionId() throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(this._request("getConnectionId", true));
                    return (String)inputStream.read_value((_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.getConnectionId();
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("getConnectionId", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.getConnectionId();
        }
        try {
            return ((RMIConnection)servant_preinvoke.servant).getConnectionId();
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public String getDefaultDomain(final Subject subject) throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("getDefaultDomain", true);
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (String)inputStream.read_value((_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.getDefaultDomain(subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("getDefaultDomain", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.getDefaultDomain(subject);
        }
        try {
            return ((RMIConnection)servant_preinvoke.servant).getDefaultDomain((Subject)Util.copyObject(subject, this._orb()));
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public String[] getDomains(final Subject subject) throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("getDomains", true);
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (String[])(Object)inputStream.read_value((_RMIConnection_Stub.array$Ljava$lang$String != null) ? _RMIConnection_Stub.array$Ljava$lang$String : (_RMIConnection_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.getDomains(subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("getDomains", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.getDomains(subject);
        }
        try {
            return (String[])Util.copyObject(((RMIConnection)servant_preinvoke.servant).getDomains((Subject)Util.copyObject(subject, this._orb())), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public Integer getMBeanCount(final Subject subject) throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("getMBeanCount", true);
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (Integer)inputStream.read_value((_RMIConnection_Stub.class$java$lang$Integer != null) ? _RMIConnection_Stub.class$java$lang$Integer : (_RMIConnection_Stub.class$java$lang$Integer = class$("java.lang.Integer")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.getMBeanCount(subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("getMBeanCount", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.getMBeanCount(subject);
        }
        try {
            return (Integer)Util.copyObject(((RMIConnection)servant_preinvoke.servant).getMBeanCount((Subject)Util.copyObject(subject, this._orb())), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public MBeanInfo getMBeanInfo(final ObjectName objectName, final Subject subject) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("getMBeanInfo", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (MBeanInfo)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanInfo != null) ? _RMIConnection_Stub.class$javax$management$MBeanInfo : (_RMIConnection_Stub.class$javax$management$MBeanInfo = class$("javax.management.MBeanInfo")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/IntrospectionEx:1.0")) {
                        throw (IntrospectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$IntrospectionException != null) ? _RMIConnection_Stub.class$javax$management$IntrospectionException : (_RMIConnection_Stub.class$javax$management$IntrospectionException = class$("javax.management.IntrospectionException")));
                    }
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.getMBeanInfo(objectName, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("getMBeanInfo", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.getMBeanInfo(objectName, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, subject }, this._orb());
            return (MBeanInfo)Util.copyObject(((RMIConnection)servant_preinvoke.servant).getMBeanInfo((ObjectName)copyObjects[0], (Subject)copyObjects[1]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof IntrospectionException) {
                throw (IntrospectionException)t2;
            }
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public ObjectInstance getObjectInstance(final ObjectName objectName, final Subject subject) throws InstanceNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("getObjectInstance", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (ObjectInstance)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ObjectInstance != null) ? _RMIConnection_Stub.class$javax$management$ObjectInstance : (_RMIConnection_Stub.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.getObjectInstance(objectName, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("getObjectInstance", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.getObjectInstance(objectName, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, subject }, this._orb());
            return (ObjectInstance)Util.copyObject(((RMIConnection)servant_preinvoke.servant).getObjectInstance((ObjectName)copyObjects[0], (Subject)copyObjects[1]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public Object invoke(final ObjectName objectName, final String s, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("invoke", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(s, (_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(this.cast_array(array), (_RMIConnection_Stub.array$Ljava$lang$String != null) ? _RMIConnection_Stub.array$Ljava$lang$String : (_RMIConnection_Stub.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return Util.readAny(inputStream);
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanEx:1.0")) {
                        throw (MBeanException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$MBeanException != null) ? _RMIConnection_Stub.class$javax$management$MBeanException : (_RMIConnection_Stub.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.invoke(objectName, s, marshalledObject, array, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("invoke", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.invoke(objectName, s, marshalledObject, array, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, s, marshalledObject, array, subject }, this._orb());
            return Util.copyObject(((RMIConnection)servant_preinvoke.servant).invoke((ObjectName)copyObjects[0], (String)copyObjects[1], (MarshalledObject)copyObjects[2], (String[])copyObjects[3], (Subject)copyObjects[4]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof MBeanException) {
                throw (MBeanException)t2;
            }
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public boolean isInstanceOf(final ObjectName objectName, final String s, final Subject subject) throws InstanceNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("isInstanceOf", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(s, (_RMIConnection_Stub.class$java$lang$String != null) ? _RMIConnection_Stub.class$java$lang$String : (_RMIConnection_Stub.class$java$lang$String = class$("java.lang.String")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return inputStream.read_boolean();
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.isInstanceOf(objectName, s, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("isInstanceOf", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.isInstanceOf(objectName, s, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, s, subject }, this._orb());
            return ((RMIConnection)servant_preinvoke.servant).isInstanceOf((ObjectName)copyObjects[0], (String)copyObjects[1], (Subject)copyObjects[2]);
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public boolean isRegistered(final ObjectName objectName, final Subject subject) throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("isRegistered", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return inputStream.read_boolean();
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.isRegistered(objectName, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("isRegistered", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.isRegistered(objectName, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, subject }, this._orb());
            return ((RMIConnection)servant_preinvoke.servant).isRegistered((ObjectName)copyObjects[0], (Subject)copyObjects[1]);
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public Set queryMBeans(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("queryMBeans", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (Set)inputStream.read_value((_RMIConnection_Stub.class$java$util$Set != null) ? _RMIConnection_Stub.class$java$util$Set : (_RMIConnection_Stub.class$java$util$Set = class$("java.util.Set")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.queryMBeans(objectName, marshalledObject, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("queryMBeans", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.queryMBeans(objectName, marshalledObject, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, marshalledObject, subject }, this._orb());
            return (Set)Util.copyObject(((RMIConnection)servant_preinvoke.servant).queryMBeans((ObjectName)copyObjects[0], (MarshalledObject)copyObjects[1], (Subject)copyObjects[2]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public Set queryNames(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("queryNames", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (Set)inputStream.read_value((_RMIConnection_Stub.class$java$util$Set != null) ? _RMIConnection_Stub.class$java$util$Set : (_RMIConnection_Stub.class$java$util$Set = class$("java.util.Set")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.queryNames(objectName, marshalledObject, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("queryNames", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.queryNames(objectName, marshalledObject, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, marshalledObject, subject }, this._orb());
            return (Set)Util.copyObject(((RMIConnection)servant_preinvoke.servant).queryNames((ObjectName)copyObjects[0], (MarshalledObject)copyObjects[1], (Subject)copyObjects[2]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        checkPermission();
        objectInputStream.defaultReadObject();
        this._instantiated = true;
    }
    
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final MarshalledObject marshalledObject2, final Subject subject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(objectName2, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(marshalledObject2, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    this._invoke(outputStream);
                }
                catch (final ApplicationException ex) {
                    inputStream = ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/ListenerNotFoundEx:1.0")) {
                        throw (ListenerNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$ListenerNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$ListenerNotFoundException : (_RMIConnection_Stub.class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    this.removeNotificationListener(objectName, objectName2, marshalledObject, marshalledObject2, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            this.removeNotificationListener(objectName, objectName2, marshalledObject, marshalledObject2, subject);
            return;
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, objectName2, marshalledObject, marshalledObject2, subject }, this._orb());
            ((RMIConnection)servant_preinvoke.servant).removeNotificationListener((ObjectName)copyObjects[0], (ObjectName)copyObjects[1], (MarshalledObject)copyObjects[2], (MarshalledObject)copyObjects[3], (Subject)copyObjects[4]);
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof ListenerNotFoundException) {
                throw (ListenerNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final Subject subject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(objectName2, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    this._invoke(outputStream);
                }
                catch (final ApplicationException ex) {
                    inputStream = ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/ListenerNotFoundEx:1.0")) {
                        throw (ListenerNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$ListenerNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$ListenerNotFoundException : (_RMIConnection_Stub.class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    this.removeNotificationListener(objectName, objectName2, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            this.removeNotificationListener(objectName, objectName2, subject);
            return;
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, objectName2, subject }, this._orb());
            ((RMIConnection)servant_preinvoke.servant).removeNotificationListener((ObjectName)copyObjects[0], (ObjectName)copyObjects[1], (Subject)copyObjects[2]);
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof ListenerNotFoundException) {
                throw (ListenerNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public void removeNotificationListeners(final ObjectName objectName, final Integer[] array, final Subject subject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("removeNotificationListeners", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(this.cast_array(array), (_RMIConnection_Stub.array$Ljava$lang$Integer != null) ? _RMIConnection_Stub.array$Ljava$lang$Integer : (_RMIConnection_Stub.array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    this._invoke(outputStream);
                }
                catch (final ApplicationException ex) {
                    inputStream = ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/ListenerNotFoundEx:1.0")) {
                        throw (ListenerNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$ListenerNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$ListenerNotFoundException : (_RMIConnection_Stub.class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    this.removeNotificationListeners(objectName, array, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("removeNotificationListeners", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            this.removeNotificationListeners(objectName, array, subject);
            return;
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, array, subject }, this._orb());
            ((RMIConnection)servant_preinvoke.servant).removeNotificationListeners((ObjectName)copyObjects[0], (Integer[])copyObjects[1], (Subject)copyObjects[2]);
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof ListenerNotFoundException) {
                throw (ListenerNotFoundException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public void setAttribute(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("setAttribute", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    this._invoke(outputStream);
                }
                catch (final ApplicationException ex) {
                    inputStream = ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/AttributeNotFoundEx:1.0")) {
                        throw (AttributeNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$AttributeNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$AttributeNotFoundException : (_RMIConnection_Stub.class$javax$management$AttributeNotFoundException = class$("javax.management.AttributeNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/InvalidAttributeValueEx:1.0")) {
                        throw (InvalidAttributeValueException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$InvalidAttributeValueException != null) ? _RMIConnection_Stub.class$javax$management$InvalidAttributeValueException : (_RMIConnection_Stub.class$javax$management$InvalidAttributeValueException = class$("javax.management.InvalidAttributeValueException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanEx:1.0")) {
                        throw (MBeanException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$MBeanException != null) ? _RMIConnection_Stub.class$javax$management$MBeanException : (_RMIConnection_Stub.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                    }
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    this.setAttribute(objectName, marshalledObject, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("setAttribute", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            this.setAttribute(objectName, marshalledObject, subject);
            return;
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, marshalledObject, subject }, this._orb());
            ((RMIConnection)servant_preinvoke.servant).setAttribute((ObjectName)copyObjects[0], (MarshalledObject)copyObjects[1], (Subject)copyObjects[2]);
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof AttributeNotFoundException) {
                throw (AttributeNotFoundException)t2;
            }
            if (t2 instanceof InvalidAttributeValueException) {
                throw (InvalidAttributeValueException)t2;
            }
            if (t2 instanceof MBeanException) {
                throw (MBeanException)t2;
            }
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public AttributeList setAttributes(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws InstanceNotFoundException, ReflectionException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                org.omg.CORBA_2_3.portable.InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("setAttributes", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(marshalledObject, (_RMIConnection_Stub.class$java$rmi$MarshalledObject != null) ? _RMIConnection_Stub.class$java$rmi$MarshalledObject : (_RMIConnection_Stub.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)this._invoke(outputStream);
                    return (AttributeList)inputStream.read_value((_RMIConnection_Stub.class$javax$management$AttributeList != null) ? _RMIConnection_Stub.class$javax$management$AttributeList : (_RMIConnection_Stub.class$javax$management$AttributeList = class$("javax.management.AttributeList")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (org.omg.CORBA_2_3.portable.InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/ReflectionEx:1.0")) {
                        throw (ReflectionException)inputStream.read_value((_RMIConnection_Stub.class$javax$management$ReflectionException != null) ? _RMIConnection_Stub.class$javax$management$ReflectionException : (_RMIConnection_Stub.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.setAttributes(objectName, marshalledObject, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("setAttributes", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            return this.setAttributes(objectName, marshalledObject, subject);
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, marshalledObject, subject }, this._orb());
            return (AttributeList)Util.copyObject(((RMIConnection)servant_preinvoke.servant).setAttributes((ObjectName)copyObjects[0], (MarshalledObject)copyObjects[1], (Subject)copyObjects[2]), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof ReflectionException) {
                throw (ReflectionException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public void unregisterMBean(final ObjectName objectName, final Subject subject) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)this._request("unregisterMBean", true);
                    outputStream.write_value(objectName, (_RMIConnection_Stub.class$javax$management$ObjectName != null) ? _RMIConnection_Stub.class$javax$management$ObjectName : (_RMIConnection_Stub.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                    outputStream.write_value(subject, (_RMIConnection_Stub.class$javax$security$auth$Subject != null) ? _RMIConnection_Stub.class$javax$security$auth$Subject : (_RMIConnection_Stub.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                    this._invoke(outputStream);
                }
                catch (final ApplicationException ex) {
                    inputStream = ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                        throw (InstanceNotFoundException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$InstanceNotFoundException != null) ? _RMIConnection_Stub.class$javax$management$InstanceNotFoundException : (_RMIConnection_Stub.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                    }
                    if (read_string.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                        throw (MBeanRegistrationException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$javax$management$MBeanRegistrationException != null) ? _RMIConnection_Stub.class$javax$management$MBeanRegistrationException : (_RMIConnection_Stub.class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                    }
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value((_RMIConnection_Stub.class$java$io$IOException != null) ? _RMIConnection_Stub.class$java$io$IOException : (_RMIConnection_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    this.unregisterMBean(objectName, subject);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("unregisterMBean", (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIConnection_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
        if (servant_preinvoke == null) {
            this.unregisterMBean(objectName, subject);
            return;
        }
        try {
            final Object[] copyObjects = Util.copyObjects(new Object[] { objectName, subject }, this._orb());
            ((RMIConnection)servant_preinvoke.servant).unregisterMBean((ObjectName)copyObjects[0], (Subject)copyObjects[1]);
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)t2;
            }
            if (t2 instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)t2;
            }
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
}
