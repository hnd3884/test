package com.adventnet.ds.util;

import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import com.adventnet.persistence.Row;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;
import org.w3c.dom.Document;

public class TableDSMappingScanner
{
    Document document;
    private static TableDSMappingScanner tabMap;
    private static DataObject tabDSDo;
    private static final Logger LOGGER;
    
    private TableDSMappingScanner() {
    }
    
    public static TableDSMappingScanner getInstance() {
        if (TableDSMappingScanner.tabMap == null) {
            TableDSMappingScanner.tabMap = new TableDSMappingScanner();
            try {
                TableDSMappingScanner.tabDSDo = DataAccess.constructDataObject();
            }
            catch (final DataAccessException e) {
                TableDSMappingScanner.LOGGER.log(Level.SEVERE, "Exception : ", e);
            }
        }
        return TableDSMappingScanner.tabMap;
    }
    
    public static DataObject getTableDSMapping() {
        return TableDSMappingScanner.tabDSDo;
    }
    
    public void visitDocument(final Document document) {
        final Element element = document.getDocumentElement();
        if (element != null && element.getTagName().equals("table-ds-mapping")) {
            this.visitElement_table_ds_mapping(element);
        }
    }
    
    void visitElement_table_ds_mapping(final Element element) {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("table")) {
                        this.visitElement_table(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void visitElement_table(final Element element) {
        final NamedNodeMap attrs = element.getAttributes();
        String tabName = null;
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("name")) {
                tabName = attr.getValue();
            }
        }
        final NodeList nodes = element.getChildNodes();
        List dataSources = null;
        for (int j = 0; j < nodes.getLength(); ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("datasources")) {
                        dataSources = this.visitElement_datasources(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
        for (int j = 0; j < dataSources.size(); ++j) {
            final String dsName = dataSources.get(j);
            final Row tabDSRow = new Row("TableDSMap");
            tabDSRow.set("TABLENAME", tabName);
            tabDSRow.set("DSNAME", dsName);
            try {
                TableDSMappingScanner.tabDSDo.addRow(tabDSRow);
            }
            catch (final DataAccessException e) {
                TableDSMappingScanner.LOGGER.log(Level.SEVERE, "Exception : ", e);
            }
        }
    }
    
    List visitElement_datasources(final Element element) {
        final List dataSources = new ArrayList();
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("datasource")) {
                        final String dataSourceName = this.visitElement_datasource(nodeElement);
                        dataSources.add(dataSourceName);
                        break;
                    }
                    break;
                }
            }
        }
        return dataSources;
    }
    
    String visitElement_datasource(final Element element) {
        String dataSourceName = null;
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    dataSourceName = ((Text)node).getData();
                    break;
                }
            }
        }
        return dataSourceName;
    }
    
    static {
        TableDSMappingScanner.tabMap = null;
        TableDSMappingScanner.tabDSDo = null;
        LOGGER = Logger.getLogger(TableDSMappingScanner.class.getName());
    }
}
