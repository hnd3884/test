package com.adventnet.tools.update;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Set;
import java.util.Enumeration;
import java.util.Iterator;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.apache.crimson.tree.XmlDocument;
import java.util.List;
import com.adventnet.tools.update.installer.CustomPatchStateTracker;
import com.adventnet.tools.update.installer.CustomPatchValidator;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Vector;
import java.util.HashMap;

public class XmlData
{
    private String productName;
    private String productVersion;
    private String patchVersion;
    private HashMap jarCompatible;
    private String patchDescription;
    private String patchReadme;
    private String patchReadmeUrl;
    private Vector fileGroups;
    private Object[] preInstallClasses;
    private Object[] postInstallClasses;
    private Vector dependencyContext;
    private Hashtable contextTable;
    private Properties generalProps;
    private String displayName;
    private String featureName;
    private String patchType;
    private String patchContentType;
    private ArrayList featureComp;
    private ArrayList newFilesGroup;
    private ArrayList zipFilesGroup;
    private Node localeReadmeElement;
    private int readMeType;
    private CustomPatchValidator cpv;
    private CustomPatchStateTracker cpst;
    private String resourceFile;
    private boolean autoClose;
    private long autoCloseDelay;
    private String hotSwapHandlerClassName;
    private List<String> hotSwapHandlerdependentClassesList;
    private List<String> cleanUpFiles;
    
    public XmlData() {
        this.productName = null;
        this.productVersion = null;
        this.patchVersion = null;
        this.jarCompatible = null;
        this.patchDescription = null;
        this.patchReadme = null;
        this.patchReadmeUrl = null;
        this.fileGroups = null;
        this.preInstallClasses = null;
        this.postInstallClasses = null;
        this.dependencyContext = null;
        this.contextTable = null;
        this.generalProps = null;
        this.displayName = null;
        this.featureName = null;
        this.patchType = null;
        this.patchContentType = null;
        this.featureComp = null;
        this.newFilesGroup = null;
        this.zipFilesGroup = null;
        this.localeReadmeElement = null;
        this.readMeType = 1;
        this.cpv = null;
        this.cpst = null;
        this.resourceFile = null;
        this.autoClose = false;
        this.autoCloseDelay = 2000L;
        this.hotSwapHandlerClassName = null;
        this.hotSwapHandlerdependentClassesList = null;
        this.cleanUpFiles = new ArrayList<String>();
        this.fileGroups = new Vector();
        this.preInstallClasses = new Object[0];
        this.postInstallClasses = new Object[0];
        this.contextTable = new Hashtable();
        this.dependencyContext = new Vector();
        this.newFilesGroup = new ArrayList();
        this.zipFilesGroup = new ArrayList();
    }
    
    public void setLocaleSpecificReadme(final Node localeElement) {
        this.localeReadmeElement = localeElement;
    }
    
    public void setPatchDescription(final String desc) {
        this.patchDescription = desc;
    }
    
    public void setProductName(final String name) {
        this.productName = name;
    }
    
    public void setProductVersion(final String version) {
        this.productVersion = version;
    }
    
    public void setPatchVersion(final String version) {
        this.patchVersion = version;
    }
    
    public void setJarCompatible(final HashMap jar) {
        this.jarCompatible = jar;
    }
    
    public void setPatchReadme(final String filename) {
        this.patchReadme = filename;
    }
    
    public void setPatchReadmeUrl(final String readMeUrl) {
        this.patchReadmeUrl = readMeUrl;
    }
    
    public void setReadMeType(final int patchReadMeType) {
        this.readMeType = patchReadMeType;
    }
    
    public int getReadMeType() {
        return this.readMeType;
    }
    
    public String getReadMe(final int type) {
        if (type == 3) {
            return this.patchReadmeUrl;
        }
        return this.patchReadme;
    }
    
    public void setFileGroups(final FileGroup fg) {
        this.fileGroups.addElement(fg);
    }
    
    public String getPatchDescription() {
        return this.patchDescription;
    }
    
    public String getPatchReadme() {
        return this.patchReadme;
    }
    
    public String getPatchReadmeUrl() {
        return this.patchReadmeUrl;
    }
    
    public Vector getFileGroups() {
        return this.fileGroups;
    }
    
