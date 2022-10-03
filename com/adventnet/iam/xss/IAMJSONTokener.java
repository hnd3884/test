package com.adventnet.iam.xss;

import org.json.JSONException;
import com.adventnet.iam.security.SecurityUtil;
import org.json.JSONTokener;

public class IAMJSONTokener extends JSONTokener
{
    private XSSUtil xssUtil;
    private boolean htmlEscape;
    
    public IAMJSONTokener(final String s, final boolean htmlEscape) {
        super(s);
        this.htmlEscape = htmlEscape;
    }
    
    public IAMJSONTokener(final String s, final XSSUtil xssUtil) {
        super(s);
        this.xssUtil = xssUtil;
    }
    
    public String nextString(final char quote) throws JSONException {
        String rv = super.nextString(quote);
        if (this.htmlEscape) {
            rv = SecurityUtil.htmlEscape(rv);
        }
        else {
            final XSSUtil xssUtil = this.xssUtil;
            if (XSSUtil.detectXSS(rv)) {
                rv = this.xssUtil.filterXSS(rv);
            }
        }
        return rv;
    }
}
