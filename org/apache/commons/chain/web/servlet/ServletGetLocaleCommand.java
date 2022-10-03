package org.apache.commons.chain.web.servlet;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.AbstractGetLocaleCommand;

public class ServletGetLocaleCommand extends AbstractGetLocaleCommand
{
    protected Locale getLocale(final Context context) {
        final HttpServletRequest request = context.get("request");
        return request.getLocale();
    }
}
