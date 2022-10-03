package org.apache.commons.chain.config;

import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class ConfigRuleSet extends RuleSetBase
{
    private String catalogClass;
    private String catalogElement;
    private String chainClass;
    private String chainElement;
    private String classAttribute;
    private String commandElement;
    private String defineElement;
    private String nameAttribute;
    
    public ConfigRuleSet() {
        this.catalogClass = "org.apache.commons.chain.impl.CatalogBase";
        this.catalogElement = "catalog";
        this.chainClass = "org.apache.commons.chain.impl.ChainBase";
        this.chainElement = "chain";
        this.classAttribute = "className";
        this.commandElement = "command";
        this.defineElement = "define";
        this.nameAttribute = "name";
    }
    
    public String getCatalogClass() {
        return this.catalogClass;
    }
    
    public void setCatalogClass(final String catalogClass) {
        this.catalogClass = catalogClass;
    }
    
    public String getCatalogElement() {
        return this.catalogElement;
    }
    
    public void setCatalogElement(final String catalogElement) {
        this.catalogElement = catalogElement;
    }
    
    public String getChainClass() {
        return this.chainClass;
    }
    
    public void setChainClass(final String chainClass) {
        this.chainClass = chainClass;
    }
    
    public String getChainElement() {
        return this.chainElement;
    }
    
    public void setChainElement(final String chainElement) {
        this.chainElement = chainElement;
    }
    
    public String getClassAttribute() {
        return this.classAttribute;
    }
    
    public void setClassAttribute(final String classAttribute) {
        this.classAttribute = classAttribute;
    }
    
    public String getCommandElement() {
        return this.commandElement;
    }
    
    public void setCommandElement(final String commandElement) {
        this.commandElement = commandElement;
    }
    
    public String getDefineElement() {
        return this.defineElement;
    }
    
    public void setDefineElement(final String defineElement) {
        this.defineElement = defineElement;
    }
    
    public String getNameAttribute() {
        return this.nameAttribute;
    }
    
    public void setNameAttribute(final String nameAttribute) {
        this.nameAttribute = nameAttribute;
    }
    
    public void addRuleInstances(final Digester digester) {
        digester.addRule("*/" + this.getCatalogElement(), (Rule)new ConfigCatalogRule(this.nameAttribute, this.catalogClass));
        digester.addSetProperties("*/" + this.getCatalogElement());
        digester.addObjectCreate("*/" + this.getChainElement(), this.getChainClass(), this.getClassAttribute());
        digester.addSetProperties("*/" + this.getChainElement());
        digester.addRule("*/" + this.getChainElement(), (Rule)new ConfigRegisterRule(this.nameAttribute));
        digester.addObjectCreate("*/" + this.getCommandElement(), (String)null, this.getClassAttribute());
        digester.addSetProperties("*/" + this.getCommandElement());
        digester.addRule("*/" + this.getCommandElement(), (Rule)new ConfigRegisterRule(this.nameAttribute));
        digester.addRule("*/" + this.getDefineElement(), (Rule)new ConfigDefineRule(this.getNameAttribute(), this.getClassAttribute()));
    }
}
