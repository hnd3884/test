package com.me.mdm.core.xmlparser;

import javax.xml.bind.PropertyException;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.xml.bind.Marshaller;
import java.io.File;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.stream.XMLStreamReader;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONObject;

public class XmlBeanUtil<T>
{
    public static final String BEAN_OBJECT = "BEAN_OBJECT";
    private JSONObject jsonObject;
    private Boolean formattedOutput;
    private Boolean fragmentDocument;
    private String encoding;
    private String noNameSpaceSchemaLocation;
    private String schemaLocation;
    private Class<T> beanClass;
    private T beanObject;
    
    private XmlBeanUtil() {
    }
    
    public XmlBeanUtil(final JSONObject jsonObject) throws ClassNotFoundException, SyMException {
        this.jsonObject = jsonObject;
        this.beanObject = null;
        if (jsonObject.opt("BEAN_OBJECT") != null) {
            this.beanObject = (T)jsonObject.opt("BEAN_OBJECT");
        }
        if (this.beanObject == null) {
            throw new SyMException(550011, "BEAN_OBJECT key cannot be null", new Throwable());
        }
        this.beanClass = (Class<T>)this.beanObject.getClass();
        this.formattedOutput = jsonObject.optBoolean("jaxb.formatted.output", (boolean)Boolean.FALSE);
        this.fragmentDocument = jsonObject.optBoolean("jaxb.fragment", (boolean)Boolean.TRUE);
        this.encoding = jsonObject.optString("jaxb.encoding", (String)null);
        this.noNameSpaceSchemaLocation = jsonObject.optString("jaxb.noNamespaceSchemaLocation", (String)null);
        this.schemaLocation = jsonObject.optString("jaxb.schemaLocation", (String)null);
    }
    
    private T xmlStreamToBean(final XMLStreamReader xmlInputStream) throws JAXBException {
        final JAXBContext jaxbContext = JAXBContext.newInstance(this.beanClass);
        final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return this.beanObject = this.beanClass.cast(jaxbUnmarshaller.unmarshal(xmlInputStream));
    }
    
    public T xmlStringToBean(final String xmlString) throws JAXBException, XMLStreamException {
        final XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        xif.setProperty("javax.xml.stream.supportDTD", false);
        final XMLStreamReader xmlInputStream = xif.createXMLStreamReader(new ByteArrayInputStream(xmlString.getBytes()));
        return this.xmlStreamToBean(xmlInputStream);
    }
    
    public File beanToXmlFile(final String xmlFilePath) throws JAXBException {
        final JAXBContext jaxbContext = JAXBContext.newInstance(this.beanObject.getClass());
        final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        this.setMarshallerProperties(jaxbMarshaller);
        final File xmlFile = new File(xmlFilePath);
        jaxbMarshaller.marshal(this.beanObject, xmlFile);
        return xmlFile;
    }
    
    public void beanToXmlStream(final OutputStream xmlOutputStream) throws JAXBException {
        final JAXBContext jaxbContext = JAXBContext.newInstance(this.beanObject.getClass());
        final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        this.setMarshallerProperties(jaxbMarshaller);
        jaxbMarshaller.marshal(this.beanObject, xmlOutputStream);
    }
    
    public String beanToXmlString() throws JAXBException {
        final ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
        this.beanToXmlStream(xmlStream);
        final String xmlContent = xmlStream.toString();
        return xmlContent;
    }
    
    private void setMarshallerProperties(final Marshaller jaxbMarshaller) throws PropertyException {
        jaxbMarshaller.setProperty("jaxb.fragment", this.fragmentDocument);
        jaxbMarshaller.setProperty("jaxb.formatted.output", this.formattedOutput);
        if (this.noNameSpaceSchemaLocation != null) {
            jaxbMarshaller.setProperty("jaxb.noNamespaceSchemaLocation", this.noNameSpaceSchemaLocation);
        }
        if (this.schemaLocation != null) {
            jaxbMarshaller.setProperty("jaxb.schemaLocation", this.schemaLocation);
        }
        if (this.encoding != null) {
            jaxbMarshaller.setProperty("jaxb.encoding", this.encoding);
        }
        final JSONObject customMarshallerProps = this.jsonObject.optJSONObject("customMarshallerProps");
        if (customMarshallerProps != null) {
            final Iterator customMarshallerPropsIter = customMarshallerProps.keys();
            while (customMarshallerPropsIter.hasNext()) {
                final String key = customMarshallerPropsIter.next();
                final Object value = customMarshallerProps.opt(key);
                if (key != null && !key.trim().equalsIgnoreCase("")) {
                    jaxbMarshaller.setProperty(key, value);
                }
            }
        }
    }
}
