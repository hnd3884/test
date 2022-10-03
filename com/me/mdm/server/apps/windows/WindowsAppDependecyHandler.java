package com.me.mdm.server.apps.windows;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants;
import javax.xml.parsers.DocumentBuilder;
import java.util.logging.Level;
import javax.xml.xpath.XPathFactory;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import java.util.Collection;
import java.io.File;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import java.util.HashMap;
import com.me.mdm.server.apps.AppDependencyInterface;

public class WindowsAppDependecyHandler implements AppDependencyInterface
{
    private static final String APPX_PRODUCT_EXPRESSION = "/*/*[local-name()='Identity']/@Name";
    private static final String APPX_ARCHITECTURE_EXPRESSION = "/*/*[local-name()='Identity']/@ProcessorArchitecture";
    private static final String APPX_ISFRAMEWORK_EXPRESSION = "/*/*[local-name()='Properties']/*[local-name()='Framework']/text()";
    private static final String APPX_VERSION_EXPRESSION = "/*/*[local-name()='Identity']/@Version";
    private static final String FRAMEWORK_LIST_EXPRESSION = "/*/*[local-name()='Dependencies']/*[local-name()='PackageDependency']";
    private static final String WINDOWS_APPX_MANIFEST_XML = "AppxManifest.xml";
    private static final int WINDOWS_FRAMEWORK_MANIFEST_QUERY = 1;
    private static final int WINDOWS_FRAMEWORK_LIST_QUERY = 2;
    HashMap expressions;
    public Document xmlDoc;
    public XPath xpath;
    public String manifestXmlPath;
    private static Logger logger;
    
    public WindowsAppDependecyHandler() {
        this.expressions = null;
        this.xmlDoc = null;
        this.xpath = null;
        this.manifestXmlPath = null;
    }
    
    @Override
    public HashMap extractAndValidateUpload(final String fileName) {
        final String manifestFileLoc = this.getManifestFileLoc(fileName);
        this.createQuery(1);
        final HashMap props = this.getPropertiesFromManifest(manifestFileLoc);
        final String isFrameWork = props.get("IsFramework");
        if (isFrameWork == null || isFrameWork.equals("") || isFrameWork.equals("false")) {
            props.put("success", false);
            props.put("message", "file not a framework");
        }
        else {
            props.put("success", true);
            props.put("identifier", props.get("name"));
        }
        return props;
    }
    
    @Override
    public List getDependenciesFromApp(final String fileName) {
        final List dependencyList = new ArrayList();
        this.manifestXmlPath = fileName;
        this.initalize();
        final NodeList nodeList = this.getNodeList("/*/*[local-name()='Dependencies']/*[local-name()='PackageDependency']");
        for (int it = 0; it < nodeList.getLength(); ++it) {
            final Node node = nodeList.item(it);
            if (node.getNodeName().equals("PackageDependency")) {
                final JSONObject dependencyObj = new JSONObject();
                final NamedNodeMap namedNodeMap = node.getAttributes();
                final Node nameNode = namedNodeMap.getNamedItem("Name");
                final Node minVersionNode = namedNodeMap.getNamedItem("MinVersion");
                if (nameNode != null) {
                    dependencyObj.put("name", (Object)nameNode.getNodeValue());
                    if (minVersionNode != null) {
                        dependencyObj.put("min_version", (Object)minVersionNode.getNodeValue());
                    }
                    dependencyList.add(dependencyObj);
                }
            }
        }
        return dependencyList;
    }
    
    @Override
    public List getDependencyFilePaths(final String extractedDir) {
        final List dependencyList = new ArrayList();
        final File parentDir = new File(extractedDir);
        if (parentDir.exists()) {
            final File[] listOfFiles = parentDir.listFiles();
            for (int i = 0; i < listOfFiles.length; ++i) {
                if (listOfFiles[i].isFile()) {
                    final String curFile = listOfFiles[i].getAbsolutePath();
                    if (!curFile.trim().endsWith(".zip") && curFile.trim().endsWith(".appx")) {
                        dependencyList.add(curFile);
                    }
                }
                else if (listOfFiles[i].isDirectory()) {
                    dependencyList.addAll(this.getDependencyFilePaths(listOfFiles[i].getAbsolutePath()));
                }
            }
        }
        return dependencyList;
    }
    
