package com.adventnet.sym.server.mdm.core;

import java.util.LinkedList;

public class EnrollmentRequestHandler
{
    private static EnrollmentRequestHandler enrollmentRequestHandler;
    public static final int ER_PREREMOVE = 1;
    public static final int ER_ADDED = 2;
    public static final int ER_RESENT = 3;
    public static final int ER_REMOVED = 4;
    public static final int ER_ADDED_BULK_ENROLLMENT = 5;
    public static final int ER_PRE_SEND_MAIL = 6;
    public static final int ER_PRE_RESEND_MAIL = 7;
    private LinkedList<EnrollmentRequestListener> enrollmentRequestListenerList;
    
    private EnrollmentRequestHandler() {
        this.enrollmentRequestListenerList = null;
        this.enrollmentRequestListenerList = new LinkedList<EnrollmentRequestListener>();
    }
    
    public static synchronized EnrollmentRequestHandler getInstance() {
        if (EnrollmentRequestHandler.enrollmentRequestHandler == null) {
            EnrollmentRequestHandler.enrollmentRequestHandler = new EnrollmentRequestHandler();
        }
        return EnrollmentRequestHandler.enrollmentRequestHandler;
    }
    
    public void addEnrollmentListener(final EnrollmentRequestListener enrollmentRequestListener) {
        this.enrollmentRequestListenerList.add(enrollmentRequestListener);
    }
    
    public String invokeEnrollmentRequestListeners(final EREvent erEvent, final int operation) {
        final int l = this.enrollmentRequestListenerList.size();
        if (operation == 1) {
            for (int s = 0; s < l; ++s) {
                final EnrollmentRequestListener listener = this.enrollmentRequestListenerList.get(s);
                listener.enrollmentRequestPreRemove(erEvent);
            }
        }
        else if (operation == 2) {
            final int s = 0;
            if (s < l) {
                final EnrollmentRequestListener listener = this.enrollmentRequestListenerList.get(s);
                return listener.inviteDeviceUser(erEvent);
            }
        }
        else if (operation == 3) {
            for (int s = 0; s < l; ++s) {
                final EnrollmentRequestListener listener = this.enrollmentRequestListenerList.get(s);
                listener.reinviteDeviceUser(erEvent);
            }
        }
        else if (operation == 4) {
            for (int s = 0; s < l; ++s) {
                final EnrollmentRequestListener listener = this.enrollmentRequestListenerList.get(s);
                listener.removeDeviceUser(erEvent);
            }
        }
        else if (operation == 5) {
            for (int s = 0; s < l; ++s) {
                final EnrollmentRequestListener listener = this.enrollmentRequestListenerList.get(s);
                listener.inviteBulkEnrolledDeviceUsers(erEvent);
            }
        }
        else if (operation == 6) {
            for (int s = 0; s < l; ++s) {
                final EnrollmentRequestListener listener = this.enrollmentRequestListenerList.get(s);
                listener.preSendEnrollmentRequestMail(erEvent);
            }
        }
        else if (operation == 7) {
            for (int s = 0; s < l; ++s) {
                final EnrollmentRequestListener listener = this.enrollmentRequestListenerList.get(s);
                listener.preReSendEnrollmentRequestMail(erEvent);
            }
        }
        return null;
    }
    
    static {
        EnrollmentRequestHandler.enrollmentRequestHandler = null;
    }
}
