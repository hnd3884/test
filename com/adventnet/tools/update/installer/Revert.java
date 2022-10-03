package com.adventnet.tools.update.installer;

import java.util.Vector;
import java.util.Hashtable;
import com.adventnet.tools.update.FileGroup;
import com.adventnet.tools.update.UpdateData;
import com.adventnet.tools.update.XmlParser;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import com.zoho.tools.util.UpgradeUtil;
import java.nio.file.Paths;
import java.awt.Component;
import javax.swing.JOptionPane;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.File;
import com.adventnet.tools.update.CommonUtil;
import com.adventnet.tools.update.XmlData;
import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Revert implements Runnable
{
    private static Logger out;
    private ArrayList revertList;
    private Common common;
    private boolean GUI;
    private JFrame frame;
    private XmlData infXmlData;
    
    public Revert(final Common common, final ArrayList revertVersions, final boolean GUI, final JFrame frame) {
        this.revertList = null;
        this.common = null;
        this.GUI = false;
        this.frame = null;
        this.common = common;
        this.revertList = revertVersions;
        this.GUI = GUI;
        this.frame = frame;
    }
    
    public void doReverting() {
        final RevertPatch revertVersion = new RevertPatch(this.common, this.GUI, false);
        final String dirToUnzip = this.common.getInstallationDirectory();
        Revert.out.info(CommonUtil.getString("Patch Uninstallation started"));
        if (!dirToUnzip.equals("")) {
            final String specsFile = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
            final File specsInf = new File(specsFile);
            if (specsInf.isFile()) {
                UpdateManagerUtil.setRevertState(1);
                UpdateManagerUtil.startRevertAnimation();
                final VersionProfile vprofile = VersionProfile.getInstance();
                vprofile.readDocument(specsFile, false, false);
                for (int revertSize = this.revertList.size(), i = 0; i < revertSize; ++i) {
                    final String rversion = this.revertList.get(i);
                    Revert.out.info(CommonUtil.getString("Uninstalling version:") + rversion);
                    final Map<String, String> userInfoMap = UpdateManagerUtil.getUserInformation();
                    final String userInformation = userInfoMap.isEmpty() ? "No user Information available" : userInfoMap.toString();
                    Revert.out.info(CommonUtil.getString("User Information :  ") + userInformation);
                    String displayName = vprofile.getTheAdditionalDetail(rversion, "DisplayName");
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = vprofile.getTheAdditionalDetail(rversion, "Name");
                    }
                    final String msg = "Going to uninstall the patch :: " + displayName;
                    if (!this.GUI) {
                        ConsoleOut.println("\n" + ((i > 0) ? "\n" : "") + msg);
                    }
                    else if (revertSize > 1) {
                        JOptionPane.showMessageDialog(UpdateManagerUtil.getParent(), msg, "", 1);
                    }
                    final String patchName = vprofile.getTheAdditionalDetail(rversion, "PatchName");
                    final Path patchFilePath = Paths.get(dirToUnzip, "Patch", patchName);
                    final File patchFile = patchFilePath.toFile();
                    UpgradeUtil.notifyStatus(patchFile.toPath(), this.infXmlData, PatchInstallationState.PATCH_REVERT_STARTED);
                    final String[] contextList = vprofile.getTheContext(rversion);
                    int j;
                    for (int conLength = j = contextList.length; j > 0; --j) {
                        final LoggingUtil logg = new LoggingUtil();
                        final String rcontext = contextList[j - 1];
                        Revert.out.info(CommonUtil.getString("Context Name:") + rcontext);
                        try {
                            final String dirName = dirToUnzip + File.separator + "Patch" + File.separator + "logs";
                            final File t = new File(dirName);
                            if (!t.isDirectory()) {
                                t.mkdir();
                            }
                            String logName = null;
                            if (rcontext.equals("NoContext")) {
                                logName = rversion + "Rlog.txt";
                            }
                            else {
                                logName = rversion + rcontext + "Rlog.txt";
                            }
                            logg.init(dirName + File.separator + logName);
                        }
                        catch (final Exception e) {
                            Revert.out.log(Level.SEVERE, "ERR:" + CommonUtil.getString("Exception while uninstalling"), e);
                        }
                        final boolean fileCorrupt = revertVersion.readInfoFile(rversion, rcontext, logg);
                        if (!fileCorrupt) {
                            final String errMsg = CommonUtil.getString("Unexpected error.The directory may be corrupted.");
                            Revert.out.severe("ERR:" + errMsg);
                            if (this.GUI) {
                                UpdateManagerUtil.setRevertState(2);
                                UpdateManagerUtil.setRevertCorruptMainLabelMessage(errMsg, " ");
                            }
                            else {
                                ConsoleOut.println(errMsg);
                            }
                            UpdateManagerUtil.setExitStatus(1);
                            return;
                        }
                        UpdateManager.getUpdateState().setCurrentState(11, System.currentTimeMillis());
                        revertVersion.extractEEARForReverting();
                        UpdateManager.getUpdateState().setCurrentState(14, System.currentTimeMillis());
                        final boolean postBoolean = revertVersion.revertPostInvocationClasses();
                        if (!postBoolean) {
                            return;
                        }
                        UpdateManagerUtil.clearRevertProgress(false);
                        UpdateManager.getUpdateState().setCurrentState(13, System.currentTimeMillis());
                        revertVersion.startReverting();
                        UpdateManager.getUpdateState().setCurrentState(10, System.currentTimeMillis());
                        final boolean preBoolean = revertVersion.revertPreInvocationClasses();
                        if (!preBoolean) {
                            return;
                        }
                        UpdateManagerUtil.clearRevertProgress(false);
                        UpdateManager.getUpdateState().setCurrentState(15, System.currentTimeMillis());
                        revertVersion.compressEEARAfterReverting();
                        revertVersion.deleteBackupDir();
                    }
                    UpgradeUtil.notifyStatus(patchFilePath, this.infXmlData, PatchInstallationState.PATCH_REVERT_COMPLETED);
                    String patchType = vprofile.getTheAdditionalDetail(rversion, "Type");
                    if (patchType == null) {
                        patchType = "SP";
                    }
                    if (patchFile.exists()) {
                        patchFile.delete();
                    }
                    revertVersion.deleteDir(new File(dirToUnzip + File.separator + "Patch" + File.separator + rversion));
                    vprofile.removeVersion(rversion, specsFile, patchType);
                }
                final String uim = CommonUtil.getString("Uninstalled successfully");
                Revert.out.info(uim);
                if (this.GUI) {
                    UpdateManagerUtil.clearRevertProgress(true);
                    UpdateManagerUtil.setRevertState(2);
                    UpdateManagerUtil.startUnInstallCompletionAnimation();
                    UpdateManagerUtil.setRevertCompletedMessage(uim, " ");
                    UpdateManagerUtil.updateTheUI();
                    UpdateManagerUtil.setExitStatus(0);
                }
                else {
                    ConsoleOut.println("\n\n" + uim);
                    UpdateManagerUtil.setExitStatus(0);
                    UpdateManagerUtil.setTaskStatus(true);
                }
            }
        }
    }
    
    @Override
    public void run() {
        long count = 0L;
        try {
            count = this.getSizeCount();
        }
        catch (final Exception exp) {
            final String corruptMessage = CommonUtil.getString("Problem in uninstallation.");
            Revert.out.log(Level.SEVERE, corruptMessage, exp);
            if (this.GUI) {
                UpdateManagerUtil.setRevertState(2);
                UpdateManagerUtil.setRevertCorruptMainLabelMessage(corruptMessage, " ");
            }
            else {
                ConsoleOut.println(corruptMessage);
            }
            UpdateManagerUtil.setExitStatus(1);
            return;
        }
        UpdateManagerUtil.setProgressCount(count);
        UpdateManagerUtil.setState(17);
        this.doReverting();
        if (UpdateManager.getUpdateConfProperty("showstats") != null && "true".equalsIgnoreCase(UpdateManager.getUpdateConfProperty("showstats"))) {
            UpdateManager.getUpdateState().printStates();
        }
    }
    
    public long getSizeCount() throws Exception {
        long sizeCount = 0L;
        final String dirToUnzip = this.common.getInstallationDirectory();
        final String specsFile = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final File specsInf = new File(specsFile);
        if (specsInf.isFile()) {
            final VersionProfile vprofile = VersionProfile.getInstance();
            vprofile.readDocument(specsFile, false, false);
            for (int versionList = this.revertList.size(), i = 0; i < versionList; ++i) {
                final String versionDir = this.revertList.get(i);
                final String dirName = dirToUnzip + File.separator + "Patch" + File.separator + versionDir;
                final String temp = dirName + File.separator + "inf.xml";
                final String[] contextList = vprofile.getTheContext(versionDir);
                if (new File(temp).exists()) {
                    final XmlParser xmlParser = new XmlParser(temp);
                    this.infXmlData = xmlParser.getXmlData();
                    for (final String contextDir : contextList) {
                        String contextDirectory = null;
                        if (contextDir.equals("NoContext")) {
                            contextDirectory = dirName;
                        }
                        else {
                            contextDirectory = dirName + File.separator + contextDir;
                        }
                        final Hashtable hashTable = this.infXmlData.getContextTable();
                        final UpdateData updateData = hashTable.get(contextDir);
                        final Vector fileGrpVector = updateData.getContextVector();
                        for (int size = fileGrpVector.size(), k = 0; k < size; ++k) {
                            final FileGroup filgrp = fileGrpVector.elementAt(k);
                            final Vector fgFileNames = filgrp.getFileNameVector();
                            final Vector jarFileNames = filgrp.getJarNameVector();
                            if (jarFileNames.size() == 0) {
                                for (int s = fgFileNames.size(), q = 0; q < s; ++q) {
                                    String name = fgFileNames.elementAt(q);
                                    name = CommonUtil.convertfilenameToOsFilename(name);
                                    if (name.startsWith(contextDir)) {
                                        name = name.substring(contextDir.length() + 1);
                                    }
                                    final File file = new File(contextDirectory + File.separator + name);
                                    if (file.exists()) {
                                        sizeCount += file.length();
                                    }
                                    else {
                                        sizeCount += 200L;
                                    }
                                }
                            }
                            else {
                                for (int jarSize = jarFileNames.size(), a = 0; a < jarSize; ++a) {
                                    for (int fileCount = 1, x = 0; x < fileCount; ++x) {
                                        String fgFileName = fgFileNames.elementAt(x);
                                        fgFileName = CommonUtil.convertfilenameToOsFilename(fgFileName);
                                        if (fgFileName.startsWith(contextDir)) {
                                            fgFileName = fgFileName.substring(contextDir.length() + 1);
                                        }
                                        if (fgFileName.endsWith(".ujar")) {
                                            fgFileName = fgFileName.substring(0, fgFileName.lastIndexOf(".ujar")) + ".jar";
                                        }
                                        final File jarFile = new File(contextDirectory + File.separator + fgFileName);
                                        if (jarFile.exists()) {
                                            sizeCount += jarFile.length();
                                        }
                                        else {
                                            sizeCount += 200L;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return sizeCount;
    }
    
    static {
        Revert.out = Logger.getLogger(Revert.class.getName());
    }
}
