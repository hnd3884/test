package sun.util.resources.cldr.pa;

import sun.util.resources.TimeZoneNamesBundle;

public class TimeZoneNames_pa extends TimeZoneNamesBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "India Standard Time", "IST", "India Daylight Time", "IDT", "India Time", "IT" };
        return new Object[][] { { "Asia/Calcutta", array }, { "Asia/Colombo", array } };
    }
}
