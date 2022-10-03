package sun.util.resources.zh;

import java.util.ResourceBundle;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.resources.OpenListResourceBundle;

public final class LocaleNames_zh_HK extends OpenListResourceBundle
{
    public LocaleNames_zh_HK() {
        this.setParent(((ResourceBundleBasedAdapter)LocaleProviderAdapter.forJRE()).getLocaleData().getLocaleNames(Locale.TAIWAN));
    }
    
    @Override
    protected Object[][] getContents() {
        return new Object[0][];
    }
}
