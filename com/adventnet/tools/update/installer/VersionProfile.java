package com.adventnet.tools.update.installer;

import com.adventnet.tools.update.FeatureVersionComp;
import java.util.StringTokenizer;
import org.w3c.dom.Text;
import java.util.ArrayList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.util.logging.Logger;

public class VersionProfile
{
    private static final Logger LOG;
    private static VersionProfile versionProfile;
    private static Document document;
    public static final int VERSION_NOTPRESENT = 0;
    public static final int CONTEXT_PRESENT = 1;
    public static final int CONTEXT_ADDED = 2;
    public static final int VERSION_ADDED = 3;
    public static final int VERSION_REMOVED = 4;
    public static final int VERSION_PRESENT = 5;
    public static final int CONTEXT_NOTPRESENT = 6;
    
    private VersionProfile() {
    }
    
    public static VersionProfile getInstance() {
        if (VersionProfile.versionProfile == null) {
            VersionProfile.versionProfile = new VersionProfile();
        }
        return VersionProfile.versionProfile;
    }
    
    public void readDocument(final String fileName, final boolean validation, final boolean namespaceAware) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validation);
        factory.setNamespaceAware(namespaceAware);
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            this.setErrorHandler(builder);
            if (new File(fileName).exists()) {
                VersionProfile.document = builder.parse(new File(fileName));
            }
        }
        catch (final Error err) {
            err.printStackTrace();
        }
        catch (final SAXParseException sxe) {
            Exception x = sxe;
            sxe.printStackTrace();
            VersionProfile.LOG.severe("The line number is " + sxe.getLineNumber());
            if (sxe.getException() != null) {
                x = sxe.getException();
                x.printStackTrace();
            }
        }
        catch (final SAXException sxe2) {
            Exception x = sxe2;
            VersionProfile.LOG.severe("The sxe is " + sxe2);
            sxe2.printStackTrace();
            if (sxe2.getException() != null) {
                x = sxe2.getException();
                x.printStackTrace();
            }
        }
        catch (final ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void setErrorHandler(final DocumentBuilder builder) {
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
            }
            
            @Override
            public void error(final SAXParseException e) throws SAXParseException {
                throw e;
            }
            
            @Override
            public void warning(final SAXParseException err) throws SAXParseException {
                ConsoleOut.println("** Warning, line " + err.getLineNumber() + ", uri " + err.getSystemId());
                ConsoleOut.println("   " + err.getMessage());
            }
        });
    }
    
    public Element getRootElement() {
        if (VersionProfile.document == null) {
            return null;
        }
        return VersionProfile.document.getDocumentElement();
    }
    
    public Element getElementByName(final Element element, final String versionName, final String tag) {
        final NodeList list = element.getElementsByTagName(tag);
        for (int listLen = list.getLength(), c = 0; c < listLen; ++c) {
            final Node childNode = list.item(c);
            if (childNode.getNodeType() != 3) {
                final String str = ((Element)childNode).getAttribute("Name");
                if (str.equals(versionName)) {
                    return (Element)childNode;
                }
            }
        }
        return null;
    }
    
    public Element getElementByName(final Element element, final String tag) {
        final NodeList list = element.getElementsByTagName(tag);
        for (int listLen = list.getLength(), c = 0; c < listLen; ++c) {
            final Node childNode = list.item(c);
            if (childNode.getNodeType() != 3) {
                return (Element)childNode;
            }
        }
        return null;
    }
    
    public void updateXmlFile(final String fileName) {
        try {
            final TransformerFactory tFactory = TransformerFactory.newInstance();
            final Transformer transformer = tFactory.newTransformer();
            final Node node = VersionProfile.document.getDocumentElement();
            final DOMSource source = new DOMSource(node);
            final FileOutputStream fos = new FileOutputStream(new File(fileName));
            final StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
        }
        catch (final TransformerConfigurationException tce) {
            ConsoleOut.println("\n** Transformer Factory error");
            ConsoleOut.println("   " + tce.getMessage());
            Throwable x = tce;
            if (tce.getException() != null) {
                x = tce.getException();
            }
            else {
                x.printStackTrace();
            }
        }
        catch (final TransformerException te) {
            ConsoleOut.println("\n** Transformation error");
            ConsoleOut.println("   " + te.getMessage());
            Throwable x = te;
            if (te.getException() != null) {
                x = te.getException();
            }
            else {
                x.printStackTrace();
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    public int addContext(final String versionName, final String contextName, final String fileName, final String[] details) {
        final Element root = this.getRootElement();
        final Element version = this.getElementByName(root, versionName, "VERSION");
        if (version == null) {
            return 0;
        }
        for (int detailLength = details.length, i = 0; i < detailLength; i += 2) {
            final String key1 = details[i];
            String value1 = details[i + 1];
            if (key1.equals("Size")) {
                final String val1 = version.getAttribute(key1);
                final int first = Integer.valueOf(val1);
                final int second = Integer.valueOf(value1);
                value1 = String.valueOf(first + second);
            }
            else {
                version.removeAttribute(key1);
            }
            if (value1 != null) {
                version.setAttribute(key1, value1);
            }
        }
        final Element context = this.getElementByName(version, contextName, "CONTEXT");
        if (context != null) {
            return 1;
        }
        final Element child = VersionProfile.document.createElement("CONTEXT");
        child.setAttribute("Name", contextName);
        version.appendChild(child);
        this.updateXmlFile(fileName);
        return 2;
    }
    
    public int addVersion(final String versionName, final String contextName, final String fileName, final String[] details, final String pt, final String compPatchVersion, final String compPatchOption, final ArrayList compFeatureList) {
        final String patchType = pt;
        final Element root = this.getRootElement();
        final Element version = this.getElementByName(root, versionName, "VERSION");
        if (version == null) {
            if (patchType.equals("FP")) {
                this.updateTheRootElem(root, "FPVersions", versionName);
            }
            else {
                this.updateTheRootElem(root, "Versions", versionName);
            }
            this.updateTheRootElem(root, "AllVersions", versionName);
            final Element elem = this.createNode(VersionProfile.document, contextName, "VERSION", versionName, details, compPatchVersion, compPatchOption, compFeatureList, patchType);
            root.appendChild(elem);
            this.updateXmlFile(fileName);
            return 3;
        }
        return 5;
    }
    
    private void createCompElement(final Document doc, final Element elem, final String compPatchVersion, final String compPatchOption, final ArrayList compFeatureList, final String patchType) {
        if (patchType.equals("SP")) {
            return;
        }
        final Element child = doc.createElement("Compatibility");
        if (compPatchOption != null && compPatchVersion != null) {
            final Element comPat = doc.createElement("compPatchVersion");
            comPat.setAttribute("option", compPatchOption);
            child.appendChild(comPat);
            final Text pcvText = doc.createTextNode(compPatchVersion);
            comPat.appendChild(pcvText);
            child.appendChild(comPat);
        }
        if (compFeatureList != null) {
            for (int size = compFeatureList.size(), i = 0; i < size; i += 3) {
                final String featureName = compFeatureList.get(i);
                final String featureOption = compFeatureList.get(i + 1);
                final String featureVersion = compFeatureList.get(i + 2);
                final Element fpvElem = doc.createElement("compFPVersion");
                fpvElem.setAttribute("name", featureName);
                fpvElem.setAttribute("option", featureOption);
                final Text pcvText2 = doc.createTextNode(featureVersion);
                fpvElem.appendChild(pcvText2);
                child.appendChild(fpvElem);
            }
        }
        elem.appendChild(child);
    }
    
    private void updateTheRootElem(final Element root, final String attributeName, final String versionName) {
        if (root.hasAttribute(attributeName)) {
            String fpattr = root.getAttribute(attributeName);
            if (fpattr.trim().equals("")) {
                fpattr = versionName;
            }
            else {
                fpattr = fpattr + "," + versionName;
            }
            root.removeAttribute(attributeName);
            root.setAttribute(attributeName, fpattr);
        }
        else if (attributeName.equals("FPVersions")) {
            root.setAttribute(attributeName, versionName);
        }
        else if (attributeName.equals("Versions")) {
            root.setAttribute(attributeName, versionName);
        }
        else {
            String oldVer = root.getAttribute("Versions");
            final String fver = root.getAttribute("FPVersions");
            if (!fver.trim().equals("")) {
                oldVer = oldVer + "," + fver;
            }
            root.setAttribute(attributeName, oldVer);
        }
    }
    
    public int removeVersion(final String versionName, final String fileName, final String type) {
        final Element root = this.getRootElement();
        final Element version = this.getElementByName(root, versionName, "VERSION");
        if (version != null) {
            this.updateFile(root, versionName, type, version, fileName);
            final boolean bool = root.hasAttribute("AllVersions");
            if (bool) {
                final String[] allVersions = this.getArray(root, versionName, "AllVersions");
                final String allattr = this.getVersionsToAdd(allVersions);
                root.removeAttribute("AllVersions");
                root.setAttribute("AllVersions", allattr);
                root.setAttribute("lastReverted", versionName);
                this.updateXmlFile(fileName);
            }
            return 4;
        }
        return 0;
    }
    
    private void updateFile(final Element root, final String versionName, final String type, final Element elemToRemove, final String fileName) {
        String[] versionArray = null;
        String attributeName = null;
        if (type.equals("FP")) {
            attributeName = "FPVersions";
            versionArray = this.getArray(root, versionName, attributeName);
        }
        else {
            attributeName = "Versions";
            versionArray = this.getArray(root, versionName, attributeName);
        }
        final String attr = this.getVersionsToAdd(versionArray);
        if (attr.equals("")) {
            root.removeAttribute(attributeName);
            root.setAttribute(attributeName, attr);
            root.removeChild(elemToRemove);
            this.updateXmlFile(fileName);
        }
        else {
            root.removeAttribute(attributeName);
            root.setAttribute(attributeName, attr);
            root.removeChild(elemToRemove);
            this.updateXmlFile(fileName);
        }
    }
    
    private String[] getArray(final Element root, final String versionName, final String attrName) {
        final String attribute = root.getAttribute(attrName);
        final StringTokenizer st = new StringTokenizer(attribute, ",");
        final String[] versionArray = new String[st.countTokens() - 1];
        int i = 0;
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (!token.equals(versionName)) {
                versionArray[i] = token;
                ++i;
            }
        }
        return versionArray;
    }
    
    public int isContextPresent(final String versionName, final String contextName, final String fileName) {
        final Element root = this.getRootElement();
        final Element version = this.getElementByName(root, versionName, "VERSION");
        if (version == null) {
            return 0;
        }
        final Element context = this.getElementByName(version, contextName, "CONTEXT");
        if (context != null) {
            return 1;
        }
        return 6;
    }
    
    public int isVersionPresent(final String versionName, final String fileName) {
        final Element root = this.getRootElement();
        final Element version = this.getElementByName(root, versionName, "VERSION");
        if (version == null) {
            return 0;
        }
        return 5;
    }
    
    public String[] getTheVersions() {
        final Element root = this.getRootElement();
        if (root == null) {
            return null;
        }
        final String attribute = root.getAttribute("Versions");
        final StringTokenizer st = new StringTokenizer(attribute, ",");
        final String[] versionArray = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            versionArray[i] = token;
            ++i;
        }
        if (versionArray.length == 0) {
            return null;
        }
        return versionArray;
    }
    
    public String getLastRevertedVersion() {
        final Element root = this.getRootElement();
        if (root != null) {
            return root.getAttribute("lastReverted");
        }
        return null;
    }
    
    public String[] getAllVersions() {
        final Element root = this.getRootElement();
        if (root == null) {
            return null;
        }
        String attribute = root.getAttribute("AllVersions");
        if (attribute.trim().equals("")) {
            attribute = root.getAttribute("Versions");
        }
        final StringTokenizer st = new StringTokenizer(attribute, ",");
        final String[] versionArray = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            versionArray[i] = token;
            ++i;
        }
        if (versionArray.length == 0) {
            return null;
        }
        return versionArray;
    }
    
    public String[] getTheContext(final String versionName) {
        final Element root = this.getRootElement();
        if (root != null) {
            final Element version = this.getElementByName(root, versionName, "VERSION");
            if (version != null) {
                final NodeList list = version.getElementsByTagName("CONTEXT");
                final int listLen = list.getLength();
                final String[] versionArray = new String[listLen];
                for (int c = 0; c < listLen; ++c) {
                    final Node childNode = list.item(c);
                    if (childNode.getNodeType() != 3) {
                        final String str = ((Element)childNode).getAttribute("Name");
                        versionArray[c] = str;
                    }
                }
                return versionArray;
            }
        }
        return null;
    }
    
    public String getTheAdditionalDetail(final String versionName, final String option) {
        String value = null;
        final Element root = this.getRootElement();
        final Element version = this.getElementByName(root, versionName, "VERSION");
        if (version != null) {
            value = version.getAttribute(option);
        }
        return value;
    }
    
    public void createDocument(final String fileName, final String versionName, final String contextName, final String[] details, final String pt, final String compPatchVersion, final String compPatchOption, final ArrayList compFeatureList) {
        final String patchType = pt;
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            this.setErrorHandler(builder);
            final File out = new File(fileName);
            if (!out.exists()) {
                out.createNewFile();
            }
            VersionProfile.document = builder.newDocument();
            final Element glob = VersionProfile.document.createElement("VERSION-DETAILS");
            final String[] verName = { versionName };
            final String string = this.getVersionsToAdd(verName);
            if (patchType.equals("FP")) {
                glob.setAttribute("FPVersions", string);
            }
            else {
                glob.setAttribute("Versions", string);
            }
            glob.setAttribute("AllVersions", string);
            glob.appendChild(this.createNode(VersionProfile.document, contextName, "VERSION", versionName, details, compPatchVersion, compPatchOption, compFeatureList, patchType));
            VersionProfile.document.appendChild(glob);
            this.updateXmlFile(fileName);
        }
        catch (final ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private String getVersionsToAdd(final String[] contextList) {
        String string = "";
        for (int length = contextList.length, i = 0; i < length; ++i) {
            if (i == length - 1) {
                string += contextList[i];
            }
            else {
                string = string + contextList[i] + ",";
            }
        }
        return string;
    }
    
    private Element createNode(final Document doc, final String value, final String name, final String key, final String[] details, final String compPatchVersion, final String compPatchOption, final ArrayList compFeatureList, final String patchType) {
        final Element childNode = doc.createElement(name);
        childNode.setAttribute("Name", key);
        for (int detailLength = details.length, i = 0; i < detailLength; i += 2) {
            final String key2 = details[i];
            final String value2 = details[i + 1];
            if (value2 != null) {
                childNode.setAttribute(key2, value2);
            }
        }
        if (value != null || !value.equals("")) {
            final Element child = doc.createElement("CONTEXT");
            child.setAttribute("Name", value);
            childNode.appendChild(child);
        }
        this.createCompElement(doc, childNode, compPatchVersion, compPatchOption, compFeatureList, patchType);
        return childNode;
    }
    
    public String[] getTheFPVersions() {
        final Element root = this.getRootElement();
        if (root == null) {
            return null;
        }
        final String attribute = root.getAttribute("FPVersions");
        final StringTokenizer st = new StringTokenizer(attribute, ",");
        final String[] versionArray = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            versionArray[i] = token;
            ++i;
        }
        if (versionArray.length == 0) {
            return null;
        }
        return versionArray;
    }
    
    public FeatureVersionComp getVersionCompatibility(final String versionName) {
        final Element root = this.getRootElement();
        if (root == null) {
            return null;
        }
        final Element version = this.getElementByName(root, versionName, "VERSION");
        if (version == null) {
            return null;
        }
        final Node subChildNode = this.getElementByName(version, "Compatibility");
        final NodeList patchchildList = subChildNode.getChildNodes();
        final int plength = patchchildList.getLength();
        int pcount = 0;
        final String patchChild = null;
        final FeatureVersionComp fpComp = new FeatureVersionComp();
        while (pcount < plength) {
            final Node child = patchchildList.item(pcount);
            if (child.getNodeType() != 1) {
                ++pcount;
            }
            else {
                if (!child.getNodeName().equals("compFPVersion") && !child.getNodeName().equals("compPatchVersion")) {
                    ConsoleOut.println("INVALID NODE:" + child.getNodeName());
                    break;
                }
                if (child.getNodeName().equals("compFPVersion")) {
                    final String feature = ((Element)child).getAttribute("name");
                    final String featureOption = ((Element)child).getAttribute("option");
                    final String featureValue = child.getFirstChild().getNodeValue();
                    fpComp.addVersion(feature, featureOption, featureValue);
                }
                if (child.getNodeName().equals("compPatchVersion")) {
                    final String option = ((Element)child).getAttribute("option");
                    final String value = child.getFirstChild().getNodeValue();
                    fpComp.setCompPatchOption(option);
                    fpComp.setCompPatchVersion(value);
                }
                ++pcount;
            }
        }
        return fpComp;
    }
    
    static {
        LOG = Logger.getLogger(VersionProfile.class.getName());
        VersionProfile.versionProfile = null;
        VersionProfile.document = null;
    }
}
