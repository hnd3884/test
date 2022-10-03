package com.adventnet.tools.update.installer;

import java.util.zip.ZipEntry;
import java.util.Vector;
import java.util.Hashtable;
import com.adventnet.tools.update.ZipFileGroup;
import com.adventnet.tools.update.FileGroup;
import com.adventnet.tools.update.UpdateData;
import java.util.zip.ZipFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import com.adventnet.tools.update.FeatureVersionComp;
import com.adventnet.tools.update.FeaturePrdVersionInfo;
import com.adventnet.tools.update.FeatureCompInfo;
import java.io.IOException;
import com.adventnet.tools.update.PatchesInfoHolder;
import java.util.List;
import com.adventnet.tools.update.XmlData;
import java.util.Map;
import java.nio.file.Path;
import com.zoho.tools.util.FileUtil;
import com.zoho.tools.util.UpgradeUtil;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import com.adventnet.tools.update.CommonUtil;
import java.util.logging.Level;
import java.awt.Component;
import javax.swing.JOptionPane;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.File;
import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ApplyPatch implements Runnable
{
    private static Logger out;
    private ArrayList selectedContext;
    private String dirToUnzip;
    private String versionDir;
    private Common common;
    private boolean GUI;
    private JFrame frame;
    private boolean showMessage;
    
    public ApplyPatch(final ArrayList context, final String dir, final String patchVersion, final Common common, final boolean GUI, final JFrame frame) {
        this.selectedContext = null;
        this.dirToUnzip = null;
        this.versionDir = null;
        this.common = null;
        this.GUI = false;
        this.frame = null;
        this.showMessage = true;
        this.selectedContext = context;
        this.dirToUnzip = dir;
        this.versionDir = patchVersion;
        this.common = common;
        this.GUI = GUI;
        this.frame = frame;
    }
    
    public ApplyPatch(final ArrayList context, final String dir, final String patchVersion, final Common common, final boolean GUI, final JFrame frame, final boolean showMessage) {
        this.selectedContext = null;
        this.dirToUnzip = null;
        this.versionDir = null;
        this.common = null;
        this.GUI = false;
        this.frame = null;
        this.showMessage = true;
        this.selectedContext = context;
        this.dirToUnzip = dir;
        this.versionDir = patchVersion;
        this.common = common;
        this.GUI = GUI;
        this.frame = frame;
        this.showMessage = showMessage;
    }
    
    public boolean isPatchAlreadyInstalled() {
        final String specsFile = this.dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsFile).exists()) {
            return false;
        }
        final VersionProfile verProfile = VersionProfile.getInstance();
        verProfile.readDocument(specsFile, false, false);
        if (verProfile.getRootElement() == null) {
            final String errMsg = "\"" + specsFile + "\" which holds already applied patch details is Empty.";
            if (this.GUI) {
                JOptionPane.showMessageDialog(UpdateManagerUtil.getParent(), errMsg, "Error", 0);
            }
            else {
                ConsoleOut.println("\n" + errMsg + "\n");
            }
            throw new RuntimeException(errMsg);
        }
        final int size = this.selectedContext.size();
        final int versionInt = verProfile.isVersionPresent(this.versionDir, specsFile);
        if (versionInt == 0) {
            return false;
        }
        final ArrayList contextToInstall = new ArrayList();
        for (int i = 0; i < size; ++i) {
            final String select = this.selectedContext.get(i);
            final int contextInt = verProfile.isContextPresent(this.versionDir, select, specsFile);
            if (contextInt == 6) {
                contextToInstall.add(select);
            }
        }
        this.selectedContext = contextToInstall;
        if (!contextToInstall.isEmpty()) {
            return false;
        }
        if (!this.GUI) {
            ApplyPatch.out.severe("This Service Pack is already installed");
            UpdateManagerUtil.setTaskStatus(false);
        }
        if (UpdateManager.getUpdateState() != null) {
            UpdateManager.getUpdateState().setErrorCode(900);
        }
        return true;
    }
    
    @Override
    public void run() {
        final String patchFile = this.common.getPatchFileNamePath();
        UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_IN_PROGRESS);
        UpdateManagerUtil.startInstallAnimation();
        long sizeCount = 0L;
        try {
            sizeCount = this.getSizeCount();
        }
        catch (final Exception exp) {
            ApplyPatch.out.log(Level.SEVERE, exp.getMessage(), exp);
            final String corruptMessage = CommonUtil.getString("The file may be corrupted.Download again.");
            ApplyPatch.out.severe("ERR:" + corruptMessage);
            this.abort(corruptMessage);
            return;
        }
        boolean discSpaceCheck = true;
        final String discProperty = System.getProperty("tools.discSpaceCheck");
        if (discProperty != null && discProperty.equalsIgnoreCase("false")) {
            discSpaceCheck = false;
        }
        if (discSpaceCheck) {
            final DiskSpace space = DiskSpace.getInstance();
            long freeSpace = 0L;
            if (this.dirToUnzip != null) {
                freeSpace = space.getFreeSpace(this.dirToUnzip);
            }
            else {
                freeSpace = space.getFreeSpace(UpdateManagerUtil.getHomeDirectory());
            }
            long unCompressedSize = sizeCount * 3L;
            final long patchSize = new File(patchFile).length();
            unCompressedSize += patchSize * 2L;
            if (unCompressedSize > freeSpace) {
                final String diskSpaceError = CommonUtil.getString("Not enough space available for installation of Service Pack");
                ApplyPatch.out.info(CommonUtil.getString("ERROR CODE") + " : " + 800);
                UpdateManager.getUpdateState().setErrorCode(800);
                ApplyPatch.out.severe("ERR:" + diskSpaceError);
                this.abort(diskSpaceError);
                return;
            }
        }
        final String homeDirectory = UpdateManagerUtil.getHomeDirectory();
        final String confPath = Paths.get(homeDirectory, "conf").toString();
        final Path instanceConfigPath = Paths.get(confPath, "um_instance.config");
        final Path keyStore = Paths.get(confPath, "manageengine.keystore");
        if (UpdateManager.getAlreadyCompletedPrePostClassName() != null) {
            try {
                UpdateManagerUtil.getInstanceConfig(instanceConfigPath.toString());
            }
            catch (final Exception e) {
                ApplyPatch.out.log(Level.SEVERE, "Problem while reading/generating instance config : " + e.getMessage(), e);
            }
            if (!Files.exists(keyStore, new LinkOption[0])) {
                UpdateManager.autoImportCertificate(homeDirectory);
            }
        }
        ApplyPatch.out.info("Patch Integrity verification is done again before installation to ensure PPM is not tampered.");
        final PatchIntegrityState patchIntegrityState = PatchIntegrityVerifier.verifyPatch(patchFile, UpdateManagerUtil.getHomeDirectory() + File.separator + "conf", UpdateManager.getInstanceConfig().getKeyStorePassword());
        if (patchIntegrityState != PatchIntegrityState.SUCCESS) {
            final String patchIntegrityMessage = patchIntegrityState.getMessage();
            ApplyPatch.out.severe(patchIntegrityMessage);
            this.abort(patchIntegrityMessage);
            return;
        }
        UpdateManagerUtil.setProgressCount(sizeCount);
        if (this.GUI) {
            UpdateManagerUtil.updateTheSizeInInstallUI(sizeCount);
        }
        if (!this.GUI && UpdateManager.getAlreadyCompletedPrePostClassName() == null) {
            ConsoleOut.println("\n" + CommonUtil.getString("Started to apply the patch.") + "\n");
        }
        ApplyPatch.out.info(CommonUtil.getString("The ppm file path : ") + patchFile);
        ApplyPatch.out.info(CommonUtil.getString("The Patch Version : ") + this.versionDir);
        final Map<String, String> userInfoMap = UpdateManagerUtil.getUserInformation();
        final String userInformation = userInfoMap.isEmpty() ? "No user Information available" : userInfoMap.toString();
        ApplyPatch.out.info(CommonUtil.getString("User Information :  ") + userInformation);
        for (int size = this.selectedContext.size(), i = 0; i < size; ++i) {
            final String s = this.selectedContext.get(i);
            ApplyPatch.out.info(CommonUtil.getString("Context Name") + " : " + s);
            final LoggingUtil logg = new LoggingUtil();
            try {
                final String dirName = this.dirToUnzip + File.separator + "Patch" + File.separator + "logs";
                final File t = new File(dirName);
                if (!t.isDirectory()) {
                    t.mkdir();
                }
                String logName = null;
                if (s.equals("NoContext")) {
                    logName = this.versionDir + "log.txt";
                }
                else {
                    logName = this.versionDir + s + "log.txt";
                }
                logg.init(dirName + File.separator + logName);
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
            UpdateManagerUtil.setState(16);
            UpgradeUtil.notifyStatus(Paths.get(patchFile, new String[0]), this.common.getXmlData(), PatchInstallationState.PATCH_INSTALLATION_STARTED);
            final boolean b = this.installPatch(s, logg);
            if (UpdateManager.getUpdateConfProperty("showstats") != null && "true".equalsIgnoreCase(UpdateManager.getUpdateConfProperty("showstats"))) {
                UpdateManager.getUpdateState().printStates();
            }
            try {
                new PreProcessor().cleanup();
            }
            catch (final Exception e3) {
                ApplyPatch.out.log(Level.FINE, "Cleaning up the tmp directory created for patch validation process got failed.", e3);
            }
            if (!b) {
                UpgradeUtil.notifyStatus(Paths.get(patchFile, new String[0]), this.common.getXmlData(), PatchInstallationState.PATCH_INSTALLATION_FAILED);
                UpdateManagerUtil.setExitStatus(1);
                return;
            }
            if (size == i + 1) {
                this.writeSpecsFile(s, sizeCount, true);
            }
            else {
                this.writeSpecsFile(s, sizeCount, false);
            }
        }
        try {
            final XmlData infXmlData = UpdateManagerUtil.getXmlData(Paths.get(this.dirToUnzip, "Patch", "inf.xml"));
            final List<String> cleanUpFiles = infXmlData.getCleanUpFiles();
            if (!cleanUpFiles.isEmpty()) {
                final String dirPath = Paths.get(this.dirToUnzip, new String[0]).normalize().toString();
                final List<String> serverFolders = new ArrayList<String>();
                final List<String> tempFiles = new ArrayList<String>();
                serverFolders.add(Paths.get(dirPath, "bin").toString());
                serverFolders.add(Paths.get(dirPath, "conf").toString());
                serverFolders.add(Paths.get(dirPath, "lib").toString());
                serverFolders.add(Paths.get(dirPath, "webapps").toString());
                for (int j = 0; j < cleanUpFiles.size(); ++j) {
                    final Path tempFilePath = Paths.get(dirPath, cleanUpFiles.get(j)).normalize();
                    final String tempString = tempFilePath.toString();
                    if (!serverFolders.contains(tempString)) {
                        if (!tempString.startsWith(new File(dirPath).getCanonicalPath())) {
                            ApplyPatch.out.log(Level.INFO, "The path {0} was outside server home, so deletion is restricted", tempString);
                        }
                        else {
                            tempFiles.add(tempString);
                        }
                    }
                    else {
                        ApplyPatch.out.log(Level.INFO, "Skipping the deletion of directory {0} because it should not be deleted", cleanUpFiles.get(j));
                    }
                }
                FileUtil.deleteFiles((String[])tempFiles.toArray(new String[0]));
            }
        }
        catch (final Exception exc) {
            ApplyPatch.out.log(Level.SEVERE, "Exception in deleting the user defined tempFiles present in inf.xml file after installation", exc);
        }
        ApplyPatch.out.info(CommonUtil.getString("Service Pack installed successfully"));
        final String toUpgrdPrdtName = this.common.getXmlData().getProductName();
        final String toUpgrdPrdtVersion = this.common.getXmlData().getProductVersion();
        ApplyPatch.out.log(Level.INFO, "Service pack [ {0} ] installed successfully and upgraded to \"{1} ({2})\".", new Object[] { this.versionDir, toUpgrdPrdtName, toUpgrdPrdtVersion });
        if (this.GUI) {
            UpdateManagerUtil.updateTheInstallUI();
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.startInstallCompletionAnimation();
            UpdateManagerUtil.clearInstallProgress(true);
            UpdateManagerUtil.setInstallCompletedMessage(CommonUtil.getString("Service Pack installed successfully"), " ");
            if (!UpdateManagerUtil.autoCloseOnSuccessfulCompletion()) {
                UpdateManagerUtil.enableTheInstallUIButton();
            }
            UpdateManagerUtil.setExitStatus(0);
        }
        else {
            ConsoleOut.println("\n\n" + CommonUtil.getString("Service Pack installed successfully"));
            UpdateManagerUtil.setExitStatus(0);
            UpdateManagerUtil.setTaskStatus(true);
        }
        if (!UpdateManagerUtil.autoCloseOnSuccessfulCompletion()) {
            if (!this.common.getXmlData().getAutoClose()) {
                return;
            }
        }
        try {
            if (UpdateManagerUtil.autoCloseOnSuccessfulCompletion()) {
                final long milliseconds = UpdateManagerUtil.getAutoCloseDelayTimeInSeconds() * 1000L;
                Thread.sleep(milliseconds);
            }
            else {
                Thread.sleep(this.common.getXmlData().getAutoCloseDelay());
            }
        }
        catch (final InterruptedException e4) {
            ApplyPatch.out.log(Level.SEVERE, e4.getMessage(), e4);
        }
        if (this.GUI) {
            if (UpdateManager.isInvokedForAutoApplyOfPatches()) {
                final InstallUI intall = (InstallUI)InstallProgress.getInstance().getTopLevelAncestor();
                intall.setVisible(false);
            }
            else {
                UpdateManager.getUi().setVisible(false);
            }
        }
    }
    
    public boolean installPatch(final String contextName, final LoggingUtil logg) {
        Unzipper unzipper = null;
        try {
            final String currentPrdtName = this.common.getConfProductName();
            final String currentPrdtVersion = this.common.getConfProductVersion();
            final String toUpgrdPrdtName = this.common.getXmlData().getProductName();
            final String toUpgrdPrdtVersion = this.common.getXmlData().getProductVersion();
            ApplyPatch.out.log(Level.INFO, "Going to upgrade from \"{0} ({1})\" to \"{2} ({3})\".", new Object[] { currentPrdtName, currentPrdtVersion, toUpgrdPrdtName, toUpgrdPrdtVersion });
            final String patchFileName = Paths.get(this.common.getPatchFileNamePath(), new String[0]).getFileName().toString();
            final boolean isHotSwappablePatch = PatchesInfoHolder.isHotSwappablePatch(patchFileName);
            unzipper = new Unzipper(this.common, contextName, this.GUI, logg);
            UpdateManager.getUpdateState().setCurrentState(12, System.currentTimeMillis());
            if (!unzipper.initializeUnzipper()) {
                UpdateManager.getUpdateState().setErrorCode(300);
                ApplyPatch.out.info(CommonUtil.getString("ERROR CODE") + " : " + 300);
                ApplyPatch.out.info(CommonUtil.getString("ERROR IN ") + "  : " + CommonUtil.getString("File backup"));
                UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
                UpdateManagerUtil.setDefaultCursor();
                return false;
            }
            if (!isHotSwappablePatch) {
                UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.PRE_PROCESSOR_EXECUTION_STARTED);
                UpdateManager.getUpdateState().setCurrentState(10, System.currentTimeMillis());
                final boolean preBoolean = unzipper.invokePreInstallationClasses();
                UpdateManagerUtil.clearInstallProgress(false);
                if (!preBoolean) {
                    UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.PRE_PROCESSOR_EXECUTION_FAILED);
                    UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
                    UpdateManagerUtil.setDefaultCursor();
                    return false;
                }
                UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.PRE_PROCESSOR_EXECUTION_COMPLETED);
            }
            UpdateManager.getUpdateState().setCurrentState(11, System.currentTimeMillis());
            unzipper.extractBaseEEARFiles();
            UpdateManager.getUpdateState().setCurrentState(12, System.currentTimeMillis());
            UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.FILE_BACKUP_STARTED);
            if (!unzipper.backupFiles()) {
                UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.FILE_BACKUP_FAILED);
                UpdateManager.getUpdateState().setErrorCode(300);
                ApplyPatch.out.info(CommonUtil.getString("ERROR CODE") + " : " + 300);
                ApplyPatch.out.info(CommonUtil.getString("ERROR IN") + "   : " + CommonUtil.getString("Back up"));
                return false;
            }
            UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.FILE_BACKUP_COMPLETED);
            UpdateManager.getUpdateState().setCurrentState(13, System.currentTimeMillis());
            UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.FILE_UPDATE_STARTED);
            final boolean corrupt = unzipper.startUnzipping();
            if (!corrupt) {
                UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.FILE_UPDATE_FAILED);
                UpdateManager.getUpdateState().setErrorCode(400);
                ApplyPatch.out.info(CommonUtil.getString("ERROR CODE") + " : " + 400);
                ApplyPatch.out.info(CommonUtil.getString("ERROR IN") + "   : " + CommonUtil.getString("File Update"));
                return false;
            }
            UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.FILE_UPDATE_COMPLETED);
            if (!isHotSwappablePatch) {
                UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.POST_PROCESSOR_EXECUTION_STARTED);
                UpdateManager.getUpdateState().setCurrentState(14, System.currentTimeMillis());
                final boolean postBoolean = unzipper.invokePostInstallationClasses();
                UpdateManagerUtil.clearInstallProgress(false);
                if (!postBoolean) {
                    UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.POST_PROCESSOR_EXECUTION_FAILED);
                    UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
                    UpdateManagerUtil.setDefaultCursor();
                    return false;
                }
                UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.POST_PROCESSOR_EXECUTION_COMPLETED);
            }
            else {
                unzipper.copyPPMFile();
            }
            UpdateManager.getUpdateState().setCurrentState(15, System.currentTimeMillis());
            unzipper.compressUpdatedEEARFiles();
            try {
                FileUtil.deleteFiles(Paths.get(this.dirToUnzip, "patchtemp").toString(), Paths.get(this.dirToUnzip, "eeartemp").toString());
            }
            catch (final RuntimeException | IOException rte) {
                ApplyPatch.out.log(Level.SEVERE, "Ignoring the problem, while deleting the patchtemp/eeartemp directory.", rte);
            }
            UpdateManager.cleanupUnWantedInfo();
            UpgradeUtil.notifyStatus(Paths.get(this.common.getPatchFileNamePath(), new String[0]), this.common.getXmlData(), PatchInstallationState.PATCH_INSTALLATION_COMPLETED);
            return true;
        }
        finally {
            if (unzipper != null) {
                unzipper.closeZipFile();
            }
        }
    }
    
    private void writeSpecsFile(final String selected, final long sizeCount, final boolean detailsToWrite) {
        final XmlData xmlData = this.common.getXmlData();
        String displayName = xmlData.getDisplayName();
        final String patchVersion = xmlData.getPatchVersion();
        if (displayName == null) {
            displayName = patchVersion;
        }
        String patchType = xmlData.getPatchType();
        if (patchType == null || patchType.equals("")) {
            patchType = "SP";
        }
        String patchContentType = xmlData.getPatchContentType();
        if (patchContentType == null) {
            patchContentType = "Consolidated";
        }
        final String feature = xmlData.getFeatureName();
        final String patchDescription = xmlData.getPatchDescription();
        String patchFileName = this.common.getPatchFileNamePath();
        patchFileName = patchFileName.substring(patchFileName.lastIndexOf(File.separator) + 1);
        String compPatchOption = null;
        String compPatchVersion = null;
        ArrayList compFeatureList = null;
        final ArrayList fcomp = xmlData.getFeatureCompatibility();
        if (fcomp != null && !fcomp.isEmpty()) {
            for (int a = 0; a < fcomp.size(); ++a) {
                final FeatureCompInfo fci = fcomp.get(a);
                if (fci.getProductName().equals(this.common.getConfProductName())) {
                    final Object[] obj = fci.getPrdVersionInfo();
                    if (obj != null && obj.length > 0) {
                        for (final Object o : obj) {
                            final FeaturePrdVersionInfo fpvi = (FeaturePrdVersionInfo)o;
                            if (fpvi.getProductVersion().equals(this.common.getConfProductVersion())) {
                                final FeatureVersionComp fvc = fpvi.getFeatureVersionComp();
                                if (fvc != null) {
                                    compPatchVersion = fvc.getCompPatchVersion();
                                    compPatchOption = fvc.getCompPatchOption();
                                    if (patchType.equals("FP")) {
                                        final String[] verr = fvc.getVersions();
                                        for (int b = 0; b < verr.length; b += 3) {
                                            final String featureName = verr[b];
                                            final String featureOption = verr[b + 1];
                                            final String featureValue = verr[b + 2];
                                            if (compFeatureList == null) {
                                                compFeatureList = new ArrayList();
                                            }
                                            compFeatureList.add(featureName);
                                            compFeatureList.add(featureOption);
                                            compFeatureList.add(featureValue);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        long fileSize = 0L;
        if (detailsToWrite) {
            fileSize = sizeCount;
        }
        else {
            fileSize = 0L;
        }
        final String patchSize = String.valueOf(fileSize);
        final String DATE_FORMAT1 = "dd MMMMM yyyy";
        final String date1 = this.getTheFormatedDate(DATE_FORMAT1);
        final String DATE_FORMAT2 = "h:mm a";
        final String date2 = this.getTheFormatedDate(DATE_FORMAT2);
        final String date3 = date1 + " " + date2;
        final String[] detailsArray = { "Size", patchSize, "Date", date3, "Description", patchDescription, "PatchName", patchFileName, "DisplayName", displayName, "Type", patchType, "ContentType", patchContentType, "FeatureName", feature };
        final String specsFile = this.dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile verProfile = VersionProfile.getInstance();
        if (new File(specsFile).exists()) {
            verProfile.readDocument(specsFile, false, false);
            final int isPresent = verProfile.addContext(this.versionDir, selected, specsFile, detailsArray);
            if (isPresent == 0) {
                verProfile.addVersion(this.versionDir, selected, specsFile, detailsArray, patchType, compPatchVersion, compPatchOption, compFeatureList);
            }
        }
        else {
            verProfile.createDocument(specsFile, this.versionDir, selected, detailsArray, patchType, compPatchVersion, compPatchOption, compFeatureList);
        }
    }
    
    private String getTheFormatedDate(final String format) {
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getDefault());
        final String date = sdf.format(cal.getTime());
        return date;
    }
    
    public long getSizeCount() throws Exception {
        long sizeCount = 0L;
        ZipFile ppmZipFile = null;
        try {
            final String fileName = this.common.getPatchFileNamePath();
            final File tempFile = new File(fileName);
            ppmZipFile = new ZipFile(tempFile);
            final Hashtable hash = UpdateManagerUtil.getXmlData(Paths.get(this.dirToUnzip, "Patch", "inf.xml")).getContextTable();
            for (int i = 0; i < this.selectedContext.size(); ++i) {
                final String selContext = this.selectedContext.get(i);
                final UpdateData updateData = hash.get(selContext);
                final Vector fileGrpVector = updateData.getContextVector();
                final int size = fileGrpVector.size();
                final ArrayList zipGrpVector = updateData.getZipFileGroup();
                final int s = zipGrpVector.size();
                for (int j = 0; j < size; ++j) {
                    final FileGroup filgrp = fileGrpVector.elementAt(j);
                    final Vector fgFileNames = filgrp.getFileNameVector();
                    final Vector jarFileNames = filgrp.getJarNameVector();
                    if (jarFileNames.size() == 0) {
                        for (int q = 0; q < fgFileNames.size(); ++q) {
                            final String jfileName = fgFileNames.elementAt(q);
                            final ZipEntry zipFileEntry = ppmZipFile.getEntry(jfileName);
                            if (!zipFileEntry.isDirectory()) {
                                sizeCount += zipFileEntry.getSize();
                            }
                        }
                    }
                    else {
                        for (int a = 0; a < jarFileNames.size(); ++a) {
                            for (int fileCount = 1, x = 0; x < fileCount; ++x) {
                                final String fgFileName = fgFileNames.elementAt(x);
                                final ZipEntry zipFileEntry = ppmZipFile.getEntry(fgFileName);
                                sizeCount += zipFileEntry.getSize();
                            }
                        }
                    }
                }
                for (int y = 0; y < s; ++y) {
                    final ZipFileGroup zipfgFileNames = zipGrpVector.get(y);
                    final ArrayList zipFileNames = zipfgFileNames.getFilesList();
                    for (int z = 0; z < zipFileNames.size(); ++z) {
                        final String zfileName = zipFileNames.get(z);
                        final ZipEntry zipFileEntry = ppmZipFile.getEntry(zfileName);
                        if (!zipFileEntry.isDirectory()) {
                            sizeCount += zipFileEntry.getSize();
                        }
                    }
                }
            }
        }
        finally {
            if (ppmZipFile != null) {
                ppmZipFile.close();
            }
        }
        return sizeCount;
    }
    
    public ArrayList getSelectedContext() {
        return this.selectedContext;
    }
    
    private void abort(final String message) {
        if (this.GUI) {
            UpdateManagerUtil.setInstallState(UpdateManagerUtil.INSTALL_COMPLETED);
            UpdateManagerUtil.setInstallCorruptMainLabelMessage(message, " ");
            UpdateManagerUtil.setDefaultCursor();
        }
        else {
            ConsoleOut.println(message);
        }
        UpdateManagerUtil.setExitStatus(1);
    }
    
    static {
        ApplyPatch.out = Logger.getLogger(ApplyPatch.class.getName());
    }
}
