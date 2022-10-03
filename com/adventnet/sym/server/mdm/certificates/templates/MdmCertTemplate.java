package com.adventnet.sym.server.mdm.certificates.templates;

public class MdmCertTemplate
{
    public final MdmCertTemplateType templateType;
    public final String templateName;
    
    public MdmCertTemplate(final MdmCertTemplateType templateType, final String templateName) {
        this.templateType = templateType;
        this.templateName = templateName;
    }
}
