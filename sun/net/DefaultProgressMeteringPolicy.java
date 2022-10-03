package sun.net;

import java.net.URL;

class DefaultProgressMeteringPolicy implements ProgressMeteringPolicy
{
    @Override
    public boolean shouldMeterInput(final URL url, final String s) {
        return false;
    }
    
    @Override
    public int getProgressUpdateThreshold() {
        return 8192;
    }
}
