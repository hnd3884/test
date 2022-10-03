package sun.util.locale;

class Extension
{
    private final char key;
    private String value;
    private String id;
    
    protected Extension(final char key) {
        this.key = key;
    }
    
    Extension(final char key, final String value) {
        this.key = key;
        this.setValue(value);
    }
    
    protected void setValue(final String value) {
        this.value = value;
        this.id = this.key + "-" + value;
    }
    
    public char getKey() {
        return this.key;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getID() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return this.getID();
    }
}
