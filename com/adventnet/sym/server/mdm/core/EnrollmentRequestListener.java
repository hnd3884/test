package com.adventnet.sym.server.mdm.core;

import java.util.logging.Logger;

public interface EnrollmentRequestListener
{
    public static final Logger ENROLLMENT_LOGGER = Logger.getLogger("EnrollmentRequestListener");
    
    void enrollmentRequestPreRemove(final EREvent p0);
    
    String inviteDeviceUser(final EREvent p0);
    
    void reinviteDeviceUser(final EREvent p0);
    
    void removeDeviceUser(final EREvent p0);
    
    void inviteBulkEnrolledDeviceUsers(final EREvent p0);
    
    void preSendEnrollmentRequestMail(final EREvent p0);
    
    void preReSendEnrollmentRequestMail(final EREvent p0);
}
