package com.adventnet.client.components.table.template;

import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.i18n.I18N;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.view.web.ViewContext;

public class TemplateMenuGenerator
{
    public static String getMenu(final String viewName, final ViewContext vcxt) throws Exception {
        final String orientation = null;
        final String type = null;
        String reqParams = null;
        final String ctxPath = vcxt.getRequest().getContextPath();
        String classId = null;
        if (type == null || type.equals("button")) {
            if (orientation == null || orientation.equals("horizontal")) {
                classId = "hmenubutton";
            }
            else if (orientation.equals("vertical")) {
                classId = "vmenubutton";
            }
        }
        else if (type.equals("link")) {
            if (orientation == null || orientation.equals("horizontal")) {
                classId = "hmenu";
            }
            else if (orientation.equals("vertical")) {
                classId = "vmenu";
            }
        }
        if (reqParams != null) {
            reqParams = "'" + reqParams + "'";
        }
        else {
            reqParams = "null";
        }
        final StringBuffer sb = new StringBuffer();
        final Persistence persistence = LookUpUtil.getPersistence();
        final Column col = new Column("ViewConfiguration", "VIEWNAME");
        final Criteria crit = new Criteria(col, (Object)viewName, 0);
        final DataObject dobj = persistence.get("ViewConfiguration", crit);
        final Row viewConfigRow = dobj.getFirstRow("ViewConfiguration");
        final String menuName = (String)viewConfigRow.get("MENUID");
        if (menuName != null) {
            final HashMap menuProps = MenuVariablesGenerator.getMenuItemProps(menuName);
            sb.append("<ul class=\"" + classId + "\" id=\"menu_" + IAMEncoder.encodeHTMLAttribute(menuName) + "\">");
            for (int i = 1; menuProps.containsKey(i + "_MENUITEMID") && i < 5; ++i) {
                if (menuProps.containsKey(i + "_MENUITEMID")) {
                    final String menuItemId = menuProps.get(i + "_MENUITEMID");
                    final String displayType = "BOTH";
                    String imgSrc = "";
                    if (menuProps.get(i + "_IMAGE") != null) {
                        imgSrc = menuProps.get(i + "_IMAGE");
                    }
                    String str = "";
                    if (menuProps.get(i + "_DISPLAYNAME") != null) {
                        str = menuProps.get(i + "_DISPLAYNAME");
                    }
                    String imageCSSClass = "";
                    if (menuProps.get(i + "_IMAGECSSCLASS") != null) {
                        imageCSSClass = menuProps.get(i + "_IMAGECSSCLASS");
                    }
                    if (imgSrc != null && !imgSrc.equals("")) {
                        imgSrc = ((imgSrc.charAt(0) == '/') ? (ctxPath + imgSrc) : imgSrc);
                    }
                    sb.append("<li id=\"menuitem_" + IAMEncoder.encodeHTMLAttribute(menuItemId) + "\">");
                    sb.append(" <a href=\"javascript:invokeMenuAction('" + IAMEncoder.encodeJavaScript(menuItemId) + "','" + IAMEncoder.encodeJavaScript(viewName) + "'," + (Object)null + ")\" title=\"" + IAMEncoder.encodeHTMLAttribute(I18N.getMsg(str, new Object[0])) + "\">");
                    if (imgSrc != null && imgSrc.trim().length() != 0 && ("IMAGE".equals(displayType) || "BOTH".equals(displayType))) {
                        sb.append("<img src=\"" + IAMEncoder.encodeHTMLAttribute(imgSrc) + "\"/>");
                    }
                    else {
                        sb.append("<span  class=\"" + IAMEncoder.encodeHTMLAttribute(imageCSSClass) + "\" ></span>");
                    }
                    if (str != null && ("TEXT".equals(displayType) || "BOTH".equals(displayType))) {
                        sb.append(IAMEncoder.encodeHTML(I18N.getMsg(str, new Object[0])));
                    }
                    sb.append("</a>");
                    sb.append("</li>");
                }
            }
            sb.append("</ul>");
            return sb.toString();
        }
        return "";
    }
}
