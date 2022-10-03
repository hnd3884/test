package org.glassfish.jersey.message.internal;

import org.glassfish.jersey.internal.LocalizationMessages;
import javax.inject.Singleton;
import javax.ws.rs.core.NewCookie;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public class NewCookieProvider implements HeaderDelegateProvider<NewCookie>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == NewCookie.class;
    }
    
    public String toString(final NewCookie cookie) {
        Utils.throwIllegalArgumentExceptionIfNull(cookie, LocalizationMessages.NEW_COOKIE_IS_NULL());
        final StringBuilder b = new StringBuilder();
        b.append(cookie.getName()).append('=');
        StringBuilderUtils.appendQuotedIfWhitespace(b, cookie.getValue());
        b.append(";").append("Version=").append(cookie.getVersion());
        if (cookie.getComment() != null) {
            b.append(";Comment=");
            StringBuilderUtils.appendQuotedIfWhitespace(b, cookie.getComment());
        }
        if (cookie.getDomain() != null) {
            b.append(";Domain=");
            StringBuilderUtils.appendQuotedIfWhitespace(b, cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            b.append(";Path=");
            StringBuilderUtils.appendQuotedIfWhitespace(b, cookie.getPath());
        }
        if (cookie.getMaxAge() != -1) {
            b.append(";Max-Age=");
            b.append(cookie.getMaxAge());
        }
        if (cookie.isSecure()) {
            b.append(";Secure");
        }
        if (cookie.isHttpOnly()) {
            b.append(";HttpOnly");
        }
        if (cookie.getExpiry() != null) {
            b.append(";Expires=");
            b.append(HttpDateFormat.getPreferredDateFormat().format(cookie.getExpiry()));
        }
        return b.toString();
    }
    
    public NewCookie fromString(final String header) {
        Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.NEW_COOKIE_IS_NULL());
        return HttpHeaderReader.readNewCookie(header);
    }
}
