package com.adventnet.persistence.xml;

import com.zoho.conf.AppResources;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.util.Properties;
import org.xml.sax.InputSource;
import com.adventnet.persistence.DataAccessException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class Xml2DoConverter
{
    private static String xmlParserClass;
    private static final Logger LOGGER;
    
    public static DataObject transformIgnoringDVH(final String xmlFileName) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(false).transform(xmlFileName);
    }
    
    public static DataObject transformIgnoringDVH(final InputSource input) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(false).transform(input);
    }
    
    public static DataObject transform(final String xmlFileName) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(true).transform(xmlFileName);
    }
    
    public static DataObject transform(final String xmlFileName, final Properties securityProperties) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(true).transform(xmlFileName, securityProperties);
    }
    
    public static DataObject transform(final InputSource input) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(true).transform(input);
    }
    
    public static DataObject transform(final InputSource input, final Properties securityProperties) throws SAXException, IOException, DataAccessException {
        return new XmlParser(true).transform(input, securityProperties);
    }
    
    public static DataObject transform(final URL url) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(true).transform(url);
    }
    
    public static DataObject transform(final URL url, final Properties securityProperties) throws SAXException, IOException, DataAccessException {
        return new XmlParser(true).transform(url, securityProperties);
    }
    
    public static DataObject transform(final URL url, final boolean dumpUVH) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(true).transform(url, null, dumpUVH);
    }
    
    public static DataObject transform(final URL url, final boolean dumpUVH, final String moduleName) throws SAXException, IOException, DataAccessException {
        return new XmlParser(true).transform(url, null, dumpUVH, moduleName);
    }
    
    public static DataObject transform(final URL url, final boolean dumpUVH, final Map patternVsValue) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(true).transform(url, patternVsValue, dumpUVH);
    }
    
    public static DataObject transform(final InputSource input, final URL inputURL, final boolean dumpUVH, final Map patternVsValue) throws SAXException, IOException, DataAccessException {
        return getXmlParserObject(true).transform(input, inputURL, patternVsValue, dumpUVH);
    }
    
    public static Map getPatternVsValue(final Long confFileID) throws DataAccessException {
        final Map uvhMap = new HashMap(5);
        final DataObject uvhValuesDO = DataAccess.get("UVHValues", new Criteria(Column.getColumn("UVHValues", "FILEID"), confFileID, 0));
        final Iterator iterator = uvhValuesDO.getRows("UVHValues");
        if (!iterator.hasNext()) {
            return null;
        }
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long genValue = (Long)row.get(4);
            final String pattern = (String)row.get(3);
            uvhMap.put(pattern, genValue);
        }
        return uvhMap;
    }
    
    private static XmlParser getXmlParserObject(final boolean usedvh) {
        try {
            if (Xml2DoConverter.xmlParserClass != null) {
                final Constructor c = Class.forName(Xml2DoConverter.xmlParserClass).getConstructor(Boolean.TYPE);
                return c.newInstance(usedvh);
            }
        }
        catch (final Exception exc) {
            Xml2DoConverter.LOGGER.log(Level.INFO, "Exception while getting xmlParserObject.", exc);
        }
        return new XmlParser(usedvh);
    }
    
    static {
        Xml2DoConverter.xmlParserClass = AppResources.getString("com.adventnet.persistence.xml.XmlParser");
        LOGGER = Logger.getLogger(Xml2DoConverter.class.getName());
    }
}
