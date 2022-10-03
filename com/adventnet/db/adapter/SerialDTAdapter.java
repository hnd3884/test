package com.adventnet.db.adapter;

import java.sql.Connection;

public class SerialDTAdapter implements DTAdapter
{
    @Override
    public void validateVersion(final Connection conn) {
    }
    
    @Override
    public int getJavaSQLType() {
        return -5;
    }
    
    @Override
    public byte[] getBytes(final Object value) {
        return (null != value) ? value.toString().getBytes() : "\\N".getBytes();
    }
}
