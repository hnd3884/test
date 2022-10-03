package com.sun.xml.internal.ws.fault;

import org.w3c.dom.Element;
import java.util.ArrayList;
import com.sun.xml.internal.ws.developer.ServerSideException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.w3c.dom.Node;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://jax-ws.dev.java.net/", name = "exception")
final class ExceptionBean
{
    @XmlAttribute(name = "class")
    public String className;
    @XmlElement
    public String message;
    @XmlElementWrapper(namespace = "http://jax-ws.dev.java.net/", name = "stackTrace")
    @XmlElement(namespace = "http://jax-ws.dev.java.net/", name = "frame")
    public List<StackFrame> stackTrace;
    @XmlElement(namespace = "http://jax-ws.dev.java.net/", name = "cause")
    public ExceptionBean cause;
    @XmlAttribute
    public String note;
    private static final JAXBContext JAXB_CONTEXT;
    static final String NS = "http://jax-ws.dev.java.net/";
    static final String LOCAL_NAME = "exception";
    private static final NamespacePrefixMapper nsp;
    
    public static void marshal(final Throwable t, final Node parent) throws JAXBException {
        final Marshaller m = ExceptionBean.JAXB_CONTEXT.createMarshaller();
        try {
            m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", ExceptionBean.nsp);
        }
        catch (final PropertyException ex) {}
        m.marshal(new ExceptionBean(t), parent);
    }
    
    public static ServerSideException unmarshal(final Node xml) throws JAXBException {
        final ExceptionBean e = (ExceptionBean)ExceptionBean.JAXB_CONTEXT.createUnmarshaller().unmarshal(xml);
        return e.toException();
    }
    
    ExceptionBean() {
        this.stackTrace = new ArrayList<StackFrame>();
        this.note = "To disable this feature, set " + SOAPFaultBuilder.CAPTURE_STACK_TRACE_PROPERTY + " system property to false";
    }
    
    private ExceptionBean(final Throwable t) {
        this.stackTrace = new ArrayList<StackFrame>();
        this.note = "To disable this feature, set " + SOAPFaultBuilder.CAPTURE_STACK_TRACE_PROPERTY + " system property to false";
        this.className = t.getClass().getName();
        this.message = t.getMessage();
        for (final StackTraceElement f : t.getStackTrace()) {
            this.stackTrace.add(new StackFrame(f));
        }
        final Throwable cause = t.getCause();
        if (t != cause && cause != null) {
            this.cause = new ExceptionBean(cause);
        }
    }
    
    private ServerSideException toException() {
        final ServerSideException e = new ServerSideException(this.className, this.message);
        if (this.stackTrace != null) {
            final StackTraceElement[] ste = new StackTraceElement[this.stackTrace.size()];
            for (int i = 0; i < this.stackTrace.size(); ++i) {
                ste[i] = this.stackTrace.get(i).toStackTraceElement();
            }
            e.setStackTrace(ste);
        }
        if (this.cause != null) {
            e.initCause(this.cause.toException());
        }
        return e;
    }
    
    public static boolean isStackTraceXml(final Element n) {
        return "exception".equals(n.getLocalName()) && "http://jax-ws.dev.java.net/".equals(n.getNamespaceURI());
    }
    
    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(ExceptionBean.class);
        }
        catch (final JAXBException e) {
            throw new Error(e);
        }
        nsp = new NamespacePrefixMapper() {
            @Override
            public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix) {
                if ("http://jax-ws.dev.java.net/".equals(namespaceUri)) {
                    return "";
                }
                return suggestion;
            }
        };
    }
    
    static final class StackFrame
    {
        @XmlAttribute(name = "class")
        public String declaringClass;
        @XmlAttribute(name = "method")
        public String methodName;
        @XmlAttribute(name = "file")
        public String fileName;
        @XmlAttribute(name = "line")
        public String lineNumber;
        
        StackFrame() {
        }
        
        public StackFrame(final StackTraceElement ste) {
            this.declaringClass = ste.getClassName();
            this.methodName = ste.getMethodName();
            this.fileName = ste.getFileName();
            this.lineNumber = this.box(ste.getLineNumber());
        }
        
        private String box(final int i) {
            if (i >= 0) {
                return String.valueOf(i);
            }
            if (i == -2) {
                return "native";
            }
            return "unknown";
        }
        
        private int unbox(final String v) {
            try {
                return Integer.parseInt(v);
            }
            catch (final NumberFormatException e) {
                if ("native".equals(v)) {
                    return -2;
                }
                return -1;
            }
        }
        
        private StackTraceElement toStackTraceElement() {
            return new StackTraceElement(this.declaringClass, this.methodName, this.fileName, this.unbox(this.lineNumber));
        }
    }
}