    public ArrayList getNewFileGroup() {
        return this.newFilesGroup;
    }
    
    public void setNewFileGroup(final NewFileGroup nfg) {
        this.newFilesGroup.add(nfg);
    }
    
    public ArrayList getZipFileGroup() {
        return this.zipFilesGroup;
    }
    
    public void setZipFileGroup(final ZipFileGroup zfg) {
        this.zipFilesGroup.add(zfg);
    }
    
    public Object[] getPreInstallClasses() {
        return this.preInstallClasses;
    }
    
    public void setPreInstallClasses(final Object[] obj) {
        this.preInstallClasses = obj;
    }
    
    public Object[] getPostInstallClasses() {
        return this.postInstallClasses;
    }
    
    public void setPostInstallClasses(final Object[] obj) {
        this.postInstallClasses = obj;
    }
    
    public void setCustomPatchValidator(final CustomPatchValidator cpv) {
        this.cpv = cpv;
    }
    
    public void setCustomPatchStateTracker(final CustomPatchStateTracker cpst) {
        this.cpst = cpst;
    }
    
    @Deprecated
    public void setHotSwapListener(final String hotSwapHandlerClassName) {
        this.hotSwapHandlerClassName = hotSwapHandlerClassName;
    }
    
    public void setHotSwapHandler(final String hotSwapHandlerClassName) {
        this.hotSwapHandlerClassName = hotSwapHandlerClassName;
    }
    
    @Deprecated
    public void setHotSwapListenerDependents(final List<String> hotSwapListenerdependentClassesList) {
        this.hotSwapHandlerdependentClassesList = hotSwapListenerdependentClassesList;
    }
    
    public void setHotSwapHandlerDependents(final List<String> hotSwapHandlerdependentClassesList) {
        this.hotSwapHandlerdependentClassesList = hotSwapHandlerdependentClassesList;
    }
    
    public void setResourceFile(final String resourceFile) {
        this.resourceFile = resourceFile;
    }
    
    public void setAutoCloseDelay(final long autoCloseDelay) {
        this.autoCloseDelay = autoCloseDelay;
    }
    
    public void setAutoClose(final boolean autoClose) {
        this.autoClose = autoClose;
    }
    
    public Vector getDependencyContext() {
        return this.dependencyContext;
    }
    
    public String getPatchVersion() {
        return this.patchVersion;
    }
    
    public String getProductVersion() {
        return this.productVersion;
    }
    
    public CustomPatchValidator getCustomPatchValidator() {
        return this.cpv;
    }
    
    public CustomPatchStateTracker getCustomPatchStateTracker() {
        return this.cpst;
    }
    
    @Deprecated
    public String getHotSwapListener() {
        return this.hotSwapHandlerClassName;
    }
    
    @Deprecated
    public List<String> getHotSwapListenerDependents() {
        return this.hotSwapHandlerdependentClassesList;
    }
    
    public String getHotSwapHandler() {
        return this.hotSwapHandlerClassName;
    }
    
    public List<String> getHotSwapHandlerDependents() {
        return this.hotSwapHandlerdependentClassesList;
    }
    
    public String getResourceFile() {
        return this.resourceFile;
    }
    
    public boolean getAutoClose() {
        return this.autoClose;
    }
    
    public long getAutoCloseDelay() {
        return this.autoCloseDelay;
    }
    
