package org.apache.poi.xssf.extractor;

import org.apache.poi.util.POILogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.text.DateFormat;
import org.apache.poi.util.LocaleUtil;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.CellType;
import javax.xml.validation.Validator;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import javax.xml.transform.Transformer;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.util.List;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.usermodel.XSSFTableColumn;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import java.util.Vector;
import org.w3c.dom.Node;
import org.apache.poi.ooxml.util.DocumentHelper;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import java.io.OutputStream;
import java.util.HashMap;
import org.apache.poi.xssf.usermodel.XSSFMap;
import org.apache.poi.util.POILogger;
import java.util.Comparator;

public class XSSFExportToXml implements Comparator<String>
{
    private static final POILogger LOG;
    private XSSFMap map;
    private final HashMap<String, Integer> indexMap;
    
    public XSSFExportToXml(final XSSFMap map) {
        this.indexMap = new HashMap<String, Integer>();
        this.map = map;
    }
    
    public void exportToXML(final OutputStream os, final boolean validate) throws SAXException, TransformerException {
        this.exportToXML(os, "UTF-8", validate);
    }
    
    public void exportToXML(final OutputStream os, final String encoding, final boolean validate) throws SAXException, TransformerException {
        final List<XSSFSingleXmlCell> singleXMLCells = this.map.getRelatedSingleXMLCell();
        final List<XSSFTable> tables = this.map.getRelatedTables();
        final String rootElement = this.map.getCtMap().getRootElement();
        final Document doc = DocumentHelper.createDocument();
        Element root;
        if (this.isNamespaceDeclared()) {
            root = doc.createElementNS(this.getNamespace(), rootElement);
        }
        else {
            root = doc.createElementNS("", rootElement);
        }
        doc.appendChild(root);
        final List<String> xpaths = new Vector<String>();
        final Map<String, XSSFSingleXmlCell> singleXmlCellsMappings = new HashMap<String, XSSFSingleXmlCell>();
        final Map<String, XSSFTable> tableMappings = new HashMap<String, XSSFTable>();
        for (final XSSFSingleXmlCell simpleXmlCell : singleXMLCells) {
            xpaths.add(simpleXmlCell.getXpath());
            singleXmlCellsMappings.put(simpleXmlCell.getXpath(), simpleXmlCell);
        }
        for (final XSSFTable table : tables) {
            final String commonXPath = table.getCommonXpath();
            xpaths.add(commonXPath);
            tableMappings.put(commonXPath, table);
        }
        this.indexMap.clear();
        xpaths.sort(this);
        this.indexMap.clear();
        for (final String xpath : xpaths) {
            final XSSFSingleXmlCell simpleXmlCell2 = singleXmlCellsMappings.get(xpath);
            final XSSFTable table2 = tableMappings.get(xpath);
            if (!xpath.matches(".*\\[.*")) {
                if (simpleXmlCell2 != null) {
                    final XSSFCell cell = simpleXmlCell2.getReferencedCell();
                    if (cell != null) {
                        final Node currentNode = this.getNodeByXPath(xpath, doc.getFirstChild(), doc, false);
                        this.mapCellOnNode(cell, currentNode);
                        if ("".equals(currentNode.getTextContent()) && currentNode.getParentNode() != null) {
                            currentNode.getParentNode().removeChild(currentNode);
                        }
                    }
                }
                if (table2 == null) {
                    continue;
                }
                final List<XSSFTableColumn> tableColumns = table2.getColumns();
                final XSSFSheet sheet = table2.getXSSFSheet();
                final int startRow = table2.getStartCellReference().getRow() + table2.getHeaderRowCount();
                for (int endRow = table2.getEndCellReference().getRow(), i = startRow; i <= endRow; ++i) {
                    final XSSFRow row = sheet.getRow(i);
                    final Node tableRootNode = this.getNodeByXPath(table2.getCommonXpath(), doc.getFirstChild(), doc, true);
                    final short startColumnIndex = table2.getStartCellReference().getCol();
                    for (final XSSFTableColumn tableColumn : tableColumns) {
                        final XSSFCell cell2 = row.getCell(startColumnIndex + tableColumn.getColumnIndex());
                        if (cell2 != null) {
                            final XSSFXmlColumnPr xmlColumnPr = tableColumn.getXmlColumnPr();
                            if (xmlColumnPr == null) {
                                continue;
                            }
                            final String localXPath = xmlColumnPr.getLocalXPath();
                            final Node currentNode2 = this.getNodeByXPath(localXPath, tableRootNode, doc, false);
                            this.mapCellOnNode(cell2, currentNode2);
                        }
                    }
                }
            }
        }
        boolean isValid = true;
        if (validate) {
            isValid = this.isValid(doc);
        }
        if (isValid) {
            final Transformer trans = XMLHelper.newTransformer();
            trans.setOutputProperty("omit-xml-declaration", "yes");
            trans.setOutputProperty("indent", "yes");
            trans.setOutputProperty("encoding", encoding);
            final StreamResult result = new StreamResult(os);
            final DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
        }
    }
    
