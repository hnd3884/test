package com.adventnet.tools.update.installer;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import com.adventnet.tools.update.XmlData;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.Collections;
import com.zoho.tools.util.UpgradeUtil;
import java.text.MessageFormat;
import com.adventnet.tools.update.CommonUtil;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.adventnet.tools.update.PatchesInfoHolder;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.Collection;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.util.ArrayList;
import java.util.logging.Logger;

public class AutoApplyPatches
{
    private static final Logger OUT;
    
    public void applyPatches(PatchesCompatibilityVerifier pcv) {
        List<String> patchesToBeAppliedInOrder = new ArrayList<String>(1);
        pcv = ((pcv != null) ? pcv : new ContainerPatchesCompatibilityVerifier());
        final Path patchesFolderPath = Paths.get(UpdateManagerUtil.getHomeDirectory(), "patches");
        if (Files.isDirectory(patchesFolderPath, new LinkOption[0])) {
            String[] patchFilesList = patchesFolderPath.toFile().list(UpdateManagerUtil.PPMFILEFILTER);
            List<String> patchFiles = new ArrayList<String>();
            if (patchFilesList != null) {
                patchFiles.addAll(Arrays.asList(patchFilesList));
            }
            if (!patchFiles.isEmpty()) {
                for (final String patchFile : patchFiles) {
                    final Path patchFilePath = Paths.get(patchesFolderPath.toString(), patchFile);
                    if (UpdateManager.isPatchAppliedSuccessfully(patchFilePath)) {
                        this.deleteFile(patchFilePath);
                    }
                }
            }
            if (UpdateManager.isInvokedFromAutoUpgrade()) {
                patchFiles = new ArrayList<String>();
                patchFilesList = patchesFolderPath.toFile().list(UpdateManagerUtil.PPMFILEFILTER);
                if (patchFilesList != null) {
                    patchFiles.addAll(Arrays.asList(patchFilesList));
                }
                for (final String patchName : patchFiles) {
                    try {
                        final Path patchFilePath = Paths.get(patchesFolderPath.toString(), patchName);
                        if (!UpdateManagerUtil.isContainerOfPatches(patchFilePath.toString())) {
                            continue;
                        }
                        Unzipper.extractPatchesFromPatch(patchFilePath.toString(), UpdateManagerUtil.getHomeDirectory());
                    }
                    catch (final Exception ioe) {
                        AutoApplyPatches.OUT.log(Level.SEVERE, ioe.getMessage(), ioe);
                    }
                }
            }
            patchFiles = new ArrayList<String>();
            patchFilesList = patchesFolderPath.toFile().list(UpdateManagerUtil.PPMFILEFILTER);
            if (patchFilesList != null) {
                patchFiles.addAll(Arrays.asList(patchFilesList));
            }
            if (!patchFiles.isEmpty()) {
                patchesToBeAppliedInOrder = pcv.getPatchesOrdered(patchesFolderPath, patchFiles);
                if (patchesToBeAppliedInOrder == null || patchesToBeAppliedInOrder.isEmpty()) {
                    AutoApplyPatches.OUT.severe("PatchesCompatibilityVerifier returned null or empty list of patches.");
                }
                else {
                    AutoApplyPatches.OUT.log(Level.INFO, "Patches going to be applied in the following order :: {0}", patchesToBeAppliedInOrder);
                    final List<String> patchFilesCloned = new ArrayList<String>(patchFiles);
                    for (final String patchFile2 : patchFiles) {
                        if (patchesToBeAppliedInOrder.contains(patchFile2)) {
                            patchFilesCloned.remove(patchFile2);
                        }
                    }
                    patchFiles = patchFilesCloned;
                }
                if (!patchFiles.isEmpty()) {
                    this.moveIncompatiblePatches(patchFiles);
                }
            }
            else {
                AutoApplyPatches.OUT.info("No patch files available to be installed.");
            }
        }
        this.clearIncompatiblePatches();
        if (patchesToBeAppliedInOrder != null && !patchesToBeAppliedInOrder.isEmpty()) {
            try {
                final List<Path> pathOfPatchFilesInOrder = new ArrayList<Path>(patchesToBeAppliedInOrder.size());
                for (final String patchFileName : patchesToBeAppliedInOrder) {
                    pathOfPatchFilesInOrder.add(patchesFolderPath.resolve(patchFileName));
                }
                for (final Path patchFilePath2 : pathOfPatchFilesInOrder) {
                    if (UpdateManager.getAlreadyCompletedPrePostClassName() == null) {
                        pcv.verifyPatch(patchFilePath2);
                    }
                    final boolean isHotSwappablePatch = pcv.isHotSwappablePatch(patchFilePath2);
                    PatchesInfoHolder.addPatchInfo(patchFilePath2.getFileName().toString(), isHotSwappablePatch);
                }
            }
            catch (final Throwable t) {
                AutoApplyPatches.OUT.log(Level.SEVERE, "Problem while loading class to verify whether the Patches got tampered or not", t);
                throw new IllegalArgumentException(t);
            }
            this.applyPatches(patchesFolderPath, patchesToBeAppliedInOrder);
        }
    }
    