    public HashMap getJarCompatible() {
        return this.jarCompatible;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public Hashtable getContextTable() {
        return this.contextTable;
    }
    
    public void setDependencyContext(final Vector v) {
        this.dependencyContext = v;
    }
    
    public void setContextTable(final Hashtable table) {
        this.contextTable = table;
    }
    
    public XmlDocument createDocument() {
        final XmlDocument xmlDoc = new XmlDocument();
        final Element root = xmlDoc.createElement("xmlData");
        final Element glob = xmlDoc.createElement("patchDetails");
        final Element parse = this.createTextNode(xmlDoc, "patchDescription", this.patchDescription);
        final Element name = this.createTextNode(xmlDoc, "productName", this.productName);
        final Element version = this.createTextNode(xmlDoc, "productVersion", this.productVersion);
        final Element disName = this.createTextNode(xmlDoc, "displayName", this.displayName);
        final Element pType = this.createTextNode(xmlDoc, "patchType", this.patchType);
        final Element feaType = this.createTextNode(xmlDoc, "feature", this.featureName);
        final Element pCType = this.createTextNode(xmlDoc, "patchContentType", this.patchContentType);
        final Element patchVer = this.createTextNode(xmlDoc, "patchVersion", this.patchVersion);
        Element patchRea = null;
        if (this.patchReadmeUrl != null) {
            patchRea = this.createTextNode(xmlDoc, "patchReadmeUrl", this.patchReadmeUrl);
        }
        else {
            patchRea = this.createTextNode(xmlDoc, "patchReadme", this.patchReadme);
        }
        final Element fc = this.createFeatureNode(xmlDoc, this.featureComp, "productCompatibility");
        final Element jarComp = this.createJarNode(xmlDoc, this.jarCompatible, "jarCompatibility");
        final Element update = this.createUpdateNode(xmlDoc, this.contextTable);
        final Element props = this.createPropertiesNode(xmlDoc, this.generalProps);
        this.append(glob, name);
        this.append(glob, version);
        this.append(glob, disName);
        this.append(glob, pType);
        this.append(glob, feaType);
        this.append(glob, pCType);
        this.append(glob, patchVer);
        this.append(glob, patchRea);
        this.append(glob, parse);
        if (this.readMeType == 2) {
            this.append(glob, this.createTextNode(xmlDoc, "patchReadmeFileUrl", "true"));
        }
        this.append(glob, fc);
        if (this.localeReadmeElement != null) {
            final NodeList nl = this.localeReadmeElement.getChildNodes();
            final Element lre = xmlDoc.createElement("LOCALEREADME");
            for (int li = 0; li < nl.getLength(); ++li) {
                final Node node = nl.item(li);
                if (node.getNodeType() == 1) {
                    final Element cEle = xmlDoc.createElement(node.getNodeName());
                    final Text textNod = xmlDoc.createTextNode(node.getFirstChild().getNodeValue());
                    cEle.appendChild(textNod);
                    lre.appendChild(cEle);
                }
            }
            this.append(glob, lre);
        }
        this.append(glob, jarComp);
        if (this.cpv != null) {
            this.append(glob, this.createCustomPatchValidator(xmlDoc, "customPatchValidator"));
        }
        if (this.cpst != null) {
            this.append(glob, this.createPatchStateTrackerElement(xmlDoc));
        }
        if (this.hotSwapHandlerClassName != null) {
            this.append(glob, this.createHotSwapHandlerElement(xmlDoc));
        }
        if (this.resourceFile != null) {
            this.append(glob, this.createTextNode(xmlDoc, "resourceFile", this.resourceFile));
        }
        if (this.autoClose) {
            this.append(glob, this.createTextNode(xmlDoc, "autoCloseOnSuccess", "true"));
            if (this.autoCloseDelay > 0L) {
                this.append(glob, this.createTextNode(xmlDoc, "autoCloseDelay", String.valueOf(this.autoCloseDelay)));
            }
        }
        this.append(root, glob);
        this.append(root, update);
        this.append(root, props);
        this.append(root, this.createCleanUpFiles(xmlDoc));
        xmlDoc.appendChild((Node)root);
        return xmlDoc;
    }
    
    public List<String> getCleanUpFiles() {
        return this.cleanUpFiles;
    }
    
    public void setCleanUpFiles(final List<String> cleanUpFiles) {
        this.cleanUpFiles = cleanUpFiles;
    }
    
    private void append(final Element element, final Element toAppend) {
        if (toAppend != null) {
            element.appendChild(toAppend);
        }
    }
    
    private Element createCustomPatchValidator(final XmlDocument xmlDoc, final String name) {
        final Element patchValiEle = xmlDoc.createElement(name);
        patchValiEle.setAttribute("name", this.cpv.getCustomPatchValidatorNode().getAttributes().getNamedItem("name").getNodeValue());
        final ArrayList<String> dependentList = this.cpv.getDependentClassesList();
        for (final String dependentJar : dependentList) {
            final Element depenClassPathElem = this.createTextNode(xmlDoc, "dependentClassPath", dependentJar);
            patchValiEle.appendChild(depenClassPathElem);
        }
        final ArrayList<String> classPathList = this.cpv.getClassPathList();
        for (final String classPathJar : classPathList) {
            final Element classPathElement = this.createTextNode(xmlDoc, "classPath", classPathJar);
            patchValiEle.appendChild(classPathElement);
        }
        final Properties prop = this.cpv.getProperties();
        if (prop != null && !prop.isEmpty()) {
            final Enumeration en = prop.keys();
            while (en.hasMoreElements()) {
                final String key = en.nextElement();
                final String value = prop.getProperty(key);
                final Element propElement = xmlDoc.createElement("property");
                propElement.setAttribute("name", key);
                propElement.setAttribute("value", prop.getProperty(key));
                patchValiEle.appendChild(propElement);
            }
        }
        return patchValiEle;
    }
    
    private Element createCleanUpFiles(final XmlDocument xmlDoc) {
        final Element tempFileCleanUp = xmlDoc.createElement("CleanUp");
        for (final String tempFile : this.cleanUpFiles) {
            final Element child = xmlDoc.createElement("file");
            child.setAttribute("path", tempFile);
            tempFileCleanUp.appendChild(child);
        }
        return tempFileCleanUp;
    }
    
    private Element createPatchStateTrackerElement(final XmlDocument xmlDoc) {
        final Element trackerElement = xmlDoc.createElement("patchStateTracker");
        trackerElement.setAttribute("name", this.cpst.getTrackerImplementationClassName());
        final List<String> dependentClassesList = this.cpst.getDependentClassesList();
        for (final String dependentClass : dependentClassesList) {
            final Element depenClassPathElem = this.createTextNode(xmlDoc, "dependentClassPath", dependentClass);
            trackerElement.appendChild(depenClassPathElem);
        }
        final Properties prop = this.cpst.getProperties();
        if (prop != null && !prop.isEmpty()) {
            final Enumeration en = prop.keys();
            while (en.hasMoreElements()) {
                final String key = en.nextElement();
                final String value = prop.getProperty(key);
                final Element propElement = xmlDoc.createElement("property");
                propElement.setAttribute("name", key);
                propElement.setAttribute("value", value);
                trackerElement.appendChild(propElement);
            }
        }
        return trackerElement;
    }
    
    private Element createHotSwapHandlerElement(final XmlDocument xmlDoc) {
        final Element listenerElement = xmlDoc.createElement("hotSwapHandler");
        listenerElement.setAttribute("name", this.hotSwapHandlerClassName);
        if (this.hotSwapHandlerdependentClassesList != null) {
            for (final String dependentClass : this.hotSwapHandlerdependentClassesList) {
                final Element depenClassPathElem = this.createTextNode(xmlDoc, "dependentClassPath", dependentClass);
                listenerElement.appendChild(depenClassPathElem);
            }
        }
        return listenerElement;
    }
    
    private Element createTextNode(final XmlDocument xmlDoc, final String name, final String key) {
        Element parse = null;
        if (key != null) {
            parse = xmlDoc.createElement(name);
            final Text parseText = xmlDoc.createTextNode(key);
            parse.appendChild(parseText);
        }
        return parse;
    }
    
    private Element createNode(final XmlDocument xmlDoc, final HashMap hash, final String name) {
        final Element pcv = xmlDoc.createElement(name);
        if (hash != null) {
            final ArrayList versionList = this.getVersions(hash);
            final ArrayList products = this.getProducts(hash);
            for (int i = 0; i < products.size(); ++i) {
                final Element pcvChild = xmlDoc.createElement("compProductName");
                final String productName = products.get(i);
                pcvChild.setAttribute("name", productName);
                for (int j = 0; j < versionList.size(); ++j) {
                    final String ver = versionList.get(j);
                    final String key = productName + ":" + ver;
                    if (hash.containsKey(key)) {
                        final HashMap list = hash.get(key);
                        final Element pcvVersion = xmlDoc.createElement("version");
                        pcvVersion.setAttribute("compProductVersion", ver);
                        pcvChild.appendChild(pcvVersion);
                        final Set enum1 = list.keySet();
                        for (final Object pcVer : enum1.toArray()) {
                            final Object option = list.get(pcVer);
                            final Element pcVersion = xmlDoc.createElement("compPatchVersion");
                            pcVersion.setAttribute("option", (String)option);
                            final Text pcvText = xmlDoc.createTextNode((String)pcVer);
                            pcVersion.appendChild(pcvText);
                            pcvVersion.appendChild(pcVersion);
                        }
                    }
                }
                pcv.appendChild(pcvChild);
            }
        }
        return pcv;
    }
    
    public ArrayList getProducts(final HashMap hash) {
        final Set enum1 = hash.keySet();
        final Object[] obj = enum1.toArray();
        final int count = obj.length;
        final ArrayList versionList = new ArrayList();
        for (int i = 0; i < count; ++i) {
            final String pcvproduct = (String)obj[i];
            final int index = pcvproduct.indexOf(":");
            final String productName = pcvproduct.substring(0, index);
            if (!versionList.contains(productName)) {
                versionList.add(productName);
            }
        }
        return versionList;
    }
    
    public ArrayList getVersions(final HashMap hash) {
        final Set enum1 = hash.keySet();
        final Object[] obj = enum1.toArray();
        final int count = obj.length;
        final ArrayList versionList = new ArrayList();
        for (int i = 0; i < count; ++i) {
            final String pcvproduct = (String)obj[i];
            final int index = pcvproduct.indexOf(":");
            final String version = pcvproduct.substring(pcvproduct.lastIndexOf(":") + 1);
            versionList.add(version);
        }
        return versionList;
    }
    
    private Element createUpdateNode(final XmlDocument xmlDoc, final Hashtable hash) {
        FileGroup fGroup = null;
        final Element mainContext = xmlDoc.createElement("context");
        final Object[] keyArray = hash.keySet().toArray();
        int l;
        for (int arrayLength = l = keyArray.length; l > 0; --l) {
            String contextName = null;
            contextName = (String)keyArray[l - 1];
            final UpdateData context = hash.get(contextName);
            String contextReadme = null;
            contextReadme = context.getContextReadme();
            final String contextType = context.getContextType();
            final String contextDesc = context.getContextDescription();
            final Element updation = xmlDoc.createElement("updation");
            updation.setAttribute("context", contextName);
            updation.setAttribute("readme", contextReadme);
            updation.setAttribute("type", contextType);
            updation.setAttribute("details", contextDesc);
            final Element dependent = this.createDependencyNode(xmlDoc, context.getDependencyVector());
            updation.appendChild(dependent);
            final Element elem = this.createPrePostNode(xmlDoc, context.getPreInstallArray(), "preInstall");
            updation.appendChild(elem);
            final Element addElem = xmlDoc.createElement("add");
            final List<String> modifiedJarNames = new ArrayList<String>();
            final Vector conVec = context.getContextVector();
            final int vecSize = conVec.size();
            for (int i = 0; i < vecSize; ++i) {
                fGroup = conVec.elementAt(i);
                String jarName = null;
                final int j = 0;
                if (j < fGroup.getJarNameVector().size()) {
                    jarName = fGroup.getJarNameVector().elementAt(j);
                }
                if (jarName != null) {
                    final Element fgElem = xmlDoc.createElement("fileGroup");
                    final Element ttuElem = xmlDoc.createElement("targetToUpdate");
                    final Element jarElem = this.createTextNode(xmlDoc, "jarName", jarName);
                    ttuElem.appendChild(jarElem);
                    fgElem.appendChild(ttuElem);
                    final Element ftuElem = xmlDoc.createElement("filesToUpdate");
                    for (int k = 0; k < fGroup.getFileNameVector().size(); ++k) {
                        final String fileName = fGroup.getFileNameVector().elementAt(k);
                        final Element fileElem = this.createTextNode(xmlDoc, "fileName", fileName);
                        ftuElem.appendChild(fileElem);
                    }
                    fgElem.appendChild(ftuElem);
                    if (context.getJarEntriesMarkedForDelete(jarName) != null) {
                        final Element delElem = xmlDoc.createElement("filesToDelete");
                        for (final String entry : context.getJarEntriesMarkedForDelete(jarName)) {
                            final Element fileElem2 = this.createTextNode(xmlDoc, "fileName", entry);
                            delElem.appendChild(fileElem2);
                        }
                        fgElem.appendChild(delElem);
                    }
                    modifiedJarNames.add(jarName);
                    addElem.appendChild(fgElem);
                }
            }
            for (int i = 0; i < vecSize; ++i) {
                fGroup = conVec.elementAt(i);
                String jarName = null;
                final int j = 0;
                if (j < fGroup.getJarNameVector().size()) {
                    jarName = fGroup.getJarNameVector().elementAt(j);
                }
                if (jarName == null) {
                    final Element fgElem = xmlDoc.createElement("fileGroup");
                    final Element ttuElem = xmlDoc.createElement("targetToUpdate");
                    fgElem.appendChild(ttuElem);
                    final Element ftuElem2 = xmlDoc.createElement("filesToUpdate");
                    for (int m = 0; m < fGroup.getFileNameVector().size(); ++m) {
                        final String fileName2 = fGroup.getFileNameVector().elementAt(m);
                        final Element fileElem3 = this.createTextNode(xmlDoc, "fileName", fileName2);
                        ftuElem2.appendChild(fileElem3);
                    }
                    fgElem.appendChild(ftuElem2);
                    addElem.appendChild(fgElem);
                }
            }
            final ArrayList newList = context.getNewFileGroup();
            final Element newElem = this.getNewFileElement(xmlDoc, newList);
            if (newElem != null) {
                addElem.appendChild(newElem);
            }
            updation.appendChild(addElem);
            final ArrayList zipList = context.getZipFileGroup();
            final Element zipElem = this.getZipFileElement(xmlDoc, zipList);
            if (zipElem != null) {
                addElem.appendChild(zipElem);
            }
            updation.appendChild(addElem);
            final Set<String> deletedFiles = context.getFilesMarkedForDelete();
            final Set<String> jarNamesContainingDeleteEntries = context.getJarNamesContainingDeleteEntries();
            if (!deletedFiles.isEmpty() || !jarNamesContainingDeleteEntries.isEmpty()) {
                final Element deleteElem = xmlDoc.createElement("delete");
                for (final String deletedFile : deletedFiles) {
                    final Element delFileElem = this.createTextNode(xmlDoc, "fileName", deletedFile);
                    deleteElem.appendChild(delFileElem);
                }
                for (final String jarName2 : jarNamesContainingDeleteEntries) {
                    if (modifiedJarNames.contains(jarName2)) {
                        continue;
                    }
                    final Element jarEntriesElem = xmlDoc.createElement("jarEntries");
                    jarEntriesElem.setAttribute("jarName", jarName2);
                    for (final String deletedJarEntry : context.getJarEntriesMarkedForDelete(jarName2)) {
                        final Element entryElem = this.createTextNode(xmlDoc, "jarEntry", deletedJarEntry);
                        jarEntriesElem.appendChild(entryElem);
                    }
                    deleteElem.appendChild(jarEntriesElem);
                }
                updation.appendChild(deleteElem);
            }
            final Element postElem = this.createPrePostNode(xmlDoc, context.getPostInstallArray(), "postInstall");
            updation.appendChild(postElem);
            mainContext.appendChild(updation);
        }
        return mainContext;
    }
    
    private Element getNewFileElement(final XmlDocument xmlDoc, final ArrayList newList) {
        if (newList == null || newList.isEmpty()) {
            return null;
        }
        final Element newElem = xmlDoc.createElement("newFiles");
        for (int newSize = newList.size(), i = 0; i < newSize; ++i) {
            final NewFileGroup nfg = newList.get(i);
            final String versionName = nfg.getVersionName();
            final Element verElem = xmlDoc.createElement("version");
            verElem.setAttribute("name", versionName);
            newElem.appendChild(verElem);
            final ArrayList filesList = nfg.getFilesList();
            for (int len = filesList.size(), j = 0; j < len; ++j) {
                final String fileName = filesList.get(j);
                final Element fElem = this.createTextNode(xmlDoc, "fileName", fileName);
                verElem.appendChild(fElem);
            }
        }
        return newElem;
    }
    
    private Element getZipFileElement(final XmlDocument xmlDoc, final ArrayList zipList) {
        if (zipList == null || zipList.isEmpty()) {
            return null;
        }
        final Element zipElem = xmlDoc.createElement("zipFiles");
        for (int zipSize = zipList.size(), i = 0; i < zipSize; ++i) {
            final ZipFileGroup zfg = zipList.get(i);
            final String zipName = zfg.getZipName();
            final Element archiveElem = xmlDoc.createElement("archive");
            archiveElem.setAttribute("name", zipName);
            final ArrayList filesList = zfg.getFilesList();
            for (int len = filesList.size(), j = 0; j < len; ++j) {
                final String fileName = filesList.get(j);
                final Element fElem = this.createTextNode(xmlDoc, "fileName", fileName);
                archiveElem.appendChild(fElem);
                zipElem.appendChild(archiveElem);
            }
        }
        return zipElem;
    }
    
    private Element createJarNode(final XmlDocument xmlDoc, final HashMap v, final String s) {
        final Element elem = xmlDoc.createElement(s);
        final HashMap hash = v;
        if (hash != null) {
            final Set enum1 = hash.keySet();
            for (final Object key : enum1.toArray()) {
                final Element pcvChild = xmlDoc.createElement("compJarName");
                pcvChild.setAttribute("name", (String)key);
                final HashMap value = hash.get(key);
                final Set valueSet = value.keySet();
                for (final Object obKey : valueSet.toArray()) {
                    final Object obValue = value.get(obKey);
                    final Element pcvVersion = xmlDoc.createElement("property");
                    pcvVersion.setAttribute("name", (String)obKey);
                    pcvVersion.setAttribute("value", (String)obValue);
                    pcvChild.appendChild(pcvVersion);
                }
                elem.appendChild(pcvChild);
            }
        }
        return elem;
    }
    
    private Element createDependencyNode(final XmlDocument xmlDoc, final Vector depenVec) {
        final Element elem = xmlDoc.createElement("dependency");
        for (int size = depenVec.size(), i = 0; i < size; ++i) {
            final String conName = depenVec.elementAt(i);
            final Element conElem = this.createTextNode(xmlDoc, "on", conName);
            elem.appendChild(conElem);
        }
        return elem;
    }
    
    private Element createPrePostNode(final XmlDocument xmlDoc, final Object[] array, final String s) {
        final Element elem = xmlDoc.createElement(s);
        final Object[] obj = array;
        final int length = obj.length;
        if (length != 0) {
            for (int j = 0; j < length; j += 4) {
                final Object key = obj[j];
                final Element pcvChild = xmlDoc.createElement("className");
                pcvChild.setAttribute("name", (String)key);
                final Object depen = obj[j + 1];
                if (depen != null) {
                    final ArrayList depenList = (ArrayList)depen;
                    final int size = depenList.size();
                    if (size == 0) {
                        break;
                    }
                    final Element depenElem = xmlDoc.createElement("dependentClasses");
                    for (int k = 0; k < size; ++k) {
                        final Element depenChild = xmlDoc.createElement("dependentClassName");
                        depenChild.setAttribute("name", depenList.get(k));
                        depenElem.appendChild(depenChild);
                    }
                    pcvChild.appendChild(depenElem);
                }
                final Object depenClassPath = obj[j + 2];
                if (depenClassPath != null) {
                    final ArrayList classPathList = (ArrayList)depenClassPath;
                    for (int size2 = classPathList.size(), l = 0; l < size2; ++l) {
                        final Element depenClassPathElem = this.createTextNode(xmlDoc, "classPath", classPathList.get(l));
                        pcvChild.appendChild(depenClassPathElem);
                    }
                }
                final Properties value = (Properties)obj[j + 3];
                final Enumeration e = value.keys();
                while (e.hasMoreElements()) {
                    final String obKey = e.nextElement();
                    final String obValue = value.getProperty(obKey);
                    final Element pcvVersion = xmlDoc.createElement("property");
                    pcvVersion.setAttribute("name", obKey);
                    pcvVersion.setAttribute("value", obValue);
                    pcvChild.appendChild(pcvVersion);
                }
                elem.appendChild(pcvChild);
            }
        }
        return elem;
    }
    
    public boolean updateXML(final XmlDocument doc, final String dir) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(dir));
        }
        catch (final Exception e) {
            System.out.println("File not Found :");
        }
        try {
            final Element root = doc.getDocumentElement();
            root.normalize();
            doc.write((OutputStream)fos);
            fos.close();
        }
        catch (final Exception e) {
            System.out.println("Error in saving doc");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void writeXmlFile(final String dir) {
        this.updateXML(this.createDocument(), dir);
    }
    
    public void setGeneralProps(final Properties prop) {
        this.generalProps = prop;
    }
    
    public Properties getGeneralProps() {
        return this.generalProps;
    }
    
    private Element createPropertiesNode(final XmlDocument xmlDoc, final Properties prop) {
        if (prop == null) {
            return null;
        }
        final Element elem = xmlDoc.createElement("Properties");
        final Enumeration e = prop.keys();
        while (e.hasMoreElements()) {
            final String obKey = e.nextElement();
            final String obValue = prop.getProperty(obKey);
            final Element property = xmlDoc.createElement("property");
            property.setAttribute("name", obKey);
            property.setAttribute("value", obValue);
            elem.appendChild(property);
        }
        return elem;
    }
    
    public void setPatchDisplayName(final String name) {
        this.displayName = name;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setFeatureName(final String name) {
        this.featureName = name;
    }
    
    public String getFeatureName() {
        return this.featureName;
    }
    
    public void setPatchType(final String type) {
        this.patchType = type;
    }
    
    public String getPatchType() {
        return this.patchType;
    }
    
    public void setPatchContentType(final String type) {
        this.patchContentType = type;
    }
    
    public String getPatchContentType() {
        return this.patchContentType;
    }
    
    public void setFeatureCompatibility(final ArrayList list) {
        this.featureComp = list;
    }
    
    public ArrayList getFeatureCompatibility() {
        return this.featureComp;
    }
    
    private Element createFeatureNode(final XmlDocument xmlDoc, final ArrayList list, final String name) {
        final Element fcv = xmlDoc.createElement(name);
        if (list == null || list.isEmpty()) {
            return fcv;
        }
        for (int size = list.size(), i = 0; i < size; ++i) {
            final FeatureCompInfo fpc = list.get(i);
            final String productName = fpc.getProductName();
            final Element prdElem = xmlDoc.createElement("compProductName");
            prdElem.setAttribute("name", productName);
            fcv.appendChild(prdElem);
            final Object[] obj = fpc.getPrdVersionInfo();
            for (int s = obj.length, j = 0; j < s; ++j) {
                final FeaturePrdVersionInfo fc = (FeaturePrdVersionInfo)obj[j];
                final String productVersion = fc.getProductVersion();
                final Element versionElem = xmlDoc.createElement("version");
                versionElem.setAttribute("compProductVersion", productVersion);
                prdElem.appendChild(versionElem);
                final FeatureVersionComp fvc = fc.getFeatureVersionComp();
                if (fvc != null) {
                    final String option = fvc.getCompPatchOption();
                    final String compVers = fvc.getCompPatchVersion();
                    if (compVers != null && option != null) {
                        final Element comPat = xmlDoc.createElement("compPatchVersion");
                        comPat.setAttribute("option", option);
                        versionElem.appendChild(comPat);
                        final Text pcvText = xmlDoc.createTextNode(compVers);
                        comPat.appendChild(pcvText);
                        versionElem.appendChild(comPat);
                    }
                    final String[] verr = fvc.getVersions();
                    if (verr != null) {
                        final int len = verr.length;
                        for (int l = 0; l < verr.length; l += 3) {
                            final String featureName = verr[l];
                            final String featureOption = verr[l + 1];
                            final String featureVersion = verr[l + 2];
                            final Element fpvElem = xmlDoc.createElement("compFPVersion");
                            fpvElem.setAttribute("name", featureName);
                            fpvElem.setAttribute("option", featureOption);
                            final Text pcvText2 = xmlDoc.createTextNode(featureVersion);
                            fpvElem.appendChild(pcvText2);
                            versionElem.appendChild(fpvElem);
                        }
                    }
                }
            }
        }
        return fcv;
    }
    
    public Node getLocaleSpecificReadme() {
        return this.localeReadmeElement;
    }
}
