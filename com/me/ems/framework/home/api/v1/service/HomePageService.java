package com.me.ems.framework.home.api.v1.service;

import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.me.ems.framework.home.core.CardPositionBean;
import com.me.ems.framework.home.core.DashCardBean;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.home.core.HomePageUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;

public class HomePageService
{
    private static Logger logger;
    
    public Map<String, Object> getHomePageDetails(final User user, final Map<String, Boolean> requestData) {
        final Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("requestData", requestData);
        try {
            ApiFactoryProvider.getHomePageHandler().setHomePageMessagesAndDetails(user, responseMap);
        }
        catch (final Exception ex) {
            HomePageService.logger.log(Level.SEVERE, "Exception occurred while fetching home page messages and setting details.. ", ex);
        }
        responseMap.remove("requestData");
        return responseMap;
    }
    
    public Map<String, Object> getHomePageHelpLinks(final User user) throws APIException {
        return HomePageUtil.getInstance().getHomePageQuickLinks(user.getUserLocale().toString());
    }
    
    public Map<String, Object> getHomePageDashboardCards(final Long loginID, final Long customerID) throws APIException {
        final Map<String, Object> dashBeansMap = new HashMap<String, Object>(1);
        final List<DashCardBean> dashCardBeans = HomePageUtil.getInstance().getHomePageDashboardCards(loginID, customerID);
        dashBeansMap.put("cards", dashCardBeans);
        return dashBeansMap;
    }
    
    public Map<String, Boolean> updateCardPosition(final List<CardPositionBean> idVsCardPositionList, final Long loginID, final Long customerID) throws APIException {
        final Map<String, Boolean> responseMap = new HashMap<String, Boolean>(1);
        try {
            final Map<String, Integer> cardToPositionMap = idVsCardPositionList.stream().collect(Collectors.toMap((Function<? super Object, ? extends String>)CardPositionBean::getViewID, (Function<? super Object, ? extends Integer>)CardPositionBean::getPosition));
            final Set<Long> defaultSummaryIDs = HomePageUtil.getInstance().getDefaultSummaryIDs(loginID);
            final Map<Long, Integer> defaultCardToPositionMap = new HashMap<Long, Integer>();
            if (cardToPositionMap.isEmpty()) {
                Integer position = 1;
                for (final Long summaryID : defaultSummaryIDs) {
                    final Map<Long, Integer> map = defaultCardToPositionMap;
                    final Long n = summaryID;
                    final Integer n2 = position;
                    ++position;
                    map.put(n, n2);
                }
            }
            else {
                for (final String key : cardToPositionMap.keySet()) {
                    final Long summaryID = Long.valueOf(key);
                    if (defaultSummaryIDs.contains(summaryID)) {
                        defaultCardToPositionMap.put(summaryID, cardToPositionMap.get(key));
                    }
                }
            }
            HomePageUtil.getInstance().updateDashCardPosition(defaultCardToPositionMap, loginID, customerID);
        }
        catch (final Exception ex) {
            HomePageService.logger.log(Level.SEVERE, "Exception occurred while updating dashcard position... ", ex);
            throw new APIException("GENERIC0002", ex.getLocalizedMessage(), new String[0]);
        }
        responseMap.put("isPositionUpdated", Boolean.TRUE);
        return responseMap;
    }
    
    public Response closeNotificationForLicense(final Long loginID) {
        try {
            Criteria notifyCri = new Criteria(Column.getColumn("NotifyChangesToUser", "LOGIN_ID"), (Object)loginID, 0);
            notifyCri = notifyCri.and(new Criteria(Column.getColumn("NotifyChangesToUser", "FUNCTIONALITY"), (Object)"--", 0));
            final DataObject dObj = SyMUtil.getPersistence().get("NotifyChangesToUser", notifyCri);
            if (!dObj.isEmpty()) {
                dObj.deleteRows("NotifyChangesToUser", notifyCri);
                SyMUtil.getPersistence().update(dObj);
            }
            return Response.ok().build();
        }
        catch (final Exception e) {
            HomePageService.logger.log(Level.WARNING, "Exception in  closeNotifyMessage  : ", e);
            return Response.serverError().build();
        }
    }
    
    static {
        HomePageService.logger = Logger.getLogger(HomePageService.class.getName());
    }
}
