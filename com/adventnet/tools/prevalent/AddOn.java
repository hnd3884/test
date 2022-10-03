package com.adventnet.tools.prevalent;

import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import org.w3c.dom.Element;

class AddOn
{
    private static AddOn add;
    Element compElement;
    private Element root;
    private ArrayList components;
    private ArrayList oldComponents;
    private String userID;
    
    AddOn() {
        this.compElement = null;
        this.root = null;
        this.components = null;
        this.oldComponents = null;
        this.userID = null;
    }
    
    public static AddOn getInstance() {
        if (AddOn.add == null) {
            AddOn.add = new AddOn();
        }
        return AddOn.add;
    }
    
    public boolean updateXML(final ArrayList component, final String lFilePath, final String mapId) {
        this.userID = mapId;
        this.oldComponents = component;
        String licenseFilePath = lFilePath + File.separator + LUtil.getLicenseDir() + File.separator + "AdventNetLicense.xml";
        if (!new File(licenseFilePath).exists()) {
            licenseFilePath = lFilePath + File.separator + LUtil.getLicenseDir() + File.separator + "StandardEvaluation.xml";
        }
        LUtil.copyFile(new File(licenseFilePath), new File(lFilePath + File.separator + LUtil.getLicenseDir() + File.separator + "AdventNetLicense.xml_bkp"));
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setValidating(false);
            docBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            docBuilder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(final String publicId, final String systemId) {
                    return new InputSource(new StringReader(""));
                }
            });
            final Document doc = docBuilder.parse(licenseFilePath);
            this.root = doc.getDocumentElement();
            this.components = this.getNewComponents(doc, licenseFilePath);
            this.createElement(doc);
            this.UpdateXmlFile(doc, licenseFilePath);
            this.UpdateKey(doc, licenseFilePath);
            this.UpdateXmlFile(doc, licenseFilePath);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    private void UpdateXmlFile(final Document doc, final String licenseFilePath) throws Exception {
        FileOutputStream fos = null;
        try {
            final TransformerFactory tFactory = TransformerFactory.newInstance();
            final Transformer transformer = tFactory.newTransformer();
            final Node node = doc.getDocumentElement();
            final DOMSource source = new DOMSource(node);
            fos = new FileOutputStream(new File(licenseFilePath));
            final StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
    
    private Element createPropertyElement(final Document docu, final String name, final String value, final String limitValue) {
        final Element ele = docu.createElement("Properties");
        if (name != null) {
            ele.setAttribute("Name", name);
        }
        if (value != null) {
            if (name.equals("Expiry") && value.indexOf("-") == -1) {
                final Calendar cal = Calendar.getInstance();
                cal.add(5, Integer.parseInt(value));
                final Date date = LUtil.getCurrentDate(cal);
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                final String expiryDate = df.format(date).toString();
                ele.setAttribute("Value", expiryDate);
            }
            else {
                ele.setAttribute("Value", value);
            }
        }
        if (limitValue != null) {
            ele.setAttribute("Limit", limitValue);
        }
        return ele;
    }
    
    private Element getElementByName(final Element element, final String tag) {
        final NodeList list = element.getElementsByTagName(tag);
        for (int listLen = list.getLength(), c = 0; c < listLen; ++c) {
            final Node childNode = list.item(c);
            if (childNode.getNodeType() != 3) {
                return (Element)childNode;
            }
        }
        return null;
    }
    
    private ArrayList getNewComponents(final Document document, final String licenseFile) throws Exception {
        final ArrayList newComponents = new ArrayList();
        final InputFileParser parser = new InputFileParser(licenseFile);
        final DataClass dataClass = parser.getDataClass();
        final User user = dataClass.getUserObject(dataClass.getUserList().get(0).toString());
        if (user == null) {
            return newComponents;
        }
        final ArrayList ID = user.getIDs();
        final int idSize = ID.size();
        this.userID = ID.get(0);
        final Details details = dataClass.getDetails(this.userID);
        for (int compCount = 0; compCount < this.oldComponents.size(); ++compCount) {
            final Component comp = this.oldComponents.get(compCount);
            final String name = comp.getName();
            if (details.isComponentPresent(name)) {
                final ArrayList comProp = comp.getProperties();
                if (!comProp.contains("Expiry")) {
                    this.removeComponent(document, name);
                    newComponents.add(comp);
                }
                else {
                    final int index = comProp.indexOf("Expiry");
                    if (comProp.get(index + 1) != null && comProp.get(index + 1).toString().indexOf("-") != -1) {
                        this.removeComponent(document, name);
                        newComponents.add(comp);
                    }
                }
            }
            else {
                newComponents.add(comp);
            }
        }
        return newComponents;
    }
    
    private void removeComponent(final Node node, final String name) {
        if (node.getNodeName().equals("Component")) {
            final NamedNodeMap list = node.getAttributes();
            if (list == null) {
                return;
            }
            for (int length = list.getLength(), j = 0; j < length; ++j) {
                final Attr nextAttr = (Attr)list.item(j);
                final String attrValue = nextAttr.getValue();
                if (attrValue != null && attrValue.equals(name)) {
                    node.getParentNode().removeChild(node);
                }
            }
        }
        else {
            final NodeList list2 = node.getChildNodes();
            for (int i = 0; i < list2.getLength(); ++i) {
                this.removeComponent(list2.item(i), name);
            }
        }
    }
    
    private void UpdateKey(final Document document, final String licenseFile) throws Exception {
        final InputFileParser parser = new InputFileParser(licenseFile);
        final DataClass dataClass = parser.getDataClass();
        final Element compElement = this.getElementByName(this.root, "LicenseKey");
        final String encodedKey = Encode.getFinalKey(dataClass.getWholeKeyBuffer());
        final Text text = document.createTextNode(encodedKey);
        compElement.replaceChild(text, compElement.getFirstChild());
    }
    
    private void createElement(final Document document) {
        this.compElement = this.getElementByName(this.root, "LicenseeDetails");
        if (this.components != null) {
            for (int compSize = this.components.size(), i = 0; i < compSize; ++i) {
                final Element ele = document.createElement("Component");
                final Component comp = this.components.get(i);
                final String compName = comp.getName();
                ele.setAttribute("Name", compName);
                final ArrayList props = comp.getProperties();
                final ArrayList limitList = comp.getLimitProperties();
                if (props != null) {
                    for (int propSize = props.size(), j = 0; j < propSize; j += 2) {
                        final String propName = props.get(j);
                        final int index = limitList.indexOf(propName);
                        if (j + 1 < propSize) {
                            final String value = props.get(j + 1);
                            String limitValue = null;
                            if (index != -1) {
                                limitValue = limitList.get(index + 1);
                            }
                            final Element propElement = this.createPropertyElement(document, propName, value, limitValue);
                            ele.appendChild(propElement);
                        }
                    }
                }
                this.compElement.appendChild(ele);
            }
        }
    }
    
    static {
        AddOn.add = null;
    }
}
