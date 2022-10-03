package com.me.devicemanagement.onpremise.start;

import java.util.Hashtable;
import java.awt.Frame;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.UIManager;
import java.util.logging.Logger;

public class ShowShutdownProcessor implements OutputProcesser
{
    private static final Logger LOGGER;
    private boolean isTerminated;
    ShowShutdownStatus ssStatus;
    
    public ShowShutdownProcessor() {
        this.ssStatus = null;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception occured while trying to show shutdown status", e);
        }
        ShowShutdownProcessor.LOGGER.log(Level.INFO, "Init ShowShutdownProcessor");
    }
    
    @Override
    public boolean hasProcessStarted(final Properties additionalParams) {
        final JOptionPane jop = new JOptionPane("This will shutdown Desktop Central. Do you want to continue?");
        final int i = JOptionPane.showConfirmDialog(new JFrame(), "This will shutdown Desktop Central. Do you want to continue?", "ManageEngine Desktop Central: Confirm Shutdown", 2);
        boolean check = false;
        check = (i != 2 && i != -1);
        ShowShutdownProcessor.LOGGER.log(Level.INFO, "ShowShutdownProcessor: Returning value " + (Object)check);
        if (check) {
            this.ssStatus = new ShowShutdownStatus(new JFrame(), false, additionalParams);
            this.handleAdditionalParams(additionalParams);
        }
        return !check;
    }
    
    private void handleAdditionalParams(final Properties additionalParams) {
        try {
            final String dcomcommand = ((Hashtable<K, String>)additionalParams).get("DCOMCommand");
            Label_0089: {
                if (dcomcommand != null) {
                    try {
                        ShowShutdownProcessor.LOGGER.log(Level.INFO, "STOPPING DCOM SERVICE:::" + dcomcommand);
                        final ProcessBuilder builder = new ProcessBuilder(new String[] { dcomcommand });
                        builder.start();
                        break Label_0089;
                    }
                    catch (final Exception e) {
                        throw new RuntimeException("Error occured while trying to stopping DCOM Service", e);
                    }
                }
                ShowShutdownProcessor.LOGGER.log(Level.INFO, "Command to stop DCOM SERVICE is not given on server shutdown.");
            }
            final String dbcommand = ((Hashtable<K, String>)additionalParams).get("DBCommand");
            if (dbcommand != null) {
                try {
                    ShowShutdownProcessor.LOGGER.log(Level.INFO, "STOPPING DB WITH COMMAND:::" + dbcommand);
                    final ProcessBuilder builder2 = new ProcessBuilder(new String[] { dbcommand });
                    builder2.start();
                    return;
                }
                catch (final Exception e2) {
                    throw new RuntimeException("Error occured while trying to stop DB", e2);
                }
            }
            ShowShutdownProcessor.LOGGER.log(Level.INFO, "Command to stop DB is not given on server shutdown.");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void terminated() {
        this.isTerminated = true;
        this.ssStatus.terminated();
        System.exit(0);
    }
    
    @Override
    public boolean processOutput(final String string) {
        return false;
    }
    
    @Override
    public boolean processError(final String string) {
        return false;
    }
    
    @Override
    public void endStringReached() {
    }
    
    static {
        LOGGER = Logger.getLogger(ShowShutdownProcessor.class.getName());
    }
}
