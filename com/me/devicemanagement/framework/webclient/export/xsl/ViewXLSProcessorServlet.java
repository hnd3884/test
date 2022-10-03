package com.me.devicemanagement.framework.webclient.export.xsl;

import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.client.view.xls.XLSTheme;
import com.adventnet.client.view.web.ViewContext;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ViewXLSProcessorServlet extends com.adventnet.client.view.xls.ViewXLSProcessorServlet
{
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String origView = WebClientUtil.getRequestedPathName(request);
        String fileName = request.getParameter("fileName");
        fileName = (String)StringUtils.defaultIfBlank((CharSequence)fileName, (CharSequence)origView);
        try {
            final ViewContext vc = ViewContext.getViewContext((Object)origView, request);
            if (response.getContentType() == null) {
                response.setContentType("text/xlsx");
            }
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "max-age=0");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");
            vc.getModel().getController().processPreRendering(vc, request, response, (String)null);
            vc.setRenderType(6);
            final String xlsthemeclass = vc.getModel().getFeatureValue("xlsClassName");
            XLSTheme xlsren;
            if (xlsthemeclass != null) {
                xlsren = (XLSTheme)WebClientUtil.createInstance(xlsthemeclass);
            }
            else {
                xlsren = (XLSTheme)WebClientUtil.createInstance("com.me.devicemanagement.framework.webclient.export.xsl.ExportAsExcel");
            }
            xlsren.generateXLS(origView, request, (Object)response);
            vc.getModel().getController().processPostRendering(vc, request, response);
        }
        catch (final Exception e) {
            response.setContentType("text/html");
            e.printStackTrace();
            throw new ServletException((Throwable)e);
        }
    }
}
