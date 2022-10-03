package com.adventnet.client.action.web;

import org.apache.struts.tiles.TilesRequestProcessor;
import java.io.IOException;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.RequestProcessor;

public class MenuActionRequestProcessor extends RequestProcessor
{
    protected ActionMapping processMapping(final HttpServletRequest request, final HttpServletResponse response, final String path) throws IOException {
        ActionMapping mapping = null;
        try {
            mapping = MenuStrutsUtil.getActionMapping(request, response, path, this.getServletContext());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        if (mapping == null) {
            mapping = super.processMapping(request, response, path);
        }
        return mapping;
    }
    
    public static class TilesBasedProcessor extends TilesRequestProcessor
    {
        protected ActionMapping processMapping(final HttpServletRequest request, final HttpServletResponse response, final String path) throws IOException {
            ActionMapping mapping = null;
            try {
                mapping = MenuStrutsUtil.getActionMapping(request, response, path, this.getServletContext());
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            if (mapping == null) {
                mapping = super.processMapping(request, response, path);
            }
            return mapping;
        }
    }
}
