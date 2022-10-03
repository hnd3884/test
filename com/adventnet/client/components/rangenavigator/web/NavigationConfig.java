package com.adventnet.client.components.rangenavigator.web;

import java.util.HashMap;
import com.adventnet.client.view.web.WebViewAPI;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.persistence.Row;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.web.ViewContext;
import java.util.List;

public class NavigationConfig
{
    private int endLinkIndex;
    private int startLinkIndex;
    private final int firstPageIndex = 1;
    private int previousPageIndex;
    private int lastPageIndex;
    private int nextPageIndex;
    private int pageLength;
    private int pageNumber;
    private long fromIndex;
    private long toIndex;
    private long totalRecords;
    private int totalPages;
    private List<Integer> rangeList;
    private String navigType;
    private String orientation;
    private String nocount;
    private String navigTemplateName;
    private static int actualPageLength;
    private final String id;
    
    public NavigationConfig(final String id, final long totalRec, final int pageNum, final int pageLen, final List<Integer> rangeLt, final int startIndex, final int endIndex) {
        this.endLinkIndex = 0;
        this.startLinkIndex = 0;
        this.previousPageIndex = 0;
        this.lastPageIndex = 0;
        this.nextPageIndex = 0;
        this.pageLength = 0;
        this.pageNumber = 0;
        this.fromIndex = 0L;
        this.toIndex = 0L;
        this.totalRecords = 0L;
        this.totalPages = 0;
        this.rangeList = null;
        this.navigType = null;
        this.orientation = null;
        this.nocount = null;
        this.navigTemplateName = null;
        this.id = id;
        this.totalRecords = totalRec;
        this.pageNumber = pageNum;
        this.pageLength = Math.max(endIndex - startIndex + 1, pageLen);
        this.rangeList = rangeLt;
        this.fromIndex = startIndex;
        this.toIndex = endIndex;
        this.previousPageIndex = (int)(this.fromIndex - this.pageLength);
        if (this.totalRecords != -1L) {
            if (this.totalRecords == 0L) {
                this.pageNumber = 0;
            }
            this.totalPages = (int)this.totalRecords / this.pageLength;
            if (this.totalRecords % this.pageLength != 0L) {
                ++this.totalPages;
            }
            this.nextPageIndex = (int)(this.fromIndex + this.pageLength);
            this.lastPageIndex = (this.totalPages - 1) * this.pageLength + 1;
            this.setPageStartAndEndLink();
        }
    }
    
    private void setPageStartAndEndLink() {
        final int currentPage = this.pageNumber;
        final int leftOrRightVisiblePages = 2;
        final int totalVisiblePages = 5;
        int pagesFrom = Math.max(1, currentPage - leftOrRightVisiblePages);
        if (this.totalPages >= totalVisiblePages && this.totalPages + 1 - pagesFrom < totalVisiblePages) {
            final int noOfCountToIncrease = totalVisiblePages - (this.totalPages + 1 - pagesFrom);
            pagesFrom = Math.max(1, currentPage - (leftOrRightVisiblePages + noOfCountToIncrease));
        }
        final int pagesTo = Math.min(this.totalPages, pagesFrom + (totalVisiblePages - 1));
        this.startLinkIndex = pagesFrom;
        this.endLinkIndex = pagesTo;
    }
    
    public String getNavigationType() {
        return this.navigType;
    }
    
    public String getOrientation() {
        return this.orientation;
    }
    
    public int getStartLinkIndex() {
        return this.startLinkIndex;
    }
    
    public int getEndLinkIndex() {
        return this.endLinkIndex;
    }
    
    public int getFirstPageIndex() {
        return 1;
    }
    
    public int getPreviousPageIndex() {
        return this.previousPageIndex;
    }
    
    public int getNextPageIndex() {
        return this.nextPageIndex;
    }
    
    public int getLastPageIndex() {
        return this.lastPageIndex;
    }
    
    public int getPageLength() {
        return this.pageLength;
    }
    
    public int getPageNumber() {
        return this.pageNumber;
    }
    
    public long getFromIndex() {
        return this.fromIndex;
    }
    
    public long getToIndex() {
        return this.toIndex;
    }
    
    public long getTotalRecords() {
        return this.totalRecords;
    }
    
    public int getTotalPages() {
        return this.totalPages;
    }
    
    public List<Integer> getRangeList() {
        return this.rangeList;
    }
    
    public String getNavigTemplateName() {
        return this.navigTemplateName;
    }
    
    public boolean isNoCount() {
        return "true".equals(this.nocount);
    }
    
    public String getId() {
        return this.id;
    }
    
    public int getActualPageLength() {
        return NavigationConfig.actualPageLength;
    }
    
