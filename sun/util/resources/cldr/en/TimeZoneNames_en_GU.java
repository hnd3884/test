package sun.util.resources.cldr.en;

import sun.util.resources.TimeZoneNamesBundle;

public class TimeZoneNames_en_GU extends TimeZoneNamesBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "Chamorro Standard Time", "ChST", "Chamorro Daylight Time", "CDT", "Chamorro Time", "CT" };
        return new Object[][] { { "Pacific/Guam", array }, { "Pacific/Saipan", array } };
    }
}
