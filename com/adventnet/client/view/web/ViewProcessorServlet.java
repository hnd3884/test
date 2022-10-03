package com.adventnet.client.view.web;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.adventnet.client.properties.ClientProperties;
import com.adventnet.client.themes.web.ThemesAPI;
import com.adventnet.client.ClientException;
import com.adventnet.client.ClientErrorCodes;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaModel;
import com.adventnet.i18n.I18N;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import java.util.Iterator;
import com.adventnet.client.tpl.service.TemplateService;
import com.adventnet.mfw.service.ServiceStarter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.adventnet.client.box.web.BoxAPI;
import com.adventnet.client.dialog.web.DialogAPI;
import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class ViewProcessorServlet extends HttpServlet implements WebConstants, JavaScriptConstants
{
    private static final long serialVersionUID = 1L;
    static String httpjsSnippet;
    static String httpsjsSnippet;
    public static final Logger LOGGER;
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final boolean renderingPhaseStart = request.getAttribute("ROOT_VIEW_CTX") == null;
        final boolean subRequest = WebViewAPI.isAjaxRequest(request);
        String origView = null;
        try {
            origView = WebClientUtil.getRequestedPathName(request);
            final ViewContext vc = this.getViewCtx(request, response, origView);
            if (renderingPhaseStart) {
                this.initializeRenderingPhase(vc, request, response, subRequest);
            }
            try {
                request.setAttribute("VIEW_CTX", (Object)vc);
                this.includeView(vc, response);
            }
            finally {
                request.setAttribute("VIEW_CTX", (Object)vc.getParentContext());
            }
            if (renderingPhaseStart) {
                this.endRenderingPhase(vc, request, response, subRequest);
            }
        }
        catch (final Exception ex) {
            throw new ServletException((Throwable)ex);
        }
        finally {
            if (renderingPhaseStart) {
                StateAPI.clearStateForThread();
            }
        }
    }
    
    public void includeView(final ViewContext vc, final HttpServletResponse response) throws Exception {
        final HttpServletRequest request = vc.getRequest();
        final String params = WebViewAPI.getParamsForView(vc.getModel().getViewConfiguration(), request);
        vc.setStateOrURLStateParam("_D_RP", params, true);
        response.getWriter().println(DialogAPI.getDialogPrefix(vc, response));
        response.getWriter().println(StateParserGenerator.generateViewPrefix(vc));
        vc.getViewModel(false);
        vc.getModel().getController().updateAssociatedTiledViews(vc);
        response.getWriter().print(BoxAPI.getBoxPrefix(vc, response));
        String compUrl = vc.getModel().getForwardURL();
        compUrl = compUrl + "&UNIQUE_ID=" + vc.getUniqueId();
        compUrl = vc.getModel().getController().processPreRendering(vc, request, response, compUrl);
        if (compUrl != null) {
            final RequestDispatcher rd = request.getRequestDispatcher(compUrl);
            rd.include((ServletRequest)request, (ServletResponse)response);
            vc.getModel().getController().processPostRendering(vc, request, response);
        }
        response.getWriter().print(BoxAPI.getBoxSuffix(vc, response));
        response.getWriter().println(StateParserGenerator.generateViewSuffix(vc));
        response.getWriter().println(DialogAPI.getDialogSuffix(vc));
    }
    
    public void initializeRenderingPhase(final ViewContext rootViewCtx, final HttpServletRequest request, final HttpServletResponse response, final boolean subRequest) throws Exception {
        final long start = System.currentTimeMillis();
        if (request.getAttribute("TIME_TO_LOAD_START_TIME") == null) {
            request.setAttribute("TIME_TO_LOAD_START_TIME", (Object)new Long(start));
        }
        WebViewAPI.setContentType(request, response);
        request.setAttribute("ROOT_VIEW_CTX", (Object)rootViewCtx);
        this.generatePrefixHtmlCode(rootViewCtx, request, response, subRequest);
        if (System.getProperty("developmentMode") != null && System.getProperty("developmentMode").equals("true")) {
            for (final String key : ServiceStarter.serviceMap.keySet()) {
                final Object value = ServiceStarter.serviceMap.get(key);
                if (value instanceof TemplateService) {
                    ((TemplateService)value).updateCache();
                }
            }
        }
        if (!subRequest || request.getParameter("RenderAsMainView") == null) {
            response.getWriter().println(StateParserGenerator.generateRootDetails(rootViewCtx, request));
        }
    }
    
    public void endRenderingPhase(final ViewContext rootViewCtx, final HttpServletRequest request, final HttpServletResponse response, final boolean subRequest) throws Exception {
        final String parentDCA = (String)rootViewCtx.getStateParameter("PDCA");
        if ("GLOBAL_DCA".equals(parentDCA)) {
            rootViewCtx.setStateParameter("PDCA", parentDCA);
            final DynamicContentAreaModel dca = DynamicContentAreaAPI.getDynamicContentAreaModel(request, parentDCA);
            response.getWriter().println(DynamicContentAreaAPI.generateOldStateForViews(dca, request));
            response.getWriter().println(DynamicContentAreaAPI.genContentAreaStateScript(request, dca));
        }
        response.getWriter().println(StateParserGenerator.generateSessionStateJS());
        if (subRequest) {
            final String origId = WebViewAPI.getOriginalRootViewId(request, false);
            if (origId != null) {
                final ViewContext origRoot = ViewContext.getViewContext(origId, request);
                final StringBuffer strBuf = new StringBuffer();
                StateParserGenerator.generateStateVariables(origRoot, strBuf);
                response.getWriter().println(strBuf.toString());
            }
        }
        final long start = (long)request.getAttribute("TIME_TO_LOAD_START_TIME");
        final long end = System.currentTimeMillis();
        final long timeTaken = end - start;
        request.setAttribute("TIME_TO_LOAD", (Object)new Long(timeTaken));
        final StringBuffer buffer = new StringBuffer("\n<script>updateTimeToLoad(" + timeTaken + ",window)</script>");
        this.setWindowTitle(rootViewCtx, request, response, subRequest, buffer);
        if (I18N.getI18N() == 1) {
            buffer.append("<script>localize();</script>");
        }
        if (!subRequest) {
            buffer.append("\n</body>\n</html>");
        }
        buffer.append("\n<script defer>execOnLoadScripts(window);</script>");
        response.getWriter().println(buffer.toString());
    }
    
    public void setWindowTitle(final ViewContext rootViewCtx, final HttpServletRequest request, final HttpServletResponse response, final boolean subRequest, final StringBuffer buffer) {
        String title = (String)request.getAttribute("WINDOWTITLE");
        if (title == null && !subRequest) {
            title = rootViewCtx.getTitle();
        }
        if (title != null) {
            buffer.append("<script>document.title='" + IAMEncoder.encodeJavaScript(title) + "';</script>");
        }
    }
    
    protected ViewContext getViewCtx(final HttpServletRequest request, final HttpServletResponse response, final String origView) throws Exception {
        String uniqueId = request.getParameter("UNIQUE_ID");
        final ViewContext parentCtx = (ViewContext)request.getAttribute("VIEW_CTX");
        if (parentCtx != null && parentCtx.getUniqueId().equals(uniqueId)) {
            uniqueId = null;
        }
        if (uniqueId == null) {
            uniqueId = origView;
        }
        final ViewContext vc = ViewContext.getViewContext(uniqueId, origView, request);
        if (parentCtx != null) {
            for (ViewContext curCtx = parentCtx; curCtx != null; curCtx = curCtx.getParentContext()) {
                if (curCtx.getModel().getViewName().equals(vc.getModel().getViewName())) {
                    final ClientException ce = new ClientException(ClientErrorCodes.RECURSIVE_LAYOUT);
                    ce.setErrorProperty("VIEWCONTEXT", vc);
                    ce.setErrorProperty("PARENTCONTEXT", vc);
                    throw ce;
                }
            }
            vc.setParentContext(parentCtx);
            parentCtx.addChildViewContext(vc);
        }
        return vc;
    }
    
    public void generatePrefixHtmlCode(final ViewContext rootViewCtx, final HttpServletRequest request, final HttpServletResponse response, final boolean subRequest) throws Exception {
        if (subRequest) {
            request.setAttribute("SUBREQUEST", (Object)"true");
        }
        else {
            request.setAttribute("SUBREQUEST", (Object)"false");
        }
        if (System.getProperty("use.compression") == null) {
            request.setAttribute("ROOT_VIEW_CTX", (Object)rootViewCtx);
        }
        else {
            final String cssSnippet = ThemesAPI.getStyleSnippet(request);
            request.setAttribute("cssSnippet", (Object)cssSnippet);
            final String jssnip = this.getJsSnippet(request);
            request.setAttribute("jsSnippet", (Object)jssnip);
        }
        final RequestDispatcher rd = request.getRequestDispatcher("/framework/jsp/PrefixHtmlCode.jsp");
        rd.include((ServletRequest)request, (ServletResponse)response);
    }
    
    public String getJsSnippet(final HttpServletRequest request) throws Exception {
        String scheme = request.getScheme();
        if ("http".equals(scheme) && request.getServerPort() == 443) {
            scheme = "https";
        }
        String jsSnippet = null;
        if ("http".equals(scheme)) {
            jsSnippet = ViewProcessorServlet.httpjsSnippet;
        }
        else {
            jsSnippet = ViewProcessorServlet.httpsjsSnippet;
        }
        if (jsSnippet != null) {
            return jsSnippet;
        }
        final String jsListFiles = System.getProperty("numberofjslistfiles");
        int files = 0;
        if (jsListFiles != null) {
            files = Integer.parseInt(jsListFiles);
        }
        final StringBuilder snippetBuf = new StringBuilder();
        if (ClientProperties.useCompression) {
            if (jsListFiles != null) {
                for (int i = 0; i < files; ++i) {
                    snippetBuf.append("<script src='");
                    if (ClientProperties.useApache) {
                        snippetBuf.append(IAMEncoder.encodeJavaScript(scheme) + "://" + ClientProperties.jsHost);
                    }
                    snippetBuf.append("/" + ClientProperties.jsVersion).append("client").append(i).append(".js' type='text/javascript'></script>\n");
                }
            }
            else {
                snippetBuf.append("<script src='");
                if (ClientProperties.useApache) {
                    snippetBuf.append(IAMEncoder.encodeJavaScript(scheme) + "://" + ClientProperties.jsHost);
                }
                snippetBuf.append(request.getContextPath() + "/" + ClientProperties.jsVersion).append("client.js' type='text/javascript'></script>\n");
            }
        }
        else {
            for (int i = 0; i < files; ++i) {
                final String jsFileList = this.getServletContext().getRealPath("/fileslist/jslist" + i + ".txt");
                try (final BufferedReader bf = new BufferedReader(new FileReader(jsFileList))) {
                    String input = null;
                    while ((input = bf.readLine()) != null) {
                        if (!input.trim().startsWith("#")) {
                            snippetBuf.append("<script src='/").append(IAMEncoder.encodeJavaScript(input)).append("' type='text/javascript'></script>\n");
                        }
                    }
                }
            }
        }
        jsSnippet = snippetBuf.toString();
        if ("http".equals(scheme)) {
            ViewProcessorServlet.httpjsSnippet = jsSnippet;
        }
        else {
            ViewProcessorServlet.httpsjsSnippet = jsSnippet;
        }
        return jsSnippet;
    }
    
    static {
        ViewProcessorServlet.httpjsSnippet = null;
        ViewProcessorServlet.httpsjsSnippet = null;
        LOGGER = Logger.getLogger(ViewProcessorServlet.class.getName());
    }
}
