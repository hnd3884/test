package sun.nio.cs.ext;

import java.nio.charset.Charset;

public class MS50221 extends MS50220
{
    public MS50221() {
        super("x-windows-50221", ExtendedCharsets.aliasesFor("x-windows-50221"));
    }
    
    @Override
    public String historicalName() {
        return "MS50221";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return super.contains(charset) || charset instanceof JIS_X_0212 || charset instanceof MS50221;
    }
    
    @Override
    protected boolean doSBKANA() {
        return true;
    }
}
