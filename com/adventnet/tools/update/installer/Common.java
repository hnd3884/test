package com.adventnet.tools.update.installer;

import javax.swing.JLabel;
import java.awt.Frame;
import java.util.zip.CRC32;
import java.io.FileInputStream;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ManagementFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.SwingUtilities;
import java.awt.Component;
import com.adventnet.tools.update.UpdateData;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.HashMap;
import com.adventnet.tools.update.VersionComparator;
import com.adventnet.tools.update.PatchesInfoHolder;
import java.util.logging.Level;
import com.adventnet.tools.update.UpdateManagerUtil;
import com.zoho.tools.util.UpgradeUtil;
import java.nio.file.Paths;
import java.io.File;
import com.adventnet.tools.update.CommonUtil;
import javax.swing.JDialog;
import javax.swing.JFrame;
import com.adventnet.tools.update.XmlData;
import java.util.logging.Logger;

public class Common
{
    private static Logger out;
    private String fileName;
    private String dirToUnzip;
    private String confProductName;
    private String confProductVersion;
    private XmlData xmlData;
    private boolean isGUI;
    private boolean isPreValidationRequired;
    private final String corruptMessage;
    private final String corruptDiscSpaceMessage = "The file may be corrupted or check for the available disk space.";
    private final String directoryMessage = "The directory that you have specified doesnot exist.";
    private final String fileNotExistMessage = "The file that you have specified doesnot exist.";
    private final String invalidFile = "The format of the file that you have specified is not supported.";
    private final String jarCompatibleMessage = "The required criteria for installing this patch is missing.";
    private final String notCompatibleMessage = "The file that you have specified is not compatible with this product.";
    private final String validatorMissingMessage = "customPatchValidator entry is missing.";
    private final String versionCompatibleMessage = "The file that you have specified is not compatible with the version of the product.";
    private JFrame frame;
    private static JDialog validatingDialog;
    private static Thread thr;
    
    public Common(final String dirToUnzip, final String patchFileName, final boolean isGUI, final String confProductName) {
        this(dirToUnzip, patchFileName, isGUI, confProductName, true);
    }
    
    public Common(final String dirToUnzip, final String patchFileName, final boolean isGUI, final String confProductName, final boolean isPreValidationRequired) {
        this.fileName = null;
        this.dirToUnzip = null;
        this.confProductName = null;
        this.confProductVersion = null;
        this.xmlData = null;
        this.isGUI = false;
        this.isPreValidationRequired = true;
        this.corruptMessage = CommonUtil.getString("The file may be corrupted.Download again.");
        this.frame = null;
        this.dirToUnzip = dirToUnzip;
        this.fileName = patchFileName;
        this.confProductName = confProductName;
        this.confProductVersion = UpdateManager.getProductVersion(UpdateManager.getUpdateConfPath());
        this.isGUI = isGUI;
        this.isPreValidationRequired = isPreValidationRequired;
    }
    
