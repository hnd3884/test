package com.me.uem.announcement;

public class AnnouncementException extends Exception
{
    private String errorMsg;
    public static final String INVALID_SPAN_END_INFO = "Invalid end time";
    public static final String INVALID_SPAN = "Invalid start and end time";
    public static final String INVALID_NBAR_INFO = "Invalid information for notification bar";
    public static final String INVALID_DETAILED_INFO = "Invalid information for detailed message";
    public static final String INVALID_ANNOUNCEMENT_FORMAT = "Invalid announcement format";
    
    public AnnouncementException() {
    }
    
    public AnnouncementException(final String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    @Override
    public String toString() {
        return this.errorMsg;
    }
}
