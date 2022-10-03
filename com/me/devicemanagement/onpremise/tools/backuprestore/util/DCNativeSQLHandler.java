package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.net.URL;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.LinkedHashMap;
import org.xml.sax.helpers.DefaultHandler;

public class DCNativeSQLHandler extends DefaultHandler
{
    private LinkedHashMap queryId_SQL_Map;
    private LinkedHashMap queryId_Details;
    private String stopPro;
    String dbName;
    
    public DCNativeSQLHandler() {
        this.queryId_SQL_Map = new LinkedHashMap();
        this.queryId_Details = null;
        this.stopPro = "false";
        this.dbName = PersistenceInitializer.getConfigurationValue("DBName");
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("DCNativeSQLString")) {
            this.queryId_Details = new LinkedHashMap();
            final String queryID = attributes.getValue("sql_id");
            String attr_dbName = attributes.getValue("sqlfor");
            final String remark = attributes.getValue("sql_remarks");
            final String sqlRemark = (remark == null || remark.equalsIgnoreCase("null")) ? "Remarks not given ." : remark;
            final String stopProduct = attributes.getValue("stop_product");
            final String isDependentDeletion = attributes.getValue("sql_is_dependent_deletion");
            final String sqlJSON = attributes.getValue("sql_json");
            if (queryID != null && attr_dbName != null) {
                attr_dbName = attr_dbName.toLowerCase();
                attr_dbName = attr_dbName.replaceAll(" ", "");
                final ArrayList arrDBNames = StringUtil.getInstance().splitToArrayList(attr_dbName, ",");
                if (arrDBNames.contains(this.dbName) || arrDBNames.contains("common")) {
                    final String attr_sql_command = attributes.getValue("sql_command");
                    this.queryId_Details.put("sql_id", queryID);
                    this.queryId_Details.put("sql_command", attr_sql_command);
                    this.queryId_Details.put("sql_remarks", sqlRemark);
                    this.queryId_Details.put("sql_stopProduct", stopProduct);
                    if (isDependentDeletion != null && isDependentDeletion.equals("true")) {
                        this.queryId_Details.put("sql_is_dependent_deletion", "true");
                        this.queryId_Details.put("sql_json", sqlJSON);
                    }
                    else {
                        this.queryId_Details.put("sql_is_dependent_deletion", "false");
                    }
                    this.queryId_SQL_Map.put(queryID, this.queryId_Details);
                    this.stopPro = (this.stopPro.equalsIgnoreCase("false") ? ((stopProduct != null && stopProduct.equalsIgnoreCase("true")) ? "true" : "false") : this.stopPro);
                }
            }
        }
    }
    
    public LinkedHashMap parse(final URL url) throws Exception {
        final SAXParser parser = XMLUtils.getSAXParserInstance(true, false);
        parser.parse(new InputSource(url.toExternalForm()), this);
        this.queryId_SQL_Map.put("stopPro", this.stopPro);
        return this.queryId_SQL_Map;
    }
}