    public boolean install(final JFrame frame) {
        this.frame = frame;
        boolean isInstallSuccess = true;
        final File ppmFile = new File(this.fileName);
        Common.out.info("Going to validate the patch file " + ppmFile);
        final File dir = new File(this.dirToUnzip);
        final File t = new File(this.dirToUnzip + File.separator + "Patch");
        if (!t.exists()) {
            t.mkdir();
        }
        try {
            if (!dir.isDirectory()) {
                this.displayError("The directory that you have specified doesnot exist.");
                return false;
            }
            if (!ppmFile.exists()) {
                this.displayError("The file that you have specified doesnot exist.");
                UpdateManager.getUpdateState().setErrorCode(1200);
                return false;
            }
            if (!this.fileName.endsWith(".ppm")) {
                this.displayError("The format of the file that you have specified is not supported.");
                UpdateManager.getUpdateState().setErrorCode(1200);
                return false;
            }
            boolean status = this.writeInfFile();
            if (!status) {
                Common.out.severe("ERRError in writing inf.xml file.");
                return false;
            }
            final boolean fileCorrupt = this.readInfFile();
            if (!fileCorrupt) {
                this.displayError(this.corruptMessage);
                UpdateManager.getUpdateState().setErrorCode(1200);
                return false;
            }
            if (this.xmlData.getResourceFile() != null) {
                status = this.extractAndLoadResourceFileFromPPM();
                if (!status) {
                    final String errMsg = "Problem in reading Resource file.";
                    this.displayError(errMsg);
                    return false;
                }
            }
            UpgradeUtil.notifyStatus(Paths.get(this.fileName, new String[0]), this.xmlData, PatchInstallationState.PATCH_VALIDATION_STARTED);
            final String patchFileName = Paths.get(this.fileName, new String[0]).getFileName().toString();
            try {
                Common.out.info("Going to check for trusted certificate");
                if (!JarSignerUtils.isTrustedArchive(this.fileName)) {
                    if (!UpdateManager.isInvokedFromAutoUpgrade()) {
                        Common.out.severe("Given patch is signed using self signed certificate");
                        UpdateManagerUtil.audit("Attempting to get consent for Self Signed patch");
                        if (this.isGUI) {
                            final DevelopmentWarningDialog developmentWarningDialog = new DevelopmentWarningDialog();
                            developmentWarningDialog.pack();
                            developmentWarningDialog.setModal(true);
                            developmentWarningDialog.setVisible(true);
                        }
                        else {
                            UpdateManagerCMD.getConsentForSelfSignedCertificates("patch");
                        }
                        UpdateManagerUtil.audit("Consent obtained for Self Signed patch");
                        Common.out.log(Level.WARNING, "Consent obtained for Self Signed patch");
                        UpgradeUtil.notifyStatus(Paths.get(this.fileName, new String[0]), this.xmlData, PatchInstallationState.CONSENT_ACCEPTED_FOR_SELF_SIGNED_PPM);
                    }
                    else {
                        Common.out.info("Obtaining patch consent for self-signed patch were skipped as Updatemanager is invoked from Autoupgrade");
                    }
                }
                final String password = UpdateManager.getInstanceConfig().getKeyStorePassword();
                final String validationMessage = CommonUtil.getString("The integrity of the patch is being checked. Please do not shutdown or kill the process as it needs some time.");
                if (this.isGUI) {
                    Common.validatingDialog = (JDialog)this.showMessge(validationMessage);
                }
                else {
                    Common.thr = (Thread)this.showMessge(validationMessage);
                }
                final PatchIntegrityState patchIntegrityState = PatchIntegrityVerifier.verifyPatch(this.fileName, UpdateManagerUtil.getHomeDirectory() + File.separator + "conf", password);
                if (patchIntegrityState != PatchIntegrityState.SUCCESS) {
                    UpgradeUtil.notifyStatus(Paths.get(this.fileName, new String[0]), this.xmlData, PatchInstallationState.PATCH_VALIDATION_FAILED);
                    Common.out.severe(patchIntegrityState.getMessage());
                    this.displayError(patchIntegrityState.getMessage());
                    return false;
                }
            }
            finally {
                if (Common.validatingDialog != null) {
                    this.frame.setEnabled(true);
                    Common.validatingDialog.dispose();
                    Common.validatingDialog = null;
                }
                if (Common.thr != null) {
                    ConsoleOut.println("\n");
                    Common.thr.interrupt();
                    Common.thr.stop();
                    Common.thr = null;
                }
            }
            final boolean isHotSwappablePatch = PatchesInfoHolder.isHotSwappablePatch(patchFileName);
            if (this.isPreValidationRequired && !isHotSwappablePatch) {
                try {
                    final String validationMessage = CommonUtil.getString("Product readiness and patch compatibility are being checked. Please do not shutdown or kill the process as it needs some time.");
                    if (this.isGUI) {
                        Common.validatingDialog = (JDialog)this.showMessge(validationMessage);
                    }
                    else {
                        Common.thr = (Thread)this.showMessge(validationMessage);
                    }
                    UpdateManager.getUpdateState().setCurrentState(18, System.currentTimeMillis());
                    final String toUpgradePrdtName = this.xmlData.getProductName();
                    final String toUpgradePrdtVersion = this.xmlData.getProductVersion();
                    if (this.xmlData.getCustomPatchValidator() == null) {
                        this.displayError("customPatchValidator entry is missing.");
                        UpdateManager.getUpdateState().setErrorCode(1200);
                        return false;
                    }
                    this.addCustomPatchValidatorProperties();
                    PreProcessor prePro = null;
                    try {
                        prePro = new PreProcessor(this.xmlData, this.fileName);
                        final boolean isPatchValidationSuccess = prePro.invokeCustomSPValidation(UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "tmp", this.xmlData.getCustomPatchValidator().getValidatorClass(), this.xmlData.getCustomPatchValidator().getDependentClassesList(), this.xmlData);
                        if (!isPatchValidationSuccess) {
                            Common.out.info("Patch Validation denied to apply this patch.");
                            UpgradeUtil.notifyStatus(Paths.get(this.fileName, new String[0]), this.xmlData, PatchInstallationState.PATCH_VALIDATION_FAILED);
                            return false;
                        }
                        Common.out.info("Patch Validation process completed and this patch can be applied.");
                    }
                    finally {
                        if (prePro != null) {
                            prePro.closeZipFile();
                        }
                    }
                    if (!this.isJarFilesCompatible()) {
                        this.displayError("The required criteria for installing this patch is missing.");
                        return false;
                    }
                }
                finally {
                    if (Common.validatingDialog != null) {
                        this.frame.setEnabled(true);
                        Common.validatingDialog.dispose();
                        Common.validatingDialog = null;
                    }
                    if (Common.thr != null) {
                        ConsoleOut.println("\n");
                        Common.thr.interrupt();
                        Common.thr.stop();
                    }
                }
            }
            else if (isHotSwappablePatch) {
                Common.out.info("Pre-Validation(s) were skipped as the patch is HotSwappable.");
            }
            else {
                Common.out.info("Pre-Validation(s) were skipped.");
            }
            UpgradeUtil.notifyStatus(Paths.get(this.fileName, new String[0]), this.xmlData, PatchInstallationState.PATCH_VALIDATION_COMPLETED);
            UpdateManager.setProductStats("prevalidation.completed", "true");
        }
        catch (final Exception exp) {
            isInstallSuccess = false;
            Common.out.log(Level.SEVERE, "ERR:" + this.corruptMessage, exp);
            this.displayError(this.corruptMessage);
            UpdateManagerUtil.setExitStatus(1);
        }
        return isInstallSuccess;
    }
    
