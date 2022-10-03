package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.api.message.Header;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.ws.resources.ServerMessages;
import javax.xml.ws.soap.SOAPFaultException;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.SOAPVersion;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.encoding.DataHandlerDataSource;
import com.sun.xml.internal.ws.encoding.StringDataContentHandler;
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
import java.lang.reflect.Type;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.ws.WebServiceException;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.namespace.QName;
import java.util.Map;

public abstract class ResponseBuilder
{
    protected Map<QName, WrappedPartBuilder> wrappedParts;
    protected QName wrapperName;
    public static final ResponseBuilder NONE;
    private static final Map<Class, Object> primitiveUninitializedValues;
    
    public ResponseBuilder() {
        this.wrappedParts = null;
    }
    
    public abstract Object readResponse(final Message p0, final Object[] p1) throws JAXBException, XMLStreamException;
    
    protected Object readWrappedResponse(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
        Object retVal = null;
        if (!msg.hasPayload()) {
            throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
        }
        final XMLStreamReader reader = msg.readPayload();
        XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
        reader.nextTag();
        while (reader.getEventType() == 1) {
            final WrappedPartBuilder part = this.wrappedParts.get(reader.getName());
            if (part == null) {
                XMLStreamReaderUtil.skipElement(reader);
                reader.nextTag();
            }
            else {
                final Object o = part.readResponse(args, reader, msg.getAttachments());
                if (o != null) {
                    assert retVal == null;
                    retVal = o;
                }
            }
            if (reader.getEventType() != 1 && reader.getEventType() != 2) {
                XMLStreamReaderUtil.nextElementContent(reader);
            }
        }
        reader.close();
        XMLStreamReaderFactory.recycle(reader);
        return retVal;
    }
    
    public static Object getVMUninitializedValue(final Type type) {
        return ResponseBuilder.primitiveUninitializedValues.get(type);
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
        final Map<Class, Object> m = ResponseBuilder.primitiveUninitializedValues;
        m.put(Integer.TYPE, 0);
        m.put(Character.TYPE, '\0');
        m.put(Byte.TYPE, 0);
        m.put(Short.TYPE, 0);
        m.put(Long.TYPE, 0L);
        m.put(Float.TYPE, 0.0f);
        m.put(Double.TYPE, 0.0);
    }
    
    static final class WrappedPartBuilder
    {
        private final XMLBridge bridge;
        private final ValueSetter setter;
        
        public WrappedPartBuilder(final XMLBridge bridge, final ValueSetter setter) {
            this.bridge = bridge;
            this.setter = setter;
        }
        
        final Object readResponse(final Object[] args, final XMLStreamReader r, final AttachmentSet att) throws JAXBException {
            final AttachmentUnmarshallerImpl au = (att != null) ? new AttachmentUnmarshallerImpl(att) : null;
            Object obj;
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
            return this.setter.put(obj, args);
        }
    }
    
    static final class None extends ResponseBuilder
    {
        private None() {
        }
        
        @Override
        public Object readResponse(final Message msg, final Object[] args) {
            msg.consume();
            return null;
        }
    }
    
    public static final class NullSetter extends ResponseBuilder
    {
        private final ValueSetter setter;
        private final Object nullValue;
        
        public NullSetter(final ValueSetter setter, final Object nullValue) {
            assert setter != null;
            this.nullValue = nullValue;
            this.setter = setter;
        }
        
        @Override
        public Object readResponse(final Message msg, final Object[] args) {
            return this.setter.put(this.nullValue, args);
        }
    }
    
    public static final class Composite extends ResponseBuilder
    {
        private final ResponseBuilder[] builders;
        
        public Composite(final ResponseBuilder... builders) {
            this.builders = builders;
        }
        
        public Composite(final Collection<? extends ResponseBuilder> builders) {
            this((ResponseBuilder[])builders.toArray(new ResponseBuilder[builders.size()]));
        }
        
        @Override
        public Object readResponse(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
            Object retVal = null;
            for (final ResponseBuilder builder : this.builders) {
                final Object r = builder.readResponse(msg, args);
                if (r != null) {
                    assert retVal == null;
                    retVal = r;
                }
            }
            return retVal;
        }
    }
    
    public abstract static class AttachmentBuilder extends ResponseBuilder
    {
        protected final ValueSetter setter;
        protected final ParameterImpl param;
        private final String pname;
        private final String pname1;
        
        AttachmentBuilder(final ParameterImpl param, final ValueSetter setter) {
            this.setter = setter;
            this.param = param;
            this.pname = param.getPartName();
            this.pname1 = "<" + this.pname;
        }
        
