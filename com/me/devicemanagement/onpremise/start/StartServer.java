package com.me.devicemanagement.onpremise.start;

import java.util.Hashtable;
import com.adventnet.mfw.Starter;
import java.io.File;
import com.me.devicemanagement.onpremise.start.util.CheckServerStatus;
import java.util.Iterator;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.Color;
import java.awt.Image;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.InstallUtil;
import java.awt.Window;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import com.me.devicemanagement.onpremise.start.util.ClientUtil;
import java.util.Properties;
import javax.swing.JWindow;
import javax.swing.JProgressBar;
import java.util.logging.Logger;

public class StartServer implements InvokeClass
{
    private static final Logger LOGGER;
    private JProgressBar progressBar;
    private JWindow jw;
    private Properties msgProps;
    String urlStr;
    private String productLabel;
    ClientUtil clientUtil;
    private boolean isServerAlreadyRunning;
    
    public StartServer() {
        this.jw = new JWindow();
        this.msgProps = new Properties();
        this.urlStr = ClientUtil.urlStr;
        this.productLabel = "Server";
        this.clientUtil = new ClientUtil();
    }
    
    public void showSplashScreen(final Properties addionalParams) {
        this.jw.getContentPane().setLayout(new BorderLayout());
        final JLabel imageLabel = new JLabel();
        final ImageIcon splashIcon = new ImageIcon(addionalParams.getProperty("SplashImageFileName"));
        final Image spImg = splashIcon.getImage();
        final int width = spImg.getWidth(splashIcon.getImageObserver());
        final int height = spImg.getHeight(splashIcon.getImageObserver());
        imageLabel.setIcon(splashIcon);
        imageLabel.setSize(width, height);
        (this.progressBar = new JProgressBar()).setStringPainted(true);
        this.progressBar.setBackground(this.getColor(addionalParams.getProperty("BackgroundColor")));
        this.progressBar.setForeground(this.getColor(addionalParams.getProperty("ForegroundColor")));
        this.jw.setSize(width, height + 15);
        this.jw.getContentPane().add(imageLabel, "Center");
        this.jw.getContentPane().add(this.progressBar, "South");
        StartupUtil.centerWindow(this.jw);
        this.jw.setVisible(true);
        final int webPort = InstallUtil.getWebServerPort();
        this.urlStr += webPort;
        try {
            StartServer.LOGGER.log(Level.INFO, "TRYING TO STOP DB WITH COMMAND:::");
        }
        catch (final Exception e) {
            throw new RuntimeException("Error occured while trying to stopping DB", e);
        }
        finally {
            this.showProgress(this.getMessageVector(addionalParams.getProperty("StartupMsgsFileName")));
        }
    }
    
    private Color getColor(final String colorString) {
        if (colorString == null) {
            return Color.white;
        }
        final StringTokenizer rgbTokens = new StringTokenizer(colorString, "-");
        final int r = Integer.parseInt(rgbTokens.nextToken());
        final int g = Integer.parseInt(rgbTokens.nextToken());
        final int b = Integer.parseInt(rgbTokens.nextToken());
        return new Color(r, g, b);
    }
    
    private ArrayList getMessageVector(final String fileName) {
        final ArrayList al = new ArrayList();
        try {
            final RandomAccessFile ram = new RandomAccessFile(fileName, "r");
            String line = null;
            while ((line = ram.readLine()) != null) {
                final StringTokenizer str = new StringTokenizer(line, ",");
                if (str.hasMoreTokens()) {
                    final String msg = str.nextToken();
                    al.add(msg);
                }
            }
        }
        catch (final Exception e) {
            StartServer.LOGGER.log(Level.SEVERE, "Exception while reading Startup messages conf file");
            e.printStackTrace();
        }
        return al;
    }
    
    public void showProgress(final ArrayList msgs) {
        final Iterator msgIte = msgs.iterator();
        int percentage = 0;
        int count = 0;
        this.startIsServerAlreadyRunningCheck(this.urlStr);
        while (percentage < 90) {
            StartServer.LOGGER.log(Level.INFO, "percentage " + percentage);
            if (this.isServerAlreadyRunning) {
                percentage = 90;
            }
            if (count == 0 && msgIte.hasNext()) {
                this.progressBar.setString(msgIte.next() + "");
            }
            this.progressBar.setValue(percentage);
            try {
                Thread.sleep(900L);
                if (count < 4) {
                    ++count;
                }
                else {
                    count = 0;
                }
            }
            catch (final Exception ex) {}
            ++percentage;
        }
        while (!this.isServerAlreadyRunning) {
            try {
                Thread.sleep(500L);
            }
            catch (final Exception ex2) {}
            this.progressBar.setString(this.progressBar.getString() + ".");
        }
        this.progressBar.setString(this.productLabel + " Started" + ".");
        this.progressBar.setValue(100);
        this.progressBar.setString(this.productLabel + " Started" + ".");
        this.jw.setVisible(false);
        this.progressBar = null;
        this.jw = null;
    }
    
