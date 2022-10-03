package org.apache.commons.chain.web.servlet;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.AbstractSetLocaleCommand;

public class ServletSetLocaleCommand extends AbstractSetLocaleCommand
{
    protected void setLocale(final Context context, final Locale locale) {
        final HttpServletResponse response = context.get("response");
        response.setLocale(locale);
    }
}
