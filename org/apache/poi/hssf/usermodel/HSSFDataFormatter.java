package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.LocaleUtil;
import java.util.Locale;
import org.apache.poi.ss.usermodel.DataFormatter;

public final class HSSFDataFormatter extends DataFormatter
{
    public HSSFDataFormatter(final Locale locale) {
        super(locale);
    }
    
    public HSSFDataFormatter() {
        this(LocaleUtil.getUserLocale());
    }
}
