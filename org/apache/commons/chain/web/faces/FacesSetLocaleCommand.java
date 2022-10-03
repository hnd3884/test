package org.apache.commons.chain.web.faces;

import javax.faces.context.FacesContext;
import java.util.Locale;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.AbstractSetLocaleCommand;

public class FacesSetLocaleCommand extends AbstractSetLocaleCommand
{
    protected void setLocale(final Context context, final Locale locale) {
        final FacesContext fcontext = context.get("context");
        fcontext.getViewRoot().setLocale(locale);
    }
}
