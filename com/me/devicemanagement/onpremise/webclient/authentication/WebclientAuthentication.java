package com.me.devicemanagement.onpremise.webclient.authentication;

import com.me.devicemanagement.onpremise.server.sdp.Ticket;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface WebclientAuthentication
{
    Ticket validateTicketForSSO(final String p0, final HttpServletRequest p1, final HttpServletResponse p2, final boolean p3);
}