    private boolean isProductVersionCompatible(final String confProductVersion, final String toUpgradeProductVersion) {
        final VersionComparator.VersionDiffType compareVersion = VersionComparator.compareVersion(confProductVersion, toUpgradeProductVersion);
        return compareVersion != VersionComparator.VersionDiffType.LOWER;
    }
    
    private String getProductMisMatchError(final String toUpgradeProductName) {
        return CommonUtil.getString(toUpgradeProductName.replaceAll("AdventNet", "").replaceAll("ManageEngine", "").trim() + " patch can not be applied over " + this.confProductName.replaceAll("AdventNet", "").replaceAll("ManageEngine", "").trim());
    }
    
    public boolean isJarFilesCompatible() {
        final HashMap jarCompatible = this.xmlData.getJarCompatible();
        if (jarCompatible == null || jarCompatible.isEmpty()) {
            return true;
        }
        final Object[] jarNameKey = jarCompatible.keySet().toArray();
        final JarChecker jarCheck = new JarChecker();
        final int len = jarNameKey.length;
        boolean checkBoolean = false;
        for (int i = 0; i < len; ++i) {
            final String jarName = (String)jarNameKey[i];
            final HashMap properties = jarCompatible.get(jarName);
            final Object[] propArray = properties.keySet().toArray();
            for (int length = propArray.length, j = 0; j < length; ++j) {
                final String v = (String)propArray[j];
                String nameJar = UpdateManagerUtil.getHomeDirectory() + File.separator + jarName;
                nameJar = CommonUtil.convertfilenameToOsFilename(nameJar);
                if (new File(nameJar).exists()) {
                    final String value = jarCheck.getTheSubAttributesValue(nameJar, v);
                    final String value2 = properties.get(v);
                    if (value.trim().equals(value2.trim())) {
                        checkBoolean = true;
                    }
                }
            }
        }
        return checkBoolean;
    }
    
