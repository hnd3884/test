package org.apache.commons.chain.web.faces;

import javax.faces.context.FacesContext;
import java.util.Locale;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.AbstractGetLocaleCommand;

public class FacesGetLocaleCommand extends AbstractGetLocaleCommand
{
    protected Locale getLocale(final Context context) {
        final FacesContext fcontext = context.get("context");
        return fcontext.getViewRoot().getLocale();
    }
}
