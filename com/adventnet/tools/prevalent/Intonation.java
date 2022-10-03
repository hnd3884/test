package com.adventnet.tools.prevalent;

import java.io.File;

public final class Intonation
{
    private static Intonation mget;
    
    private native String getMAC(final String p0);
    
    private Intonation() {
    }
    
    public static Intonation getInstance() {
        if (Intonation.mget == null) {
            Intonation.mget = new Intonation();
        }
        return Intonation.mget;
    }
    
    public String getTheMAC(final String MacId) {
        final String mac = this.getMAC(MacId);
        return mac;
    }
    
    static {
        Intonation.mget = null;
        if (System.getProperty("os.name").startsWith("Sun")) {
            final File f = new File(LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "libUniqueIDSO.so");
            if (f.exists()) {
                System.load(LUtil.getDir() + File.separator + "lib" + File.separator + "libUniqueIDSO.so");
            }
            else {
                System.loadLibrary("UniqueIDSO");
            }
        }
        else if (System.getProperty("os.name").startsWith("HP")) {
            System.load(LUtil.getDir() + File.separator + "lib" + File.separator + "libUniqueIDHP-UX.so");
        }
        else {
            try {
                System.loadLibrary("UniqueID");
            }
            catch (final UnsatisfiedLinkError er) {
                System.out.println("Licensing : Loading 64 bit library");
                System.loadLibrary("UniqueID_64");
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
