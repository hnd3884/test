package com.me.devicemanagement.onpremise.server.websockets;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Base64;
import java.security.SecureRandom;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class WebSocketUtil
{
    private static Logger wsFrameworkLogger;
    
    public static String generateTicket(final String clientType, final String paramName, final String paramValue) {
        String ticket;
        for (ticket = getRandomTicket(); isTicketAlreadyExists(ticket); ticket = getRandomTicket()) {}
        try {
            final DataObject dataObject = SyMUtil.getPersistenceLite().constructDataObject();
            final Row row = new Row("WebSocketTicketDetails");
            row.set("TICKET", (Object)ticket);
            row.set("PARAM_NAME", (Object)paramName);
            row.set("PARAM_VALUE", (Object)paramValue);
            row.set("CLIENT_TYPE", (Object)clientType);
            row.set("CREATED_TIME", (Object)System.currentTimeMillis());
            dataObject.addRow(row);
            SyMUtil.getPersistenceLite().add(dataObject);
        }
        catch (final DataAccessException e) {
            WebSocketUtil.wsFrameworkLogger.log(Level.SEVERE, "Exception Occurred while adding row", (Throwable)e);
        }
        return ticket;
    }
    
    public static String getRandomTicket() {
        final SecureRandom random = new SecureRandom();
        final byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
    
    public static boolean isTicketAlreadyExists(final String ticket) {
        boolean isTicketAlreadyExists = false;
        try {
            final Table table = new Table("WebSocketTicketDetails");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
            final Criteria criteria = new Criteria(Column.getColumn("WebSocketTicketDetails", "TICKET"), (Object)ticket, 0);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("WebSocketTicketDetails", "TICKET_ID"));
            final DataObject dataObject = SyMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                isTicketAlreadyExists = true;
            }
        }
        catch (final DataAccessException e) {
            WebSocketUtil.wsFrameworkLogger.log(Level.SEVERE, "Exception Occurred while getting dataObject", (Throwable)e);
        }
        return isTicketAlreadyExists;
    }
    
    public static boolean isOSDWithinNoAuthPeriod() {
        boolean isOSDWithinNoAuthPeriod = false;
        final long diffDays = getDiffDays();
        if (diffDays > 0L && diffDays <= 15L) {
            isOSDWithinNoAuthPeriod = true;
        }
        return isOSDWithinNoAuthPeriod;
    }
    
    public static long getDiffDays() {
        long diffDays = 0L;
        if (SyMUtil.getSyMParameter("osdWithoutAuthEndDate") != null) {
            final Long endDate = Long.parseLong(SyMUtil.getSyMParameter("osdWithoutAuthEndDate"));
            final Long currentTimeMillis = System.currentTimeMillis();
            final long diff = endDate - currentTimeMillis;
            diffDays = diff / 86400000L;
            WebSocketUtil.wsFrameworkLogger.log(Level.INFO, "Number of days left for trial period {0}", diffDays);
        }
        else {
            WebSocketUtil.wsFrameworkLogger.log(Level.SEVERE, "osdTrialEndDate is null.");
        }
        return diffDays;
    }
    
    static {
        WebSocketUtil.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
