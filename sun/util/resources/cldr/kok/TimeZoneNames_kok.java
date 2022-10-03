package sun.util.resources.cldr.kok;

import sun.util.resources.TimeZoneNamesBundle;

public class TimeZoneNames_kok extends TimeZoneNamesBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "\u092d\u093e\u0930\u0924\u0940\u092f \u0938\u092e\u092f", "IST", "India Daylight Time", "IDT", "India Time", "IT" };
        return new Object[][] { { "Asia/Calcutta", array }, { "Asia/Colombo", array } };
    }
}
