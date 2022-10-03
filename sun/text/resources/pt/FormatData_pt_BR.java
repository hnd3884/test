package sun.text.resources.pt;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_pt_BR extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.00;-¤ #,##0.00", "#,##0%" } }, { "TimePatterns", { "HH'h'mm'min'ss's' z", "H'h'm'min's's' z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d' de 'MMMM' de 'yyyy", "d' de 'MMMM' de 'yyyy", "dd/MM/yyyy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
