package sun.util.resources.cldr;

import java.util.ListResourceBundle;

public class CalendarData extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "firstDayOfWeek", "1" }, { "minimalDaysInFirstWeek", "1" } };
    }
}
