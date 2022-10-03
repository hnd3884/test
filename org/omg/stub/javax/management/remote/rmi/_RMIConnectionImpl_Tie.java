package org.omg.stub.javax.management.remote.rmi;

import org.omg.CORBA.ORB;
import java.rmi.Remote;
import org.omg.CORBA.portable.Delegate;
import java.util.Set;
import javax.management.remote.NotificationResult;
import javax.management.ObjectInstance;
import javax.management.MBeanInfo;
import javax.management.AttributeList;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.BAD_OPERATION;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import java.rmi.MarshalledObject;
import javax.rmi.CORBA.Util;
import javax.management.ReflectionException;
import javax.management.InstanceNotFoundException;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import java.io.Serializable;
import javax.security.auth.Subject;
import javax.management.ObjectName;
import java.io.IOException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import javax.management.remote.rmi.RMIConnectionImpl;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class _RMIConnectionImpl_Tie extends ObjectImpl implements Tie
{
    private volatile RMIConnectionImpl target;
    private static final String[] _type_ids;
    static /* synthetic */ Class class$javax$management$ObjectName;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$javax$security$auth$Subject;
    static /* synthetic */ Class class$javax$management$MBeanException;
    static /* synthetic */ Class class$javax$management$AttributeNotFoundException;
    static /* synthetic */ Class class$javax$management$InstanceNotFoundException;
    static /* synthetic */ Class class$javax$management$ReflectionException;
    static /* synthetic */ Class class$java$io$IOException;
    static /* synthetic */ Class array$Ljava$lang$String;
    static /* synthetic */ Class class$javax$management$AttributeList;
    static /* synthetic */ Class class$java$rmi$MarshalledObject;
    static /* synthetic */ Class class$javax$management$InvalidAttributeValueException;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$javax$management$IntrospectionException;
    static /* synthetic */ Class class$javax$management$MBeanInfo;
    static /* synthetic */ Class array$Ljavax$management$ObjectName;
    static /* synthetic */ Class array$Ljava$rmi$MarshalledObject;
    static /* synthetic */ Class array$Ljavax$security$auth$Subject;
    static /* synthetic */ Class array$Ljava$lang$Integer;
    static /* synthetic */ Class class$javax$management$ObjectInstance;
    static /* synthetic */ Class class$javax$management$InstanceAlreadyExistsException;
    static /* synthetic */ Class class$javax$management$NotCompliantMBeanException;
    static /* synthetic */ Class class$javax$management$remote$NotificationResult;
    static /* synthetic */ Class class$javax$management$MBeanRegistrationException;
    static /* synthetic */ Class class$javax$management$ListenerNotFoundException;
    static /* synthetic */ Class class$java$util$Set;
    
    static {
        _type_ids = new String[] { "RMI:javax.management.remote.rmi.RMIConnection:0000000000000000" };
    }
    
    public _RMIConnectionImpl_Tie() {
        this.target = null;
    }
    
    public String[] _ids() {
        return _RMIConnectionImpl_Tie._type_ids.clone();
    }
    
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) throws SystemException {
        try {
            final RMIConnectionImpl target = this.target;
            if (target == null) {
                throw new IOException();
            }
            final org.omg.CORBA_2_3.portable.InputStream inputStream2 = (org.omg.CORBA_2_3.portable.InputStream)inputStream;
            switch (s.charAt(3)) {
                case 'A': {
                    if (s.equals("getAttribute")) {
                        final ObjectName objectName = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final String s2 = (String)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        final Subject subject = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        Object attribute;
                        try {
                            attribute = target.getAttribute(objectName, s2, subject);
                        }
                        catch (final MBeanException ex) {
                            final String s3 = "IDL:javax/management/MBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream.write_string(s3);
                            outputStream.write_value(ex, (_RMIConnectionImpl_Tie.class$javax$management$MBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanException : (_RMIConnectionImpl_Tie.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                            return outputStream;
                        }
                        catch (final AttributeNotFoundException ex2) {
                            final String s4 = "IDL:javax/management/AttributeNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream2 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream2.write_string(s4);
                            outputStream2.write_value(ex2, (_RMIConnectionImpl_Tie.class$javax$management$AttributeNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$AttributeNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$AttributeNotFoundException = class$("javax.management.AttributeNotFoundException")));
                            return outputStream2;
                        }
                        catch (final InstanceNotFoundException ex3) {
                            final String s5 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream3 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream3.write_string(s5);
                            outputStream3.write_value(ex3, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream3;
                        }
                        catch (final ReflectionException ex4) {
                            final String s6 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream4 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream4.write_string(s6);
                            outputStream4.write_value(ex4, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream4;
                        }
                        catch (final IOException ex5) {
                            final String s7 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream5 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream5.write_string(s7);
                            outputStream5.write_value(ex5, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream5;
                        }
                        final OutputStream reply = responseHandler.createReply();
                        Util.writeAny(reply, attribute);
                        return reply;
                    }
                    if (s.equals("getAttributes")) {
                        final ObjectName objectName2 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final String[] array = (Object)inputStream2.read_value((_RMIConnectionImpl_Tie.array$Ljava$lang$String != null) ? _RMIConnectionImpl_Tie.array$Ljava$lang$String : (_RMIConnectionImpl_Tie.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                        final Subject subject2 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        AttributeList attributes;
                        try {
                            attributes = target.getAttributes(objectName2, array, subject2);
                        }
                        catch (final InstanceNotFoundException ex6) {
                            final String s8 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream6 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream6.write_string(s8);
                            outputStream6.write_value(ex6, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream6;
                        }
                        catch (final ReflectionException ex7) {
                            final String s9 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream7 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream7.write_string(s9);
                            outputStream7.write_value(ex7, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream7;
                        }
                        catch (final IOException ex8) {
                            final String s10 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream8 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream8.write_string(s10);
                            outputStream8.write_value(ex8, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream8;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream9 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream9.write_value(attributes, (_RMIConnectionImpl_Tie.class$javax$management$AttributeList != null) ? _RMIConnectionImpl_Tie.class$javax$management$AttributeList : (_RMIConnectionImpl_Tie.class$javax$management$AttributeList = class$("javax.management.AttributeList")));
                        return outputStream9;
                    }
                    if (s.equals("setAttribute")) {
                        final ObjectName objectName3 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final MarshalledObject marshalledObject = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final Subject subject3 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        try {
                            target.setAttribute(objectName3, marshalledObject, subject3);
                        }
                        catch (final InstanceNotFoundException ex9) {
                            final String s11 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream10 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream10.write_string(s11);
                            outputStream10.write_value(ex9, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream10;
                        }
                        catch (final AttributeNotFoundException ex10) {
                            final String s12 = "IDL:javax/management/AttributeNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream11 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream11.write_string(s12);
                            outputStream11.write_value(ex10, (_RMIConnectionImpl_Tie.class$javax$management$AttributeNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$AttributeNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$AttributeNotFoundException = class$("javax.management.AttributeNotFoundException")));
                            return outputStream11;
                        }
                        catch (final InvalidAttributeValueException ex11) {
                            final String s13 = "IDL:javax/management/InvalidAttributeValueEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream12 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream12.write_string(s13);
                            outputStream12.write_value(ex11, (_RMIConnectionImpl_Tie.class$javax$management$InvalidAttributeValueException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InvalidAttributeValueException : (_RMIConnectionImpl_Tie.class$javax$management$InvalidAttributeValueException = class$("javax.management.InvalidAttributeValueException")));
                            return outputStream12;
                        }
                        catch (final MBeanException ex12) {
                            final String s14 = "IDL:javax/management/MBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream13 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream13.write_string(s14);
                            outputStream13.write_value(ex12, (_RMIConnectionImpl_Tie.class$javax$management$MBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanException : (_RMIConnectionImpl_Tie.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                            return outputStream13;
                        }
                        catch (final ReflectionException ex13) {
                            final String s15 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream14 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream14.write_string(s15);
                            outputStream14.write_value(ex13, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream14;
                        }
                        catch (final IOException ex14) {
                            final String s16 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream15 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream15.write_string(s16);
                            outputStream15.write_value(ex14, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream15;
                        }
                        return responseHandler.createReply();
                    }
                    if (s.equals("setAttributes")) {
                        final ObjectName objectName4 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final MarshalledObject marshalledObject2 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final Subject subject4 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        AttributeList setAttributes;
                        try {
                            setAttributes = target.setAttributes(objectName4, marshalledObject2, subject4);
                        }
                        catch (final InstanceNotFoundException ex15) {
                            final String s17 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream16 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream16.write_string(s17);
                            outputStream16.write_value(ex15, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream16;
                        }
                        catch (final ReflectionException ex16) {
                            final String s18 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream17 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream17.write_string(s18);
                            outputStream17.write_value(ex16, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream17;
                        }
                        catch (final IOException ex17) {
                            final String s19 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream18 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream18.write_string(s19);
                            outputStream18.write_value(ex17, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream18;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream19 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream19.write_value(setAttributes, (_RMIConnectionImpl_Tie.class$javax$management$AttributeList != null) ? _RMIConnectionImpl_Tie.class$javax$management$AttributeList : (_RMIConnectionImpl_Tie.class$javax$management$AttributeList = class$("javax.management.AttributeList")));
                        return outputStream19;
                    }
                }
                case 'C': {
                    if (s.equals("getConnectionId")) {
                        String connectionId;
                        try {
                            connectionId = target.getConnectionId();
                        }
                        catch (final IOException ex18) {
                            final String s20 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream20 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream20.write_string(s20);
                            outputStream20.write_value(ex18, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream20;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream21 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream21.write_value(connectionId, (_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        return outputStream21;
                    }
                }
                case 'D': {
                    if (s.equals("getDefaultDomain")) {
                        final Subject subject5 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        String defaultDomain;
                        try {
                            defaultDomain = target.getDefaultDomain(subject5);
                        }
                        catch (final IOException ex19) {
                            final String s21 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream22 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream22.write_string(s21);
                            outputStream22.write_value(ex19, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream22;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream23 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream23.write_value(defaultDomain, (_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        return outputStream23;
                    }
                    if (s.equals("getDomains")) {
                        final Subject subject6 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        String[] domains;
                        try {
                            domains = target.getDomains(subject6);
                        }
                        catch (final IOException ex20) {
                            final String s22 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream24 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream24.write_string(s22);
                            outputStream24.write_value(ex20, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream24;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream25 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream25.write_value(this.cast_array(domains), (_RMIConnectionImpl_Tie.array$Ljava$lang$String != null) ? _RMIConnectionImpl_Tie.array$Ljava$lang$String : (_RMIConnectionImpl_Tie.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                        return outputStream25;
                    }
                }
                case 'M': {
                    if (s.equals("getMBeanCount")) {
                        final Subject subject7 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        Integer mBeanCount;
                        try {
                            mBeanCount = target.getMBeanCount(subject7);
                        }
                        catch (final IOException ex21) {
                            final String s23 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream26 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream26.write_string(s23);
                            outputStream26.write_value(ex21, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream26;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream27 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream27.write_value(mBeanCount, (_RMIConnectionImpl_Tie.class$java$lang$Integer != null) ? _RMIConnectionImpl_Tie.class$java$lang$Integer : (_RMIConnectionImpl_Tie.class$java$lang$Integer = class$("java.lang.Integer")));
                        return outputStream27;
                    }
                    if (s.equals("getMBeanInfo")) {
                        final ObjectName objectName5 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final Subject subject8 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        MBeanInfo mBeanInfo;
                        try {
                            mBeanInfo = target.getMBeanInfo(objectName5, subject8);
                        }
                        catch (final InstanceNotFoundException ex22) {
                            final String s24 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream28 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream28.write_string(s24);
                            outputStream28.write_value(ex22, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream28;
                        }
                        catch (final IntrospectionException ex23) {
                            final String s25 = "IDL:javax/management/IntrospectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream29 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream29.write_string(s25);
                            outputStream29.write_value(ex23, (_RMIConnectionImpl_Tie.class$javax$management$IntrospectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$IntrospectionException : (_RMIConnectionImpl_Tie.class$javax$management$IntrospectionException = class$("javax.management.IntrospectionException")));
                            return outputStream29;
                        }
                        catch (final ReflectionException ex24) {
                            final String s26 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream30 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream30.write_string(s26);
                            outputStream30.write_value(ex24, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream30;
                        }
                        catch (final IOException ex25) {
                            final String s27 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream31 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream31.write_string(s27);
                            outputStream31.write_value(ex25, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream31;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream32 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream32.write_value(mBeanInfo, (_RMIConnectionImpl_Tie.class$javax$management$MBeanInfo != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanInfo : (_RMIConnectionImpl_Tie.class$javax$management$MBeanInfo = class$("javax.management.MBeanInfo")));
                        return outputStream32;
                    }
                }
                case 'N': {
                    if (s.equals("addNotificationListener")) {
                        final ObjectName objectName6 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final ObjectName objectName7 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final MarshalledObject marshalledObject3 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final MarshalledObject marshalledObject4 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final Subject subject9 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        try {
                            target.addNotificationListener(objectName6, objectName7, marshalledObject3, marshalledObject4, subject9);
                        }
                        catch (final InstanceNotFoundException ex26) {
                            final String s28 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream33 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream33.write_string(s28);
                            outputStream33.write_value(ex26, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream33;
                        }
                        catch (final IOException ex27) {
                            final String s29 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream34 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream34.write_string(s29);
                            outputStream34.write_value(ex27, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream34;
                        }
                        return responseHandler.createReply();
                    }
                    if (s.equals("addNotificationListeners")) {
                        final ObjectName[] array2 = (Object)inputStream2.read_value((_RMIConnectionImpl_Tie.array$Ljavax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.array$Ljavax$management$ObjectName : (_RMIConnectionImpl_Tie.array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")));
                        final MarshalledObject[] array3 = (Object)inputStream2.read_value((_RMIConnectionImpl_Tie.array$Ljava$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.array$Ljava$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")));
                        final Subject[] array4 = (Object)inputStream2.read_value((_RMIConnectionImpl_Tie.array$Ljavax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.array$Ljavax$security$auth$Subject : (_RMIConnectionImpl_Tie.array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")));
                        Integer[] addNotificationListeners;
                        try {
                            addNotificationListeners = target.addNotificationListeners(array2, array3, array4);
                        }
                        catch (final InstanceNotFoundException ex28) {
                            final String s30 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream35 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream35.write_string(s30);
                            outputStream35.write_value(ex28, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream35;
                        }
                        catch (final IOException ex29) {
                            final String s31 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream36 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream36.write_string(s31);
                            outputStream36.write_value(ex29, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream36;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream37 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream37.write_value(this.cast_array(addNotificationListeners), (_RMIConnectionImpl_Tie.array$Ljava$lang$Integer != null) ? _RMIConnectionImpl_Tie.array$Ljava$lang$Integer : (_RMIConnectionImpl_Tie.array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
                        return outputStream37;
                    }
                }
                case 'O': {
                    if (s.equals("getObjectInstance")) {
                        final ObjectName objectName8 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final Subject subject10 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        ObjectInstance objectInstance;
                        try {
                            objectInstance = target.getObjectInstance(objectName8, subject10);
                        }
                        catch (final InstanceNotFoundException ex30) {
                            final String s32 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream38 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream38.write_string(s32);
                            outputStream38.write_value(ex30, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream38;
                        }
                        catch (final IOException ex31) {
                            final String s33 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream39 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream39.write_string(s33);
                            outputStream39.write_value(ex31, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream39;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream40 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream40.write_value(objectInstance, (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectInstance : (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                        return outputStream40;
                    }
                }
                case 'a': {
                    if (s.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject")) {
                        final String s34 = (String)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        final ObjectName objectName9 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final Subject subject11 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        ObjectInstance mBean;
                        try {
                            mBean = target.createMBean(s34, objectName9, subject11);
                        }
                        catch (final ReflectionException ex32) {
                            final String s35 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream41 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream41.write_string(s35);
                            outputStream41.write_value(ex32, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream41;
                        }
                        catch (final InstanceAlreadyExistsException ex33) {
                            final String s36 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream42 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream42.write_string(s36);
                            outputStream42.write_value(ex33, (_RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                            return outputStream42;
                        }
                        catch (final MBeanException ex34) {
                            final String s37 = "IDL:javax/management/MBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream43 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream43.write_string(s37);
                            outputStream43.write_value(ex34, (_RMIConnectionImpl_Tie.class$javax$management$MBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanException : (_RMIConnectionImpl_Tie.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                            return outputStream43;
                        }
                        catch (final NotCompliantMBeanException ex35) {
                            final String s38 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream44 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream44.write_string(s38);
                            outputStream44.write_value(ex35, (_RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException : (_RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                            return outputStream44;
                        }
                        catch (final IOException ex36) {
                            final String s39 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream45 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream45.write_string(s39);
                            outputStream45.write_value(ex36, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream45;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream46 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream46.write_value(mBean, (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectInstance : (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                        return outputStream46;
                    }
                    if (s.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject")) {
                        final String s40 = (String)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        final ObjectName objectName10 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final ObjectName objectName11 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final Subject subject12 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        ObjectInstance mBean2;
                        try {
                            mBean2 = target.createMBean(s40, objectName10, objectName11, subject12);
                        }
                        catch (final ReflectionException ex37) {
                            final String s41 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream47 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream47.write_string(s41);
                            outputStream47.write_value(ex37, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream47;
                        }
                        catch (final InstanceAlreadyExistsException ex38) {
                            final String s42 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream48 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream48.write_string(s42);
                            outputStream48.write_value(ex38, (_RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                            return outputStream48;
                        }
                        catch (final MBeanException ex39) {
                            final String s43 = "IDL:javax/management/MBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream49 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream49.write_string(s43);
                            outputStream49.write_value(ex39, (_RMIConnectionImpl_Tie.class$javax$management$MBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanException : (_RMIConnectionImpl_Tie.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                            return outputStream49;
                        }
                        catch (final NotCompliantMBeanException ex40) {
                            final String s44 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream50 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream50.write_string(s44);
                            outputStream50.write_value(ex40, (_RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException : (_RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                            return outputStream50;
                        }
                        catch (final InstanceNotFoundException ex41) {
                            final String s45 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream51 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream51.write_string(s45);
                            outputStream51.write_value(ex41, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream51;
                        }
                        catch (final IOException ex42) {
                            final String s46 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream52 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream52.write_string(s46);
                            outputStream52.write_value(ex42, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream52;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream53 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream53.write_value(mBean2, (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectInstance : (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                        return outputStream53;
                    }
                    if (s.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject")) {
                        final String s47 = (String)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        final ObjectName objectName12 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final MarshalledObject marshalledObject5 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final String[] array5 = (Object)inputStream2.read_value((_RMIConnectionImpl_Tie.array$Ljava$lang$String != null) ? _RMIConnectionImpl_Tie.array$Ljava$lang$String : (_RMIConnectionImpl_Tie.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                        final Subject subject13 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        ObjectInstance mBean3;
                        try {
                            mBean3 = target.createMBean(s47, objectName12, marshalledObject5, array5, subject13);
                        }
                        catch (final ReflectionException ex43) {
                            final String s48 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream54 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream54.write_string(s48);
                            outputStream54.write_value(ex43, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream54;
                        }
                        catch (final InstanceAlreadyExistsException ex44) {
                            final String s49 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream55 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream55.write_string(s49);
                            outputStream55.write_value(ex44, (_RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                            return outputStream55;
                        }
                        catch (final MBeanException ex45) {
                            final String s50 = "IDL:javax/management/MBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream56 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream56.write_string(s50);
                            outputStream56.write_value(ex45, (_RMIConnectionImpl_Tie.class$javax$management$MBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanException : (_RMIConnectionImpl_Tie.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                            return outputStream56;
                        }
                        catch (final NotCompliantMBeanException ex46) {
                            final String s51 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream57 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream57.write_string(s51);
                            outputStream57.write_value(ex46, (_RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException : (_RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                            return outputStream57;
                        }
                        catch (final IOException ex47) {
                            final String s52 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream58 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream58.write_string(s52);
                            outputStream58.write_value(ex47, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream58;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream59 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream59.write_value(mBean3, (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectInstance : (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                        return outputStream59;
                    }
                    if (s.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject")) {
                        final String s53 = (String)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        final ObjectName objectName13 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final ObjectName objectName14 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final MarshalledObject marshalledObject6 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final String[] array6 = (Object)inputStream2.read_value((_RMIConnectionImpl_Tie.array$Ljava$lang$String != null) ? _RMIConnectionImpl_Tie.array$Ljava$lang$String : (_RMIConnectionImpl_Tie.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                        final Subject subject14 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        ObjectInstance mBean4;
                        try {
                            mBean4 = target.createMBean(s53, objectName13, objectName14, marshalledObject6, array6, subject14);
                        }
                        catch (final ReflectionException ex48) {
                            final String s54 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream60 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream60.write_string(s54);
                            outputStream60.write_value(ex48, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream60;
                        }
                        catch (final InstanceAlreadyExistsException ex49) {
                            final String s55 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream61 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream61.write_string(s55);
                            outputStream61.write_value(ex49, (_RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                            return outputStream61;
                        }
                        catch (final MBeanException ex50) {
                            final String s56 = "IDL:javax/management/MBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream62 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream62.write_string(s56);
                            outputStream62.write_value(ex50, (_RMIConnectionImpl_Tie.class$javax$management$MBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanException : (_RMIConnectionImpl_Tie.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                            return outputStream62;
                        }
                        catch (final NotCompliantMBeanException ex51) {
                            final String s57 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream63 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream63.write_string(s57);
                            outputStream63.write_value(ex51, (_RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException : (_RMIConnectionImpl_Tie.class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                            return outputStream63;
                        }
                        catch (final InstanceNotFoundException ex52) {
                            final String s58 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream64 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream64.write_string(s58);
                            outputStream64.write_value(ex52, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream64;
                        }
                        catch (final IOException ex53) {
                            final String s59 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream65 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream65.write_string(s59);
                            outputStream65.write_value(ex53, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream65;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream66 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream66.write_value(mBean4, (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectInstance : (_RMIConnectionImpl_Tie.class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                        return outputStream66;
                    }
                }
                case 'c': {
                    if (s.equals("fetchNotifications")) {
                        final long read_longlong = inputStream2.read_longlong();
                        final int read_long = inputStream2.read_long();
                        final long read_longlong2 = inputStream2.read_longlong();
                        NotificationResult fetchNotifications;
                        try {
                            fetchNotifications = target.fetchNotifications(read_longlong, read_long, read_longlong2);
                        }
                        catch (final IOException ex54) {
                            final String s60 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream67 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream67.write_string(s60);
                            outputStream67.write_value(ex54, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream67;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream68 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream68.write_value(fetchNotifications, (_RMIConnectionImpl_Tie.class$javax$management$remote$NotificationResult != null) ? _RMIConnectionImpl_Tie.class$javax$management$remote$NotificationResult : (_RMIConnectionImpl_Tie.class$javax$management$remote$NotificationResult = class$("javax.management.remote.NotificationResult")));
                        return outputStream68;
                    }
                }
                case 'e': {
                    if (s.equals("unregisterMBean")) {
                        final ObjectName objectName15 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final Subject subject15 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        try {
                            target.unregisterMBean(objectName15, subject15);
                        }
                        catch (final InstanceNotFoundException ex55) {
                            final String s61 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream69 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream69.write_string(s61);
                            outputStream69.write_value(ex55, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream69;
                        }
                        catch (final MBeanRegistrationException ex56) {
                            final String s62 = "IDL:javax/management/MBeanRegistrationEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream70 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream70.write_string(s62);
                            outputStream70.write_value(ex56, (_RMIConnectionImpl_Tie.class$javax$management$MBeanRegistrationException != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanRegistrationException : (_RMIConnectionImpl_Tie.class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                            return outputStream70;
                        }
                        catch (final IOException ex57) {
                            final String s63 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream71 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream71.write_string(s63);
                            outputStream71.write_value(ex57, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream71;
                        }
                        return responseHandler.createReply();
                    }
                    if (s.equals("isRegistered")) {
                        final ObjectName objectName16 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final Subject subject16 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        boolean registered;
                        try {
                            registered = target.isRegistered(objectName16, subject16);
                        }
                        catch (final IOException ex58) {
                            final String s64 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream72 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream72.write_string(s64);
                            outputStream72.write_value(ex58, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream72;
                        }
                        final OutputStream reply2 = responseHandler.createReply();
                        reply2.write_boolean(registered);
                        return reply2;
                    }
                }
                case 'n': {
                    if (s.equals("isInstanceOf")) {
                        final ObjectName objectName17 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final String s65 = (String)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        final Subject subject17 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        boolean instance;
                        try {
                            instance = target.isInstanceOf(objectName17, s65, subject17);
                        }
                        catch (final InstanceNotFoundException ex59) {
                            final String s66 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream73 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream73.write_string(s66);
                            outputStream73.write_value(ex59, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream73;
                        }
                        catch (final IOException ex60) {
                            final String s67 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream74 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream74.write_string(s67);
                            outputStream74.write_value(ex60, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream74;
                        }
                        final OutputStream reply3 = responseHandler.createReply();
                        reply3.write_boolean(instance);
                        return reply3;
                    }
                }
                case 'o': {
                    if (s.equals("invoke")) {
                        final ObjectName objectName18 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final String s68 = (String)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$lang$String != null) ? _RMIConnectionImpl_Tie.class$java$lang$String : (_RMIConnectionImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        final MarshalledObject marshalledObject7 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final String[] array7 = (Object)inputStream2.read_value((_RMIConnectionImpl_Tie.array$Ljava$lang$String != null) ? _RMIConnectionImpl_Tie.array$Ljava$lang$String : (_RMIConnectionImpl_Tie.array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                        final Subject subject18 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        Object invoke;
                        try {
                            invoke = target.invoke(objectName18, s68, marshalledObject7, array7, subject18);
                        }
                        catch (final InstanceNotFoundException ex61) {
                            final String s69 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream75 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream75.write_string(s69);
                            outputStream75.write_value(ex61, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream75;
                        }
                        catch (final MBeanException ex62) {
                            final String s70 = "IDL:javax/management/MBeanEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream76 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream76.write_string(s70);
                            outputStream76.write_value(ex62, (_RMIConnectionImpl_Tie.class$javax$management$MBeanException != null) ? _RMIConnectionImpl_Tie.class$javax$management$MBeanException : (_RMIConnectionImpl_Tie.class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                            return outputStream76;
                        }
                        catch (final ReflectionException ex63) {
                            final String s71 = "IDL:javax/management/ReflectionEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream77 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream77.write_string(s71);
                            outputStream77.write_value(ex63, (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ReflectionException : (_RMIConnectionImpl_Tie.class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                            return outputStream77;
                        }
                        catch (final IOException ex64) {
                            final String s72 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream78 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream78.write_string(s72);
                            outputStream78.write_value(ex64, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream78;
                        }
                        final OutputStream reply4 = responseHandler.createReply();
                        Util.writeAny(reply4, invoke);
                        return reply4;
                    }
                    if (s.equals("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject")) {
                        final ObjectName objectName19 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final ObjectName objectName20 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final Subject subject19 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        try {
                            target.removeNotificationListener(objectName19, objectName20, subject19);
                        }
                        catch (final InstanceNotFoundException ex65) {
                            final String s73 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream79 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream79.write_string(s73);
                            outputStream79.write_value(ex65, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream79;
                        }
                        catch (final ListenerNotFoundException ex66) {
                            final String s74 = "IDL:javax/management/ListenerNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream80 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream80.write_string(s74);
                            outputStream80.write_value(ex66, (_RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                            return outputStream80;
                        }
                        catch (final IOException ex67) {
                            final String s75 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream81 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream81.write_string(s75);
                            outputStream81.write_value(ex67, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream81;
                        }
                        return responseHandler.createReply();
                    }
                    if (s.equals("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject")) {
                        final ObjectName objectName21 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final ObjectName objectName22 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final MarshalledObject marshalledObject8 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final MarshalledObject marshalledObject9 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final Subject subject20 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        try {
                            target.removeNotificationListener(objectName21, objectName22, marshalledObject8, marshalledObject9, subject20);
                        }
                        catch (final InstanceNotFoundException ex68) {
                            final String s76 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream82 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream82.write_string(s76);
                            outputStream82.write_value(ex68, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream82;
                        }
                        catch (final ListenerNotFoundException ex69) {
                            final String s77 = "IDL:javax/management/ListenerNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream83 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream83.write_string(s77);
                            outputStream83.write_value(ex69, (_RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                            return outputStream83;
                        }
                        catch (final IOException ex70) {
                            final String s78 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream84 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream84.write_string(s78);
                            outputStream84.write_value(ex70, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream84;
                        }
                        return responseHandler.createReply();
                    }
                    if (s.equals("removeNotificationListeners")) {
                        final ObjectName objectName23 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final Integer[] array8 = (Object)inputStream2.read_value((_RMIConnectionImpl_Tie.array$Ljava$lang$Integer != null) ? _RMIConnectionImpl_Tie.array$Ljava$lang$Integer : (_RMIConnectionImpl_Tie.array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
                        final Subject subject21 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        try {
                            target.removeNotificationListeners(objectName23, array8, subject21);
                        }
                        catch (final InstanceNotFoundException ex71) {
                            final String s79 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream85 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream85.write_string(s79);
                            outputStream85.write_value(ex71, (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                            return outputStream85;
                        }
                        catch (final ListenerNotFoundException ex72) {
                            final String s80 = "IDL:javax/management/ListenerNotFoundEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream86 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream86.write_string(s80);
                            outputStream86.write_value(ex72, (_RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException != null) ? _RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException : (_RMIConnectionImpl_Tie.class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                            return outputStream86;
                        }
                        catch (final IOException ex73) {
                            final String s81 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream87 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream87.write_string(s81);
                            outputStream87.write_value(ex73, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream87;
                        }
                        return responseHandler.createReply();
                    }
                }
                case 'r': {
                    if (s.equals("queryMBeans")) {
                        final ObjectName objectName24 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final MarshalledObject marshalledObject10 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final Subject subject22 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        Set<ObjectInstance> queryMBeans;
                        try {
                            queryMBeans = target.queryMBeans(objectName24, marshalledObject10, subject22);
                        }
                        catch (final IOException ex74) {
                            final String s82 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream88 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream88.write_string(s82);
                            outputStream88.write_value(ex74, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream88;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream89 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream89.write_value((Serializable)queryMBeans, (_RMIConnectionImpl_Tie.class$java$util$Set != null) ? _RMIConnectionImpl_Tie.class$java$util$Set : (_RMIConnectionImpl_Tie.class$java$util$Set = class$("java.util.Set")));
                        return outputStream89;
                    }
                    if (s.equals("queryNames")) {
                        final ObjectName objectName25 = (ObjectName)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$management$ObjectName != null) ? _RMIConnectionImpl_Tie.class$javax$management$ObjectName : (_RMIConnectionImpl_Tie.class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                        final MarshalledObject marshalledObject11 = (MarshalledObject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject != null) ? _RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject : (_RMIConnectionImpl_Tie.class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                        final Subject subject23 = (Subject)inputStream2.read_value((_RMIConnectionImpl_Tie.class$javax$security$auth$Subject != null) ? _RMIConnectionImpl_Tie.class$javax$security$auth$Subject : (_RMIConnectionImpl_Tie.class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
                        Set<ObjectName> queryNames;
                        try {
                            queryNames = target.queryNames(objectName25, marshalledObject11, subject23);
                        }
                        catch (final IOException ex75) {
                            final String s83 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream90 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream90.write_string(s83);
                            outputStream90.write_value(ex75, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream90;
                        }
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream91 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream91.write_value((Serializable)queryNames, (_RMIConnectionImpl_Tie.class$java$util$Set != null) ? _RMIConnectionImpl_Tie.class$java$util$Set : (_RMIConnectionImpl_Tie.class$java$util$Set = class$("java.util.Set")));
                        return outputStream91;
                    }
                }
                case 's': {
                    if (s.equals("close")) {
                        try {
                            target.close();
                        }
                        catch (final IOException ex76) {
                            final String s84 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream92 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream92.write_string(s84);
                            outputStream92.write_value(ex76, (_RMIConnectionImpl_Tie.class$java$io$IOException != null) ? _RMIConnectionImpl_Tie.class$java$io$IOException : (_RMIConnectionImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream92;
                        }
                        return responseHandler.createReply();
                    }
                    break;
                }
            }
            throw new BAD_OPERATION();
        }
        catch (final SystemException ex77) {
            throw ex77;
        }
        catch (final Throwable t) {
            throw new UnknownException(t);
        }
    }
    
    private Serializable cast_array(final Object o) {
        return (Serializable)o;
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public void deactivate() {
        this._orb().disconnect(this);
        this._set_delegate(null);
        this.target = null;
    }
    
    public Remote getTarget() {
        return this.target;
    }
    
    public ORB orb() {
        return this._orb();
    }
    
    public void orb(final ORB orb) {
        orb.connect(this);
    }
    
    public void setTarget(final Remote remote) {
        this.target = (RMIConnectionImpl)remote;
    }
    
    public org.omg.CORBA.Object thisObject() {
        return this;
    }
}