        public static ResponseBuilder createAttachmentBuilder(final ParameterImpl param, final ValueSetter setter) {
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
            throw new UnsupportedOperationException("Unexpected Attachment type =" + type);
        }
        
        @Override
        public Object readResponse(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
            for (final Attachment att : msg.getAttachments()) {
                final String part = ResponseBuilder.getWSDLPartName(att);
                if (part == null) {
                    continue;
                }
                if (part.equals(this.pname) || part.equals(this.pname1)) {
                    return this.mapAttachment(att, args);
                }
            }
            return null;
        }
        
        abstract Object mapAttachment(final Attachment p0, final Object[] p1) throws JAXBException;
    }
    
    private static final class DataHandlerBuilder extends AttachmentBuilder
    {
        DataHandlerBuilder(final ParameterImpl param, final ValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        Object mapAttachment(final Attachment att, final Object[] args) {
            return this.setter.put(att.asDataHandler(), args);
        }
    }
    
    private static final class StringBuilder extends AttachmentBuilder
    {
        StringBuilder(final ParameterImpl param, final ValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        Object mapAttachment(final Attachment att, final Object[] args) {
            att.getContentType();
            final StringDataContentHandler sdh = new StringDataContentHandler();
            try {
                final String str = (String)sdh.getContent(new DataHandlerDataSource(att.asDataHandler()));
                return this.setter.put(str, args);
            }
            catch (final Exception e) {
                throw new WebServiceException(e);
            }
        }
    }
    
    private static final class ByteArrayBuilder extends AttachmentBuilder
    {
        ByteArrayBuilder(final ParameterImpl param, final ValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        Object mapAttachment(final Attachment att, final Object[] args) {
            return this.setter.put(att.asByteArray(), args);
        }
    }
    
    private static final class SourceBuilder extends AttachmentBuilder
    {
        SourceBuilder(final ParameterImpl param, final ValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        Object mapAttachment(final Attachment att, final Object[] args) {
            return this.setter.put(att.asSource(), args);
        }
    }
    
    private static final class ImageBuilder extends AttachmentBuilder
    {
        ImageBuilder(final ParameterImpl param, final ValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        Object mapAttachment(final Attachment att, final Object[] args) {
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
            return this.setter.put(image, args);
        }
    }
    
    private static final class InputStreamBuilder extends AttachmentBuilder
    {
        InputStreamBuilder(final ParameterImpl param, final ValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        Object mapAttachment(final Attachment att, final Object[] args) {
            return this.setter.put(att.asInputStream(), args);
        }
    }
    
    private static final class JAXBBuilder extends AttachmentBuilder
    {
        JAXBBuilder(final ParameterImpl param, final ValueSetter setter) {
            super(param, setter);
        }
        
        @Override
        Object mapAttachment(final Attachment att, final Object[] args) throws JAXBException {
            final Object obj = this.param.getXMLBridge().unmarshal(att.asInputStream());
            return this.setter.put(obj, args);
        }
    }
    
    public static final class Header extends ResponseBuilder
    {
        private final XMLBridge<?> bridge;
        private final ValueSetter setter;
        private final QName headerName;
        private final SOAPVersion soapVersion;
        
        public Header(final SOAPVersion soapVersion, final QName name, final XMLBridge<?> bridge, final ValueSetter setter) {
            this.soapVersion = soapVersion;
            this.headerName = name;
            this.bridge = bridge;
            this.setter = setter;
        }
        
        public Header(final SOAPVersion soapVersion, final ParameterImpl param, final ValueSetter setter) {
            this(soapVersion, param.getTypeInfo().tagName, param.getXMLBridge(), setter);
            assert param.getOutBinding() == ParameterBinding.HEADER;
        }
        
