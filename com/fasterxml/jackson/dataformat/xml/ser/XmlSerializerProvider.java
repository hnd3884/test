package com.fasterxml.jackson.dataformat.xml.ser;

import java.io.Closeable;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.databind.PropertyName;
import javax.xml.stream.XMLStreamException;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.dataformat.xml.util.TypeUtil;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.dataformat.xml.util.XmlRootNameLookup;
import javax.xml.namespace.QName;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

public class XmlSerializerProvider extends DefaultSerializerProvider
{
    private static final long serialVersionUID = 1L;
    protected static final QName ROOT_NAME_FOR_NULL;
    protected final XmlRootNameLookup _rootNameLookup;
    
    public XmlSerializerProvider(final XmlRootNameLookup rootNames) {
        this._rootNameLookup = rootNames;
    }
    
    public XmlSerializerProvider(final XmlSerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
        super((SerializerProvider)src, config, f);
        this._rootNameLookup = src._rootNameLookup;
    }
    
    protected XmlSerializerProvider(final XmlSerializerProvider src) {
        super((DefaultSerializerProvider)src);
        this._rootNameLookup = new XmlRootNameLookup();
    }
    
    public DefaultSerializerProvider copy() {
        return new XmlSerializerProvider(this);
    }
    
    public DefaultSerializerProvider createInstance(final SerializationConfig config, final SerializerFactory jsf) {
        return new XmlSerializerProvider(this, config, jsf);
    }
    
    public void serializeValue(final JsonGenerator gen, final Object value) throws IOException {
        this._generator = gen;
        if (value == null) {
            this._serializeXmlNull(gen);
            return;
        }
        final Class<?> cls = value.getClass();
        final ToXmlGenerator xgen = this._asXmlGenerator(gen);
        boolean asArray;
        if (xgen == null) {
            asArray = false;
        }
        else {
            QName rootName = this._rootNameFromConfig();
            if (rootName == null) {
                rootName = this._rootNameLookup.findRootName(cls, (MapperConfig<?>)this._config);
            }
            this._initWithRootName(xgen, rootName);
            asArray = TypeUtil.isIndexedType(cls);
            if (asArray) {
                this._startRootArray(xgen, rootName);
            }
        }
        final JsonSerializer<Object> ser = (JsonSerializer<Object>)this.findTypedValueSerializer((Class)cls, true, (BeanProperty)null);
        try {
            ser.serialize(value, gen, (SerializerProvider)this);
        }
        catch (final Exception e) {
            throw this._wrapAsIOE(gen, e);
        }
        if (asArray) {
            gen.writeEndObject();
        }
    }
    
    public void serializeValue(final JsonGenerator gen, final Object value, final JavaType rootType) throws IOException {
        this.serializeValue(gen, value, rootType, null);
    }
    
    public void serializeValue(final JsonGenerator gen, final Object value, final JavaType rootType, JsonSerializer<Object> ser) throws IOException {
        this._generator = gen;
        if (value == null) {
            this._serializeXmlNull(gen);
            return;
        }
        if (rootType != null && !rootType.getRawClass().isAssignableFrom(value.getClass())) {
            this._reportIncompatibleRootType(value, rootType);
        }
        final ToXmlGenerator xgen = this._asXmlGenerator(gen);
        boolean asArray;
        if (xgen == null) {
            asArray = false;
        }
        else {
            QName rootName = this._rootNameFromConfig();
            if (rootName == null) {
                rootName = this._rootNameLookup.findRootName(rootType, (MapperConfig<?>)this._config);
            }
            this._initWithRootName(xgen, rootName);
            asArray = TypeUtil.isIndexedType(rootType);
            if (asArray) {
                this._startRootArray(xgen, rootName);
            }
        }
        if (ser == null) {
            ser = (JsonSerializer<Object>)this.findTypedValueSerializer(rootType, true, (BeanProperty)null);
        }
        try {
            ser.serialize(value, gen, (SerializerProvider)this);
        }
        catch (final Exception e) {
            throw this._wrapAsIOE(gen, e);
        }
        if (asArray) {
            gen.writeEndObject();
        }
    }
    
