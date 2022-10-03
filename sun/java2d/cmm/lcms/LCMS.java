package sun.java2d.cmm.lcms;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.java2d.cmm.ColorTransform;
import java.awt.color.ICC_Profile;
import java.awt.color.CMMException;
import sun.java2d.cmm.Profile;
import sun.java2d.cmm.PCMM;

public class LCMS implements PCMM
{
    private static LCMS theLcms;
    
    @Override
    public Profile loadProfile(final byte[] array) {
        final Object o = new Object();
        final long loadProfileNative = this.loadProfileNative(array, o);
        if (loadProfileNative != 0L) {
            return new LCMSProfile(loadProfileNative, o);
        }
        return null;
    }
    
    private native long loadProfileNative(final byte[] p0, final Object p1);
    
    private LCMSProfile getLcmsProfile(final Profile profile) {
        if (profile instanceof LCMSProfile) {
            return (LCMSProfile)profile;
        }
        throw new CMMException("Invalid profile: " + profile);
    }
    
    @Override
    public void freeProfile(final Profile profile) {
    }
    
    @Override
    public int getProfileSize(final Profile profile) {
        synchronized (profile) {
            return this.getProfileSizeNative(this.getLcmsProfile(profile).getLcmsPtr());
        }
    }
    
    private native int getProfileSizeNative(final long p0);
    
    @Override
    public void getProfileData(final Profile profile, final byte[] array) {
        synchronized (profile) {
            this.getProfileDataNative(this.getLcmsProfile(profile).getLcmsPtr(), array);
        }
    }
    
    private native void getProfileDataNative(final long p0, final byte[] p1);
    
    @Override
    public int getTagSize(final Profile profile, final int n) {
        final LCMSProfile lcmsProfile = this.getLcmsProfile(profile);
        synchronized (lcmsProfile) {
            final LCMSProfile.TagData tag = lcmsProfile.getTag(n);
            return (tag == null) ? 0 : tag.getSize();
        }
    }
    
    static native byte[] getTagNative(final long p0, final int p1);
    
    @Override
    public void getTagData(final Profile profile, final int n, final byte[] array) {
        final LCMSProfile lcmsProfile = this.getLcmsProfile(profile);
        synchronized (lcmsProfile) {
            final LCMSProfile.TagData tag = lcmsProfile.getTag(n);
            if (tag != null) {
                tag.copyDataTo(array);
            }
        }
    }
    
    @Override
    public synchronized void setTagData(final Profile profile, final int n, final byte[] array) {
        final LCMSProfile lcmsProfile = this.getLcmsProfile(profile);
        synchronized (lcmsProfile) {
            lcmsProfile.clearTagCache();
            this.setTagDataNative(lcmsProfile.getLcmsPtr(), n, array);
        }
    }
    
    private native void setTagDataNative(final long p0, final int p1, final byte[] p2);
    
    public static synchronized native LCMSProfile getProfileID(final ICC_Profile p0);
    
    static long createTransform(final LCMSProfile[] array, final int n, final int n2, final boolean b, final int n3, final boolean b2, final Object o) {
        final long[] array2 = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new CMMException("Unknown profile ID");
            }
            array2[i] = array[i].getLcmsPtr();
        }
        return createNativeTransform(array2, n, n2, b, n3, b2, o);
    }
    
    private static native long createNativeTransform(final long[] p0, final int p1, final int p2, final boolean p3, final int p4, final boolean p5, final Object p6);
    
    @Override
    public ColorTransform createTransform(final ICC_Profile icc_Profile, final int n, final int n2) {
        return new LCMSTransform(icc_Profile, n, n);
    }
    
    @Override
    public synchronized ColorTransform createTransform(final ColorTransform[] array) {
        return new LCMSTransform(array);
    }
    
    public static native void colorConvert(final LCMSTransform p0, final LCMSImageLayout p1, final LCMSImageLayout p2);
    
    public static native void freeTransform(final long p0);
    
    public static native void initLCMS(final Class p0, final Class p1, final Class p2);
    
    private LCMS() {
    }
    
    static synchronized PCMM getModule() {
        if (LCMS.theLcms != null) {
            return LCMS.theLcms;
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                System.loadLibrary("awt");
                System.loadLibrary("lcms");
                return null;
            }
        });
        initLCMS(LCMSTransform.class, LCMSImageLayout.class, ICC_Profile.class);
        return LCMS.theLcms = new LCMS();
    }
    
    static {
        LCMS.theLcms = null;
    }
}
