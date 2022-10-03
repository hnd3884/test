package com.adventnet.tools.update.viewer;

import com.adventnet.tools.update.installer.Common;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.util.Hashtable;
import java.util.Properties;
import javax.swing.JFrame;

public class DiffUtility
{
    private String productHome;
    private String baseDirStructure;
    private String ppmFile;
    private String pversion;
    private String pname;
    private JFrame frame;
    PatchQueryTool pqt;
    
    public DiffUtility(final String phome, final String ppm, final String bDir, final String pname, final String pv, final JFrame jf) {
        this.productHome = ".";
        this.baseDirStructure = "AdventNet/WebNMS";
        this.ppmFile = null;
        this.pversion = null;
        this.pname = null;
        this.frame = null;
        this.pqt = null;
        this.productHome = phome;
        this.ppmFile = ppm;
        this.pname = pname;
        this.pversion = pv;
        this.frame = jf;
    }
    
    public DiffUtility(final JFrame jf, final String pname, final String pversion, final String subprod, final String patchFile) {
        this.productHome = ".";
        this.baseDirStructure = "AdventNet/WebNMS";
        this.ppmFile = null;
        this.pversion = null;
        this.pname = null;
        this.frame = null;
        this.pqt = null;
        this.productHome = getHomeDir();
        this.ppmFile = patchFile;
        this.pname = pname;
        this.pversion = pversion;
        this.frame = jf;
        if (this.validatePPM(patchFile, this.productHome, pname, pversion, this.frame)) {
            final ViewDialog view = new ViewDialog(jf, this.productHome, pname, pversion, subprod, patchFile, this);
        }
    }
    
    public String[] getBaseNodeIDs() {
        final Properties props = new Properties();
        String[] baseNodes = null;
        try {
            this.pqt = new PatchQueryTool(this.productHome, this.ppmFile, props);
            baseNodes = this.pqt.getBaseNodeIDs();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return baseNodes;
    }
    
    public Hashtable getDisplayVector(final String[] nodeids) {
        final Hashtable mapHash = new Hashtable();
        for (int dscount = 0; dscount < nodeids.length; ++dscount) {
            final String node = this.pqt.getDisplayName(nodeids[dscount]);
            mapHash.put(nodeids[dscount], node);
        }
        return mapHash;
    }
    
    public Hashtable getFileList(final String node) {
        return this.pqt.getFileList(node);
    }
    
    public static String getHomeDir() {
        return UpdateManagerUtil.getHomeDirectory();
    }
    
    public void cleanResource() {
        this.pqt = null;
    }
    
    public DocumentNodeProps[] getDocumentNodes() {
        final DocumentNodeProps[] nodeProps = this.pqt.getDocumentNodeProps();
        return nodeProps;
    }
    
    private boolean validatePPM(final String ppmf, final String productHome, final String pname, final String pversion, final JFrame frame) {
        final String[] files = { "Patch/DiffViewer/conf/QueryPatch.xml" };
        final Common cm = new Common(productHome, ppmf, true, pname);
        return cm.validateFile(frame, files);
    }
    
    public String getDisplayName(final String name) {
        return this.pqt.getDisplayName(name);
    }
}
