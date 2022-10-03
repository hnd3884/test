package com.me.tools.zcutil;

import java.util.Map;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.logging.Level;
import org.w3c.dom.Element;
import java.util.logging.Logger;
import java.util.Hashtable;
import java.util.Properties;
import java.util.HashMap;

public class LoadQuery
{
    private HashMap<String, Properties> queryTable;
    private Hashtable<String, String> formTable;
    private static Logger logger;
    
    public LoadQuery(final Element el1) {
        this.queryTable = null;
        this.formTable = null;
        this.queryTable = new HashMap<String, Properties>();
        this.formTable = new Hashtable<String, String>();
        this.parseDocument(el1);
    }
    
    public void parseDocument(final Element el1) {
        try {
            if (el1.getElementsByTagName("record").getLength() > 0) {
                final NodeList nList0 = el1.getElementsByTagName("record");
                for (int i1 = 0; i1 < nList0.getLength(); ++i1) {
                    final Properties dataProps = new Properties();
                    final Node cnoTemp = nList0.item(i1);
                    final NodeList nList2 = cnoTemp.getChildNodes();
                    for (int j = 0; j < nList2.getLength(); ++j) {
                        final Node cno = nList2.item(j);
                        final NamedNodeMap nnm0 = cno.getAttributes();
                        final String propKey = nnm0.getNamedItem("name").getNodeValue();
                        String propValue = "";
                        final NodeList nl1 = cno.getChildNodes();
                        for (int k = 0; k < nl1.getLength(); ++k) {
                            final Node cno2 = nl1.item(k);
                            if (cno2.getNodeType() == 1) {
                                final NodeList nl2 = cno2.getChildNodes();
                                for (int j2 = 0; j2 < nl2.getLength(); ++j2) {
                                    final Node cno3 = nl2.item(j2);
                                    propValue = cno3.getNodeValue();
                                }
                            }
                        }
                        dataProps.setProperty(propKey, propValue);
                    }
                    this.setTableValues(dataProps);
                }
            }
        }
        catch (final Exception e2) {
            LoadQuery.logger.log(Level.INFO, "Exception while parsing doc element : " + e2.toString());
        }
    }
    
    public void setTableValues(final Properties dataProp) {
        this.formTable.put(dataProp.getProperty("columnname"), dataProp.getProperty("Form_Name"));
        if (dataProp.getProperty("Query_For_DB") != null) {
            this.setLoadQueries(dataProp.getProperty("Query_For_DB"), dataProp.getProperty("query"), dataProp.getProperty("columnname"));
        }
        else {
            this.setLoadQueries("All", dataProp.getProperty("query"), dataProp.getProperty("columnname"));
        }
    }
    
    private void setLoadQueries(final String queryFor, final String query, final String columnName) {
        if (this.queryTable.get(queryFor) != null) {
            this.queryTable.get(queryFor).setProperty(columnName, query);
        }
        else {
            final Properties queryHash = new Properties();
            queryHash.setProperty(columnName, query);
            this.queryTable.put(queryFor, queryHash);
        }
    }
    
    public Properties getLoadQuery(final String criteria) {
        if (this.queryTable.get(criteria) != null) {
            return this.queryTable.get(criteria);
        }
        return null;
    }
    
    public String getFormName(final String column) {
        return this.formTable.get(column);
    }
    
    public HashMap<String, Properties> getLoadQuery() {
        return this.queryTable;
    }
    
    public Hashtable<String, String> getFormNameDetails() {
        return this.formTable;
    }
    
    public Properties getAllAndDBQueryies(final String dbVendor) {
        final Properties retProp = new Properties();
        if (this.queryTable.get("All") != null) {
            retProp.putAll(this.queryTable.get("All"));
        }
        if (this.queryTable.get(dbVendor) != null) {
            retProp.putAll(this.queryTable.get(dbVendor));
        }
        return retProp;
    }
    
    static {
        LoadQuery.logger = Logger.getLogger(ZCUtil.class.getName());
    }
}
