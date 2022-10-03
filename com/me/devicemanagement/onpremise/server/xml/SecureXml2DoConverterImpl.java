package com.me.devicemanagement.onpremise.server.xml;

import java.util.Hashtable;
import java.util.Properties;
import java.net.URL;
import java.util.logging.Level;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.xml.SecureXml2DoConverterAPI;

public class SecureXml2DoConverterImpl implements SecureXml2DoConverterAPI
{
    private static Logger logger;
    
    public DataObject convertXMLToDO(final byte[] data) {
        DataObject dataObject = null;
        try {
            if (data != null && data.length > 0) {
                final InputSource is = new InputSource(new ByteArrayInputStream(data));
                dataObject = Xml2DoConverter.transform(is, getXmlProperties());
                SecureXml2DoConverterImpl.logger.log(Level.INFO, "Successfully converted the InputSource to DataObject");
            }
            else {
                SecureXml2DoConverterImpl.logger.log(Level.INFO, "The received data is null. Hence the converted inputSource is null");
            }
        }
        catch (final Exception ex) {
            SecureXml2DoConverterImpl.logger.log(Level.SEVERE, "Exception occured while converting Byte[] to DataObject. ", ex);
        }
        return dataObject;
    }
    
    public DataObject convertXMLToDO(final URL url) {
        DataObject dataObject = null;
        try {
            dataObject = Xml2DoConverter.transform(url, getXmlProperties());
        }
        catch (final Exception ex) {
            SecureXml2DoConverterImpl.logger.log(Level.SEVERE, "Exception occured while converting xml Url to DataObject. ", ex);
        }
        return dataObject;
    }
    
    public DataObject convertXMLToDO(final String xmlFileName) {
        DataObject dataObject = null;
        try {
            dataObject = Xml2DoConverter.transform(xmlFileName, getXmlProperties());
        }
        catch (final Exception ex) {
            SecureXml2DoConverterImpl.logger.log(Level.SEVERE, "Exception occured while converting xml in the given path to DataObject. ", ex);
        }
        return dataObject;
    }
    
    private static Properties getXmlProperties() {
        final Properties xmlProps = new Properties();
        ((Hashtable<String, Boolean>)xmlProps).put("http://apache.org/xml/features/disallow-doctype-decl", true);
        ((Hashtable<String, Boolean>)xmlProps).put("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        ((Hashtable<String, Boolean>)xmlProps).put("http://xml.org/sax/features/external-general-entities", false);
        ((Hashtable<String, Boolean>)xmlProps).put("http://xml.org/sax/features/external-parameter-entities", false);
        return xmlProps;
    }
    
    static {
        SecureXml2DoConverterImpl.logger = Logger.getLogger(com.me.devicemanagement.framework.server.xml.SecureXml2DoConverterImpl.class.getName());
    }
}
