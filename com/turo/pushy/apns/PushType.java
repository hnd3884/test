package com.turo.pushy.apns;

public enum PushType
{
    ALERT("alert"), 
    BACKGROUND("background"), 
    VOIP("voip"), 
    COMPLICATION("complication"), 
    FILEPROVIDER("fileprovider"), 
    MDM("mdm");
    
    private final String headerValue;
    
    private PushType(final String headerValue) {
        this.headerValue = headerValue;
    }
    
    public String getHeaderValue() {
        return this.headerValue;
    }
    
    public static PushType getFromHeaderValue(final CharSequence headerValue) {
        for (final PushType pushType : values()) {
            if (pushType.headerValue.contentEquals(headerValue)) {
                return pushType;
            }
        }
        throw new IllegalArgumentException("No push type found for header value: " + (Object)headerValue);
    }
}
