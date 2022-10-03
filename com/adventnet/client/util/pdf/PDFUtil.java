package com.adventnet.client.util.pdf;

import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.view.web.ViewController;
import com.adventnet.client.view.web.WebViewModel;
import com.adventnet.client.view.pdf.DefaultPDFTheme;
import com.lowagie.text.DocumentException;
import com.adventnet.client.view.common.ExportUtils;
import javax.transaction.TransactionManager;
import com.adventnet.persistence.PersistenceException;
import java.util.logging.Level;
import com.adventnet.client.view.common.MultiViewPDFRenderer;
import com.lowagie.text.PageSize;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import java.io.File;
import com.adventnet.client.themes.web.ThemesAPI;
import com.adventnet.client.view.web.HttpReqWrapper;
import java.io.OutputStream;
import com.adventnet.client.ClientException;
import com.adventnet.client.ClientErrorCodes;
import com.adventnet.persistence.DataObject;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.view.common.ExportAuditModel;
import com.adventnet.i18n.I18N;
import com.adventnet.client.view.pdf.PDFView;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.db.api.RelationalAPI;
import com.lowagie.text.Element;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.ServletContext;
import java.util.logging.Logger;

public class PDFUtil
{
    private static Logger out;
    
    public static Object includeView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        return includeView(sc, vc, parent, doc, pdfWriter, theme, boxType, false);
    }
    
    public static Element includeView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType, final boolean writeInDocIfSupported) throws Exception {
        final HttpServletRequest request = vc.getRequest();
        Connection connection = null;
        PDFView view = null;
        final long start = System.currentTimeMillis();
        try {
            request.setAttribute("VIEW_CTX", (Object)vc);
            connection = RelationalAPI.getInstance().getConnection();
            vc.setTransientState("CONNECTION", connection);
            final String params = WebViewAPI.getParamsForView(vc.getModel().getViewConfiguration(), request);
            vc.setStateOrURLStateParam("_D_RP", params, true);
            vc.getViewModel(true);
            final DataObject viewConfig = vc.getModel().getViewConfiguration();
            final DataObject uiCompConfig = vc.getModel().getUIComponentConfig();
            String viewClass = null;
            if (viewConfig.containsTable("PdfViewConfig")) {
                viewClass = (String)viewConfig.getFirstValue("PdfViewConfig", "VIEWCLASS");
            }
            if (viewClass == null && uiCompConfig != null) {
                viewClass = (String)uiCompConfig.getFirstValue("PdfUIComponent", "VIEWCLASS");
            }
            if (viewClass == null) {
                return null;
            }
            view = (PDFView)Thread.currentThread().getContextClassLoader().loadClass(viewClass).newInstance();
            final String userLanguage = I18N.getLocale().getLanguage();
            if ("iw".equals(userLanguage) || "ar".equals(userLanguage)) {
                pdfWriter.setRunDirection(3);
            }
            Element pdfObject = null;
            if (writeInDocIfSupported && view.canAddInParent(sc, vc, parent, doc, pdfWriter, theme)) {
                view.addInView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            }
            else {
                pdfObject = view.getView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            }
            final ExportAuditModel exportInfo = new ExportAuditModel();
            exportInfo.setStartTime(start);
            exportInfo.setAccountID(WebClientUtil.getAccountId());
            exportInfo.setViewName(vc.getModel().getViewNameNo());
            exportInfo.setViewContext(vc);
            exportInfo.setExportedTime(System.currentTimeMillis());
            view.auditExport(exportInfo);
            return pdfObject;
        }
        finally {
            request.setAttribute("VIEW_CTX", (Object)vc.getParentContext());
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static boolean isRightToLeft(final PdfWriter pdfWriter) {
        return pdfWriter.getRunDirection() == 3;
    }
    
    public static ViewContext getViewCtx(final HttpServletRequest request, final Object origView) throws Exception {
        final String uniqueId = request.getParameter("UNIQUE_ID");
        return getViewCtx(request, origView, uniqueId);
    }
    
    public static ViewContext getViewCtx(final HttpServletRequest request, final Object origView, Object uniqueId) throws Exception {
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
        vc.setRenderType(3);
        return vc;
    }
    
    public static void generatePDF(final String viewName, HttpServletRequest request, final OutputStream os) throws Exception {
        if (request == null) {
            request = (HttpServletRequest)new HttpReqWrapper('/' + System.getProperty("contextDIR"));
        }
        final ServletContext sc = WebClientUtil.getServletContext(request.getContextPath());
        generatePDF(viewName, sc, request, os);
    }
    
    public static String getRealPathOfImage(final ServletContext sc, final String image, final HttpServletRequest request) throws Exception {
        final Row themeRow = ThemesAPI.getThemeForAccount(WebClientUtil.getAccountId());
        final String themeDir = (String)themeRow.get("THEME_DIR");
        if (image == null || image.isEmpty()) {
            return null;
        }
        String path = null;
        if (image.charAt(0) == '/') {
            if (System.getProperty("contextDIR") != null) {
                path = System.getProperty("server.dir") + "/webapps/" + System.getProperty("contextDIR") + image;
            }
            else {
                path = sc.getRealPath(image.substring(1));
            }
        }
        else {
            path = sc.getRealPath(themeDir + "/" + image);
        }
        if (!new File(path).exists()) {
            path = sc.getRealPath("/themes/common/images/broken.png");
        }
        return path;
    }
    
    public static void generatePDF(final String viewName, final ServletContext sc, final HttpServletRequest request, final OutputStream os) throws Exception {
        TransactionManager txnMgr = null;
        try {
            txnMgr = DataAccess.getTransactionManager();
            if (txnMgr.getTransaction() != null) {
                throw new Exception("A new transaction will be created for rendering PDF files in PDFUtil, therefore generatePDF method should not be invoked within transaction.");
            }
            txnMgr.setTransactionTimeout(86400000);
            txnMgr.begin();
            Document doc = null;
            final String landscapemode = request.getParameter("landscape");
            if (landscapemode != null && landscapemode.equals("true")) {
                doc = new Document(PageSize.A4.rotate());
            }
            if (!viewName.contains(",")) {
                final ViewContext vc = getViewCtx(request, viewName);
                final PDFTheme theme = getThemeClass(request);
                if (doc == null) {
                    doc = theme.getDocument(vc);
                }
                final PdfWriter writer = PdfWriter.getInstance(doc, os);
                encryptExportData(writer, vc);
                doc.open();
                theme.startPDFDoc(sc, vc, doc, writer);
                initializeRenderingPhase(vc, request);
                final Object ob = includeView(sc, vc, doc, doc, writer, theme, "generalBox");
                if (ob != null && ob instanceof Element) {
                    doc.add((Element)ob);
                }
                endRenderingPhase(vc, request);
                theme.endPDFDoc(sc, vc, doc, writer);
            }
            else {
                if (doc == null) {
                    doc = new Document(PageSize.A4);
                }
                final String[] viewNames = viewName.split(",");
                final PdfWriter writer2 = PdfWriter.getInstance(doc, os);
                encryptExportData(writer2, ViewContext.getViewContext(viewNames[0], request));
                doc.open();
                final MultiViewPDFRenderer renderer = (MultiViewPDFRenderer)WebClientUtil.createInstance(System.getProperty("multiview.pdfrenderer", "com.adventnet.client.view.common.DefaultMultiViewPDFRenderer"));
                renderer.generatePDF(viewNames, sc, writer2, doc, request);
            }
            doc.close();
            txnMgr.commit();
        }
        catch (final Exception ex) {
            try {
                if (txnMgr.getTransaction() != null) {
                    txnMgr.rollback();
                }
            }
            catch (final Exception e) {
                PDFUtil.out.log(Level.INFO, "Error while rollback : ", e);
            }
            throw new PersistenceException(ex.getMessage(), (Throwable)ex);
        }
    }
    
    private static void encryptExportData(final PdfWriter writer, final ViewContext vc) {
        if (ExportUtils.isExportPasswordProtected()) {
            try {
                writer.setEncryption(ExportUtils.getExportPassword(vc).getBytes(), "".getBytes(), 16, 2);
            }
            catch (final DocumentException e) {
                e.printStackTrace();
                PDFUtil.out.severe("exception occurred while encrypt the pdf using a password for the view : " + vc.getModel().getViewName());
                return;
            }
            writer.createXmpMetadata();
        }
    }
    
    public static PDFTheme getThemeClass(final HttpServletRequest request) throws Exception {
        String themeClass = null;
        final Row themeRow = ThemesAPI.getThemeForAccount(WebClientUtil.getAccountId());
        if (themeRow != null) {
            themeClass = (String)themeRow.get("PDFTHEME_CLASS");
        }
        if (themeClass != null) {
            return (PDFTheme)WebClientUtil.createInstance(themeClass);
        }
        return new DefaultPDFTheme();
    }
    
    private static void updatePDFController(final WebViewModel viewModelArg) throws DataAccessException {
        String controllerClass = null;
        if (viewModelArg.getViewConfiguration().containsTable("PdfViewConfig")) {
            controllerClass = (String)viewModelArg.getViewConfiguration().getFirstValue("PdfViewConfig", 3);
        }
        if (controllerClass == null) {
            final DataObject uiCompConfig = viewModelArg.getUIComponentConfig();
            if (uiCompConfig != null) {
                controllerClass = (String)uiCompConfig.getFirstValue("PdfUIComponent", 3);
            }
        }
        if (controllerClass != null) {
            final ViewController controller = (ViewController)WebClientUtil.createInstance(controllerClass);
            viewModelArg.setController(controller);
        }
    }
    
    public static void initializeRenderingPhase(final ViewContext rootViewCtx, final HttpServletRequest request) throws Exception {
        final long start = System.currentTimeMillis();
        if (request.getAttribute("TIME_TO_LOAD_START_TIME") == null) {
            request.setAttribute("TIME_TO_LOAD_START_TIME", (Object)new Long(start));
        }
        request.setAttribute("ROOT_VIEW_CTX", (Object)rootViewCtx);
    }
    
    public static void endRenderingPhase(final ViewContext rootViewCtx, final HttpServletRequest request) throws Exception {
        final long start = (long)request.getAttribute("TIME_TO_LOAD_START_TIME");
        final long end = System.currentTimeMillis();
        final long timeTaken = end - start;
        request.setAttribute("TIME_TO_LOAD", (Object)new Long(timeTaken));
    }
    
    public static PdfPCell setElementInCell(final Element element) {
        PdfPCell cell = null;
        if (element instanceof PdfPCell) {
            cell = (PdfPCell)element;
        }
        else if (element instanceof PdfPTable) {
            cell = new PdfPCell((PdfPTable)element);
        }
        else if (element instanceof Chunk) {
            final Phrase phrase = new Phrase((Chunk)element);
            cell = new PdfPCell(phrase);
        }
        else if (element instanceof Phrase) {
            cell = new PdfPCell((Phrase)element);
        }
        else if (element instanceof Image) {
            cell = new PdfPCell((Image)element);
        }
        else {
            cell = new PdfPCell();
            cell.addElement(element);
        }
        return cell;
    }
    
    public static PdfPCell getTitleElementInCell(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) {
        if (boxType != null && !boxType.trim().equalsIgnoreCase("NONE")) {
            PdfPCell cell = null;
            String title = vc.getTitle();
            if (title == null) {
                title = "";
            }
            final Element element = theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)new Chunk(title), "title");
            if (element instanceof PdfPCell) {
                cell = (PdfPCell)element;
            }
            else {
                if (element instanceof Chunk) {
                    final Phrase phrase = new Phrase((Chunk)element);
                    cell = new PdfPCell(phrase);
                }
                else if (element instanceof Phrase) {
                    cell = new PdfPCell((Phrase)element);
                }
                else if (element instanceof Image) {
                    cell = new PdfPCell((Image)element);
                }
                else if (element instanceof PdfPTable) {
                    cell = new PdfPCell((PdfPTable)element);
                }
                else {
                    cell = new PdfPCell();
                    cell.addElement(element);
                }
                theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)cell, "title");
            }
            return cell;
        }
        return null;
    }
    
    static {
        PDFUtil.out = Logger.getLogger(PDFUtil.class.getName());
    }
}