    private void addCustomPatchValidatorProperties() {
        this.xmlData.getCustomPatchValidator().addProperty("patchversion", this.xmlData.getPatchVersion());
        this.xmlData.getCustomPatchValidator().addProperty("product", UpdateManager.getProductName(UpdateManager.getUpdateConfPath()));
        this.xmlData.getCustomPatchValidator().addProperty("version", UpdateManager.getProductVersion(UpdateManager.getUpdateConfPath()));
        this.xmlData.getCustomPatchValidator().addProperty("home", UpdateManagerUtil.getHomeDirectory());
        this.xmlData.getCustomPatchValidator().addProperty("mode", String.valueOf(UpdateManager.isGUI()));
    }
    
    private boolean readInfFile() {
        final String temp = this.dirToUnzip + File.separator + "Patch" + File.separator + "inf.xml";
        try {
            UpdateManagerUtil.setXmlData(this.xmlData = UpdateManagerUtil.getXmlData(Paths.get(temp, new String[0])));
        }
        catch (final Exception e) {
            Common.out.log(Level.SEVERE, "ERR : " + e);
            UpdateManagerUtil.setExitStatus(1);
            return false;
        }
        return true;
    }
    
    public XmlData getXmlData() {
        return this.xmlData;
    }
    
    public Vector getTheContext() {
        final Vector conVector = new Vector();
        final Hashtable contextTable = this.xmlData.getContextTable();
        final Enumeration enum1 = contextTable.keys();
        while (enum1.hasMoreElements()) {
            conVector.addElement(enum1.nextElement());
        }
        return conVector;
    }
    
    public String getPatchFileNamePath() {
        return this.fileName;
    }
    
    public String getPatchReadme() {
        return this.xmlData.getPatchReadme();
    }
    
    public String getPatchVersion() {
        return this.xmlData.getPatchVersion();
    }
    
    public String getInstallationDirectory() {
        return this.dirToUnzip;
    }
    
    public String getConfProductName() {
        return this.confProductName;
    }
    
    public String getConfProductVersion() {
        return this.confProductVersion;
    }
    
    public String getPatchDescription() {
        return this.xmlData.getPatchDescription();
    }
    
    public Hashtable getContextTable() {
        return this.xmlData.getContextTable();
    }
    
    public HashMap getTheCompatibleContext(final String confSubProductName) {
        final Hashtable hash = this.getContextTable();
        final HashMap contextList = new HashMap();
        if (hash != null) {
            final Object[] enum1 = hash.keySet().toArray();
            int i;
            for (int size = i = enum1.length; i > 0; --i) {
                final String contextName = (String)enum1[i - 1];
                final UpdateData updateData = hash.get(contextName);
                final String type = updateData.getContextType();
                if (type.equals("Optional")) {
                    final Vector depenVec = updateData.getDependencyVector();
                    final String desc = updateData.getContextDescription();
                    if (depenVec.contains(confSubProductName)) {
                        contextList.put(contextName, desc);
                    }
                }
            }
        }
        return contextList;
    }
    
