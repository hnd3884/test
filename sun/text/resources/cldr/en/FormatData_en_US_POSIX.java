package sun.text.resources.cldr.en;

import java.util.ListResourceBundle;

public class FormatData_en_US_POSIX extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "DefaultNumberingSystem", "latn" }, { "latn.NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "0/00", "INF", "NaN" } }, { "NumberPatterns", { "#0.######", "��#0.00", "#0%" } } };
    }
}
