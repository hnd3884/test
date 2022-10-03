package com.fasterxml.jackson.dataformat.xml.ser;

import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.Set;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.dataformat.xml.util.XmlInfo;
import java.util.BitSet;
import javax.xml.namespace.QName;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public abstract class XmlBeanSerializerBase extends BeanSerializerBase
{
    public static final String KEY_XML_INFO;
    protected final int _attributeCount;
    protected final int _textPropertyIndex;
    protected final QName[] _xmlNames;
    protected final BitSet _cdata;
    
    public XmlBeanSerializerBase(final BeanSerializerBase src) {
        super(src);
        int attrCount = 0;
        for (final BeanPropertyWriter bpw : this._props) {
            if (_isAttribute(bpw)) {
                attrCount = _orderAttributesFirst(this._props, this._filteredProps);
                break;
            }
        }
        this._attributeCount = attrCount;
        BitSet cdata = null;
        for (int i = 0, len = this._props.length; i < len; ++i) {
            final BeanPropertyWriter bpw = this._props[i];
            if (_isCData(bpw)) {
                if (cdata == null) {
                    cdata = new BitSet(len);
                }
                cdata.set(i);
            }
        }
        this._cdata = cdata;
        this._xmlNames = new QName[this._props.length];
        int textIndex = -1;
        for (int j = 0, len2 = this._props.length; j < len2; ++j) {
            final BeanPropertyWriter bpw2 = this._props[j];
            final XmlInfo info = (XmlInfo)bpw2.getInternalSetting((Object)XmlBeanSerializerBase.KEY_XML_INFO);
            String ns = null;
            if (info != null) {
                ns = info.getNamespace();
                if (textIndex < 0 && info.isText()) {
                    textIndex = j;
                }
            }
            this._xmlNames[j] = new QName((ns == null) ? "" : ns, bpw2.getName());
        }
        this._textPropertyIndex = textIndex;
    }
    
    protected XmlBeanSerializerBase(final XmlBeanSerializerBase src, final ObjectIdWriter objectIdWriter) {
        super((BeanSerializerBase)src, objectIdWriter);
        this._attributeCount = src._attributeCount;
        this._textPropertyIndex = src._textPropertyIndex;
        this._xmlNames = src._xmlNames;
        this._cdata = src._cdata;
    }
    
    protected XmlBeanSerializerBase(final XmlBeanSerializerBase src, final ObjectIdWriter objectIdWriter, final Object filterId) {
        super((BeanSerializerBase)src, objectIdWriter, filterId);
        this._attributeCount = src._attributeCount;
        this._textPropertyIndex = src._textPropertyIndex;
        this._xmlNames = src._xmlNames;
        this._cdata = src._cdata;
    }
    
    protected XmlBeanSerializerBase(final XmlBeanSerializerBase src, final Set<String> toIgnore) {
        super((BeanSerializerBase)src, (Set)toIgnore);
        this._attributeCount = src._attributeCount;
        this._textPropertyIndex = src._textPropertyIndex;
        this._xmlNames = src._xmlNames;
        this._cdata = src._cdata;
    }
    
    public XmlBeanSerializerBase(final XmlBeanSerializerBase src, final NameTransformer transformer) {
        super((BeanSerializerBase)src, transformer);
        this._attributeCount = src._attributeCount;
        this._textPropertyIndex = src._textPropertyIndex;
        this._xmlNames = src._xmlNames;
        this._cdata = src._cdata;
    }
    
    protected XmlBeanSerializerBase(final XmlBeanSerializerBase src, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super((BeanSerializerBase)src, properties, filteredProperties);
        this._attributeCount = src._attributeCount;
        this._textPropertyIndex = src._textPropertyIndex;
        this._xmlNames = src._xmlNames;
        this._cdata = src._cdata;
    }
    
    protected void serializeFields(final Object bean, final JsonGenerator gen0, final SerializerProvider provider) throws IOException {
        if (!(gen0 instanceof ToXmlGenerator)) {
            super.serializeFields(bean, gen0, provider);
            return;
        }
        final ToXmlGenerator xgen = (ToXmlGenerator)gen0;
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        final int attrCount = this._attributeCount;
        final boolean isAttribute = xgen._nextIsAttribute;
        if (attrCount > 0) {
            xgen.setNextIsAttribute(true);
        }
        final int textIndex = this._textPropertyIndex;
        final QName[] xmlNames = this._xmlNames;
        int i = 0;
        final BitSet cdata = this._cdata;
        try {
            for (int len = props.length; i < len; ++i) {
                if (i == attrCount && (!isAttribute || !this.isUnwrappingSerializer())) {
                    xgen.setNextIsAttribute(false);
                }
                if (i == textIndex) {
                    xgen.setNextIsUnwrapped(true);
                }
                xgen.setNextName(xmlNames[i]);
                final BeanPropertyWriter prop = props[i];
                if (prop != null) {
                    if (cdata != null && cdata.get(i)) {
                        xgen.setNextIsCData(true);
                        prop.serializeAsField(bean, (JsonGenerator)xgen, provider);
                        xgen.setNextIsCData(false);
                    }
                    else {
                        prop.serializeAsField(bean, (JsonGenerator)xgen, provider);
                    }
                }
                if (i == textIndex) {
                    xgen.setNextIsUnwrapped(false);
                }
            }
            if (this._anyGetterWriter != null) {
                xgen.setNextIsAttribute(false);
                this._anyGetterWriter.getAndSerialize(bean, (JsonGenerator)xgen, provider);
            }
        }
        catch (final Exception e) {
            final String name = (i == props.length) ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, (Throwable)e, bean, name);
        }
        catch (final StackOverflowError e2) {
            final JsonMappingException mapE = JsonMappingException.from(gen0, "Infinite recursion (StackOverflowError)");
            final String name2 = (i == props.length) ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name2));
            throw mapE;
        }
    }
    
    protected void serializeFieldsFiltered(final Object bean, final JsonGenerator gen0, final SerializerProvider provider) throws IOException {
        if (!(gen0 instanceof ToXmlGenerator)) {
            super.serializeFieldsFiltered(bean, gen0, provider);
            return;
        }
        final ToXmlGenerator xgen = (ToXmlGenerator)gen0;
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        final PropertyFilter filter = this.findPropertyFilter(provider, this._propertyFilterId, bean);
        if (filter == null) {
            this.serializeFields(bean, gen0, provider);
            return;
        }
        final boolean isAttribute = xgen._nextIsAttribute;
        final int attrCount = this._attributeCount;
        if (attrCount > 0) {
            xgen.setNextIsAttribute(true);
        }
        final int textIndex = this._textPropertyIndex;
        final QName[] xmlNames = this._xmlNames;
        final BitSet cdata = this._cdata;
        int i = 0;
        try {
            for (int len = props.length; i < len; ++i) {
                if (i == attrCount && (!isAttribute || !this.isUnwrappingSerializer())) {
                    xgen.setNextIsAttribute(false);
                }
                if (i == textIndex) {
                    xgen.setNextIsUnwrapped(true);
                }
                xgen.setNextName(xmlNames[i]);
                final BeanPropertyWriter prop = props[i];
                if (prop != null) {
                    if (cdata != null && cdata.get(i)) {
                        xgen.setNextIsCData(true);
                        filter.serializeAsField(bean, (JsonGenerator)xgen, provider, (PropertyWriter)prop);
                        xgen.setNextIsCData(false);
                    }
                    else {
                        filter.serializeAsField(bean, (JsonGenerator)xgen, provider, (PropertyWriter)prop);
                    }
                }
            }
            if (this._anyGetterWriter != null) {
                xgen.setNextIsAttribute(false);
                this._anyGetterWriter.getAndFilter(bean, (JsonGenerator)xgen, provider, filter);
            }
        }
        catch (final Exception e) {
            final String name = (i == props.length) ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, (Throwable)e, bean, name);
        }
        catch (final StackOverflowError e2) {
            final JsonMappingException mapE = JsonMappingException.from(gen0, "Infinite recursion (StackOverflowError)", (Throwable)e2);
            final String name2 = (i == props.length) ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name2));
            throw mapE;
        }
    }
    
    public void serializeWithType(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        if (this._objectIdWriter != null) {
            this._serializeWithObjectId(bean, gen, provider, typeSer);
            return;
        }
        if (typeSer.getTypeInclusion() == JsonTypeInfo.As.PROPERTY) {
            final ToXmlGenerator xgen = (ToXmlGenerator)gen;
            xgen.setNextIsAttribute(true);
            super.serializeWithType(bean, gen, provider, typeSer);
            if (this._attributeCount == 0) {
                xgen.setNextIsAttribute(false);
            }
        }
        else {
            super.serializeWithType(bean, gen, provider, typeSer);
        }
    }
    
    protected void _serializeObjectId(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer, final WritableObjectId objectId) throws IOException {
        if (typeSer.getTypeInclusion() == JsonTypeInfo.As.PROPERTY) {
            final ToXmlGenerator xgen = (ToXmlGenerator)gen;
            xgen.setNextIsAttribute(true);
            super._serializeObjectId(bean, gen, provider, typeSer, objectId);
            if (this._attributeCount == 0) {
                xgen.setNextIsAttribute(false);
            }
        }
        else {
            super._serializeObjectId(bean, gen, provider, typeSer, objectId);
        }
    }
    
    protected static boolean _isAttribute(final BeanPropertyWriter bpw) {
        final XmlInfo info = (XmlInfo)bpw.getInternalSetting((Object)XmlBeanSerializerBase.KEY_XML_INFO);
        return info != null && info.isAttribute();
    }
    
    protected static boolean _isCData(final BeanPropertyWriter bpw) {
        final XmlInfo info = (XmlInfo)bpw.getInternalSetting((Object)XmlBeanSerializerBase.KEY_XML_INFO);
        return info != null && info.isCData();
    }
    
    protected static int _orderAttributesFirst(final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        int attrCount = 0;
        for (int i = 0, len = properties.length; i < len; ++i) {
            final BeanPropertyWriter bpw = properties[i];
            if (_isAttribute(bpw)) {
                final int moveBy = i - attrCount;
                if (moveBy > 0) {
                    System.arraycopy(properties, attrCount, properties, attrCount + 1, moveBy);
                    properties[attrCount] = bpw;
                    if (filteredProperties != null) {
                        final BeanPropertyWriter fbpw = filteredProperties[i];
                        System.arraycopy(filteredProperties, attrCount, filteredProperties, attrCount + 1, moveBy);
                        filteredProperties[attrCount] = fbpw;
                    }
                }
                ++attrCount;
            }
        }
        return attrCount;
    }
    
    static {
        KEY_XML_INFO = new String("xmlInfo");
    }
}
