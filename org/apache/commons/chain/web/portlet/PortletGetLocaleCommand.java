package org.apache.commons.chain.web.portlet;

import javax.portlet.PortletRequest;
import java.util.Locale;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.AbstractGetLocaleCommand;

public class PortletGetLocaleCommand extends AbstractGetLocaleCommand
{
    protected Locale getLocale(final Context context) {
        final PortletRequest request = context.get("request");
        return request.getLocale();
    }
}
