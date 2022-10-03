package java.awt.color;

import sun.java2d.cmm.ProfileDeferralInfo;
import sun.java2d.cmm.Profile;

public class ICC_ProfileGray extends ICC_Profile
{
    static final long serialVersionUID = -1124721290732002649L;
    
    ICC_ProfileGray(final Profile profile) {
        super(profile);
    }
    
    ICC_ProfileGray(final ProfileDeferralInfo profileDeferralInfo) {
        super(profileDeferralInfo);
    }
    
    public float[] getMediaWhitePoint() {
        return super.getMediaWhitePoint();
    }
    
    public float getGamma() {
        return super.getGamma(1800688195);
    }
    
    public short[] getTRC() {
        return super.getTRC(1800688195);
    }
}
