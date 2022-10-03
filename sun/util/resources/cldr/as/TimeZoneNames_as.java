package sun.util.resources.cldr.as;

import sun.util.resources.TimeZoneNamesBundle;

public class TimeZoneNames_as extends TimeZoneNamesBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "\u09ad\u09be\u09f0\u09a4\u09c0\u09af\u09bc \u09b8\u09ae\u09af\u09bc", "\u09ad\u09be. \u09b8.", "India Daylight Time", "IDT", "India Time", "IT" };
        return new Object[][] { { "Asia/Calcutta", array }, { "Asia/Colombo", array } };
    }
}
