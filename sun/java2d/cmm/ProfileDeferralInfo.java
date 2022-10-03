package sun.java2d.cmm;

import java.io.IOException;
import java.io.InputStream;

public class ProfileDeferralInfo extends InputStream
{
    public int colorSpaceType;
    public int numComponents;
    public int profileClass;
    public String filename;
    
    public ProfileDeferralInfo(final String filename, final int colorSpaceType, final int numComponents, final int profileClass) {
        this.filename = filename;
        this.colorSpaceType = colorSpaceType;
        this.numComponents = numComponents;
        this.profileClass = profileClass;
    }
    
    @Override
    public int read() throws IOException {
        return 0;
    }
}
