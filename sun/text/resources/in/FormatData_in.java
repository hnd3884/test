package sun.text.resources.in;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_in extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember", "" } }, { "MonthAbbreviations", { "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des", "" } }, { "DayNames", { "Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu" } }, { "DayAbbreviations", { "Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab" } }, { "Eras", { "BCE", "CE" } }, { "NumberPatterns", { "#,##0.###", "¤#,##0.00", "#,##0%" } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, yyyy MMMM dd", "yyyy MMMM d", "yyyy MMM d", "yy/MM/dd" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
