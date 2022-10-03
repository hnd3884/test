package com.adventnet.sym.winutil;

public class ValidationInfo
{
    public int vlType;
    public String vlValue;
    public String vlDN;
    public boolean exclude;
    
    @Override
    public String toString() {
        return "Type :" + this.vlType + " Value :" + this.vlValue + " DN :" + this.vlDN + " exclude :" + this.exclude;
    }
}
