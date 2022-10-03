package org.apache.poi.xssf.extractor;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import org.apache.poi.util.POILogFactory;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.usermodel.DateUtil;
import java.text.SimpleDateFormat;
import org.apache.poi.util.LocaleUtil;
import java.io.IOException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;
import java.util.Iterator;
import javax.xml.xpath.XPath;
import java.util.List;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.apache.poi.xssf.usermodel.XSSFTableColumn;
import org.w3c.dom.NodeList;
import org.apache.poi.xssf.usermodel.XSSFTable;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Node;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import javax.xml.namespace.NamespaceContext;
import org.apache.poi.ooxml.util.XPathHelper;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.XSSFMap;

public class XSSFImportFromXML
{
    private final XSSFMap _map;
    private static final POILogger logger;
    
    public XSSFImportFromXML(final XSSFMap map) {
        this._map = map;
    }
    
    public void importFromXML(final String xmlInputString) throws SAXException, XPathExpressionException, IOException {
        final DocumentBuilder builder = DocumentHelper.newDocumentBuilder();
        final Document doc = builder.parse(new InputSource(new StringReader(xmlInputString.trim())));
        final List<XSSFSingleXmlCell> singleXmlCells = this._map.getRelatedSingleXMLCell();
        final List<XSSFTable> tables = this._map.getRelatedTables();
        final XPath xpath = XPathHelper.getFactory().newXPath();
        xpath.setNamespaceContext(new DefaultNamespaceContext(doc));
        for (final XSSFSingleXmlCell singleXmlCell : singleXmlCells) {
            final STXmlDataType.Enum xmlDataType = singleXmlCell.getXmlDataType();
            final String xpathString = singleXmlCell.getXpath();
            final Node result = (Node)xpath.evaluate(xpathString, doc, XPathConstants.NODE);
            if (result != null) {
                final String textContent = result.getTextContent();
                XSSFImportFromXML.logger.log(1, new Object[] { "Extracting with xpath " + xpathString + " : value is '" + textContent + "'" });
                final XSSFCell cell = singleXmlCell.getReferencedCell();
                XSSFImportFromXML.logger.log(1, new Object[] { "Setting '" + textContent + "' to cell " + cell.getColumnIndex() + "-" + cell.getRowIndex() + " in sheet " + cell.getSheet().getSheetName() });
                this.setCellValue(textContent, cell, xmlDataType);
            }
        }
        for (final XSSFTable table : tables) {
            final String commonXPath = table.getCommonXpath();
            final NodeList result2 = (NodeList)xpath.evaluate(commonXPath, doc, XPathConstants.NODESET);
            final int rowOffset = table.getStartCellReference().getRow() + table.getHeaderRowCount();
            final int columnOffset = table.getStartCellReference().getCol();
            table.setDataRowCount(result2.getLength());
            for (int i = 0; i < result2.getLength(); ++i) {
                final Node singleNode = result2.item(i).cloneNode(true);
                for (final XSSFTableColumn tableColumn : table.getColumns()) {
                    final XSSFXmlColumnPr xmlColumnPr = tableColumn.getXmlColumnPr();
                    if (xmlColumnPr == null) {
                        continue;
                    }
                    final int rowId = rowOffset + i;
                    final int columnId = columnOffset + tableColumn.getColumnIndex();
                    String localXPath = xmlColumnPr.getLocalXPath();
                    localXPath = localXPath.substring(localXPath.indexOf(47, 1) + 1);
                    final String value = (String)xpath.evaluate(localXPath, singleNode, XPathConstants.STRING);
                    XSSFImportFromXML.logger.log(1, new Object[] { "Extracting with xpath " + localXPath + " : value is '" + value + "'" });
                    XSSFRow row = table.getXSSFSheet().getRow(rowId);
                    if (row == null) {
                        row = table.getXSSFSheet().createRow(rowId);
                    }
                    XSSFCell cell2 = row.getCell(columnId);
                    if (cell2 == null) {
                        cell2 = row.createCell(columnId);
                    }
                    XSSFImportFromXML.logger.log(1, new Object[] { "Setting '" + value + "' to cell " + cell2.getColumnIndex() + "-" + cell2.getRowIndex() + " in sheet " + table.getXSSFSheet().getSheetName() });
                    this.setCellValue(value, cell2, xmlColumnPr.getXmlDataType());
                }
            }
        }
    }
    
