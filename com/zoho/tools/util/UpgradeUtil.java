package com.zoho.tools.util;

import com.adventnet.tools.update.installer.CustomPatchStateTracker;
import com.adventnet.tools.update.installer.PatchInstallationStateTracker;
import java.util.logging.Level;
import com.adventnet.tools.update.installer.PatchInstallationState;
import com.adventnet.tools.update.XmlData;
import java.nio.file.Path;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import com.adventnet.tools.update.installer.ConsoleOut;
import javax.swing.JLabel;
import java.awt.Component;
import com.adventnet.tools.update.UpdateManagerUtil;
import javax.swing.JDialog;
import com.adventnet.tools.update.installer.UpdateManager;
import java.util.logging.Logger;

public class UpgradeUtil
{
    private static final Logger LOGGER;
    
    public static void showMessageOnClient(final String message, int watitimeInMilliseconds) {
        if (UpdateManager.isGUI()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            final String[] splitString = message.split("\\.");
            for (int n = 0; n < splitString.length; ++n) {
                sb.append(splitString[n].trim()).append(".");
                if (n < splitString.length - 1) {
                    sb.append("<br/>");
                }
            }
            sb.append("</html>");
            final JDialog jd = new JDialog();
            jd.setDefaultCloseOperation(0);
            jd.setResizable(false);
            jd.setSize(700, 100);
            jd.setLocationRelativeTo(UpdateManagerUtil.getParent());
            jd.setTitle("Update Manager");
            final JLabel jl = new JLabel(sb.toString(), 0);
            jd.add(jl);
            jd.setVisible(true);
            try {
                if (watitimeInMilliseconds < 1) {
                    watitimeInMilliseconds = 3000;
                }
                Thread.sleep(watitimeInMilliseconds);
            }
            catch (final InterruptedException ex) {}
            jd.dispose();
        }
        else {
            ConsoleOut.println("\n\n" + message + "\n");
        }
    }
    
    public static void extractFileFromPatch(final InputStream is, final String filePath) throws IOException {
        new File(filePath).getParentFile().mkdirs();
        try (final FileOutputStream fos = new FileOutputStream(filePath)) {
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
    }
    
    public static void notifyStatus(final Path patchFilePath, final XmlData infXmlData, final PatchInstallationState installationState) {
        PatchInstallationStateTracker trackerInstance = null;
        final CustomPatchStateTracker customPatchStateTracker = infXmlData.getCustomPatchStateTracker();
        if (customPatchStateTracker != null) {
            try {
                trackerInstance = customPatchStateTracker.getTrackerInstance(patchFilePath);
            }
            catch (final Exception e) {
                UpgradeUtil.LOGGER.log(Level.SEVERE, "Problem while creating patch installation tracker instance", e);
                throw new IllegalArgumentException(e);
            }
            trackerInstance.onNotify(patchFilePath.toString(), installationState);
            try {
                customPatchStateTracker.cleanup();
            }
            catch (final Exception e) {
                UpgradeUtil.LOGGER.log(Level.SEVERE, "Problem while closing the Classloader loaded for updating the status", e);
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(UpgradeUtil.class.getName());
    }
}
