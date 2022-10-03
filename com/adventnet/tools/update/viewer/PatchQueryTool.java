package com.adventnet.tools.update.viewer;

import com.adventnet.tools.update.util.Criteria;
import java.util.Collection;
import com.adventnet.tools.update.util.GroupingFunction;
import com.adventnet.tools.update.util.EnhancedFileFilter;
import java.util.Enumeration;
import java.util.Hashtable;
import com.adventnet.tools.update.util.JarFileUtil;
import java.util.ArrayList;
import java.util.zip.ZipFile;
import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.HashMap;
import com.adventnet.tools.update.util.ZipDiffUtil;

public class PatchQueryTool
{
    private String productHome;
    private String ppmFile;
    private ConfReader confReader;
    private ZipDiffUtil diffUtil;
    private InfQueryHelper infHelper;
    private HashMap baseIdVsFileList;
    private final String EXCEPTIONS = "ExceptionCases";
    private final String NEWFILES = "NewFiles";
    private final String REINTRODUCED = "ReIntroducedFiles";
    private final String MODIFIED = "ModifiedFiles";
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("USAGE : java com.adventnet.tools.update.viewer.PatchQueryTool <ProductHome> <PPMFile>");
            System.exit(0);
        }
        final List excludeCRCList = new Vector();
        excludeCRCList.add("*.class");
        final Properties props = new Properties();
        ((Hashtable<String, List>)props).put("ExcludeForCRC", excludeCRCList);
        final PatchQueryTool pqt = new PatchQueryTool(args[0], args[1], props);
        pqt.getFileList("ExceptionCases");
        pqt.getFileList("ModifiedFiles");
        pqt.getFileList("NewFiles");
        pqt.getFileList("ReIntroducedFiles");
    }
    
    public PatchQueryTool(final String productHome, final String ppmFile, final Properties props) throws Exception {
        this.productHome = ".";
        this.ppmFile = null;
        this.confReader = null;
        this.diffUtil = null;
        this.infHelper = null;
        this.baseIdVsFileList = new HashMap();
        this.productHome = productHome;
        this.ppmFile = ppmFile;
        this.parseTheXML(productHome);
        this.extractFiles();
        final File bFile = new File(productHome);
        final ZipFile pZip = new ZipFile(ppmFile);
        final Properties propsForDiff = new Properties();
        propsForDiff.setProperty("ProductHome", productHome);
        ((Hashtable<String, EnhancedFileFilter>)propsForDiff).put("ExcludeForCRC", this.getFilterForAction("ExcludeForCRC"));
        ((Hashtable<String, EnhancedFileFilter>)propsForDiff).put("ExcludeForDiff", this.getFilterForAction("ExcludeForDiff"));
        this.diffUtil = new ZipDiffUtil(bFile, pZip, propsForDiff);
        pZip.close();
        this.infHelper = new InfQueryHelper(productHome);
    }
    
    private void parseTheXML(final String productHome) throws Exception {
        final File file = new File(productHome, "Patch/DiffViewer/conf/QueryPatch.xml");
        this.confReader = new ConfReader(file);
    }
    
    private void extractFiles() {
        final Hashtable table = this.confReader.getFilesToExtract();
        final Enumeration en = table.keys();
        while (en.hasMoreElements()) {
            final String dirName = en.nextElement();
            final ArrayList list = table.get(dirName);
            try {
                JarFileUtil.extractJarFile(this.ppmFile, list, new File(this.productHome, dirName));
            }
            catch (final Exception e) {
                System.err.println("Exception while extracting files from the list : " + list);
                e.printStackTrace();
            }
        }
    }
    
    public String[] getBaseNodeIDs() {
        return this.confReader.getBaseNodeIDs();
    }
    
    public String getDisplayName(final String baseID) {
        return this.confReader.getDisplayName(baseID);
    }
    
    public DocumentNodeProps[] getDocumentNodeProps() {
        return this.confReader.getDocumentNodeProps();
    }
    
    public Hashtable getFileList(final String baseID) {
        final EnhancedFileFilter fileFilter = this.confReader.getFileFilter(baseID);
        ArrayList tempList = null;
        if (baseID.equals("ExceptionCases")) {
            tempList = this.getExceptionFiles();
        }
        else if (baseID.equals("ModifiedFiles")) {
            tempList = this.getModifiedFiles();
        }
        else if (baseID.equals("ReIntroducedFiles")) {
            tempList = this.getReIntroducedFiles();
        }
        else {
            if (!baseID.equals("NewFiles")) {
                System.err.println("Unknown baseID [ " + baseID + " ] specified.");
                return null;
            }
            tempList = this.getNewFiles();
        }
        this.baseIdVsFileList.put(baseID, tempList.clone());
        tempList = this.removePlatformSpecificFiles(tempList);
        final GroupingFunction gr = this.confReader.getGroupingFunction();
        final Hashtable ht = gr.groupElements(tempList);
        return ht;
    }
    
    private ArrayList getExceptionFiles() {
        if (this.baseIdVsFileList.get("ExceptionCases") != null) {
            return this.baseIdVsFileList.get("ExceptionCases");
        }
        final EnhancedFileFilter fileFilter = this.confReader.getFileFilter("ExceptionCases");
        ArrayList tempList = this.diffUtil.getModifiedFiles(fileFilter);
        tempList = this.infHelper.filterFilesBasedOnContext(tempList);
        return tempList;
    }
    
    private ArrayList getModifiedFiles() {
        if (this.baseIdVsFileList.get("ModifiedFiles") != null) {
            return this.baseIdVsFileList.get("ModifiedFiles");
        }
        final EnhancedFileFilter fileFilter = this.confReader.getFileFilter("ModifiedFiles");
        ArrayList tempList = this.diffUtil.getModifiedFiles(fileFilter);
        tempList.addAll(this.infHelper.getModifiedJars());
        tempList = this.infHelper.filterFilesBasedOnContext(tempList);
        tempList.removeAll(this.getExceptionFiles());
        return tempList;
    }
    
    private ArrayList getNewFiles() {
        if (this.baseIdVsFileList.get("NewFiles") != null) {
            return this.baseIdVsFileList.get("NewFiles");
        }
        ArrayList tempList = this.infHelper.getNewFiles();
        final EnhancedFileFilter fileFilter = this.confReader.getFileFilter("NewFiles");
        if (fileFilter != null) {
            final ArrayList list = new ArrayList();
            for (int size = tempList.size(), i = 0; i < size; ++i) {
                final String fileName = tempList.remove(0);
                if (fileFilter.accept(fileName)) {
                    list.add(fileName);
                }
            }
            tempList = list;
        }
        return tempList;
    }
    
    private ArrayList getReIntroducedFiles() {
        if (this.baseIdVsFileList.get("ReIntroducedFiles") != null) {
            return this.baseIdVsFileList.get("ReIntroducedFiles");
        }
        final EnhancedFileFilter fileFilter = this.confReader.getFileFilter("ReIntroducedFiles");
        ArrayList tempList = this.diffUtil.getNewFiles(fileFilter);
        tempList = this.infHelper.filterFilesBasedOnContext(tempList);
        tempList.removeAll(this.getNewFiles());
        tempList.removeAll(this.getModifiedFiles());
        return tempList;
    }
    
    private EnhancedFileFilter getFilterForAction(final String action) {
        final EnhancedFileFilter filter = new EnhancedFileFilter();
        if (action.equals("ExcludeForCRC")) {
            final Criteria crit = new Criteria();
            crit.addCriterion("*.class", "REJECT");
            filter.setCriteria(crit);
        }
        else if (action.equals("ExcludeForDiff")) {
            final Criteria crit = new Criteria();
            crit.addCriterion("Patch/", "REJECT");
            crit.addCriterion("*.class", "REJECT");
            filter.setCriteria(crit);
        }
        return filter;
    }
    
    private ArrayList removePlatformSpecificFiles(final ArrayList fileList) {
        final EnhancedFileFilter filter = new EnhancedFileFilter();
        final Criteria crit = new Criteria();
        final String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("windows") != -1) {
            crit.addCriterion("*.so", "REJECT");
            crit.addCriterion("*.sh", "REJECT");
        }
        else {
            crit.addCriterion("*.exe", "REJECT");
            crit.addCriterion("*.dll", "REJECT");
            crit.addCriterion("*.bat", "REJECT");
        }
        filter.setCriteria(crit);
        final int size = fileList.size();
        final ArrayList listToReturn = new ArrayList();
        for (int i = 0; i < size; ++i) {
            final String fName = fileList.remove(0);
            if (filter.accept(fName)) {
                listToReturn.add(fName);
            }
        }
        return listToReturn;
    }
    
    private void log(final String message) {
    }
}
