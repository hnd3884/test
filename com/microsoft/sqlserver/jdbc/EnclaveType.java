package com.microsoft.sqlserver.jdbc;

enum EnclaveType
{
    VBS("VBS"), 
    SGX("SGX");
    
    private final String type;
    
    private EnclaveType(final String type) {
        this.type = type;
    }
    
    public int getValue() {
        return this.ordinal() + 1;
    }
    
    static boolean isValidEnclaveType(final String type) {
        for (final EnclaveType t : values()) {
            if (type.equalsIgnoreCase(t.toString())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.type;
    }
}
