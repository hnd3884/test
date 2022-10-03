package com.adventnet.tools.update.installer;

import java.util.Hashtable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.util.Properties;
import org.w3c.dom.Node;
import java.util.ArrayList;

public class CustomPatchValidator
{
    private String validatorClass;
    private ArrayList<String> dependentClassesList;
    private ArrayList<String> classPathList;
    private Node validatorNode;
    private Properties prop;
    
    public static void main(final String[] args) throws Exception {
        final FileInputStream fis = new FileInputStream(args[0]);
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        final Document doc = docBuilder.parse(fis);
        final NodeList nl = doc.getElementsByTagName("customPatchValidator");
        final CustomPatchValidator cpv = new CustomPatchValidator(nl.item(0));
    }
    
    public Node getCustomPatchValidatorNode() {
        return this.validatorNode;
    }
    
    public CustomPatchValidator(final Node node) {
        this.validatorClass = null;
        this.dependentClassesList = null;
        this.classPathList = null;
        this.validatorNode = null;
        this.prop = null;
        this.process(this.validatorNode = node);
    }
    
    private void process(final Node rootNode) {
        this.validatorClass = rootNode.getAttributes().getNamedItem("name").getNodeValue();
        this.setDependentClassesList(((Element)rootNode).getElementsByTagName("dependentClassPath"));
        this.setClassPathList(((Element)rootNode).getElementsByTagName("classPath"));
        this.setProperties(((Element)rootNode).getElementsByTagName("property"));
    }
    
    public void setProperties(final NodeList nl) {
        this.prop = new Properties();
        if (nl != null) {
            for (int i = 0; i < nl.getLength(); ++i) {
                final Element ele = (Element)nl.item(i);
                this.prop.setProperty(ele.getAttribute("name"), ele.getAttribute("value"));
            }
        }
    }
    
    public void addProperty(final String name, final String value) {
        ((Hashtable<String, String>)this.prop).put(name, value);
    }
    
    public void setClassPathList(final NodeList cl) {
        this.classPathList = new ArrayList<String>();
        for (int i = 0; i < cl.getLength(); ++i) {
            this.classPathList.add(cl.item(i).getFirstChild().getNodeValue());
        }
    }
    
    public void setDependentClassesList(final NodeList dl) {
        this.dependentClassesList = new ArrayList<String>();
        for (int i = 0; i < dl.getLength(); ++i) {
            this.dependentClassesList.add(dl.item(i).getFirstChild().getNodeValue());
        }
    }
    
    public void setValidatorClass(final String validatorClass) {
        this.validatorClass = validatorClass;
    }
    
    public String getValidatorClass() {
        return this.validatorClass;
    }
    
    public ArrayList<String> getDependentClassesList() {
        return this.dependentClassesList;
    }
    
    public ArrayList<String> getClassPathList() {
        return this.classPathList;
    }
    
    public Properties getProperties() {
        return this.prop;
    }
}
