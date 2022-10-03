package sun.util.resources.zh;

import java.util.ResourceBundle;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.resources.OpenListResourceBundle;

public final class CurrencyNames_zh_HK extends OpenListResourceBundle
{
    public CurrencyNames_zh_HK() {
        this.setParent(((ResourceBundleBasedAdapter)LocaleProviderAdapter.forJRE()).getLocaleData().getCurrencyNames(Locale.TAIWAN));
    }
    
    @Override
    protected Object[][] getContents() {
        return new Object[][] { { "HKD", "HK$" }, { "TWD", "TWD" } };
    }
}
