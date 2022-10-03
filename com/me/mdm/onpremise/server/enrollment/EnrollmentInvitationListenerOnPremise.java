package com.me.mdm.onpremise.server.enrollment;

import com.adventnet.sym.server.mdm.core.EREvent;
import com.me.mdm.server.enrollment.EnrollmentInvitationListener;

public class EnrollmentInvitationListenerOnPremise extends EnrollmentInvitationListener
{
    public void enrollmentRequestPreRemove(final EREvent erEvent) {
        InvitationQRCodeEnrollmentHander.removeQRCode(Long.valueOf(erEvent.enrollmentRequestId));
    }
    
    public void preSendEnrollmentRequestMail(final EREvent erEvent) {
        InvitationQRCodeEnrollmentHander.generateQRCode(erEvent);
    }
    
    public void preReSendEnrollmentRequestMail(final EREvent erEvent) {
        InvitationQRCodeEnrollmentHander.reGenerateQRCode(erEvent);
    }
}