    private void setCellValue(final String value, final XSSFCell cell, final STXmlDataType.Enum xmlDataType) {
        final DataType type = DataType.getDataType(xmlDataType);
        try {
            if (value.isEmpty() || type == null) {
                cell.setCellValue((String)null);
            }
            else {
                switch (type) {
                    case BOOLEAN: {
                        cell.setCellValue(Boolean.parseBoolean(value));
                        break;
                    }
                    case DOUBLE: {
                        cell.setCellValue(Double.parseDouble(value));
                        break;
                    }
                    case INTEGER: {
                        cell.setCellValue((double)Integer.parseInt(value));
                        break;
                    }
                    case DATE: {
                        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", LocaleUtil.getUserLocale());
                        final Date date = sdf.parse(value);
                        cell.setCellValue(date);
                        if (!DateUtil.isValidExcelDate(cell.getNumericCellValue())) {
                            cell.setCellValue(value);
                            break;
                        }
                        break;
                    }
                    default: {
                        cell.setCellValue(value.trim());
                        break;
                    }
                }
            }
        }
        catch (final IllegalArgumentException | ParseException e) {
            throw new IllegalArgumentException(String.format(LocaleUtil.getUserLocale(), "Unable to format value '%s' as %s for cell %s", value, type, new CellReference((Cell)cell).formatAsString()));
        }
    }
    
    static {
        logger = POILogFactory.getLogger((Class)XSSFImportFromXML.class);
    }
    
    private enum DataType
    {
        BOOLEAN(new STXmlDataType.Enum[] { STXmlDataType.BOOLEAN }), 
        DOUBLE(new STXmlDataType.Enum[] { STXmlDataType.DOUBLE }), 
        INTEGER(new STXmlDataType.Enum[] { STXmlDataType.INT, STXmlDataType.UNSIGNED_INT, STXmlDataType.INTEGER }), 
        STRING(new STXmlDataType.Enum[] { STXmlDataType.STRING }), 
        DATE(new STXmlDataType.Enum[] { STXmlDataType.DATE });
        
        private Set<STXmlDataType.Enum> xmlDataTypes;
        
        private DataType(final STXmlDataType.Enum[] xmlDataTypes) {
            this.xmlDataTypes = new HashSet<STXmlDataType.Enum>(Arrays.asList(xmlDataTypes));
        }
        
        public static DataType getDataType(final STXmlDataType.Enum xmlDataType) {
            for (final DataType dataType : values()) {
                if (dataType.xmlDataTypes.contains(xmlDataType)) {
                    return dataType;
                }
            }
            return null;
        }
    }
    
    private static final class DefaultNamespaceContext implements NamespaceContext
    {
        private final Element _docElem;
        
        public DefaultNamespaceContext(final Document doc) {
            this._docElem = doc.getDocumentElement();
        }
        
        @Override
        public String getNamespaceURI(final String prefix) {
            return this.getNamespaceForPrefix(prefix);
        }
        
        private String getNamespaceForPrefix(final String prefix) {
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            Node parent = this._docElem;
            while (parent != null) {
                final int type = parent.getNodeType();
                if (type == 1) {
                    if (parent.getNodeName().startsWith(prefix + ":")) {
                        return parent.getNamespaceURI();
                    }
                    final NamedNodeMap nnm = parent.getAttributes();
                    for (int i = 0; i < nnm.getLength(); ++i) {
                        final Node attr = nnm.item(i);
                        final String aname = attr.getNodeName();
                        final boolean isPrefix = aname.startsWith("xmlns:");
                        if (isPrefix || aname.equals("xmlns")) {
                            final int index = aname.indexOf(58);
                            final String p = isPrefix ? aname.substring(index + 1) : "";
                            if (p.equals(prefix)) {
                                return attr.getNodeValue();
                            }
                        }
                    }
                    parent = parent.getParentNode();
                }
                else {
                    if (type == 5) {
                        continue;
                    }
                    break;
                }
            }
            return null;
        }
        
        @Override
        public Iterator<String> getPrefixes(final String val) {
            return null;
        }
        
        @Override
        public String getPrefix(final String uri) {
            return null;
        }
    }
}
