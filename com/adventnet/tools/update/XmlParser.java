package com.adventnet.tools.update;

import java.util.Set;
import java.util.List;
import org.w3c.dom.NodeList;
import java.util.TreeSet;
import java.util.Properties;
import java.util.ArrayList;
import com.adventnet.tools.update.installer.CustomPatchStateTracker;
import com.adventnet.tools.update.installer.CustomPatchValidator;
import java.util.HashMap;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Node;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;

public class XmlParser
{
    private XmlData xmlData;
    private UpdateData updateData;
    
    public XmlParser(final String filename) {
        this.xmlData = null;
        this.updateData = null;
        try {
            final FileInputStream fis = new FileInputStream(filename);
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(fis);
            this.xmlData = new XmlData();
            final Element rootNode = doc.getDocumentElement();
            this.processNodeDetails(rootNode);
            final Vector vec = this.xmlData.getFileGroups();
            fis.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }
    
    public XmlParser(final InputStream stream) {
        this.xmlData = null;
        this.updateData = null;
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(stream);
            this.xmlData = new XmlData();
            final Element rootNode = doc.getDocumentElement();
            this.processNodeDetails(rootNode);
            final Vector vec = this.xmlData.getFileGroups();
            stream.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }
    
    void processNodeDetails(final Node node) throws Exception {
        String nodeName = null;
        if (node.getNodeType() != 1) {
            return;
        }
        nodeName = node.getNodeName();
        if (nodeName.equals("xmlData")) {
            final NodeList childList = node.getChildNodes();
            for (int childListLen = childList.getLength(), ctr = 0; ctr < childListLen; ++ctr) {
                final Node childNode = childList.item(ctr);
                this.processNodeDetails(childNode);
            }
        }
        else if (nodeName.equals("patchDetails")) {
            final NodeList childList2 = node.getChildNodes();
            final int childListLen2 = childList2.getLength();
            int ctr2 = 0;
            final String filename = null;
            while (ctr2 < childListLen2) {
                final Node childNode2 = childList2.item(ctr2);
                if (childNode2.getNodeType() != 1) {
                    ++ctr2;
                }
                else {
                    if (childNode2.getNodeName().equals("patchDescription")) {
                        final Node patchDescNode = childNode2.getFirstChild();
                        if (patchDescNode != null) {
                            final String patchDesc = patchDescNode.getNodeValue();
                            if (patchDesc != null) {
                                this.xmlData.setPatchDescription(patchDesc);
                            }
                        }
                    }
                    else if (childNode2.getNodeName().equals("productName")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setProductName(prdNode.getNodeValue());
                        }
                    }
                    else if (childNode2.getNodeName().equals("productVersion")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setProductVersion(prdNode.getNodeValue());
                        }
                    }
                    else if (childNode2.getNodeName().equals("displayName")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setPatchDisplayName(prdNode.getNodeValue());
                        }
                    }
                    else if (childNode2.getNodeName().equals("feature")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setFeatureName(prdNode.getNodeValue());
                        }
                    }
                    else if (childNode2.getNodeName().equals("patchType")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setPatchType(prdNode.getNodeValue());
                        }
                    }
                    else if (childNode2.getNodeName().equals("patchContentType")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setPatchContentType(prdNode.getNodeValue());
                        }
                    }
                    else if (childNode2.getNodeName().equals("patchVersion")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setPatchVersion(prdNode.getNodeValue());
                        }
                    }
                    else if (childNode2.getNodeName().equals("patchReadme")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setPatchReadme(prdNode.getNodeValue());
                        }
                    }
                    else if (childNode2.getNodeName().equals("LOCALEREADME")) {
                        this.xmlData.setLocaleSpecificReadme(childNode2);
                    }
                    else if (childNode2.getNodeName().equals("patchReadmeUrl")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null) {
                            this.xmlData.setPatchReadmeUrl(prdNode.getNodeValue());
                            this.xmlData.setReadMeType(3);
                        }
                    }
                    else if (childNode2.getNodeName().equals("patchReadmeFileUrl")) {
                        final Node prdNode = childNode2.getFirstChild();
                        if (prdNode != null && prdNode.getNodeValue().equals("true")) {
                            this.xmlData.setReadMeType(2);
                        }
                    }
                    else if (childNode2.getNodeName().equals("productCompatibility")) {
                        final ArrayList list = this.getFeatureCompatibility(childNode2);
                        this.xmlData.setFeatureCompatibility(list);
                    }
                    else if (childNode2.getNodeName().equals("jarCompatibility")) {
                        final NodeList list2 = childNode2.getChildNodes();
                        final int listLen = list2.getLength();
                        int length = 0;
                        String subfilename = null;
                        final HashMap hash = new HashMap();
                        while (length < listLen) {
                            final Node subChild = list2.item(length);
                            subfilename = subChild.getNodeName();
                            String jarName = null;
                            HashMap property = null;
                            if (subfilename.equals("compJarName")) {
                                jarName = ((Element)subChild).getAttribute("name");
                                final NodeList subchildList = subChild.getChildNodes();
                                final int len = subchildList.getLength();
                                int count = 0;
                                String versionChild = null;
                                property = new HashMap();
                                while (count < len) {
                                    final Node subChildNode = subchildList.item(count);
                                    versionChild = subChildNode.getNodeName();
                                    String propertyName = null;
                                    String propertyValue = null;
                                    if (versionChild.equals("property")) {
                                        propertyName = ((Element)subChildNode).getAttribute("name");
                                        propertyValue = ((Element)subChildNode).getAttribute("value");
                                    }
                                    if (propertyName != null && propertyValue != null) {
                                        property.put(propertyName, propertyValue);
                                    }
                                    ++count;
                                }
                                hash.put(jarName, property);
                            }
                            ++length;
                        }
                        this.xmlData.setJarCompatible(hash);
                    }
                    else if (childNode2.getNodeName().equals("customPatchValidator")) {
                        final CustomPatchValidator cpv = new CustomPatchValidator(childNode2);
                        this.xmlData.setCustomPatchValidator(cpv);
                    }
                    else if (childNode2.getNodeName().equals("resourceFile")) {
                        this.xmlData.setResourceFile(childNode2.getFirstChild().getNodeValue());
                    }
                    else if (childNode2.getNodeName().equals("autoCloseOnSuccess")) {
                        this.xmlData.setAutoClose(Boolean.valueOf(childNode2.getFirstChild().getNodeValue()));
                    }
                    else if (childNode2.getNodeName().equals("autoCloseDelay")) {
                        this.xmlData.setAutoCloseDelay(Long.parseLong(childNode2.getFirstChild().getNodeValue()));
                    }
                    else if (childNode2.getNodeName().equals("patchStateTracker")) {
                        final CustomPatchStateTracker customPatchStateTracker = new CustomPatchStateTracker(childNode2);
                        this.xmlData.setCustomPatchStateTracker(customPatchStateTracker);
                    }
                    else if (childNode2.getNodeName().equals("hotSwapHandler")) {
                        this.xmlData.setHotSwapHandler(childNode2.getAttributes().getNamedItem("name").getNodeValue());
                        final NodeList dependentClassPathNodes = ((Element)childNode2).getElementsByTagName("dependentClassPath");
                        if (dependentClassPathNodes != null) {
                            final List<String> dependentClassesList = new ArrayList<String>();
                            for (int i = 0; i < dependentClassPathNodes.getLength(); ++i) {
                                dependentClassesList.add(dependentClassPathNodes.item(i).getFirstChild().getNodeValue());
                            }
                            this.xmlData.setHotSwapHandlerDependents(dependentClassesList);
                        }
                    }
                    else {
                        System.out.println("INVALID NODE:" + childNode2.getNodeName());
                    }
                    ++ctr2;
                }
            }
        }
        else if (nodeName.equals("CleanUp")) {
            final NodeList childList = node.getChildNodes();
            final List<String> cleanUpFiles = new ArrayList<String>();
            for (int j = 0; j < childList.getLength(); ++j) {
                final Node fileNode = childList.item(j);
                if (fileNode.getNodeName().equals("file")) {
                    cleanUpFiles.add(fileNode.getAttributes().getNamedItem("path").getNodeValue());
                }
            }
            if (!cleanUpFiles.isEmpty()) {
                this.xmlData.setCleanUpFiles(cleanUpFiles);
            }
        }
        else if (nodeName.equals("context")) {
            final NodeList childList = node.getChildNodes();
            for (int childListLen = childList.getLength(), ctr = 0; ctr < childListLen; ++ctr) {
                final Node childNode = childList.item(ctr);
                this.processNodeDetails(childNode);
            }
        }
        else if (nodeName.equals("updation")) {
            (this.updateData = new UpdateData()).setContextName(((Element)node).getAttribute("context"));
            this.updateData.setContextReadme(((Element)node).getAttribute("readme"));
            this.updateData.setContextType(((Element)node).getAttribute("type"));
            this.updateData.setContextDescription(((Element)node).getAttribute("details"));
            final NodeList childList = node.getChildNodes();
            for (int childListLen = childList.getLength(), ctr = 0; ctr < childListLen; ++ctr) {
                final Node childNode = childList.item(ctr);
                this.processNodeDetails(childNode);
            }
            final Vector dependencyContext = new Vector();
            final Vector depenVector = this.xmlData.getDependencyContext();
            for (int desize = depenVector.size(), l = 0; l < desize; ++l) {
                dependencyContext.addElement(depenVector.elementAt(l));
            }
            this.updateData.setDependencyContext(dependencyContext);
            final Vector vector = new Vector();
            final Vector oldVector = this.xmlData.getFileGroups();
            for (int vecSize = oldVector.size(), k = 0; k < vecSize; ++k) {
                vector.addElement(oldVector.elementAt(k));
            }
            this.updateData.setContextVector(vector);
            final ArrayList newList = new ArrayList();
            final ArrayList list3 = this.xmlData.getNewFileGroup();
            for (int nsize = list3.size(), n = 0; n < nsize; ++n) {
                newList.add(list3.get(n));
            }
            this.updateData.setNewFileGroup(newList);
            final ArrayList zipList = new ArrayList();
            final ArrayList zlist = this.xmlData.getZipFileGroup();
            for (int zsize = zlist.size(), n2 = 0; n2 < zsize; ++n2) {
                zipList.add(zlist.get(n2));
            }
            this.updateData.setZipFileGroup(zipList);
            Object[] preArray = new Object[0];
            final Object[] obj = this.xmlData.getPreInstallClasses();
            for (int count2 = obj.length, m = 0; m < count2; m += 4) {
                final Object className = obj[m];
                final Object depenClassName = obj[m + 1];
                final Object depenClassPath = obj[m + 2];
                final Object classProp = obj[m + 3];
                final int len2 = preArray.length;
                final Object[] tmp = new Object[len2 + 4];
                System.arraycopy(obj, 0, tmp, 0, len2);
                tmp[len2] = className;
                tmp[len2 + 1] = depenClassName;
                tmp[len2 + 2] = depenClassPath;
                tmp[len2 + 3] = classProp;
                preArray = tmp;
            }
            this.updateData.setPreInstallArray(preArray);
            Object[] postArray = new Object[0];
            final Object[] pobj = this.xmlData.getPostInstallClasses();
            for (int pcount = pobj.length, k2 = 0; k2 < pcount; k2 += 4) {
                final Object className2 = pobj[k2];
                final Object depenClassName2 = pobj[k2 + 1];
                final Object depenClassPath2 = pobj[k2 + 2];
                final Object classProp2 = pobj[k2 + 3];
                final int len3 = postArray.length;
                final Object[] tmp2 = new Object[len3 + 4];
                System.arraycopy(pobj, 0, tmp2, 0, len3);
                tmp2[len3] = className2;
                tmp2[len3 + 1] = depenClassName2;
                tmp2[len3 + 2] = depenClassPath2;
                tmp2[len3 + 3] = classProp2;
                postArray = tmp2;
            }
            this.updateData.setPostInstallArray(postArray);
            this.xmlData.getContextTable().put(this.updateData.getContextName(), this.updateData);
            this.xmlData.getFileGroups().clear();
            this.xmlData.getNewFileGroup().clear();
            this.xmlData.getZipFileGroup().clear();
            this.xmlData.getDependencyContext().clear();
            this.xmlData.setPreInstallClasses(new Object[0]);
            this.xmlData.setPostInstallClasses(new Object[0]);
        }
        else if (nodeName.equals("dependency")) {
            final NodeList preList = node.getChildNodes();
            final int preLen = preList.getLength();
            int preCount = 0;
            final String prefilename = null;
            while (preCount < preLen) {
                final Node preNode = preList.item(preCount);
                if (preNode.getNodeType() != 1) {
                    ++preCount;
                }
                else {
                    if (preNode.getNodeName().equals("on")) {
                        final Node classNode = preNode.getFirstChild();
                        if (classNode != null) {
                            this.xmlData.getDependencyContext().addElement(classNode.getNodeValue());
                        }
                    }
                    ++preCount;
                }
            }
        }
        else if (nodeName.equals("preInstall")) {
            final NodeList preList = node.getChildNodes();
            final int preLen = preList.getLength();
            int preCount = 0;
            String prefilename = null;
            while (preCount < preLen) {
                final Node preNode = preList.item(preCount);
                prefilename = preNode.getNodeName();
                String className3 = null;
                Properties prop = null;
                ArrayList depenArrayList = null;
                ArrayList classPath = null;
                if (prefilename.equals("className")) {
                    className3 = ((Element)preNode).getAttribute("name");
                    final NodeList subchildList2 = preNode.getChildNodes();
                    final int len4 = subchildList2.getLength();
                    int count3 = 0;
                    String versionChild2 = null;
                    prop = new Properties();
                    classPath = new ArrayList();
                    while (count3 < len4) {
                        final Node subChildNode2 = subchildList2.item(count3);
                        versionChild2 = subChildNode2.getNodeName();
                        if (versionChild2.equals("property")) {
                            String propertyName2 = null;
                            String propertyValue2 = null;
                            propertyName2 = ((Element)subChildNode2).getAttribute("name");
                            propertyValue2 = ((Element)subChildNode2).getAttribute("value");
                            if (propertyName2 != null && propertyValue2 != null) {
                                prop.setProperty(propertyName2, propertyValue2);
                            }
                        }
                        else if (versionChild2.equals("classPath")) {
                            classPath.add(subChildNode2.getFirstChild().getNodeValue());
                        }
                        else if (versionChild2.equals("dependentClasses")) {
                            final NodeList depenList = subChildNode2.getChildNodes();
                            final int depenLen = depenList.getLength();
                            int depenCount = 0;
                            depenArrayList = new ArrayList();
                            while (depenCount < depenLen) {
                                final Node depenNode = depenList.item(depenCount);
                                final String depenFileName = depenNode.getNodeName();
                                String depenClassName3 = null;
                                if (depenFileName.equals("dependentClassName")) {
                                    depenClassName3 = ((Element)depenNode).getAttribute("name");
                                    depenArrayList.add(depenClassName3);
                                }
                                ++depenCount;
                            }
                        }
                        ++count3;
                    }
                    Object[] obj2 = this.xmlData.getPreInstallClasses();
                    int j2;
                    for (int leng = j2 = obj2.length; j2 < leng + 4; j2 += 4) {
                        final Object[] tmp3 = new Object[j2 + 4];
                        System.arraycopy(obj2, 0, tmp3, 0, j2);
                        tmp3[j2] = className3;
                        tmp3[j2 + 1] = depenArrayList;
                        tmp3[j2 + 2] = classPath;
                        tmp3[j2 + 3] = prop;
                        obj2 = tmp3;
                    }
                    this.xmlData.setPreInstallClasses(obj2);
                }
                ++preCount;
            }
        }
        else if (nodeName.equals("postInstall")) {
            final NodeList preList = node.getChildNodes();
            final int preLen = preList.getLength();
            int preCount = 0;
            String prefilename = null;
            while (preCount < preLen) {
                final Node preNode = preList.item(preCount);
                prefilename = preNode.getNodeName();
                String className3 = null;
                Properties prop = null;
                ArrayList depenArrayList = null;
                ArrayList classPath = null;
                if (prefilename.equals("className")) {
                    className3 = ((Element)preNode).getAttribute("name");
                    final NodeList subchildList2 = preNode.getChildNodes();
                    final int len4 = subchildList2.getLength();
                    int count3 = 0;
                    String versionChild2 = null;
                    prop = new Properties();
                    classPath = new ArrayList();
                    while (count3 < len4) {
                        final Node subChildNode2 = subchildList2.item(count3);
                        versionChild2 = subChildNode2.getNodeName();
                        if (versionChild2.equals("property")) {
                            String propertyName2 = null;
                            String propertyValue2 = null;
                            propertyName2 = ((Element)subChildNode2).getAttribute("name");
                            propertyValue2 = ((Element)subChildNode2).getAttribute("value");
                            if (propertyName2 != null && propertyValue2 != null) {
                                prop.setProperty(propertyName2, propertyValue2);
                            }
                        }
                        else if (versionChild2.equals("classPath")) {
                            classPath.add(subChildNode2.getFirstChild().getNodeValue());
                        }
                        else if (versionChild2.equals("dependentClasses")) {
                            final NodeList depenList = subChildNode2.getChildNodes();
                            final int depenLen = depenList.getLength();
                            int depenCount = 0;
                            depenArrayList = new ArrayList();
                            while (depenCount < depenLen) {
                                final Node depenNode = depenList.item(depenCount);
                                final String depenFileName = depenNode.getNodeName();
                                String depenClassName3 = null;
                                if (depenFileName.equals("dependentClassName")) {
                                    depenClassName3 = ((Element)depenNode).getAttribute("name");
                                    depenArrayList.add(depenClassName3);
                                }
                                ++depenCount;
                            }
                        }
                        ++count3;
                    }
                    Object[] obj2 = this.xmlData.getPostInstallClasses();
                    int j2;
                    for (int leng = j2 = obj2.length; j2 < leng + 4; j2 += 4) {
                        final Object[] tmp3 = new Object[j2 + 4];
                        System.arraycopy(obj2, 0, tmp3, 0, j2);
                        tmp3[j2] = className3;
                        tmp3[j2 + 1] = depenArrayList;
                        tmp3[j2 + 2] = classPath;
                        tmp3[j2 + 3] = prop;
                        obj2 = tmp3;
                    }
                    this.xmlData.setPostInstallClasses(obj2);
                }
                ++preCount;
            }
        }
        else if (nodeName.equals("add")) {
            final NodeList childList = node.getChildNodes();
            for (int childListLen = childList.getLength(), ctr = 0; ctr < childListLen; ++ctr) {
                final Node childNode = childList.item(ctr);
                this.processNodeDetails(childNode);
            }
        }
        else if (nodeName.equals("fileGroup")) {
            final FileGroup group = new FileGroup();
            this.xmlData.getFileGroups().addElement(group);
            final NodeList childList3 = node.getChildNodes();
            for (int childListLen3 = childList3.getLength(), ctr3 = 0; ctr3 < childListLen3; ++ctr3) {
                final Node childNode3 = childList3.item(ctr3);
                this.processNodeDetails(childNode3);
            }
        }
        else if (nodeName.equals("newFiles")) {
            final NodeList childList = node.getChildNodes();
            for (int childListLen = childList.getLength(), ctr = 0; ctr < childListLen; ++ctr) {
                final Node childNode = childList.item(ctr);
                this.processNodeDetails(childNode);
            }
        }
        else if (nodeName.equals("zipFiles")) {
            final NodeList childList = node.getChildNodes();
            for (int childListLen = childList.getLength(), ctr = 0; ctr < childListLen; ++ctr) {
                final Node childNode = childList.item(ctr);
                this.processNodeDetails(childNode);
            }
        }
        else if (nodeName.equals("version")) {
            final NewFileGroup newFiles = new NewFileGroup();
            newFiles.setVersionName(((Element)node).getAttribute("name"));
            this.xmlData.setNewFileGroup(newFiles);
            final NodeList childList3 = node.getChildNodes();
            final int childListLen3 = childList3.getLength();
            int ctr3 = 0;
            String filename2 = null;
            while (ctr3 < childListLen3) {
                final Node childNode3 = childList3.item(ctr3);
                if (childNode3.getNodeType() != 1) {
                    ++ctr3;
                }
                else {
                    if (childNode3.getNodeName().equals("fileName")) {
                        filename2 = childNode3.getFirstChild().getNodeValue();
                        newFiles.getFilesList().add(filename2);
                    }
                    else {
                        System.out.println("INVALID NODE:" + childNode3.getNodeName());
                    }
                    ++ctr3;
                }
            }
        }
        else if (nodeName.equals("archive")) {
            final ZipFileGroup zipFiles = new ZipFileGroup();
            zipFiles.setZipName(((Element)node).getAttribute("name"));
            this.xmlData.setZipFileGroup(zipFiles);
            final NodeList childList3 = node.getChildNodes();
            final int childListLen3 = childList3.getLength();
            int ctr3 = 0;
            String filename2 = null;
            while (ctr3 < childListLen3) {
                final Node childNode3 = childList3.item(ctr3);
                if (childNode3.getNodeType() != 1) {
                    ++ctr3;
                }
                else {
                    if (childNode3.getNodeName().equals("fileName")) {
                        filename2 = childNode3.getFirstChild().getNodeValue();
                        zipFiles.getFilesList().add(filename2);
                    }
                    else {
                        System.out.println("INVALID NODE:" + childNode3.getNodeName());
                    }
                    ++ctr3;
                }
            }
        }
        else if (nodeName.equals("targetToUpdate")) {
            final NodeList childList2 = node.getChildNodes();
            final int childListLen2 = childList2.getLength();
            int ctr2 = 0;
            String filename = null;
            while (ctr2 < childListLen2) {
                final Node childNode2 = childList2.item(ctr2);
                if (childNode2.getNodeType() != 1) {
                    ++ctr2;
                }
                else {
                    if (childNode2.getNodeName().equals("jarName")) {
                        filename = childNode2.getFirstChild().getNodeValue();
                        final FileGroup fileGrpObj = this.xmlData.getFileGroups().lastElement();
                        fileGrpObj.getJarNameVector().addElement(filename);
                    }
                    else {
                        System.out.println("INVALID NODE:" + childNode2.getNodeName());
                    }
                    ++ctr2;
                }
            }
        }
        else if (nodeName.equals("filesToUpdate")) {
            final NodeList childList2 = node.getChildNodes();
            final int childListLen2 = childList2.getLength();
            int ctr2 = 0;
            String filename = null;
            while (ctr2 < childListLen2) {
                final Node childNode2 = childList2.item(ctr2);
                if (childNode2.getNodeType() != 1) {
                    ++ctr2;
                }
                else {
                    if (childNode2.getNodeName().equals("fileName")) {
                        filename = childNode2.getFirstChild().getNodeValue();
                        final FileGroup fileGrpObj = this.xmlData.getFileGroups().lastElement();
                        fileGrpObj.getFileNameVector().addElement(filename);
                    }
                    else {
                        System.out.println("INVALID NODE:" + childNode2.getNodeName());
                    }
                    ++ctr2;
                }
            }
        }
        else if (nodeName.equals("filesToDelete")) {
            final NodeList childList2 = node.getChildNodes();
            final int childListLen2 = childList2.getLength();
            int ctr2 = 0;
            String filename = null;
            while (ctr2 < childListLen2) {
                final Node childNode2 = childList2.item(ctr2);
                if (childNode2.getNodeType() != 1) {
                    ++ctr2;
                }
                else {
                    if (childNode2.getNodeName().equals("fileName")) {
                        filename = childNode2.getFirstChild().getNodeValue();
                        final FileGroup fileGrpObj = this.xmlData.getFileGroups().lastElement();
                        fileGrpObj.addDeletedFileEntry(filename);
                    }
                    else {
                        System.out.println("INVALID NODE:" + childNode2.getNodeName());
                    }
                    ++ctr2;
                }
            }
        }
        else if (nodeName.equals("delete")) {
            final NodeList childNodes = node.getChildNodes();
            for (int nodesCount = childNodes.getLength(), cnt = 0; cnt < nodesCount; ++cnt) {
                final Node childNode = childNodes.item(cnt);
                if (childNode.getNodeType() == 1) {
                    if (childNode.getNodeName().equals("fileName")) {
                        final String fileName = childNode.getFirstChild().getNodeValue();
                        this.updateData.markFileForDelete(fileName);
                    }
                    else if (childNode.getNodeName().equals("jarEntries")) {
                        final String jarName2 = ((Element)childNode).getAttribute("jarName");
                        final Set<String> deletedJarEntries = new TreeSet<String>();
                        final NodeList subChildNodes = childNode.getChildNodes();
                        for (int l = 0; l < subChildNodes.getLength(); ++l) {
                            final Node subChildNode3 = subChildNodes.item(l);
                            if (subChildNode3.getNodeType() == 1) {
                                if (subChildNode3.getNodeName().equals("jarEntry")) {
                                    final String jarEntry = subChildNode3.getFirstChild().getNodeValue();
                                    deletedJarEntries.add(jarEntry);
                                }
                            }
                        }
                        this.updateData.markJarEntriesForDelete(jarName2, deletedJarEntries);
                    }
                    else {
                        System.out.println("INVALID NODE:" + childNode.getNodeName());
                    }
                }
            }
        }
        else if (nodeName.equals("Properties")) {
            final NodeList subchildList3 = node.getChildNodes();
            final int len5 = subchildList3.getLength();
            int count4 = 0;
            String childNode4 = null;
            final Properties prop2 = new Properties();
            while (count4 < len5) {
                final Node subChildNode4 = subchildList3.item(count4);
                childNode4 = subChildNode4.getNodeName();
                String propertyName3 = null;
                String propertyValue3 = null;
                if (childNode4.equals("property")) {
                    propertyName3 = ((Element)subChildNode4).getAttribute("name");
                    propertyValue3 = ((Element)subChildNode4).getAttribute("value");
                }
                if (propertyName3 != null && propertyValue3 != null) {
                    prop2.setProperty(propertyName3, propertyValue3);
                }
                ++count4;
            }
            this.xmlData.setGeneralProps(prop2);
        }
    }
    
    public XmlData getXmlData() {
        return this.xmlData;
    }
    
    private ArrayList getFeatureCompatibility(final Node childNode) {
        final NodeList list = childNode.getChildNodes();
        final int listLen = list.getLength();
        int length = 0;
        String subfilename = null;
        final ArrayList fList = new ArrayList();
        while (length < listLen) {
            final Node subChild = list.item(length);
            if (subChild.getNodeType() != 1) {
                ++length;
            }
            else {
                subfilename = subChild.getNodeName();
                if (!subfilename.equals("compProductName")) {
                    break;
                }
                final FeatureCompInfo fcomp = new FeatureCompInfo();
                final String productNameKey = ((Element)subChild).getAttribute("name");
                final NodeList subchildList = subChild.getChildNodes();
                final int len = subchildList.getLength();
                int count = 0;
                String versionChild = null;
                fcomp.setProductName(productNameKey);
                fList.add(fcomp);
                while (count < len) {
                    final Node subChildNode = subchildList.item(count);
                    if (subChildNode.getNodeType() != 1) {
                        ++count;
                    }
                    else {
                        versionChild = subChildNode.getNodeName();
                        String version = null;
                        if (!versionChild.equals("version")) {
                            break;
                        }
                        final FeaturePrdVersionInfo fvcomp = new FeaturePrdVersionInfo();
                        version = ((Element)subChildNode).getAttribute("compProductVersion");
                        final NodeList patchchildList = subChildNode.getChildNodes();
                        final int plength = patchchildList.getLength();
                        int pcount = 0;
                        final String patchChild = null;
                        fvcomp.setProductVersion(version);
                        fcomp.addPrdVersionInfo(fvcomp);
                        final FeatureVersionComp fpComp = new FeatureVersionComp();
                        while (pcount < plength) {
                            final Node child = patchchildList.item(pcount);
                            if (child.getNodeType() != 1) {
                                ++pcount;
                            }
                            else {
                                if (!child.getNodeName().equals("compFPVersion") && !child.getNodeName().equals("compPatchVersion")) {
                                    System.out.println("INVALID NODE:" + child.getNodeName());
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
                        fvcomp.setFeatureVersionComp(fpComp);
                        ++count;
                    }
                }
                ++length;
            }
        }
        return fList;
    }
}
