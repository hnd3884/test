package com.google.zxing.client.result;

public final class CalendarParsedResult extends ParsedResult
{
    private final String summary;
    private final String start;
    private final String end;
    private final String location;
    private final String attendee;
    private final String description;
    private final double latitude;
    private final double longitude;
    
    public CalendarParsedResult(final String summary, final String start, final String end, final String location, final String attendee, final String description) {
        this(summary, start, end, location, attendee, description, Double.NaN, Double.NaN);
    }
    
    public CalendarParsedResult(final String summary, final String start, final String end, final String location, final String attendee, final String description, final double latitude, final double longitude) {
        super(ParsedResultType.CALENDAR);
        validateDate(start);
        this.summary = summary;
        this.start = start;
        if (end != null) {
            validateDate(end);
            this.end = end;
        }
        else {
            this.end = null;
        }
        this.location = location;
        this.attendee = attendee;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public String getSummary() {
        return this.summary;
    }
    
    public String getStart() {
        return this.start;
    }
    
    public String getEnd() {
        return this.end;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public String getAttendee() {
        return this.attendee;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public double getLatitude() {
        return this.latitude;
    }
    
    public double getLongitude() {
        return this.longitude;
    }
    
    @Override
    public String getDisplayResult() {
        final StringBuilder result = new StringBuilder(100);
        ParsedResult.maybeAppend(this.summary, result);
        ParsedResult.maybeAppend(this.start, result);
        ParsedResult.maybeAppend(this.end, result);
        ParsedResult.maybeAppend(this.location, result);
        ParsedResult.maybeAppend(this.attendee, result);
        ParsedResult.maybeAppend(this.description, result);
        return result.toString();
    }
    
    private static void validateDate(final CharSequence date) {
        if (date != null) {
            final int length = date.length();
            if (length != 8 && length != 15 && length != 16) {
                throw new IllegalArgumentException();
            }
            for (int i = 0; i < 8; ++i) {
                if (!Character.isDigit(date.charAt(i))) {
                    throw new IllegalArgumentException();
                }
            }
            if (length > 8) {
                if (date.charAt(8) != 'T') {
                    throw new IllegalArgumentException();
                }
                for (int i = 9; i < 15; ++i) {
                    if (!Character.isDigit(date.charAt(i))) {
                        throw new IllegalArgumentException();
                    }
                }
                if (length == 16 && date.charAt(15) != 'Z') {
                    throw new IllegalArgumentException();
                }
            }
        }
    }
}
