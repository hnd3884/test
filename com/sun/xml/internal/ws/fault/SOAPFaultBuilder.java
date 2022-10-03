package com.sun.xml.internal.ws.fault;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.PrivilegedAction;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.security.Permissions;
import javax.xml.soap.DetailEntry;
import com.sun.xml.internal.ws.message.FaultMessage;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.soap.Detail;
import javax.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import com.sun.xml.internal.ws.util.StringUtils;
import java.lang.reflect.Field;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import java.util.Iterator;
import org.w3c.dom.Document;
import java.util.logging.Level;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Element;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.ws.ProtocolException;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Constructor;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import java.util.Map;
import com.sun.istack.internal.Nullable;
import javax.xml.bind.annotation.XmlTransient;
import org.w3c.dom.Node;
import javax.xml.namespace.QName;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;

public abstract class SOAPFaultBuilder
{
    private static final JAXBContext JAXB_CONTEXT;
    private static final Logger logger;
    public static final boolean captureStackTrace;
    static final String CAPTURE_STACK_TRACE_PROPERTY;
    
    abstract DetailType getDetail();
    
    abstract void setDetail(final DetailType p0);
    
    @XmlTransient
    @Nullable
    public QName getFirstDetailEntryName() {
        final DetailType dt = this.getDetail();
        if (dt != null) {
            final Node entry = dt.getDetail(0);
            if (entry != null) {
                return new QName(entry.getNamespaceURI(), entry.getLocalName());
            }
        }
        return null;
    }
    
    abstract String getFaultString();
    