    private void startIsServerAlreadyRunningCheck(final String urlStr) {
        final String urlStrToTest = urlStr;
        final Thread serverRunningCheckThread = new Thread("ServerRunningCheckThread") {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (final Exception ex) {}
                    StartServer.this.isServerAlreadyRunning = CheckServerStatus.getInstance().isServerRunningURL();
                    StartServer.LOGGER.log(Level.INFO, "isServerAlreadyRunning " + StartServer.this.isServerAlreadyRunning + " while testing " + urlStrToTest);
                } while (!StartServer.this.isServerAlreadyRunning);
            }
        };
        serverRunningCheckThread.start();
    }
    
    public void checkServerStartup(final Properties addionalParams) {
        final int webPort = InstallUtil.getWebServerPort();
        this.startIsServerAlreadyRunningCheck(this.urlStr += webPort);
        while (!this.isServerAlreadyRunning) {
            try {
                Thread.sleep(500L);
            }
            catch (final Exception ex) {}
        }
    }
    
    private void startDBServer(final Properties addionalParams) {
        final String dbcommand = ((Hashtable<K, String>)addionalParams).get("DBCommand");
        try {
            StartServer.LOGGER.log(Level.INFO, "STARTING DB SERVER: " + dbcommand);
            if (dbcommand == null) {
                StartServer.LOGGER.log(Level.INFO, "WARNING: DBCommand not found in startup args. Assumes the DB is already running...");
            }
            else {
                final ProcessBuilder builder = new ProcessBuilder(new String[] { dbcommand });
                builder.start();
                try {
                    final int dbPort = InstallUtil.getDBPort(null);
                    boolean dbIsReady = false;
                    for (int s = 0; s < 30; ++s) {
                        dbIsReady = InstallUtil.isPortEngaged(dbPort);
                        StartServer.LOGGER.log(Level.INFO, "DB is Ready: " + dbIsReady);
                        if (dbIsReady) {
                            break;
                        }
                        Thread.sleep(900L);
                    }
                }
                catch (final Exception ex) {
                    StartServer.LOGGER.log(Level.INFO, "Caught exception while checking whether DB is ready. " + ex);
                }
            }
        }
        catch (final Exception e) {
            throw new RuntimeException("Error occured while starting DB Server", e);
        }
    }
    
    private Properties getProductProperties() {
        Properties props = null;
        try {
            final String fname = ".." + File.separator + "conf" + File.separator + "product.conf";
            props = StartupUtil.getProperties(fname);
        }
        catch (final Exception ex) {
            StartServer.LOGGER.log(Level.INFO, "Caught exception while getting product properties: " + ex);
        }
        return props;
    }
    
    @Override
    public void executeProgram(final Properties addionalParams, final String[] args) {
        this.productLabel = ((Hashtable<K, String>)addionalParams).get("ProductLabel");
        final String argStr = ((Hashtable<K, String>)addionalParams).get("ARGS");
        final String[] params = { argStr };
        StartServer.LOGGER.log(Level.INFO, "===================================================");
        StartServer.LOGGER.log(Level.INFO, "===================================================");
        StartServer.LOGGER.log(Level.INFO, "===================================================");
        StartServer.LOGGER.log(Level.INFO, "Starting the Desktop Central Server...");
        StartServer.LOGGER.log(Level.INFO, "===================================================");
        StartServer.LOGGER.log(Level.INFO, "Product Details: ");
        StartServer.LOGGER.log(Level.INFO, "" + this.getProductProperties());
        StartServer.LOGGER.log(Level.INFO, "===================================================");
        this.startDBServer(addionalParams);
        final Thread mainThread = new Thread("MainThread") {
            @Override
            public void run() {
                try {
                    StartServer.LOGGER.log(Level.INFO, "Invoking the main class: com.adventnet.mfw.Starter.main()");
                    Starter.main(params);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        };
        StartServer.LOGGER.log(Level.INFO, "Starting mainThread.");
        mainThread.start();
        final String splashImageName = addionalParams.getProperty("SplashImageFileName");
        if (splashImageName != null) {
            StartServer.LOGGER.log(Level.INFO, "Splash image is available...");
            this.showSplashScreen(addionalParams);
        }
        else {
            StartServer.LOGGER.log(Level.INFO, "Splash image is not available...");
            this.checkServerStartup(addionalParams);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(StartServer.class.getName());
    }
}
