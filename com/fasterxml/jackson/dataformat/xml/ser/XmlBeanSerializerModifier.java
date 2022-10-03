package com.fasterxml.jackson.dataformat.xml.ser;

import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.dataformat.xml.util.TypeUtil;
import com.fasterxml.jackson.dataformat.xml.util.XmlInfo;
import com.fasterxml.jackson.dataformat.xml.util.AnnotationUtil;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import java.util.List;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import java.io.Serializable;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public class XmlBeanSerializerModifier extends BeanSerializerModifier implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public List<BeanPropertyWriter> changeProperties(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyWriter> beanProperties) {
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        for (int i = 0, len = beanProperties.size(); i < len; ++i) {
            final BeanPropertyWriter bpw = beanProperties.get(i);
            final AnnotatedMember member = bpw.getMember();
            final String ns = AnnotationUtil.findNamespaceAnnotation(intr, member);
            final Boolean isAttribute = AnnotationUtil.findIsAttributeAnnotation(intr, member);
            final Boolean isText = AnnotationUtil.findIsTextAnnotation(intr, member);
            final Boolean isCData = AnnotationUtil.findIsCDataAnnotation(intr, member);
            bpw.setInternalSetting((Object)XmlBeanSerializerBase.KEY_XML_INFO, (Object)new XmlInfo(isAttribute, ns, isText, isCData));
            if (TypeUtil.isIndexedType(bpw.getType())) {
                final PropertyName wrappedName = PropertyName.construct(bpw.getName(), ns);
                PropertyName wrapperName = bpw.getWrapperName();
                if (wrapperName != null) {
                    if (wrapperName != PropertyName.NO_NAME) {
                        final String localName = wrapperName.getSimpleName();
                        if (localName == null || localName.length() == 0) {
                            wrapperName = wrappedName;
                        }
                        beanProperties.set(i, new XmlBeanPropertyWriter(bpw, wrapperName, wrappedName));
                    }
                }
            }
        }
        return beanProperties;
    }
    
    public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        if (!(serializer instanceof BeanSerializerBase)) {
            return serializer;
        }
        return (JsonSerializer<?>)new XmlBeanSerializer((BeanSerializerBase)serializer);
    }
}
