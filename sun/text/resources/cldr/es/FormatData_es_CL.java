package sun.text.resources.cldr.es;

import java.util.ListResourceBundle;

public class FormatData_es_CL extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "TimePatterns", { "HH:mm:ss zzzz", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE, d 'de' MMMM 'de' y", "d 'de' MMMM 'de' y", "dd-MM-yyyy", "dd-MM-yy" } }, { "DefaultNumberingSystem", "latn" }, { "latn.NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "NumberPatterns", { "#,##0.###", "¤#,##0.00;¤-#,##0.00", "#,##0%" } } };
    }
}
