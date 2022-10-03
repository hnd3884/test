package org.apache.http.impl.cookie;

import java.util.Date;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicExpiresHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler
{
    private final String[] datePatterns;
    
    public BasicExpiresHandler(final String[] datePatterns) {
        Args.notNull((Object)datePatterns, "Array of date patterns");
        this.datePatterns = datePatterns.clone();
    }
    
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull((Object)cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for 'expires' attribute");
        }
        final Date expiry = DateUtils.parseDate(value, this.datePatterns);
        if (expiry == null) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + value);
        }
        cookie.setExpiryDate(expiry);
    }
    
    @Override
    public String getAttributeName() {
        return "expires";
    }
}
