package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

class UniqueValue
{
    private static int part;
    
    public static String getUniqueBoundaryValue() {
        final StringBuffer s = new StringBuffer();
        s.append("----=_Part_").append(UniqueValue.part++).append("_").append(s.hashCode()).append('.').append(System.currentTimeMillis());
        return s.toString();
    }
    
    static {
        UniqueValue.part = 0;
    }
}
