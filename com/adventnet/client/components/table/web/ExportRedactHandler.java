package com.adventnet.client.components.table.web;

public interface ExportRedactHandler
{
    String mask(final String p0);
    
    String shuffle(final String p0);
    
    String prefixMask(final String p0, final int p1);
    
    String suffixMask(final String p0, final int p1);
    
    String mask(final String p0, final String p1);
    
    String phone(final String p0);
    
    String email(final String p0);
}