    private boolean isValid(final Document xml) throws SAXException {
        try {
            final SchemaFactory factory = XMLHelper.getSchemaFactory();
            final Source source = new DOMSource(this.map.getSchema());
            final Schema schema = factory.newSchema(source);
            final Validator validator = schema.newValidator();
            validator.validate(new DOMSource(xml));
            return true;
        }
        catch (final IOException e) {
            XSSFExportToXml.LOG.log(7, new Object[] { "document is not valid", e });
            return false;
        }
    }
    
    private void mapCellOnNode(final XSSFCell cell, final Node node) {
        String value = "";
        switch (cell.getCellType()) {
            case STRING: {
                value = cell.getStringCellValue();
                break;
            }
            case BOOLEAN: {
                value += cell.getBooleanCellValue();
                break;
            }
            case ERROR: {
                value = cell.getErrorCellString();
                break;
            }
            case FORMULA: {
                if (cell.getCachedFormulaResultType() == CellType.STRING) {
                    value = cell.getStringCellValue();
                    break;
                }
                if (DateUtil.isCellDateFormatted((Cell)cell)) {
                    value = this.getFormattedDate(cell);
                    break;
                }
                value += cell.getNumericCellValue();
                break;
            }
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted((Cell)cell)) {
                    value = this.getFormattedDate(cell);
                    break;
                }
                value += cell.getRawValue();
                break;
            }
        }
        if (node instanceof Element) {
            final Element currentElement = (Element)node;
            currentElement.setTextContent(value);
        }
        else {
            node.setNodeValue(value);
        }
    }
    
    private String removeNamespace(final String elementName) {
        return elementName.matches(".*:.*") ? elementName.split(":")[1] : elementName;
    }
    
    private String getFormattedDate(final XSSFCell cell) {
        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        sdf.setTimeZone(LocaleUtil.getUserTimeZone());
        return sdf.format(cell.getDateCellValue());
    }
    
    private Node getNodeByXPath(final String xpath, final Node rootNode, final Document doc, final boolean createMultipleInstances) {
        final String[] xpathTokens = xpath.split("/");
        Node currentNode = rootNode;
        for (int i = 2; i < xpathTokens.length; ++i) {
            final String axisName = this.removeNamespace(xpathTokens[i]);
            if (!axisName.startsWith("@")) {
                final NodeList list = currentNode.getChildNodes();
                Node selectedNode = null;
                if (!createMultipleInstances || i != xpathTokens.length - 1) {
                    selectedNode = this.selectNode(axisName, list);
                }
                if (selectedNode == null) {
                    selectedNode = this.createElement(doc, currentNode, axisName);
                }
                currentNode = selectedNode;
            }
            else {
                currentNode = this.createAttribute(doc, currentNode, axisName);
            }
        }
        return currentNode;
    }
    
    private Node createAttribute(final Document doc, final Node currentNode, final String axisName) {
        final String attributeName = axisName.substring(1);
        final NamedNodeMap attributesMap = currentNode.getAttributes();
        Node attribute = attributesMap.getNamedItem(attributeName);
        if (attribute == null) {
            attribute = doc.createAttributeNS("", attributeName);
            attributesMap.setNamedItem(attribute);
        }
        return attribute;
    }
    
    private Node createElement(final Document doc, final Node currentNode, final String axisName) {
        Node selectedNode;
        if (this.isNamespaceDeclared()) {
            selectedNode = doc.createElementNS(this.getNamespace(), axisName);
        }
        else {
            selectedNode = doc.createElementNS("", axisName);
        }
        currentNode.appendChild(selectedNode);
        return selectedNode;
    }
    
    private Node selectNode(final String axisName, final NodeList list) {
        Node selectedNode = null;
        for (int j = 0; j < list.getLength(); ++j) {
            final Node node = list.item(j);
            if (node.getNodeName().equals(axisName)) {
                selectedNode = node;
                break;
            }
        }
        return selectedNode;
    }
    
    private boolean isNamespaceDeclared() {
        final String schemaNamespace = this.getNamespace();
        return schemaNamespace != null && !schemaNamespace.isEmpty();
    }
    
    private String getNamespace() {
        return this.map.getCTSchema().getNamespace();
    }
    
    @Override
    public int compare(final String leftXpath, final String rightXpath) {
        final Node xmlSchema = this.map.getSchema();
        final String[] leftTokens = leftXpath.split("/");
        final String[] rightTokens = rightXpath.split("/");
        String samePath = "";
        final int minLength = Math.min(leftTokens.length, rightTokens.length);
        Node localComplexTypeRootNode = xmlSchema;
        for (int i = 1; i < minLength; ++i) {
            final String leftElementName = leftTokens[i];
            final String rightElementName = rightTokens[i];
            if (!leftElementName.equals(rightElementName)) {
                return this.indexOfElementInComplexType(samePath, leftElementName, rightElementName, localComplexTypeRootNode);
            }
            samePath = samePath + "/" + leftElementName;
            localComplexTypeRootNode = this.getComplexTypeForElement(leftElementName, xmlSchema, localComplexTypeRootNode);
        }
        return 0;
    }
    
    private int indexOfElementInComplexType(final String samePath, final String leftElementName, final String rightElementName, final Node complexType) {
        if (complexType == null) {
            return 0;
        }
        int i = 0;
        Node node = complexType.getFirstChild();
        final String leftWithoutNamespace = this.removeNamespace(leftElementName);
        int leftIndexOf = this.getAndStoreIndex(samePath, leftWithoutNamespace);
        final String rightWithoutNamespace = this.removeNamespace(rightElementName);
        int rightIndexOf;
        for (rightIndexOf = this.getAndStoreIndex(samePath, rightWithoutNamespace); node != null && (rightIndexOf == -1 || leftIndexOf == -1); node = node.getNextSibling()) {
            if (node instanceof Element && "element".equals(node.getLocalName())) {
                final String elementValue = this.getNameOrRefElement(node).getNodeValue();
                if (elementValue.equals(leftWithoutNamespace)) {
                    leftIndexOf = i;
                    this.indexMap.put(samePath + "/" + leftWithoutNamespace, leftIndexOf);
                }
                if (elementValue.equals(rightWithoutNamespace)) {
                    rightIndexOf = i;
                    this.indexMap.put(samePath + "/" + rightWithoutNamespace, rightIndexOf);
                }
            }
            ++i;
        }
        if (leftIndexOf == -1 || rightIndexOf == -1) {
            return 0;
        }
        return Integer.compare(leftIndexOf, rightIndexOf);
    }
    
    private int getAndStoreIndex(final String samePath, final String withoutNamespace) {
        final String withPath = samePath + "/" + withoutNamespace;
        return this.indexMap.getOrDefault(withPath, -1);
    }
    
    private Node getNameOrRefElement(final Node node) {
        final Node returnNode = node.getAttributes().getNamedItem("ref");
        if (returnNode != null) {
            return returnNode;
        }
        return node.getAttributes().getNamedItem("name");
    }
    
    private Node getComplexTypeForElement(final String elementName, final Node xmlSchema, final Node localComplexTypeRootNode) {
        final String elementNameWithoutNamespace = this.removeNamespace(elementName);
        final String complexTypeName = this.getComplexTypeNameFromChildren(localComplexTypeRootNode, elementNameWithoutNamespace);
        Node complexTypeNode = null;
        if (!"".equals(complexTypeName)) {
            complexTypeNode = this.getComplexTypeNodeFromSchemaChildren(xmlSchema, null, complexTypeName);
        }
        return complexTypeNode;
    }
    
    private String getComplexTypeNameFromChildren(final Node localComplexTypeRootNode, final String elementNameWithoutNamespace) {
        if (localComplexTypeRootNode == null) {
            return "";
        }
        Node node = localComplexTypeRootNode.getFirstChild();
        String complexTypeName = "";
        while (node != null) {
            if (node instanceof Element && "element".equals(node.getLocalName())) {
                final Node nameAttribute = this.getNameOrRefElement(node);
                if (nameAttribute.getNodeValue().equals(elementNameWithoutNamespace)) {
                    final Node complexTypeAttribute = node.getAttributes().getNamedItem("type");
                    if (complexTypeAttribute != null) {
                        complexTypeName = complexTypeAttribute.getNodeValue();
                        break;
                    }
                }
            }
            node = node.getNextSibling();
        }
        return complexTypeName;
    }
    
    private Node getComplexTypeNodeFromSchemaChildren(final Node xmlSchema, Node complexTypeNode, final String complexTypeName) {
        for (Node node = xmlSchema.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element && "complexType".equals(node.getLocalName())) {
                final Node nameAttribute = this.getNameOrRefElement(node);
                if (nameAttribute.getNodeValue().equals(complexTypeName)) {
                    for (Node sequence = node.getFirstChild(); sequence != null; sequence = sequence.getNextSibling()) {
                        if (sequence instanceof Element) {
                            final String localName = sequence.getLocalName();
                            if ("sequence".equals(localName) || "all".equals(localName)) {
                                complexTypeNode = sequence;
                                break;
                            }
                        }
                    }
                    if (complexTypeNode != null) {
                        break;
                    }
                }
            }
        }
        return complexTypeNode;
    }
    
    private static void trySet(final String name, final SecurityFeature securityFeature) {
        try {
            securityFeature.accept(name);
        }
        catch (final Exception e) {
            XSSFExportToXml.LOG.log(5, new Object[] { "SchemaFactory feature unsupported", name, e });
        }
        catch (final AbstractMethodError ame) {
            XSSFExportToXml.LOG.log(5, new Object[] { "Cannot set SchemaFactory feature because outdated XML parser in classpath", name, ame });
        }
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XSSFExportToXml.class);
    }
    
    @FunctionalInterface
    private interface SecurityFeature
    {
        void accept(final String p0) throws SAXException;
    }
}
