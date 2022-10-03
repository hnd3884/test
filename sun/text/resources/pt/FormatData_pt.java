package sun.text.resources.pt;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_pt extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "Janeiro", "Fevereiro", "Mar\u00e7o", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro", "" } }, { "MonthAbbreviations", { "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "DayNames", { "Domingo", "Segunda-feira", "Ter\u00e7a-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "S\u00e1bado" } }, { "DayAbbreviations", { "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "S\u00e1b" } }, { "DayNarrows", { "D", "S", "T", "Q", "Q", "S", "S" } }, { "long.Eras", { "Antes de Cristo", "Ano do Senhor" } }, { "Eras", { "a.C.", "d.C." } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "HH'H'mm'm' z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE, d' de 'MMMM' de 'yyyy", "d' de 'MMMM' de 'yyyy", "d/MMM/yyyy", "dd-MM-yyyy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
