package com.me.devicemanagement.framework.webclient.common;

import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Collection;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.io.File;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import javax.servlet.http.HttpSession;
import com.adventnet.client.view.web.ViewContext;
import org.json.JSONObject;
import java.util.Locale;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.persistence.DataAccessException;
import java.security.Principal;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.resource.ResourceDataProvider;
import com.me.devicemanagement.framework.server.util.Utils;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import com.me.devicemanagement.framework.server.exception.NativeException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.ByteArrayOutputStream;
import org.w3c.dom.Node;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import java.util.StringTokenizer;
import com.adventnet.customview.ViewData;
import java.util.List;
import com.adventnet.customview.CustomViewException;
import javax.naming.NamingException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class SYMClientUtil
{
    protected static String className;
    protected static Logger out;
    public static SYMClientUtil symclient;
    
    public static SYMClientUtil getInstance() {
        if (SYMClientUtil.symclient == null) {
            SYMClientUtil.symclient = new SYMClientUtil();
        }
        return SYMClientUtil.symclient;
    }
    
    public static TableNavigatorModel getTableNavigatorModel(final SelectQuery selectQuery) throws SyMException {
        try {
            if (selectQuery.getSortColumns().isEmpty()) {
                final List selectColumnsList = selectQuery.getSelectColumns();
                if (selectColumnsList.size() > 0) {
                    final SortColumn sort = new SortColumn((Column)selectColumnsList.get(0), true);
                    selectQuery.addSortColumn(sort);
                }
            }
            final CustomViewManager manager = (CustomViewManager)BeanUtil.lookup("TableViewManager");
            final CustomViewRequest cvReq = new CustomViewRequest(selectQuery);
            final ViewData vData = manager.getData(cvReq);
            final TableNavigatorModel tableModel = (TableNavigatorModel)vData.getModel();
            return tableModel;
        }
        catch (final NamingException exp) {
            SYMClientUtil.out.log(Level.WARNING, "NamingException while getting table model...", exp);
            throw new SyMException(1001, exp);
        }
        catch (final CustomViewException exp2) {
            SYMClientUtil.out.log(Level.WARNING, "CustomViewException while getting table model...", (Throwable)exp2);
            throw new SyMException(1001, (Throwable)exp2);
        }
        catch (final Exception exp3) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while getting table model...", exp3);
            throw new SyMException(1001, exp3);
        }
    }
    
    public static SortColumn getSortColumn(final SelectQuery selectQuery, final String columnName, final String ascending) {
        SortColumn sortCol = null;
        final List colList = selectQuery.getSelectColumns();
        final int size = colList.size();
        int i = 0;
        while (i < size) {
            final Column column = colList.get(i);
            String sortColName = column.getColumnName();
            String sortColAlias = column.getColumnAlias();
            if (sortColName == null) {
                final Column innerColumn = column.getColumn();
                sortColName = innerColumn.getColumnName();
                sortColAlias = innerColumn.getColumnAlias();
            }
            if (sortColName.equalsIgnoreCase(columnName) || (sortColAlias != null && sortColAlias.equalsIgnoreCase(columnName))) {
                if (ascending.equals("true")) {
                    sortCol = new SortColumn(column, true);
                    break;
                }
                sortCol = new SortColumn(column, false);
                break;
            }
            else {
                ++i;
            }
        }
        return sortCol;
    }
    
    public static String getFileNameFromSharePath(final String filePath) {
        final StringTokenizer st = new StringTokenizer(filePath, "\\");
        String fileName = null;
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (token.indexOf(".") != -1) {
                fileName = token;
            }
        }
        return fileName;
    }
    
    public static boolean isIPAddress(final String string) {
        final String[] ipAddress = string.split("\\.");
        if (ipAddress.length != 4) {
            return false;
        }
        for (int i = 0; i < ipAddress.length; ++i) {
            Integer temp = null;
            try {
                temp = Integer.parseInt(ipAddress[i]);
            }
            catch (final NumberFormatException exp) {
                return false;
            }
            if (temp > 255 || temp < 0) {
                return false;
            }
        }
        return true;
    }
    
    public static void createXMLObject(final Properties parentProps, final String parentElement, final List list, final HttpServletResponse response) {
        ByteArrayOutputStream outNode = null;
        try {
            final Document document = createDocument();
            final Element data = document.createElement("DesktopCentral");
            document.appendChild(data);
            if (parentProps != null) {
                final Element parent = getElementFromProperties(document, parentProps, "parent");
                data.appendChild(parent);
            }
            for (int i = 0; i < list.size(); ++i) {
                final Properties childProps = list.get(i);
                final Element parent2 = getElementFromProperties(document, childProps, parentElement);
                data.appendChild(parent2);
            }
            outNode = (ByteArrayOutputStream)createXMLDocument(data);
            createXML(outNode, response);
        }
        catch (final Exception e) {
            SYMClientUtil.out.log(Level.WARNING, "Exception has occured while creating the XMLObject...", e);
            try {
                if (outNode != null) {
                    outNode.close();
                    outNode = null;
                }
            }
            catch (final Exception e) {
                SYMClientUtil.out.log(Level.WARNING, "Exception has occured while closing the streams...", e);
            }
        }
        finally {
            try {
                if (outNode != null) {
                    outNode.close();
                    outNode = null;
                }
            }
            catch (final Exception e2) {
                SYMClientUtil.out.log(Level.WARNING, "Exception has occured while closing the streams...", e2);
            }
        }
    }
    
    public static void createXMLObject(final String parentElement, final List list, final HttpServletResponse response) {
        createXMLObject(null, parentElement, list, response);
    }
    
    public static Element getElementFromProperties(final Document document, final Properties childProps, final String parentElement) {
        final Element parent = document.createElement(parentElement);
        final Enumeration e = childProps.keys();
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            parent.setAttribute(key, childProps.getProperty(key));
        }
        return parent;
    }
    
    public static void createXMLObject(final String parentElement, final Properties childProps, final HttpServletResponse response) {
        ByteArrayOutputStream outNode = null;
        try {
            final Document document = createDocument();
            final Element data = document.createElement("DesktopCentral");
            document.appendChild(data);
            final Element parent = getElementFromProperties(document, childProps, parentElement);
            data.appendChild(parent);
            outNode = (ByteArrayOutputStream)createXMLDocument(data);
            createXML(outNode, response);
        }
        catch (final Exception e) {
            SYMClientUtil.out.log(Level.WARNING, "Exception has occured while creating the XMLObject...", e);
            try {
                if (outNode != null) {
                    outNode.close();
                    outNode = null;
                }
            }
            catch (final Exception e) {
                SYMClientUtil.out.log(Level.WARNING, "Exception has occured while closing the streams...", e);
            }
        }
        finally {
            try {
                if (outNode != null) {
                    outNode.close();
                    outNode = null;
                }
            }
            catch (final Exception e2) {
                SYMClientUtil.out.log(Level.WARNING, "Exception has occured while closing the streams...", e2);
            }
        }
    }
    
    public static List getStringList(final String valueWithCommaSep, final String delimiter) {
        final List valueList = new ArrayList();
        final StringTokenizer strTok = new StringTokenizer(valueWithCommaSep, delimiter);
        while (strTok.hasMoreTokens()) {
            String value = strTok.nextToken();
            value = value.trim();
            valueList.add(value);
        }
        return valueList;
    }
    
    public static long getAddrLong(final String s) {
        long l = 0L;
        if (s == null) {
            return 0L;
        }
        final boolean isIP = isIPAddress(s);
        if (isIP) {
            final String[] Ipaddr = s.split("\\.");
            final int[] ai = new int[4];
            try {
                for (int i = 0; i < 4; ++i) {
                    ai[i] = Integer.parseInt(Ipaddr[i]);
                }
            }
            catch (final NumberFormatException numberformatexception) {
                SYMClientUtil.out.log(Level.SEVERE, "Ipadrress getAddrArray() : Number format exception occured. So returning null.");
                return 0L;
            }
            if (ai == null) {
                return 0L;
            }
            for (int i = 0; i < 4; ++i) {
                l |= (long)ai[i] << 8 * (3 - i);
            }
        }
        return l;
    }
    
    public static HashMap getParameterMap(final HttpServletRequest request) {
        Enumeration enume = request.getAttributeNames();
        final HashMap map = new HashMap();
        String attrName = null;
        while (enume.hasMoreElements()) {
            attrName = enume.nextElement();
            map.put(attrName, request.getAttribute(attrName));
        }
        enume = request.getParameterNames();
        while (enume.hasMoreElements()) {
            attrName = enume.nextElement();
            map.put(attrName, request.getParameter(attrName));
        }
        return map;
    }
    
    public static void setMapInRequest(final HashMap map, final HttpServletRequest request) {
        final Set keys = map.keySet();
        for (final String key : keys) {
            request.setAttribute(key, map.get(key));
        }
    }
    
    protected static void createXML(final ByteArrayOutputStream outNode, final HttpServletResponse response) throws IOException {
        response.setContentType("text/xml");
        final PrintWriter out = response.getWriter();
        out.print(outNode.toString());
    }
    
    protected static Document createDocument() throws Exception {
        try {
            final DocumentBuilder builder = XMLUtils.getDocumentBuilderInstance(true, false);
            final Document document = builder.newDocument();
            return document;
        }
        catch (final ParserConfigurationException ee) {
            SYMClientUtil.out.log(Level.SEVERE, "ParserConfigurationException while creating the document!", ee);
            throw ee;
        }
    }
    
    protected static OutputStream createXMLObject(final SyMException ex, final String title, final int total) throws SyMException {
        try {
            final Document document = createDocument();
            final Element data = document.createElement(title);
            document.appendChild(data);
            Element parent = document.createElement("BuildNo");
            final String buildNo = SyMUtil.getProductProperty("buildnumber");
            parent.setAttribute("BuildNo", buildNo);
            data.appendChild(parent);
            if (ex != null) {
                parent = document.createElement("exception");
                final String message = ex.getMessage();
                parent.setAttribute("Message", message);
                final int symExCode = ex.getErrorCode();
                String errorCode = symExCode + "";
                if (ex instanceof NativeException) {
                    final NativeException nativeex = (NativeException)ex;
                    final int nativeExCode = nativeex.getNativeErrorCode();
                    errorCode = errorCode + "(" + nativeExCode + ")";
                }
                parent.setAttribute("ErrorCode", errorCode);
                data.appendChild(parent);
            }
            else {
                parent = document.createElement("TotalComputers");
                parent.setAttribute("TotalComputers", total + "");
                data.appendChild(parent);
            }
            return createXMLDocument(data);
        }
        catch (final Exception ee) {
            SYMClientUtil.out.log(Level.WARNING, "Exception occured while creating the xml output", ee);
            throw new SyMException(1002, "Exception occured while creating the xml output", ee);
        }
    }
    
    private static OutputStream createXMLDocument(final Element data) throws SyMException {
        ByteArrayOutputStream outNode = null;
        try {
            final DOMSource source = new DOMSource(data);
            final Transformer transformer = XMLUtils.getTransformerInstance();
            outNode = new ByteArrayOutputStream();
            final StreamResult result = new StreamResult(outNode);
            transformer.transform(source, result);
        }
        catch (final TransformerConfigurationException ee) {
            SYMClientUtil.out.log(Level.WARNING, "Exception occured while creating the xml output", ee);
            throw new SyMException(1002, "Exception occured while creating the xml output", ee);
        }
        catch (final TransformerException ee2) {
            SYMClientUtil.out.log(Level.WARNING, "Exception occured while creating the xml output", ee2);
            throw new SyMException(1002, "Exception occured while creating the xml output", ee2);
        }
        finally {
            try {
                if (outNode != null) {
                    outNode.close();
                }
            }
            catch (final IOException e) {
                SYMClientUtil.out.log(Level.WARNING, "Exception has occured while closing the streams in createXMLObject", e);
                throw new SyMException(1001, "Exception has occured while closing the streams in createXMLObject", e);
            }
        }
        return outNode;
    }
    
    public static void convertPropertiesToRequestInfo(final Properties properties, final HttpServletRequest request) {
        String attrName = "";
        if (properties != null && properties.size() > 0) {
            final Enumeration enume = properties.propertyNames();
            while (enume.hasMoreElements()) {
                attrName = enume.nextElement();
                request.setAttribute(attrName, (Object)properties.getProperty(attrName));
            }
        }
    }
    
    public static String getTimeString(final long time) {
        final Utils util = new Utils();
        String formattedDate = null;
        if (time == 0L) {
            formattedDate = "--";
        }
        else {
            formattedDate = Utils.getEventTime(time);
        }
        return formattedDate;
    }
    
    public static String getTimeString(final long time, final String format) {
        final Utils util = new Utils();
        String formattedDate = null;
        if (time == 0L) {
            formattedDate = "--";
        }
        else {
            formattedDate = Utils.getTime(time, format);
        }
        return formattedDate;
    }
    
    public static String getExportTimeString(final long time) {
        final Utils util = new Utils();
        String formattedDate = null;
        if (time == 0L) {
            formattedDate = "--";
        }
        else {
            formattedDate = Utils.getTime(time);
        }
        return formattedDate;
    }
    
    public static String getNameOfResource(final Long resourceID) {
        String nameOfResource = null;
        try {
            nameOfResource = ResourceDataProvider.getResourceName(resourceID);
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while getting the name of resourceID :" + resourceID, ex);
        }
        return nameOfResource;
    }
    
    public static Long getCurrentlyLoggedInUserID(final HttpServletRequest request) throws DataAccessException, SyMException {
        final Principal principal = request.getUserPrincipal();
        Long userID = null;
        final String loginUserName = (String)request.getAttribute("loginName");
        if (principal == null && loginUserName != null) {
            userID = DMUserHandler.getUserID(loginUserName);
        }
        else {
            userID = SyMUtil.getInstance().getLoggedInUserID();
        }
        return userID;
    }
    
    public static void renderRemarksWithKB(final TransformerContext tableContext, final HashMap columnProperties, final Object data, final boolean isError) throws Exception {
        try {
            if (data != null && !data.equals("") && !data.equals("--")) {
                String trimmedVal = columnProperties.get("TRIMMED_VALUE");
                String val = columnProperties.get("VALUE");
                if (isError) {
                    if (trimmedVal != null && !trimmedVal.equalsIgnoreCase("")) {
                        trimmedVal = "<span class=\"bodytextred\" >" + trimmedVal + "</span>";
                    }
                    else if (val != null) {
                        val = "<span class=\"bodytextred\" >" + val + "</span>";
                    }
                }
                else if (trimmedVal != null && !trimmedVal.equalsIgnoreCase("")) {
                    trimmedVal = "<span class=\"bodytext\" >" + trimmedVal + "</span>";
                }
                else if (val != null) {
                    val = "<span class=\"bodytext\" >" + val + "</span>";
                }
                final String kbURL = (String)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.KB_URL");
                Integer errorCode = (Integer)tableContext.getAssociatedPropertyValue("InstallPatchStatus.ERROR_CODE");
                if (errorCode == null) {
                    errorCode = (Integer)tableContext.getAssociatedPropertyValue("psl.ERROR_CODE");
                    if (errorCode == null || errorCode == 5007) {
                        errorCode = (Integer)tableContext.getAssociatedPropertyValue("PatchDownloadToErrCode.ERROR_CODE");
                    }
                }
                if (errorCode == null) {
                    errorCode = (Integer)tableContext.getAssociatedPropertyValue("FileDownloadToErrCode.ERROR_CODE");
                }
                if (!data.equals(I18N.getMsg("dc.patch.apd.patch_download_inprogress", new Object[0])) && ((kbURL != null && !kbURL.equals("") && !kbURL.equals("--")) || (errorCode != null && errorCode != -1))) {
                    final HttpServletRequest srvRequest = tableContext.getViewContext().getRequest();
                    final Locale locale = srvRequest.getLocale();
                    final Long userID = getCurrentlyLoggedInUserID(srvRequest);
                    final String i18n = I18NUtil.getString("dc.common.READ_KB", locale, userID);
                    String kbStr = null;
                    if (kbURL != null && !kbURL.equals("") && !kbURL.equals("--")) {
                        kbStr = "&nbsp;<a href=\"" + kbURL + "\" style=\"color: rgb(0, 0, 204);\" target=\"_blank\" >" + i18n + "</a>";
                        kbStr = UrlReplacementUtil.replaceUrlAndAppendTrackCode(kbStr);
                    }
                    if (errorCode != null && errorCode != -1) {
                        kbStr = "&nbsp;<a href=\"javascript:openReadKB(" + errorCode + ");\" style=\"color: rgb(0, 0, 204);\"  >" + i18n + "</a>";
                        kbStr = UrlReplacementUtil.replaceUrlAndAppendTrackCode(kbStr);
                    }
                    if (kbStr != null) {
                        if (trimmedVal != null && !trimmedVal.equalsIgnoreCase("")) {
                            trimmedVal += kbStr;
                        }
                        else if (val != null) {
                            val += kbStr;
                        }
                    }
                }
                if (trimmedVal != null && !trimmedVal.equalsIgnoreCase("")) {
                    columnProperties.put("TRIMMED_VALUE", trimmedVal);
                }
                else {
                    columnProperties.put("TRIMMED_VALUE", "");
                }
                if (val != null) {
                    columnProperties.put("VALUE", val);
                }
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while rendering remarks with KB :", ex);
        }
    }
    
    public static void renderRemarksWithKB(final TransformerContext tableContext, final HashMap columnProperties, final Object data, final String remarksArgs, final boolean isError) throws Exception {
        renderRemarksWithKB(tableContext, columnProperties, data, remarksArgs, isError, Boolean.TRUE);
    }
    
    public static void renderRemarksWithKB(final TransformerContext tableContext, final HashMap columnProperties, final Object data, final String remarksArgs, final boolean isError, final boolean showError) throws Exception {
        try {
            if (data != null && !data.equals("") && !data.equals("--")) {
                String val = I18NUtil.transformRemarks(data.toString(), remarksArgs);
                if (isError) {
                    val = "<span class=\"bodytextred\" >" + val + "</span>";
                }
                else {
                    val = "<span class=\"bodytext\" >" + val + "</span>";
                }
                final String kbURL = (String)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.KB_URL");
                if (kbURL != null && !kbURL.equals("") && !kbURL.equals("--") && showError) {
                    final String trackingCode = ProductUrlLoader.getInstance().getValue("trackingcode");
                    final HttpServletRequest srvRequest = tableContext.getViewContext().getRequest();
                    final Locale locale = srvRequest.getLocale();
                    final String i18n = I18N.getMsg("dc.common.READ_KB", new Object[0]);
                    String kbStr = "&nbsp;<a href=\"" + kbURL + "\" style=\"color: rgb(0, 0, 204);\" target=\"_blank\" >" + i18n + "</a>";
                    kbStr = UrlReplacementUtil.replaceUrlAndAppendTrackCode(kbStr);
                    val += kbStr;
                }
                columnProperties.put("VALUE", val);
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while rendering remarks with KB :", ex);
        }
    }
    
    public static void emberRenderRemarksWithKB(final TransformerContext tableContext, final HashMap columnProperties, final Object data, final String remarksArgs, final boolean isError, final boolean showError) throws Exception {
        try {
            if (data != null && !data.equals("") && !data.equals("--")) {
                final String val = I18NUtil.transformRemarks(data.toString(), remarksArgs);
                final JSONObject payload = new JSONObject();
                String kbURL = (String)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.KB_URL");
                if (kbURL != null && !kbURL.equals("") && !kbURL.equals("--") && showError) {
                    kbURL = UrlReplacementUtil.replaceUrlAndAppendTrackCode(kbURL);
                    payload.put("kbURL", (Object)kbURL);
                }
                payload.put("isError", isError);
                columnProperties.put("PAYLOAD", payload);
                columnProperties.put("VALUE", val);
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while rendering remarks with KB :", ex);
        }
    }
    
    public static void renderRemarksWithKB(final TransformerContext tableContext, final HashMap columnProperties, final Object data, final String remarksArgs, final boolean isError, final String renderText) {
        try {
            String val = I18NUtil.transformRemarks(data.toString(), remarksArgs);
            if (isError) {
                val = "<span class=\"bodytextred\" >" + val + "</span>";
            }
            else {
                val = "<span class=\"bodytext\" >" + val + "</span>";
            }
            final String kbURL = (String)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.KB_URL");
            if (kbURL != null && !kbURL.equals("") && !kbURL.equals("--")) {
                final String trackingCode = ProductUrlLoader.getInstance().getValue("trackingcode");
                final String i18n = I18N.getMsg(renderText, new Object[0]);
                String kbStr = "&nbsp;<a href=\"" + kbURL + trackingCode + "\" style=\"color: rgb(0, 0, 204);\" target=\"_blank\" >" + i18n + "</a>";
                kbStr = UrlReplacementUtil.replaceUrlAndAppendTrackCode(kbStr);
                val += kbStr;
            }
            columnProperties.put("VALUE", val);
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception occurred while rendering data in SYMClientUtil.java -> renderRemarksWithKB", ex);
        }
    }
    
    public static void sendTextResponseThroughAjax(final HttpServletRequest request, final HttpServletResponse response, final String resultText) {
        PrintWriter writer = null;
        try {
            response.setContentType("text/plain");
            writer = response.getWriter();
            writer.println(resultText);
            writer.close();
        }
        catch (final Exception e) {
            SYMClientUtil.out.log(Level.WARNING, " sendTextResponseThroughAjax() : Exception : ", e);
            writer.println("false");
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception e2) {
                SYMClientUtil.out.log(Level.WARNING, " sendTextResponseThroughAjax() : Exception : ", e2);
            }
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception e3) {
                SYMClientUtil.out.log(Level.WARNING, " sendTextResponseThroughAjax() : Exception : ", e3);
            }
        }
    }
    
    public static Object getStateValue(final ViewContext viewCtx, final String key) {
        Object value = viewCtx.getRequest().getParameter(key);
        if (value == null) {
            value = viewCtx.getRequest().getAttribute(key);
        }
        if (value == null) {
            value = viewCtx.getStateParameter(key);
        }
        if (value != null) {
            viewCtx.setStateParameter(key, value);
            viewCtx.getRequest().setAttribute(key, value);
        }
        return value;
    }
    
    public static void setAttribute(final ViewContext viewCtx, final String key, final Object value) {
        viewCtx.getRequest().setAttribute(key, value);
    }
    
    public static void getCopyRihtProps(final HttpSession session) {
        try {
            final Properties copyrightProps = SyMUtil.getCopyrightProps();
            copyrightProps.setProperty("company_url", I18N.getMsg(copyrightProps.getProperty("company_url"), new Object[0]));
            session.setAttribute("COPYRIGHT_PROPS", (Object)copyrightProps);
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception in getCopyRihtProps :", ex);
        }
    }
    
    public static void setCopyRightProps(final HttpServletRequest request) {
        try {
            final Properties copyrightProps = SyMUtil.getCopyrightProps();
            copyrightProps.setProperty("company_url", I18N.getMsg(copyrightProps.getProperty("company_url"), new Object[0]));
            WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "COPYRIGHT_PROPS", copyrightProps);
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception in setCopyRightProps : ", ex);
        }
    }
    
    public static String convertBytesToGBorMB(final long bytes) {
        String result = null;
        final long bytesGBRounded = bytes / 1073741824L;
        if (bytesGBRounded > 0L) {
            final long bytesGBRemainder = bytes % 1073741824L;
            String bytesGBRemainderStr = String.valueOf(bytesGBRemainder);
            if (bytesGBRemainderStr.length() > 2) {
                bytesGBRemainderStr = bytesGBRemainderStr.substring(0, 2);
            }
            result = "" + bytesGBRounded + "." + bytesGBRemainderStr + " GB";
        }
        else {
            final long bytesMBRounded = bytes / 1048576L;
            result = "" + bytesMBRounded + " MB";
        }
        return result;
    }
    
    public static boolean isUserInAdminRole(final HttpServletRequest request) {
        return request.isUserInRole("Common_Write");
    }
    
    public static boolean isUserInRole(final HttpServletRequest request, final String roleName) {
        final Long loginID = getLoginId(request);
        return DMUserHandler.isUserInRole(loginID, roleName);
    }
    
    public static String getCurrentlyLoggedInUserName(final HttpServletRequest request) throws DataAccessException, SyMException, Exception {
        final Principal principal = request.getUserPrincipal();
        String loginUserName = null;
        if (principal != null) {
            loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        }
        else {
            loginUserName = (String)request.getAttribute("loginName");
            if (loginUserName == null) {
                loginUserName = EventConstant.DC_SYSTEM_USER;
            }
        }
        return loginUserName;
    }
    
    public static HashMap getQuickLinks(final Locale locale) {
        final HashMap returnHash = new HashMap();
        try {
            final int NO_OF_INT_LINK = 5;
            final int NO_OF_EXT_LINK = 2;
            final ArrayList displayInt = new ArrayList(NO_OF_INT_LINK);
            final ArrayList linkInt = new ArrayList(NO_OF_INT_LINK);
            final ArrayList displayExt = new ArrayList(NO_OF_EXT_LINK);
            final ArrayList linkExt = new ArrayList(NO_OF_EXT_LINK);
            final ArrayList internal = new ArrayList();
            final ArrayList external = new ArrayList();
            if (!CustomerInfoUtil.isSAS) {
                displayInt.add("desktopcentral.webclient.common.contactus.create_support_file");
                String supportUrl = "javascript:openOtherWindow('/webclient#/uems/support/create')";
                if (CustomerInfoUtil.isMDM() || CustomerInfoUtil.isMDMP()) {
                    supportUrl = "javascript:openOtherWindow('/supportPage.do?actionToCall=supportFile&selectedTreeElem=supportFileLayout')";
                }
                linkInt.add(supportUrl);
                displayInt.add("desktopcentral.webclient.common.contactus.send_log_files");
                final String supportMailId = I18N.getMsg(ProductUrlLoader.getInstance().getValue("supportmailid"), new Object[0]);
                linkInt.add("javascript:openOtherWindow('https://bonitas.zohocorp.com/')");
            }
            displayInt.add("desktopcentral.webclient.common.contactus.send_feedback");
            if (!CustomerInfoUtil.isSAS) {
                linkInt.add("javascript:supportRequest();");
                displayInt.add("desktopcentral.webclient.common.contactus.send_testimonials");
                linkInt.add("javascript:testimonial();");
            }
            else {
                linkInt.add("javascript:sendFeedbackRequest();");
            }
            if (!CustomerInfoUtil.isSAS) {
                displayInt.add("desktopcentral.webclient.common.contactus.visit_user_community");
                final String forumsUrl = I18N.getMsg(ProductUrlLoader.getInstance().getValue("forums_url"), new Object[0]);
                linkInt.add("javascript:openOtherWindow('" + forumsUrl + "')");
            }
            else {
                displayExt.add("desktopcentral.webclient.common.contactus.visit_user_community");
                final String forumsUrl = I18N.getMsg(ProductUrlLoader.getInstance().getValue("forums_url"), new Object[0]);
                linkExt.add("javascript:openOtherWindow('" + forumsUrl + "')");
            }
            displayExt.add("desktopcentral.webclient.common.contactus.join_web_conference");
            linkExt.add("javascript:openOtherWindow('http://meeting.zoho.com/home.do')");
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (!isMSP) {
                CustomerInfoUtil.getInstance();
                if (!CustomerInfoUtil.isSAS()) {
                    displayExt.add("desktopcentral.webclient.common.contactus.get_price_quote");
                    linkExt.add("javascript:opengetpricequotePage();");
                }
            }
            for (int i = 0; i < displayInt.size(); ++i) {
                final HashMap interHs = new HashMap();
                interHs.put("display", displayInt.get(i));
                interHs.put("link", linkInt.get(i));
                internal.add(interHs);
            }
            for (int i = 0; i < displayExt.size(); ++i) {
                final HashMap extHs = new HashMap();
                extHs.put("display", displayExt.get(i));
                extHs.put("link", linkExt.get(i));
                external.add(extHs);
            }
            returnHash.put("intLink", internal);
            returnHash.put("extLink", external);
        }
        catch (final Exception e) {
            SYMClientUtil.out.log(Level.WARNING, "Exception while getting list of external and internal links..... ", e);
        }
        return returnHash;
    }
    
    public static Long getLoginId(final HttpServletRequest request) {
        Long loginID = null;
        try {
            String selectedTab = (String)getInstance().getValueFromSessionOrRequest(request, "selectedTab");
            if (selectedTab == null && request.getParameter("selectedTab") != null) {
                selectedTab = request.getParameter("selectedTab");
            }
            else if (request != null && request.getAttribute("isAPI") != null && request.getAttribute("isAPI").equals("true")) {
                return (Long)request.getAttribute("loginid");
            }
            if ((selectedTab != null && selectedTab.equalsIgnoreCase("Tools")) || (selectedTab != null && selectedTab.equalsIgnoreCase("Configurations"))) {
                String isPMTools = null;
                if (request.getAttribute("isPMTools") != null) {
                    isPMTools = (String)request.getAttribute("isPMTools");
                }
                else if (request.getParameter("isPMTools") != null) {
                    isPMTools = request.getParameter("isPMTools");
                }
                if ("true".equalsIgnoreCase(isPMTools)) {
                    selectedTab = "PatchMgmt";
                }
            }
            loginID = DMUserHandler.getLoginId(selectedTab);
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return loginID;
    }
    
    public static HttpServletRequest setDomainValFailedAttributes(final HttpServletRequest request, final String type) {
        final String sourceMethod = "setDomainValFailedAttributes";
        try {
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType != 3) {
                if (type.equals("patch")) {
                    if (SyMUtil.getSyMParameter("patch_domain_validation") != null) {
                        final String patchDomainStatus = SyMUtil.getSyMParameter("patch_domain_validation");
                        if (patchDomainStatus.equals("failed")) {
                            request.setAttribute("patch_domain_validation", (Object)patchDomainStatus);
                            request.setAttribute("msgTittle", (Object)I18N.getMsg("dm.proxy.admin.domain_failed_message_box_header", new Object[0]));
                            request.setAttribute("msgContent", (Object)I18N.getMsg("desktopcentral.proxySettings.admin.patch_msg_desc", new Object[0]));
                        }
                    }
                }
                else if (type.equals("inv")) {
                    if (SyMUtil.getSyMParameter("inv_domain_validation") != null) {
                        final String invDomainStatus = SyMUtil.getSyMParameter("inv_domain_validation");
                        if (invDomainStatus.equals("failed")) {
                            request.setAttribute("inv_domain_validation", (Object)SyMUtil.getSyMParameter("inv_domain_validation"));
                            request.setAttribute("msgTittle", (Object)I18N.getMsg("dm.proxy.admin.domain_failed_message_box_header", new Object[0]));
                            request.setAttribute("msgContent", (Object)I18N.getMsg("desktopcentral.proxySettings.admin.inv_msg_desc", new Object[0]));
                        }
                    }
                }
                else if (type.equals("mdm") && SyMUtil.getSyMParameter("mdm_domain_validation") != null) {
                    final String mdmDomainStatus = SyMUtil.getSyMParameter("mdm_domain_validation");
                    if (mdmDomainStatus.equals("failed")) {
                        request.setAttribute("mdm_domain_validation", (Object)SyMUtil.getSyMParameter("mdm_domain_validation"));
                        request.setAttribute("msgTittle", (Object)I18N.getMsg("dm.proxy.admin.domain_failed_message_box_header", new Object[0]));
                        request.setAttribute("msgContent", (Object)I18N.getMsg("desktopcentral.proxySettings.admin.mdm_msg_desc", new Object[0]));
                    }
                }
            }
            else {
                SYMClientUtil.out.log(Level.INFO, sourceMethod + " No Internet connection configured");
            }
        }
        catch (final Exception exp) {
            SYMClientUtil.out.log(Level.WARNING, sourceMethod + "--> Exception while adding customer", exp);
        }
        return request;
    }
    
    public static boolean writeJsonFormattedResponse(final HttpServletResponse response) {
        try {
            response.setContentType("application/json; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=test.txt;");
            response.setHeader("X-Download-Options", "noopen");
        }
        catch (final Exception exp) {
            return false;
        }
        return true;
    }
    
    public static void checkAndUpdateCertificateExpiry(final HttpServletRequest request) {
        String sslPageUrl = "/webclient#/uems/admin/import-ssl-certificates";
        if (CustomerInfoUtil.isMDMP()) {
            sslPageUrl = "/ImportCertificates.do?actionToCall=showUploadForm";
        }
        final int mobDeviceCount = getMobileDeviceCount();
        SYMClientUtil.out.log(Level.INFO, "CERTIFICATE expiry check!!!");
        X509Certificate x509Certificate = null;
        final Boolean isAdminUser = request.isUserInRole("Common_Write");
        try {
            if (SSLCertificateUtil.getInstance().isThirdPartySSLInstalled()) {
                x509Certificate = CertificateUtils.loadX509CertificateFromFile(new File(SSLCertificateUtil.getInstance().getServerCertificateFilePath()));
                Date expiryDate = x509Certificate.getNotAfter();
                Date todaysDate = new Date();
                SYMClientUtil.out.log(Level.INFO, "creation date!!!" + x509Certificate.getNotBefore());
                SYMClientUtil.out.log(Level.INFO, "expiry date!!!" + expiryDate);
                final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                expiryDate = sdf.parse(sdf.format(expiryDate));
                todaysDate = sdf.parse(sdf.format(todaysDate));
                final Long expiryAlertTime = todaysDate.getTime() + 2592000000L;
                Long noOfDays = 0L;
                String noOfDaysRemaining = "";
                String msg = "";
                if (expiryDate != null && expiryDate.getTime() <= expiryAlertTime) {
                    final Long userID = getCurrentlyLoggedInUserID(request);
                    String isClose = SyMUtil.getUserParameter(userID, "CERTIFICATE_ALERT_CLOSE");
                    request.setAttribute("isExpired", (Object)Boolean.FALSE);
                    noOfDays = expiryDate.getTime() - todaysDate.getTime();
                    noOfDays /= 86400000L;
                    if (noOfDays <= 1L) {
                        request.setAttribute("isExpired", (Object)Boolean.TRUE);
                        isClose = null;
                    }
                    if (isClose == null || !isClose.equalsIgnoreCase("close")) {
                        request.setAttribute("CERTIFICATE_EXPIRY_ALERT", (Object)I18N.getMsg("dc.ssl.server.certificate.expiry.alert", new Object[0]));
                        noOfDaysRemaining = ((noOfDays <= 1L) ? ((noOfDays == 0L) ? "today" : "in 1 day") : ("in " + noOfDays + " days"));
                        if (mobDeviceCount > 0) {
                            msg = ((isAdminUser == Boolean.TRUE) ? I18N.getMsg("dc.ssl.server.certificate.expiry.MdmUsagemsg", new Object[] { noOfDaysRemaining, " <a href=\"" + sslPageUrl + "\" target=\"_blank\" >Import Certificate</a>" }) : I18N.getMsg("dc.ssl.server.certificate.expiry.MdmUsagemsg", new Object[] { noOfDaysRemaining, "Please contact your administrator." }));
                            request.setAttribute("CERTIFICATE_EXPIRY_ALERT_MSG", (Object)msg);
                        }
                        else {
                            msg = ((isAdminUser == Boolean.TRUE) ? I18N.getMsg("dc.ssl.server.certificate.expiry.msg", new Object[] { noOfDaysRemaining, " <a href=\"" + sslPageUrl + "\" target=\"_blank\" >Import Certificate</a>" }) : I18N.getMsg("dc.ssl.server.certificate.expiry.msg", new Object[] { noOfDaysRemaining, "Please contact your administrator." }));
                            request.setAttribute("CERTIFICATE_EXPIRY_ALERT_MSG", (Object)msg);
                        }
                    }
                    else {
                        request.setAttribute("CERTIFICATE_EXPIRY_ALERT", (Object)"");
                    }
                }
            }
        }
        catch (final CertificateExpiredException ex) {
            try {
                request.setAttribute("CERTIFICATE_EXPIRY_ALERT", (Object)I18N.getMsg("dc.ssl.server.certificate.expired.alert", new Object[0]));
                String msg2 = "";
                if (mobDeviceCount > 0) {
                    msg2 = ((isAdminUser == Boolean.TRUE) ? I18N.getMsg("dc.ssl.server.certificate.expired.MdmUsagemsg", new Object[] { " <a href=\"" + sslPageUrl + "\" target=\"_blank\" >Import Certificate</a>" }) : I18N.getMsg("dc.ssl.server.certificate.expired.MdmUsagemsg", new Object[] { "Please contact your administrator." }));
                    request.setAttribute("CERTIFICATE_EXPIRY_ALERT_MSG", (Object)msg2);
                }
                else {
                    msg2 = ((isAdminUser == Boolean.TRUE) ? I18N.getMsg("dc.ssl.server.certificate.expired.msg", new Object[] { " <a href=\"" + sslPageUrl + "\" target=\"_blank\" >Import Certificate</a>" }) : I18N.getMsg("dc.ssl.server.certificate.expired.msg", new Object[] { "Please contact your administrator." }));
                    request.setAttribute("CERTIFICATE_EXPIRY_ALERT_MSG", (Object)msg2);
                }
                request.setAttribute("isExpired", (Object)Boolean.TRUE);
            }
            catch (final Exception e) {
                SYMClientUtil.out.log(Level.INFO, "Exception in getting I18N properties" + e);
            }
        }
        catch (final Exception e2) {
            SYMClientUtil.out.log(Level.INFO, "Exception in checking certificate expiry" + e2);
        }
    }
    
    public static int getMobileDeviceCount() {
        int count = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        final String query = "select count(1) from ManagedDevice";
        final RelationalAPI relapi = RelationalAPI.getInstance();
        try {
            conn = relapi.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs != null & rs.next()) {
                count = rs.getInt(1);
            }
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception occurred while finding Recent modified file details from DB ", ex);
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception ex) {
                SYMClientUtil.out.log(Level.WARNING, "Exception occurred while closing connections in SYMClientUtil ", ex);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception ex2) {
                SYMClientUtil.out.log(Level.WARNING, "Exception occurred while closing connections in SYMClientUtil ", ex2);
            }
        }
        SYMClientUtil.out.log(Level.INFO, "DEVICE COUNT" + count);
        return count;
    }
    
    public Reader getProperEncodedReader(final HttpServletRequest request, Reader reader) throws IOException {
        try {
            reader = new BufferedReader(new InputStreamReader((InputStream)request.getInputStream(), Charset.forName("UTF-8")));
            return reader;
        }
        catch (final IOException ex) {
            SYMClientUtil.out.log(Level.WARNING, "Exception occurred while converting the HTTPServlet Request's InputStream to Reader in getProperEncodedReader() of SYMClientUtil ... ", ex);
            throw ex;
        }
    }
    
    public static List getRestrictedHostNames() {
        final List restrictedHostNames = new ArrayList();
        Properties webServerSettingsProps = null;
        try {
            webServerSettingsProps = ApiFactoryProvider.getUtilAccessAPI().getWebServerSettings();
        }
        catch (final Exception ex) {
            SYMClientUtil.out.log(Level.WARNING, "Unable to retrive WebServerSettings : {0}", ex);
        }
        if (webServerSettingsProps != null) {
            String uiAccessRestrictedHostNames = webServerSettingsProps.getProperty("ui.access.restricted.hostnames");
            if (uiAccessRestrictedHostNames != null && uiAccessRestrictedHostNames.trim().length() > 0) {
                uiAccessRestrictedHostNames = uiAccessRestrictedHostNames.replaceAll(" ", "");
                uiAccessRestrictedHostNames = uiAccessRestrictedHostNames.toLowerCase();
                final String[] uiAccessRestrictedHostNamesArr = uiAccessRestrictedHostNames.split(",");
                restrictedHostNames.addAll(Arrays.asList(uiAccessRestrictedHostNamesArr));
            }
        }
        return restrictedHostNames;
    }
    
    public static void returnRequestFromRestrictedHostName(final List restrictedHostNames, final ServletRequest request, final ServletResponse response) throws IOException {
        final HttpServletRequest servletRequest = (HttpServletRequest)request;
        final String hostNameUsedForUIAccess = servletRequest.getServerName().toLowerCase();
        if (!restrictedHostNames.isEmpty() && restrictedHostNames.contains(hostNameUsedForUIAccess)) {
            String errorMessage = null;
            try {
                errorMessage = I18N.getMsg("desktopcentral.webclient.filter.UIRestrictionFilter.Permission_Deny_Msg", new Object[0]);
            }
            catch (final Exception ex) {
                SYMClientUtil.out.log(Level.WARNING, "Unable to get I18N message : {0}", ex);
            }
            String ipAddress = servletRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = servletRequest.getRemoteAddr();
            }
            SYMClientUtil.out.log(Level.INFO, "UIRestrictionFilter : Going to reject the request from {0}", ipAddress);
            ((HttpServletResponse)response).sendError(403, errorMessage);
        }
    }
    
    public Object getValueFromSessionOrRequest(final HttpServletRequest request, final String key) {
        Object value = null;
        try {
            value = WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, key);
            if (value == null) {
                value = request.getAttribute(key);
            }
        }
        catch (final Exception e) {
            SYMClientUtil.out.log(Level.SEVERE, "Exception occurred : ", e);
        }
        return value;
    }
    
    public static boolean setTrimStatusSetting(final Object newTrimStatusSetting) {
        boolean isTrimStatusUpdated = false;
        final Boolean columntrimStatus = ReportCriteriaUtil.getTrimStatus();
        if (columntrimStatus == null || columntrimStatus != (boolean)newTrimStatusSetting) {
            ReportCriteriaUtil.updateViewGlobalSettings((Boolean)newTrimStatusSetting);
            isTrimStatusUpdated = true;
        }
        return isTrimStatusUpdated;
    }
    
    public static String getSelectedTheme(final HttpServletRequest request) {
        String theme = "";
        theme = (String)getInstance().getValueFromSessionOrRequest(request, "selectedskin");
        if (theme == null) {
            theme = SyMUtil.getInstance().getTheme();
        }
        if (request.getParameterMap().containsKey("scheduleReportTheme")) {
            theme = request.getParameter("scheduleReportTheme");
        }
        return theme;
    }
    
    static {
        SYMClientUtil.className = SYMClientUtil.class.getName();
        SYMClientUtil.out = Logger.getLogger(SYMClientUtil.className);
        SYMClientUtil.symclient = null;
    }
}
