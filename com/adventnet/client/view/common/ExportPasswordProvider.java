package com.adventnet.client.view.common;

import com.zoho.mickey.exception.PasswordException;
import com.adventnet.client.view.web.ViewContext;
import com.zoho.mickey.crypto.PasswordProvider;

public class ExportPasswordProvider implements PasswordProvider
{
    public String getPassword(final Object context) throws PasswordException {
        final ViewContext viewCtx = (ViewContext)context;
        final String viewName = viewCtx.getModel().getViewName();
        return new StringBuilder(viewName).reverse().toString();
    }
}
