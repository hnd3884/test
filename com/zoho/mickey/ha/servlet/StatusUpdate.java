package com.zoho.mickey.ha.servlet;

import com.zoho.framework.utils.crypto.CryptoUtil;
import java.nio.file.Files;
import com.zoho.mickey.ha.HAImpl;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.zoho.mickey.ha.DBUtil;
import com.zoho.clustering.agent.util.ServletUtil;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class StatusUpdate extends HttpServlet
{
    private static final Logger LOGGER;
    private static String uniqueID;
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            if (StatusUpdate.uniqueID == null) {
                StatusUpdate.LOGGER.log(Level.SEVERE, "unique id is not available");
                throw new RuntimeException("unable to find the unique identity of current machine");
            }
            if (request.getHeader("uniqueID") == null || !request.getHeader("uniqueID").equals(StatusUpdate.uniqueID)) {
                throw new IllegalArgumentException("request from unknown source is turned down.");
            }
            final String operationType = ServletUtil.Param.optionalValue(request, "operationType");
            final String ipAddr = ServletUtil.Param.optionalValue(request, "ipAddr");
            final Object value = ServletUtil.Param.optionalValue(request, "value");
            if (operationType.equals("updateHAStatus")) {
                DBUtil.updateStatus(ipAddr, (String)value);
            }
            else if (operationType.equals("updateReplState")) {
                DBUtil.updateReplState(ipAddr, (String)value);
            }
            else {
                if (!operationType.equals("updateServerStatus")) {
                    throw new IllegalArgumentException("Trying to modify unauthorized data");
                }
                final DataObject dobj = DataAccess.get("ServerStatus", new Criteria(Column.getColumn("ServerStatus", "SERVERNAME"), (Object)ipAddr, 0));
                if (dobj.isEmpty()) {
                    final Row serverStatusRow = new Row("ServerStatus");
                    serverStatusRow.set("SERVERNAME", (Object)ipAddr);
                    serverStatusRow.set("STATUS", value);
                    dobj.addRow(serverStatusRow);
                    DataAccess.add(dobj);
                }
                else {
                    final Row serverStatusRow = dobj.getFirstRow("ServerStatus");
                    serverStatusRow.set("STATUS", value);
                    dobj.updateRow(serverStatusRow);
                    DataAccess.update(dobj);
                }
            }
            ServletUtil.Write.text(response, "ok");
        }
        catch (final IllegalArgumentException exp) {
            exp.printStackTrace();
            ServletUtil.Write.text(response, 400, exp.getMessage());
        }
        catch (final DataAccessException | RuntimeException exp2) {
            exp2.printStackTrace();
            ServletUtil.Write.text(response, 500, exp2.getMessage());
        }
    }
    
    static {
        LOGGER = Logger.getLogger(StatusUpdate.class.getName());
        StatusUpdate.uniqueID = null;
        if (HAImpl.UNIQUE_ID_FILE.exists()) {
            try {
                StatusUpdate.uniqueID = CryptoUtil.decrypt(new String(Files.readAllBytes(HAImpl.UNIQUE_ID_FILE.toPath())));
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
}
