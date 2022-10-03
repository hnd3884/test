package sun.java2d.cmm;

import java.awt.color.ICC_Profile;

public interface PCMM
{
    Profile loadProfile(final byte[] p0);
    
    void freeProfile(final Profile p0);
    
    int getProfileSize(final Profile p0);
    
    void getProfileData(final Profile p0, final byte[] p1);
    
    void getTagData(final Profile p0, final int p1, final byte[] p2);
    
    int getTagSize(final Profile p0, final int p1);
    
    void setTagData(final Profile p0, final int p1, final byte[] p2);
    
    ColorTransform createTransform(final ICC_Profile p0, final int p1, final int p2);
    
    ColorTransform createTransform(final ColorTransform[] p0);
}
