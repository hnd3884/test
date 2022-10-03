package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.ARM;

public class ARMOptions extends BCJOptions
{
    private static final int ALIGNMENT = 4;
    
    public ARMOptions() {
        super(4);
    }
    
    @Override
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream, final ArrayCache arrayCache) {
        return new SimpleOutputStream(finishableOutputStream, new ARM(true, this.startOffset));
    }
    
    @Override
    public InputStream getInputStream(final InputStream inputStream, final ArrayCache arrayCache) {
        return new SimpleInputStream(inputStream, new ARM(false, this.startOffset));
    }
    
    @Override
    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, 7L);
    }
}
