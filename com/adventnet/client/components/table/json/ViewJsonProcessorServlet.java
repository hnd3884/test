package com.adventnet.client.components.table.json;

import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.view.common.ExportUtils;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class ViewJsonProcessorServlet extends HttpServlet implements WebConstants
{
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String origView = WebClientUtil.getRequestedPathName(request);
        String fileName = request.getParameter("fileName");
        fileName = (String)StringUtils.defaultIfBlank((CharSequence)fileName, (CharSequence)origView);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=0");
        if (ExportUtils.isExportPasswordProtected()) {
            String zipName = request.getParameter("zipName");
            zipName = (String)StringUtils.defaultIfBlank((CharSequence)zipName, (CharSequence)fileName);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + ".zip\"");
            response.setContentType("application/zip");
        }
        else {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".json\"");
            response.setContentType("application/json");
        }
        final String originalView = WebClientUtil.getRequestedPathName(request);
        final ViewContext vc = ViewContext.getViewContext((Object)originalView, request);
        try {
            vc.getModel().getController().processPreRendering(vc, request, response, (String)null);
            vc.setRenderType(7);
            final String jsonthemeclass = vc.getModel().getFeatureValue("jsonClassName");
            JsonTheme jsonren;
            if (jsonthemeclass != null) {
                jsonren = (JsonTheme)WebClientUtil.createInstance(jsonthemeclass);
            }
            else {
                jsonren = (JsonTheme)WebClientUtil.createInstance("com.adventnet.client.components.table.json.TableJSONRenderer");
            }
            jsonren.generateJSON(origView, request, response);
            vc.getModel().getController().processPostRendering(vc, request, response);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new ServletException((Throwable)exp);
        }
    }
}