    public Throwable createException(final Map<QName, CheckedExceptionImpl> exceptions) throws JAXBException {
        final DetailType dt = this.getDetail();
        Node detail = null;
        if (dt != null) {
            detail = dt.getDetail(0);
        }
        if (detail == null || exceptions == null) {
            return this.attachServerException(this.getProtocolException());
        }
        final QName detailName = new QName(detail.getNamespaceURI(), detail.getLocalName());
        final CheckedExceptionImpl ce = exceptions.get(detailName);
        if (ce == null) {
            return this.attachServerException(this.getProtocolException());
        }
        if (ce.getExceptionType().equals(ExceptionType.UserDefined)) {
            return this.attachServerException(this.createUserDefinedException(ce));
        }
        final Class exceptionClass = ce.getExceptionClass();
        try {
            final Constructor constructor = exceptionClass.getConstructor(String.class, (Class)ce.getDetailType().type);
            final Exception exception = constructor.newInstance(this.getFaultString(), this.getJAXBObject(detail, ce));
            return this.attachServerException(exception);
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
    }
    
    @NotNull
    public static Message createSOAPFaultMessage(@NotNull final SOAPVersion soapVersion, @NotNull final ProtocolException ex, @Nullable final QName faultcode) {
        final Object detail = getFaultDetail(null, ex);
        if (soapVersion == SOAPVersion.SOAP_12) {
            return createSOAP12Fault(soapVersion, ex, detail, null, faultcode);
        }
        return createSOAP11Fault(soapVersion, ex, detail, null, faultcode);
    }
    
    public static Message createSOAPFaultMessage(final SOAPVersion soapVersion, final CheckedExceptionImpl ceModel, final Throwable ex) {
        final Throwable t = (ex instanceof InvocationTargetException) ? ((InvocationTargetException)ex).getTargetException() : ex;
        return createSOAPFaultMessage(soapVersion, ceModel, t, null);
    }
    
    public static Message createSOAPFaultMessage(final SOAPVersion soapVersion, final CheckedExceptionImpl ceModel, final Throwable ex, final QName faultCode) {
        final Object detail = getFaultDetail(ceModel, ex);
        if (soapVersion == SOAPVersion.SOAP_12) {
            return createSOAP12Fault(soapVersion, ex, detail, ceModel, faultCode);
        }
        return createSOAP11Fault(soapVersion, ex, detail, ceModel, faultCode);
    }
    
    public static Message createSOAPFaultMessage(final SOAPVersion soapVersion, final String faultString, QName faultCode) {
        if (faultCode == null) {
            faultCode = getDefaultFaultCode(soapVersion);
        }
        return createSOAPFaultMessage(soapVersion, faultString, faultCode, null);
    }
    
    public static Message createSOAPFaultMessage(final SOAPVersion soapVersion, final SOAPFault fault) {
        switch (soapVersion) {
            case SOAP_11: {
                return JAXBMessage.create(SOAPFaultBuilder.JAXB_CONTEXT, new SOAP11Fault(fault), soapVersion);
            }
            case SOAP_12: {
                return JAXBMessage.create(SOAPFaultBuilder.JAXB_CONTEXT, new SOAP12Fault(fault), soapVersion);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    private static Message createSOAPFaultMessage(final SOAPVersion soapVersion, final String faultString, final QName faultCode, final Element detail) {
        switch (soapVersion) {
            case SOAP_11: {
                return JAXBMessage.create(SOAPFaultBuilder.JAXB_CONTEXT, new SOAP11Fault(faultCode, faultString, null, detail), soapVersion);
            }
            case SOAP_12: {
                return JAXBMessage.create(SOAPFaultBuilder.JAXB_CONTEXT, new SOAP12Fault(faultCode, faultString, detail), soapVersion);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    final void captureStackTrace(@Nullable final Throwable t) {
        if (t == null) {
            return;
        }
        if (!SOAPFaultBuilder.captureStackTrace) {
            return;
        }
        try {
            final Document d = DOMUtil.createDom();
            ExceptionBean.marshal(t, d);
            DetailType detail = this.getDetail();
            if (detail == null) {
                this.setDetail(detail = new DetailType());
            }
            detail.getDetails().add(d.getDocumentElement());
        }
        catch (final JAXBException e) {
            SOAPFaultBuilder.logger.log(Level.WARNING, "Unable to capture the stack trace into XML", e);
        }
    }
    
    private <T extends Throwable> T attachServerException(final T t) {
        final DetailType detail = this.getDetail();
        if (detail == null) {
            return t;
        }
        for (final Element n : detail.getDetails()) {
            if (ExceptionBean.isStackTraceXml(n)) {
                try {
                    t.initCause(ExceptionBean.unmarshal(n));
                }
                catch (final JAXBException e) {
                    SOAPFaultBuilder.logger.log(Level.WARNING, "Unable to read the capture stack trace in the fault", e);
                }
                return t;
            }
        }
        return t;
    }
    
    protected abstract Throwable getProtocolException();
    
    private Object getJAXBObject(final Node jaxbBean, final CheckedExceptionImpl ce) throws JAXBException {
        final XMLBridge bridge = ce.getBond();
        return bridge.unmarshal(jaxbBean, null);
    }
    
    private Exception createUserDefinedException(final CheckedExceptionImpl ce) {
        final Class exceptionClass = ce.getExceptionClass();
        final Class detailBean = ce.getDetailBean();
        try {
            final Node detailNode = this.getDetail().getDetails().get(0);
            final Object jaxbDetail = this.getJAXBObject(detailNode, ce);
            try {
                final Constructor exConstructor = exceptionClass.getConstructor(String.class, detailBean);
                return exConstructor.newInstance(this.getFaultString(), jaxbDetail);
            }
            catch (final NoSuchMethodException e) {
                final Constructor exConstructor = exceptionClass.getConstructor(String.class);
                return exConstructor.newInstance(this.getFaultString());
            }
        }
        catch (final Exception e2) {
            throw new WebServiceException(e2);
        }
    }
    
    private static String getWriteMethod(final Field f) {
        return "set" + StringUtils.capitalize(f.getName());
    }
    
    private static Object getFaultDetail(final CheckedExceptionImpl ce, final Throwable exception) {
        if (ce == null) {
            return null;
        }
        if (ce.getExceptionType().equals(ExceptionType.UserDefined)) {
            return createDetailFromUserDefinedException(ce, exception);
        }
        try {
            final Method m = exception.getClass().getMethod("getFaultInfo", (Class<?>[])new Class[0]);
            return m.invoke(exception, new Object[0]);
        }
        catch (final Exception e) {
            throw new SerializationException(e);
        }
    }
    
    private static Object createDetailFromUserDefinedException(final CheckedExceptionImpl ce, final Object exception) {
        final Class detailBean = ce.getDetailBean();
        final Field[] fields = detailBean.getDeclaredFields();
        try {
            final Object detail = detailBean.newInstance();
            for (final Field f : fields) {
                final Method em = exception.getClass().getMethod(getReadMethod(f), (Class<?>[])new Class[0]);
                try {
                    final Method sm = detailBean.getMethod(getWriteMethod(f), em.getReturnType());
                    sm.invoke(detail, em.invoke(exception, new Object[0]));
                }
                catch (final NoSuchMethodException ne) {
                    final Field sf = detailBean.getField(f.getName());
                    sf.set(detail, em.invoke(exception, new Object[0]));
                }
            }
            return detail;
        }
        catch (final Exception e) {
            throw new SerializationException(e);
        }
    }
    
    private static String getReadMethod(final Field f) {
        if (f.getType().isAssignableFrom(Boolean.TYPE)) {
            return "is" + StringUtils.capitalize(f.getName());
        }
        return "get" + StringUtils.capitalize(f.getName());
    }
    
    private static Message createSOAP11Fault(final SOAPVersion soapVersion, final Throwable e, final Object detail, final CheckedExceptionImpl ce, QName faultCode) {
        SOAPFaultException soapFaultException = null;
        String faultString = null;
        String faultActor = null;
        final Throwable cause = e.getCause();
        if (e instanceof SOAPFaultException) {
            soapFaultException = (SOAPFaultException)e;
        }
        else if (cause != null && cause instanceof SOAPFaultException) {
            soapFaultException = (SOAPFaultException)e.getCause();
        }
        if (soapFaultException != null) {
            final QName soapFaultCode = soapFaultException.getFault().getFaultCodeAsQName();
            if (soapFaultCode != null) {
                faultCode = soapFaultCode;
            }
            faultString = soapFaultException.getFault().getFaultString();
            faultActor = soapFaultException.getFault().getFaultActor();
        }
        if (faultCode == null) {
            faultCode = getDefaultFaultCode(soapVersion);
        }
        if (faultString == null) {
            faultString = e.getMessage();
            if (faultString == null) {
                faultString = e.toString();
            }
        }
        Element detailNode = null;
        QName firstEntry = null;
        if (detail == null && soapFaultException != null) {
            detailNode = soapFaultException.getFault().getDetail();
            firstEntry = getFirstDetailEntryName((Detail)detailNode);
        }
        else if (ce != null) {
            try {
                final DOMResult dr = new DOMResult();
                ce.getBond().marshal(detail, dr);
                detailNode = (Element)dr.getNode().getFirstChild();
                firstEntry = getFirstDetailEntryName(detailNode);
            }
            catch (final JAXBException e2) {
                faultString = e.getMessage();
                faultCode = getDefaultFaultCode(soapVersion);
            }
        }
        final SOAP11Fault soap11Fault = new SOAP11Fault(faultCode, faultString, faultActor, detailNode);
        if (ce == null) {
            soap11Fault.captureStackTrace(e);
        }
        final Message msg = JAXBMessage.create(SOAPFaultBuilder.JAXB_CONTEXT, soap11Fault, soapVersion);
        return new FaultMessage(msg, firstEntry);
    }
    
    @Nullable
    private static QName getFirstDetailEntryName(@Nullable final Detail detail) {
        if (detail != null) {
            final Iterator<DetailEntry> it = detail.getDetailEntries();
            if (it.hasNext()) {
                final DetailEntry entry = it.next();
                return getFirstDetailEntryName(entry);
            }
        }
        return null;
    }
    
    @NotNull
    private static QName getFirstDetailEntryName(@NotNull final Element entry) {
        return new QName(entry.getNamespaceURI(), entry.getLocalName());
    }
    
    private static Message createSOAP12Fault(final SOAPVersion soapVersion, final Throwable e, final Object detail, final CheckedExceptionImpl ce, QName faultCode) {
        SOAPFaultException soapFaultException = null;
        CodeType code = null;
        String faultString = null;
        String faultRole = null;
        String faultNode = null;
        final Throwable cause = e.getCause();
        if (e instanceof SOAPFaultException) {
            soapFaultException = (SOAPFaultException)e;
        }
        else if (cause != null && cause instanceof SOAPFaultException) {
            soapFaultException = (SOAPFaultException)e.getCause();
        }
        if (soapFaultException != null) {
            final SOAPFault fault = soapFaultException.getFault();
            final QName soapFaultCode = fault.getFaultCodeAsQName();
            if (soapFaultCode != null) {
                faultCode = soapFaultCode;
                code = new CodeType(faultCode);
                final Iterator iter = fault.getFaultSubcodes();
                boolean first = true;
                SubcodeType subcode = null;
                while (iter.hasNext()) {
                    final QName value = iter.next();
                    if (first) {
                        final SubcodeType sct = new SubcodeType(value);
                        code.setSubcode(sct);
                        subcode = sct;
                        first = false;
                    }
                    else {
                        subcode = fillSubcodes(subcode, value);
                    }
                }
            }
            faultString = soapFaultException.getFault().getFaultString();
            faultRole = soapFaultException.getFault().getFaultActor();
            faultNode = soapFaultException.getFault().getFaultNode();
        }
        if (faultCode == null) {
            faultCode = getDefaultFaultCode(soapVersion);
            code = new CodeType(faultCode);
        }
        else if (code == null) {
            code = new CodeType(faultCode);
        }
        if (faultString == null) {
            faultString = e.getMessage();
            if (faultString == null) {
                faultString = e.toString();
            }
        }
        final ReasonType reason = new ReasonType(faultString);
        Element detailNode = null;
        QName firstEntry = null;
        if (detail == null && soapFaultException != null) {
            detailNode = soapFaultException.getFault().getDetail();
            firstEntry = getFirstDetailEntryName((Detail)detailNode);
        }
        else if (detail != null) {
            try {
                final DOMResult dr = new DOMResult();
                ce.getBond().marshal(detail, dr);
                detailNode = (Element)dr.getNode().getFirstChild();
                firstEntry = getFirstDetailEntryName(detailNode);
            }
            catch (final JAXBException e2) {
                faultString = e.getMessage();
            }
        }
        final SOAP12Fault soap12Fault = new SOAP12Fault(code, reason, faultNode, faultRole, detailNode);
        if (ce == null) {
            soap12Fault.captureStackTrace(e);
        }
        final Message msg = JAXBMessage.create(SOAPFaultBuilder.JAXB_CONTEXT, soap12Fault, soapVersion);
        return new FaultMessage(msg, firstEntry);
    }
    
    private static SubcodeType fillSubcodes(final SubcodeType parent, final QName value) {
        final SubcodeType newCode = new SubcodeType(value);
        parent.setSubcode(newCode);
        return newCode;
    }
    
    private static QName getDefaultFaultCode(final SOAPVersion soapVersion) {
        return soapVersion.faultCodeServer;
    }
    
    public static SOAPFaultBuilder create(final Message msg) throws JAXBException {
        return msg.readPayloadAsJAXB(SOAPFaultBuilder.JAXB_CONTEXT.createUnmarshaller());
    }
    
    private static JAXBContext createJAXBContext() {
        if (isJDKRuntime()) {
            final Permissions permissions = new Permissions();
            permissions.add(new RuntimePermission("accessClassInPackage.com.sun.xml.internal.ws.fault"));
            permissions.add(new ReflectPermission("suppressAccessChecks"));
            return AccessController.doPrivileged((PrivilegedAction<JAXBContext>)new PrivilegedAction<JAXBContext>() {
                @Override
                public JAXBContext run() {
                    try {
                        return JAXBContext.newInstance(SOAP11Fault.class, SOAP12Fault.class);
                    }
                    catch (final JAXBException e) {
                        throw new Error(e);
                    }
                }
            }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) }));
        }
        try {
            return JAXBContext.newInstance(SOAP11Fault.class, SOAP12Fault.class);
        }
        catch (final JAXBException e) {
            throw new Error(e);
        }
    }
    
    private static boolean isJDKRuntime() {
        return SOAPFaultBuilder.class.getName().contains("internal");
    }
    
    static {
        logger = Logger.getLogger(SOAPFaultBuilder.class.getName());
        CAPTURE_STACK_TRACE_PROPERTY = SOAPFaultBuilder.class.getName() + ".captureStackTrace";
        boolean tmpVal = false;
        try {
            tmpVal = Boolean.getBoolean(SOAPFaultBuilder.CAPTURE_STACK_TRACE_PROPERTY);
        }
        catch (final SecurityException ex) {}
        captureStackTrace = tmpVal;
        JAXB_CONTEXT = createJAXBContext();
    }
}