    private HashMap getPropertiesFromManifest(final String manifestFileLoc) {
        final HashMap hashMap = new HashMap();
        final Iterator iterator = this.expressions.keySet().iterator();
        this.manifestXmlPath = manifestFileLoc;
        this.initalize();
        while (iterator.hasNext()) {
            final String curExpKey = iterator.next();
            final String value = this.expressions.get(curExpKey);
            String nodeValue = this.getNodeName(value);
            if (nodeValue == null) {
                nodeValue = "";
            }
            nodeValue = nodeValue.replaceAll("[{}]", " ");
            hashMap.put(curExpKey, nodeValue);
        }
        return hashMap;
    }
    
    private String getManifestFileLoc(final String fileName) {
        final File file = new File(fileName);
        final File xapDirectory = new File(file.getParent());
        String manifestFileLocation = null;
        ApiFactoryProvider.getZipUtilAPI().unzip(fileName, xapDirectory.toString(), true, true, new String[] { "AppxManifest.xml" });
        manifestFileLocation = xapDirectory.toString() + File.separator.toString() + "AppxManifest.xml";
        return manifestFileLocation;
    }
    
    private void initalize() {
        try {
            final DocumentBuilder builder = DMSecurityUtil.getDocumentBuilder();
            this.xmlDoc = builder.parse(this.manifestXmlPath);
            this.xpath = XPathFactory.newInstance().newXPath();
        }
        catch (final Exception exp) {
            WindowsAppDependecyHandler.logger.log(Level.WARNING, "Exception occurred in initalize {0}", exp);
        }
    }
    
    private String getNodeName(final String expression) {
        String nodeName = null;
        final NodeList nodes = this.getNodeList(expression);
        if (nodes != null) {
            if (nodes.getLength() == 1) {
                nodeName = nodes.item(0).getNodeValue();
            }
            else {
                nodeName = "";
                for (int i = 0; i < nodes.getLength(); ++i) {
                    if (nodeName == "") {
                        nodeName = nodes.item(i).getNodeValue();
                    }
                    else {
                        nodeName = nodeName + "," + nodes.item(i).getNodeValue();
                    }
                }
            }
        }
        WindowsAppDependecyHandler.logger.log(Level.INFO, "Node Name is {0}", nodeName);
        return nodeName;
    }
    
    private NodeList getNodeList(final String expression) {
        try {
            XPathExpression expr = null;
            expr = this.xpath.compile(expression);
            final Object result = expr.evaluate(this.xmlDoc, XPathConstants.NODESET);
            return (NodeList)result;
        }
        catch (final Exception exp) {
            WindowsAppDependecyHandler.logger.log(Level.WARNING, "Exception ocurred in querying the values {0}", exp);
            return null;
        }
    }
    
    private void createQuery(final int type) {
        switch (type) {
            case 1: {
                (this.expressions = new HashMap()).put("name", "/*/*[local-name()='Identity']/@Name");
                this.expressions.put("IsFramework", "/*/*[local-name()='Properties']/*[local-name()='Framework']/text()");
                this.expressions.put("architecture", "/*/*[local-name()='Identity']/@ProcessorArchitecture");
                this.expressions.put("version", "/*/*[local-name()='Identity']/@Version");
                break;
            }
            case 2: {
                (this.expressions = new HashMap()).put("dependencies", "/*/*[local-name()='Dependencies']/*[local-name()='PackageDependency']");
                break;
            }
        }
    }
    
    static {
        WindowsAppDependecyHandler.logger = Logger.getLogger("MDMLogger");
    }
}
