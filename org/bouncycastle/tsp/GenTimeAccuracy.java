package org.bouncycastle.tsp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.tsp.Accuracy;

public class GenTimeAccuracy
{
    private Accuracy accuracy;
    
    public GenTimeAccuracy(final Accuracy accuracy) {
        this.accuracy = accuracy;
    }
    
    public int getSeconds() {
        return this.getTimeComponent(this.accuracy.getSeconds());
    }
    
    public int getMillis() {
        return this.getTimeComponent(this.accuracy.getMillis());
    }
    
    public int getMicros() {
        return this.getTimeComponent(this.accuracy.getMicros());
    }
    
    private int getTimeComponent(final ASN1Integer asn1Integer) {
        if (asn1Integer != null) {
            return asn1Integer.getValue().intValue();
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return this.getSeconds() + "." + this.format(this.getMillis()) + this.format(this.getMicros());
    }
    
    private String format(final int n) {
        if (n < 10) {
            return "00" + n;
        }
        if (n < 100) {
            return "0" + n;
        }
        return Integer.toString(n);
    }
}
