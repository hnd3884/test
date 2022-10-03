package javax.xml.ws;

public final class RespectBindingFeature extends WebServiceFeature
{
    public static final String ID = "javax.xml.ws.RespectBindingFeature";
    
    public RespectBindingFeature() {
        this.enabled = true;
    }
    
    public RespectBindingFeature(final boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String getID() {
        return "javax.xml.ws.RespectBindingFeature";
    }
}
