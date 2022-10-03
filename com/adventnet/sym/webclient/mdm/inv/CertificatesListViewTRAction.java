package com.adventnet.sym.webclient.mdm.inv;

import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.Date;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class CertificatesListViewTRAction extends MDMEmberTableRetrieverAction
{
    public Logger logger;
    
    public CertificatesListViewTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String sResource_ID = request.getParameter("RESOURCE_ID");
            final String expireDate = request.getParameter("expireDate");
            this.logger.log(Level.INFO, "Expire Date : {0}", expireDate);
            if (sResource_ID != null && !sResource_ID.trim().isEmpty()) {
                final Criteria criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)new Long(sResource_ID.trim()), 0, false);
                query.setCriteria(criteria);
            }
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (!RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, false)) {
                query.addJoin(RBDAUtil.getInstance().getUserDeviceMappingJoin("Resource", "RESOURCE_ID"));
                final Criteria userDeviceCriteria = RBDAUtil.getInstance().getUserDeviceMappingCriteria(loginId);
                query.setCriteria(query.getCriteria().and(userDeviceCriteria));
            }
            if (expireDate != null && !expireDate.trim().isEmpty()) {
                viewCtx.getRequest().setAttribute("expire", (Object)expireDate);
                Criteria expireCriteria = null;
                final Date currentDate = new Date();
                final long currentDateInLong = currentDate.getTime();
                if (expireDate.equals("1")) {
                    expireCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_EXPIRE"), (Object)currentDateInLong, 7, false);
                }
                else if (expireDate.equals("2")) {
                    final int currentDay = currentDate.getDay();
                    final long expireWeek = currentDateInLong + (6 - currentDay);
                    expireCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_ID"), (Object)new Long[] { currentDateInLong, expireWeek }, 14, false);
                }
                else if (expireDate.equals("3")) {
                    Calendar calendar = this.getCurrentDate(currentDate);
                    calendar.set(5, calendar.getActualMinimum(5));
                    calendar = this.setTimeToBeginningOfDay(calendar);
                    final Date beginning = calendar.getTime();
                    calendar = this.getCurrentDate(currentDate);
                    calendar.set(5, calendar.getActualMaximum(5));
                    this.setTimeToEndofDay(calendar);
                    final Date end = calendar.getTime();
                    expireCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_ID"), (Object)new Long[] { beginning.getTime(), end.getTime() }, 14, false);
                }
                query.setCriteria(query.getCriteria().and(expireCriteria));
            }
            final String sQuery = RelationalAPI.getInstance().getSelectSQL((Query)query);
            this.logger.log(Level.FINE, "CertificatesListViewTRAction Query -- {0}", sQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in CertificatesListViewTRAction ", ex);
        }
        super.setCriteria(query, viewCtx);
    }
    
    private Calendar setTimeToBeginningOfDay(final Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }
    
    private Calendar setTimeToEndofDay(final Calendar calendar) {
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        return calendar;
    }
    
    private Calendar getCurrentDate(final Date currentDate) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        return calendar;
    }
}
