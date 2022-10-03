package org.apache.commons.chain.config;

import org.xml.sax.Attributes;
import org.apache.commons.digester.Rule;

class ConfigDefineRule extends Rule
{
    private String classAttribute;
    private String nameAttribute;
    
    public ConfigDefineRule(final String nameAttribute, final String classAttribute) {
        this.classAttribute = null;
        this.nameAttribute = null;
        this.nameAttribute = nameAttribute;
        this.classAttribute = classAttribute;
    }
    
    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
        final String nameValue = attributes.getValue(this.nameAttribute);
        final String classValue = attributes.getValue(this.classAttribute);
        this.digester.addObjectCreate("*/" + nameValue, classValue);
        this.digester.addSetProperties("*/" + nameValue);
        this.digester.addRule("*/" + nameValue, (Rule)new ConfigRegisterRule(this.nameAttribute));
    }
}
