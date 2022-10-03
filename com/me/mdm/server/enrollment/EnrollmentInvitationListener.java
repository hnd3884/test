package com.me.mdm.server.enrollment;

import org.json.simple.JSONArray;
import com.adventnet.sym.server.mdm.core.EREvent;
import com.adventnet.sym.server.mdm.core.EnrollmentRequestListener;

public class EnrollmentInvitationListener implements EnrollmentRequestListener
{
    @Override
    public void enrollmentRequestPreRemove(final EREvent erEvent) {
    }
    
    @Override
    public String inviteDeviceUser(final EREvent erEvent) {
        return null;
    }
    
    @Override
    public void reinviteDeviceUser(final EREvent erEvent) {
    }
    
    @Override
    public void removeDeviceUser(final EREvent erEvent) {
    }
    
    @Override
    public void inviteBulkEnrolledDeviceUsers(final EREvent event) {
    }
    
    @Override
    public void preSendEnrollmentRequestMail(final EREvent event) {
    }
    
    @Override
    public void preReSendEnrollmentRequestMail(final EREvent event) {
    }
    
    public void handleBulkImport(final JSONArray emails) {
    }
}
