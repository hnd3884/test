package java.time.chrono;

import java.time.DateTimeException;

public enum ThaiBuddhistEra implements Era
{
    BEFORE_BE, 
    BE;
    
    public static ThaiBuddhistEra of(final int n) {
        switch (n) {
            case 0: {
                return ThaiBuddhistEra.BEFORE_BE;
            }
            case 1: {
                return ThaiBuddhistEra.BE;
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
