package sun.util.resources.zh;

import java.util.ResourceBundle;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.resources.OpenListResourceBundle;

public final class CurrencyNames_zh_SG extends OpenListResourceBundle
{
    public CurrencyNames_zh_SG() {
        this.setParent(((ResourceBundleBasedAdapter)LocaleProviderAdapter.forJRE()).getLocaleData().getCurrencyNames(Locale.CHINA));
    }
    
    @Override
    protected Object[][] getContents() {
        return new Object[][] { { "CNY", "CNY" }, { "SGD", "S$" } };
    }
}
