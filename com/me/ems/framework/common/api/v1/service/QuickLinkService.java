package com.me.ems.framework.common.api.v1.service;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.quicklink.QuickLinkControllerUtil;
import com.me.ems.framework.common.api.v1.model.QuickLink;
import com.me.ems.framework.common.api.v1.model.QuickLinkGroup;
import java.util.List;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;

public class QuickLinkService
{
    private static Logger logger;
    private static QuickLinkService quickLinkService;
    
    private QuickLinkService() {
    }
    
    public static synchronized QuickLinkService getInstance() {
        if (QuickLinkService.quickLinkService == null) {
            QuickLinkService.quickLinkService = new QuickLinkService();
        }
        return QuickLinkService.quickLinkService;
    }
    
    public QuickLink getDCQuickLink(final User dcUser, final Long pageNumber, final List<QuickLinkGroup> dcQuickLinkGroups) {
        final QuickLink dcQuickLink = new QuickLink();
        try {
            final Long userID = dcUser.getUserID();
            final Integer visualState = QuickLinkControllerUtil.getInstance().getShowHideStatus(pageNumber, userID);
            String primaryContact = LicenseProvider.getInstance().getPrimaryContact();
            final String moduleName = QuickLinkControllerUtil.getInstance().getModuleNamefromPage(pageNumber);
            if (primaryContact == null) {
                final String customerInfoPath = System.getProperty("server.home") + File.separator + "logs" + File.separator + "customerInfo.txt";
                final Properties fileContentProp = FileAccessUtil.readProperties(customerInfoPath);
                primaryContact = fileContentProp.getProperty("Email");
            }
            primaryContact = ((primaryContact != null && !primaryContact.equalsIgnoreCase("")) ? ("&email=" + primaryContact) : "");
            dcQuickLink.setModuleName(moduleName);
            dcQuickLink.setPageNumber(pageNumber);
            dcQuickLink.setPrimaryContact(primaryContact);
            dcQuickLink.setShowHideStatus((visualState == null) ? "show" : ((visualState == 0) ? "hide" : "show"));
            dcQuickLink.setQuickLinkContent(dcQuickLinkGroups);
        }
        catch (final Exception e) {
            QuickLinkService.logger.log(Level.WARNING, "Exception occured in getDCQuickLink in DCQuickLinkService", e);
        }
        return dcQuickLink;
    }
    
    public List<QuickLinkGroup> getDCQuickLinkList(final Long pageNumber) {
        final List<QuickLinkGroup> dcQuickLinkGroups = new ArrayList<QuickLinkGroup>();
        try {
            final List<HashMap<String, Object>> howToContent = QuickLinkControllerUtil.getInstance().getQuickLinkList(pageNumber, "HOWTO");
            final List<HashMap<String, Object>> faqContent = QuickLinkControllerUtil.getInstance().getQuickLinkList(pageNumber, "FQA");
            final List<HashMap<String, Object>> kbContent = QuickLinkControllerUtil.getInstance().getQuickLinkList(pageNumber, "KB");
            final List<HashMap<String, Object>> videoContent = QuickLinkControllerUtil.getInstance().getQuickLinkList(pageNumber, "VIDEO");
            final QuickLinkGroup howToContentLinkGroup = new QuickLinkGroup();
            final QuickLinkGroup fqaContentLinkGroup = new QuickLinkGroup();
            final QuickLinkGroup kbContentLinkGroup = new QuickLinkGroup();
            final QuickLinkGroup videoContentLinkGroup = new QuickLinkGroup();
            if (!howToContent.isEmpty()) {
                howToContentLinkGroup.setCategory("HOWTO");
                howToContentLinkGroup.setLinks(howToContent);
                howToContentLinkGroup.setLabel(I18N.getMsg("desktopcentral.common.quicklinks.How_Tos", new Object[0]));
                howToContentLinkGroup.setMoreUrl("/how-to.html");
                dcQuickLinkGroups.add(howToContentLinkGroup);
            }
            if (!kbContent.isEmpty()) {
                kbContentLinkGroup.setCategory("KB");
                kbContentLinkGroup.setLabel(I18N.getMsg("dc.common.KNOWLEDGE_BASE", new Object[0]));
                kbContentLinkGroup.setLinks(kbContent);
                kbContentLinkGroup.setMoreUrl("/knowledge-base.html");
                dcQuickLinkGroups.add(kbContentLinkGroup);
            }
            if (!faqContent.isEmpty()) {
                fqaContentLinkGroup.setCategory("FAQ");
                fqaContentLinkGroup.setLabel(I18N.getMsg("dc.common.FAQ", new Object[0]));
                fqaContentLinkGroup.setLinks(faqContent);
                fqaContentLinkGroup.setMoreUrl("/faq.html");
                dcQuickLinkGroups.add(fqaContentLinkGroup);
            }
            if (!videoContent.isEmpty()) {
                videoContentLinkGroup.setCategory("VIDEO");
                videoContentLinkGroup.setLabel(I18N.getMsg("dc.common.VIDEOS", new Object[0]));
                videoContentLinkGroup.setMoreUrl("/demo/desktop-management-videos.html");
                videoContentLinkGroup.setLinks(videoContent);
                dcQuickLinkGroups.add(videoContentLinkGroup);
            }
        }
        catch (final Exception e) {
            QuickLinkService.logger.log(Level.WARNING, "Exception occured while getDCQuickLinkList in DCQuickLinkService", e);
        }
        return dcQuickLinkGroups;
    }
    
    static {
        QuickLinkService.logger = Logger.getLogger(QuickLinkService.class.getName());
        QuickLinkService.quickLinkService = null;
    }
}
