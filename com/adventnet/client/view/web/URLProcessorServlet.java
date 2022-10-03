package com.adventnet.client.view.web;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaModel;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import java.util.ArrayList;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class URLProcessorServlet extends HttpServlet implements WebConstants
{
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            String reqUri = WebClientUtil.getRequestedPathWithExtension(request);
            reqUri = reqUri.substring(reqUri.indexOf(47) + 1);
            String rootview = null;
            if (reqUri.indexOf(47) != -1) {
                rootview = reqUri.substring(0, reqUri.indexOf(47));
            }
            reqUri = reqUri.substring(reqUri.indexOf(47) + 1);
            String firstview = null;
            if (reqUri.indexOf(47) != -1) {
                firstview = reqUri.substring(0, reqUri.indexOf(47));
            }
            else {
                firstview = reqUri;
            }
            final ArrayList lastviews = new ArrayList();
            while (reqUri.indexOf(47) != -1) {
                String lastview = null;
                reqUri = reqUri.substring(reqUri.indexOf(47) + 1);
                if (reqUri.length() > 0) {
                    if (reqUri.indexOf(47) != -1) {
                        lastview = reqUri.substring(0, reqUri.indexOf(47));
                    }
                    else {
                        lastview = reqUri;
                    }
                }
                lastviews.add(lastview);
            }
            request.setAttribute("rootview", (Object)rootview);
            ArrayList targetlist = TabInformationAPI.getTabMCFrameNames(0, rootview, request);
            DynamicContentAreaAPI.updateDynamicContentArea(request, firstview, firstview, targetlist.get(0), null, true, false);
            final int len = lastviews.size();
            if (len == 0) {
                targetlist = TabInformationAPI.getTabMCFrameNames(1, rootview, request);
                for (int targetlistsize = targetlist.size(), y = 0; y < targetlistsize; ++y) {
                    final String target = targetlist.get(y);
                    final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(request, target);
                    model.clearList();
                }
            }
            for (int i = 0; i < len; ++i) {
                final String lastview2 = lastviews.get(i);
                if (lastview2 != null && lastview2.length() > 0) {
                    targetlist = TabInformationAPI.getTabMCFrameNames(i + 1, rootview, request);
                    for (int targetlistsize2 = targetlist.size(), y2 = 0; y2 < targetlistsize2; ++y2) {
                        final String target2 = targetlist.get(y2);
                        final DynamicContentAreaModel model2 = DynamicContentAreaAPI.getDynamicContentAreaModel(request, target2);
                        model2.clearList();
                        final ArrayList list = TabInformationAPI.getDACListFromParams(target2, request);
                        if (list != null) {
                            for (int size = list.size(), x = 0; x < size; ++x) {
                                DynamicContentAreaAPI.updateDynamicContentArea(request, new Long(list.get(x)), null, target2, null, true, true);
                            }
                        }
                        DynamicContentAreaAPI.updateDynamicContentArea(request, lastview2, lastview2, target2, null, true, true);
                    }
                }
            }
            final RequestDispatcher rd = request.getRequestDispatcher("/" + rootview + ".cc");
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ServletException((Throwable)ex);
        }
    }
    
    public static String getUrl2(final HttpServletRequest req) {
        String reqUri = req.getRequestURI().toString();
        final String queryString = req.getQueryString();
        if (queryString != null) {
            reqUri = reqUri + "?" + queryString;
        }
        return reqUri;
    }
}
