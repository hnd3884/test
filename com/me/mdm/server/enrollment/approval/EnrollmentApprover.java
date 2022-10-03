package com.me.mdm.server.enrollment.approval;

import com.me.devicemanagement.framework.server.exception.SyMException;

public interface EnrollmentApprover
{
    void allowEnrollment(final EnrollmentRequest p0) throws SyMException;
}
