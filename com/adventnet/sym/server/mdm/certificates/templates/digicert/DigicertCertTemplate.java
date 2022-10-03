package com.adventnet.sym.server.mdm.certificates.templates.digicert;

import com.adventnet.sym.server.mdm.certificates.templates.MdmCertTemplateType;
import com.adventnet.sym.server.mdm.certificates.templates.MdmCertTemplate;

public class DigicertCertTemplate extends MdmCertTemplate
{
    public final String certificateOID;
    
    public DigicertCertTemplate(final String templateName, final String certificateOID) {
        super(MdmCertTemplateType.DIGICERT, templateName);
        this.certificateOID = certificateOID;
    }
}
