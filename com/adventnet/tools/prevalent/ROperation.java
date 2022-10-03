package com.adventnet.tools.prevalent;

import java.util.logging.Level;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.logging.Logger;

public final class ROperation
{
    private static Logger logger;
    private static ROperation rOperation;
    
    private native String getRegValuesNat(final String p0, final String p1);
    
    private native int writeRegValueNat(final String p0, final String p1, final String p2);
    
    private native int removeRegValueNAt(final String p0);
    
    private ROperation() {
    }
    
    public static ROperation getInstance() {
        if (ROperation.rOperation == null) {
            ROperation.rOperation = new ROperation();
        }
        return ROperation.rOperation;
    }
    
    public String getRegValues(final String product, final String version) {
        if (System.getProperty("os.name").startsWith("Win")) {
            return this.getRegValuesNat(product, version);
        }
        return this.getRegValuesUnix(product, version);
    }
    
    public boolean writeRegValue(final String product, final String version, final String value) {
        int i = 0;
        if (System.getProperty("os.name").startsWith("Win")) {
            i = this.writeRegValueNat(product, version, value);
        }
        else {
            i = this.writeRegValueUnix(product, version, value);
        }
        return i != 0;
    }
    
    public static void main(final String[] args) {
    }
    
    private String getRegValuesUnix(final String product, final String version) {
        if (product == null || version == null) {
            return "NULL";
        }
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("/tmp/.");
        strBuff.append(product);
        strBuff.append(version);
        final File regFile = new File(strBuff.toString());
        if (regFile.exists()) {
            try {
                final FileInputStream fis = new FileInputStream(regFile);
                final int BUFSIZE = 1024;
                final byte[] buf = new byte[1024];
                final StringBuffer strBuff2 = new StringBuffer();
                int len;
                while ((len = fis.read(buf)) != -1) {
                    for (int i = 0; i < len; ++i) {
                        strBuff2.append((char)buf[i]);
                    }
                }
                fis.close();
                return strBuff2.toString().trim();
            }
            catch (final Exception ex) {
                return "NULL";
            }
        }
        return "NULL";
    }
    
    private int writeRegValueUnix(final String product, final String version, final String value) {
        if (value == null || product == null || version == null) {
            return 0;
        }
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("/tmp/.");
        strBuff.append(product);
        strBuff.append(version);
        final File regFile = new File(strBuff.toString());
        try {
            final FileOutputStream fos = new FileOutputStream(regFile);
            fos.write(value.getBytes());
            fos.flush();
            fos.close();
            return 1;
        }
        catch (final Exception ex) {
            return 0;
        }
    }
    
    static {
        ROperation.logger = Logger.getLogger(ROperation.class.getName());
        ROperation.rOperation = null;
        try {
            if (System.getProperty("os.name").startsWith("Win")) {
                final File f = new File(LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "AdventnetOper.dll");
                if (f.exists()) {
                    System.load(LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "AdventnetOper.dll");
                }
                else {
                    System.loadLibrary("AdventnetOper");
                }
            }
        }
        catch (final UnsatisfiedLinkError e) {
            ROperation.logger.info("Going to load 64 bit license dll");
            System.loadLibrary("AdventnetOper_64");
        }
        catch (final Exception e2) {
            ROperation.logger.log(Level.SEVERE, e2.getMessage(), e2);
        }
    }
}
