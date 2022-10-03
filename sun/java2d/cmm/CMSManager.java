package sun.java2d.cmm;

import java.awt.color.ICC_Profile;
import sun.security.action.GetPropertyAction;
import java.awt.color.CMMException;
import java.security.AccessController;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.security.PrivilegedAction;
import java.awt.color.ColorSpace;

public class CMSManager
{
    public static ColorSpace GRAYspace;
    public static ColorSpace LINEAR_RGBspace;
    private static PCMM cmmImpl;
    
    public static synchronized PCMM getModule() {
        if (CMSManager.cmmImpl != null) {
            return CMSManager.cmmImpl;
        }
        CMSManager.cmmImpl = AccessController.doPrivileged((PrivilegedAction<CMMServiceProvider>)new PrivilegedAction<CMMServiceProvider>() {
            @Override
            public CMMServiceProvider run() {
                final String property = System.getProperty("sun.java2d.cmm", "sun.java2d.cmm.lcms.LcmsServiceProvider");
                final ServiceLoader<CMMServiceProvider> loadInstalled = ServiceLoader.loadInstalled(CMMServiceProvider.class);
                CMMServiceProvider cmmServiceProvider = null;
                final Iterator<CMMServiceProvider> iterator = loadInstalled.iterator();
                while (iterator.hasNext() && !(cmmServiceProvider = iterator.next()).getClass().getName().equals(property)) {}
                return cmmServiceProvider;
            }
        }).getColorManagementModule();
        if (CMSManager.cmmImpl == null) {
            throw new CMMException("Cannot initialize Color Management System.No CM module found");
        }
        if (AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.cmm.trace")) != null) {
            CMSManager.cmmImpl = new CMMTracer(CMSManager.cmmImpl);
        }
        return CMSManager.cmmImpl;
    }
    
    static synchronized boolean canCreateModule() {
        return CMSManager.cmmImpl == null;
    }
    
    static {
        CMSManager.cmmImpl = null;
    }
    
    public static class CMMTracer implements PCMM
    {
        PCMM tcmm;
        String cName;
        
        public CMMTracer(final PCMM tcmm) {
            this.tcmm = tcmm;
            this.cName = tcmm.getClass().getName();
        }
        
        @Override
        public Profile loadProfile(final byte[] array) {
            System.err.print(this.cName + ".loadProfile");
            final Profile loadProfile = this.tcmm.loadProfile(array);
            System.err.printf("(ID=%s)\n", loadProfile.toString());
            return loadProfile;
        }
        
        @Override
        public void freeProfile(final Profile profile) {
            System.err.printf(this.cName + ".freeProfile(ID=%s)\n", profile.toString());
            this.tcmm.freeProfile(profile);
        }
        
        @Override
        public int getProfileSize(final Profile profile) {
            System.err.print(this.cName + ".getProfileSize(ID=" + profile + ")");
            final int profileSize = this.tcmm.getProfileSize(profile);
            System.err.println("=" + profileSize);
            return profileSize;
        }
        
        @Override
        public void getProfileData(final Profile profile, final byte[] array) {
            System.err.print(this.cName + ".getProfileData(ID=" + profile + ") ");
            System.err.println("requested " + array.length + " byte(s)");
            this.tcmm.getProfileData(profile, array);
        }
        
        @Override
        public int getTagSize(final Profile profile, final int n) {
            System.err.printf(this.cName + ".getTagSize(ID=%x, TagSig=%s)", profile, signatureToString(n));
            final int tagSize = this.tcmm.getTagSize(profile, n);
            System.err.println("=" + tagSize);
            return tagSize;
        }
        
        @Override
        public void getTagData(final Profile profile, final int n, final byte[] array) {
            System.err.printf(this.cName + ".getTagData(ID=%x, TagSig=%s)", profile, signatureToString(n));
            System.err.println(" requested " + array.length + " byte(s)");
            this.tcmm.getTagData(profile, n, array);
        }
        
        @Override
        public void setTagData(final Profile profile, final int n, final byte[] array) {
            System.err.print(this.cName + ".setTagData(ID=" + profile + ", TagSig=" + n + ")");
            System.err.println(" sending " + array.length + " byte(s)");
            this.tcmm.setTagData(profile, n, array);
        }
        
        @Override
        public ColorTransform createTransform(final ICC_Profile icc_Profile, final int n, final int n2) {
            System.err.println(this.cName + ".createTransform(ICC_Profile,int,int)");
            return this.tcmm.createTransform(icc_Profile, n, n2);
        }
        
        @Override
        public ColorTransform createTransform(final ColorTransform[] array) {
            System.err.println(this.cName + ".createTransform(ColorTransform[])");
            return this.tcmm.createTransform(array);
        }
        
        private static String signatureToString(final int n) {
            return String.format("%c%c%c%c", (char)(0xFF & n >> 24), (char)(0xFF & n >> 16), (char)(0xFF & n >> 8), (char)(0xFF & n));
        }
    }
}
