package com.fasterxml.jackson.dataformat.xml;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import javax.xml.stream.XMLStreamWriter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.lang.reflect.Type;
import javax.xml.stream.XMLStreamReader;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.util.Iterator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;
import com.fasterxml.jackson.dataformat.xml.util.XmlRootNameLookup;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class XmlMapper extends ObjectMapper
{
    private static final long serialVersionUID = 1L;
    protected static final JacksonXmlModule DEFAULT_XML_MODULE;
    protected static final DefaultXmlPrettyPrinter DEFAULT_XML_PRETTY_PRINTER;
    protected final JacksonXmlModule _xmlModule;
    
    public XmlMapper() {
        this(new XmlFactory());
    }
    
    public XmlMapper(final XMLInputFactory inputF, final XMLOutputFactory outF) {
        this(new XmlFactory(inputF, outF));
    }
    
    public XmlMapper(final XMLInputFactory inputF) {
        this(new XmlFactory(inputF));
    }
    
    public XmlMapper(final XmlFactory xmlFactory) {
        this(xmlFactory, XmlMapper.DEFAULT_XML_MODULE);
    }
    
    public XmlMapper(final JacksonXmlModule module) {
        this(new XmlFactory(), module);
    }
    
    public XmlMapper(final XmlFactory xmlFactory, final JacksonXmlModule module) {
        super((JsonFactory)xmlFactory, (DefaultSerializerProvider)new XmlSerializerProvider(new XmlRootNameLookup()), (DefaultDeserializationContext)null);
        this._xmlModule = module;
        if (module != null) {
            this.registerModule((Module)module);
        }
        this._serializationConfig = this._serializationConfig.withDefaultPrettyPrinter((PrettyPrinter)XmlMapper.DEFAULT_XML_PRETTY_PRINTER);
        this.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }
    
    protected XmlMapper(final XmlMapper src) {
        super((ObjectMapper)src);
        this._xmlModule = src._xmlModule;
    }
    
    public XmlMapper copy() {
        this._checkInvalidCopy((Class)XmlMapper.class);
        return new XmlMapper(this);
    }
    
    public static Builder xmlBuilder() {
        return new Builder(new XmlMapper());
    }
    
    public static Builder builder() {
        return new Builder(new XmlMapper());
    }
    
    public static Builder builder(final XmlFactory streamFactory) {
        return new Builder(new XmlMapper(streamFactory));
    }
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    protected TypeResolverBuilder<?> _constructDefaultTypeResolverBuilder(final ObjectMapper.DefaultTyping applicability, final PolymorphicTypeValidator ptv) {
        return (TypeResolverBuilder<?>)new DefaultingXmlTypeResolverBuilder(applicability, ptv);
    }
    
    @Deprecated
    protected void setXMLTextElementName(final String name) {
        ((XmlFactory)this._jsonFactory).setXMLTextElementName(name);
    }
    
    @Deprecated
    public XmlMapper setDefaultUseWrapper(final boolean state) {
        final AnnotationIntrospector ai0 = this.getDeserializationConfig().getAnnotationIntrospector();
        for (final AnnotationIntrospector ai2 : ai0.allIntrospectors()) {
            if (ai2 instanceof XmlAnnotationIntrospector) {
                ((XmlAnnotationIntrospector)ai2).setDefaultUseWrapper(state);
            }
        }
        return this;
    }
    
    public XmlFactory getFactory() {
        return (XmlFactory)this._jsonFactory;
    }
    
    public ObjectMapper configure(final ToXmlGenerator.Feature f, final boolean state) {
        ((XmlFactory)this._jsonFactory).configure(f, state);
        return this;
    }
    
    public ObjectMapper configure(final FromXmlParser.Feature f, final boolean state) {
        ((XmlFactory)this._jsonFactory).configure(f, state);
        return this;
    }
    
    public ObjectMapper enable(final ToXmlGenerator.Feature f) {
        ((XmlFactory)this._jsonFactory).enable(f);
        return this;
    }
    
    public ObjectMapper enable(final FromXmlParser.Feature f) {
        ((XmlFactory)this._jsonFactory).enable(f);
        return this;
    }
    
    public ObjectMapper disable(final ToXmlGenerator.Feature f) {
        ((XmlFactory)this._jsonFactory).disable(f);
        return this;
    }
    
    public ObjectMapper disable(final FromXmlParser.Feature f) {
        ((XmlFactory)this._jsonFactory).disable(f);
        return this;
    }
    
    public <T> T readValue(final XMLStreamReader r, final Class<T> valueType) throws IOException {
        return this.readValue(r, this._typeFactory.constructType((Type)valueType));
    }
    
    public <T> T readValue(final XMLStreamReader r, final TypeReference<T> valueTypeRef) throws IOException {
        return this.readValue(r, this._typeFactory.constructType((TypeReference)valueTypeRef));
    }
    
    public <T> T readValue(final XMLStreamReader r, final JavaType valueType) throws IOException {
        final FromXmlParser p = this.getFactory().createParser(r);
        return (T)super.readValue((JsonParser)p, valueType);
    }
    
    public void writeValue(final XMLStreamWriter w0, final Object value) throws IOException {
        final ToXmlGenerator g = this.getFactory().createGenerator(w0);
        super.writeValue((JsonGenerator)g, value);
    }
    
    static {
        DEFAULT_XML_MODULE = new JacksonXmlModule();
        DEFAULT_XML_PRETTY_PRINTER = new DefaultXmlPrettyPrinter();
    }
    
    public static class Builder extends MapperBuilder<XmlMapper, Builder>
    {
        public Builder(final XmlMapper m) {
            super((ObjectMapper)m);
        }
        
        public Builder enable(final FromXmlParser.Feature... features) {
            for (final FromXmlParser.Feature f : features) {
                ((XmlMapper)this._mapper).enable(f);
            }
            return this;
        }
        
        public Builder disable(final FromXmlParser.Feature... features) {
            for (final FromXmlParser.Feature f : features) {
                ((XmlMapper)this._mapper).disable(f);
            }
            return this;
        }
        
        public Builder configure(final FromXmlParser.Feature feature, final boolean state) {
            if (state) {
                ((XmlMapper)this._mapper).enable(feature);
            }
            else {
                ((XmlMapper)this._mapper).disable(feature);
            }
            return this;
        }
        
        public Builder enable(final ToXmlGenerator.Feature... features) {
            for (final ToXmlGenerator.Feature f : features) {
                ((XmlMapper)this._mapper).enable(f);
            }
            return this;
        }
        
        public Builder disable(final ToXmlGenerator.Feature... features) {
            for (final ToXmlGenerator.Feature f : features) {
                ((XmlMapper)this._mapper).disable(f);
            }
            return this;
        }
        
        public Builder configure(final ToXmlGenerator.Feature feature, final boolean state) {
            if (state) {
                ((XmlMapper)this._mapper).enable(feature);
            }
            else {
                ((XmlMapper)this._mapper).disable(feature);
            }
            return this;
        }
        
        public Builder nameForTextElement(final String name) {
            ((XmlMapper)this._mapper).setXMLTextElementName(name);
            return this;
        }
        
        public Builder defaultUseWrapper(final boolean state) {
            ((XmlMapper)this._mapper).setDefaultUseWrapper(state);
            return this;
        }
    }
}
