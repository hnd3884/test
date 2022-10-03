package org.jscep.transaction;

public enum PkiStatus
{
    SUCCESS(0), 
    FAILURE(2), 
    PENDING(3);
    
    private final int value;
    
    private PkiStatus(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static PkiStatus valueOf(final int value) {
        for (final PkiStatus status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}