    public void serializePolymorphic(final JsonGenerator gen, final Object value, final JavaType rootType, JsonSerializer<Object> valueSer, final TypeSerializer typeSer) throws IOException {
        this._generator = gen;
        if (value == null) {
            this._serializeXmlNull(gen);
            return;
        }
        if (rootType != null && !rootType.getRawClass().isAssignableFrom(value.getClass())) {
            this._reportIncompatibleRootType(value, rootType);
        }
        final ToXmlGenerator xgen = this._asXmlGenerator(gen);
        boolean asArray;
        if (xgen == null) {
            asArray = false;
        }
        else {
            QName rootName = this._rootNameFromConfig();
            if (rootName == null) {
                rootName = this._rootNameLookup.findRootName(rootType, (MapperConfig<?>)this._config);
            }
            this._initWithRootName(xgen, rootName);
            asArray = TypeUtil.isIndexedType(rootType);
            if (asArray) {
                this._startRootArray(xgen, rootName);
            }
        }
        if (valueSer == null) {
            if (rootType != null && rootType.isContainerType()) {
                valueSer = (JsonSerializer<Object>)this.findValueSerializer(rootType, (BeanProperty)null);
            }
            else {
                valueSer = (JsonSerializer<Object>)this.findValueSerializer((Class)value.getClass(), (BeanProperty)null);
            }
        }
        try {
            valueSer.serializeWithType(value, gen, (SerializerProvider)this, typeSer);
        }
        catch (final Exception e) {
            throw this._wrapAsIOE(gen, e);
        }
        if (asArray) {
            gen.writeEndObject();
        }
    }
    
    protected void _serializeXmlNull(final JsonGenerator jgen) throws IOException {
        QName rootName = this._rootNameFromConfig();
        if (rootName == null) {
            rootName = XmlSerializerProvider.ROOT_NAME_FOR_NULL;
        }
        if (jgen instanceof ToXmlGenerator) {
            this._initWithRootName((ToXmlGenerator)jgen, rootName);
        }
        super.serializeValue(jgen, (Object)null);
    }
    
    protected void _startRootArray(final ToXmlGenerator xgen, final QName rootName) throws IOException {
        xgen.writeStartObject();
        xgen.writeFieldName("item");
    }
    
    protected void _initWithRootName(final ToXmlGenerator xgen, final QName rootName) throws IOException {
        if (!xgen.setNextNameIfMissing(rootName) && xgen.inRoot()) {
            xgen.setNextName(rootName);
        }
        xgen.initGenerator();
        final String ns = rootName.getNamespaceURI();
        if (ns != null && ns.length() > 0) {
            try {
                xgen.getStaxWriter().setDefaultNamespace(ns);
            }
            catch (final XMLStreamException e) {
                StaxUtil.throwAsGenerationException(e, (JsonGenerator)xgen);
            }
        }
    }
    
    protected QName _rootNameFromConfig() {
        final PropertyName name = this._config.getFullRootName();
        if (name == null) {
            return null;
        }
        final String ns = name.getNamespace();
        if (ns == null || ns.isEmpty()) {
            return new QName(name.getSimpleName());
        }
        return new QName(ns, name.getSimpleName());
    }
    
    protected ToXmlGenerator _asXmlGenerator(final JsonGenerator gen) throws JsonMappingException {
        if (gen instanceof ToXmlGenerator) {
            return (ToXmlGenerator)gen;
        }
        if (!(gen instanceof TokenBuffer)) {
            throw JsonMappingException.from(gen, "XmlMapper does not with generators of type other than ToXmlGenerator; got: " + gen.getClass().getName());
        }
        return null;
    }
    
    protected IOException _wrapAsIOE(final JsonGenerator g, final Exception e) {
        if (e instanceof IOException) {
            return (IOException)e;
        }
        String msg = e.getMessage();
        if (msg == null) {
            msg = "[no message for " + e.getClass().getName() + "]";
        }
        return (IOException)new JsonMappingException((Closeable)g, msg, (Throwable)e);
    }
    
    static {
        ROOT_NAME_FOR_NULL = new QName("null");
    }
}
