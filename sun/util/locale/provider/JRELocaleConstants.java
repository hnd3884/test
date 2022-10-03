package sun.util.locale.provider;

import java.util.Locale;

public class JRELocaleConstants
{
    public static final Locale JA_JP_JP;
    public static final Locale NO_NO_NY;
    public static final Locale TH_TH;
    public static final Locale TH_TH_TH;
    
    private JRELocaleConstants() {
    }
    
    static {
        JA_JP_JP = new Locale("ja", "JP", "JP");
        NO_NO_NY = new Locale("no", "NO", "NY");
        TH_TH = new Locale("th", "TH");
        TH_TH_TH = new Locale("th", "TH", "TH");
    }
}
