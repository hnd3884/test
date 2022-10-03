package HTTPClient;

public final class NVPair
{
    private String name;
    private String value;
    
    public NVPair(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
    
    public NVPair(final NVPair p) {
        this(p.name, p.value);
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final String getValue() {
        return this.value;
    }
    
    public String toString() {
        return String.valueOf(this.getClass().getName()) + "[name=" + this.name + ",value=" + this.value + "]";
    }
}
