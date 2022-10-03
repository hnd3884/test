package com.adventnet.tools.update;

import com.zoho.tools.util.CryptoHelper;
import com.adventnet.tools.update.installer.InstanceConfig;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import com.zoho.tools.util.OSCheckUtil;
import java.net.InetAddress;
import java.util.HashMap;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.adventnet.tools.update.installer.UpdateManagerParser;
import java.util.Vector;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.io.FileOutputStream;
import java.util.zip.ZipFile;
import com.adventnet.tools.update.installer.DiskSpace;
import java.util.logging.Level;
import javax.swing.UIManager;
import java.awt.Dimension;
import com.adventnet.tools.update.installer.UpdateManager;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintStream;
import javax.swing.SwingUtilities;
import java.awt.Window;
import com.adventnet.tools.update.installer.OptionDialogInformer;
import com.adventnet.tools.update.installer.VersionChecker;
import com.adventnet.tools.update.installer.VersionProfile;
import java.io.File;
import javax.swing.JFrame;
import java.text.DecimalFormat;
import com.adventnet.tools.update.installer.RevertProgress;
import com.adventnet.tools.update.installer.InstallProgress;
import com.adventnet.tools.update.installer.ConsoleOut;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.ArrayList;
import java.awt.Font;
import javax.swing.JDialog;
import java.util.logging.Logger;

public class UpdateManagerUtil
{
    private static Logger out;
    private static long progressCount;
    private static long filesCount;
    private static char[] spinChars;
    private static int spinCount;
    private static int imgCount;
    private static String helpXmlFilePath;
    private static String helpHtmlFilePath;
    public static int INSTALL_IN_PROGRESS;
    public static int INSTALL_COMPLETED;
    private static int installState;
    private static int revertState;
    public static final int REVERT_IN_PROGRESS = 1;
    public static final int REVERT_COMPLETED = 2;
    public static final int NORMAL_EXIT = 0;
    public static final int ABNORMAL_EXIT = 1;
    private static int EXIT_STATUS;
    private static String homeDir;
    private static JDialog dialog;
    private static Font default_font;
    private static Font bold_font;
    private static boolean deploymentEnabled;
    private static boolean installStatus;
    public static final int OK_DETAILS = 400;
    public static final int YES = 402;
    public static final int NO = 403;
    private static String cmdPatchPatch;
    private static ArrayList uninstallList;
    private static boolean supportUninstalltion;
    private static XmlData xmlData;
    private static boolean keepSubLableText;
    private static boolean keepMainLableText;
    private static boolean keepFileLableText;
    private static int state;
    private static int currentCompletedPercentage;
    private static int lengthyMessageLength;
    public static final String CERT_URL = "https://www.manageengine.com/certificate/ppmsigner_publickey.crt";
    private static boolean autoCloseOnSuccessfulCompletion;
    private static long autoCloseDelayTimeInSeconds;
    private static Map<String, String> userInformation;
    public static final FilenameFilter PPMFILEFILTER;
    
    public static void updateProgress(int percentageCompleted, String fileName, final String status) {
        final byte byte0 = 35;
        final int len = fileName.length();
        if (len > 35) {
            fileName = fileName.substring(0, 35) + "...  ";
        }
        else {
            for (int i = 0; i < 40 - len; ++i) {
                fileName += " ";
            }
        }
        if (percentageCompleted > 100) {
            percentageCompleted = 100;
        }
        UpdateManagerUtil.currentCompletedPercentage = percentageCompleted;
        String message = status + "  " + fileName + percentageCompleted + CommonUtil.getString("% Completed");
        if (percentageCompleted < 100) {
            message = message + "     [" + UpdateManagerUtil.spinChars[UpdateManagerUtil.spinCount] + "]";
        }
        final int messageLength = message.length();
        if (messageLength > UpdateManagerUtil.lengthyMessageLength) {
            UpdateManagerUtil.lengthyMessageLength = messageLength;
        }
        else {
            for (int j = messageLength; j < UpdateManagerUtil.lengthyMessageLength; ++j) {
                message += " ";
            }
        }
        ConsoleOut.print("\r" + message);
        ++UpdateManagerUtil.spinCount;
        if (UpdateManagerUtil.spinCount == 4) {
            UpdateManagerUtil.spinCount = 0;
        }
    }
    
    public static int getCurrentCompletedPercentage() {
        return UpdateManagerUtil.currentCompletedPercentage;
    }
    
    public static void displayText(final String s, final long count, final String status, final boolean GUI, final boolean install) {
        UpdateManagerUtil.progressCount += count;
        final double d = UpdateManagerUtil.progressCount / (double)UpdateManagerUtil.filesCount;
        final int percen = (int)(d * 100.0);
        if (GUI) {
            if (install) {
                updateInstallUIProgress(percen, s, status);
            }
            else {
                updateRevertUIProgress(percen, s, status);
            }
        }
        else {
            updateProgress(percen, s, status);
        }
    }
    
    public static void setProgressCount(final long count) {
        UpdateManagerUtil.progressCount = 0L;
        UpdateManagerUtil.filesCount = count;
    }
    
    public static void updateInstallUIProgress(final int percentageCompleted, final String fileName, final String status) {
        updateInstallUIProgress(percentageCompleted, fileName, status, null, 0, null);
    }
    
