package com.me.devicemanagement.onpremise.webclient.authentication;

import com.me.devicemanagement.onpremise.server.sdp.Ticket;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class WebclientAuthenticationImpl implements WebclientAuthentication
{
    @Override
    public Ticket validateTicketForSSO(final String ticket, final HttpServletRequest request, final HttpServletResponse response, final boolean isRetry) {
        return null;
    }
}
