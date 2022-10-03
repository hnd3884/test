package sun.text.resources.ms;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_ms extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "Januari", "Februari", "Mac", "April", "Mei", "Jun", "Julai", "Ogos", "September", "Oktober", "November", "Disember", "" } }, { "MonthAbbreviations", { "Jan", "Feb", "Mac", "Apr", "Mei", "Jun", "Jul", "Ogos", "Sep", "Okt", "Nov", "Dis", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "O", "S", "O", "N", "D", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "O", "S", "O", "N", "D", "" } }, { "standalone.MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "O", "S", "O", "N", "D", "" } }, { "DayNames", { "Ahad", "Isnin", "Selasa", "Rabu", "Khamis", "Jumaat", "Sabtu" } }, { "DayAbbreviations", { "Ahd", "Isn", "Sel", "Rab", "Kha", "Jum", "Sab" } }, { "DayNarrows", { "A", "I", "S", "R", "K", "J", "S" } }, { "standalone.DayNarrows", { "A", "I", "S", "R", "K", "J", "S" } }, { "Eras", { "BCE", "CE" } }, { "NumberPatterns", { "#,##0.###", "¤ #,##0.00", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, yyyy MMMM dd", "yyyy MMMM d", "yyyy MMM d", "yy/MM/dd" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
