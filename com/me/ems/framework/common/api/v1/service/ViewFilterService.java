package com.me.ems.framework.common.api.v1.service;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilter;
import com.adventnet.persistence.DataAccessException;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.i18n.I18N;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ViewFilterService
{
    private static Logger logger;
    
    public Map fetchSavedFilterMap(final Long pageID, final Long viewID, final Long loginID) {
        final Map<String, List> filterDetails = new HashMap<String, List>();
        try {
            final DataObject dataObject = DCViewFilterUtil.getInstance().getSavedFiltersDO(viewID, pageID, loginID);
            final List<Map> filterList = new ArrayList<Map>();
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("DCViewFilterDetails");
                while (iterator.hasNext()) {
                    final Row filterRows = iterator.next();
                    final Long filterId = (Long)filterRows.get("FILTER_ID");
                    final String filterName = (String)filterRows.get("FILTER_NAME");
                    final Map<String, Object> filterMap = new HashMap<String, Object>();
                    filterMap.put("filterId", filterId);
                    filterMap.put("filterName", filterName);
                    filterList.add(filterMap);
                }
            }
            filterDetails.put("savedFilters", filterList);
        }
        catch (final Exception ex) {
            filterDetails.put("savedFilters", new ArrayList());
            ViewFilterService.logger.log(Level.SEVERE, "Exception while fetching savedFilterDetails for Page: " + pageID + " viewID: " + viewID + " loginID: " + loginID, ex);
        }
        return filterDetails;
    }
    
    public List fetchCRColumnDetailsForView(final Long viewID) {
        final List crColumnList = new ArrayList();
        try {
            final ArrayList categories = DCViewFilterUtil.getInstance().getColumnCategoriesList(viewID.toString());
            final DataObject dataObject = DCViewFilterUtil.getInstance().getCRColumnDetailsDO(viewID.toString());
            if (dataObject != null && !dataObject.isEmpty()) {
                for (int i = 0; i < categories.size(); ++i) {
                    final Map categoryMap = new HashMap();
                    final List columnsList = new ArrayList();
                    final Object value = categories.get(i);
                    categoryMap.put("category", I18N.getMsg((String)value, new Object[0]));
                    final Criteria categoryCriteria = new Criteria(new Column("CRColumns", "COLUMN_CATEGORY"), value, 0);
                    final Iterator iter = dataObject.getRows("CRColumns", categoryCriteria);
                    while (iter.hasNext()) {
                        final Map columnDetails = new HashMap();
                        final Row tableRows = iter.next();
                        final String colname = (String)tableRows.get("DISPLAY_NAME");
                        final String dataType = (String)tableRows.get("DATA_TYPE");
                        final String colId = String.valueOf(tableRows.get("COLUMN_ID"));
                        final String searchEnabled = String.valueOf(tableRows.get("SEARCH_ENABLED"));
                        final String displayName = I18N.getMsg(colname, new Object[0]);
                        columnDetails.put("displayString", displayName);
                        columnDetails.put("dataType", dataType);
                        columnDetails.put("columnId", colId);
                        columnDetails.put("searchEnabled", searchEnabled);
                        columnsList.add(columnDetails);
                    }
                    categoryMap.put("columns", columnsList);
                    crColumnList.add(categoryMap);
                }
            }
        }
        catch (final Exception ex) {
            ViewFilterService.logger.log(Level.SEVERE, "Exception while fetching CRColumns for the View: " + viewID, ex);
        }
        return crColumnList;
    }
    
    public List getColumnBrowseValues(final Long columnID, final Long viewID, final Map filterMap, final Long loginID) throws APIException {
        try {
            final List valuesList = ReportCriteriaUtil.getInstance().getColumnValues(columnID, viewID, filterMap, loginID);
            return valuesList;
        }
        catch (final Exception ex) {
            ViewFilterService.logger.log(Level.SEVERE, "Exception while fetching search values for CRColumns for the column " + columnID + " in View: " + viewID, ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public Map isFilterNameExistsCheck(final String filterName, final Long pageID, final Long viewID, final Long loginID, final Long filterID) throws APIException {
        final Map map = new HashMap();
        try {
            final Boolean isFilterNameExists = DCViewFilterUtil.getInstance().filterNameValidationCheck(filterName, pageID, viewID, loginID, filterID);
            map.put("isFilterNameExists", isFilterNameExists);
            return map;
        }
        catch (final DataAccessException ex) {
            ViewFilterService.logger.log(Level.SEVERE, "Exception while doing Filter name validation", (Throwable)ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public void renameFilter(final Long filterID, final String filterName, final String loginName, final Long loginID) throws APIException {
        try {
            final Map filterMap = DCViewFilterUtil.getInstance().getFilterDetails(filterID, loginID);
            if (!filterMap.get("isFilterNameExists")) {
                ViewFilterService.logger.log(Level.SEVERE, "No Filter is present with given filterID :" + filterID);
                throw new APIException(Response.Status.BAD_REQUEST, "FILTER0002", "dc.common.viewFilter.not_exists");
            }
            if (!filterMap.get("isFilterMappedToUser")) {
                ViewFilterService.logger.log(Level.SEVERE, " Login user is not authorized to access the filter : " + filterID);
                throw new APIException(Response.Status.UNAUTHORIZED, "USER0005", "ems.rest.authentication.unauthorized");
            }
            final boolean isFilterNameExists = this.isFilterNameExistsCheck(filterName, filterMap.get("pageID"), filterMap.get("viewID"), loginID, filterID).get("isFilterNameExists");
            if (isFilterNameExists) {
                ViewFilterService.logger.log(Level.SEVERE, "Filter name already exists ");
                throw new APIException(Response.Status.BAD_REQUEST, "FILTER0003", "dc.common.viewFilter.filter_name_exists");
            }
            DCViewFilterUtil.getInstance().renameDCViewFilter(filterID, filterName, loginName);
        }
        catch (final DataAccessException ex) {
            ViewFilterService.logger.log(Level.SEVERE, "Exception while renaming filter", (Throwable)ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public void deleteFilter(final Long filterID, final Long loginID, final String loginName) throws APIException {
        try {
            final Map filterMap = DCViewFilterUtil.getInstance().getFilterDetails(filterID, loginID);
            if (!filterMap.get("isFilterNameExists")) {
                ViewFilterService.logger.log(Level.SEVERE, "No Filter is present with given filterID :" + filterID);
                throw new APIException(Response.Status.BAD_REQUEST, "FILTER0002", "dc.common.viewFilter.not_exists");
            }
            if (!filterMap.get("isFilterMappedToUser")) {
                ViewFilterService.logger.log(Level.SEVERE, " Login user is not authorized to access the filter : " + filterID);
                throw new APIException(Response.Status.UNAUTHORIZED, "USER0005", "ems.rest.authentication.unauthorized");
            }
            DCViewFilterUtil.getInstance().deleteDCViewFilterCriteriaDetails(filterID, true, loginName);
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            ViewFilterService.logger.log(Level.SEVERE, "Exception while deleting Filter", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public DCViewFilter getCriteriaJSONForFilter(final Long filterID) throws APIException {
        final DCViewFilter dcViewFilter = DCViewFilterUtil.getInstance().getCriteriaJSONForFilter(filterID);
        if (dcViewFilter == null) {
            throw new APIException(Response.Status.BAD_REQUEST, "FILTER0002", "dc.common.viewFilter.not_exists");
        }
        return dcViewFilter;
    }
    
    public Map saveDCViewFilter(final Boolean isAnonymous, final Map dcViewFilterMap, final Long loginID, final String loginName) throws APIException {
        try {
            String filterName = dcViewFilterMap.get("filterName");
            final Long pageID = Long.valueOf(dcViewFilterMap.get("pageID").toString());
            final Long viewID = Long.valueOf(dcViewFilterMap.get("viewID").toString());
            if (filterName == null || filterName.isEmpty()) {
                if (!isAnonymous) {
                    throw new APIException(Response.Status.BAD_REQUEST, "FILTER0005", "dc.common.viewFilter.filter_name_empty");
                }
                filterName = String.valueOf(System.currentTimeMillis());
            }
            final ObjectMapper mapper = new ObjectMapper();
            final DCViewFilter dcViewFilter = DCViewFilter.dcViewFilterMapper(mapper.writeValueAsString(dcViewFilterMap.get("filter")));
            return this.saveDCViewFilter(dcViewFilter, filterName, null, pageID, viewID, loginID, loginName, isAnonymous);
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            ViewFilterService.logger.log(Level.SEVERE, "Exception while saving Filter", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.authentication.unauthorized");
        }
    }
    
    public Map updateDCViewFilter(final Long filterID, final Map dcViewFilterMap, final Long loginID, final String loginName) throws APIException {
        try {
            final String filterName = dcViewFilterMap.containsKey("filterName") ? dcViewFilterMap.get("filterName") : "";
            final Object filter = dcViewFilterMap.containsKey("filter") ? dcViewFilterMap.get("filter") : null;
            if (filterName.isEmpty() && filter == null) {
                ViewFilterService.logger.log(Level.SEVERE, " Either filterName/filter value  required to update the filter");
                throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0009", "dc.rest.api_param_missing", new String[] { "filterName/filter" });
            }
            if (!filterName.isEmpty() && filter == null) {
                this.renameFilter(filterID, filterName, loginName, loginID);
            }
            else if (filter != null) {
                final ObjectMapper mapper = new ObjectMapper();
                final DCViewFilter dcViewFilter = DCViewFilter.dcViewFilterMapper(mapper.writeValueAsString(filter));
                final Criteria filterCrit = new Criteria(new Column("DCViewFilterCriteria", "FILTER_ID"), (Object)filterID, 0);
                final DataObject filterDO = SyMUtil.getPersistence().get("DCViewFilterDetails", filterCrit);
                final Boolean isAnonymous = (Boolean)filterDO.getFirstValue("DCViewFilterDetails", "IS_ANONYMOUS");
                return DCViewFilterUtil.getInstance().saveDCViewFilter(dcViewFilter, filterID, filterName, null, null, loginID, loginName, isAnonymous);
            }
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            ViewFilterService.logger.log(Level.SEVERE, "Exception while saving Filter", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.authentication.unauthorized");
        }
        return null;
    }
    
    public Map saveDCViewFilter(final DCViewFilter dcViewFilter, final String filterName, final Long filterID, final Long pageID, final Long viewID, final Long loginID, final String loginName, final Boolean isAnonymous) throws APIException {
        try {
            if (filterID != null) {
                if (filterName != null && !filterName.isEmpty()) {
                    this.renameFilter(filterID, filterName, loginName, loginID);
                }
            }
            else {
                final boolean isFilterNameExists = this.isFilterNameExistsCheck(filterName, pageID, viewID, loginID, null).get("isFilterNameExists");
                if (isFilterNameExists) {
                    ViewFilterService.logger.log(Level.SEVERE, "Filter name already exists ");
                    throw new APIException(Response.Status.BAD_REQUEST, "FILTER0003", "dc.common.viewFilter.filter_name_exists");
                }
            }
            return DCViewFilterUtil.getInstance().saveDCViewFilter(dcViewFilter, filterID, filterName, pageID, viewID, loginID, loginName, isAnonymous);
        }
        catch (final DataAccessException ex) {
            ViewFilterService.logger.log(Level.SEVERE, "Exception while saving Filter", (Throwable)ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.authentication.unauthorized");
        }
    }
    
    static {
        ViewFilterService.logger = Logger.getLogger(ViewFilterService.class.getName());
    }
}
