package com.adventnet.client.view.xls;

import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.client.view.web.ViewContext;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class ViewXLSProcessorServlet extends HttpServlet implements WebConstants
{
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String origView = WebClientUtil.getRequestedPathName(request);
        String fileName = request.getParameter("fileName");
        fileName = (String)StringUtils.defaultIfBlank((CharSequence)fileName, (CharSequence)origView);
        try {
            final ViewContext vc = ViewContext.getViewContext(origView, request);
            if (response.getContentType() == null) {
                response.setContentType("text/xlsx");
            }
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "max-age=0");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");
            vc.getModel().getController().processPreRendering(vc, request, response, null);
            vc.setRenderType(6);
            final String xlsthemeclass = vc.getModel().getFeatureValue("xlsClassName");
            XLSTheme xlsren;
            if (xlsthemeclass != null) {
                xlsren = (XLSTheme)WebClientUtil.createInstance(xlsthemeclass);
            }
            else {
                xlsren = (XLSTheme)WebClientUtil.createInstance("com.adventnet.client.components.table.xls.ExportAsExcel");
            }
            xlsren.generateXLS(origView, request, response);
            vc.getModel().getController().processPostRendering(vc, request, response);
        }
        catch (final Exception e) {
            response.setContentType("text/html");
            e.printStackTrace();
            throw new ServletException((Throwable)e);
        }
    }
}
