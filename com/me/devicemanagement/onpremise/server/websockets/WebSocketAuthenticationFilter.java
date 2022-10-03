package com.me.devicemanagement.onpremise.server.websockets;

import java.io.IOException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.Calendar;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.websockets.SocketAdapterConfManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class WebSocketAuthenticationFilter implements Filter
{
    private static Logger wsFrameworkLogger;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)servletRequest;
        final HttpServletResponse response = (HttpServletResponse)servletResponse;
        boolean isValid = false;
        final String requestURI = request.getContextPath() + request.getServletPath();
        String clientType = null;
        if (requestURI.startsWith("/dcWebSocket/")) {
            clientType = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        }
        final String ticket = request.getParameter("ticket");
        final String paramName = SocketAdapterConfManager.getInstance().getParamName(clientType);
        final boolean authType = SocketAdapterConfManager.getInstance().getAuthType(clientType);
        final String paramValue = request.getParameter(paramName);
        WebSocketAuthenticationFilter.wsFrameworkLogger.log(Level.INFO, " clientType : {0} , paramName : {1} , paramValue : {2} of websocket request ", new Object[] { clientType, paramName, paramValue });
        if (authType) {
            if (clientType == null || ticket == null || paramName == null || paramValue == null) {
                WebSocketAuthenticationFilter.wsFrameworkLogger.log(Level.SEVERE, "request Params are null");
                response.sendError(400);
                return;
            }
            boolean isOSDWithinNoAuthPeriod = false;
            if (clientType.startsWith("OSD")) {
                isOSDWithinNoAuthPeriod = WebSocketUtil.isOSDWithinNoAuthPeriod();
            }
            if (!isOSDWithinNoAuthPeriod) {
                final Table table = new Table("WebSocketTicketDetails");
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
                selectQuery.addSelectColumn(new Column("WebSocketTicketDetails", "TICKET_ID"));
                selectQuery.addSelectColumn(new Column("WebSocketTicketDetails", "TICKET"));
                selectQuery.addSelectColumn(new Column("WebSocketTicketDetails", "CREATED_TIME"));
                final Criteria ticketCriteria = new Criteria(Column.getColumn("WebSocketTicketDetails", "TICKET"), (Object)ticket, 0);
                final Criteria clientTypeCriteria = new Criteria(Column.getColumn("WebSocketTicketDetails", "CLIENT_TYPE"), (Object)clientType, 0);
                final Criteria paramNameCriteria = new Criteria(Column.getColumn("WebSocketTicketDetails", "PARAM_NAME"), (Object)paramName, 0);
                final Criteria paramValueCriteria = new Criteria(Column.getColumn("WebSocketTicketDetails", "PARAM_VALUE"), (Object)paramValue, 0);
                final Criteria criteria = ticketCriteria.and(clientTypeCriteria).and(paramNameCriteria).and(paramValueCriteria);
                selectQuery.setCriteria(criteria);
                DataObject dataObject = null;
                Row row = null;
                String ticketDB = null;
                try {
                    dataObject = SyMUtil.getPersistenceLite().get(selectQuery);
                    if (dataObject != null && !dataObject.isEmpty()) {
                        row = dataObject.getRow("WebSocketTicketDetails");
                        final Long createdTime = (Long)row.get("CREATED_TIME");
                        final Long currentTime = System.currentTimeMillis();
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(createdTime);
                        cal.add(12, SocketAdapterConfManager.getInstance().getTicketExpiryTime());
                        final Long validTicketTime = cal.getTimeInMillis();
                        if (currentTime < createdTime || currentTime > validTicketTime) {
                            WebSocketAuthenticationFilter.wsFrameworkLogger.log(Level.SEVERE, "Ticket is expired");
                            response.sendError(401, "Ticket is expired");
                            return;
                        }
                        ticketDB = (String)row.get("TICKET");
                        SyMUtil.getPersistenceLite().delete(row);
                    }
                }
                catch (final DataAccessException e) {
                    WebSocketAuthenticationFilter.wsFrameworkLogger.log(Level.SEVERE, "Exception occurred while getting dataObject", (Throwable)e);
                }
                if (ticket.equals(ticketDB)) {
                    isValid = true;
                }
                if (!isValid) {
                    WebSocketAuthenticationFilter.wsFrameworkLogger.log(Level.SEVERE, "401,ticket doesn't match");
                    response.sendError(401, "ticket doesn't match");
                    return;
                }
            }
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
    
    public void destroy() {
    }
    
    static {
        WebSocketAuthenticationFilter.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
