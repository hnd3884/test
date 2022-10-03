package com.adventnet.tools.prevalent;

import java.io.InputStream;
import java.io.ObjectInputStream;

public class ReadLicense
{
    public LicenseObject readLicense() {
        InputStream ins = null;
        ObjectInputStream s = null;
        try {
            ins = this.getClass().getResourceAsStream("Util.class");
            s = new ObjectInputStream(ins);
            return (LicenseObject)s.readObject();
        }
        catch (final Exception e) {
            e.printStackTrace();
            try {
                if (s != null) {
                    s.close();
                }
                if (ins != null) {
                    ins.close();
                }
            }
            catch (final Exception fe) {
                fe.printStackTrace();
            }
        }
        finally {
            try {
                if (s != null) {
                    s.close();
                }
                if (ins != null) {
                    ins.close();
                }
            }
            catch (final Exception fe2) {
                fe2.printStackTrace();
            }
        }
        return null;
    }
    
    public boolean isAllowedProduct(final int existingId) {
        if (System.getProperty("isMultiPdtSupported") != null && System.getProperty("isMultiPdtSupported").equals("true")) {
            final int[] arr$;
            final int[] licenseIds = arr$ = this.readLicense().getLicenseObject();
            for (final int licenseId : arr$) {
                if (existingId == licenseId) {
                    return true;
                }
            }
        }
        return false;
    }
}
