package com.me.ems.framework.common.api.v1.service;

import java.util.Hashtable;
import org.json.JSONObject;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import com.me.ems.framework.common.api.v1.model.DashCardListBean;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.ems.framework.common.api.utils.DashboardUtil;
import com.me.ems.framework.common.api.constants.DashboardConstants;
import java.util.Properties;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.me.ems.framework.common.api.v1.model.helpermodel.DashCardBean;
import java.util.List;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DashCardListService
{
    private static Logger logger;
    
    public static DashCardListService getInstance() {
        return new DashCardListService();
    }
    
    public List<DashCardBean> formDashCardBeanFromDO(final String dashboardName, final DataObject dataObject, final Boolean renderCardData, final Long customerID, final Long loginId) throws Exception {
        return this.formDashCardBeanFromDO(dashboardName, dataObject, null, renderCardData, customerID, loginId);
    }
    
    public List<DashCardBean> formDashCardBeanFromDO(final String dashboardName, final DataObject dataObject, final Long resourceId, final Boolean renderCardData, final Long custId, final Long loginID) throws Exception {
        final List<DashCardBean> resultBean = new ArrayList<DashCardBean>();
        if (!dataObject.isEmpty()) {
            Boolean isCustomOrder = false;
            final Iterator rows = dataObject.getRows("DashboardCardInfoRel");
            if (dataObject.getRows("CardDisplayOrder").hasNext()) {
                isCustomOrder = true;
            }
            while (rows.hasNext()) {
                DashCardBean dashCardBean = new DashCardBean();
                final Row dashCardRelRow = rows.next();
                final Long cardId = (Long)dashCardRelRow.get("CARD_ID");
                final Long dashboardId = (Long)dashCardRelRow.get("DASHBOARD_ID");
                final Integer showFilter = (Integer)dashCardRelRow.get("SHOW_FILTER");
                final Properties additionalProps = new Properties();
                if (showFilter.equals(DashboardConstants.SHOW_FILTER)) {
                    final DataObject cardUserFilterDO = DashboardUtil.getInstance().getCardFilterUserDO(custId, loginID, dashboardId, cardId);
                    if (!cardUserFilterDO.isEmpty()) {
                        ((Hashtable<String, Boolean>)additionalProps).put("isCardFilterApplied", true);
                        final Row cardUserFilterRow = cardUserFilterDO.getRow("CardFilterUser");
                        final Long filterId = (Long)cardUserFilterRow.get("FILTER_ID");
                        final Long selectedId = (Long)cardUserFilterRow.get("VALUE_ID");
                        ((Hashtable<String, Long>)additionalProps).put("filterId", filterId);
                        ((Hashtable<String, Long>)additionalProps).put("filterValueId", selectedId);
                    }
                }
                final Criteria cardCrit = new Criteria(Column.getColumn("CardInfo", "CARD_ID"), (Object)cardId, 0);
                final Row cardRow = dataObject.getRow("CardInfo", cardCrit);
                final Row cardAttr = dataObject.getRow("CardDisplayAttributesInfo", new Criteria(Column.getColumn("CardDisplayAttributesInfo", "CARD_ID"), (Object)cardId, 0));
                ((Hashtable<String, Boolean>)additionalProps).put("fullDashboard", true);
                ((Hashtable<String, Boolean>)additionalProps).put("isCustomOrder", isCustomOrder);
                ((Hashtable<String, Integer>)additionalProps).put("showFilter", showFilter);
                ((Hashtable<String, String>)additionalProps).put("dashboardName", dashboardName);
                if (resourceId != null) {
                    ((Hashtable<String, Boolean>)additionalProps).put("isResourceDashboard", true);
                    ((Hashtable<String, Long>)additionalProps).put("resourceId", resourceId);
                }
                else {
                    ((Hashtable<String, Boolean>)additionalProps).put("isResourceDashboard", false);
                }
                if (isCustomOrder) {
                    final Long dashCradRel = DashboardUtil.getInstance().getDashboardCardRelId(dashboardId, cardId);
                    final Criteria custOrderCrit = new Criteria(Column.getColumn("CardDisplayOrder", "CARD_DASHBOARD_RELATION_ID"), (Object)dashCradRel, 0);
                    final Row customRow = dataObject.getRow("CardDisplayOrder", custOrderCrit);
                    ((Hashtable<String, Row>)additionalProps).put("customRow", customRow);
                }
                ((Hashtable<String, Boolean>)additionalProps).put("renderCardData", renderCardData);
                dashCardBean = (DashCardBean)DashCardService.getInstance().formCardBean(cardRow, cardAttr, dashCardRelRow, additionalProps);
                resultBean.add(dashCardBean);
            }
        }
        return resultBean;
    }
    
    public JSONArray formDashCardJSONFromBean(final DashCardListBean dashCardListBean) throws Exception {
        final JSONArray resultArr = new JSONArray();
        final List<DashCardBean> cardBeans = dashCardListBean.getCardBeans();
        for (final DashCardBean cardBean : cardBeans) {
            final Long cardId = cardBean.getCardId();
            final Integer xPos = cardBean.getxPos();
            final Integer yPos = cardBean.getyPos();
            final Integer height = cardBean.getHeight();
            final Integer width = cardBean.getWidth();
            final Object metaData = cardBean.getMeta();
            final ObjectMapper oMapper = new ObjectMapper();
            final Map<String, Object> map = (Map<String, Object>)oMapper.convertValue(metaData, (Class)Map.class);
            Boolean showFilter = null;
            if (map != null && map.containsKey("showFilter")) {
                showFilter = map.get("showFilter");
            }
            final JSONObject jsonObject = new JSONObject();
            if (showFilter != null && showFilter.equals(Boolean.TRUE)) {
                final Map<String, Object> filterCardBean = map.get("filterData");
                final Long filterId = Long.parseLong(filterCardBean.get("filterID").toString());
                final Long valueId = Long.parseLong(filterCardBean.get("selectedValueId").toString());
                jsonObject.put("filterId", (Object)filterId);
                jsonObject.put("valueId", (Object)valueId);
                jsonObject.put("showFilter", (Object)Boolean.TRUE);
            }
            else {
                jsonObject.put("showFilter", (Object)Boolean.FALSE);
            }
            jsonObject.put("cardId", (Object)cardId);
            jsonObject.put("height", (Object)height);
            jsonObject.put("width", (Object)width);
            jsonObject.put("xPos", (Object)xPos);
            jsonObject.put("yPos", (Object)yPos);
            resultArr.put((Object)jsonObject);
        }
        return resultArr;
    }
    
    static {
        DashCardListService.logger = Logger.getLogger(DashCardListService.class.getName());
    }
}
