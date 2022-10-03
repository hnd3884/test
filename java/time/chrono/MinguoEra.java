package java.time.chrono;

import java.time.DateTimeException;

public enum MinguoEra implements Era
{
    BEFORE_ROC, 
    ROC;
    
    public static MinguoEra of(final int n) {
        switch (n) {
            case 0: {
                return MinguoEra.BEFORE_ROC;
            }
            case 1: {
                return MinguoEra.ROC;
            }
            default: {
                throw new DateTimeException("Invalid era: " + n);
            }
        }
    }
    
    @Override
    public int getValue() {
        return this.ordinal();
    }
}
