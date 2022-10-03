package com.fasterxml.jackson.dataformat.xml;

import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.core.JsonFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;
import com.fasterxml.jackson.core.TSFBuilder;

public class XmlFactoryBuilder extends TSFBuilder<XmlFactory, XmlFactoryBuilder>
{
    protected int _formatParserFeatures;
    protected int _formatGeneratorFeatures;
    protected XMLInputFactory _xmlInputFactory;
    protected XMLOutputFactory _xmlOutputFactory;
    protected String _nameForTextElement;
    
    protected XmlFactoryBuilder() {
        this._formatParserFeatures = XmlFactory.DEFAULT_XML_PARSER_FEATURE_FLAGS;
        this._formatGeneratorFeatures = XmlFactory.DEFAULT_XML_GENERATOR_FEATURE_FLAGS;
    }
    
    public XmlFactoryBuilder(final XmlFactory base) {
        super((JsonFactory)base);
        this._formatParserFeatures = base._xmlParserFeatures;
        this._formatGeneratorFeatures = base._xmlGeneratorFeatures;
        this._xmlInputFactory = base._xmlInputFactory;
        this._xmlOutputFactory = base._xmlOutputFactory;
        this._nameForTextElement = base._cfgNameForTextElement;
    }
    
    public int formatParserFeaturesMask() {
        return this._formatParserFeatures;
    }
    
    public int formatGeneratorFeaturesMask() {
        return this._formatGeneratorFeatures;
    }
    
    public String nameForTextElement() {
        return this._nameForTextElement;
    }
    
    public XMLInputFactory xmlInputFactory() {
        if (this._xmlInputFactory == null) {
            return defaultInputFactory();
        }
        return this._xmlInputFactory;
    }
    
    protected static XMLInputFactory defaultInputFactory() {
        final XMLInputFactory xmlIn = XMLInputFactory.newInstance();
        xmlIn.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        xmlIn.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
        return xmlIn;
    }
    
    public XMLOutputFactory xmlOutputFactory() {
        if (this._xmlOutputFactory == null) {
            return defaultOutputFactory();
        }
        return this._xmlOutputFactory;
    }
    
    protected static XMLOutputFactory defaultOutputFactory() {
        final XMLOutputFactory xmlOut = XMLOutputFactory.newInstance();
        xmlOut.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
        return xmlOut;
    }
    
    public XmlFactoryBuilder enable(final FromXmlParser.Feature f) {
        this._formatParserFeatures |= f.getMask();
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder enable(final FromXmlParser.Feature first, final FromXmlParser.Feature... other) {
        this._formatParserFeatures |= first.getMask();
        for (final FromXmlParser.Feature f : other) {
            this._formatParserFeatures |= f.getMask();
        }
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder disable(final FromXmlParser.Feature f) {
        this._formatParserFeatures &= ~f.getMask();
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder disable(final FromXmlParser.Feature first, final FromXmlParser.Feature... other) {
        this._formatParserFeatures &= ~first.getMask();
        for (final FromXmlParser.Feature f : other) {
            this._formatParserFeatures &= ~f.getMask();
        }
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder configure(final FromXmlParser.Feature f, final boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }
    
    public XmlFactoryBuilder enable(final ToXmlGenerator.Feature f) {
        this._formatGeneratorFeatures |= f.getMask();
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder enable(final ToXmlGenerator.Feature first, final ToXmlGenerator.Feature... other) {
        this._formatGeneratorFeatures |= first.getMask();
        for (final ToXmlGenerator.Feature f : other) {
            this._formatGeneratorFeatures |= f.getMask();
        }
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder disable(final ToXmlGenerator.Feature f) {
        this._formatGeneratorFeatures &= ~f.getMask();
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder disable(final ToXmlGenerator.Feature first, final ToXmlGenerator.Feature... other) {
        this._formatGeneratorFeatures &= ~first.getMask();
        for (final ToXmlGenerator.Feature f : other) {
            this._formatGeneratorFeatures &= ~f.getMask();
        }
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder configure(final ToXmlGenerator.Feature f, final boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }
    
    public XmlFactoryBuilder nameForTextElement(final String name) {
        this._nameForTextElement = name;
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder inputFactory(final XMLInputFactory xmlIn) {
        this._xmlInputFactory = xmlIn;
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactoryBuilder outputFactory(final XMLOutputFactory xmlOut) {
        this._xmlOutputFactory = xmlOut;
        return (XmlFactoryBuilder)this._this();
    }
    
    public XmlFactory build() {
        return new XmlFactory(this);
    }
}
