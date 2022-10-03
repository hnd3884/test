package com.me.mdm.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class MDMEnterpriseEditionTag extends ConditionalTagSupport
{
    protected boolean condition() throws JspTagException {
        final Boolean isEnterpriseEdition = LicenseProvider.getInstance().getMDMLicenseAPI().isEnterpriseLicenseEdition();
        return isEnterpriseEdition;
    }
}
