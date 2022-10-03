package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.net.URL;
import org.xml.sax.SAXException;
import com.adventnet.persistence.PersistenceInitializer;
import org.xml.sax.Attributes;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.helpers.DefaultHandler;

public class ACSQLStringHandler extends DefaultHandler
{
    private String queryID;
    private Map queryId_SQL_Map;
    
    public ACSQLStringHandler() {
        this.queryID = null;
        this.queryId_SQL_Map = new HashMap();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("SelectQuery")) {
            this.queryID = attributes.getValue("queryid");
        }
        else if (qName.equalsIgnoreCase("ACSQLString")) {
            final String attr_dbName = attributes.getValue("sqlfor");
            final String dbName = PersistenceInitializer.getConfigurationValue("DBName");
            if (attr_dbName != null && dbName.equalsIgnoreCase(attr_dbName)) {
                final String attr_sql = attributes.getValue("sql");
                this.queryId_SQL_Map.put(this.queryID, attr_sql);
            }
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equalsIgnoreCase("SelectQuery")) {
            this.queryID = null;
        }
    }
    
    public Map parse(final URL url) throws Exception {
        final SAXParser parser = XMLUtils.getSAXParserInstance(true, false);
        parser.parse(new InputSource(url.toExternalForm()), this);
        return this.queryId_SQL_Map;
    }
}
