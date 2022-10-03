package com.me.mdm.api.admin.keypair;

public enum KeyPairEnumConstants
{
    MIGRATIONTOOL(1);
    
    private int keyType;
    
    private KeyPairEnumConstants(final int keyType) {
        this.keyType = keyType;
    }
    
    public int getKeyType() {
        return this.keyType;
    }
}
