package com.fasterxml.jackson.dataformat.xml.deser;

import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import java.util.Iterator;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.Collection;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.dataformat.xml.util.AnnotationUtil;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.util.List;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import java.io.Serializable;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

public class XmlBeanDeserializerModifier extends BeanDeserializerModifier implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected String _cfgNameForTextValue;
    
    public XmlBeanDeserializerModifier(final String nameForTextValue) {
        this._cfgNameForTextValue = "";
        this._cfgNameForTextValue = nameForTextValue;
    }
    
    public List<BeanPropertyDefinition> updateProperties(final DeserializationConfig config, final BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs) {
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        int changed = 0;
        for (int i = 0, propCount = propDefs.size(); i < propCount; ++i) {
            final BeanPropertyDefinition prop = propDefs.get(i);
            final AnnotatedMember acc = prop.getPrimaryMember();
            if (acc != null) {
                final Boolean b = AnnotationUtil.findIsTextAnnotation(intr, acc);
                if (b != null && b) {
                    final BeanPropertyDefinition newProp = prop.withSimpleName(this._cfgNameForTextValue);
                    if (newProp != prop) {
                        propDefs.set(i, newProp);
                    }
                }
                else {
                    final PropertyName wrapperName = prop.getWrapperName();
                    if (wrapperName != null && wrapperName != PropertyName.NO_NAME) {
                        final String localName = wrapperName.getSimpleName();
                        if (localName != null && localName.length() > 0 && !localName.equals(prop.getName())) {
                            if (changed == 0) {
                                propDefs = new ArrayList<BeanPropertyDefinition>(propDefs);
                            }
                            ++changed;
                            propDefs.set(i, prop.withSimpleName(localName));
                        }
                    }
                }
            }
        }
        return propDefs;
    }
    
    public JsonDeserializer<?> modifyDeserializer(final DeserializationConfig config, final BeanDescription beanDesc, final JsonDeserializer<?> deser0) {
        if (!(deser0 instanceof BeanDeserializerBase)) {
            return deser0;
        }
        final BeanDeserializerBase deser = (BeanDeserializerBase)deser0;
        final ValueInstantiator inst = deser.getValueInstantiator();
        if (!inst.canCreateFromString()) {
            final SettableBeanProperty textProp = this._findSoleTextProp(config, deser.properties());
            if (textProp != null) {
                return (JsonDeserializer<?>)new XmlTextDeserializer(deser, textProp);
            }
        }
        return (JsonDeserializer<?>)new WrapperHandlingDeserializer(deser);
    }
    
    private SettableBeanProperty _findSoleTextProp(final DeserializationConfig config, final Iterator<SettableBeanProperty> propIt) {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        SettableBeanProperty textProp = null;
        while (propIt.hasNext()) {
            final SettableBeanProperty prop = propIt.next();
            final AnnotatedMember m = prop.getMember();
            if (m != null) {
                final PropertyName n = prop.getFullName();
                if (this._cfgNameForTextValue.equals(n.getSimpleName())) {
                    textProp = prop;
                    continue;
                }
                final Boolean b = AnnotationUtil.findIsAttributeAnnotation(ai, m);
                if (b != null && b) {
                    continue;
                }
            }
            return null;
        }
        return textProp;
    }
}
