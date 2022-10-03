package com.adventnet.db.persistence.metadata.generator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import com.zoho.mickey.api.TransformerFactoryUtil;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import com.adventnet.db.persistence.metadata.MetaDataException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.AllowedValues;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.TableDefinition;
import org.w3c.dom.NodeList;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class DDXMLGenerator
{
    private DDXMLGenerator() {
    }
    
    private static Element insertDDTag(final Document document, final String ddname, final String desc) {
        final Element ddtag = document.createElement("data-dictionary");
        ddtag.setAttribute("name", ddname);
        document.appendChild(ddtag);
        if (desc != null) {
            final Element property = document.createElement("description");
            ddtag.appendChild(property);
            final Node propvalue = document.createTextNode(desc);
            property.appendChild(propvalue);
        }
        return ddtag;
    }
    
    private static Element insertTableTag(final Document document, final Element parent, final String tablename, final String desc) {
        final Element tabletag = document.createElement("table");
        tabletag.setAttribute("name", tablename);
        parent.appendChild(tabletag);
        if (desc != null) {
            final Element property = document.createElement("description");
            tabletag.appendChild(property);
            final Node propvalue = document.createTextNode(desc);
            property.appendChild(propvalue);
        }
        return tabletag;
    }
    
    private static Element insertColumnsTag(final Document document, final Element parent) {
        final Element columnstag = document.createElement("columns");
        parent.appendChild(columnstag);
        return columnstag;
    }
    
    private static Element insertColumnTag(final Document document, final Element parent, final String colname, final String desc) {
        final Element columnChild = document.createElement("column");
        columnChild.setAttribute("name", colname);
        parent.appendChild(columnChild);
        if (desc != null) {
            final Element property = document.createElement("description");
            columnChild.appendChild(property);
            final Node propvalue = document.createTextNode(desc);
            property.appendChild(propvalue);
        }
        return columnChild;
    }
    
    private static void insertColumnPropertyTags(final Document document, final Node parent, final Hashtable colprops) {
        int flag = 0;
        Element property = document.createElement("data-type");
        parent.appendChild(property);
        Node propvalue = document.createTextNode(colprops.get("data-type").toString());
        property.appendChild(propvalue);
        final int maxSize = new Integer(colprops.get("max-size").toString());
        if (maxSize > 0 || maxSize == -1) {
            property = document.createElement("max-size");
            parent.appendChild(property);
            propvalue = document.createTextNode(colprops.get("max-size").toString());
            property.appendChild(propvalue);
        }
        if (new Integer(colprops.get("precision").toString()) > 0) {
            property = document.createElement("precision");
            parent.appendChild(property);
            propvalue = document.createTextNode(colprops.get("precision").toString());
            property.appendChild(propvalue);
        }
        if (colprops.get("default-value") != null) {
            property = document.createElement("default-value");
            parent.appendChild(property);
            propvalue = document.createTextNode(colprops.get("default-value").toString());
            property.appendChild(propvalue);
        }
        if (colprops.get("nullable").toString().equals("false")) {
            property = document.createElement("nullable");
            parent.appendChild(property);
            final String value = "false";
            propvalue = document.createTextNode(String.valueOf(value));
            property.appendChild(propvalue);
        }
        Element allvalue = null;
        if (colprops.get("from") != null && colprops.get("to") != null) {
            flag = 1;
            allvalue = document.createElement("allowed-values");
            parent.appendChild(allvalue);
            property = document.createElement("from");
            allvalue.appendChild(property);
            propvalue = document.createTextNode(colprops.get("from").toString());
            property.appendChild(propvalue);
            property = document.createElement("to");
            allvalue.appendChild(property);
            propvalue = document.createTextNode(colprops.get("to").toString());
            property.appendChild(propvalue);
        }
        final List allValuesList = colprops.get("allowedvalues");
        if (allValuesList != null) {
            allvalue = document.createElement("allowed-values");
            parent.appendChild(allvalue);
            final Iterator allValIterator = allValuesList.iterator();
            while (allValIterator.hasNext()) {
                property = document.createElement("value");
                allvalue.appendChild(property);
                propvalue = document.createTextNode(allValIterator.next().toString());
                property.appendChild(propvalue);
            }
        }
        if (colprops.get("pattern") != null) {
            property = document.createElement("allowed-values");
            parent.appendChild(property);
            allvalue = document.createElement("pattern");
            property.appendChild(allvalue);
            propvalue = document.createTextNode(colprops.get("pattern").toString());
            allvalue.appendChild(propvalue);
        }
        if (colprops.get("generator") != null) {
            final Element uniquegen = document.createElement("uniquevalue-generation");
            parent.appendChild(uniquegen);
            property = document.createElement("generator-name");
            uniquegen.appendChild(property);
            propvalue = document.createTextNode(colprops.get("generator").toString());
            property.appendChild(propvalue);
        }
    }
    
    private static void insertPKDefinitionTag(final Document document, final Element parent, final List pkdata) {
        final Element pktag = document.createElement("primary-key");
        pktag.setAttribute("name", pkdata.get(0).toString());
        parent.appendChild(pktag);
        for (int i = 1; i < pkdata.size(); ++i) {
            final Element pkcolname = document.createElement("primary-key-column");
            pktag.appendChild(pkcolname);
            final Node propvalue = document.createTextNode(pkdata.get(i).toString());
            pkcolname.appendChild(propvalue);
        }
    }
    
    private static Element insertFKKeysTag(final Document document, final Element parent) {
        final Element fkkeytag = document.createElement("foreign-keys");
        parent.appendChild(fkkeytag);
        return fkkeytag;
    }
    
    private static Element insertFKDefinitionTag(final Document document, final Element parent, final List fkdata) {
        final Element fktag = document.createElement("foreign-key");
        fktag.setAttribute("name", fkdata.get(0).toString());
        fktag.setAttribute("reference-table-name", fkdata.get(1).toString());
        if (fkdata.get(2).toString().equals("true")) {
            fktag.setAttribute("isbidirectional", fkdata.get(2).toString());
        }
        parent.appendChild(fktag);
        return fktag;
    }
    
    private static void insertFKColumnDefinitionTag(final Document document, final Element parent, final List fkcoldata) {
        final Element fkcolstag = document.createElement("fk-columns");
        parent.appendChild(fkcolstag);
        for (int i = 0; i < fkcoldata.size(); i += 2) {
            final Element fkcol = document.createElement("fk-column");
            fkcolstag.appendChild(fkcol);
            Element localcol = document.createElement("fk-local-column");
            fkcol.appendChild(localcol);
            Node propvalue = document.createTextNode(fkcoldata.get(i).toString());
            localcol.appendChild(propvalue);
            localcol = document.createElement("fk-reference-column");
            fkcol.appendChild(localcol);
            propvalue = document.createTextNode(fkcoldata.get(i + 1).toString());
            localcol.appendChild(propvalue);
        }
    }
    
    private static void insertFKConstraintTag(final Document document, final Element parent, final String const_name) {
        final Element fkconst_tag = document.createElement("fk-constraints");
        parent.appendChild(fkconst_tag);
        final Node propvalue = document.createTextNode(const_name);
        fkconst_tag.appendChild(propvalue);
    }
    
    private static Element insertUniqueKeysTag(final Document document, final Element parent) {
        final Element unqkeytag = document.createElement("unique-keys");
        parent.appendChild(unqkeytag);
        return unqkeytag;
    }
    
    private static Element insertUniqueKeyDefinitionTag(final Document document, final Element parent, final String name) {
        final Element unqtag = document.createElement("unique-key");
        unqtag.setAttribute("name", name);
        parent.appendChild(unqtag);
        return unqtag;
    }
    
    private static void insertUniqueColTags(final Document document, final Element parent, final List unqcolnames) {
        for (int i = 0; i < unqcolnames.size(); ++i) {
            final Element unqcol = document.createElement("unique-key-column");
            parent.appendChild(unqcol);
            final Node propvalue = document.createTextNode(unqcolnames.get(i).toString());
            unqcol.appendChild(propvalue);
        }
    }
    
    public static void deleteBlankNodes(final Element ele) {
        if (ele.hasChildNodes()) {
            final NodeList nodeList = ele.getChildNodes();
            for (int i = nodeList.getLength() - 1; i >= 0; --i) {
                final Node node = nodeList.item(i);
                if (node.getNodeType() == 3) {
                    if (node.getNodeValue() == null) {
                        ele.removeChild(node);
                    }
                    else if (node.getNodeValue().trim().equals("")) {
                        ele.removeChild(node);
                    }
                }
                else if (node.hasChildNodes()) {
                    final Element elt = (Element)node;
                    deleteBlankNodes(elt);
                }
            }
        }
    }
    
    public static void insertTableTag(final Document document, final Element root, final TableDefinition td) {
        String desc = null;
        final String tabname = td.getTableName();
        if (td.getDescription() != null) {
            desc = td.getDescription();
        }
        Element parenttag;
        final Element tabletag = parenttag = insertTableTag(document, root, tabname, desc);
        Element tag = insertColumnsTag(document, parenttag);
        List list = td.getColumnNames();
        for (int j = 0; j < list.size(); ++j) {
            final Hashtable colprops = new Hashtable();
            final ColumnDefinition cd = td.getColumnDefinitionByName(list.get(j).toString());
            desc = null;
            if (cd.getDescription() != null) {
                desc = cd.getDescription();
            }
            parenttag = insertColumnTag(document, tag, list.get(j).toString(), desc);
            colprops.put("data-type", cd.getDataType());
            colprops.put("max-size", String.valueOf(cd.getMaxLength()));
            colprops.put("precision", String.valueOf(cd.getPrecision()));
            colprops.put("nullable", String.valueOf(cd.isNullable()));
            colprops.put("unique", String.valueOf(cd.isUnique()));
            if (cd.getUniqueValueGeneration() != null) {
                final UniqueValueGeneration uv = cd.getUniqueValueGeneration();
                colprops.put("generator", uv.getGeneratorName());
            }
            if (cd.getDefaultValue() != null) {
                colprops.put("default-value", cd.getDefaultValue());
            }
            if (cd.getAllowedValues() != null) {
                final AllowedValues alv = cd.getAllowedValues();
                if (alv.getFromVal() != null) {
                    colprops.put("from", alv.getFromVal());
                }
                if (alv.getToVal() != null) {
                    colprops.put("to", alv.getToVal());
                }
                if (alv.getValueList() != null) {
                    final List values = alv.getValueList();
                    colprops.put("allowedvalues", values);
                }
                if (alv.getPattern() != null) {
                    colprops.put("pattern", alv.getPattern());
                }
            }
            insertColumnPropertyTags(document, parenttag, colprops);
        }
        final PrimaryKeyDefinition pk = td.getPrimaryKey();
        if (pk != null) {
            final List pkList = new ArrayList();
            pkList.add(pk.getName());
            final List pkcols = pk.getColumnList();
            for (int m = 0; m < pkcols.size(); ++m) {
                pkList.add(pkcols.get(m));
            }
            insertPKDefinitionTag(document, tabletag, pkList);
        }
        list = td.getForeignKeyList();
        if (list != null) {
            if (list.size() > 0) {
                parenttag = insertFKKeysTag(document, tabletag);
            }
            for (int i = 0; i < list.size(); ++i) {
                final ForeignKeyDefinition fkd = list.get(i);
                List fkdeflist = new ArrayList();
                fkdeflist.add(fkd.getName());
                fkdeflist.add(fkd.getMasterTableName());
                fkdeflist.add(String.valueOf(fkd.isBidirectional()));
                tag = insertFKDefinitionTag(document, parenttag, fkdeflist);
                final List fkcolumns = fkd.getForeignKeyColumns();
                fkdeflist = new ArrayList();
                for (int k = 0; k < fkcolumns.size(); ++k) {
                    final ForeignKeyColumnDefinition ff = fkcolumns.get(k);
                    ColumnDefinition col = ff.getLocalColumnDefinition();
                    final String localcolumn = col.getColumnName();
                    col = ff.getReferencedColumnDefinition();
                    final String refcolumn = col.getColumnName();
                    fkdeflist.add(localcolumn);
                    fkdeflist.add(refcolumn);
                }
                insertFKColumnDefinitionTag(document, tag, fkdeflist);
                final int constraint = fkd.getConstraints();
                String constname = null;
                switch (constraint) {
                    case 0: {
                        constname = "ON-DELETE-RESTRICT";
                        break;
                    }
                    case 1: {
                        constname = "ON-DELETE-CASCADE";
                        break;
                    }
                    case 2: {
                        constname = "ON-DELETE-SET-NULL";
                        break;
                    }
                    case 3: {
                        constname = "ON-DELETE-SET-DEFAULT";
                        break;
                    }
                }
                insertFKConstraintTag(document, tag, constname);
            }
        }
        list = td.getUniqueKeys();
        if (list != null && !list.isEmpty()) {
            parenttag = insertUniqueKeysTag(document, tabletag);
            for (int l = 0; l < list.size(); ++l) {
                final UniqueKeyDefinition unq = list.get(l);
                tag = insertUniqueKeyDefinitionTag(document, parenttag, unq.getName());
                final List collist = unq.getColumns();
                insertUniqueColTags(document, tag, collist);
            }
        }
        list = td.getIndexes();
        if (list != null && !list.isEmpty()) {
            parenttag = insertIndexKeysTag(document, tabletag);
            for (int l = 0; l < list.size(); ++l) {
                final IndexDefinition idxDef = list.get(l);
                tag = insertIndexKeyDefinitionTag(document, parenttag, idxDef.getName());
                final List collist = idxDef.getColumns();
                insertIndexColTags(document, tag, collist);
            }
        }
    }
    
    public static Document getDocumentForDD(final DataDictionary dd) throws DDXMLGeneratorException, MetaDataException {
        if (null == dd) {
            throw new IllegalArgumentException("Data Dictionary provided is null!!!, Hence Document object cannot be created.");
        }
        Document document = null;
        DocumentBuilder builder = null;
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.newDocument();
            final Element root = insertDDTag(document, dd.getName(), dd.getDescription());
            final List tablenames = dd.getTableDefinitions();
            for (int i = 0; i < tablenames.size(); ++i) {
                insertTableTag(document, root, tablenames.get(i));
            }
            document.getDocumentElement().normalize();
        }
        catch (final ParserConfigurationException pce) {
            throw new DDXMLGeneratorException(getExceptionMessage(pce), pce);
        }
        catch (final Exception e) {
            throw new MetaDataException("Exception occured while converting DD to Xml", e);
        }
        return document;
    }
    
    private static Element insertIndexKeysTag(final Document document, final Element parent) {
        final Element indextag = document.createElement("indexes");
        parent.appendChild(indextag);
        return indextag;
    }
    
    private static Element insertIndexKeyDefinitionTag(final Document document, final Element parent, final String idxName) {
        final Element idxtag = document.createElement("index");
        idxtag.setAttribute("name", idxName);
        parent.appendChild(idxtag);
        return idxtag;
    }
    
    private static void insertIndexColTags(final Document document, final Element parent, final List idxcolnames) {
        for (int i = 0; i < idxcolnames.size(); ++i) {
            final Element idxcol = document.createElement("index-column");
            parent.appendChild(idxcol);
            final Node propvalue = document.createTextNode(idxcolnames.get(i).toString());
            idxcol.appendChild(propvalue);
        }
    }
    
    public static void writeToStream(final OutputStream outputStream, final Document doc) throws TransformerConfigurationException, TransformerException {
        if (doc == null) {
            return;
        }
        final TransformerFactory tfac = TransformerFactoryUtil.newInstance();
        tfac.setAttribute("indent-number", new Integer(4));
        final Transformer tf = tfac.newTransformer();
        tf.setOutputProperty("indent", "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        tf.setOutputProperty("doctype-system", "data-dictionary.dtd");
        deleteBlankNodes(doc.getDocumentElement());
        final DOMSource doms = new DOMSource(doc);
        final StreamResult stream = new StreamResult(outputStream);
        tf.transform(doms, stream);
    }
    
    public static void writeToStream(final String fileNameWithFullPath, final Document doc) throws TransformerConfigurationException, TransformerException, IOException {
        if (doc == null) {
            return;
        }
        deleteBlankNodes(doc.getDocumentElement());
        final NodeList nodelist = doc.getElementsByTagName("data-dictionary");
        StringBuffer strBuf = new StringBuffer();
        final Element rootEl = doc.getDocumentElement();
        final NodeList nl = rootEl.getChildNodes();
        final int n = nl.getLength();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node childNode = nl.item(i);
            if (childNode.getNodeType() == 1) {
                final TransformerFactory tfac = TransformerFactoryUtil.newInstance();
                tfac.setAttribute("indent-number", new Integer(4));
                final Transformer tf = tfac.newTransformer();
                final Properties prop = new Properties();
                prop.setProperty("indent", "yes");
                prop.setProperty("method", "xml");
                ((Hashtable<String, String>)prop).put("{http://xml.apache.org/xslt}indent-amount", "4");
                tf.setOutputProperties(prop);
                if (i == 0) {
                    tf.setOutputProperty("doctype-system", "data-dictionary.dtd");
                }
                else {
                    tf.setOutputProperty("omit-xml-declaration", "yes");
                }
                final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                try {
                    tf.transform(new DOMSource(childNode), new StreamResult(new OutputStreamWriter(byteStream)));
                }
                finally {
                    byteStream.close();
                }
                strBuf.append(byteStream.toString());
                if (i == 0) {
                    strBuf = new StringBuffer(strBuf.toString().replaceFirst("<!DOCTYPE table SYSTEM", "<!DOCTYPE data-dictionary SYSTEM"));
                    final int index = strBuf.indexOf("<table");
                    if (index != -1) {
                        final String ddtag = "<data-dictionary name=\"" + nodelist.item(0).getAttributes().getNamedItem("name").getNodeValue() + "\">\n";
                        strBuf = new StringBuffer(strBuf.substring(0, index) + ddtag + strBuf.substring(index));
                    }
                }
                strBuf.append("\n\n");
            }
        }
        strBuf.append("</data-dictionary>");
        new File(fileNameWithFullPath).delete();
        final RandomAccessFile rd = new RandomAccessFile(fileNameWithFullPath, "rw");
        rd.write(strBuf.toString().getBytes());
        rd.close();
    }
    
    public static void generateDD(final DataDictionary dd) throws DDXMLGeneratorException, MetaDataException {
        final String url = dd.getURL();
        if (url == null || url.trim().equals("")) {
            throw new MetaDataException("URL cannot be null/empty");
        }
        generateDD(dd, url);
    }
    
    public static void generateDD(final DataDictionary dd, final String fileNameWithFullPath) throws DDXMLGeneratorException, MetaDataException {
        try {
            final Document document = getDocumentForDD(dd);
            writeToStream(fileNameWithFullPath, document);
        }
        catch (final TransformerConfigurationException tce) {
            throw new DDXMLGeneratorException(getExceptionMessage(tce), tce);
        }
        catch (final TransformerException te) {
            throw new DDXMLGeneratorException(getExceptionMessage(te), te);
        }
        catch (final Exception e) {
            throw new MetaDataException("Exception occured while converting DD to Xml", e);
        }
    }
    
    private static String getExceptionMessage(final Exception e) {
        return String.valueOf(DDXMLGeneratorException.getErrorCode(e)) + ": " + e.getMessage();
    }
    
    public static void generateDD(final DataDictionary dd, final OutputStream outputStream) throws DDXMLGeneratorException, MetaDataException {
        try {
            final Document document = getDocumentForDD(dd);
            writeToStream(outputStream, document);
        }
        catch (final TransformerConfigurationException tce) {
            throw new DDXMLGeneratorException(getExceptionMessage(tce), tce);
        }
        catch (final TransformerException te) {
            throw new DDXMLGeneratorException(getExceptionMessage(te), te);
        }
        catch (final Exception e) {
            throw new MetaDataException("Exception occured while converting DD to Xml", e);
        }
    }
}
