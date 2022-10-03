package com.me.mdm.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class MDMProfessionalEditionTag extends ConditionalTagSupport
{
    protected boolean condition() throws JspTagException {
        final Boolean isProfessionalEdition = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
        final Boolean isEnterpriseEdition = LicenseProvider.getInstance().getMDMLicenseAPI().isEnterpriseLicenseEdition();
        return isEnterpriseEdition || isProfessionalEdition;
    }
}
