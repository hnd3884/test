package javax.naming.ldap;

public class BasicControl implements Control
{
    protected String id;
    protected boolean criticality;
    protected byte[] value;
    private static final long serialVersionUID = -4233907508771791687L;
    
    public BasicControl(final String id) {
        this.criticality = false;
        this.value = null;
        this.id = id;
    }
    
    public BasicControl(final String id, final boolean criticality, final byte[] value) {
        this.criticality = false;
        this.value = null;
        this.id = id;
        this.criticality = criticality;
        this.value = value;
    }
    
    @Override
    public String getID() {
        return this.id;
    }
    
    @Override
    public boolean isCritical() {
        return this.criticality;
    }
    
    @Override
    public byte[] getEncodedValue() {
        return this.value;
    }
}
