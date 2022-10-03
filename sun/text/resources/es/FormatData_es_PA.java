package sun.text.resources.es;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_es_PA extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "hh:mm:ss a z", "hh:mm:ss a z", "hh:mm:ss a", "hh:mm a" } }, { "DatePatterns", { "EEEE d' de 'MMMM' de 'yyyy", "d' de 'MMMM' de 'yyyy", "MM/dd/yyyy", "MM/dd/yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
