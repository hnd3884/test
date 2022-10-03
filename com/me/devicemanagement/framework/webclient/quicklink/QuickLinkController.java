package com.me.devicemanagement.framework.webclient.quicklink;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.LinkedHashMap;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.form.web.AjaxFormController;

public class QuickLinkController extends AjaxFormController
{
    public final String quickLinkTrackingCodKey = "tracking-quicklinks";
    String className;
    Logger logger;
    String didValue;
    String did;
    
    public void updateViewModel(final ViewContext context) throws Exception {
        super.updateViewModel(context);
        context.setTitle(ProductUrlLoader.getInstance().getValue("title"));
    }
    
    public QuickLinkController() {
        this.className = QuickLinkController.class.getName();
        this.logger = Logger.getLogger(this.className);
        this.didValue = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2);
        this.did = ((this.didValue != null) ? ("&did=" + this.didValue) : "&did=");
    }
    
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        super.processPostRendering(viewCtx, request, response);
    }
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        try {
            super.processPreRendering(viewCtx, request, response, viewUrl);
            final String pageNumber = request.getParameter("pageNumber");
            String showHideStatus = request.getParameter("showHideStatus");
            if (showHideStatus != null) {
                this.setShowHideStatus(showHideStatus, pageNumber, viewCtx.getRequest());
            }
            else {
                showHideStatus = this.getShowHideStatus(pageNumber, viewCtx.getRequest());
            }
            String primaryContact = LicenseProvider.getInstance().getPrimaryContact();
            if (primaryContact == null) {
                final String customerInfoPath = System.getProperty("server.home") + File.separator + "logs" + File.separator + "customerInfo.txt";
                final Properties fileContentProp = FileAccessUtil.readProperties(customerInfoPath);
                primaryContact = fileContentProp.getProperty("Email");
            }
            primaryContact = ((primaryContact instanceof String && !primaryContact.equalsIgnoreCase("")) ? ("&email=" + primaryContact) : "");
            final DataObject quickLinkDO = this.getPageDetails(pageNumber);
            final LinkedHashMap howtoLinks = this.getHowToDetails(quickLinkDO);
            final LinkedHashMap kbLinks = this.getKBDetails(quickLinkDO);
            final LinkedHashMap videoLinks = this.getVideoDetails(quickLinkDO);
            final LinkedHashMap fqaLinks = this.getFQADetails(quickLinkDO);
            if (!howtoLinks.isEmpty()) {
                viewCtx.getRequest().setAttribute("HOWTO", (Object)howtoLinks);
            }
            if (!kbLinks.isEmpty()) {
                viewCtx.getRequest().setAttribute("KB", (Object)kbLinks);
            }
            if (!videoLinks.isEmpty()) {
                viewCtx.getRequest().setAttribute("VIDEO", (Object)videoLinks);
            }
            if (!fqaLinks.isEmpty()) {
                viewCtx.getRequest().setAttribute("FQA", (Object)fqaLinks);
            }
            final String moduleName = this.getModuleNamefromPage(pageNumber);
            viewCtx.getRequest().setAttribute("MODULE_NAME", (Object)moduleName);
            viewCtx.getRequest().setAttribute("PAGENUMBER", (Object)pageNumber);
            viewCtx.getRequest().setAttribute("SHOWHIDESTATUS", (Object)showHideStatus);
            viewCtx.getRequest().setAttribute("prodUrl", (Object)ProductUrlLoader.getInstance().getValue("prodUrl"));
            viewCtx.getRequest().setAttribute("trackCode", (Object)(ProductUrlLoader.getInstance().getValue("tracking-quicklinks") + ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2)));
            viewCtx.getRequest().setAttribute("PrimaryContact", (Object)((primaryContact != null) ? primaryContact : ""));
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "processPreRendering() Exception ", e);
        }
        return viewUrl;
    }
    
    private String getModuleNamefromPage(final String pageNumber) {
        String moduleName = "";
        try {
            moduleName = (String)DBUtil.getValueFromDB("DCUIPage", "PAGE_ID", pageNumber, "MODULE_NAME");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "getModuleName() Exception ", e);
        }
        return moduleName;
    }
    
    private DataObject getPageDetails(final String pageNumber) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DCUIPage"));
            sq.addSelectColumn(new Column((String)null, "*"));
            Criteria cri = new Criteria(Column.getColumn("DCUIPage", "PAGE_ID"), (Object)pageNumber, 0);
            final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
            cri = cri.and(productCodeCriteria);
            final Join join = new Join("DCUIPage", "DCQuickLink", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1);
            final Join join2 = new Join("DCQuickLink", "DMProductToQuickLink", new String[] { "ARTICLE_ID" }, new String[] { "ARTICLE_ID" }, 1);
            sq.addJoin(join);
            sq.setCriteria(cri);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("DCQuickLink", "DISPLAY_ORDER"), true);
            sq.addSortColumn(sortColumn);
            final Persistence per = SyMUtil.getPersistence();
            final DataObject dataObj = per.get(sq);
            this.logger.log(Level.FINEST, "getPageDetails() dataObj {0} ", dataObj);
            return dataObj;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "getPageDetails() Exception ", e);
            return null;
        }
    }
    
    private LinkedHashMap getHowToDetails(final DataObject dobj) {
        final LinkedHashMap howtoLinks = new LinkedHashMap();
        try {
            if (dobj != null && !dobj.isEmpty()) {
                final Criteria cri = new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_TYPE"), (Object)"HOWTO", 0);
                final Iterator itr = dobj.getRows("DCQuickLink", cri);
                while (itr.hasNext()) {
                    final Row r = itr.next();
                    String dispName = (String)r.get("DISPLAY_NAME");
                    dispName = I18N.getMsg(dispName, new Object[0]);
                    final String url = (String)r.get("ARTICLE_URL");
                    final String resultUrl = UrlReplacementUtil.replaceUrlAndAppendTrackCode(url, ProductUrlLoader.getInstance().getValue("tracking-quicklinks"));
                    howtoLinks.put(dispName, resultUrl);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "getHowToDetails() Exception ", e);
        }
        return howtoLinks;
    }
    
    private LinkedHashMap getKBDetails(final DataObject dobj) {
        final LinkedHashMap kbLinks = new LinkedHashMap();
        try {
            if (dobj != null && !dobj.isEmpty()) {
                final Criteria cri = new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_TYPE"), (Object)"KB", 0);
                final Iterator itr = dobj.getRows("DCQuickLink", cri);
                while (itr.hasNext()) {
                    final Row r = itr.next();
                    String dispName = (String)r.get("DISPLAY_NAME");
                    dispName = I18N.getMsg(dispName, new Object[0]);
                    final String url = (String)r.get("ARTICLE_URL");
                    final String resultUrl = UrlReplacementUtil.replaceUrlAndAppendTrackCode(url, ProductUrlLoader.getInstance().getValue("tracking-quicklinks"));
                    kbLinks.put(dispName, resultUrl);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "getKBDetails() Exception ", e);
        }
        return kbLinks;
    }
    
    private LinkedHashMap getVideoDetails(final DataObject dobj) {
        final LinkedHashMap videoLinks = new LinkedHashMap();
        try {
            if (dobj != null && !dobj.isEmpty()) {
                final Criteria cri = new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_TYPE"), (Object)"VIDEO", 0);
                final Iterator itr = dobj.getRows("DCQuickLink", cri);
                while (itr.hasNext()) {
                    final Row r = itr.next();
                    String dispName = (String)r.get("DISPLAY_NAME");
                    dispName = I18N.getMsg(dispName, new Object[0]);
                    final String url = (String)r.get("ARTICLE_URL");
                    final String resultUrl = UrlReplacementUtil.replaceUrlAndAppendTrackCode(url, ProductUrlLoader.getInstance().getValue("tracking-quicklinks"));
                    videoLinks.put(dispName, resultUrl);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "getVideoDetails() Exception ", e);
        }
        return videoLinks;
    }
    
    private LinkedHashMap getFQADetails(final DataObject dobj) {
        final LinkedHashMap fqaLinks = new LinkedHashMap();
        try {
            if (dobj != null && !dobj.isEmpty()) {
                final Criteria cri = new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_TYPE"), (Object)"FQA", 0);
                final Iterator itr = dobj.getRows("DCQuickLink", cri);
                while (itr.hasNext()) {
                    final Row r = itr.next();
                    String dispName = (String)r.get("DISPLAY_NAME");
                    dispName = I18N.getMsg(dispName, new Object[0]);
                    final String url = (String)r.get("ARTICLE_URL");
                    final String resultUrl = UrlReplacementUtil.replaceUrlAndAppendTrackCode(url, ProductUrlLoader.getInstance().getValue("tracking-quicklinks"));
                    fqaLinks.put(dispName, resultUrl);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "getFQADetails() Exception ", e);
        }
        return fqaLinks;
    }
    
    private void setShowHideStatus(final String showHideStatus, final String pageNumber, final HttpServletRequest req) {
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DCQuickLinkDisplayStatus"));
            sq.addSelectColumn(new Column((String)null, "*"));
            Criteria cri = new Criteria(Column.getColumn("DCQuickLinkDisplayStatus", "PAGE_ID"), (Object)pageNumber, 0);
            final Criteria cri2 = new Criteria(Column.getColumn("DCQuickLinkDisplayStatus", "USER_ID"), (Object)userID, 0);
            cri = cri.and(cri2);
            sq.setCriteria(cri);
            final Persistence per = SyMUtil.getPersistence();
            DataObject dataObj = per.get(sq);
            if (dataObj != null && !dataObj.isEmpty()) {
                final Row statusRow = dataObj.getRow("DCQuickLinkDisplayStatus");
                statusRow.set("VISUAL_STATE", (Object)showHideStatus);
                statusRow.set("USER_ID", (Object)userID);
                dataObj.updateRow(statusRow);
                per.update(dataObj);
            }
            else {
                dataObj = SyMUtil.getPersistence().constructDataObject();
                final Row statusRow = new Row("DCQuickLinkDisplayStatus");
                statusRow.set("PAGE_ID", (Object)pageNumber);
                statusRow.set("VISUAL_STATE", (Object)showHideStatus);
                statusRow.set("USER_ID", (Object)userID);
                dataObj.addRow(statusRow);
                per.add(dataObj);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "setShowHideStatus() Exception ", e);
        }
    }
    
    private String getShowHideStatus(final String pageNumber, final HttpServletRequest req) {
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DCQuickLinkDisplayStatus"));
            sq.addSelectColumn(new Column((String)null, "*"));
            Criteria cri = new Criteria(Column.getColumn("DCQuickLinkDisplayStatus", "PAGE_ID"), (Object)pageNumber, 0);
            final Criteria cri2 = new Criteria(Column.getColumn("DCQuickLinkDisplayStatus", "USER_ID"), (Object)userID, 0);
            cri = cri.and(cri2);
            sq.setCriteria(cri);
            final Persistence per = SyMUtil.getPersistence();
            final DataObject dataObj = per.get(sq);
            if (dataObj != null && !dataObj.isEmpty()) {
                final Row statusRow = dataObj.getRow("DCQuickLinkDisplayStatus");
                final String statusValue = statusRow.get("VISUAL_STATE") + "";
                return statusValue;
            }
            return null;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "getShowHideStatus() Exception ", e);
            return null;
        }
    }
}