    public static void updateInstallUIProgress(int percentageCompleted, String fileName, final String status, final String subStatus, final int fontSize, final String fontColor) {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            if (percentageCompleted > 98) {
                percentageCompleted = 98;
            }
            install.setPercenStatus(percentageCompleted);
            UpdateManagerUtil.currentCompletedPercentage = percentageCompleted;
            if (fileName != null) {
                fileName = "<html>" + fileName + "</html>";
            }
            install.setFileLabelText(fileName);
            if (status != null) {
                install.setMainLabelText(status, fontSize, fontColor);
            }
            if (subStatus != null) {
                install.setSubLabelText(subStatus, fontSize, fontColor);
            }
            install.setPercenLabelText(String.valueOf(percentageCompleted) + " " + CommonUtil.getString("% Completed"));
        }
    }
    
    public static void setInstallState(final int state) {
        UpdateManagerUtil.installState = state;
    }
    
    public static int getInstallState() {
        return UpdateManagerUtil.installState;
    }
    
    public static void startInstallAnimation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final InstallProgress install = InstallProgress.getInstance();
                while (UpdateManagerUtil.installState == UpdateManagerUtil.INSTALL_IN_PROGRESS) {
                    if (UpdateManagerUtil.imgCount >= 9) {
                        UpdateManagerUtil.imgCount = 0;
                    }
                    if (install != null) {
                        install.updateImage("com/adventnet/tools/update/installer/images/install_" + (UpdateManagerUtil.imgCount + 1) + ".png");
                    }
                    UpdateManagerUtil.imgCount++;
                    try {
                        Thread.sleep(200L);
                    }
                    catch (final InterruptedException ie) {}
                }
            }
        }, "InstallAnimationThread").start();
    }
    
    public static void startInstallCompletionAnimation() {
        final InstallProgress install = InstallProgress.getInstance();
        while (UpdateManagerUtil.imgCount <= 8) {
            install.updateImage("com/adventnet/tools/update/installer/images/install_" + (UpdateManagerUtil.imgCount + 1) + ".png");
            try {
                Thread.sleep(200L);
            }
            catch (final InterruptedException ex) {}
            if (UpdateManagerUtil.imgCount == 9) {
                break;
            }
            ++UpdateManagerUtil.imgCount;
        }
        install.setPercenLabelText(String.valueOf(100) + " " + CommonUtil.getString("% Completed"));
        install.setPercenStatus(100);
        for (int installCompCount = 0; installCompCount < 7; ++installCompCount) {
            install.updateImage("com/adventnet/tools/update/installer/images/install_seal_" + (installCompCount + 1) + ".png");
            try {
                Thread.sleep(200L);
            }
            catch (final InterruptedException ex2) {}
        }
    }
    
    public static void startUnInstallCompletionAnimation() {
        final RevertProgress revert = RevertProgress.getInstance();
        while (UpdateManagerUtil.imgCount <= 7) {
            revert.updateImage("com/adventnet/tools/update/installer/images/uninstall_" + (UpdateManagerUtil.imgCount + 1) + ".png");
            try {
                Thread.sleep(200L);
            }
            catch (final InterruptedException ex) {}
            if (UpdateManagerUtil.imgCount == 8) {
                break;
            }
            ++UpdateManagerUtil.imgCount;
        }
        for (int revertCompCount = 0; revertCompCount < 7; ++revertCompCount) {
            revert.updateImage("com/adventnet/tools/update/installer/images/uninstall_seal_" + (revertCompCount + 1) + ".png");
            try {
                Thread.sleep(200L);
            }
            catch (final InterruptedException ex2) {}
        }
    }
    
    public static void setRevertState(final int state) {
        UpdateManagerUtil.revertState = state;
    }
    
    public static int getRevertState() {
        return UpdateManagerUtil.revertState;
    }
    
    public static void startRevertAnimation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RevertProgress revert = RevertProgress.getInstance();
                while (UpdateManagerUtil.revertState == 1) {
                    if (UpdateManagerUtil.imgCount >= 8) {
                        UpdateManagerUtil.imgCount = 0;
                    }
                    if (revert != null) {
                        revert.updateImage("com/adventnet/tools/update/installer/images/uninstall_" + (UpdateManagerUtil.imgCount + 1) + ".png");
                    }
                    UpdateManagerUtil.imgCount++;
                    try {
                        Thread.sleep(200L);
                    }
                    catch (final InterruptedException ie) {}
                }
            }
        }, "UninstallAnimationThread").start();
    }
    
    public static void updateRevertUIProgress(final int percentageCompleted, final String fileName, final String status) {
        updateRevertUIProgress(percentageCompleted, fileName, status, null, 0, null);
    }
    
    public static void updateRevertUIProgress(int percentageCompleted, String fileName, final String status, final String subStatus, final int fontSize, final String fontColor) {
        final RevertProgress revert = RevertProgress.getInstance();
        if (revert != null) {
            if (percentageCompleted > 100) {
                percentageCompleted = 100;
            }
            revert.setPercenStatus(percentageCompleted);
            UpdateManagerUtil.currentCompletedPercentage = percentageCompleted;
            if (fileName != null) {
                fileName = "<html>" + fileName + "</html>";
            }
            revert.setFileLabelText(fileName);
            if (status != null) {
                revert.setMainLabelText(status, fontSize, fontColor);
            }
            if (subStatus != null) {
                revert.setSubLabelText(subStatus, fontSize, fontColor);
            }
            revert.setPercenLabelText(String.valueOf(percentageCompleted) + " " + CommonUtil.getString("% Completed"));
        }
    }
    
    public static void updateTheUI() {
        final RevertProgress revert = RevertProgress.getInstance();
        if (revert != null) {
            revert.updateTheVersion();
        }
    }
    
    public static void updateTheInstallUI() {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            install.updateTheVersion();
        }
    }
    
    public static void enableTheInstallUIButton() {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            install.enableTheButton();
        }
    }
    
    public static void setDefaultCursor() {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            install.setDefaultCursor();
        }
    }
    
    public static void updateTheSizeInInstallUI(final long size) {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            final String patchSize = getSizeString(size);
            install.setPPMSize(patchSize);
        }
    }
    
    public static void backingUIProgress(String contextName, String fileName) {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            if (contextName.equals("NoContext")) {
                contextName = "";
            }
            else {
                contextName = contextName + " " + CommonUtil.getString("context");
            }
            install.setMainLabelText(CommonUtil.getString("Backing up") + " " + contextName);
            if (fileName.length() > 55) {
                fileName = fileName.substring(0, 55) + "...";
            }
            install.setFileLabelText(fileName);
        }
    }
    
    public static void backingProgress(String contextName, String fileName) {
        if (contextName.equals("NoContext")) {
            contextName = "";
        }
        else {
            contextName = contextName + " " + CommonUtil.getString("context");
        }
        fileName = contextName + fileName;
        final int len = fileName.length();
        if (len > 35) {
            fileName = fileName.substring(0, 35) + "...  ";
        }
        else {
            for (int i = 0; i < 40 - len; ++i) {
                fileName += " ";
            }
        }
        String message = CommonUtil.getString("Backing up") + " " + fileName + "      [" + UpdateManagerUtil.spinChars[UpdateManagerUtil.spinCount] + "]";
        final int messageLength = message.length();
        if (messageLength > UpdateManagerUtil.lengthyMessageLength) {
            UpdateManagerUtil.lengthyMessageLength = messageLength;
        }
        else {
            for (int j = messageLength; j < UpdateManagerUtil.lengthyMessageLength; ++j) {
                message += " ";
            }
        }
        ConsoleOut.print("\r" + message);
        ++UpdateManagerUtil.spinCount;
        if (UpdateManagerUtil.spinCount == 4) {
            UpdateManagerUtil.spinCount = 0;
        }
    }
    
    public static void showErrorMessage(final String mesg, final boolean keepMessageAlive) {
        if (keepMessageAlive) {
            UpdateManagerUtil.keepMainLableText = keepMessageAlive;
        }
        setErrorMessage(mesg);
    }
    
    public static void setErrorMessage(final String mesg) {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            install.mainLabel.setFont(getFont());
            install.setMainLabelText(mesg);
            install.failure();
        }
    }
    
    public static void setRevertCorruptMainLabelMessage(final String mesg, final String subMessage) {
        final RevertProgress revert = RevertProgress.getInstance();
        if (revert != null) {
            revert.updateImage("com/adventnet/tools/update/installer/images/uninstall_pc.png");
            revert.mainLabel.setFont(getFont());
            revert.setMainLabelText(mesg);
            revert.setFileLabelText(subMessage);
            revert.setDefaultCursor();
        }
    }
    
    public static void setInstallCompletedMessage(final String mesg, final String subMessage) {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            install.mainLabel.setFont(getFont());
            install.setMainLabelText(mesg);
            install.setFileLabelText(subMessage);
        }
    }
    
    public static void setInstallMainLabelMessage(final String mesg, final String subMessage) {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            install.setMainLabelText(mesg);
            install.setFileLabelText(subMessage);
        }
    }
    
    public static void setInstallCorruptMainLabelMessage(final String mesg, final String subMessage) {
        final InstallProgress install = InstallProgress.getInstance();
        if (install != null) {
            while (UpdateManagerUtil.imgCount <= 8) {
                install.updateImage("com/adventnet/tools/update/installer/images/install_" + (UpdateManagerUtil.imgCount + 1) + ".png");
                try {
                    Thread.sleep(200L);
                }
                catch (final InterruptedException ex) {}
                if (UpdateManagerUtil.imgCount == 9) {
                    break;
                }
                ++UpdateManagerUtil.imgCount;
            }
            install.updateImage("com/adventnet/tools/update/installer/images/install_1.png");
            install.setMainLabelText(mesg);
            install.mainLabel.setFont(getFont());
            install.setFileLabelText(subMessage);
        }
    }
    
    public static void setRevertCompletedMessage(final String mesg, final String subMessage) {
        final RevertProgress revert = RevertProgress.getInstance();
        if (revert != null) {
            revert.mainLabel.setFont(getFont());
            revert.setMainLabelText(mesg);
            revert.setFileLabelText(subMessage);
        }
    }
    
    public static void setRevertMainLabelMessage(final String mesg, final String subMessage) {
        final RevertProgress revert = RevertProgress.getInstance();
        if (revert != null) {
            revert.setMainLabelText(mesg);
            revert.setFileLabelText(subMessage);
        }
    }
    
    public static String getHelpHtmlFilePath() {
        return UpdateManagerUtil.helpHtmlFilePath;
    }
    
    public static String getHelpXmlFilePath() {
        return UpdateManagerUtil.helpXmlFilePath;
    }
    
    public static void setHelpHtmlFilePath(final String help) {
        UpdateManagerUtil.helpHtmlFilePath = CommonUtil.convertfilenameToOsFilename(help);
    }
    
    public static void setHelpXmlFilePath(final String xml) {
        UpdateManagerUtil.helpXmlFilePath = CommonUtil.convertfilenameToOsFilename(xml);
    }
    
    public static void startFailureCompletion() {
        final InstallProgress install = InstallProgress.getInstance();
        install.mainLabel.setFont(getFont());
        install.installProgressBar.setVisible(false);
        install.setPercenLabelText(" ");
        install.setFileLabelText(CommonUtil.getString("Uninstalling"));
        while (UpdateManagerUtil.imgCount <= 8) {
            install.updateImage("com/adventnet/tools/update/installer/images/install_" + (UpdateManagerUtil.imgCount + 1) + ".png");
            try {
                Thread.sleep(200L);
            }
            catch (final InterruptedException ex) {}
            if (UpdateManagerUtil.imgCount == 9) {
                break;
            }
            ++UpdateManagerUtil.imgCount;
        }
    }
    
    public static void startInstallFailureAnimation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final InstallProgress install = InstallProgress.getInstance();
                while (UpdateManagerUtil.installState == UpdateManagerUtil.INSTALL_IN_PROGRESS) {
                    if (UpdateManagerUtil.imgCount >= 8) {
                        UpdateManagerUtil.imgCount = 0;
                    }
                    if (install != null) {
                        install.updateImage("com/adventnet/tools/update/installer/images/uninstall_" + (UpdateManagerUtil.imgCount + 1) + ".png");
                    }
                    UpdateManagerUtil.imgCount++;
                    try {
                        Thread.sleep(200L);
                    }
                    catch (final InterruptedException ie) {}
                }
            }
        }, "InstallFailureAnimationThread").start();
    }
    
    public static void startInstallFailureAnimationCompletion() {
        final InstallProgress install = InstallProgress.getInstance();
        while (UpdateManagerUtil.imgCount <= 7) {
            install.updateImage("com/adventnet/tools/update/installer/images/uninstall_" + (UpdateManagerUtil.imgCount + 1) + ".png");
            try {
                Thread.sleep(200L);
            }
            catch (final InterruptedException ex) {}
            if (UpdateManagerUtil.imgCount == 8) {
                break;
            }
            ++UpdateManagerUtil.imgCount;
        }
        install.updateImage("com/adventnet/tools/update/installer/images/uninstall_1.png");
        install.setFileLabelText(CommonUtil.getString("Uninstallation completed"));
        UpdateManagerUtil.keepFileLableText = true;
    }
    
    public static void updateFailureProgress(String fileName, final String status) {
        final byte byte0 = 35;
        final int len = fileName.length();
        if (len > 35) {
            fileName = fileName.substring(0, 35) + "...  ";
        }
        else {
            for (int i = 0; i < 40 - len; ++i) {
                fileName += " ";
            }
        }
        String message = status + "  " + fileName + "[" + UpdateManagerUtil.spinChars[UpdateManagerUtil.spinCount] + "]";
        final int messageLength = message.length();
        if (messageLength > UpdateManagerUtil.lengthyMessageLength) {
            UpdateManagerUtil.lengthyMessageLength = messageLength;
        }
        else {
            for (int j = messageLength; j < UpdateManagerUtil.lengthyMessageLength; ++j) {
                message += " ";
            }
        }
        ConsoleOut.print("\r" + message);
        ++UpdateManagerUtil.spinCount;
        if (UpdateManagerUtil.spinCount == 4) {
            UpdateManagerUtil.spinCount = 0;
        }
    }
    
    public static void updateTheFailureInCMD() {
        ConsoleOut.println("\n" + CommonUtil.getString("Uninstallation Completed"));
    }
    
    public static String getSizeString(final long size) {
        final DecimalFormat sizeFormatter = new DecimalFormat("#0.00");
        String sizeString = null;
        if (size < 1024L) {
            sizeString = String.valueOf(String.valueOf(sizeFormatter.format(size))).concat(" bytes");
        }
        else if (size < 1048576L) {
            sizeString = String.valueOf(String.valueOf(sizeFormatter.format(size / 1024.0))).concat(" KB");
        }
        else if (size < 1073741824L) {
            sizeString = String.valueOf(String.valueOf(sizeFormatter.format(size / 1048576.0))).concat(" MB");
        }
        else if (size < 1099511627776L) {
            sizeString = String.valueOf(String.valueOf(sizeFormatter.format(size / 1.073741824E9))).concat(" GB");
        }
        else {
            sizeString = String.valueOf(String.valueOf(sizeFormatter.format(size / 1.099511627776E12))).concat(" TB");
        }
        return sizeString;
    }
    
    public static void setExitStatus(final int status) {
        UpdateManagerUtil.EXIT_STATUS = status;
    }
    
    public static int getExitStatus() {
        return UpdateManagerUtil.EXIT_STATUS;
    }
    
    public static void setHomeDirectory(final String dir) {
        UpdateManagerUtil.homeDir = dir;
    }
    
    public static String getHomeDirectory() {
        return UpdateManagerUtil.homeDir;
    }
    
    public static void setParent(final JDialog dia) {
        UpdateManagerUtil.dialog = dia;
    }
    
    public static JDialog getParent() {
        return UpdateManagerUtil.dialog;
    }
    
    public static void setFont(final Font font) {
        UpdateManagerUtil.default_font = font;
    }
    
    public static void setBoldFont(final Font font) {
        UpdateManagerUtil.bold_font = font;
    }
    
    public static Font getFont() {
        if (UpdateManagerUtil.default_font == null) {
            return UpdateManagerUtil.default_font = new Font("Dialog", 0, 12);
        }
        return UpdateManagerUtil.default_font;
    }
    
    public static Font getBoldFont() {
        if (UpdateManagerUtil.bold_font == null) {
            return UpdateManagerUtil.bold_font = new Font("Dialog", 1, 12);
        }
        return UpdateManagerUtil.bold_font;
    }
    
    public static void enableDeploymentTool(final boolean bool) {
        UpdateManagerUtil.deploymentEnabled = bool;
    }
    
    public static boolean isDeploymentToolEnabled() {
        return UpdateManagerUtil.deploymentEnabled;
    }
    
    public static ArrayList getTheListToUninstall() {
        return UpdateManagerUtil.uninstallList;
    }
    
    public static void setTheListToUninstall(final ArrayList list) {
        UpdateManagerUtil.uninstallList = list;
    }
    
    public static ArrayList getTheListToUninstall(final String selected, final int index, final boolean bool, final JFrame frame, final boolean GUI, final boolean semiCMD) {
        final ArrayList list = new ArrayList();
        final String dirToUnzip = getHomeDirectory();
        final String specsFile = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsFile, false, false);
        String ver = null;
        if (bool) {
            ver = selected.substring(0, selected.indexOf(" "));
        }
        else {
            ver = selected;
        }
        final String[] pVersion = vProfile.getAllVersions();
        final String[] newVersion = reverseOrder(pVersion);
        final String patchVersion = newVersion[index];
        String type = vProfile.getTheAdditionalDetail(patchVersion, "Type");
        if (type == null) {
            type = "SP";
        }
        if (type.equals("FP")) {
            list.add(patchVersion);
        }
        else {
            final String[] spVersions = vProfile.getTheVersions();
            int i;
            for (int len = i = spVersions.length; i > 0; --i) {
                final String vers = spVersions[i - 1];
                list.add(vers);
                if (vers.equals(patchVersion)) {
                    break;
                }
            }
        }
        if (!type.equals("SP")) {
            check(patchVersion, list, GUI, semiCMD);
            setTheListToUninstall(list);
            return list;
        }
        final boolean state = checkForCompatibility(ver, list, false, null, semiCMD, GUI);
        if (!state) {
            return null;
        }
        setTheListToUninstall(list);
        return list;
    }
    
    private static boolean check(final String selVer, final ArrayList list, final boolean GUI, final boolean semiCMD) {
        boolean compStatus = false;
        compStatus = checkForFPCompatibility(selVer, list, false, null, semiCMD, GUI);
        return compStatus;
    }
    
    private static String[] reverseOrder(final String[] strArray) {
        final String[] pVersion = strArray;
        final int len = pVersion.length;
        final String[] newVersion = new String[len];
        int j = 0;
        for (int i = len; i > 0; --i) {
            newVersion[j] = pVersion[i - 1];
            ++j;
        }
        return newVersion;
    }
    
    public static boolean checkForCompatibility(final String version, final ArrayList list, final boolean bool, final JFrame frame, final boolean semiCMD, final boolean GUI) {
        final String selected = version;
        final String dirToUnzip = getHomeDirectory();
        final String specsFile = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsFile, false, false);
        String ver = null;
        if (bool) {
            ver = selected.substring(0, selected.indexOf(" "));
        }
        else {
            ver = selected;
        }
        final String[] pVersion = vProfile.getTheFPVersions();
        if (pVersion == null) {
            return true;
        }
        for (final String fpVer : pVersion) {
            final FeatureVersionComp fvc = vProfile.getVersionCompatibility(fpVer);
            if (fvc != null) {
                final String patchOption = fvc.getCompPatchOption();
                final String patchVersion = fvc.getCompPatchVersion();
                if (patchOption != null) {
                    if (patchVersion != null) {
                        final String[] trunVersionArray = getTruncatedArray(list);
                        final VersionChecker vChecker = new VersionChecker();
                        final int opt = CommonUtil.parseOption(patchOption);
                        final boolean status = vChecker.checkVersionCompatible(patchVersion, trunVersionArray, opt);
                        if (status) {
                            final String message = CommonUtil.getString("The ServicePack version is dependent on the installed FeaturePack.");
                            final String detailMessage = CommonUtil.getString("The FeaturePack: ") + fpVer + " " + CommonUtil.getString("is dependent on the selected ServicePack: ") + ver + "\n" + CommonUtil.getString("Uninstall the FeaturePack and proceed");
                            displayError(message, frame, GUI, detailMessage, semiCMD);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean checkForFPCompatibility(final String version, final ArrayList list, final boolean bool, final JFrame frame, final boolean semiCMD, final boolean GUI) {
        final String selected = version;
        final String dirToUnzip = getHomeDirectory();
        final String specsFile = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsFile, false, false);
        final String ver = selected;
        final String[] pVersion = vProfile.getTheFPVersions();
        if (pVersion == null) {
            return true;
        }
        for (final String fpVer : pVersion) {
            final FeatureVersionComp fvc = vProfile.getVersionCompatibility(fpVer);
            if (fvc != null) {
                final boolean check = featureCompatibility(ver, fvc, bool, frame, semiCMD);
                if (!check) {
                    list.add(fpVer);
                }
            }
        }
        return true;
    }
    
    private static boolean featureCompatibility(final String fpVersion, final FeatureVersionComp fvc, final boolean bool, final JFrame frame, final boolean semiCMD) {
        final String[] versions = fvc.getVersions();
        final int len = versions.length;
        if (len == 0) {
            return true;
        }
        for (int i = 0; i < len; i += 3) {
            final String featureName = versions[i];
            final String option = versions[i + 1];
            final String versionName = versions[i + 2];
            final int index = fpVersion.indexOf(featureName);
            if (index != -1) {
                final ArrayList verList = new ArrayList();
                verList.add(fpVersion);
                final String[] trunVersionArray = getTruncatedArray(verList);
                final VersionChecker vChecker = new VersionChecker();
                final int opt = CommonUtil.parseOption(option);
                final boolean status = vChecker.checkVersionCompatible(versionName, trunVersionArray, opt);
                if (status) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static String[] getTruncatedArray(final ArrayList list) {
        String[] trunVersionArray = new String[0];
        for (int size = list.size(), i = 0; i < size; ++i) {
            final String installedVersion = list.get(i);
            final String patch = installedVersion.substring(installedVersion.lastIndexOf("-") + 1);
            final int leng = trunVersionArray.length;
            final String[] tmp = new String[leng + 1];
            System.arraycopy(trunVersionArray, 0, tmp, 0, leng);
            tmp[leng] = patch;
            trunVersionArray = tmp;
        }
        return trunVersionArray;
    }
    
    private static void showErrorDialog(final String message, final JFrame frame, final String details) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                OptionDialogInformer odi = new OptionDialogInformer();
                odi.showOptionDialog(frame, "Error", 0, 1, message, details, CommonUtil.getResourceBundle());
                odi = null;
            }
        };
        SwingUtilities.invokeLater(r);
    }
    
    public static void showDialog(final String message, final JFrame frame, final String details) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                OptionDialogInformer odi = new OptionDialogInformer();
                odi.showOptionDialog(frame, "Information", 0, 2, message, details, CommonUtil.getResourceBundle());
                odi = null;
            }
        };
        SwingUtilities.invokeLater(r);
    }
    
    private static int displayError(final String message, final JFrame frame, final boolean GUI, final String detailMessage, final boolean semiCMD) {
        if (GUI) {
            showErrorDialog(CommonUtil.getString(message), frame, detailMessage);
            return 402;
        }
        return displayCMDError(message, detailMessage, 400, semiCMD);
    }
    
    private static int displayCMDError(final String message, final String details, final int option, final boolean semiCMD) {
        int selectedOption = -1;
        final PrintStream tempLogger = System.out;
        final StringBuffer strBuf = new StringBuffer();
        if (option == 400 && !semiCMD) {
            if (!nullCheck(details)) {
                strBuf.append("\n [o]k \n [d]etails\n");
            }
            else {
                strBuf.append("\n [o]k \n ");
            }
        }
        ConsoleOut.println(message + strBuf.toString());
        if (!semiCMD) {
            final InputStream ips = System.in;
            System.setIn(ips);
            while ((selectedOption = getFromUser(details, option)) == -1) {}
            System.setIn(ips);
        }
        return selectedOption;
    }
    
    private static boolean nullCheck(final String value) {
        return value == null || value.equalsIgnoreCase("null") || value.trim().equals("");
    }
    
    private static int getFromUser(final String details, final int option) {
        String opt = null;
        int selectedOption = -1;
        final BufferedReader bufred = new BufferedReader(new InputStreamReader(System.in));
        try {
            opt = bufred.readLine().trim();
            if (!nullCheck(opt)) {
                if (option == 400) {
                    if (opt.equalsIgnoreCase(CommonUtil.getString("o")) || opt.equalsIgnoreCase(CommonUtil.getString("ok"))) {
                        selectedOption = 402;
                    }
                    else if ((opt.equalsIgnoreCase(CommonUtil.getString("d")) || opt.equalsIgnoreCase(CommonUtil.getString("details"))) && !nullCheck(details)) {
                        selectedOption = -1;
                        ConsoleOut.println("\n" + details);
                    }
                }
            }
            else {
                selectedOption = -1;
            }
        }
        catch (final Exception excp) {
            selectedOption = -1;
        }
        return selectedOption;
    }
    
    public static String getDisplayVersionName(final String name) {
        final String dirToUnzip = getHomeDirectory();
        final String specsPath = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final File specFile = new File(specsPath);
        if (!specFile.exists()) {
            return name;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String versionName = name;
        String displayName = vProfile.getTheAdditionalDetail(versionName, "DisplayName");
        if (displayName == null || displayName.trim().equals("")) {
            displayName = versionName;
        }
        return displayName;
    }
    
    public static String getOriginalVersion(final String displayName) {
        final String installDir = getHomeDirectory();
        final String specsPath = installDir + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        String spVersion = null;
        final String[] vect = vProfile.getAllVersions();
        if (vect == null) {
            return null;
        }
        for (final String oriversion : vect) {
            String dName = vProfile.getTheAdditionalDetail(oriversion, "DisplayName");
            if (dName == null || dName.trim().equals("")) {
                dName = oriversion;
            }
            if (dName.equals(displayName)) {
                spVersion = oriversion;
                break;
            }
        }
        return spVersion;
    }
    
    public static void setCMDPatchPath(final String path) {
        UpdateManagerUtil.cmdPatchPatch = path;
    }
    
    public static String getCMDPatchPath() {
        return UpdateManagerUtil.cmdPatchPatch;
    }
    
    public static void setTaskStatus(final boolean status) {
        UpdateManagerUtil.installStatus = status;
    }
    
    public static boolean getTaskStatus() {
        return UpdateManagerUtil.installStatus;
    }
    
    public static void setExecutablePermission(final String file) throws Exception {
        final Runtime runcmd = Runtime.getRuntime();
        final Process pr = runcmd.exec("/bin/chmod 777 " + file);
    }
    
    public static String getTheAvailableBrowser() {
        final String[] browsers = { "netscape", "mozilla", "firefox", "mozilla-firefox", "opera", "konqueror", "epiphany" };
        String browser = null;
        for (int count = 0; count < browsers.length; ++count) {
            try {
                if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
                    browser = browsers[count];
                }
                if (browser == null) {
                    continue;
                }
            }
            catch (final Exception e) {
                continue;
            }
            return browser;
        }
        return null;
    }
    
    public static void setAllowUninstalltion(final boolean uninstall) {
        UpdateManagerUtil.supportUninstalltion = uninstall;
    }
    
    public static boolean getAllowUninstalltion() {
        return UpdateManagerUtil.supportUninstalltion;
    }
    
    public static void displayReadMe(final String fileUrl, final int invocationType, final String pathToAppend) {
        if (invocationType == 2) {
            CommonUtil.displayURL(getHomeDirectory() + File.separator + pathToAppend + File.separator + fileUrl, invocationType);
        }
        else {
            CommonUtil.displayURL(fileUrl);
        }
    }
    
    public static String longdateToString(final long ld, final String fmt) {
        final long ldate = ld;
        String dateFormat = null;
        try {
            final SimpleDateFormat formatter = new SimpleDateFormat(fmt);
            final Date theCreatedDate = new Date(ldate);
            dateFormat = formatter.format(theCreatedDate);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dateFormat;
    }
    
    public static String getNewText(final String text, final int fontSize, final String fontColor) {
        if (fontSize <= 0 && fontColor == null) {
            return text;
        }
        return "<html><font " + getFontAtr(fontSize, fontColor) + ">" + text + "</font></html>";
    }
    
    public static String getFontAtr(final int fontSize, String fontColor) {
        if (fontColor == null) {
            fontColor = "black";
        }
        return "size='" + fontSize + "' color='" + fontColor + "'";
    }
    
    public static int getNewWidth(final int width) {
        if (width > 700) {
            return 700;
        }
        return width;
    }
    
    public static int getTextWidth(final int fontSize, final int subLabelLength, final int mainLabelLength) {
        final int textWidth = (subLabelLength + mainLabelLength) * 20 * fontSize;
        if (textWidth > 425) {
            return 425 + textWidth / 425 + 20;
        }
        return 425;
    }
    
    public static void resizeThirdPanel(final int width, final int height) {
        if (UpdateManager.isGUI() && (width > getParent().getWidth() || height > getParent().getHeight())) {
            getParent().setSize(new Dimension(getNewWidth(width), getNewWidth(height)));
            getParent().validate();
        }
    }
    
    public static void clearInstallStatus() {
        if (UpdateManager.isGUI()) {
            InstallProgress.getInstance().setMainLabelText("");
        }
    }
    
    public static void clearInstallSubStatus() {
        if (UpdateManager.isGUI()) {
            InstallProgress.getInstance().setSubLabelText("");
        }
    }
    
    public static void clearRevertStatus() {
        if (UpdateManager.isGUI()) {
            RevertProgress.getInstance().setMainLabelText("");
        }
    }
    
    public static void clearRevertSubStatus() {
        if (UpdateManager.isGUI()) {
            RevertProgress.getInstance().setSubLabelText("", 0, null);
        }
    }
    
    public static void clearInstallProgress(final boolean forceClean) {
        if (UpdateManager.isGUI()) {
            if (!UpdateManagerUtil.keepMainLableText || forceClean) {
                InstallProgress.getInstance().setMainLabelText("");
            }
            if (!UpdateManagerUtil.keepSubLableText || forceClean) {
                InstallProgress.getInstance().setSubLabelText("");
            }
            if (!UpdateManagerUtil.keepFileLableText || forceClean) {
                InstallProgress.getInstance().setFileLabelText("");
            }
            if (getParent().getWidth() > 425 || getParent().getHeight() > 425) {
                getParent().setSize(425, 425);
                getParent().validate();
            }
        }
    }
    
    public static void clearRevertProgress(final boolean forceClean) {
        if (UpdateManager.isGUI()) {
            if (!UpdateManagerUtil.keepMainLableText || forceClean) {
                RevertProgress.getInstance().setMainLabelText("");
            }
            if (!UpdateManagerUtil.keepSubLableText || forceClean) {
                RevertProgress.getInstance().setSubLabelText("", 0, null);
            }
            if (!UpdateManagerUtil.keepFileLableText || forceClean) {
                RevertProgress.getInstance().setFileLabelText("");
            }
            if (getParent().getWidth() > 425 || getParent().getHeight() > 425) {
                getParent().setSize(425, 425);
                getParent().validate();
            }
        }
    }
    
    public static void setXmlData(final XmlData xml) {
        UpdateManagerUtil.xmlData = xml;
    }
    
    public static XmlData getXmlData() {
        return UpdateManagerUtil.xmlData;
    }
    
    public static void setSubLabelText(final String message, final int fontSize, final String fontColor, final boolean keepMessagetAlive) {
        if (UpdateManager.isGUI()) {
            if (keepMessagetAlive) {
                UpdateManagerUtil.keepSubLableText = keepMessagetAlive;
            }
            if (UpdateManagerUtil.state == 16) {
                InstallProgress.getInstance().setSubLabelText(message, fontSize, fontColor);
            }
            else if (UpdateManagerUtil.state == 17) {
                RevertProgress.getInstance().setSubLabelText(message, fontSize, fontColor);
            }
        }
    }
    
    public static void setMainLabelText(final String message, final int fontSize, final String fontColor, final boolean keepMessagetAlive) {
        if (UpdateManager.isGUI()) {
            if (keepMessagetAlive) {
                UpdateManagerUtil.keepMainLableText = keepMessagetAlive;
            }
            if (UpdateManagerUtil.state == 16) {
                InstallProgress.getInstance().setMainLabelText(message, fontSize, fontColor);
            }
            else if (UpdateManagerUtil.state == 17) {
                RevertProgress.getInstance().setMainLabelText(message, fontSize, fontColor);
            }
        }
    }
    
    public static void setState(final int ustate) {
        UpdateManagerUtil.state = ustate;
    }
    
    public static void loadUIManager() {
        final String os = getOS();
        try {
            if (os != null && UpdateManager.getUpdateConfProperty(os + "_uimanager") != null) {
                UIManager.setLookAndFeel(UpdateManager.getUpdateConfProperty(os + "_uimanager"));
            }
            else if (UpdateManager.getUpdateConfProperty("uimanager") != null) {
                UIManager.setLookAndFeel(UpdateManager.getUpdateConfProperty("uimanager"));
            }
        }
        catch (final Exception ex) {
            UpdateManagerUtil.out.log(Level.SEVERE, CommonUtil.getString("Error while loading UI Manager"), ex);
        }
    }
    
    public static String getOS() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "windows";
        }
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            return "linux";
        }
        return null;
    }
    
    public static boolean isDiskSpaceAvailable(final String patchFilePath, final String logsDir, final String productHome) throws Exception {
        long unCompressedSize = getSizeCount(patchFilePath, productHome + File.separator + logsDir, productHome) * 3L;
        final long patchSize = new File(patchFilePath).length();
        unCompressedSize += patchSize * 2L;
        final DiskSpace space = DiskSpace.getInstance();
        final long freeSpace = space.getFreeSpace(productHome);
        return unCompressedSize <= freeSpace;
    }
    
    private static void extractInf(final String patchFilePath, final String logsDir) throws Exception {
        InputStream xmlUnzipper = null;
        FileOutputStream xmlFile = null;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(patchFilePath);
            final ZipEntry zEntry = zipFile.getEntry("inf.xml");
            final String temp = logsDir + File.separator + "inf.xml";
            xmlUnzipper = zipFile.getInputStream(zEntry);
            CommonUtil.createAllSubDirectories(temp);
            xmlFile = new FileOutputStream(temp);
            final byte[] dataRead = new byte[10240];
            int length = 0;
            while (length != -1) {
                length = xmlUnzipper.read(dataRead);
                if (length == -1) {
                    break;
                }
                xmlFile.write(dataRead, 0, length);
            }
        }
        finally {
            if (xmlUnzipper != null) {
                xmlUnzipper.close();
            }
            if (xmlFile != null) {
                xmlFile.flush();
                xmlFile.close();
            }
            if (zipFile != null) {
                zipFile.close();
            }
        }
    }
    
    private static long getSizeCount(final String patchFilePath, final String logsDir, final String productHome) throws Exception {
        ZipFile zipFile = null;
        try {
            long sizeCount = 0L;
            extractInf(patchFilePath, logsDir);
            final File tempFile = new File(patchFilePath);
            zipFile = new ZipFile(tempFile);
            ZipEntry zipFileEntry = new ZipEntry("sdfa.sdf");
            final XmlParser xmlParser = new XmlParser(logsDir + File.separator + "inf.xml");
            final Hashtable hash = xmlParser.getXmlData().getContextTable();
            final ArrayList selectedContext = new ArrayList();
            selectedContext.add(getContext(productHome + File.separator + "conf" + File.separator + "update_conf.xml"));
            for (int i = 0; i < selectedContext.size(); ++i) {
                final String selContext = selectedContext.get(i);
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
                            zipFileEntry = zipFile.getEntry(jfileName);
                            if (!zipFileEntry.isDirectory()) {
                                sizeCount += zipFileEntry.getSize();
                            }
                        }
                    }
                    else {
                        for (int a = 0; a < jarFileNames.size(); ++a) {
                            for (int fileCount = 1, x = 0; x < fileCount; ++x) {
                                final String fgFileName = fgFileNames.elementAt(x);
                                zipFileEntry = zipFile.getEntry(fgFileName);
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
                        zipFileEntry = zipFile.getEntry(zfileName);
                        if (!zipFileEntry.isDirectory()) {
                            sizeCount += zipFileEntry.getSize();
                        }
                    }
                }
            }
            return sizeCount;
        }
        finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }
    }
    
    private static String getContext(final String updateConfPath) {
        final UpdateManagerParser parser = new UpdateManagerParser(updateConfPath);
        return parser.getGeneralProps().getProperty("SubProductName");
    }
    
    public static void setAutoCloseOnSuccessfulCompletion(final boolean autoClose) {
        UpdateManagerUtil.autoCloseOnSuccessfulCompletion = autoClose;
    }
    
    public static boolean autoCloseOnSuccessfulCompletion() {
        return UpdateManagerUtil.autoCloseOnSuccessfulCompletion;
    }
    
    public static void setAutoCloseDelayTimeInSeconds(final long autoCloseDelayTimeInSeconds) {
        UpdateManagerUtil.autoCloseDelayTimeInSeconds = autoCloseDelayTimeInSeconds;
    }
    
    public static long getAutoCloseDelayTimeInSeconds() {
        return UpdateManagerUtil.autoCloseDelayTimeInSeconds;
    }
    
    public static void audit(final String message) throws IOException {
        final File auditDirPath = new File(getHomeDirectory() + File.separator + "logs" + File.separator + "audit");
        if (!auditDirPath.exists()) {
            UpdateManagerUtil.out.log(Level.INFO, "Creating audit directory :: {0} :: {1}", new Object[] { auditDirPath, auditDirPath.mkdirs() });
        }
        final File auditFile = new File(auditDirPath + File.separator + "um_audit.log");
        if (!auditFile.exists()) {
            UpdateManagerUtil.out.log(Level.INFO, "Creating new audit file :: {0} :: {1}", new Object[] { auditFile, auditFile.createNewFile() });
        }
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(auditFile, true))) {
            writer.write("[" + new Date() + "] [" + getUserInformation().get("USERNAME") + "] " + message + "\n");
        }
    }
    
    public static Map<String, String> getUserInformation() {
        if (UpdateManagerUtil.userInformation != null) {
            return UpdateManagerUtil.userInformation;
        }
        UpdateManagerUtil.userInformation = new HashMap<String, String>();
        Process userNameProc = null;
        try {
            UpdateManagerUtil.userInformation.put("MACHINE NAME", InetAddress.getLocalHost().getHostName());
            String domainName = null;
            final Map<String, String> userInfoMap = System.getenv();
            String userName;
            if (OSCheckUtil.isWindowsPlatform()) {
                userName = userInfoMap.get("USERNAME");
                domainName = userInfoMap.get("USERDOMAIN");
            }
            else {
                userNameProc = executeCommand("id", "-u", "-n");
                try (final BufferedReader userNameReader = new BufferedReader(new InputStreamReader(userNameProc.getInputStream()))) {
                    userName = userNameReader.readLine();
                }
                userNameProc.waitFor();
            }
            if (userName != null) {
                UpdateManagerUtil.userInformation.put("USERNAME", userName);
            }
            if (domainName != null) {
                UpdateManagerUtil.userInformation.put("USERDOMAIN", domainName);
            }
        }
        catch (final Exception exc) {
            UpdateManagerUtil.out.info("Exception occurred while getting user information so returning empty map");
            return Collections.emptyMap();
        }
        finally {
            if (userNameProc != null) {
                userNameProc.destroy();
            }
        }
        return UpdateManagerUtil.userInformation;
    }
    
    private static Process executeCommand(final String... commands) throws Exception {
        return new ProcessBuilder(commands).start();
    }
    
    public static XmlData getXmlData(final Path infXmlPath) {
        if (Files.exists(infXmlPath, new LinkOption[0])) {
            final XmlParser xmlParser = new XmlParser(infXmlPath.toString());
            return xmlParser.getXmlData();
        }
        return new XmlData();
    }
    
    public static XmlData getInfXmlData(final String patchFilePath) {
        try (final ZipFile patchZipFile = new ZipFile(patchFilePath)) {
            final ZipEntry infEntry = patchZipFile.getEntry("inf.xml");
            try (final InputStream is = patchZipFile.getInputStream(infEntry)) {
                final XmlParser parser = new XmlParser(is);
                return parser.getXmlData();
            }
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }
    
    public static InstanceConfig getInstanceConfig(final String instanceConfigPath) throws Exception {
        InstanceConfig instanceConfig;
        if (!new File(instanceConfigPath).exists()) {
            instanceConfig = InstanceConfig.getNewInstance();
            InstanceConfig.write(instanceConfigPath, instanceConfig);
            UpdateManagerUtil.out.log(Level.INFO, "Generated new installation configuration");
        }
        else {
            instanceConfig = InstanceConfig.read(instanceConfigPath);
        }
        CryptoHelper.initialize(instanceConfig.getEncryptionKey());
        return instanceConfig;
    }
    
    public static boolean isContainerOfPatches(final String patchFilePath) throws IOException {
        try (final ZipFile ppmZipFile = new ZipFile(new File(patchFilePath))) {
            return ppmZipFile.getEntry("patches.cp") != null;
        }
    }
    
    static {
        UpdateManagerUtil.out = Logger.getLogger(UpdateManagerUtil.class.getName());
        UpdateManagerUtil.progressCount = 0L;
        UpdateManagerUtil.filesCount = 0L;
        UpdateManagerUtil.spinChars = new char[] { '|', '/', '-', '\\' };
        UpdateManagerUtil.spinCount = 0;
        UpdateManagerUtil.imgCount = 0;
        UpdateManagerUtil.helpXmlFilePath = null;
        UpdateManagerUtil.helpHtmlFilePath = null;
        UpdateManagerUtil.INSTALL_IN_PROGRESS = 1;
        UpdateManagerUtil.INSTALL_COMPLETED = 2;
        UpdateManagerUtil.installState = -1;
        UpdateManagerUtil.revertState = -1;
        UpdateManagerUtil.EXIT_STATUS = 1;
        UpdateManagerUtil.homeDir = ".";
        UpdateManagerUtil.dialog = null;
        UpdateManagerUtil.default_font = null;
        UpdateManagerUtil.bold_font = null;
        UpdateManagerUtil.deploymentEnabled = false;
        UpdateManagerUtil.installStatus = false;
        UpdateManagerUtil.cmdPatchPatch = null;
        UpdateManagerUtil.uninstallList = new ArrayList();
        UpdateManagerUtil.supportUninstalltion = true;
        UpdateManagerUtil.xmlData = null;
        UpdateManagerUtil.keepSubLableText = false;
        UpdateManagerUtil.keepMainLableText = false;
        UpdateManagerUtil.keepFileLableText = false;
        UpdateManagerUtil.state = 0;
        UpdateManagerUtil.currentCompletedPercentage = 0;
        UpdateManagerUtil.lengthyMessageLength = 0;
        UpdateManagerUtil.autoCloseOnSuccessfulCompletion = false;
        UpdateManagerUtil.autoCloseDelayTimeInSeconds = 2L;
        PPMFILEFILTER = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".ppm");
            }
        };
    }
}
