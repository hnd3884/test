package org.postgresql.util;

import java.sql.SQLException;

public interface PGBinaryObject
{
    void setByteValue(final byte[] p0, final int p1) throws SQLException;
    
    int lengthInBytes();
    
    void toBytes(final byte[] p0, final int p1);
}