    private void cleanUp(final Path patchesFolderPath) {
        if (Files.exists(patchesFolderPath.resolve("patches.cp"), new LinkOption[0])) {
            this.deleteFile(patchesFolderPath.resolve("patches.cp"));
        }
    }
    
    private void deleteFile(final Path patchFilePath) {
        try {
            Files.delete(patchFilePath);
            AutoApplyPatches.OUT.log(Level.INFO, "File : {0} deleted successfully", patchFilePath);
        }
        catch (final IOException ioe) {
            AutoApplyPatches.OUT.severe("Problem while deleting the already successfully applied patch file :: " + patchFilePath + ". \n" + ioe.getMessage());
            AutoApplyPatches.OUT.info("Marking the patch file \"" + patchFilePath + "\" for deleteOnExit.");
            patchFilePath.toFile().deleteOnExit();
        }
    }
    
    private void moveIncompatiblePatches(final List<String> patchesList) {
        final Path patchesFolderPath = Paths.get(UpdateManagerUtil.getHomeDirectory(), "patches");
        final Path inCompatiblePatchesFolderPath = Paths.get(UpdateManagerUtil.getHomeDirectory(), "incompatible_patches");
        if (!Files.exists(inCompatiblePatchesFolderPath, new LinkOption[0])) {
            AutoApplyPatches.OUT.log(Level.INFO, "Directory {0} created, status {1}.", new Object[] { inCompatiblePatchesFolderPath, inCompatiblePatchesFolderPath.toFile().mkdir() });
        }
        try {
            for (final String patchFile : patchesList) {
                final Path source = patchesFolderPath.resolve(patchFile);
                final Path target = inCompatiblePatchesFolderPath.resolve(patchFile);
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                AutoApplyPatches.OUT.log(Level.INFO, "File {0} moved successfully to destination {1}.", new Object[] { source, target });
            }
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    private void clearIncompatiblePatches() {
        try {
            final Path incompatible_patches_path = Paths.get(UpdateManagerUtil.getHomeDirectory(), "incompatible_patches");
            if (incompatible_patches_path.toFile().exists()) {
                AutoApplyPatches.OUT.info("Cleaning up incompatible_patches folder content.");
                CommonUtil.deleteFiles(incompatible_patches_path.toString());
            }
        }
        catch (final Exception e) {
            AutoApplyPatches.OUT.log(Level.SEVERE, "Problem while clearing the incompatible_patches folder content is ignored.", e);
        }
        try {
            final Path patchesFolderPath = Paths.get(UpdateManagerUtil.getHomeDirectory(), "patches");
            final String[] patchFilesList = patchesFolderPath.toFile().list();
            if (Files.isDirectory(patchesFolderPath, new LinkOption[0]) && patchFilesList != null && patchFilesList.length == 0) {
                Files.delete(patchesFolderPath);
            }
        }
        catch (final IOException ex) {
            AutoApplyPatches.OUT.log(Level.SEVERE, "Problem while clearing empty patches folder.", ex);
        }
    }
    
    private void applyPatches(final Path patchesFolderPath, final List<String> patchFilesInOrder) {
        if (UpdateManager.getAlreadyCompletedPrePostClassName() == null) {
            String messageContent = "There {0} available to be applied. Going to apply {1}";
            if (patchFilesInOrder.size() == 1) {
                messageContent = MessageFormat.format(messageContent, " is a patch file ", " it.");
            }
            else {
                messageContent = MessageFormat.format(messageContent, "are " + patchFilesInOrder.size() + " patches", " them one by one. ");
            }
            messageContent = CommonUtil.getString(messageContent);
            UpgradeUtil.showMessageOnClient(messageContent, 0);
        }
        UpdateManager.setIsInvokedFromAutoApplyOfPatches(true);
        int totalPatches = UpdateManager.getAlreadyAppliedPatchesCount();
        for (int i = 0; i < patchFilesInOrder.size(); ++i) {
            final String patchFileName = patchFilesInOrder.get(i);
            final Path patchFilePath = Paths.get(patchesFolderPath.toString(), patchFileName);
            if (!UpdateManager.isPatchAppliedSuccessfully(patchFilePath)) {
                ++totalPatches;
            }
        }
        for (int i = 0; i < patchFilesInOrder.size(); ++i) {
            if (i < patchFilesInOrder.size() - 1) {
                UpdateManagerUtil.setAutoCloseOnSuccessfulCompletion(true);
            }
            else {
                UpdateManagerUtil.setAutoCloseOnSuccessfulCompletion(UpdateManager.isInvokedFromAutoUpgrade());
                if (UpdateManager.isGUI()) {
                    UpdateManagerUtil.setAutoCloseOnSuccessfulCompletion(false);
                }
            }
            final String patchFileName = patchFilesInOrder.get(i);
            final Path patchFilePath = Paths.get(patchesFolderPath.toString(), patchFileName);
            if (!UpdateManager.isPatchAppliedSuccessfully(patchFilePath)) {
                AutoApplyPatches.OUT.info("Going to apply the patch :: " + patchFileName);
                if (UpdateManager.getAlreadyCompletedPrePostClassName() == null && patchFilesInOrder.size() > 1) {
                    String messageContent2 = "Going to apply patch " + (UpdateManager.getSuccessfullyAppliedPatchesCount() + 1) + " out of " + totalPatches;
                    messageContent2 = CommonUtil.getString(messageContent2);
                    UpgradeUtil.showMessageOnClient(messageContent2, 0);
                }
                this.applyThisPatch(patchFilePath.toString());
                if (UpdateManager.getUpdateState().getCurrentPrePostClassInProgress() != null && UpdateManager.getUpdateState().getCurrentState() == 10 && UpdateManager.getUpdateState().getErrorCode() == 0) {
                    return;
                }
                AutoApplyPatches.OUT.info("Checking whether patch applied successfully to clean it up.");
                if (UpdateManager.isPatchAppliedSuccessfully(patchFilePath)) {
                    UpdateManager.incrementSuccessfullyAppliedPatchesCount();
                    this.deleteFile(patchFilePath);
                }
                else {
                    AutoApplyPatches.OUT.info("Patch not applied successfully.");
                    this.writeToErrFile();
                    this.cleanUp(patchesFolderPath);
                    this.moveIncompatiblePatches(Collections.singletonList(patchFileName));
                    if (UpdateManager.isGUI()) {
                        return;
                    }
                    System.exit(1);
                }
            }
        }
        AutoApplyPatches.OUT.info("\n\nSuccessfully applied all the patches. \n\nTotally applied patches count :: " + UpdateManager.getSuccessfullyAppliedPatchesCount());
        this.clearIncompatiblePatches();
        this.cleanUp(patchesFolderPath);
    }
    
    private void writeToErrFile() {
        final File errFile = new File(UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "ppm.err");
        if (errFile.exists()) {
            AutoApplyPatches.OUT.log(Level.INFO, "Existing ppm.err file deletion status : {0}.", errFile.delete());
        }
        try {
            AutoApplyPatches.OUT.log(Level.INFO, "ppm.err file creation status : {0}.", errFile.createNewFile());
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(errFile.toString(), true))) {
                writer.append((CharSequence)"Error occurred when applying PPM, UpdateManager exiting with error code : ").append((CharSequence)String.valueOf(UpdateManager.getUpdateState().getErrorCode()));
                writer.append((CharSequence)System.lineSeparator());
                writer.append((CharSequence)"Please refer Upgrade logs for more information");
            }
        }
        catch (final IOException e) {
            AutoApplyPatches.OUT.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    private void applyThisPatch(final String patchFilePath) {
        UpdateManager.i18N();
        UpdateManager.resetUpdateState();
        if (!UpdateManager.isGUI()) {
            if (UpdateManager.isInvokedFromAutoUpgrade()) {
                UpdateManager.setPatchPath(patchFilePath);
            }
            AutoApplyPatches.OUT.info(CommonUtil.getString("Invoking UpdateManager AutoApply in CMD mode"));
            final XmlData infXmlData = UpdateManagerUtil.getInfXmlData(patchFilePath);
            final ArrayList contexts = new ArrayList();
            final Hashtable contextTable = infXmlData.getContextTable();
            final Enumeration keys = contextTable.keys();
            while (keys.hasMoreElements()) {
                if (keys.nextElement().equals("NoContext")) {
                    contexts.add("NoContext");
                }
            }
            if (contexts.isEmpty()) {
                contexts.add(UpdateManager.getSubProductName("conf"));
            }
            final UpdateManagerCMD comm = new UpdateManagerCMD(UpdateManager.getProductName("conf"), UpdateManager.getSubProductName("conf"), UpdateManager.isPreValidationRequired());
            UpdateManagerUtil.setCMDPatchPath(patchFilePath);
            comm.installPatch(UpdateManagerUtil.getHomeDirectory(), patchFilePath, contexts);
        }
        else {
            AutoApplyPatches.OUT.info(CommonUtil.getString("Invoking UpdateManager AutoApply in UI mode"));
            UpdateManagerUI ui = UpdateManager.getUi();
            if (ui == null) {
                ui = new UpdateManagerUI(UpdateManager.getProductName("conf"), UpdateManager.getSubProductName("conf"), true);
                ui.init();
                ui.setPPMPath(patchFilePath);
                ui.showUI(true);
            }
            if (ui.validateTheFile(patchFilePath, UpdateManager.isPreValidationRequired())) {
                ui.callActionPerformed(patchFilePath);
            }
        }
    }
    
    static {
        OUT = Logger.getLogger(AutoApplyPatches.class.getName());
    }
    
    private static class ContainerPatchesCompatibilityVerifier implements PatchesCompatibilityVerifier
    {
        @Override
        public void verifyPatch(final Path patchPath) {
        }
        
        @Override
        public boolean isHotSwappablePatch(final Path patchPath) {
            return false;
        }
        
        @Override
        public List<String> getPatchesOrdered(final Path patchesFolderPath, final List<String> patches) {
            final List<String> patchInfoList = new ArrayList<String>();
            final File file = new File(patchesFolderPath + File.separator + "patches.cp");
            if (!file.exists()) {
                return patchInfoList;
            }
            try (final FileInputStream fileIn = new FileInputStream(file);
                 final ObjectInputStream in = new ObjectInputStream(fileIn)) {
                final List<PatchInfo> patchInfos = (List<PatchInfo>)in.readObject();
                for (final PatchInfo patchInfo : patchInfos) {
                    patchInfoList.add(patchInfo.getFileName());
                }
            }
            catch (final ClassNotFoundException | IOException e) {
                Logger.getLogger(ContainerPatchesCompatibilityVerifier.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
            final List<String> patchInfoListCloned = new ArrayList<String>(patchInfoList);
            for (final String fileName : patchInfoListCloned) {
                if (!patches.contains(fileName)) {
                    patchInfoList.remove(fileName);
                }
            }
            return patchInfoList;
        }
    }
}
