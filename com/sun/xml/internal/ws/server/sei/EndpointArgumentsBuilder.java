package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import javax.jws.WebParam;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.api.message.Header;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.ws.resources.ServerMessages;
import javax.xml.ws.soap.SOAPFaultException;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.encoding.DataHandlerDataSource;
import com.sun.xml.internal.ws.encoding.StringDataContentHandler;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Iterator;
import java.io.InputStream;
import java.awt.Image;
import javax.xml.transform.Source;
import javax.activation.DataHandler;
import com.sun.xml.internal.ws.model.ParameterImpl;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import java.util.ArrayList;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.sun.xml.internal.ws.api.message.Attachment;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.ws.WebServiceException;
import java.lang.reflect.Type;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.namespace.QName;
import java.util.Map;

public abstract class EndpointArgumentsBuilder
{
    public static final EndpointArgumentsBuilder NONE;
    private static final Map<Class, Object> primitiveUninitializedValues;
    protected QName wrapperName;
    protected Map<QName, WrappedPartBuilder> wrappedParts;
    
    public EndpointArgumentsBuilder() {
        this.wrappedParts = null;
    }
    
    public abstract void readRequest(final Message p0, final Object[] p1) throws JAXBException, XMLStreamException;
    
    public static Object getVMUninitializedValue(final Type type) {
        return EndpointArgumentsBuilder.primitiveUninitializedValues.get(type);
    }
    
    protected void readWrappedRequest(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
        if (!msg.hasPayload()) {
            throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
        }
        final XMLStreamReader reader = msg.readPayload();
        XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
        reader.nextTag();
        while (reader.getEventType() == 1) {
            final QName name = reader.getName();
            final WrappedPartBuilder part = this.wrappedParts.get(name);
            if (part == null) {
                XMLStreamReaderUtil.skipElement(reader);
                reader.nextTag();
            }
            else {
                part.readRequest(args, reader, msg.getAttachments());
            }
            XMLStreamReaderUtil.toNextTag(reader, name);
        }
        reader.close();
        XMLStreamReaderFactory.recycle(reader);
    }
    
