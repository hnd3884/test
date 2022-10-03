package com.sun.xml.internal.ws.runtime.config;

import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory
{
    private static final QName _Tubelines_QNAME;
    private static final QName _TubelineMapping_QNAME;
    private static final QName _Tubeline_QNAME;
    
    public TubeFactoryConfig createTubeFactoryConfig() {
        return new TubeFactoryConfig();
    }
    
    public TubeFactoryList createTubeFactoryList() {
        return new TubeFactoryList();
    }
    
    public TubelineDefinition createTubelineDefinition() {
        return new TubelineDefinition();
    }
    
    public Tubelines createTubelines() {
        return new Tubelines();
    }
    
    public MetroConfig createMetroConfig() {
        return new MetroConfig();
    }
    
    public TubelineMapping createTubelineMapping() {
        return new TubelineMapping();
    }
    
    @XmlElementDecl(namespace = "http://java.sun.com/xml/ns/metro/config", name = "tubelines")
    public JAXBElement<Tubelines> createTubelines(final Tubelines value) {
        return new JAXBElement<Tubelines>(ObjectFactory._Tubelines_QNAME, Tubelines.class, null, value);
    }
    
    @XmlElementDecl(namespace = "http://java.sun.com/xml/ns/metro/config", name = "tubeline-mapping")
    public JAXBElement<TubelineMapping> createTubelineMapping(final TubelineMapping value) {
        return new JAXBElement<TubelineMapping>(ObjectFactory._TubelineMapping_QNAME, TubelineMapping.class, null, value);
    }
    
    @XmlElementDecl(namespace = "http://java.sun.com/xml/ns/metro/config", name = "tubeline")
    public JAXBElement<TubelineDefinition> createTubeline(final TubelineDefinition value) {
        return new JAXBElement<TubelineDefinition>(ObjectFactory._Tubeline_QNAME, TubelineDefinition.class, null, value);
    }
    
    static {
        _Tubelines_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubelines");
        _TubelineMapping_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubeline-mapping");
        _Tubeline_QNAME = new QName("http://java.sun.com/xml/ns/metro/config", "tubeline");
    }
}
