package com.adventnet.db.adapter;

import java.sql.Connection;

public interface DTAdapter
{
    int getJavaSQLType();
    
    void validateVersion(final Connection p0);
    
    byte[] getBytes(final Object p0);
}
