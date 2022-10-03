package com.adventnet.client.cache.web;

import com.adventnet.client.util.web.WebClientUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.adventnet.client.util.web.WebConstants;
import org.apache.struts.action.Action;

public class ClearViewCacheAction extends Action implements WebConstants
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        ClientDataObjectCache.clearViewCacheForAccount(WebClientUtil.getAccountId());
        return null;
    }
}
