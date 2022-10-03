package com.adventnet.tools.prevalent;

import java.io.InputStream;
import java.io.ObjectInputStream;

public class ReadBaseLicense
{
    public BaseObject readLicense() {
        InputStream ins = null;
        ObjectInputStream s = null;
        try {
            ins = this.getClass().getResourceAsStream("BaseUtil.class");
            s = new ObjectInputStream(ins);
            return (BaseObject)s.readObject();
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
}