    private void showErrorDialog(final String message, final JFrame frame) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                UMOptionPane.showErroDialog(frame, message);
            }
        };
        SwingUtilities.invokeLater(r);
    }
    
    private void displayError(String message) {
        message = CommonUtil.getString(message);
        Common.out.severe("ERR:" + message);
        UpdateManagerUtil.setExitStatus(1);
        if (this.isGUI) {
            this.showErrorDialog(message, this.frame);
        }
        else {
            ConsoleOut.println(message);
        }
    }
    
    private boolean extractAndLoadResourceFileFromPPM() throws Exception {
        final String resourceFileName = this.xmlData.getResourceFile();
        final String locationFilePath = Paths.get(UpdateManagerUtil.getHomeDirectory(), "Patch", "tmp", resourceFileName).toString();
        ZipFile ppmZipFile = null;
        try {
            ppmZipFile = new ZipFile(this.fileName);
            final ZipEntry ze = ppmZipFile.getEntry(resourceFileName);
            if (ze == null) {
                Common.out.log(Level.SEVERE, "Resource file \"{0}\" is not present inside the patch(.ppm) file.", resourceFileName);
                return false;
            }
            final InputStream is = ppmZipFile.getInputStream(ze);
            this.extractFile(is, locationFilePath);
        }
        finally {
            if (ppmZipFile != null) {
                ppmZipFile.close();
            }
        }
        UpdateManager.setResourceBundle(locationFilePath);
        CommonUtil.setResourceBundle(locationFilePath.substring(0, locationFilePath.lastIndexOf(".")), UpdateManager.getLanguage(), UpdateManager.getCountry());
        if (UpdateManager.isGUI()) {
            UpdateManager.getUi().init();
        }
        return true;
    }
    
    private boolean writeInfFile() {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(this.fileName);
            final ZipEntry zEntry = zipFile.getEntry("inf.xml");
            if (zEntry == null) {
                this.displayError("The format of the file that you have specified is not supported.");
                return false;
            }
            final String temp = this.dirToUnzip + File.separator + "Patch" + File.separator + "inf.xml";
            final InputStream xmlUnzipper = zipFile.getInputStream(zEntry);
            this.extractFile(xmlUnzipper, temp);
        }
        catch (final Exception excep) {
            this.displayError("The file may be corrupted or check for the available disk space.");
            UpdateManagerUtil.setExitStatus(1);
            Common.out.log(Level.SEVERE, "ERR:", excep);
            UpdateManager.getUpdateState().setErrorCode(1200);
            return false;
        }
        finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            }
            catch (final IOException ioe) {
                Common.out.log(Level.SEVERE, ioe.getMessage(), ioe);
            }
        }
        return true;
    }
    
    private void extractFile(final InputStream is, final String filePath) throws Exception {
        FileOutputStream fos = null;
        try {
            CommonUtil.createAllSubDirectories(filePath);
            fos = new FileOutputStream(filePath);
            int length = 0;
            final byte[] dataRead = new byte[10240];
            while (length != -1) {
                length = is.read(dataRead);
                if (length == -1) {
                    break;
                }
                fos.write(dataRead, 0, length);
            }
        }
        finally {
            is.close();
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }
    }
    
    public boolean validateFile(final JFrame frame, final String[] files) {
        try {
            final boolean status = this.extractAdditionalFiles(this.fileName, files);
            if (!status) {
                this.displayError("The format of the file that you have specified is not supported.");
                return false;
            }
        }
        catch (final Exception exp) {
            this.displayError("The format of the file that you have specified is not supported.");
            UpdateManagerUtil.setExitStatus(1);
            return false;
        }
        return true;
    }
    
    private boolean extractAdditionalFiles(final String fileName, final String[] files) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(fileName);
            if (files != null) {
                for (String name : files) {
                    ZipEntry zent = zipFile.getEntry(name);
                    if (zent == null) {
                        name = this.convertSlash(name);
                        zent = zipFile.getEntry(name);
                        if (zent == null) {
                            return false;
                        }
                    }
                    final InputStream input = zipFile.getInputStream(zent);
                    final String path = this.dirToUnzip + File.separator + name;
                    this.extractFile(input, path);
                }
            }
        }
        catch (final Exception excep) {
            excep.printStackTrace();
            this.displayError("The format of the file that you have specified is not supported.");
            UpdateManagerUtil.setExitStatus(1);
            return false;
        }
        finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            }
            catch (final IOException ioe) {
                Common.out.log(Level.SEVERE, ioe.getMessage(), ioe);
            }
        }
        return true;
    }
    
    private String convertSlash(String name) {
        if (name != null) {
            name = name.replace('/', '\\');
        }
        return name;
    }
    
    private long getCRCOfExistingJar() {
        final String jarName = "AdventNetUpdateManagerInstaller.jar";
        File existingJarFile = null;
        final RuntimeMXBean rmb = ManagementFactory.getRuntimeMXBean();
        final String existingClasspath = rmb.getClassPath();
        for (final String jarFileInClassPath : existingClasspath.split("" + File.pathSeparatorChar)) {
            if (jarFileInClassPath.contains(jarName)) {
                existingJarFile = Paths.get(jarFileInClassPath, new String[0]).toFile();
                break;
            }
        }
        if (existingJarFile != null && existingJarFile.exists()) {
            return this.determineCRC(existingJarFile);
        }
        return -1L;
    }
    
    private long determineCRC(final File file) {
        if (file != null && file.exists()) {
            try (final FileInputStream fis = new FileInputStream(file)) {
                final long crc = this.determineCRC(fis);
                return crc;
            }
            catch (final Exception ex) {
                Common.out.log(Level.SEVERE, ex.getMessage(), ex);
                return -1L;
            }
        }
        return -1L;
    }
    
    private long determineCRC(final InputStream in) throws IOException {
        final CRC32 crc32 = new CRC32();
        final byte[] newBytes = new byte[2048];
        int read;
        while ((read = in.read(newBytes)) != -1) {
            crc32.update(newBytes, 0, read);
        }
        final long crc33 = crc32.getValue();
        return crc33;
    }
    
    private Object showMessge(final String message) {
        if (this.isGUI) {
            final JDialog validatingDialog = new JDialog(this.frame, "Validating the given Patch");
            validatingDialog.setSize(700, 100);
            validatingDialog.setLocationRelativeTo(this.frame);
            validatingDialog.setResizable(false);
            validatingDialog.setDefaultCloseOperation(0);
            final StringBuilder msg = new StringBuilder();
            msg.append("<html>");
            if (message.lastIndexOf(".") > -1) {
                final String[] splitString = message.split("\\.");
                for (int n = 0; n < splitString.length; ++n) {
                    msg.append(splitString[n].trim()).append(".");
                    if (n < splitString.length - 1) {
                        msg.append("<br/>");
                    }
                }
            }
            else {
                msg.append(message);
            }
            msg.append("</html>");
            final JLabel jl = new JLabel(msg.toString(), 0);
            validatingDialog.add(jl);
            validatingDialog.setVisible(true);
            this.frame.setEnabled(false);
            return validatingDialog;
        }
        final String msg2 = message;
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                int i = 1;
                while (true) {
                    final StringBuilder ext = new StringBuilder();
                    for (int j = 0; j < i; ++j) {
                        ext.append(".");
                    }
                    for (int k = i; k < 10; ++k) {
                        ext.append(" ");
                    }
                    ConsoleOut.print("\r" + msg2 + ext.toString());
                    try {
                        Thread.sleep(500L);
                    }
                    catch (final InterruptedException ex) {}
                    if (++i == 10) {
                        i = 0;
                    }
                }
            }
        };
        final Thread thr = new Thread(r);
        thr.start();
        return thr;
    }
    
    public static JDialog getValidatingDialogBox() {
        return Common.validatingDialog;
    }
    
    public static Thread getPrevalidationMessageDisplayThread() {
        return Common.thr;
    }
    
    static {
        Common.out = Logger.getLogger(Common.class.getName());
        Common.validatingDialog = null;
        Common.thr = null;
    }
}
