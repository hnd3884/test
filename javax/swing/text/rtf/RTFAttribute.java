package javax.swing.text.rtf;

import java.io.IOException;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

interface RTFAttribute
{
    public static final int D_CHARACTER = 0;
    public static final int D_PARAGRAPH = 1;
    public static final int D_SECTION = 2;
    public static final int D_DOCUMENT = 3;
    public static final int D_META = 4;
    
    int domain();
    
    Object swingName();
    
    String rtfName();
    
    boolean set(final MutableAttributeSet p0);
    
    boolean set(final MutableAttributeSet p0, final int p1);
    
    boolean setDefault(final MutableAttributeSet p0);
    
    boolean write(final AttributeSet p0, final RTFGenerator p1, final boolean p2) throws IOException;
    
    boolean writeValue(final Object p0, final RTFGenerator p1, final boolean p2) throws IOException;
}
