package sun.text.resources.cldr.en;

import java.util.ListResourceBundle;

public class FormatData_en_IN extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "TimePatterns", { "h:mm:ss a zzzz", "h:mm:ss a z", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE d MMMM y", "d MMMM y", "dd-MMM-y", "dd/MM/yy" } }, { "NumberPatterns", { "#,##,##0.###", "да#,##,##0.00", "#,##,##0%" } } };
    }
}
