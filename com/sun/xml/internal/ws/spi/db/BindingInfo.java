package com.sun.xml.internal.ws.spi.db;

import java.util.HashMap;
import java.util.ArrayList;
import java.net.URL;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.util.Map;
import java.util.Collection;

public class BindingInfo
{
    private String databindingMode;
    private String defaultNamespace;
    private Collection<Class> contentClasses;
    private Collection<TypeInfo> typeInfos;
    private Map<Class, Class> subclassReplacements;
    private Map<String, Object> properties;
    protected ClassLoader classLoader;
    private SEIModel seiModel;
    private URL wsdlURL;
    
    public BindingInfo() {
        this.contentClasses = new ArrayList<Class>();
        this.typeInfos = new ArrayList<TypeInfo>();
        this.subclassReplacements = new HashMap<Class, Class>();
        this.properties = new HashMap<String, Object>();
    }
    
    public String getDatabindingMode() {
        return this.databindingMode;
    }
    
    public void setDatabindingMode(final String databindingMode) {
        this.databindingMode = databindingMode;
    }
    
    public String getDefaultNamespace() {
        return this.defaultNamespace;
    }
    
    public void setDefaultNamespace(final String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }
    
    public Collection<Class> contentClasses() {
        return this.contentClasses;
    }
    
    public Collection<TypeInfo> typeInfos() {
        return this.typeInfos;
    }
    
    public Map<Class, Class> subclassReplacements() {
        return this.subclassReplacements;
    }
    
    public Map<String, Object> properties() {
        return this.properties;
    }
    
    public SEIModel getSEIModel() {
        return this.seiModel;
    }
    
    public void setSEIModel(final SEIModel seiModel) {
        this.seiModel = seiModel;
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public URL getWsdlURL() {
        return this.wsdlURL;
    }
    
    public void setWsdlURL(final URL wsdlURL) {
        this.wsdlURL = wsdlURL;
    }
}
