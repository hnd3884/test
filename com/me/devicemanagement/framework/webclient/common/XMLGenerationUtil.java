package com.me.devicemanagement.framework.webclient.common;

import com.adventnet.persistence.DataAccessException;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import java.io.PrintWriter;
import javax.xml.transform.Transformer;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import com.me.devicemanagement.framework.utils.XMLUtils;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import java.io.OutputStream;
import org.w3c.dom.Element;
import java.util.logging.Logger;

public class XMLGenerationUtil
{
    private String className;
    private Logger logger;
    
    public XMLGenerationUtil() {
        this.className = XMLGenerationUtil.class.getName();
        this.logger = Logger.getLogger(this.className);
    }
    
    private OutputStream createXMLDocument(final Element data) throws SyMException {
        ByteArrayOutputStream outNode = null;
        try {
            final DOMSource source = new DOMSource(data);
            final Transformer transformer = XMLUtils.getTransformerInstance();
            outNode = new ByteArrayOutputStream();
            final StreamResult result = new StreamResult(outNode);
            transformer.transform(source, result);
        }
        catch (final TransformerConfigurationException ee) {
            this.logger.log(Level.WARNING, "Exception occured while creating the xml output", ee);
            throw new SyMException(1002, "Exception occured while creating the xml output", ee);
        }
        catch (final TransformerException ee2) {
            this.logger.log(Level.WARNING, "Exception occured while creating the xml output", ee2);
            throw new SyMException(1002, "Exception occured while creating the xml output", ee2);
        }
        finally {
            try {
                if (outNode != null) {
                    outNode.close();
                }
            }
            catch (final IOException e) {
                this.logger.log(Level.WARNING, "Exception has occured while closing the streams in createXMLObject", e);
                throw new SyMException(1001, "Exception has occured while closing the streams in createXMLObject", e);
            }
        }
        return outNode;
    }
    
    public void generateXMLContent(final Element parentData, final PrintWriter writer) {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = (ByteArrayOutputStream)this.createXMLDocument(parentData);
            writer.print(outputStream.toString());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (final IOException ie) {
                this.logger.log(Level.WARNING, "Exception has occured while closing the streams in getNetworkTree", ie);
            }
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (final IOException ie2) {
                this.logger.log(Level.WARNING, "Exception has occured while closing the streams in getNetworkTree", ie2);
            }
        }
    }
    
    private Document createDocument() throws ParserConfigurationException {
        final DocumentBuilder builder = XMLUtils.getDocumentBuilderInstance(true, false);
        final Document document = builder.newDocument();
        return document;
    }
    
    public Element createRootElement(final String parentNodeName) throws ParserConfigurationException {
        Element parentData = null;
        final Document document = this.createDocument();
        parentData = document.createElement(parentNodeName);
        document.appendChild(parentData);
        return parentData;
    }
    
    public Element generateElementFromTableModel(final Element rootElement, final String tableName, final TableNavigatorModel tModel) throws ParserConfigurationException {
        final long recordsCount = tModel.getTotalRecordsCount();
        tModel.showRange(1L, recordsCount);
        final int columnCount = tModel.getColumnCount();
        for (int rowCount = tModel.getRowCount(), i = 0; i < rowCount; ++i) {
            final Element childElement = this.createElement(rootElement, tableName);
            for (int j = 0; j < columnCount; ++j) {
                final String columnName = tModel.getColumnName(j);
                final String columnValue = tModel.getValueAt(i, j).toString();
                childElement.setAttribute(columnName, columnValue);
            }
            rootElement.appendChild(childElement);
        }
        return rootElement;
    }
    
    public Element generateElementFromDataObject(final Element rootElement, final String tableName, final DataObject dataObject) throws DataAccessException, ParserConfigurationException {
        final int size = dataObject.size(tableName);
        if (size > 0) {
            final Iterator iterator = dataObject.getRows(tableName);
            while (iterator.hasNext()) {
                final Element childElement = this.createElement(rootElement, tableName);
                final Row rowObject = iterator.next();
                final List columnList = rowObject.getColumns();
                for (int j = 0; j < columnList.size(); ++j) {
                    final String columnName = columnList.get(j);
                    final Object columnValue = rowObject.getOriginalValue(columnName);
                    if (columnValue != null) {
                        childElement.setAttribute(columnName, columnValue.toString());
                    }
                }
                rootElement.appendChild(childElement);
            }
        }
        return rootElement;
    }
    
    public Element createElement(final Element parentNode, final String childNodeName) throws ParserConfigurationException {
        Document document = null;
        if (parentNode == null) {
            document = this.createDocument();
        }
        else {
            document = parentNode.getOwnerDocument();
        }
        final Element childElement = document.createElement(childNodeName);
        return childElement;
    }
    
    public Element createErrorNode(final Element parentData, String errorStr) throws ParserConfigurationException {
        final Element error = this.createElement(parentData, "error");
        Integer errorCode = new Integer(1);
        if (errorStr == null) {
            errorStr = "RuntimeException";
        }
        else if (errorStr.equals("")) {
            errorCode = new Integer(0);
        }
        error.setAttribute("ErrCode", errorCode.toString());
        error.setAttribute("ErrString", errorStr);
        parentData.appendChild(error);
        return parentData;
    }
}
