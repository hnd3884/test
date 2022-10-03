package sun.text.resources.zh;

import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import java.util.ListResourceBundle;

public class CollationData_zh_HK extends ListResourceBundle
{
    public CollationData_zh_HK() {
        this.setParent(((ResourceBundleBasedAdapter)LocaleProviderAdapter.forJRE()).getLocaleData().getCollationData(Locale.TAIWAN));
    }
    
    @Override
    protected final Object[][] getContents() {
        return new Object[0][];
    }
}
