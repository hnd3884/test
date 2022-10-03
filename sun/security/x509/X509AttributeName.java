package sun.security.x509;

public class X509AttributeName
{
    private static final char SEPARATOR = '.';
    private String prefix;
    private String suffix;
    
    public X509AttributeName(final String prefix) {
        this.prefix = null;
        this.suffix = null;
        final int index = prefix.indexOf(46);
        if (index < 0) {
            this.prefix = prefix;
        }
        else {
            this.prefix = prefix.substring(0, index);
            this.suffix = prefix.substring(index + 1);
        }
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
}
