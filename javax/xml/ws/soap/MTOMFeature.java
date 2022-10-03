package javax.xml.ws.soap;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public final class MTOMFeature extends WebServiceFeature
{
    public static final String ID = "http://www.w3.org/2004/08/soap/features/http-optimization";
    protected int threshold;
    
    public MTOMFeature() {
        this.enabled = true;
        this.threshold = 0;
    }
    
    public MTOMFeature(final boolean enabled) {
        this.enabled = enabled;
        this.threshold = 0;
    }
    
    public MTOMFeature(final int threshold) {
        if (threshold < 0) {
            throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: " + threshold);
        }
        this.enabled = true;
        this.threshold = threshold;
    }
    
    public MTOMFeature(final boolean enabled, final int threshold) {
        if (threshold < 0) {
            throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: " + threshold);
        }
        this.enabled = enabled;
        this.threshold = threshold;
    }
    
    @Override
    public String getID() {
        return "http://www.w3.org/2004/08/soap/features/http-optimization";
    }
    
    public int getThreshold() {
        return this.threshold;
    }
}
