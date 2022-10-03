package com.adventnet.client.view.csv;

import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.client.view.common.ExportUtils;
import com.adventnet.client.view.web.ViewContext;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class ViewCSVProcessorServlet extends HttpServlet implements WebConstants
{
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String origView = WebClientUtil.getRequestedPathName(request);
        String fileName = request.getParameter("fileName");
        fileName = (String)StringUtils.defaultIfBlank((CharSequence)fileName, (CharSequence)origView);
        try {
            final ViewContext vc = ViewContext.getViewContext(origView, request);
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "max-age=0");
            if (ExportUtils.isExportPasswordProtected()) {
                String zipName = request.getParameter("zipName");
                zipName = (String)StringUtils.defaultIfBlank((CharSequence)zipName, (CharSequence)fileName);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + ".zip\"");
                response.setContentType("application/zip");
            }
            else {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");
                response.setContentType("text/csv");
            }
            vc.getModel().getController().processPreRendering(vc, request, response, null);
            vc.setRenderType(5);
            final String csvthemeclass = vc.getModel().getFeatureValue("csvClassName");
            CSVTheme csvren;
            if (csvthemeclass != null) {
                csvren = (CSVTheme)WebClientUtil.createInstance(csvthemeclass);
            }
            else {
                csvren = (CSVTheme)WebClientUtil.createInstance("com.adventnet.client.components.table.csv.TableCSVRenderer");
            }
            csvren.generateCSV(origView, request, response);
            vc.getModel().getController().processPostRendering(vc, request, response);
        }
        catch (final Exception exp) {
            response.setContentType("text/html");
            exp.printStackTrace();
            throw new ServletException((Throwable)exp);
        }
    }
}
