package com.adventnet.tools.update.installer;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import com.adventnet.tools.update.UpdateManagerUtil;
import javax.swing.JTabbedPane;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import com.adventnet.tools.update.XmlParser;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JPanel;
import com.adventnet.tools.update.CommonUtil;
import javax.swing.JFrame;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.util.logging.Level;
import java.util.zip.ZipFile;
import java.io.File;
import java.util.logging.Logger;

public class ContextReadme
{
    private static final Logger LOG;
    private String readmefilename;
    private String patchVersion;
    private int readMeType;
    
    public ContextReadme() {
        this.readmefilename = null;
        this.patchVersion = null;
        this.readMeType = 1;
    }
    
    public void writeReadmeFile(final File file, final String readme, final String path, final String entry) {
        ZipFile t_zipFile = null;
        try {
            final String readmefilepath = path;
            final String readmefilename = readme;
            final File checkFile = file;
            if (!checkFile.exists()) {
                checkFile.mkdir();
            }
            final File temp = new File(readmefilepath);
            t_zipFile = new ZipFile(temp);
            final ZipEntry infZipEntry1 = t_zipFile.getEntry(entry);
            final boolean flag = false;
            if (!flag) {
                if (infZipEntry1 == null) {
                    return;
                }
                final InputStream unzipper = t_zipFile.getInputStream(infZipEntry1);
                this.writeFile(unzipper, checkFile + File.separator + readmefilename);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            try {
                if (t_zipFile != null) {
                    t_zipFile.close();
                }
            }
            catch (final IOException ioe) {
                ContextReadme.LOG.log(Level.SEVERE, ioe.getMessage(), ioe);
            }
        }
        finally {
            try {
                if (t_zipFile != null) {
                    t_zipFile.close();
                }
            }
            catch (final IOException ioe2) {
                ContextReadme.LOG.log(Level.SEVERE, ioe2.getMessage(), ioe2);
            }
        }
    }
    
    private void writeFile(final InputStream unzipper, final String output) {
        try {
            final int BUFFER = 10240;
            final byte[] data = new byte[10240];
            final BufferedInputStream origin = new BufferedInputStream(unzipper, 10240);
            final FileOutputStream out = new FileOutputStream(output);
            int count;
            while ((count = origin.read(data, 0, 10240)) != -1) {
                out.write(data, 0, count);
            }
            out.close();
            origin.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void displayReadme(final String path, final JFrame frame, String title) {
        final ReadmeUI readme = new ReadmeUI();
        final ReadMeWrapper reme = new ReadMeWrapper(frame);
        reme.setModal(true);
        if (title == null) {
            title = "";
        }
        readme.setReadmeTitle(CommonUtil.getString("The Readme for") + " " + title + " " + CommonUtil.getString("is shown below"));
        reme.setDialogTitle(title + " " + CommonUtil.getString("Readme") + " ");
        reme.init();
        reme.addReadMePanel(readme);
        reme.setPage(path);
        reme.showCornered();
    }
    
    public void displayReadme(final String path, final JDialog frame, String title) {
        final ReadmeUI readme = new ReadmeUI();
        final ReadMeWrapper reme = new ReadMeWrapper(frame);
        reme.setModal(true);
        if (title == null) {
            title = "";
        }
        readme.setReadmeTitle(CommonUtil.getString("The Readme for") + " " + title + " " + CommonUtil.getString("is shown below"));
        reme.setDialogTitle(title + " " + CommonUtil.getString("Readme") + " ");
        reme.init();
        reme.addReadMePanel(readme);
        reme.setPage(path);
        reme.showCornered();
    }
    
    public boolean extractInfFile(final String dirToUnzip, final String patchFile, final JFrame frame) {
        final String patchFilePath = patchFile;
        if (patchFilePath.equals("")) {
            JOptionPane.showMessageDialog(frame, CommonUtil.getString("Select a patch file to be queried"), CommonUtil.getString("Error"), 2);
            return false;
        }
        final File testFileName = new File(patchFilePath);
        if (!testFileName.exists()) {
            JOptionPane.showMessageDialog(frame, CommonUtil.getString("Selected file doesn't exist"), CommonUtil.getString("Error"), 0);
            return false;
        }
        if (!patchFilePath.endsWith(".ppm")) {
            JOptionPane.showMessageDialog(frame, CommonUtil.getString("Select a valid patch file"), CommonUtil.getString("Error"), 0);
            return false;
        }
        final File zipFile = new File(patchFilePath);
        ZipFile t_zipFile = null;
        try {
            t_zipFile = new ZipFile(zipFile);
            final ZipEntry infZipEntry = t_zipFile.getEntry("inf.xml");
            if (infZipEntry == null) {
                JOptionPane.showMessageDialog(frame, CommonUtil.getString("Select a valid patch file"), CommonUtil.getString("Error"), 0);
                return false;
            }
            final InputStream infStream = t_zipFile.getInputStream(infZipEntry);
            final File temp = new File(dirToUnzip + File.separator + "patchtemp");
            if (!temp.exists()) {
                temp.mkdir();
            }
            final String ptemp = dirToUnzip + File.separator + "patchtemp" + File.separator + "inf.xml";
            this.writeFile(infStream, ptemp);
            return true;
        }
        catch (final Exception e) {
            e.printStackTrace();
            try {
                if (t_zipFile != null) {
                    t_zipFile.close();
                }
            }
            catch (final IOException ioe) {
                ContextReadme.LOG.log(Level.SEVERE, ioe.getMessage(), ioe);
            }
        }
        finally {
            try {
                if (t_zipFile != null) {
                    t_zipFile.close();
                }
            }
            catch (final IOException ioe2) {
                ContextReadme.LOG.log(Level.SEVERE, ioe2.getMessage(), ioe2);
            }
        }
        return false;
    }
    
    public String getPatchFileReadme() {
        return this.readmefilename;
    }
    
    public String getPatchVersion() {
        return this.patchVersion;
    }
    
    public void readTheInfFile(final String path) {
        try {
            final String ptemp = path;
            final XmlParser xmlParser = new XmlParser(ptemp);
            this.readMeType = xmlParser.getXmlData().getReadMeType();
            this.readmefilename = xmlParser.getXmlData().getReadMe(this.readMeType);
            if (UpdateManager.getLanguage() != null && UpdateManager.getLanguage().length() == 2 && xmlParser.getXmlData().getLocaleSpecificReadme() != null) {
                final NodeList readmeList = ((Element)xmlParser.getXmlData().getLocaleSpecificReadme()).getElementsByTagName(UpdateManager.getLanguage());
                if (readmeList != null && readmeList.getLength() > 0) {
                    final Node prdNode = readmeList.item(0).getFirstChild();
                    this.readmefilename = prdNode.getNodeValue();
                }
            }
            this.patchVersion = xmlParser.getXmlData().getPatchVersion();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void displayTabbedReadme(final String path, final JDialog frame, final String patchVersion) {
        final ReadmeUI readme = new ReadmeUI();
        final ReadMeWrapper reme = new ReadMeWrapper(frame);
        reme.setModal(true);
        readme.setReadmeTitle(CommonUtil.getString("The Readme for ") + patchVersion + CommonUtil.getString(" is shown below"));
        reme.setDialogTitle(patchVersion + CommonUtil.getString(" Readme "));
        reme.init();
        final JTabbedPane tabbedPane = new JTabbedPane();
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        final String specsFile = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile verProfile = VersionProfile.getInstance();
        verProfile.readDocument(specsFile, false, false);
        final String[] contextArray = verProfile.getTheContext(patchVersion);
        final int arrayLength = contextArray.length;
        final StringBuffer appendLogs = new StringBuffer();
        appendLogs.append(patchVersion + " \n\n");
        int k = 0;
        while (k < arrayLength) {
            final String con = contextArray[k];
            String logName = null;
            if (con.equals("NoContext")) {
                logName = patchVersion + "log.txt";
                final File noContextLogFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "logs" + File.separator + logName);
                final StringBuffer append = this.getTheLogs(noContextLogFile);
                if (append != null) {
                    appendLogs.append(append.toString());
                    break;
                }
                break;
            }
            else {
                logName = patchVersion + con + "log.txt";
                final File noContextLogFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "logs" + File.separator + logName);
                final StringBuffer append = this.getTheLogs(noContextLogFile);
                if (append != null) {
                    appendLogs.append(con + " context\n\n");
                    appendLogs.append(append.toString());
                    appendLogs.append("---------------------------------------------------------------------------------------------------\n\n");
                }
                ++k;
            }
        }
        final LogsUI logsUI = new LogsUI();
        logsUI.init();
        if (appendLogs != null) {
            logsUI.logsTextArea.append(appendLogs.toString());
        }
        tabbedPane.addTab(CommonUtil.getString("Readme"), readme);
        tabbedPane.addTab(CommonUtil.getString("Installed files"), logsUI);
        reme.addTabbedPane(tabbedPane);
        reme.setPage(path);
        reme.showCornered();
    }
    
    public void displayLogsDialog(final ArrayList contextList, final JDialog frame, final String patchVersion) {
        final ReadMeWrapper reme = new ReadMeWrapper(frame);
        reme.setModal(true);
        reme.setDialogTitle(patchVersion);
        reme.init();
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        final Object[] contextArray = contextList.toArray();
        final int arrayLength = contextArray.length;
        final StringBuffer appendLogs = new StringBuffer();
        appendLogs.append(patchVersion + " \n\n");
        int k = 0;
        while (k < arrayLength) {
            final String con = (String)contextArray[k];
            String logName = null;
            if (con.equals("NoContext")) {
                logName = patchVersion + "log.txt";
                final File noContextLogFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "logs" + File.separator + logName);
                final StringBuffer append = this.getTheLogs(noContextLogFile);
                if (append != null) {
                    appendLogs.append(append.toString());
                    break;
                }
                break;
            }
            else {
                logName = patchVersion + con + "log.txt";
                final File noContextLogFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "logs" + File.separator + logName);
                final StringBuffer append = this.getTheLogs(noContextLogFile);
                if (append != null) {
                    appendLogs.append(con + " context\n\n");
                    appendLogs.append(append.toString());
                    appendLogs.append("---------------------------------------------------------------------------------------------------\n\n");
                }
                ++k;
            }
        }
        final LogsUI logsUI = new LogsUI();
        logsUI.init();
        if (appendLogs != null) {
            logsUI.logsTextArea.append(appendLogs.toString());
        }
        reme.addLogsPanel(logsUI);
        reme.showCornered();
    }
    
    public StringBuffer getTheLogs(final File file) {
        if (!file.exists()) {
            return null;
        }
        final StringBuffer strBuffer = new StringBuffer();
        try {
            String buffer = null;
            final BufferedReader is = new BufferedReader(new FileReader(file));
            while (true) {
                buffer = is.readLine();
                if (buffer == null) {
                    break;
                }
                strBuffer.append(buffer + "\n");
            }
        }
        catch (final Exception ioe) {
            ioe.printStackTrace();
        }
        return strBuffer;
    }
    
    public int getReadMeType() {
        return this.readMeType;
    }
    
    static {
        LOG = Logger.getLogger(ContextReadme.class.getName());
    }
}
