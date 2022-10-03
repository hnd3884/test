package sun.text.resources.cldr.en;

import java.util.ListResourceBundle;

public class FormatData_en_GB extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "TimePatterns", { "HH:mm:ss zzzz", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d MMMM y", "d MMMM y", "d MMM y", "dd/MM/yyyy" } }, { "java.time.buddhist.DatePatterns", { "EEEE, d MMMM y G", "d MMMM y G", "d MMM y G", "dd/MM/y G" } }, { "buddhist.DatePatterns", { "EEEE, d MMMM y GGGG", "d MMMM y GGGG", "d MMM y GGGG", "dd/MM/y GGGG" } }, { "java.time.japanese.DatePatterns", { "EEEE, d MMMM y G", "d MMMM y G", "d MMM y G", "dd/MM/y GGGGG" } }, { "japanese.DatePatterns", { "EEEE, d MMMM y GGGG", "d MMMM y GGGG", "d MMM y GGGG", "dd/MM/y G" } }, { "java.time.roc.DatePatterns", { "EEEE, d MMMM y G", "d MMMM y G", "d MMM y G", "dd/MM/y GGGGG" } }, { "roc.DatePatterns", { "EEEE, d MMMM y GGGG", "d MMMM y GGGG", "d MMM y GGGG", "dd/MM/y G" } }, { "java.time.islamic.DatePatterns", { "EEEE, d MMMM y G", "d MMMM y G", "d MMM y G", "dd/MM/y G" } }, { "islamic.DatePatterns", { "EEEE, d MMMM y GGGG", "d MMMM y GGGG", "d MMM y GGGG", "dd/MM/y GGGG" } }, { "NumberPatterns", { "#,##0.###", "¤#,##0.00", "#,##0%" } } };
    }
}
