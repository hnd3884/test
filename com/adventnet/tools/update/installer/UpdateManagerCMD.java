package com.adventnet.tools.update.installer;

import java.io.IOException;
import com.zoho.tools.WordUtils;
import java.util.HashMap;
import java.nio.file.Path;
import com.zoho.tools.CertificateUtil;
import java.util.logging.Level;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.Vector;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.util.ArrayList;
import javax.swing.JFrame;
import java.io.File;
import com.adventnet.tools.update.CommonUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class UpdateManagerCMD
{
    private static Logger out;
    private String confProductName;
    private String confProductVersion;
    private String confSubProductName;
    private boolean isPreValidationRequired;
    
    public UpdateManagerCMD(final String confProductName, final String confSubProductName, final boolean isPreValidationRequired) {
        this.confProductName = confProductName;
        this.confProductVersion = UpdateManager.getProductVersion(UpdateManager.getUpdateConfPath());
        this.confSubProductName = confSubProductName;
        this.isPreValidationRequired = isPreValidationRequired;
    }
    
    static String getInput() {
        String inpStr = "";
        try {
            final BufferedReader bufred = new BufferedReader(new InputStreamReader(System.in));
            inpStr = bufred.readLine().trim();
        }
        catch (final Exception e) {
            System.err.println(e.toString());
        }
        return inpStr;
    }
    
    private void displayInstalledServicePack(final String insDir) {
        String dirToUnzip = null;
        if (insDir == null) {
            ConsoleOut.print("\n" + CommonUtil.getString("Enter the product installation directory:"));
            dirToUnzip = getInput();
        }
        else {
            dirToUnzip = insDir;
        }
        final File dir = new File(dirToUnzip);
        if (!dir.exists()) {
            ConsoleOut.println("\n" + CommonUtil.getString("The installation directory doesn't exists"));
            return;
        }
        final String specsPath = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final File sFile = new File(specsPath);
        if (!sFile.exists()) {
            ConsoleOut.println("\n" + CommonUtil.getString("No ServicePack is installed"));
            this.continueCheck();
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] versionArray = vProfile.getAllVersions();
        if (versionArray == null) {
            ConsoleOut.println("\n" + CommonUtil.getString("No ServicePack is installed"));
            this.continueCheck();
        }
        ConsoleOut.println("\n" + CommonUtil.getString("Installed ServicePack(s)"));
        int i;
        for (int size = i = versionArray.length; i > 0; --i) {
            final String version = versionArray[i - 1];
            final String desc = vProfile.getTheAdditionalDetail(version, "Description");
            String displayName = vProfile.getTheAdditionalDetail(version, "DisplayName");
            if (displayName == null || displayName.trim().equals("")) {
                displayName = version;
            }
            ConsoleOut.println("\n " + displayName + " [" + desc.trim() + "]");
            final String[] array = vProfile.getTheContext(version);
            if (array != null) {
                for (final String cont : array) {
                    if (!cont.equals(this.confSubProductName)) {
                        ConsoleOut.println("   |_______" + cont);
                    }
                }
            }
        }
    }
    
    private void revert(final String dirToUnzip) {
        final File dir = new File(dirToUnzip);
        if (!dir.exists()) {
            ConsoleOut.println("\n" + CommonUtil.getString("The directory that you have specified doesnot exist."));
            this.continueCheck();
        }
        final String specsPath = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final File sFile = new File(specsPath);
        if (!sFile.exists()) {
            ConsoleOut.println("\n" + CommonUtil.getString("No Service Pack is installed"));
            this.continueCheck();
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] vect = vProfile.getAllVersions();
        if (vect == null) {
            ConsoleOut.println("\n" + CommonUtil.getString("No Service Pack is installed"));
            this.continueCheck();
        }
        final ArrayList list = this.getTheVersionToRevert(vect, vProfile);
        if (list == null) {
            return;
        }
        final Common common = new Common(dirToUnzip, "", false, this.confProductName);
        final Revert revertVersion = new Revert(common, list, false, null);
        try {
            final Thread revertThread = new Thread(revertVersion);
            revertThread.start();
            revertThread.join();
            this.continueCheck();
        }
        catch (final Exception ex) {}
    }
    
    private ArrayList getTheVersionToRevert(final String[] v, final VersionProfile vProfile) {
        final String[] vect = v;
        ArrayList list = null;
        final int size = vect.length;
        boolean correct = false;
        while (!correct) {
            ConsoleOut.println("\n" + CommonUtil.getString("Uninstall "));
            ConsoleOut.println("\n" + CommonUtil.getString("Note: Selected Service Pack version and its dependent resources will be automatically removed."));
            ConsoleOut.println("\n" + CommonUtil.getString("Installed Service Pack(s)"));
            int j = 1;
            for (int i = size; i > 0; --i) {
                final String version = vect[i - 1];
                String displayName = vProfile.getTheAdditionalDetail(version, "DisplayName");
                if (displayName == null || displayName.trim().equals("")) {
                    displayName = version;
                }
                ConsoleOut.println("\n " + j + " " + displayName);
                ++j;
            }
            ConsoleOut.print("\n" + CommonUtil.getString("Enter the number from the above list to uninstall the corresponding Service Pack:"));
            try {
                final String selected = getInput();
                final int sel = Integer.parseInt(selected);
                final String ds = vProfile.getTheAdditionalDetail(vect[size - sel], "DisplayName");
                list = UpdateManagerUtil.getTheListToUninstall(ds, sel - 1, false, null, false, false);
                correct = true;
            }
            catch (final Exception e) {
                list = null;
                ConsoleOut.println(CommonUtil.getString("Invalid entry"));
            }
        }
        return list;
    }
    
    private ArrayList getTheContext(final Vector v) {
        final ArrayList list = new ArrayList();
        final Vector vec = v;
        final int size = vec.size();
        boolean correct = false;
        while (!correct) {
            ConsoleOut.println("\n" + CommonUtil.getString("INSTALL"));
            ConsoleOut.println("\n" + CommonUtil.getString("To install single upgrade    Usage: 1"));
            ConsoleOut.println("\n" + CommonUtil.getString("To install multiple upgrades Usage: 1,2"));
            for (int i = 0; i < size; ++i) {
                ConsoleOut.println("\n " + (i + 1) + " " + vec.elementAt(i));
            }
            ConsoleOut.print("\n" + CommonUtil.getString("Enter the number from the above list to install the corresponding upgrade:"));
            final String selected = getInput();
            if (!selected.equals("")) {
                try {
                    final StringTokenizer stoken = new StringTokenizer(selected, ",");
                    while (stoken.hasMoreTokens()) {
                        final Object obj = vec.elementAt(Integer.parseInt(stoken.nextToken()) - 1);
                        if (!list.contains(obj)) {
                            list.add(obj);
                        }
                    }
                    correct = true;
                }
                catch (final Exception e) {
                    ConsoleOut.println(CommonUtil.getString("Invalid entry"));
                }
            }
        }
        return list;
    }
    
    public void installPatch(final String dirToUnzip, final String ppmPath, final ArrayList context) {
        final Common common = new Common(dirToUnzip, ppmPath, false, this.confProductName, this.isPreValidationRequired);
        if (common.install(null)) {
            if (this.applyContainerOfPatches(ppmPath)) {
                return;
            }
            final Vector v = common.getTheContext();
            if (v.contains(this.confSubProductName)) {
                final ArrayList alist = context;
                final String patchVersion = common.getPatchVersion();
                final ApplyPatch apply = new ApplyPatch(alist, dirToUnzip, patchVersion, common, false, null, false);
                if (!apply.isPatchAlreadyInstalled()) {
                    this.startInstalling(apply, false, false);
                }
            }
            else {
                ConsoleOut.println(CommonUtil.getString("The selected Service Pack does not contains Mandatory upgrade"));
            }
        }
    }
    
    public void commandLineInstall() {
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        final boolean isDeploymentToolEnabled = UpdateManagerUtil.isDeploymentToolEnabled();
        String outPutStr = "\n" + CommonUtil.getString("Press i to  Install") + "\n      ";
        if (UpdateManagerUtil.getAllowUninstalltion()) {
            outPutStr = outPutStr + CommonUtil.getString("u to  Uninstall") + "\n      ";
        }
        outPutStr = outPutStr + CommonUtil.getString("c to  Import Certificate") + "\n      ";
        outPutStr = outPutStr + CommonUtil.getString("v to  View installed ServicePack versions") + "\n      " + CommonUtil.getString("e to  Exit") + "\n" + CommonUtil.getString("Choose an Option:");
        ConsoleOut.print(outPutStr);
        final String input = getInput();
        if (input.equalsIgnoreCase(CommonUtil.getString("i"))) {
            ConsoleOut.print("\n" + CommonUtil.getString("Enter the patch file to install:"));
            final String patchPath = getInput();
            UpdateManagerUtil.setCMDPatchPath(patchPath);
            UpdateManager.setPatchPath(patchPath);
            if (this.applyContainerOfPatches(patchPath)) {
                this.commandLineInstall();
            }
            else {
                final Common common = new Common(dirToUnzip, patchPath, false, this.confProductName, this.isPreValidationRequired);
                final String homeDirectory = UpdateManagerUtil.getHomeDirectory();
                final String confPath = Paths.get(homeDirectory, "conf").toString();
                final Path instanceConfigPath = Paths.get(confPath, "um_instance.config");
                final Path keyStore = Paths.get(confPath, "manageengine.keystore");
                if (UpdateManager.getAlreadyCompletedPrePostClassName() != null) {
                    if (!Files.exists(instanceConfigPath, new LinkOption[0])) {
                        try {
                            UpdateManagerUtil.getInstanceConfig(instanceConfigPath.toString());
                        }
                        catch (final Exception e) {
                            UpdateManagerCMD.out.log(Level.SEVERE, "Problem while reading/generating instance config : " + e.getMessage(), e);
                        }
                    }
                    if (!Files.exists(keyStore, new LinkOption[0])) {
                        UpdateManager.autoImportCertificate(homeDirectory);
                        if (!CertificateUtil.isKeyStoreExists(confPath) || PatchIntegrityVerifier.verifyPatch(patchPath, confPath, UpdateManager.getInstanceConfig().getKeyStorePassword()) == PatchIntegrityState.SIGNATURE_DOES_NOT_MATCH) {
                            this.importCertificate(homeDirectory);
                        }
                    }
                }
                if (common.install(null)) {
                    final HashMap hashList = common.getTheCompatibleContext(this.confSubProductName);
                    ArrayList alist = null;
                    final String patchVersion = common.getPatchVersion();
                    final Vector v = common.getTheContext();
                    if (v.contains(this.confSubProductName)) {
                        if (hashList.isEmpty()) {
                            alist = new ArrayList();
                            alist.add(this.confSubProductName);
                        }
                        else {
                            alist = this.getTheOptionalContext(hashList, dirToUnzip, patchVersion);
                        }
                    }
                    else if (v.contains("NoContext")) {
                        alist = new ArrayList();
                        alist.add("NoContext");
                    }
                    else {
                        alist = this.getTheContext(v);
                    }
                    final ApplyPatch apply = new ApplyPatch(alist, dirToUnzip, patchVersion, common, false, null);
                    if (!apply.isPatchAlreadyInstalled()) {
                        boolean continueToApplyAnotherPatch = true;
                        if (UpdateManagerUtil.autoCloseOnSuccessfulCompletion() || common.getXmlData().getAutoClose()) {
                            continueToApplyAnotherPatch = false;
                        }
                        this.startInstalling(apply, continueToApplyAnotherPatch);
                    }
                    else {
                        ConsoleOut.println("\n" + CommonUtil.getString("This Service Pack is already installed"));
                        this.continueCheck();
                    }
                }
                else {
                    ConsoleOut.println("\n" + CommonUtil.getString("Please re-try ..."));
                    this.commandLineInstall();
                }
            }
        }
        else if (input.equalsIgnoreCase(CommonUtil.getString("u"))) {
            if (UpdateManagerUtil.getAllowUninstalltion()) {
                this.revert(dirToUnzip);
            }
            else {
                ConsoleOut.println("\n" + CommonUtil.getString("Uninstalltion of patch not supported"));
                this.continueCheck();
            }
        }
        else if (input.equalsIgnoreCase(CommonUtil.getString("v"))) {
            this.displayInstalledServicePack(dirToUnzip);
            this.continueCheck();
        }
        else if (input.equalsIgnoreCase(CommonUtil.getString("c"))) {
            this.importCertificate(dirToUnzip);
            this.commandLineInstall();
        }
        else if (input.equalsIgnoreCase(CommonUtil.getString("e"))) {
            System.exit(0);
        }
        else {
            ConsoleOut.println("\n" + CommonUtil.getString("Wrong input. Please re-try ..."));
            this.commandLineInstall();
        }
    }
    
    public static void getConsentForSelfSignedCertificates(final String context) {
        ConsoleOut.println("\n");
        ConsoleOut.println(new String(new char[60]).replace("\u0000", "*"));
        if (context == null) {
            ConsoleOut.println(CommonUtil.getString(MessageConstants.SELF_SIGNED_CERTIFICATE_WARNING_TITLE).toUpperCase());
        }
        else if (context.equals("certificate")) {
            ConsoleOut.println(CommonUtil.getString(MessageConstants.SELF_SIGNED_CERTIFICATE_WARNING_TITLE).toUpperCase());
        }
        else {
            ConsoleOut.println(CommonUtil.getString(MessageConstants.SELF_SIGNED_PATCH_WARNING_TITLE).toUpperCase());
        }
        ConsoleOut.println(CommonUtil.getString(MessageConstants.SELF_SIGNED_WARNING_HEADER).toUpperCase());
        ConsoleOut.println(new String(new char[60]).replace("\u0000", "*"));
        final String importSelfSignedCert = context.equals("patch") ? CommonUtil.getString(MessageConstants.SELF_SIGNED_WARNING_MESSAGE) : CommonUtil.getString(MessageConstants.IMPORT_SELF_SIGNED_CERTIFICATE);
        ConsoleOut.println(WordUtils.wrap(importSelfSignedCert.replace(System.lineSeparator(), ""), 60));
        ConsoleOut.println("\n");
        ConsoleOut.println(CommonUtil.getString(MessageConstants.GET_CONSENT1_INPUT));
        String input = getInput();
        if (input.equalsIgnoreCase("a")) {
            try {
                UpdateManagerUtil.audit("Pressed Advanced option (a) for L1 consent - self signed " + context);
            }
            catch (final IOException e) {
                UpdateManagerCMD.out.log(Level.SEVERE, e.getMessage(), e);
            }
            ConsoleOut.println("\n");
            ConsoleOut.println(WordUtils.wrap(CommonUtil.getString(MessageConstants.ACCEPT_RISKS_MESSAGE), 60));
            ConsoleOut.println("\n");
            ConsoleOut.println(CommonUtil.getString(MessageConstants.GET_CONSENT2_INPUT));
            input = getInput();
            if (input.equalsIgnoreCase("p")) {
                try {
                    UpdateManagerUtil.audit("Pressed Proceed option (p) for L2 consent - self signed " + context);
                }
                catch (final IOException e) {
                    UpdateManagerCMD.out.log(Level.SEVERE, e.getMessage(), e);
                }
                ConsoleOut.println(new String(new char[60]).replace("\u0000", "*"));
                ConsoleOut.println("\n");
            }
            else {
                try {
                    UpdateManagerUtil.audit("Pressed option (" + input + ") for L2 consent - self signed " + context);
                    UpdateManagerUtil.audit("Rejecting consent for Self Signed " + context);
                }
                catch (final IOException e) {
                    UpdateManagerCMD.out.log(Level.SEVERE, e.getMessage(), e);
                }
                ConsoleOut.println(new String(new char[60]).replace("\u0000", "*"));
                System.exit(0);
            }
        }
        else {
            try {
                UpdateManagerUtil.audit("Pressed option (" + input + ") for L1 consent - self signed " + context);
                UpdateManagerUtil.audit("Rejecting consent for Self Signed " + context);
            }
            catch (final IOException e) {
                UpdateManagerCMD.out.log(Level.SEVERE, e.getMessage(), e);
            }
            ConsoleOut.println(new String(new char[60]).replace("\u0000", "*"));
            System.exit(0);
        }
    }
    
    private void continueCheck() {
        ConsoleOut.println("\n" + CommonUtil.getString("Do you want to continue ?"));
        ConsoleOut.print("\n" + CommonUtil.getString("Yes(y) No(n)"));
        boolean correct = false;
        while (!correct) {
            final String in = getInput();
            if (in.equalsIgnoreCase(CommonUtil.getString("y")) || in.equalsIgnoreCase(CommonUtil.getString("yes"))) {
                this.commandLineInstall();
                correct = true;
            }
            else if (in.equalsIgnoreCase(CommonUtil.getString("n")) || in.equalsIgnoreCase(CommonUtil.getString("no"))) {
                System.exit(0);
            }
            else {
                ConsoleOut.print("\n" + CommonUtil.getString("Enter Yes(y) No(n)"));
            }
        }
    }
    
    private void startInstalling(final ApplyPatch apply, final boolean recursive) {
        this.startInstalling(apply, recursive, true);
    }
    
    private void startInstalling(final ApplyPatch apply, final boolean recursive, final boolean exit) {
        try {
            final Thread installThread = new Thread(apply);
            installThread.start();
            installThread.join();
            if (recursive) {
                this.continueCheck();
            }
            else if (exit) {
                final int status = UpdateManagerUtil.getExitStatus();
                if (status == 0) {
                    if (System.getProperty("IsWebUpdate") == null) {
                        System.exit(0);
                    }
                    else {
                        UpdateManagerUtil.setTaskStatus(true);
                    }
                }
                else if (System.getProperty("IsWebUpdate") == null) {
                    System.exit(2);
                }
                else {
                    UpdateManagerUtil.setTaskStatus(false);
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    private ArrayList getTheOptionalContext(final HashMap v, final String dirToUnzip, final String versionDir) {
        final ArrayList list = new ArrayList();
        list.add(this.confSubProductName);
        final HashMap vec = v;
        Object[] hashContext = vec.keySet().toArray();
        int size = hashContext.length;
        final String specsPath = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        boolean isAllInstalled = false;
        if (new File(specsPath).exists()) {
            final VersionProfile vProfile = VersionProfile.getInstance();
            vProfile.readDocument(specsPath, false, false);
            for (int j = 0; j < size; ++j) {
                final String context = (String)hashContext[j];
                final int conPresent = vProfile.isContextPresent(versionDir, context, specsPath);
                if (conPresent != 1) {
                    isAllInstalled = false;
                    break;
                }
                vec.remove(context);
                isAllInstalled = true;
            }
        }
        hashContext = vec.keySet().toArray();
        size = hashContext.length;
        if (isAllInstalled) {
            return list;
        }
        boolean correct = false;
        while (!correct) {
            ConsoleOut.println("\n" + CommonUtil.getString("Do you want to install the optional upgrades ?"));
            ConsoleOut.print("\n" + CommonUtil.getString("Yes(y) No(n)") + "\n" + CommonUtil.getString("Enter your choice:"));
            final String input = getInput();
            if (input.equalsIgnoreCase(CommonUtil.getString("y")) || input.equalsIgnoreCase(CommonUtil.getString("yes"))) {
                ConsoleOut.println("\n" + CommonUtil.getString("The optional upgrades and their description"));
                int k = 0;
                for (int i = size; i > 0; --i) {
                    final String key = (String)hashContext[i - 1];
                    ConsoleOut.println("\n " + (k + 1) + " " + key + " [ " + vec.get(key) + " ] ");
                    ++k;
                }
                ConsoleOut.println("\n" + CommonUtil.getString("Note :  To install single upgrade Usage : 1") + "\n\t" + CommonUtil.getString("To install multiple upgrades Usage:1,2"));
                ConsoleOut.print("\n" + CommonUtil.getString("\nEnter the number from the above list to install the corresponding upgrade:"));
                final String selected = getInput();
                if (selected.equals("")) {
                    continue;
                }
                try {
                    final StringTokenizer stoken = new StringTokenizer(selected, ",");
                    while (stoken.hasMoreTokens()) {
                        final Object obj = hashContext[size - Integer.parseInt(stoken.nextToken())];
                        if (!list.contains(obj)) {
                            list.add(obj);
                        }
                    }
                    correct = true;
                }
                catch (final Exception e) {
                    ConsoleOut.println(CommonUtil.getString("Invalid entry"));
                }
            }
            else {
                if (input.equalsIgnoreCase(CommonUtil.getString("n")) || input.equalsIgnoreCase(CommonUtil.getString("no"))) {
                    return list;
                }
                ConsoleOut.println("\n" + CommonUtil.getString("Wrong input. Please re-try ..."));
            }
        }
        return list;
    }
    
    public void cmdInstallProcesss(final String dirToUnzip, final String option, final String patch, final String version) {
        if (option.equals("i") && UpdateManager.getAlreadyCompletedPrePostClassName() != null && (!CertificateUtil.isKeyStoreExists(dirToUnzip + File.separator + "conf") || PatchIntegrityVerifier.verifyPatch(patch, UpdateManagerUtil.getHomeDirectory() + File.separator + "conf", UpdateManager.getInstanceConfig().getKeyStorePassword()) == PatchIntegrityState.SIGNATURE_DOES_NOT_MATCH)) {
            this.importCertificate(dirToUnzip);
        }
        this.cmdInstallProcesss(dirToUnzip, option, patch, version, true);
    }
    
    public void cmdInstallProcesss(final String dirToUnzip, final String option, final String patch, final String version, final boolean displayNameAsVersion) {
        UpdateManagerCMD.out.info("The product directory:" + dirToUnzip);
        final File dir = new File(dirToUnzip);
        if (!dir.exists()) {
            UpdateManagerCMD.out.severe("The product directory doesnot exists");
            ConsoleOut.println("\n" + CommonUtil.getString("The directory that you have specified doesnot exist."));
            return;
        }
        final String input = option;
        final String patchPath = patch;
        String spVersion = version;
        if (displayNameAsVersion) {
            spVersion = UpdateManagerUtil.getOriginalVersion(spVersion);
        }
        if (input.equalsIgnoreCase("i")) {
            if (this.applyContainerOfPatches(patchPath)) {
                return;
            }
            final Common common = new Common(dirToUnzip, patchPath, false, this.confProductName, this.isPreValidationRequired);
            if (common.install(null)) {
                final HashMap hashList = common.getTheCompatibleContext(this.confSubProductName);
                ArrayList alist = null;
                final String patchVersion = common.getPatchVersion();
                final Vector v = common.getTheContext();
                if (v.contains(this.confSubProductName)) {
                    if (hashList.isEmpty()) {
                        alist = new ArrayList();
                        alist.add(this.confSubProductName);
                    }
                    else {
                        alist = this.getTheOptionalContext(hashList, dirToUnzip, patchVersion);
                    }
                }
                else if (v.contains("NoContext")) {
                    alist = new ArrayList();
                    alist.add("NoContext");
                }
                else {
                    alist = this.getTheContext(v);
                }
                final ApplyPatch apply = new ApplyPatch(alist, dirToUnzip, patchVersion, common, false, null);
                if (!apply.isPatchAlreadyInstalled()) {
                    boolean continueToApplyAnotherPatch = true;
                    if (UpdateManagerUtil.autoCloseOnSuccessfulCompletion() || common.getXmlData().getAutoClose()) {
                        continueToApplyAnotherPatch = false;
                    }
                    this.startInstalling(apply, continueToApplyAnotherPatch);
                }
            }
        }
        else if (input.equalsIgnoreCase("u")) {
            final String dispMess = CommonUtil.getString("No Service Pack is installed");
            final String specsPath = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
            final File sFile = new File(specsPath);
            if (!sFile.exists()) {
                UpdateManagerCMD.out.severe("ERR:" + dispMess);
                ConsoleOut.println("\n" + dispMess);
                UpdateManagerUtil.setTaskStatus(false);
                return;
            }
            final VersionProfile vProfile = VersionProfile.getInstance();
            vProfile.readDocument(specsPath, false, false);
            final String[] vect = vProfile.getAllVersions();
            if (vect == null) {
                UpdateManagerCMD.out.severe("ERR:" + dispMess);
                ConsoleOut.println("\n" + dispMess);
                return;
            }
            final int length = vect.length;
            int versionAt = -1;
            ArrayList list = new ArrayList();
            int k = 0;
            for (int i = length; i > 0; --i) {
                final String ver = vect[i - 1];
                if (ver.equals(spVersion)) {
                    versionAt = k + 1;
                    break;
                }
                ++k;
            }
            if (versionAt != -1) {
                final String ds = vProfile.getTheAdditionalDetail(vect[length - versionAt], "DisplayName");
                list = UpdateManagerUtil.getTheListToUninstall(ds, versionAt - 1, false, null, false, true);
            }
            if (list == null) {
                return;
            }
            if (list.isEmpty()) {
                UpdateManagerCMD.out.severe("ERR:" + CommonUtil.getString("The specified Service Pack version is not installed"));
                ConsoleOut.println("\n" + CommonUtil.getString("The specified Service Pack version is not installed"));
                return;
            }
            final Common common2 = new Common(dirToUnzip, "", false, this.confProductName);
            final Revert revertVersion = new Revert(common2, list, false, null);
            try {
                final Thread revertThread = new Thread(revertVersion);
                revertThread.start();
                revertThread.join();
            }
            catch (final Exception ex) {}
        }
        else if (input.equalsIgnoreCase("v")) {
            UpdateManagerCMD.out.info("View installed ServicePack versions");
            this.displayInstalledServicePack(dirToUnzip);
        }
        else {
            if (!input.equalsIgnoreCase("e")) {
                UpdateManagerCMD.out.info("Wrong input given");
                ConsoleOut.println("\n" + CommonUtil.getString("Wrong input."));
                return;
            }
            UpdateManagerCMD.out.info("Exiting UpdateManager");
            System.exit(0);
        }
    }
    
    public void cmdImportCertificateProcesss(final String homeDir, final String importCertFile) {
        if (!new File(importCertFile).getName().endsWith("crt") && !new File(importCertFile).getName().endsWith("cer")) {
            ConsoleOut.println(CommonUtil.getString(MessageConstants.INVALID_CERTIFICATE_FILE_EXTENSION));
            return;
        }
        if (!new File(importCertFile).exists()) {
            ConsoleOut.println(CommonUtil.getString(MessageConstants.CERTIFICATE_NOT_EXIST));
        }
        else {
            try {
                final String password = UpdateManager.getInstanceConfig().getKeyStorePassword();
                if (CertificateUtil.isCertificateExists(importCertFile, homeDir + File.separator + "conf", password)) {
                    final String alias = CertificateUtil.getAlias(importCertFile, homeDir + File.separator + "conf", password);
                    UpdateManagerCMD.out.log(Level.INFO, "Certificate already exists with alias {0}.", alias);
                    UpdateManagerUtil.audit("Certificate already exists with alias " + alias);
                    ConsoleOut.println(CommonUtil.getString(MessageConstants.CERTIFICATE_EXISTS));
                    return;
                }
                if (CertificateUtil.isSelfSigned(importCertFile)) {
                    UpdateManagerUtil.audit("Attempting to get consent for Self Signed certificate");
                    getConsentForSelfSignedCertificates("certificate");
                    UpdateManagerUtil.audit("Consent obtained for Self Signed certificate");
                }
                CertificateUtil.importCertificate(importCertFile, homeDir + File.separator + "conf", password);
                final String alias = CertificateUtil.getAlias(importCertFile, homeDir + File.separator + "conf", password);
                UpdateManagerCMD.out.log(Level.INFO, "Certificate imported with alias {0}.", alias);
                UpdateManagerUtil.audit("Certificate imported with alias " + alias);
                ConsoleOut.println(CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE_SUCCESS));
            }
            catch (final Exception e) {
                UpdateManagerCMD.out.log(Level.SEVERE, e.getMessage(), e);
                ConsoleOut.println(CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE_FAILED));
            }
        }
    }
    
    public boolean applyContainerOfPatches(final String patchPath) {
        try {
            if (UpdateManagerUtil.isContainerOfPatches(patchPath)) {
                if (UpdateManager.getAlreadyAppliedPatchesCount() == 0) {
                    Unzipper.extractPatchesFromPatch(patchPath, UpdateManagerUtil.getHomeDirectory());
                }
                UpdateManager.setIsInvokedFromAutoApplyOfPatches(true);
                new AutoApplyPatches().applyPatches(null);
                return true;
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    
    void importCertificate(final String homeDirectory) {
        String message = CommonUtil.getString(MessageConstants.CMD_IMPORT_CERTIFICATE_DESCRIPTION);
        if (message.contains("${cert.url")) {
            String placeHolder = message.substring(message.indexOf("${cert.url"));
            placeHolder = placeHolder.substring(0, placeHolder.indexOf("}") + 1);
            message = message.replace(placeHolder, "https://www.manageengine.com/certificate/ppmsigner_publickey.crt");
        }
        ConsoleOut.println("\n" + WordUtils.wrap(message, 80));
        ConsoleOut.print("\n" + CommonUtil.getString(MessageConstants.GET_CERTIFICATE_INPUT));
        final String importCertFile = getInput();
        this.cmdImportCertificateProcesss(homeDirectory, importCertFile);
    }
    
    static {
        UpdateManagerCMD.out = Logger.getLogger(UpdateManagerCMD.class.getName());
    }
}