        private SOAPFaultException createDuplicateHeaderException() {
            try {
                final SOAPFault fault = this.soapVersion.getSOAPFactory().createFault();
                fault.setFaultCode(this.soapVersion.faultCodeServer);
                fault.setFaultString(ServerMessages.DUPLICATE_PORT_KNOWN_HEADER(this.headerName));
                return new SOAPFaultException(fault);
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public Object readResponse(final Message msg, final Object[] args) throws JAXBException {
            com.sun.xml.internal.ws.api.message.Header header = null;
            final Iterator<com.sun.xml.internal.ws.api.message.Header> it = msg.getHeaders().getHeaders(this.headerName, true);
            if (it.hasNext()) {
                header = it.next();
                if (it.hasNext()) {
                    throw this.createDuplicateHeaderException();
                }
            }
            if (header != null) {
                return this.setter.put(header.readAsJAXB(this.bridge), args);
            }
            return null;
        }
    }
    
    public static final class Body extends ResponseBuilder
    {
        private final XMLBridge<?> bridge;
        private final ValueSetter setter;
        
        public Body(final XMLBridge<?> bridge, final ValueSetter setter) {
            this.bridge = bridge;
            this.setter = setter;
        }
        
        @Override
        public Object readResponse(final Message msg, final Object[] args) throws JAXBException {
            return this.setter.put(msg.readPayloadAsJAXB(this.bridge), args);
        }
    }
    
    public static final class DocLit extends ResponseBuilder
    {
        private final PartBuilder[] parts;
        private final XMLBridge wrapper;
        private boolean dynamicWrapper;
        
        public DocLit(final WrapperParameter wp, final ValueSetterFactory setterFactory) {
            this.wrapperName = wp.getName();
            this.wrapper = wp.getXMLBridge();
            final Class wrapperType = (Class)this.wrapper.getTypeInfo().type;
            this.dynamicWrapper = WrapperComposite.class.equals(wrapperType);
            final List<PartBuilder> tempParts = new ArrayList<PartBuilder>();
            final List<ParameterImpl> children = wp.getWrapperChildren();
            for (final ParameterImpl p : children) {
                if (p.isIN()) {
                    continue;
                }
                final QName name = p.getName();
                if (this.dynamicWrapper) {
                    if (this.wrappedParts == null) {
                        this.wrappedParts = new HashMap<QName, WrappedPartBuilder>();
                    }
                    XMLBridge xmlBridge = p.getInlinedRepeatedElementBridge();
                    if (xmlBridge == null) {
                        xmlBridge = p.getXMLBridge();
                    }
                    this.wrappedParts.put(p.getName(), new WrappedPartBuilder(xmlBridge, setterFactory.get(p)));
                }
                else {
                    try {
                        tempParts.add(new PartBuilder(wp.getOwner().getBindingContext().getElementPropertyAccessor((Class<Object>)wrapperType, name.getNamespaceURI(), p.getName().getLocalPart()), setterFactory.get(p)));
                        assert p.getBinding() == ParameterBinding.BODY;
                        continue;
                    }
                    catch (final JAXBException e) {
                        throw new WebServiceException(wrapperType + " do not have a property of the name " + name, e);
                    }
                }
            }
            this.parts = tempParts.toArray(new PartBuilder[tempParts.size()]);
        }
        
        @Override
        public Object readResponse(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
            if (this.dynamicWrapper) {
                return this.readWrappedResponse(msg, args);
            }
            Object retVal = null;
            if (this.parts.length > 0) {
                if (!msg.hasPayload()) {
                    throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element");
                }
                final XMLStreamReader reader = msg.readPayload();
                XMLStreamReaderUtil.verifyTag(reader, this.wrapperName);
                final Object wrapperBean = this.wrapper.unmarshal(reader, (msg.getAttachments() != null) ? new AttachmentUnmarshallerImpl(msg.getAttachments()) : null);
                try {
                    for (final PartBuilder part : this.parts) {
                        final Object o = part.readResponse(args, wrapperBean);
                        if (o != null) {
                            assert retVal == null;
                            retVal = o;
                        }
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
            return retVal;
        }
        
        static final class PartBuilder
        {
            private final PropertyAccessor accessor;
            private final ValueSetter setter;
            
            public PartBuilder(final PropertyAccessor accessor, final ValueSetter setter) {
                this.accessor = accessor;
                this.setter = setter;
                assert accessor != null && setter != null;
            }
            
            final Object readResponse(final Object[] args, final Object wrapperBean) {
                final Object obj = this.accessor.get(wrapperBean);
                return this.setter.put(obj, args);
            }
        }
    }
    
    public static final class RpcLit extends ResponseBuilder
    {
        public RpcLit(final WrapperParameter wp, final ValueSetterFactory setterFactory) {
            assert wp.getTypeInfo().type == WrapperComposite.class;
            this.wrapperName = wp.getName();
            this.wrappedParts = new HashMap<QName, WrappedPartBuilder>();
            final List<ParameterImpl> children = wp.getWrapperChildren();
            for (final ParameterImpl p : children) {
                this.wrappedParts.put(p.getName(), new WrappedPartBuilder(p.getXMLBridge(), setterFactory.get(p)));
                assert p.getBinding() == ParameterBinding.BODY;
            }
        }
        
        @Override
        public Object readResponse(final Message msg, final Object[] args) throws JAXBException, XMLStreamException {
            return this.readWrappedResponse(msg, args);
        }
    }
}
