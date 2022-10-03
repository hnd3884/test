package sun.text.resources.cldr.it;

import java.util.ListResourceBundle;

public class FormatData_it_CH extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "TimePatterns", { "HH.mm:ss 'h' zzzz", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d MMMM y", "d MMMM y", "d-MMM-y", "dd.MM.yy" } }, { "DefaultNumberingSystem", "latn" }, { "latn.NumberElements", { ".", "'", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "NumberPatterns", { "#,##0.###", "да#,##0.00;д-#,##0.00", "#,##0%" } } };
    }
}
