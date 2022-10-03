package java.awt.font;

import java.awt.Font;

public interface MultipleMaster
{
    int getNumDesignAxes();
    
    float[] getDesignAxisRanges();
    
    float[] getDesignAxisDefaults();
    
    String[] getDesignAxisNames();
    
    Font deriveMMFont(final float[] p0);
    
    Font deriveMMFont(final float[] p0, final float p1, final float p2, final float p3, final float p4);
}
