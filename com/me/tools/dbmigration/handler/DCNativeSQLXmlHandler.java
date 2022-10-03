package com.me.tools.dbmigration.handler;

import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.net.URL;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.StringUtil;
import org.xml.sax.Attributes;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import org.xml.sax.helpers.DefaultHandler;

public class DCNativeSQLXmlHandler extends DefaultHandler
{
    Logger logger;
    private LinkedHashMap queryId_SQL_Map;
    private LinkedHashMap queryId_Details;
    String dbName;
    
    public DCNativeSQLXmlHandler() {
        this.logger = Logger.getLogger(DCNativeSQLXmlHandler.class.getName());
        this.queryId_SQL_Map = new LinkedHashMap();
        this.queryId_Details = null;
        this.dbName = DBMigrationUtil.getDestDBType().toString();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("DCNativeSQLString")) {
            this.queryId_Details = new LinkedHashMap();
            final String queryID = attributes.getValue("sql_id");
            String attr_dbName = attributes.getValue("sqlfor");
            final String variableHandler = attributes.getValue("variable_handler");
            final String remark = attributes.getValue("sql_remarks");
            final String isDependentDeletion = attributes.getValue("sql_is_dependent_deletion");
            final String sqlJSON = attributes.getValue("sql_json");
            final String sqlRemark = (remark == null || remark.equalsIgnoreCase("null")) ? "Remarks not given ." : remark;
            this.dbName = this.dbName.toLowerCase();
            if (queryID != null && attr_dbName != null) {
                attr_dbName = attr_dbName.toLowerCase();
                attr_dbName = attr_dbName.replaceAll(" ", "");
                final ArrayList arrDBNames = StringUtil.getInstance().splitToArrayList((CharSequence)attr_dbName, ",");
                if (arrDBNames.contains(this.dbName) || arrDBNames.contains("common")) {
                    final String attr_sql_command = attributes.getValue("sql_command");
                    this.queryId_Details.put("sql_id", queryID);
                    this.queryId_Details.put("sql_command", attr_sql_command);
                    this.queryId_Details.put("variable_handler", variableHandler);
                    this.queryId_Details.put("sql_remarks", sqlRemark);
                    if (isDependentDeletion != null && isDependentDeletion.equals("true")) {
                        this.queryId_Details.put("sql_is_dependent_deletion", "true");
                        this.queryId_Details.put("sql_json", sqlJSON);
                    }
                    else {
                        this.queryId_Details.put("sql_is_dependent_deletion", "false");
                    }
                    this.queryId_SQL_Map.put(queryID, this.queryId_Details);
                }
            }
        }
    }
    
    public LinkedHashMap parse(final URL url) throws Exception {
        final SAXParser parser = DMSecurityUtil.getSAXParser(true, false);
        this.logger.log(Level.INFO, "DB Name : {0}", this.dbName);
        parser.parse(new InputSource(url.toExternalForm()), this);
        return this.queryId_SQL_Map;
    }
}
