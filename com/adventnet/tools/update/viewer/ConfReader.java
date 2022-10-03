package com.adventnet.tools.update.viewer;

import java.util.List;
import com.adventnet.tools.update.util.Criteria;
import java.util.Properties;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import com.adventnet.tools.update.util.EnhancedFileFilter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import com.adventnet.tools.update.util.GroupingFunction;
import java.util.Hashtable;
import java.util.ArrayList;
import org.w3c.dom.Element;

public class ConfReader
{
    private Element rootElement;
    private ArrayList baseNodeIDs;
    private Hashtable idVsDispName;
    private Hashtable idVsElement;
    private Hashtable idVsFileFilter;
    private GroupingFunction grFunction;
    private Hashtable filesToExtract;
    private ArrayList docNodeProps;
    
    public static void main(final String[] args) throws Exception {
        final ConfReader cr = new ConfReader(new File("./conf/QueryPatch.xml"));
    }
    
    public ConfReader(final File confFile) throws Exception {
        this.rootElement = null;
        this.baseNodeIDs = new ArrayList();
        this.idVsDispName = new Hashtable();
        this.idVsElement = new Hashtable();
        this.idVsFileFilter = new Hashtable();
        this.grFunction = null;
        this.filesToExtract = new Hashtable();
        this.docNodeProps = new ArrayList();
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        final Document doc = docBuilder.parse(confFile);
        this.rootElement = doc.getDocumentElement();
        final NodeList children = this.rootElement.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node node = children.item(i);
            if (node.getNodeType() == 1) {
                if (node.getNodeName().equals("ExtractFiles")) {
                    this.visitExtractFilesNode((Element)node);
                }
                else if (node.getNodeName().equals("BaseNode")) {
                    this.visitBaseNode((Element)node);
                }
                else if (node.getNodeName().equals("DocumentNode")) {
                    this.visitDocumentNode((Element)node);
                }
                else if (node.getNodeName().equals("GroupingFunction")) {
                    this.grFunction = this.visitAndGetGroupingFunction((Element)node);
                }
            }
        }
    }
    
    public Node getRootNode() {
        return this.rootElement;
    }
    
    public String[] getBaseNodeIDs() {
        return this.baseNodeIDs.toArray(new String[0]);
    }
    
    public Hashtable getFilesToExtract() {
        return this.filesToExtract;
    }
    
    public DocumentNodeProps[] getDocumentNodeProps() {
        return this.docNodeProps.toArray(new DocumentNodeProps[0]);
    }
    
    public String getDisplayName(final String id) {
        return this.idVsDispName.get(id);
    }
    
    public EnhancedFileFilter getFileFilter(final String baseNodeID) {
        return this.idVsFileFilter.get(baseNodeID);
    }
    
    public GroupingFunction getGroupingFunction() {
        return this.grFunction;
    }
    
    private Object instantiateClass(final String className, final String defaultClass) {
        Object obj = null;
        if (className == null || className.equals("")) {
            return null;
        }
        try {
            obj = Class.forName(className).newInstance();
            this.log("Instantiated : " + className);
            return obj;
        }
        catch (final Exception e) {
            System.err.println("Error while instantiating class [ " + className + " ]. Trying to instantiate default class");
            e.printStackTrace();
            try {
                obj = Class.forName(defaultClass).newInstance();
                this.log("Instantiated : " + className);
                return obj;
            }
            catch (final Exception ee) {
                System.err.println("Error while instantiating default class [ " + defaultClass + " ].");
                ee.printStackTrace();
                return null;
            }
        }
    }
    
    private void visitExtractFilesNode(final Element element) {
        final NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node childNode = children.item(i);
            if (childNode.getNodeType() == 1) {
                if (childNode.getNodeName().equals("FileSet")) {
                    final NamedNodeMap attrs = childNode.getAttributes();
                    String extractDir = null;
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        final Attr attr = (Attr)attrs.item(j);
                        if (attr.getName().equals("ExtractDir")) {
                            extractDir = attr.getValue();
                        }
                    }
                    if (extractDir == null) {
                        System.err.println("The Extract Dir Attribute cannot be null");
                    }
                    else {
                        final NodeList tempChildren = childNode.getChildNodes();
                        ArrayList list = this.filesToExtract.get(extractDir);
                        if (list == null) {
                            list = new ArrayList();
                        }
                        for (int k = 0; k < tempChildren.getLength(); ++k) {
                            final Node tempChild = tempChildren.item(k);
                            if (tempChild.getNodeType() == 1) {
                                if (tempChild.getNodeName().equals("File")) {
                                    final NodeList tList = tempChild.getChildNodes();
                                    for (int l = 0; l < tList.getLength(); ++l) {
                                        list.add(tList.item(l).getNodeValue());
                                        if (tList.item(l).getNodeType() == 1) {
                                            list.add(tList.item(l).getNodeName());
                                        }
                                    }
                                }
                            }
                        }
                        this.filesToExtract.put(extractDir, list);
                    }
                }
            }
        }
    }
    
    private void visitBaseNode(final Element element) {
        final NamedNodeMap attribs = element.getAttributes();
        String id = null;
        String dispName = null;
        for (int i = 0; i < attribs.getLength(); ++i) {
            final Attr attr = (Attr)attribs.item(i);
            if (attr.getName().equals("ID")) {
                id = attr.getValue();
                this.baseNodeIDs.add(id);
                this.idVsElement.put(id, element);
                final NodeList children = element.getChildNodes();
                for (int j = 0; j < children.getLength(); ++j) {
                    final Node childNode = children.item(j);
                    if (childNode.getNodeType() == 1) {
                        final Element tempElement = (Element)childNode;
                        if (tempElement.getTagName().equals("Filter")) {
                            final EnhancedFileFilter filter = this.visitAndGetFilter(tempElement);
                            if (filter != null) {
                                this.idVsFileFilter.put(id, filter);
                            }
                        }
                    }
                }
            }
            else if (attr.getName().equals("DisplayName")) {
                dispName = attr.getValue();
            }
        }
        if (dispName == null) {
            this.idVsDispName.put(id, id);
        }
        else {
            this.idVsDispName.put(id, dispName);
        }
    }
    
    private void visitDocumentNode(final Element element) {
        final NamedNodeMap attrs = element.getAttributes();
        String id = null;
        String desc = null;
        String dispName = null;
        String fileToExtract = null;
        String dirToExtract = null;
        final Properties miscProps = new Properties();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("ID")) {
                id = attr.getValue();
            }
            else if (attr.getName().equals("DisplayName")) {
                dispName = attr.getValue();
            }
            else if (attr.getName().equals("Description")) {
                desc = attr.getValue();
            }
            else if (attr.getName().equals("File")) {
                fileToExtract = attr.getValue();
            }
            else if (attr.getName().equals("ExtractDir")) {
                dirToExtract = attr.getValue();
            }
            else {
                miscProps.setProperty(attr.getName(), attr.getValue());
            }
        }
        if (id == null) {
            System.err.println("The ID field is null in the Document Node");
            return;
        }
        final DocumentNodeProps dnp = new DocumentNodeProps(id, dispName, fileToExtract);
        dnp.setDescription(desc);
        dnp.setMiscProperties(miscProps);
        this.docNodeProps.add(dnp);
    }
    
    private EnhancedFileFilter visitAndGetFilter(final Element element) {
        final NamedNodeMap attribs = element.getAttributes();
        String className = null;
        EnhancedFileFilter filter = null;
        for (int i = 0; i < attribs.getLength(); ++i) {
            final Attr attr = (Attr)attribs.item(i);
            if (attr.getName().equals("ClassName")) {
                className = attr.getValue();
            }
        }
        filter = (EnhancedFileFilter)this.instantiateClass(className, "com.adventnet.tools.update.util.EnhancedFileFilter");
        if (filter == null) {
            return null;
        }
        Criteria crit = null;
        final NodeList children = element.getChildNodes();
        for (int j = 0; j < children.getLength(); ++j) {
            final Node childNode = children.item(j);
            if (childNode.getNodeType() == 1) {
                final Element tempElement = (Element)childNode;
                if (tempElement.getTagName().equals("Criteria")) {
                    crit = this.visitAndGetCriteria(tempElement);
                }
            }
        }
        filter.setCriteria(crit);
        return filter;
    }
    
    private Criteria visitAndGetCriteria(final Element element) {
        final NamedNodeMap attrs = element.getAttributes();
        String className = null;
        Criteria crit = null;
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("ClassName")) {
                className = attr.getValue();
            }
        }
        crit = (Criteria)this.instantiateClass(className, "com.adventnet.tools.update.util.Criteria");
        if (crit == null) {
            return null;
        }
        final NodeList children = element.getChildNodes();
        for (int j = 0; j < children.getLength(); ++j) {
            final Node childNode = children.item(j);
            if (childNode.getNodeType() == 1) {
                final NamedNodeMap tempAttrs = childNode.getAttributes();
                String key = null;
                String action = null;
                for (int k = 0; k < tempAttrs.getLength(); ++k) {
                    final Attr attr2 = (Attr)tempAttrs.item(k);
                    if (attr2.getName().equals("Key")) {
                        key = attr2.getValue();
                    }
                    else if (attr2.getName().equals("Action")) {
                        action = attr2.getValue();
                    }
                    crit.addCriterion(key, action);
                }
            }
        }
        this.log("Crit : " + crit);
        return crit;
    }
    
    private GroupingFunction visitAndGetGroupingFunction(final Element element) {
        final NodeList children = element.getChildNodes();
        final ArrayList groupNames = new ArrayList();
        final ArrayList filterNames = new ArrayList();
        final NamedNodeMap attribs = element.getAttributes();
        String className = null;
        for (int i = 0; i < attribs.getLength(); ++i) {
            final Attr attr = (Attr)attribs.item(i);
            if (attr.getName().equals("ClassName")) {
                className = attr.getValue();
            }
        }
        for (int i = 0; i < children.getLength(); ++i) {
            final Node childNode = children.item(i);
            if (childNode.getNodeType() == 1) {
                if (childNode.getNodeName().equals("Group")) {
                    String groupName = null;
                    final NamedNodeMap attrs = childNode.getAttributes();
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        final Attr attr2 = (Attr)attrs.item(j);
                        if (attr2.getName().equals("Key")) {
                            groupName = attr2.getValue();
                        }
                    }
                    final NodeList tempChildren = childNode.getChildNodes();
                    for (int k = 0; k < tempChildren.getLength(); ++k) {
                        final Node tempChild = tempChildren.item(k);
                        if (tempChild.getNodeType() == 1) {
                            if (tempChild.getNodeName().equals("Filter")) {
                                final EnhancedFileFilter filter = this.visitAndGetFilter((Element)tempChild);
                                if (groupName != null && filter != null) {
                                    groupNames.add(groupName);
                                    filterNames.add(filter);
                                }
                            }
                        }
                    }
                }
            }
        }
        final GroupingFunction gf = (GroupingFunction)this.instantiateClass(className, "com.adventnet.tools.update.util.GroupingFunction");
        gf.setFilters(groupNames, filterNames);
        return gf;
    }
    
    private void log(final String message) {
    }
}
