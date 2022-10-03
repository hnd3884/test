package sun.util.resources.zh;

import java.util.ResourceBundle;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.resources.TimeZoneNamesBundle;

public final class TimeZoneNames_zh_HK extends TimeZoneNamesBundle
{
    public TimeZoneNames_zh_HK() {
        this.setParent(((ResourceBundleBasedAdapter)LocaleProviderAdapter.forJRE()).getLocaleData().getTimeZoneNames(Locale.TAIWAN));
    }
    
    @Override
    protected Object[][] getContents() {
        return new Object[0][];
    }
}
