package com.adventnet.sym.server.mdm.certificates.scep.request;

public class MdmScepRequestValidatorFactory
{
    public static ScepEnrollmentRequestValidator getValidator(final MdmScepRequest mdmScepRequest) {
        return new MdmIosScepEnrollmentRequestValidator(mdmScepRequest);
    }
}
