package com.me.devicemanagement.onpremise.server.util;

import java.util.regex.Pattern;
import java.io.StringWriter;
import java.io.File;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PgsqlHbaConfUtil
{
    public static final String COMMENT_CHAR = "#";
    public static final String FIELD_SEPARATOR = "\t ";
    public static final String TYPE = "TYPE";
    public static final String DATABASE = "DATABASE";
    public static final String USER = "USER";
    public static final String ADDRESS = "ADDRESS";
    public static final String METHOD = "METHOD";
    public static final String MEDC = "medc";
    private String confFileWithPath;
    private static Logger logger;
    private static String[] hbaFieldsInOrder;
    public final Object lockObject;
    
    public PgsqlHbaConfUtil(final String confFile) {
        this.confFileWithPath = null;
        this.lockObject = new Object();
        this.confFileWithPath = confFile;
    }
    
    public PgsqlHbaConfUtil() {
        this.confFileWithPath = null;
        this.lockObject = new Object();
    }
    
    public void grantAccessForHost(final String hostName) {
        this.grantAccessForHost(hostName, null);
    }
    
    public void grantAccessForHost(final String hostName, final String userName) {
        PgsqlHbaConfUtil.logger.log(Level.INFO, "Going to grant access Read Only User " + userName + "to Remote computer name" + hostName);
        final Properties pr = this.createDefaultRecord();
        if (userName != null) {
            pr.setProperty("USER", userName);
        }
        this.addOrUpdateHbaRecord(hostName, pr);
    }
    
    public static Boolean isPgHbaTempEnabled() {
        boolean isPgHbaTempEnabled = false;
        try {
            final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            if (frameworkConfigurations.has("pg_hba_temp_configuration") && ((JSONObject)frameworkConfigurations.get("pg_hba_temp_configuration")).has("pg_hba_temp_conf_enabled")) {
                isPgHbaTempEnabled = Boolean.parseBoolean(String.valueOf(((JSONObject)frameworkConfigurations.get("pg_hba_temp_configuration")).get("pg_hba_temp_conf_enabled")));
            }
        }
        catch (final Exception e) {
            PgsqlHbaConfUtil.logger.log(Level.INFO, "Exception while reading the pg_hba_temp_conf_enabled property: ", e);
        }
        return isPgHbaTempEnabled;
    }
    
    private void addOrUpdateHbaRecord(final String hostName, final Properties props) {
        try {
            final String hostOrIPAddress = this.addCIDRMaskToIPAddress(hostName);
            props.setProperty("ADDRESS", hostOrIPAddress);
            final List<Properties> hbaRecords = this.getHbaRecords();
            boolean isExistingAddr = false;
            final List<Properties> hbaRecordsNew = new ArrayList<Properties>();
            for (int s = 0; s < hbaRecords.size(); ++s) {
                final Properties pr = hbaRecords.get(s);
                final String hbaHost = pr.getProperty("ADDRESS");
                if (hbaHost != null && hbaHost.trim().equalsIgnoreCase(hostOrIPAddress)) {
                    isExistingAddr = true;
                    hbaRecordsNew.add(props);
                }
                else {
                    hbaRecordsNew.add(pr);
                }
            }
            if (!isExistingAddr) {
                hbaRecordsNew.add(props);
            }
            this.writeHbaRecords(hbaRecordsNew);
            PgsqlHbaConfUtil.logger.log(Level.INFO, "HBA records written successfully for hostname: " + hostOrIPAddress);
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception while adding HBA record for hostname: " + hostName, ex);
        }
    }
    
    public boolean revokeAccessForHost(final String hostName) {
        boolean revokeStatus = false;
        try {
            final List<Properties> hbaRecords = this.getHbaRecords();
            final List<Properties> hbaRecordsNew = new ArrayList<Properties>();
            final String hostOrIPAddress = this.addCIDRMaskToIPAddress(hostName);
            for (int s = 0; s < hbaRecords.size(); ++s) {
                final Properties pr = hbaRecords.get(s);
                final String hbaHost = pr.getProperty("ADDRESS");
                if (hbaHost != null && hbaHost.equalsIgnoreCase(hostOrIPAddress)) {
                    revokeStatus = true;
                }
                else {
                    hbaRecordsNew.add(pr);
                }
            }
            if (revokeStatus) {
                this.writeHbaRecords(hbaRecordsNew);
            }
            PgsqlHbaConfUtil.logger.log(Level.INFO, "HBA records written successfully for hostname: " + hostName);
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception while adding HBA record for hostname: " + hostName, ex);
        }
        return revokeStatus;
    }
    
    private void writeHbaRecords(final List<Properties> hbaRecords) throws IOException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            synchronized (this.lockObject) {
                fos = new FileOutputStream(this.confFileWithPath);
                osw = new OutputStreamWriter(fos);
                bw = new BufferedWriter(osw);
                final String headerComment = this.constructHbaRecordHeaderComment();
                bw.append((CharSequence)headerComment);
                bw.newLine();
                for (int s = 0; s < hbaRecords.size(); ++s) {
                    final Properties pr = hbaRecords.get(s);
                    final String rline = this.constructHbaRecordAsString(pr);
                    bw.append((CharSequence)rline);
                    bw.newLine();
                }
            }
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught Exception while writing HBA records...", ex);
        }
        finally {
            if (bw != null) {
                bw.close();
            }
            if (osw != null) {
                osw.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        if (!isPgHbaTempEnabled()) {
            this.reloadHBAConf();
        }
    }
    
    private String constructHbaRecordAsString(final Properties hbaRecord) {
        String hbaRecStr = "";
        for (int s = 0; s < PgsqlHbaConfUtil.hbaFieldsInOrder.length; ++s) {
            final String key = PgsqlHbaConfUtil.hbaFieldsInOrder[s];
            final String value = hbaRecord.getProperty(key);
            hbaRecStr = hbaRecStr + "\t " + value;
        }
        hbaRecStr = hbaRecStr.substring("\t ".length());
        return hbaRecStr;
    }
    
    private String constructHbaRecordHeaderComment() {
        String hbaRecHdr = "#";
        for (int s = 0; s < PgsqlHbaConfUtil.hbaFieldsInOrder.length; ++s) {
            final String key = PgsqlHbaConfUtil.hbaFieldsInOrder[s];
            hbaRecHdr = hbaRecHdr + "\t " + key;
        }
        return hbaRecHdr;
    }
    
    public List getGrantedHostList() {
        final List<String> hostList = new ArrayList<String>();
        try {
            final List<Properties> hbaList = this.getHbaRecords();
            if (hbaList == null || hbaList.size() <= 0) {
                return hostList;
            }
            for (int s = 0; s < hbaList.size(); ++s) {
                final Properties pr = hbaList.get(s);
                hostList.add(pr.getProperty("ADDRESS"));
            }
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception while retrieving host names...", ex);
        }
        return hostList;
    }
    
    private List getHbaRecords() throws IOException {
        final List<Properties> hbaRecs = new ArrayList<Properties>();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(this.confFileWithPath);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String rline = null;
            while ((rline = br.readLine()) != null) {
                if (!rline.trim().startsWith("#")) {
                    if (rline.trim().length() <= 0) {
                        continue;
                    }
                    final Properties pr = this.parseHbaRecord(rline);
                    hbaRecs.add(pr);
                }
            }
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught Exception while reading HBA records...", ex);
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return hbaRecs;
    }
    
    private Properties parseHbaRecord(final String strRecord) {
        Properties pr = null;
        try {
            if (strRecord == null || strRecord.trim().length() == 0) {
                return pr;
            }
            pr = new Properties();
            final String[] rdArr = strRecord.split(" +");
            if (rdArr.length < PgsqlHbaConfUtil.hbaFieldsInOrder.length) {
                return pr;
            }
            for (int s = 0; s < rdArr.length; ++s) {
                pr.setProperty(PgsqlHbaConfUtil.hbaFieldsInOrder[s], rdArr[s].trim());
            }
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception while parsing the record string: " + strRecord, ex);
        }
        return pr;
    }
    
    private void reloadHBAConf() {
        InputStream is = null;
        InputStream es = null;
        InputStreamReader isr = null;
        InputStreamReader esr = null;
        BufferedReader isbr = null;
        BufferedReader esbr = null;
        try {
            int exitStatus = -1;
            final String binDir = this.getDBHome() + File.separator + "bin" + File.separator;
            final String[] command = { binDir, "pg_ctl", "reload", "-D", this.getDBHome() + File.separator + "data" };
            PgsqlHbaConfUtil.logger.log(Level.INFO, "Executing the command: " + command);
            final ProcessBuilder builder = new ProcessBuilder(command);
            final Process pr = builder.start();
            is = pr.getInputStream();
            es = pr.getErrorStream();
            isr = new InputStreamReader(is);
            esr = new InputStreamReader(es);
            isbr = new BufferedReader(isr);
            esbr = new BufferedReader(esr);
            final StringWriter isw = new StringWriter();
            final StringWriter esw = new StringWriter();
            String input = null;
            while ((input = isbr.readLine()) != null) {
                isw.append(input);
            }
            while ((input = esbr.readLine()) != null) {
                esw.append(input);
            }
            PgsqlHbaConfUtil.logger.log(Level.INFO, "Command output stream: " + isw.toString());
            PgsqlHbaConfUtil.logger.log(Level.INFO, "Command error stream: " + esw.toString());
            exitStatus = pr.waitFor();
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception while reloading the conf file: " + this.confFileWithPath, ex);
            if (isbr != null) {
                try {
                    isbr.close();
                }
                catch (final Exception ex) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (esbr != null) {
                try {
                    esbr.close();
                }
                catch (final Exception ex) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                }
                catch (final Exception ex) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (esr != null) {
                try {
                    esr.close();
                }
                catch (final Exception ex) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ex) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
            if (es != null) {
                try {
                    es.close();
                }
                catch (final Exception ex) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex);
                }
            }
        }
        finally {
            if (isbr != null) {
                try {
                    isbr.close();
                }
                catch (final Exception ex2) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (esbr != null) {
                try {
                    esbr.close();
                }
                catch (final Exception ex2) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                }
                catch (final Exception ex2) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (esr != null) {
                try {
                    esr.close();
                }
                catch (final Exception ex2) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ex2) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
            if (es != null) {
                try {
                    es.close();
                }
                catch (final Exception ex2) {
                    PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception: ", ex2);
                }
            }
        }
    }
    
    private Properties createDefaultRecord() {
        final Properties props = new Properties();
        props.setProperty("TYPE", "host");
        props.setProperty("DATABASE", "all");
        props.setProperty("USER", "all");
        props.setProperty("ADDRESS", "localhost");
        props.setProperty("METHOD", "md5");
        return props;
    }
    
    private String getDBHome() {
        String pgsqlHome = ".." + File.separator + "pgsql";
        try {
            pgsqlHome = new File(System.getProperty("pgsql.home")).getCanonicalPath();
            PgsqlHbaConfUtil.logger.log(Level.INFO, "DB Home retrieved: " + pgsqlHome);
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception while getting the DB Home...", ex);
        }
        return pgsqlHome;
    }
    
    public String addCIDRMaskToIPAddress(final String address) {
        PgsqlHbaConfUtil.logger.log(Level.INFO, "Machine name or ip address : " + address);
        String result = address;
        try {
            final String IPv4 = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
            final String IPv5 = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
            final Pattern ipv4 = Pattern.compile(IPv4);
            final Pattern ipv5 = Pattern.compile(IPv5);
            if (ipv4.matcher(address).matches()) {
                PgsqlHbaConfUtil.logger.log(Level.INFO, "Given IP Address : " + address + " is IPv4");
                result = address + "/32";
            }
            else if (ipv5.matcher(address).matches()) {
                PgsqlHbaConfUtil.logger.log(Level.INFO, "Given IP Address : " + address + " is IPv6");
                result = address + "/128";
            }
            else {
                result = address;
            }
        }
        catch (final Exception ex) {
            PgsqlHbaConfUtil.logger.log(Level.WARNING, "Caught exception while adding CIDR Mask in ip address : " + ex);
        }
        PgsqlHbaConfUtil.logger.log(Level.INFO, "After adding cidr mask result : " + result);
        return result;
    }
    
    static {
        PgsqlHbaConfUtil.logger = Logger.getLogger("RemoteDBAccessLog");
        PgsqlHbaConfUtil.hbaFieldsInOrder = new String[] { "TYPE", "DATABASE", "USER", "ADDRESS", "METHOD" };
    }
}
