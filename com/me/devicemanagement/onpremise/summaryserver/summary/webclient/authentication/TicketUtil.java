package com.me.devicemanagement.onpremise.summaryserver.summary.webclient.authentication;

import org.glassfish.jersey.internal.util.Base64;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.apache.commons.lang.RandomStringUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

public class TicketUtil
{
    private static HashMap ticketMap;
    private static Logger out;
    
    public static String getUserDomainForTicket(final String ticket) {
        final String encryptedChar = TicketUtil.ticketMap.get(ticket);
        TicketUtil.ticketMap.remove(ticket);
        return encryptedChar;
    }
    
    public static boolean isValidTicket(final String ticket) {
        return TicketUtil.ticketMap.containsKey(ticket);
    }
    
    public static String createTicket() {
        final String ticket = generateTicket();
        try {
            TicketUtil.ticketMap.put(ticket, getEncryptUserDomain());
        }
        catch (final Exception e) {
            TicketUtil.out.log(Level.SEVERE, "Exception while creating ticket", e);
            e.printStackTrace();
        }
        return ticket;
    }
    
    private static String generateTicket() {
        return RandomStringUtils.randomAlphanumeric(16);
    }
    
    public static String getEncryptUserDomain() throws Exception {
        final String domainName = ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName();
        final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        return Base64.encodeAsString(userName + "::" + domainName);
    }
    
    static {
        TicketUtil.ticketMap = new HashMap();
        TicketUtil.out = Logger.getLogger("TicketUtil");
    }
}
