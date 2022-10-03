package com.adventnet.client.view.web;

import org.json.JSONObject;
import com.adventnet.i18n.I18N;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.zoho.mickeyclient.action.HttpUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;

public class DefaultViewController implements ViewController, WebConstants
{
    private static Logger out;
    
    @Override
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
    }
    
    @Override
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        return viewUrl;
    }
    
    @Override
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    }
    
    @Override
    @Deprecated
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        return new ActionForward(WebViewAPI.getRootViewURL(request));
    }
    
    @Override
    public void processViewEvent(final ViewContext viewContext, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        HttpUtil.forward(WebViewAPI.getRootViewURL(request), request, response);
    }
    
    @Override
    public void updateAssociatedTiledViews(final ViewContext viewCtx) throws Exception {
        if (viewCtx.getModel().getViewConfiguration().containsTable("TiledView")) {
            final HttpServletRequest req = ((ViewContext)viewCtx.getRequest().getAttribute("ROOT_VIEW_CTX")).getRequest();
            final Iterator<Row> ite = viewCtx.getModel().getViewConfiguration().getRows("TiledView");
            while (ite.hasNext()) {
                final Row tiledRow = ite.next();
                req.setAttribute("TILE:" + tiledRow.get(2), (Object)WebViewAPI.getViewName(tiledRow.get(3)));
            }
        }
    }
    
    @Override
    public String getTitle(final ViewContext viewCtx) throws Exception {
        String title = (String)viewCtx.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 6);
        if (title == null) {
            title = viewCtx.getModel().getViewName();
        }
        return I18N.getMsg(title, new Object[0]);
    }
    
    @Override
    public void savePreferences(final ViewContext vc) throws Exception {
    }
    
    @Override
    public JSONObject getModelAsJSON(final ViewContext vc) throws Exception {
        return null;
    }
    
    static {
        DefaultViewController.out = Logger.getLogger(DefaultViewController.class.getName());
    }
}