    public static String getNocount(final ViewContext viewCtx) {
        try {
            final DataObject tableViewNavigDO = viewCtx.getModel().getViewConfiguration();
            final Long navigType = (Long)tableViewNavigDO.getFirstValue("ACTableViewConfig", 3);
            if (navigType != null) {
                final DataObject tableViewDO = getDBConfiguration(navigType);
                return (String)tableViewDO.getFirstValue("ACNavigationConfiguration", 4);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return "false";
    }
    
    public static String getNavigType(final ViewContext viewCtx) {
        try {
            final DataObject tableViewNavigDO = viewCtx.getModel().getViewConfiguration();
            final Long navigType = (Long)tableViewNavigDO.getFirstValue("ACTableViewConfig", 3);
            if (navigType != null) {
                final DataObject tableViewDO = getDBConfiguration(navigType);
                final String NavigationTypeNocount = (String)tableViewDO.getFirstValue("ACNavigationConfiguration", 3);
                return NavigationTypeNocount;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static NavigationConfig createNavigationConfig(final ViewContext viewCtx, final TableNavigatorModel tnModel, final Object navigConfigName) throws Exception {
        final DataObject navigDO = getDBConfiguration(navigConfigName);
        final List<Integer> rangeList = getRangeList(navigDO);
        int pageLength = NavigationConfig.actualPageLength = (int)tnModel.getPageLength();
        pageLength = ((getNocount(viewCtx).equals("true") && NavigationConfig.actualPageLength != -1) ? (pageLength - 1) : pageLength);
        final int pageNum = (int)Math.max(tnModel.getStartIndex() / pageLength + (long)((tnModel.getStartIndex() % pageLength > 0L) ? 1 : 0), 1L);
        viewCtx.setStateOrURLStateParam("_PN", (Object)(pageNum + ""));
        final String isNocount = (String)navigDO.getFirstValue("ACNavigationConfiguration", 4);
        final int totalRecords = "true".equals(isNocount) ? -1 : ((int)tnModel.getTotalRecordsCount());
        int endIndex = (int)tnModel.getEndIndex();
        if ("true".equals(isNocount) && NavigationConfig.actualPageLength != -1 && tnModel.getRowCount() >= pageLength + 1) {
            --endIndex;
        }
        final String navigationName = (String)navigDO.getValue("ACNavigationConfiguration", "NAME", (Row)null);
        final NavigationConfig navigConfig = new NavigationConfig(navigationName, totalRecords, pageNum, pageLength, rangeList, (int)tnModel.getStartIndex(), endIndex);
        navigConfig.navigType = (String)navigDO.getFirstValue("ACNavigationConfiguration", 3);
        navigConfig.orientation = (String)navigDO.getFirstValue("ACNavigationConfiguration", 5);
        navigConfig.nocount = (String)navigDO.getFirstValue("ACNavigationConfiguration", 4);
        navigConfig.navigTemplateName = getNavigTemplate(navigDO);
        return navigConfig;
    }
    
    public static DataObject getDBConfiguration(final Object navigConfigName) throws Exception {
        DataObject navigDO = (DataObject)StaticCache.getFromCache((Object)("NAVIG_CONFIG:" + navigConfigName));
        if (navigDO == null) {
            final Row navigRow = new Row("ACNavigationConfiguration");
            if (navigConfigName instanceof String) {
                navigRow.set(2, navigConfigName);
            }
            else {
                navigRow.set(1, navigConfigName);
            }
            navigDO = LookUpUtil.getPersistence().getForPersonality("NavigationConfig", navigRow);
            Long navigId = null;
            String navigName = null;
            if (navigConfigName instanceof String) {
                navigId = (Long)navigDO.getFirstValue("ACNavigationConfiguration", 1);
                navigName = (String)navigConfigName;
            }
            else {
                navigName = (String)navigDO.getFirstValue("ACNavigationConfiguration", "NAME");
                navigId = (Long)navigConfigName;
            }
            StaticCache.addToCache((Object)("NAVIG_CONFIG:" + navigId), (Object)("NAVIG_CONFIG:" + navigName), (Object)navigDO, PersonalityConfigurationUtil.getConstituentTables("NavigationConfig"));
        }
        return navigDO;
    }
    
    public static String getNavigationConfigName(final Long nameno) throws DataAccessException, Exception {
        return (String)getDBConfiguration(nameno).getValue("ACNavigationConfiguration", "NAME", (Row)null);
    }
    
    public static Long getNavigationConfigNameNo(final String name) throws DataAccessException, Exception {
        return (Long)getDBConfiguration(name).getValue("ACNavigationConfiguration", "NAME_NO", (Row)null);
    }
    
    public static List<Integer> getRangeList(final DataObject navigDO) throws Exception {
        final Long navigConfigNameNo = (Long)navigDO.getValue("ACPageLengthConfig", 1, (Row)null);
        List<Integer> rangeList = (ArrayList)StaticCache.getFromCache((Object)("RANGELIST:" + navigConfigNameNo));
        if (rangeList == null) {
            rangeList = new ArrayList<Integer>();
            final Iterator<Row> iterator = navigDO.getRows("ACPageLengthConfig");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Integer value = (Integer)row.get(2);
                rangeList.add(value);
            }
            StaticCache.addToCache((Object)("RANGELIST:" + navigConfigNameNo), (Object)rangeList);
        }
        return rangeList;
    }
    
    public static String getNavigTemplate(final DataObject navigDO) throws Exception {
        return (String)navigDO.getValue("ACNavigationConfiguration", "TEMPLATENAME", (Row)null);
    }
    
    public String getNavigURLForFirst(final ViewContext vc, final HttpServletRequest request, final String uniqueId) {
        final String navigurl = request.getContextPath() + "/" + uniqueId + ".cc?";
        vc.setURLStateParameter("_FI", (Object)String.valueOf(this.getFirstPageIndex()));
        vc.setURLStateParameter("_PN", (Object)"1");
        vc.setURLStateParameter("_PL", (Object)String.valueOf(this.getPageLength()));
        vc.setURLStateParameter("rootview", (Object)WebViewAPI.getRootView(request, vc));
        return navigurl + WebViewAPI.getAsURLStateParameters((HashMap)vc.getURLState());
    }
    
    public String getNavigURLForPrevious(final ViewContext vc, final HttpServletRequest request, final String uniqueId) {
        final int fromIndex = this.getPreviousPageIndex();
        final int pagenumber = fromIndex / this.getPageLength() + 1;
        final String navigurl = request.getContextPath() + "/" + uniqueId + ".cc?";
        vc.setURLStateParameter("_FI", (Object)String.valueOf(fromIndex));
        vc.setURLStateParameter("_PN", (Object)String.valueOf(pagenumber));
        vc.setURLStateParameter("_PL", (Object)String.valueOf(this.getPageLength()));
        final String rootview = WebViewAPI.getRootView(request, vc);
        vc.setURLStateParameter("rootview", (Object)rootview);
        return navigurl + WebViewAPI.getAsURLStateParameters((HashMap)vc.getURLState());
    }
    
    public String getNavigURLForPageNumber(final ViewContext vc, final HttpServletRequest request, final String uniqueId, final int i) {
        int pagenumber = 1;
        final int fromIndex = this.getPageLength() * (i - 1) + 1;
        pagenumber = fromIndex / this.getPageLength() + 1;
        final String navigurl = request.getContextPath() + "/" + uniqueId + ".cc?";
        vc.setURLStateParameter("_FI", (Object)String.valueOf(fromIndex));
        vc.setURLStateParameter("_PN", (Object)String.valueOf(pagenumber));
        vc.setURLStateParameter("_PL", (Object)String.valueOf(this.getPageLength()));
        final String rootview = WebViewAPI.getRootView(request, vc);
        vc.setURLStateParameter("rootview", (Object)rootview);
        return navigurl + WebViewAPI.getAsURLStateParameters((HashMap)vc.getURLState());
    }
    
    public String getNavigURLForNext(final ViewContext vc, final HttpServletRequest request, final String uniqueId) {
        final int fromIndex = this.getNextPageIndex();
        int pagenumber = 1;
        pagenumber = fromIndex / this.getPageLength() + 1;
        final String navigurl = request.getContextPath() + "/" + uniqueId + ".cc?";
        vc.setURLStateParameter("_FI", (Object)String.valueOf(fromIndex));
        vc.setURLStateParameter("_PN", (Object)String.valueOf(pagenumber));
        vc.setURLStateParameter("_PL", (Object)String.valueOf(this.getPageLength()));
        final String rootview = WebViewAPI.getRootView(request, vc);
        vc.setURLStateParameter("rootview", (Object)rootview);
        return navigurl + WebViewAPI.getAsURLStateParameters((HashMap)vc.getURLState());
    }
    
    public String getNavigURLForLast(final ViewContext vc, final HttpServletRequest request, final String uniqueId) {
        final int fromIndex = this.getLastPageIndex();
        final int pagenumber = fromIndex / this.getPageLength() + 1;
        final String navigurl = request.getContextPath() + "/" + uniqueId + ".cc?";
        vc.setURLStateParameter("_FI", (Object)String.valueOf(fromIndex));
        vc.setURLStateParameter("_PN", (Object)String.valueOf(pagenumber));
        vc.setURLStateParameter("_PL", (Object)String.valueOf(this.getPageLength()));
        final String rootview = WebViewAPI.getRootView(request, vc);
        vc.setURLStateParameter("rootview", (Object)rootview);
        return navigurl + WebViewAPI.getAsURLStateParameters((HashMap)vc.getURLState());
    }
    
    public String getNavigURLForPageLength(final ViewContext vc, final HttpServletRequest request, final String uniqueId, final int length) {
        final String navigurl = request.getContextPath() + "/" + uniqueId + ".cc?";
        vc.setURLStateParameter("_FI", (Object)String.valueOf(this.fromIndex));
        vc.setURLStateParameter("_PN", (Object)String.valueOf(1));
        vc.setURLStateParameter("_PL", (Object)String.valueOf(length));
        final String rootview = WebViewAPI.getRootView(request, vc);
        vc.setURLStateParameter("rootview", (Object)rootview);
        return navigurl + WebViewAPI.getAsURLStateParameters((HashMap)vc.getURLState());
    }
    
    static {
        NavigationConfig.actualPageLength = 0;
    }
}
