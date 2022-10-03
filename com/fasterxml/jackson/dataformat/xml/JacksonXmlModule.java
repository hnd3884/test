package com.fasterxml.jackson.dataformat.xml;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.dataformat.xml.deser.XmlBeanDeserializerModifier;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializerModifier;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.XmlStringDeserializer;
import java.io.Serializable;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JacksonXmlModule extends SimpleModule implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected boolean _cfgDefaultUseWrapper;
    protected String _cfgNameForTextElement;
    
    public JacksonXmlModule() {
        super("JacksonXmlModule", PackageVersion.VERSION);
        this._cfgDefaultUseWrapper = true;
        this._cfgNameForTextElement = "";
        final XmlStringDeserializer deser = new XmlStringDeserializer();
        this.addDeserializer((Class)String.class, (JsonDeserializer)deser);
        this.addDeserializer((Class)CharSequence.class, (JsonDeserializer)deser);
    }
    
    public void setupModule(final Module.SetupContext context) {
        context.addBeanSerializerModifier((BeanSerializerModifier)new XmlBeanSerializerModifier());
        context.addBeanDeserializerModifier((BeanDeserializerModifier)new XmlBeanDeserializerModifier(this._cfgNameForTextElement));
        context.insertAnnotationIntrospector(this._constructIntrospector());
        if (this._cfgNameForTextElement != "") {
            final XmlMapper m = (XmlMapper)context.getOwner();
            m.setXMLTextElementName(this._cfgNameForTextElement);
        }
        super.setupModule(context);
    }
    
    public void setDefaultUseWrapper(final boolean state) {
        this._cfgDefaultUseWrapper = state;
    }
    
    public void setXMLTextElementName(final String name) {
        this._cfgNameForTextElement = name;
    }
    
    protected AnnotationIntrospector _constructIntrospector() {
        return (AnnotationIntrospector)new JacksonXmlAnnotationIntrospector(this._cfgDefaultUseWrapper);
    }
}
