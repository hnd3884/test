package com.me.mdm.webclient.eas;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.easmanagement.EASMgmt;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.easmanagement.EASMgmtDataHandler;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class EASDeviceDetailsRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public EASDeviceDetailsRetrieverAction() {
        this.logger = Logger.getLogger("EASMgmtLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String accessState = request.getParameter("accessState");
            final String managedStatus = request.getParameter("managedStatus");
            final String viewAllMailBox = request.getParameter("viewAllMailBox");
            final String countFilter = request.getParameter("countFilter");
            final HashMap easDevicefilterdetails = new HashMap();
            final JSONObject exchangeServerDetails = EASMgmtDataHandler.getInstance().getExchangeServerDetails(false);
            final Long serverID = (Long)exchangeServerDetails.get((Object)"EAS_SERVER_ID");
            final Long lastSuccessfulSyncTask = (Long)exchangeServerDetails.get((Object)"LAST_SUCCESSFUL_SYNC_TASK");
            Criteria queryCriteria = new Criteria(Column.getColumn("EASMailboxDetails", "EAS_SERVER_ID"), (Object)serverID, 0).and(new Criteria(Column.getColumn("EASMailboxDeviceRel", "LAST_UPDATED_TIME"), (Object)lastSuccessfulSyncTask, 4));
            final Integer appliedFor = (Integer)exchangeServerDetails.get((Object)"APPLIED_FOR");
            if (viewAllMailBox == null || !viewAllMailBox.equalsIgnoreCase("true")) {
                if (appliedFor != null) {
                    final Column easSelectedMailbox = new Column("EASSelectedMailbox", "EAS_MAILBOX_ID");
                    easSelectedMailbox.setColumnAlias("easSelectedMailbox");
                    selectQuery.addSelectColumn(easSelectedMailbox);
                    queryCriteria = queryCriteria.and(new Criteria(easSelectedMailbox, (Object)null, (int)(appliedFor.equals(1) ? 0 : 1)));
                    selectQuery.addJoin(new Join("EASMailboxDetails", "EASSelectedMailbox", new String[] { "EAS_MAILBOX_ID" }, new String[] { "EAS_MAILBOX_ID" }, appliedFor.equals(1) ? 1 : 2));
                }
            }
            else {
                easDevicefilterdetails.put("viewAllMailBox", viewAllMailBox);
            }
            final Criteria blockDeviceCri = new Criteria(Column.getColumn("EASMailboxDeviceInfo", "DEVICE_ACCESS_STATE"), (Object)0, 1);
            final Criteria allowedDeviceCri = new Criteria(Column.getColumn("EASMailboxDeviceInfo", "DEVICE_ACCESS_STATE"), (Object)0, 0);
            final Criteria managedCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
            final Criteria enrollCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria unEmailCri = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"), 1, false);
            final Criteria emailCri = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)Column.getColumn("EASMailboxDetails", "EMAIL_ADDRESS"), 0, false);
            final Criteria unmanagedCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
            final Criteria unenrollCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 1);
            if (accessState != null && !accessState.equalsIgnoreCase("none")) {
                final Criteria accessStateCri = new Criteria(Column.getColumn("EASMailboxDeviceInfo", "DEVICE_ACCESS_STATE"), (Object)accessState, 0);
                queryCriteria = queryCriteria.and(accessStateCri);
                easDevicefilterdetails.put("accessState", accessState);
            }
            if (managedStatus != null && !managedStatus.equalsIgnoreCase("none")) {
                if (Long.parseLong(managedStatus) == 0L) {
                    queryCriteria = queryCriteria.and(managedCri);
                }
                else if (Long.parseLong(managedStatus) == 1L) {
                    queryCriteria = queryCriteria.and(unmanagedCri);
                }
                easDevicefilterdetails.put("managedStatus", managedStatus);
            }
            if (countFilter != null) {
                final String s = countFilter;
                switch (s) {
                    case "restrictedDevicesCount": {
                        queryCriteria = queryCriteria.and(blockDeviceCri);
                        break;
                    }
                    case "allowedDevicesCount": {
                        queryCriteria = queryCriteria.and(allowedDeviceCri).and(managedCri.and(enrollCri)).and(emailCri);
                        break;
                    }
                    case "gracePeriodCount": {
                        queryCriteria = queryCriteria.and(allowedDeviceCri).and(unmanagedCri.or(unenrollCri).or(unEmailCri));
                        break;
                    }
                    case "managedDevicesCount": {
                        queryCriteria = queryCriteria.and(managedCri.and(enrollCri)).and(emailCri);
                        break;
                    }
                    case "unManagedDevicesCount": {
                        queryCriteria = queryCriteria.and(unmanagedCri.or(unenrollCri).or(unEmailCri));
                        break;
                    }
                }
            }
            final JSONObject CEADetailsRequest = new JSONObject();
            CEADetailsRequest.put((Object)"EASServerDetails", (Object)String.valueOf(Boolean.TRUE));
            CEADetailsRequest.put((Object)"EASSelectedMailbox", (Object)String.valueOf(Boolean.TRUE));
            final JSONObject CEAdetails = EASMgmt.getInstance().getCEAdetails(CEADetailsRequest);
            selectQuery.setCriteria(queryCriteria);
            super.setCriteria(selectQuery, viewCtx);
            request.setAttribute("easDevicefilterdetails", (Object)easDevicefilterdetails);
            request.setAttribute("CEAdetails", (Object)CEAdetails);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in EASDeviceDetailsRetrieverAction...", e);
        }
    }
}
