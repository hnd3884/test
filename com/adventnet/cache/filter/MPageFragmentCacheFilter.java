package com.adventnet.cache.filter;

import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.cache.dataobject.PageDetailsObject;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public abstract class MPageFragmentCacheFilter extends MPageCacheFilter
{
    private static final Logger LOG;
    
    @Override
    protected void writeContent(final HttpServletRequest hreq, final ServletResponse res, final PageDetailsObject pdo) throws IOException {
        final HttpServletResponse hres = (HttpServletResponse)res;
        final byte[] fragContent = pdo.getUnCompressedBodyContent();
        final String body = new String(fragContent, hres.getCharacterEncoding());
        MPageFragmentCacheFilter.LOG.log(Level.FINE, " Inside page fragment write ............. : ");
        hres.getWriter().write(body);
    }
    
    static {
        LOG = Logger.getLogger(MPageFragmentCacheFilter.class.getName());
    }
}
