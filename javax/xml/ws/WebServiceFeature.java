package javax.xml.ws;

public abstract class WebServiceFeature
{
    protected boolean enabled;
    
    public abstract String getID();
    
    protected WebServiceFeature() {
        this.enabled = false;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
}
