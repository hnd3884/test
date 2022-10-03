package sun.java2d.cmm;

import java.util.Iterator;
import java.awt.color.ProfileDataException;
import java.util.Vector;

public class ProfileDeferralMgr
{
    public static boolean deferring;
    private static Vector<ProfileActivator> aVector;
    
    public static void registerDeferral(final ProfileActivator profileActivator) {
        if (!ProfileDeferralMgr.deferring) {
            return;
        }
        if (ProfileDeferralMgr.aVector == null) {
            ProfileDeferralMgr.aVector = new Vector<ProfileActivator>(3, 3);
        }
        ProfileDeferralMgr.aVector.addElement(profileActivator);
    }
    
    public static void unregisterDeferral(final ProfileActivator profileActivator) {
        if (!ProfileDeferralMgr.deferring) {
            return;
        }
        if (ProfileDeferralMgr.aVector == null) {
            return;
        }
        ProfileDeferralMgr.aVector.removeElement(profileActivator);
    }
    
    public static void activateProfiles() {
        ProfileDeferralMgr.deferring = false;
        if (ProfileDeferralMgr.aVector == null) {
            return;
        }
        ProfileDeferralMgr.aVector.size();
        for (final ProfileActivator profileActivator : ProfileDeferralMgr.aVector) {
            try {
                profileActivator.activate();
            }
            catch (final ProfileDataException ex) {}
        }
        ProfileDeferralMgr.aVector.removeAllElements();
        ProfileDeferralMgr.aVector = null;
    }
    
    static {
        ProfileDeferralMgr.deferring = true;
    }
}
