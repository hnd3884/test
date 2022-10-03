package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.util.HashMap;
import java.util.HashSet;
import org.xml.sax.EntityResolver;
import javax.xml.transform.Source;
import java.util.Map;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.ws.WebServiceFeature;
import java.net.URL;
import java.util.Set;

public class DatabindingConfig
{
    protected Class contractClass;
    protected Class endpointClass;
    protected Set<Class> additionalValueTypes;
    protected MappingInfo mappingInfo;
    protected URL wsdlURL;
    protected ClassLoader classLoader;
    protected Iterable<WebServiceFeature> features;
    protected WSBinding wsBinding;
    protected WSDLPort wsdlPort;
    protected MetadataReader metadataReader;
    protected Map<String, Object> properties;
    protected Source wsdlSource;
    protected EntityResolver entityResolver;
    
    public DatabindingConfig() {
        this.additionalValueTypes = new HashSet<Class>();
        this.mappingInfo = new MappingInfo();
        this.properties = new HashMap<String, Object>();
    }
    
    public Class getContractClass() {
        return this.contractClass;
    }
    
    public void setContractClass(final Class contractClass) {
        this.contractClass = contractClass;
    }
    
    public Class getEndpointClass() {
        return this.endpointClass;
    }
    
    public void setEndpointClass(final Class implBeanClass) {
        this.endpointClass = implBeanClass;
    }
    
    public MappingInfo getMappingInfo() {
        return this.mappingInfo;
    }
    
    public void setMappingInfo(final MappingInfo mappingInfo) {
        this.mappingInfo = mappingInfo;
    }
    
    public URL getWsdlURL() {
        return this.wsdlURL;
    }
    
    public void setWsdlURL(final URL wsdlURL) {
        this.wsdlURL = wsdlURL;
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public Iterable<WebServiceFeature> getFeatures() {
        if (this.features == null && this.wsBinding != null) {
            return this.wsBinding.getFeatures();
        }
        return this.features;
    }
    
    public void setFeatures(final WebServiceFeature[] features) {
        this.setFeatures(new WebServiceFeatureList(features));
    }
    
    public void setFeatures(final Iterable<WebServiceFeature> features) {
        this.features = WebServiceFeatureList.toList(features);
    }
    
    public WSDLPort getWsdlPort() {
        return this.wsdlPort;
    }
    
    public void setWsdlPort(final WSDLPort wsdlPort) {
        this.wsdlPort = wsdlPort;
    }
    
    public Set<Class> additionalValueTypes() {
        return this.additionalValueTypes;
    }
    
    public Map<String, Object> properties() {
        return this.properties;
    }
    
    public WSBinding getWSBinding() {
        return this.wsBinding;
    }
    
    public void setWSBinding(final WSBinding wsBinding) {
        this.wsBinding = wsBinding;
    }
    
    public MetadataReader getMetadataReader() {
        return this.metadataReader;
    }
    
    public void setMetadataReader(final MetadataReader reader) {
        this.metadataReader = reader;
    }
    
    public Source getWsdlSource() {
        return this.wsdlSource;
    }
    
    public void setWsdlSource(final Source wsdlSource) {
        this.wsdlSource = wsdlSource;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
}
