package sun.swing;

public class StringUIClientPropertyKey implements UIClientPropertyKey
{
    private final String key;
    
    public StringUIClientPropertyKey(final String key) {
        this.key = key;
    }
    
    @Override
    public String toString() {
        return this.key;
    }
}
