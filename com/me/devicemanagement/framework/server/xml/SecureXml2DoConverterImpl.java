package com.me.devicemanagement.framework.server.xml;

import java.net.URLDecoder;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;
import com.me.devicemanagement.framework.utils.XMLUtils;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class SecureXml2DoConverterImpl implements SecureXml2DoConverterAPI
{
    private static Logger logger;
    
    @Override
    public DataObject convertXMLToDO(final byte[] data) {
        DataObject rDo = null;
        if (data != null && data.length > 0) {
            try {
                XMLUtils.validateXML(data);
                rDo = Xml2DoConverter.transform(new InputSource(new ByteArrayInputStream(data)));
                SecureXml2DoConverterImpl.logger.log(Level.FINEST, "Successfully converted the byte[] to DataObject");
            }
            catch (final Exception excep) {
                SecureXml2DoConverterImpl.logger.log(Level.SEVERE, "Exception occured while converting Byte[] to DataObject. ", excep);
            }
        }
        else {
            SecureXml2DoConverterImpl.logger.log(Level.INFO, "The received byte[] is null. Hence the dataObject returned is null");
        }
        return rDo;
    }
    
    @Override
    public DataObject convertXMLToDO(final URL url) {
        DataObject rDo = null;
        if (url != null) {
            try {
                XMLUtils.validateXML(url);
                rDo = Xml2DoConverter.transform(url);
                SecureXml2DoConverterImpl.logger.log(Level.FINEST, "Successfully converted the Url to DataObject");
            }
            catch (final Exception excep) {
                SecureXml2DoConverterImpl.logger.log(Level.SEVERE, "Exception occured while converting xml url to DataObject. ", excep);
            }
        }
        else {
            SecureXml2DoConverterImpl.logger.log(Level.INFO, "The received url is null. Hence the dataObject returned is null");
        }
        return rDo;
    }
    
    @Override
    public DataObject convertXMLToDO(final String xmlFileName) {
        DataObject rDo = null;
        if (xmlFileName != null) {
            try {
                final URL url = new URL(URLDecoder.decode(new File(xmlFileName).toURI().toURL().toExternalForm(), "UTF-8"));
                XMLUtils.validateXML(url);
                rDo = Xml2DoConverter.transform(xmlFileName);
                SecureXml2DoConverterImpl.logger.log(Level.FINEST, "Successfully converted the xmlFile to DataObject");
            }
            catch (final Exception excep) {
                SecureXml2DoConverterImpl.logger.log(Level.SEVERE, "Exception occured while converting xmlFile to DataObject. ", excep);
            }
        }
        else {
            SecureXml2DoConverterImpl.logger.log(Level.INFO, "The received xml file is null. Hence the dataObject returned is null");
        }
        return rDo;
    }
    
    static {
        SecureXml2DoConverterImpl.logger = Logger.getLogger(SecureXml2DoConverterImpl.class.getName());
    }
}