    public static final String getWSDLPartName(final Attachment att) {
        final String cId = att.getContentId();
        int index = cId.lastIndexOf(64, cId.length());
        if (index == -1) {
            return null;
        }
        final String localPart = cId.substring(0, index);
        index = localPart.lastIndexOf(61, localPart.length());
        if (index == -1) {
            return null;
        }
        try {
            return URLDecoder.decode(localPart.substring(0, index), "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebServiceException(e);
        }
    }
    
    private static boolean isXMLMimeType(final String mimeType) {
        return mimeType.equals("text/xml") || mimeType.equals("application/xml");
    }
    
    static {
        NONE = new None();
        primitiveUninitializedValues = new HashMap<Class, Object>();
        final Map<Class, Object> m = EndpointArgumentsBuilder.primitiveUninitializedValues;
        m.put(Integer.TYPE, 0);
        m.put(Character.TYPE, '\0');
        m.put(Byte.TYPE, 0);
        m.put(Short.TYPE, 0);
        m.put(Long.TYPE, 0L);
        m.put(Float.TYPE, 0.0f);
        m.put(Double.TYPE, 0.0);
    }
    
    static final class None extends EndpointArgumentsBuilder
    {
        private None() {
        }
        
        @Override
        public void readRequest(final Message msg, final Object[] args) {
            msg.consume();
        }
    }
    
    static final class WrappedPartBuilder
    {
        private final XMLBridge bridge;
        private final EndpointValueSetter setter;
        
        public WrappedPartBuilder(final XMLBridge bridge, final EndpointValueSetter setter) {
            this.bridge = bridge;
            this.setter = setter;
        }
        
        void readRequest(final Object[] args, final XMLStreamReader r, final AttachmentSet att) throws JAXBException {
            Object obj = null;
            final AttachmentUnmarshallerImpl au = (att != null) ? new AttachmentUnmarshallerImpl(att) : null;
            if (this.bridge instanceof RepeatedElementBridge) {
                final RepeatedElementBridge rbridge = (RepeatedElementBridge)this.bridge;
                final ArrayList list = new ArrayList();
                final QName name = r.getName();
                while (r.getEventType() == 1 && name.equals(r.getName())) {
                    list.add(rbridge.unmarshal(r, au));
                    XMLStreamReaderUtil.toNextTag(r, name);
                }
                obj = rbridge.collectionHandler().convert(list);
            }
            else {
                obj = this.bridge.unmarshal(r, au);
            }
            this.setter.put(obj, args);
        }
    }
    
    public static final class NullSetter extends EndpointArgumentsBuilder
    {
        private final EndpointValueSetter setter;
        private final Object nullValue;
        
        public NullSetter(final EndpointValueSetter setter, final Object nullValue) {
            assert setter != null;
            this.nullValue = nullValue;
            this.setter = setter;
        }
        
        @Override
        public void readRequest(final Message msg, final Object[] args) {
            this.setter.put(this.nullValue, args);
        }
    }
    
    public static final class Composite extends EndpointArgumentsBuilder
    {
        private final EndpointArgumentsBuilder[] builders;
        
        public Composite(final EndpointArgumentsBuilder... builders) {
            this.builders = builders;
        }
        
        public Composite(final Collection<? extends EndpointArgumentsBuilder> builders) {
            this((EndpointArgumentsBuilder[])builders.toArray(new EndpointArgumentsBuilder[builders.size()]));
        }
        
        @Override
        public void readRequest(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
            for (final EndpointArgumentsBuilder builder : this.builders) {
                builder.readRequest(msg, args);
            }
        }
    }
    
    public abstract static class AttachmentBuilder extends EndpointArgumentsBuilder
    {
        protected final EndpointValueSetter setter;
        protected final ParameterImpl param;
        protected final String pname;
        protected final String pname1;
        
        AttachmentBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            this.setter = setter;
            this.param = param;
            this.pname = param.getPartName();
            this.pname1 = "<" + this.pname;
        }
        
        public static EndpointArgumentsBuilder createAttachmentBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            final Class type = (Class)param.getTypeInfo().type;
            if (DataHandler.class.isAssignableFrom(type)) {
                return new DataHandlerBuilder(param, setter);
            }
            if (byte[].class == type) {
                return new ByteArrayBuilder(param, setter);
            }
            if (Source.class.isAssignableFrom(type)) {
                return new SourceBuilder(param, setter);
            }
            if (Image.class.isAssignableFrom(type)) {
                return new ImageBuilder(param, setter);
            }
            if (InputStream.class == type) {
                return new InputStreamBuilder(param, setter);
            }
            if (isXMLMimeType(param.getBinding().getMimeType())) {
                return new JAXBBuilder(param, setter);
            }
            if (String.class.isAssignableFrom(type)) {
                return new StringBuilder(param, setter);
            }
            throw new UnsupportedOperationException("Unknown Type=" + type + " Attachment is not mapped.");
        }
        
        @Override
        public void readRequest(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
            boolean foundAttachment = false;
            for (final Attachment att : msg.getAttachments()) {
                final String part = EndpointArgumentsBuilder.getWSDLPartName(att);
                if (part == null) {
                    continue;
                }
                if (part.equals(this.pname) || part.equals(this.pname1)) {
                    foundAttachment = true;
                    this.mapAttachment(att, args);
                    break;
                }
            }
            if (!foundAttachment) {
                throw new WebServiceException("Missing Attachment for " + this.pname);
            }
        }
        
