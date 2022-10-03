package com.adventnet.client.box.web;

import java.util.HashMap;
import javax.servlet.jsp.PageContext;
import java.util.logging.Level;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.client.themes.web.ThemesAPI;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;

public class BoxAPI implements WebConstants, JavaScriptConstants
{
    protected static Logger logger;
    
    public static String getBoxConfigForView(final ViewContext viewCtx) throws Exception {
        String boxCr = (String)viewCtx.getTransientState("BOXCONFIG");
        if (boxCr == null) {
            boxCr = setBoxForView(viewCtx, (String)viewCtx.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 11), true);
        }
        return boxCr;
    }
    
    public static boolean isViewOpen(final ViewContext viewCtx) throws Exception {
        final String isOpen = (String)viewCtx.getTransientState("ISOPEN");
        return isOpen == null || isOpen == "true";
    }
    
    public static String setBoxForView(final ViewContext viewCtx, final String boxConfig, final boolean isOpen) throws Exception {
        String isBoxOpen;
        if (isOpen) {
            isBoxOpen = "true";
        }
        else {
            isBoxOpen = "false";
        }
        viewCtx.setTransientState("ISOPEN", isBoxOpen);
        viewCtx.setTransientState("BOXCONFIG", boxConfig);
        return boxConfig;
    }
    
    public static String getBoxPrefix(final ViewContext viewCtx, final HttpServletResponse response) throws Exception {
        final boolean isMaximized = isViewOpen(viewCtx);
        final String boxContentClass = isMaximized ? "boxContent" : "hideBoxContent";
        String maxBtnClass;
        String minBtnClass;
        if (isMaximized) {
            maxBtnClass = "hide";
            minBtnClass = "minButton";
        }
        else {
            maxBtnClass = "maxButton";
            minBtnClass = "hide";
        }
        final String boxConfig = getBoxConfigForView(viewCtx);
        if (boxConfig == null || "null".equals(boxConfig)) {
            return "";
        }
        final String boxId = "BOX_" + viewCtx.getReferenceId();
        final String themeName = ThemesAPI.getTheme();
        if (themeName == null) {
            return "";
        }
        final String toolBarContent = MenuVariablesGenerator.getToolBarLinks(viewCtx, response);
        String htmlContent;
        if (TemplateAPI.htmlmap.containsKey(themeName + "_" + boxConfig + "_Prefix")) {
            htmlContent = TemplateAPI.givehtml(themeName + "_" + boxConfig + "_Prefix", null, new Object[][] { { "BOXID", boxId }, { "TITLE", viewCtx.getTitle() }, { "TOOLBAR", toolBarContent }, { "BOXCONTENTCLASS", boxContentClass }, { "MAXBTNCLASS", maxBtnClass }, { "MINBTNCLASS", minBtnClass } });
        }
        else {
            if (!TemplateAPI.htmlmap.containsKey(boxConfig + "_Prefix")) {
                BoxAPI.logger.log(Level.FINE, boxConfig + "_Prefix Box not present in template file");
                return "";
            }
            htmlContent = TemplateAPI.givehtml(boxConfig + "_Prefix", null, new Object[][] { { "BOXID", boxId }, { "TITLE", viewCtx.getTitle() }, { "TOOLBAR", toolBarContent }, { "BOXCONTENTCLASS", boxContentClass }, { "MAXBTNCLASS", maxBtnClass }, { "MINBTNCLASS", minBtnClass } });
        }
        return htmlContent + "<div>";
    }
    
    public static String getBoxSuffix(final ViewContext viewCtx, final HttpServletResponse response) throws Exception {
        final boolean isMaximized = isViewOpen(viewCtx);
        final String toolBarContent = MenuVariablesGenerator.getToolBarLinks(viewCtx, response);
        final String boxContentClass = isMaximized ? "boxContent" : "hideBoxContent";
        String maxBtnClass;
        String minBtnClass;
        if (isMaximized) {
            maxBtnClass = "hide";
            minBtnClass = "minButton";
        }
        else {
            maxBtnClass = "maxButton";
            minBtnClass = "hide";
        }
        final String boxConfig = getBoxConfigForView(viewCtx);
        if (boxConfig == null || "null".equals(boxConfig)) {
            return "";
        }
        final String boxId = "BOX_" + viewCtx.getReferenceId();
        final String themeName = ThemesAPI.getTheme();
        if (themeName == null) {
            return "";
        }
        String htmlContent;
        if (TemplateAPI.htmlmap.containsKey(themeName + "_" + boxConfig + "_Suffix")) {
            htmlContent = TemplateAPI.givehtml(themeName + "_" + boxConfig + "_Suffix", null, new Object[][] { { "BOXID", boxId }, { "TITLE", viewCtx.getTitle() }, { "TOOLBAR", toolBarContent }, { "BOXCONTENTCLASS", boxContentClass }, { "MAXBTNCLASS", maxBtnClass }, { "MINBTNCLASS", minBtnClass } });
        }
        else {
            if (!TemplateAPI.htmlmap.containsKey(boxConfig + "_Suffix")) {
                BoxAPI.logger.log(Level.FINE, boxConfig + "_Suffix Box not present in template file");
                return "";
            }
            htmlContent = TemplateAPI.givehtml(boxConfig + "_Suffix", null, new Object[][] { { "BOXID", boxId }, { "TITLE", viewCtx.getTitle() }, { "TOOLBAR", toolBarContent }, { "BOXCONTENTCLASS", boxContentClass }, { "MAXBTNCLASS", maxBtnClass }, { "MINBTNCLASS", minBtnClass } });
        }
        return "</div>" + htmlContent;
    }
    
    public static String getHtml(final String key, final String boxId, final String title, final PageContext pageContext, final boolean isOpen, HashMap options) throws Exception {
        boolean isMaximized = true;
        isMaximized = isOpen;
        final String boxContentClass = isMaximized ? "boxContent" : "hideBoxContent";
        String maxBtnClass;
        String minBtnClass;
        if (isMaximized) {
            maxBtnClass = "hide";
            minBtnClass = "minButton";
        }
        else {
            maxBtnClass = "maxButton";
            minBtnClass = "hide";
        }
        final String themeName = ThemesAPI.getTheme();
        final String fullkey = themeName + "_" + key;
        if (options == null) {
            options = new HashMap();
        }
        String htmlContent;
        if (TemplateAPI.htmlmap.containsKey(fullkey)) {
            options.put("BOXID", boxId);
            options.put("TITLE", title);
            options.put("BOXCONTENTCLASS", boxContentClass);
            options.put("MAXBTNCLASS", maxBtnClass);
            options.put("MINBTNCLASS", minBtnClass);
            htmlContent = TemplateAPI.givehtml(fullkey, null, options);
        }
        else if (TemplateAPI.htmlmap.containsKey(key)) {
            options.put("BOXID", boxId);
            options.put("TITLE", title);
            options.put("BOXCONTENTCLASS", boxContentClass);
            options.put("MAXBTNCLASS", maxBtnClass);
            options.put("MINBTNCLASS", minBtnClass);
            htmlContent = TemplateAPI.givehtml(key, null, options);
        }
        else {
            htmlContent = "";
        }
        return htmlContent;
    }
    
    static {
        BoxAPI.logger = Logger.getLogger(BoxAPI.class.getName());
    }
}
