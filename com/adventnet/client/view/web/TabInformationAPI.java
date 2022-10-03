package com.adventnet.client.view.web;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaModel;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Document;

public class TabInformationAPI
{
    private static Document tabinfodoc;
    
    private static Document getTabInfoDoc(final HttpServletRequest request) {
        if (TabInformationAPI.tabinfodoc != null) {
            return TabInformationAPI.tabinfodoc;
        }
        try {
            final Properties dbfFeatures = new Properties();
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://apache.org/xml/features/dom/include-ignorable-whitespace", false);
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://apache.org/xml/features/include-comments", false);
            final DocumentBuilder docBuilder = SecurityUtil.createDocumentBuilder(false, false, dbfFeatures);
            final String tabinfoxml = request.getSession().getServletContext().getInitParameter("tabinfoxml");
            TabInformationAPI.tabinfodoc = docBuilder.parse(System.getProperty("server.home") + tabinfoxml);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return TabInformationAPI.tabinfodoc;
    }
    
    public static Element getRootViewEL(final String rootview, final HttpServletRequest request) {
        final Document doc = getTabInfoDoc(request);
        final NodeList nl = doc.getElementsByTagName("RootView");
        for (int len = nl.getLength(), i = 0; i < len; ++i) {
            final Element el = (Element)nl.item(i);
            final String rview = el.getAttribute("name");
            if (rview.equals(rootview)) {
                return el;
            }
        }
        return null;
    }
    
    public static String getParentView(final HttpServletRequest request, String childmcframe, final String rootview) {
        String parentmcframe = null;
        final Element rootel = getRootViewEL(rootview, request);
        final NodeList nl = rootel.getElementsByTagName("MCFrame");
        final int len = nl.getLength();
        if (childmcframe == null) {
            childmcframe = rootview + "_CONTENTAREA";
        }
        int i = 0;
        while (i < len) {
            final Element el = (Element)nl.item(i);
            final String mcframename = el.getAttribute("name");
            if (mcframename.equals(childmcframe)) {
                final Element parel = (Element)el.getParentNode();
                if (parel.getTagName().equals("MCFrame")) {
                    parentmcframe = parel.getAttribute("name");
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        if (parentmcframe == null) {
            return null;
        }
        final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(request, parentmcframe);
        final List contentlist = model.getContentList();
        if (contentlist.size() > 0) {
            final ViewContext parentview = contentlist.get(0);
            return parentview.toString();
        }
        return null;
    }
    
    public static int getMCFrameLevel(final String rootview, final String mcframename, final HttpServletRequest request) {
        final Element rootel = getRootViewEL(rootview, request);
        final NodeList nl = rootel.getElementsByTagName("MCFrame");
        for (int len = nl.getLength(), i = 0; i < len; ++i) {
            final Element el = (Element)nl.item(i);
            if (el.getAttribute("name").equals(mcframename)) {
                return Integer.parseInt(el.getAttribute("level"));
            }
        }
        return 0;
    }
    
    public static Element getMCFrameElement(final String rootview, final String mcframename, final HttpServletRequest request) {
        final Element rootel = getRootViewEL(rootview, request);
        final NodeList nl = rootel.getElementsByTagName("MCFrame");
        for (int len = nl.getLength(), i = 0; i < len; ++i) {
            final Element el = (Element)nl.item(i);
            if (el.getAttribute("name").equals(mcframename)) {
                return el;
            }
        }
        return null;
    }
    
    public static ArrayList getTabMCFrameNames(final int level, final String rootview, final HttpServletRequest request) {
        final Element rootel = getRootViewEL(rootview, request);
        final NodeList nl = rootel.getElementsByTagName("MCFrame");
        final int len = nl.getLength();
        final ArrayList list = new ArrayList();
        for (int i = 0; i < len; ++i) {
            final Element el = (Element)nl.item(i);
            if (Integer.parseInt(el.getAttribute("level")) == level) {
                list.add(((Element)nl.item(i)).getAttribute("name"));
            }
        }
        return list;
    }
    
    public static String getTabsInfoAsURL(final String rootview, final HttpServletRequest request, String contentAreaName) {
        String url = "";
        if (contentAreaName == null) {
            contentAreaName = rootview + "_CONTENTAREA";
        }
        final Element mcel = getMCFrameElement(rootview, contentAreaName, request);
        Element par_mcel = (Element)mcel.getParentNode();
        final ArrayList tabslist = new ArrayList();
        while (par_mcel != null && par_mcel.getTagName().equals("MCFrame")) {
            final String mcframe = par_mcel.getAttribute("name");
            final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(request, mcframe);
            final Object curritem = model.getCurrentItem();
            if (curritem == null) {
                continue;
            }
            tabslist.add(curritem);
            par_mcel = (Element)par_mcel.getParentNode();
        }
        for (int x = tabslist.size() - 1; x >= 0; --x) {
            url = url + "/" + tabslist.get(x);
        }
        return url;
    }
    
    public static String getDACListParams(final String contentAreaName, final HttpServletRequest request) {
        final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(request, contentAreaName);
        return "s:" + contentAreaName + "_LIST=" + model.getContentIdsAsString();
    }
    
    public static ArrayList getDACListFromParams(final String contentAreaName, final HttpServletRequest request) {
        final HashMap map = WebViewAPI.getURLStateParameterMap(request);
        final String daclist = map.get(contentAreaName + "_LIST");
        if (daclist == null) {
            return null;
        }
        final StringTokenizer stok = new StringTokenizer(daclist, "-");
        final ArrayList list = new ArrayList();
        while (stok.hasMoreTokens()) {
            list.add(stok.nextToken());
        }
        return list;
    }
}
