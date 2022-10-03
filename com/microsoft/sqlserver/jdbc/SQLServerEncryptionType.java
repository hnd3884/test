package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;

enum SQLServerEncryptionType
{
    Deterministic((byte)1), 
    Randomized((byte)2), 
    PlainText((byte)0);
    
    final byte value;
    private static final SQLServerEncryptionType[] VALUES;
    
    private SQLServerEncryptionType(final byte val) {
        this.value = val;
    }
    
    byte getValue() {
        return this.value;
    }
    
    static SQLServerEncryptionType of(final byte val) throws SQLServerException {
        for (final SQLServerEncryptionType type : SQLServerEncryptionType.VALUES) {
            if (val == type.value) {
                return type;
            }
        }
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unknownColumnEncryptionType"));
        final Object[] msgArgs = { val };
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
        return null;
    }
    
    static {
        VALUES = values();
    }
}
