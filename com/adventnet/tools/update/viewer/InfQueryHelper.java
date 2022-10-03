package com.adventnet.tools.update.viewer;

import com.adventnet.tools.update.util.Utils;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import com.adventnet.tools.update.XmlData;
import com.adventnet.tools.update.NewFileGroup;
import java.util.Collection;
import com.adventnet.tools.update.FileGroup;
import com.adventnet.tools.update.UpdateData;
import com.adventnet.tools.update.installer.UpdateManager;
import com.adventnet.tools.update.XmlParser;
import java.util.ArrayList;

public class InfQueryHelper
{
    private String fileSep;
    private String productHome;
    private ArrayList fileNameList;
    private ArrayList newFileList;
    private ArrayList modifiedJars;
    private String[] contextList;
    
    public InfQueryHelper(final String productHome) {
        this.fileSep = System.getProperty("file.separator");
        this.productHome = null;
        this.fileNameList = new ArrayList();
        this.newFileList = new ArrayList();
        this.modifiedJars = new ArrayList();
        this.contextList = null;
        this.productHome = productHome;
        final XmlParser xParser = new XmlParser(productHome + this.fileSep + "Patch" + this.fileSep + "inf.xml");
        final XmlData xData = xParser.getXmlData();
        final String currContext = UpdateManager.getSubProductName("conf");
        final Hashtable contextTable = xData.getContextTable();
        final ArrayList tempContext = new ArrayList();
        final Enumeration en = contextTable.keys();
        while (en.hasMoreElements()) {
            tempContext.add(en.nextElement() + "/");
        }
        this.contextList = tempContext.toArray(new String[0]);
        final UpdateData uData = contextTable.get(currContext);
        if (uData != null) {
            final Vector contextVector = uData.getContextVector();
            for (int i = 0; i < contextVector.size(); ++i) {
                final FileGroup fGroup = contextVector.elementAt(i);
                if (fGroup.getJarNameVector().size() == 0) {
                    this.fileNameList.addAll(fGroup.getFileNameVector());
                }
                else {
                    this.modifiedJars.addAll(fGroup.getJarNameVector());
                }
            }
            String[] patchVersions = UpdateManager.getAllServicePackVersions(productHome);
            if (patchVersions == null) {
                patchVersions = new String[] { "BaseVersion" };
            }
            else {
                final String[] tempVersions = new String[patchVersions.length + 1];
                System.arraycopy(patchVersions, 0, tempVersions, 0, patchVersions.length);
                tempVersions[tempVersions.length - 1] = "BaseVersion";
                patchVersions = tempVersions;
            }
            final ArrayList list = uData.getNewFileGroup();
            boolean isVersionFound = true;
            for (int j = 0; isVersionFound && j < patchVersions.length; ++j) {
                final String version = patchVersions[j];
                for (int k = 0; isVersionFound && k < list.size(); ++k) {
                    final NewFileGroup nfg = list.get(k);
                    final String nfgVersion = nfg.getVersionName();
                    if (nfgVersion.equals(version)) {
                        this.newFileList.addAll(nfg.getFilesList());
                        isVersionFound = false;
                    }
                }
            }
        }
        this.fileNameList = this.replaceBackSlash(this.fileNameList);
        this.modifiedJars = this.replaceBackSlash(this.modifiedJars);
        this.newFileList = this.replaceBackSlash(this.newFileList);
    }
    
    public ArrayList filterFilesBasedOnContext(final ArrayList inputList) {
        final ArrayList listToReturn = new ArrayList();
        for (int size = inputList.size(), i = 0; i < size; ++i) {
            String inputFile = inputList.remove(0);
            if (!this.isThisContextSpecific(inputFile)) {
                if (inputFile.endsWith(".ujar")) {
                    inputFile = inputFile.substring(0, inputFile.lastIndexOf(".ujar")) + ".jar";
                }
                else if (this.fileNameList.indexOf(inputFile) != -1) {
                    listToReturn.add(inputFile);
                }
                else if (this.modifiedJars.indexOf(inputFile) != -1) {
                    listToReturn.add(inputFile);
                }
            }
        }
        return listToReturn;
    }
    
    private boolean isThisContextSpecific(final String fileName) {
        for (int i = 0; i < this.contextList.length; ++i) {
            if (fileName.startsWith(this.contextList[i])) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList getNewFiles() {
        return this.newFileList;
    }
    
    public ArrayList getModifiedJars() {
        return this.modifiedJars;
    }
    
    private ArrayList replaceBackSlash(final ArrayList list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final String fileName = list.get(i);
            list.set(i, Utils.getUnixFileName(fileName));
        }
        return list;
    }
}
