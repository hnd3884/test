package org.apache.xmlbeans;

import java.math.BigDecimal;

public interface GDurationSpecification
{
    boolean isImmutable();
    
    int getSign();
    
    int getYear();
    
    int getMonth();
    
    int getDay();
    
    int getHour();
    
    int getMinute();
    
    int getSecond();
    
    BigDecimal getFraction();
    
    boolean isValid();
    
    int compareToGDuration(final GDurationSpecification p0);
}