        abstract void mapAttachment(final Attachment p0, final Object[] p1) throws JAXBException;
    }
    
    private static final class DataHandlerBuilder extends AttachmentBuilder
    {
        DataHandlerBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        void mapAttachment(final Attachment att, final Object[] args) {
            this.setter.put(att.asDataHandler(), args);
        }
    }
    
    private static final class ByteArrayBuilder extends AttachmentBuilder
    {
        ByteArrayBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        void mapAttachment(final Attachment att, final Object[] args) {
            this.setter.put(att.asByteArray(), args);
        }
    }
    
    private static final class SourceBuilder extends AttachmentBuilder
    {
        SourceBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        void mapAttachment(final Attachment att, final Object[] args) {
            this.setter.put(att.asSource(), args);
        }
    }
    
    private static final class ImageBuilder extends AttachmentBuilder
    {
        ImageBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        void mapAttachment(final Attachment att, final Object[] args) {
            InputStream is = null;
            Image image;
            try {
                is = att.asInputStream();
                image = ImageIO.read(is);
            }
            catch (final IOException ioe) {
                throw new WebServiceException(ioe);
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (final IOException ioe2) {
                        throw new WebServiceException(ioe2);
                    }
                }
            }
            this.setter.put(image, args);
        }
    }
    
    private static final class InputStreamBuilder extends AttachmentBuilder
    {
        InputStreamBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        void mapAttachment(final Attachment att, final Object[] args) {
            this.setter.put(att.asInputStream(), args);
        }
    }
    
    private static final class JAXBBuilder extends AttachmentBuilder
    {
        JAXBBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        void mapAttachment(final Attachment att, final Object[] args) throws JAXBException {
            final Object obj = this.param.getXMLBridge().unmarshal(att.asInputStream());
            this.setter.put(obj, args);
        }
    }
    
    private static final class StringBuilder extends AttachmentBuilder
    {
        StringBuilder(final ParameterImpl param, final EndpointValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        void mapAttachment(final Attachment att, final Object[] args) {
            att.getContentType();
            final StringDataContentHandler sdh = new StringDataContentHandler();
            try {
                final String str = (String)sdh.getContent(new DataHandlerDataSource(att.asDataHandler()));
                this.setter.put(str, args);
            }
            catch (final Exception e) {
                throw new WebServiceException(e);
            }
        }
    }
    
    public static final class Header extends EndpointArgumentsBuilder
    {
        private final XMLBridge<?> bridge;
        private final EndpointValueSetter setter;
        private final QName headerName;
        private final SOAPVersion soapVersion;
        
        public Header(final SOAPVersion soapVersion, final QName name, final XMLBridge<?> bridge, final EndpointValueSetter setter) {
            this.soapVersion = soapVersion;
            this.headerName = name;
            this.bridge = bridge;
            this.setter = setter;
        }
        
        public Header(final SOAPVersion soapVersion, final ParameterImpl param, final EndpointValueSetter setter) {
            this(soapVersion, param.getTypeInfo().tagName, param.getXMLBridge(), setter);
            assert param.getOutBinding() == ParameterBinding.HEADER;
        }
        
        private SOAPFaultException createDuplicateHeaderException() {
            try {
                final SOAPFault fault = this.soapVersion.getSOAPFactory().createFault();
                fault.setFaultCode(this.soapVersion.faultCodeClient);
                fault.setFaultString(ServerMessages.DUPLICATE_PORT_KNOWN_HEADER(this.headerName));
                return new SOAPFaultException(fault);
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public void readRequest(final Message msg, final Object[] args) throws JAXBException {
            com.sun.xml.internal.ws.api.message.Header header = null;
            final Iterator<com.sun.xml.internal.ws.api.message.Header> it = msg.getHeaders().getHeaders(this.headerName, true);
            if (it.hasNext()) {
                header = it.next();
                if (it.hasNext()) {
                    throw this.createDuplicateHeaderException();
                }
            }
            if (header != null) {
                this.setter.put(header.readAsJAXB(this.bridge), args);
            }
        }
    }
    
    public static final class Body extends EndpointArgumentsBuilder
    {
        private final XMLBridge<?> bridge;
        private final EndpointValueSetter setter;
        
        public Body(final XMLBridge<?> bridge, final EndpointValueSetter setter) {
            this.bridge = bridge;
            this.setter = setter;
        }
        
        @Override
        public void readRequest(final Message msg, final Object[] args) throws JAXBException {
            this.setter.put(msg.readPayloadAsJAXB(this.bridge), args);
        }
    }
    
    public static final class DocLit extends EndpointArgumentsBuilder
    {
        private final PartBuilder[] parts;
        private final XMLBridge wrapper;
        private boolean dynamicWrapper;
        
        public DocLit(final WrapperParameter wp, final WebParam.Mode skipMode) {
            this.wrapperName = wp.getName();
            this.wrapper = wp.getXMLBridge();
            final Class wrapperType = (Class)this.wrapper.getTypeInfo().type;
            this.dynamicWrapper = WrapperComposite.class.equals(wrapperType);
            final List<PartBuilder> parts = new ArrayList<PartBuilder>();
            final List<ParameterImpl> children = wp.getWrapperChildren();
            for (final ParameterImpl p : children) {
                if (p.getMode() == skipMode) {
                    continue;
                }
                final QName name = p.getName();
                try {
                    if (this.dynamicWrapper) {
                        if (this.wrappedParts == null) {
                            this.wrappedParts = new HashMap<QName, WrappedPartBuilder>();
                        }
                        XMLBridge xmlBridge = p.getInlinedRepeatedElementBridge();
                        if (xmlBridge == null) {
                            xmlBridge = p.getXMLBridge();
                        }
                        this.wrappedParts.put(p.getName(), new WrappedPartBuilder(xmlBridge, EndpointValueSetter.get(p)));
                    }
                    else {
                        parts.add(new PartBuilder(wp.getOwner().getBindingContext().getElementPropertyAccessor((Class<Object>)wrapperType, name.getNamespaceURI(), p.getName().getLocalPart()), EndpointValueSetter.get(p)));
                        assert p.getBinding() == ParameterBinding.BODY;
                        continue;
                    }
                }
                catch (final JAXBException e) {
                    throw new WebServiceException(wrapperType + " do not have a property of the name " + name, e);
                }
            }
            this.parts = parts.toArray(new PartBuilder[parts.size()]);
        }
        
        @Override
        public void readRequest(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
            if (this.dynamicWrapper) {
                this.readWrappedRequest(msg, args);
            }
            else if (this.parts.length > 0) {
                if (!msg.hasPayload()) {
                    throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
                }
                final XMLStreamReader reader = msg.readPayload();
                XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
                final Object wrapperBean = this.wrapper.unmarshal(reader, (msg.getAttachments() != null) ? new AttachmentUnmarshallerImpl(msg.getAttachments()) : null);
                try {
                    for (final PartBuilder part : this.parts) {
                        part.readRequest(args, wrapperBean);
                    }
                }
                catch (final DatabindingException e) {
                    throw new WebServiceException(e);
                }
                reader.close();
                XMLStreamReaderFactory.recycle(reader);
            }
            else {
                msg.consume();
            }
        }
        
        static final class PartBuilder
        {
            private final PropertyAccessor accessor;
            private final EndpointValueSetter setter;
            
            public PartBuilder(final PropertyAccessor accessor, final EndpointValueSetter setter) {
                this.accessor = accessor;
                this.setter = setter;
                assert accessor != null && setter != null;
            }
            
            final void readRequest(final Object[] args, final Object wrapperBean) {
                final Object obj = this.accessor.get(wrapperBean);
                this.setter.put(obj, args);
            }
        }
    }
    
    public static final class RpcLit extends EndpointArgumentsBuilder
    {
        public RpcLit(final WrapperParameter wp) {
            assert wp.getTypeInfo().type == WrapperComposite.class;
            this.wrapperName = wp.getName();
            this.wrappedParts = new HashMap<QName, WrappedPartBuilder>();
            final List<ParameterImpl> children = wp.getWrapperChildren();
            for (final ParameterImpl p : children) {
                this.wrappedParts.put(p.getName(), new WrappedPartBuilder(p.getXMLBridge(), EndpointValueSetter.get(p)));
                assert p.getBinding() == ParameterBinding.BODY;
            }
        }
        
        @Override
        public void readRequest(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
            this.readWrappedRequest(msg, args);
        }
    }
}
