package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.PowerPC;

public class PowerPCOptions extends BCJOptions
{
    private static final int ALIGNMENT = 4;
    
    public PowerPCOptions() {
        super(4);
    }
    
    @Override
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream, final ArrayCache arrayCache) {
        return new SimpleOutputStream(finishableOutputStream, new PowerPC(true, this.startOffset));
    }
    
    @Override
    public InputStream getInputStream(final InputStream inputStream, final ArrayCache arrayCache) {
        return new SimpleInputStream(inputStream, new PowerPC(false, this.startOffset));
    }
    
    @Override
    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, 5L);
    }
}
