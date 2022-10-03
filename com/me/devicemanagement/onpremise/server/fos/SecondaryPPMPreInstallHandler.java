package com.me.devicemanagement.onpremise.server.fos;

import java.util.Arrays;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.Icon;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.io.File;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.adventnet.mfw.Starter;
import java.util.logging.Logger;

public class SecondaryPPMPreInstallHandler
{
    String dcNginxExe;
    String dmRedisExe;
    String nginx;
    String redis;
    String apache;
    private static Logger logger;
    
    public SecondaryPPMPreInstallHandler() {
        this.dcNginxExe = "dcnginx.exe";
        this.dmRedisExe = "dmredis-server.exe";
        this.nginx = "nginx";
        this.redis = "redis";
        this.apache = "apache";
    }
    
    public static void main(final String[] args) {
        if (!Starter.checkShutdownListenerPort()) {
            final String msg = "Cannot kill processes when Central Server is running. Please try again after shutting down the server";
            JOptionPane.showMessageDialog(null, msg, BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.alert", (Locale)null), 2);
            System.exit(5);
        }
        new SecondaryPPMPreInstallHandler().killAssociatedProcesses();
    }
    
    private void killAssociatedProcesses() {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "SERVER HOME " + serverHome);
            final String apacheBin = serverHome + File.separator + this.apache + File.separator + "bin";
            final Boolean isUIMode = true;
            killProcess("dcserverhttpd.exe", 1, apacheBin + File.separator + "dcserverhttpd.exe", isUIMode);
            killProcess("dcrotatelogs.exe", 1, apacheBin + File.separator + "dcrotatelogs.exe", isUIMode);
            killProcess("DesktopCentral.exe", 1, serverHome + File.separator + "bin" + File.separator + "DesktopCentral.exe", isUIMode);
            killProcess("PatchManagerPlus.exe", 1, serverHome + File.separator + "bin" + File.separator + "PatchManagerPlus.exe", isUIMode);
            killProcess("VulnerabilityManagerPlus.exe", 1, serverHome + File.separator + "bin" + File.separator + "VulnerabilityManagerPlus.exe", isUIMode);
            killProcess("UEMS.exe", 1, serverHome + File.separator + "bin" + File.separator + "UEMS.exe", isUIMode);
            killProcess("wrapper.exe", 1, serverHome + File.separator + "bin" + File.separator + "wrapper.exe", isUIMode);
            killProcess("RunAsAdmin.exe", 1, serverHome + File.separator + "bin" + File.separator + "RunAsAdmin.exe", isUIMode);
            killProcess("gettimezone.exe", 1, serverHome + File.separator + "bin" + File.separator + "gettimezone.exe", isUIMode);
            killProcess("RemCom.exe", 1, serverHome + File.separator + "bin" + File.separator + "RemCom.exe", isUIMode);
            final String dcNginxExePath = serverHome + File.separator + this.nginx + File.separator + this.dcNginxExe;
            if (new File(dcNginxExePath).exists()) {
                killProcess("dcnginx.exe", 1, dcNginxExePath, isUIMode);
            }
            final String dmRedisExePath = serverHome + File.separator + this.redis + File.separator + "bin" + File.separator + this.dmRedisExe;
            if (new File(dmRedisExePath).exists()) {
                killProcess(this.dmRedisExe, 1, dmRedisExePath, isUIMode);
            }
            killProcess("MEDCCPUMonitor.exe", 1, serverHome + File.separator + "ServerTroubleShooter" + File.separator + "bin" + File.separator + "MEDCCPUMonitor.exe", isUIMode);
        }
        catch (final IOException exc) {
            SecondaryPPMPreInstallHandler.logger.log(Level.SEVERE, "Exception while killing tasks" + exc);
        }
        catch (final Exception exc2) {
            SecondaryPPMPreInstallHandler.logger.log(Level.SEVERE, "Exception while killing tasks" + exc2);
        }
    }
    
    public static boolean killProcess(final String processName, final int retryCount, final String processPath, final Boolean isUIMode) {
        final String command = "dcwinutil.exe";
        SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "Process path " + processPath);
        SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "Process name " + processName);
        executeCommand(command, "-kill", processName, "" + retryCount, processPath);
        boolean processExists = false;
        processExists = doesProcessExist(processName, processPath);
        if (processExists) {
            SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "Process not killed yet ");
            SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "Process path ", processPath + " process not killed yet, need to kill it manualy using taksmanager");
            final String message = "The process " + processName + " is running. Kill the process from TaskManager and then click Continue";
            SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "Showing alert to User :: {0}", message);
            final Object[] options = { "Continue" };
            if (isUIMode) {
                JOptionPane.showOptionDialog(UpdateManagerUtil.getParent(), message, "Update Manager Warning", -1, 2, null, options, options[0]);
            }
        }
        SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "-----------------------------------------------------------------------------------");
        return !processExists;
    }
    
    public static String executeCommand(final String... commandWithArgs) {
        SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "----------------------- In Execute command ----------------------------");
        String output = "";
        BufferedReader commandOutput = null;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
            SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "COMMAND: {0}", processBuilder.command());
            processBuilder.redirectErrorStream(true);
            final Process process = processBuilder.start();
            commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = "";
            while ((s = commandOutput.readLine()) != null) {
                SecondaryPPMPreInstallHandler.logger.log(Level.INFO, s);
                output += s;
            }
            final int exitValue = process.waitFor();
            SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "EXIT VALUE :: {0}", exitValue);
        }
        catch (final IOException ioe) {
            SecondaryPPMPreInstallHandler.logger.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ioe);
        }
        catch (final InterruptedException ie) {
            SecondaryPPMPreInstallHandler.logger.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ie);
        }
        finally {
            try {
                if (commandOutput != null) {
                    commandOutput.close();
                }
            }
            catch (final Exception exp) {
                SecondaryPPMPreInstallHandler.logger.log(Level.WARNING, "Exception : ", exp);
            }
        }
        SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "---------------------- End of Execute command -------------------------");
        return output;
    }
    
    public static boolean doesProcessExist(final String processName, final String processFullPath) {
        final String task = "dcwinutil.exe";
        final String output = executeCommand(task, "-exists", processName, processFullPath);
        SecondaryPPMPreInstallHandler.logger.log(Level.INFO, "Output from dcwinutil exists command is : " + output);
        final boolean processExists = Boolean.parseBoolean(output.replace(processName + " : ", ""));
        return processExists;
    }
    
    static {
        SecondaryPPMPreInstallHandler.logger = Logger.getLogger(SecondaryPPMPreInstallHandler.class.getName());
    }
}
