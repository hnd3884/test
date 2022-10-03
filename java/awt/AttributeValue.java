package java.awt;

import sun.util.logging.PlatformLogger;

abstract class AttributeValue
{
    private static final PlatformLogger log;
    private final int value;
    private final String[] names;
    
    protected AttributeValue(final int value, final String[] names) {
        if (AttributeValue.log.isLoggable(PlatformLogger.Level.FINEST)) {
            AttributeValue.log.finest("value = " + value + ", names = " + names);
        }
        if (AttributeValue.log.isLoggable(PlatformLogger.Level.FINER) && (value < 0 || names == null || value >= names.length)) {
            AttributeValue.log.finer("Assertion failed");
        }
        this.value = value;
        this.names = names;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.names[this.value];
    }
    
    static {
        log = PlatformLogger.getLogger("java.awt.AttributeValue");
    }
}
