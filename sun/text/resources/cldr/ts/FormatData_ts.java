package sun.text.resources.cldr.ts;

import java.util.ListResourceBundle;

public class FormatData_ts extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "DefaultNumberingSystem", "latn" }, { "latn.NumberElements", { ",", "?", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "NumberPatterns", { "#,##0.###", "?#,##0.00", "#,##0%" } } };
    }
}
