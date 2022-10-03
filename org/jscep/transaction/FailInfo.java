package org.jscep.transaction;

public enum FailInfo
{
    badAlg(0), 
    badMessageCheck(1), 
    badRequest(2), 
    badTime(3), 
    badCertId(4);
    
    private final int value;
    
    private FailInfo(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
    
    public static FailInfo valueOf(final int value) {
        for (final FailInfo failInfo : values()) {
            if (failInfo.getValue() == value) {
                return failInfo;
            }
        }
        return FailInfo.badRequest;
    }
}
