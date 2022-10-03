package com.adventnet.sym.server.mdm.inv;

public enum ProcessorType
{
    INTEL_MAC("APL1023"), 
    INTEL_WITH_T2_CHIP("APL1027"), 
    SILICON_M1_MAC("APL1102");
    
    public final String alias;
    
    private ProcessorType(final String alias) {
        this.alias = alias;
    }
}
