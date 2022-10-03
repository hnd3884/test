package com.google.zxing.client.result;

import java.util.List;
import com.google.zxing.Result;

public final class VEventResultParser extends ResultParser
{
    @Override
    public CalendarParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (rawText == null) {
            return null;
        }
        final int vEventStart = rawText.indexOf("BEGIN:VEVENT");
        if (vEventStart < 0) {
            return null;
        }
        final String summary = matchSingleVCardPrefixedField("SUMMARY", rawText, true);
        final String start = matchSingleVCardPrefixedField("DTSTART", rawText, true);
        if (start == null) {
            return null;
        }
        final String end = matchSingleVCardPrefixedField("DTEND", rawText, true);
        final String location = matchSingleVCardPrefixedField("LOCATION", rawText, true);
        final String description = matchSingleVCardPrefixedField("DESCRIPTION", rawText, true);
        final String geoString = matchSingleVCardPrefixedField("GEO", rawText, true);
        double latitude;
        double longitude;
        if (geoString == null) {
            latitude = Double.NaN;
            longitude = Double.NaN;
        }
        else {
            final int semicolon = geoString.indexOf(59);
            try {
                latitude = Double.parseDouble(geoString.substring(0, semicolon));
                longitude = Double.parseDouble(geoString.substring(semicolon + 1));
            }
            catch (final NumberFormatException nfe) {
                return null;
            }
        }
        try {
            return new CalendarParsedResult(summary, start, end, location, null, description, latitude, longitude);
        }
        catch (final IllegalArgumentException iae) {
            return null;
        }
    }
    
    private static String matchSingleVCardPrefixedField(final CharSequence prefix, final String rawText, final boolean trim) {
        final List<String> values = VCardResultParser.matchSingleVCardPrefixedField(prefix, rawText, trim);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }
}
