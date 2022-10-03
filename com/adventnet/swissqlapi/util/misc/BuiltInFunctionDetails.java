package com.adventnet.swissqlapi.util.misc;

public interface BuiltInFunctionDetails
{
    String getReturnDataType(final String p0);
    
    String getParameterDataType(final String p0, final int p1);
}
