package com.adventnet.sym.server.mdm.certificates.scep.request;

import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;

public class MdmIosScepEnrollmentRequestValidator extends MDMScepEnrollmentRequestValidator
{
    public MdmIosScepEnrollmentRequestValidator(final MdmScepRequest mdmScepRequest) {
        super(mdmScepRequest);
    }
    
    @Override
    public String getCertificateIdentifier() {
        return MDMiOSEntrollmentUtil.getInstance().getIosMdmIdentity(this.mdmScepRequest.getEnrollmentRequestId());
    }
}
