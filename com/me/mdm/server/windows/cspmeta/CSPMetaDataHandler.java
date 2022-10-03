package com.me.mdm.server.windows.cspmeta;

import org.json.JSONException;
import org.apache.axiom.om.OMElement;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import com.me.mdm.api.error.APIHTTPException;
import org.apache.axiom.om.OMXMLBuilderFactory;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Logger;

public class CSPMetaDataHandler
{
    private static final Logger LOGGER;
    private static HashMap<String, CSPMetaModel> cspMetaModelHashMap;
    private static CSPMetaDataHandler instance;
    
    public static CSPMetaDataHandler getInstance() {
        if (CSPMetaDataHandler.instance == null) {
            CSPMetaDataHandler.instance = new CSPMetaDataHandler();
        }
        return CSPMetaDataHandler.instance;
    }
    
    public JSONObject getMetaDetailsForLocURI(final String locUri) throws Exception {
        return CSPMetaModel.getCSPMetaJSON(CSPMetaDataHandler.cspMetaModelHashMap.get(locUri), locUri);
    }
    
    private CSPMetaDataHandler() {
        InputStream inputStream = null;
        try {
            final String ddfDirectory = new DDFDownloader().getDDFFolderPath();
            if (ddfDirectory == null) {
                CSPMetaDataHandler.LOGGER.log(Level.WARNING, "ddf directory not downloaded nor available, hashmap left empty");
                return;
            }
            final ArrayList<String> files = FileAccessUtil.getFilesListInFolder(ddfDirectory);
            for (final String filePath : files) {
                inputStream = FileAccessUtil.getFileAsInputStream(filePath);
                final OMElement root = OMXMLBuilderFactory.createOMBuilder(inputStream).getDocumentElement();
                this.populateHashMap(root);
                inputStream.close();
            }
        }
        catch (final Exception ex) {
            CSPMetaDataHandler.LOGGER.log(Level.SEVERE, "Exception while processing DDF files : ", ex);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException e) {
                    CSPMetaDataHandler.LOGGER.log(Level.SEVERE, "Unable to close InputStream : ", e);
                }
            }
        }
    }
    
    private void populateHashMap(final OMElement root) throws JSONException {
        final Iterator it = root.getDescendants(false);
        while (it.hasNext()) {
            final Object object = it.next();
            if (object instanceof OMElement) {
                final OMElement element = (OMElement)object;
                if (!element.getLocalName().equals("Node")) {
                    continue;
                }
                final CSPMetaModel cspMetaModel = new CSPMetaModel();
                final String locUri = this.nodeDetails(element, cspMetaModel);
                CSPMetaDataHandler.cspMetaModelHashMap.put(locUri, cspMetaModel);
            }
        }
    }
    
    private String nodeDetails(final OMElement node, final CSPMetaModel cspMetaModel) throws JSONException {
        final Iterator it = node.getChildElements();
        String path = null;
        while (it.hasNext()) {
            final Object object = it.next();
            if (object instanceof OMElement) {
                final OMElement element = (OMElement)object;
                final String localName = element.getLocalName();
                switch (localName) {
                    case "Path": {
                        path = element.getText();
                        continue;
                    }
                    case "DFProperties": {
                        this.fillDFProperty(element, cspMetaModel);
                        continue;
                    }
                }
            }
        }
        if (path == null) {
            path = this.getFullLocURI(node);
        }
        return path;
    }
    
    private void fillDFProperty(final OMElement dfProperty, final CSPMetaModel cspMetaModel) throws JSONException {
        final Iterator it = dfProperty.getChildElements();
        while (it.hasNext()) {
            final Object object = it.next();
            if (object instanceof OMElement) {
                final OMElement element = (OMElement)object;
                final String localName = element.getLocalName();
                switch (localName) {
                    case "AccessType": {
                        this.fillAccessType(element, cspMetaModel);
                        continue;
                    }
                    case "DFFormat": {
                        cspMetaModel.setDataType(this.getFirstChildName(element));
                        continue;
                    }
                }
            }
        }
    }
    
    private void fillAccessType(final OMElement accessType, final CSPMetaModel cspMetaModel) throws JSONException {
        final Iterator it = accessType.getChildElements();
        while (it.hasNext()) {
            final Object object = it.next();
            if (object instanceof OMElement) {
                final OMElement element = (OMElement)object;
                final String localName = element.getLocalName();
                switch (localName) {
                    case "Add": {
                        cspMetaModel.addAddActionType();
                        continue;
                    }
                    case "Delete": {
                        cspMetaModel.addDeleteActionType();
                        continue;
                    }
                    case "Exec": {
                        cspMetaModel.addExecActionType();
                        continue;
                    }
                    case "Replace": {
                        cspMetaModel.addReplaceActionType();
                        continue;
                    }
                }
            }
        }
    }
    
    private String getFirstChildName(final OMElement element) throws JSONException {
        return element.getFirstElement().getLocalName();
    }
    
    private String getFullLocURI(OMElement element) {
        final StringBuilder path = new StringBuilder();
        while (true) {
            final String rootPath = this.getDataFromChildNode(element, "Path");
            final String rootNodeName = this.getDataFromChildNode(element, "NodeName");
            if (rootPath != null) {
                if (path.toString().equals("")) {
                    return rootPath + "/" + rootNodeName;
                }
                return rootPath + "/" + rootNodeName + "/" + path.substring(0, path.length() - 1);
            }
            else {
                final String nodeName = this.getDataFromChildNode(element, "NodeName");
                path.insert(0, nodeName + "/");
                if (!(element.getParent() instanceof OMElement)) {
                    return rootNodeName;
                }
                element = (OMElement)element.getParent();
            }
        }
    }
    
    private String getDataFromChildNode(final OMElement element, final String tagName) {
        final OMElement data = this.getChildNodeWithTag(element, tagName);
        return (data != null) ? data.getText() : null;
    }
    
    private OMElement getChildNodeWithTag(final OMElement element, final String tagName) {
        final Iterator iterator = element.getChildElements();
        while (iterator.hasNext()) {
            final OMElement innerElement = iterator.next();
            if (innerElement.getLocalName().equals(tagName)) {
                return innerElement;
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        CSPMetaDataHandler.cspMetaModelHashMap = new HashMap<String, CSPMetaModel>();
        CSPMetaDataHandler.instance = null;
    }
}
