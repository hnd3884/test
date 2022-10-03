package java.awt.color;

import sun.java2d.cmm.ProfileDeferralInfo;
import sun.java2d.cmm.Profile;

public class ICC_ProfileRGB extends ICC_Profile
{
    static final long serialVersionUID = 8505067385152579334L;
    public static final int REDCOMPONENT = 0;
    public static final int GREENCOMPONENT = 1;
    public static final int BLUECOMPONENT = 2;
    
    ICC_ProfileRGB(final Profile profile) {
        super(profile);
    }
    
    ICC_ProfileRGB(final ProfileDeferralInfo profileDeferralInfo) {
        super(profileDeferralInfo);
    }
    
    public float[] getMediaWhitePoint() {
        return super.getMediaWhitePoint();
    }
    
    public float[][] getMatrix() {
        final float[][] array = new float[3][3];
        final float[] xyzTag = this.getXYZTag(1918392666);
        array[0][0] = xyzTag[0];
        array[1][0] = xyzTag[1];
        array[2][0] = xyzTag[2];
        final float[] xyzTag2 = this.getXYZTag(1733843290);
        array[0][1] = xyzTag2[0];
        array[1][1] = xyzTag2[1];
        array[2][1] = xyzTag2[2];
        final float[] xyzTag3 = this.getXYZTag(1649957210);
        array[0][2] = xyzTag3[0];
        array[1][2] = xyzTag3[1];
        array[2][2] = xyzTag3[2];
        return array;
    }
    
    public float getGamma(final int n) {
        int n2 = 0;
        switch (n) {
            case 0: {
                n2 = 1918128707;
                break;
            }
            case 1: {
                n2 = 1733579331;
                break;
            }
            case 2: {
                n2 = 1649693251;
                break;
            }
            default: {
                throw new IllegalArgumentException("Must be Red, Green, or Blue");
            }
        }
        return super.getGamma(n2);
    }
    
    public short[] getTRC(final int n) {
        int n2 = 0;
        switch (n) {
            case 0: {
                n2 = 1918128707;
                break;
            }
            case 1: {
                n2 = 1733579331;
                break;
            }
            case 2: {
                n2 = 1649693251;
                break;
            }
            default: {
                throw new IllegalArgumentException("Must be Red, Green, or Blue");
            }
        }
        return super.getTRC(n2);
    }
}
