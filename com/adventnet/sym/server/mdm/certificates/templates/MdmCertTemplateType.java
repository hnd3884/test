package com.adventnet.sym.server.mdm.certificates.templates;

public enum MdmCertTemplateType
{
    DIGICERT(1);
    
    private final int templateType;
    
    private MdmCertTemplateType(final int templateType) {
        this.templateType = templateType;
    }
    
    public int getTemplateType() {
        return this.templateType;
    }
}
