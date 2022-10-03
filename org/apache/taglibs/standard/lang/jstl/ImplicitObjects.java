package org.apache.taglibs.standard.lang.jstl;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import javax.servlet.ServletContext;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.jsp.PageContext;

public class ImplicitObjects
{
    static final String sAttributeName = "org.apache.taglibs.standard.ImplicitObjects";
    PageContext mContext;
    Map mPage;
    Map mRequest;
    Map mSession;
    Map mApplication;
    Map mParam;
    Map mParams;
    Map mHeader;
    Map mHeaders;
    Map mInitParam;
    Map mCookie;
    
    public ImplicitObjects(final PageContext pContext) {
        this.mContext = pContext;
    }
    
    public static ImplicitObjects getImplicitObjects(final PageContext pContext) {
        ImplicitObjects objs = (ImplicitObjects)pContext.getAttribute("org.apache.taglibs.standard.ImplicitObjects", 1);
        if (objs == null) {
            objs = new ImplicitObjects(pContext);
            pContext.setAttribute("org.apache.taglibs.standard.ImplicitObjects", (Object)objs, 1);
        }
        return objs;
    }
    
    public Map getPageScopeMap() {
        if (this.mPage == null) {
            this.mPage = createPageScopeMap(this.mContext);
        }
        return this.mPage;
    }
    
    public Map getRequestScopeMap() {
        if (this.mRequest == null) {
            this.mRequest = createRequestScopeMap(this.mContext);
        }
        return this.mRequest;
    }
    
    public Map getSessionScopeMap() {
        if (this.mSession == null) {
            this.mSession = createSessionScopeMap(this.mContext);
        }
        return this.mSession;
    }
    
    public Map getApplicationScopeMap() {
        if (this.mApplication == null) {
            this.mApplication = createApplicationScopeMap(this.mContext);
        }
        return this.mApplication;
    }
    
    public Map getParamMap() {
        if (this.mParam == null) {
            this.mParam = createParamMap(this.mContext);
        }
        return this.mParam;
    }
    
    public Map getParamsMap() {
        if (this.mParams == null) {
            this.mParams = createParamsMap(this.mContext);
        }
        return this.mParams;
    }
    
    public Map getHeaderMap() {
        if (this.mHeader == null) {
            this.mHeader = createHeaderMap(this.mContext);
        }
        return this.mHeader;
    }
    
    public Map getHeadersMap() {
        if (this.mHeaders == null) {
            this.mHeaders = createHeadersMap(this.mContext);
        }
        return this.mHeaders;
    }
    
    public Map getInitParamMap() {
        if (this.mInitParam == null) {
            this.mInitParam = createInitParamMap(this.mContext);
        }
        return this.mInitParam;
    }
    
    public Map getCookieMap() {
        if (this.mCookie == null) {
            this.mCookie = createCookieMap(this.mContext);
        }
        return this.mCookie;
    }
    
    public static Map createPageScopeMap(final PageContext pContext) {
        final PageContext context = pContext;
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return context.getAttributeNamesInScope(1);
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    return context.getAttribute((String)pKey, 1);
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return true;
            }
        };
    }
    
    public static Map createRequestScopeMap(final PageContext pContext) {
        final PageContext context = pContext;
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return context.getAttributeNamesInScope(2);
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    return context.getAttribute((String)pKey, 2);
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return true;
            }
        };
    }
    
    public static Map createSessionScopeMap(final PageContext pContext) {
        final PageContext context = pContext;
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return context.getAttributeNamesInScope(3);
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    return context.getAttribute((String)pKey, 3);
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return true;
            }
        };
    }
    
    public static Map createApplicationScopeMap(final PageContext pContext) {
        final PageContext context = pContext;
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return context.getAttributeNamesInScope(4);
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    return context.getAttribute((String)pKey, 4);
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return true;
            }
        };
    }
    
    public static Map createParamMap(final PageContext pContext) {
        final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return request.getParameterNames();
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    return request.getParameter((String)pKey);
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return false;
            }
        };
    }
    
    public static Map createParamsMap(final PageContext pContext) {
        final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return request.getParameterNames();
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    return request.getParameterValues((String)pKey);
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return false;
            }
        };
    }
    
    public static Map createHeaderMap(final PageContext pContext) {
        final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return request.getHeaderNames();
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    return request.getHeader((String)pKey);
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return false;
            }
        };
    }
    
    public static Map createHeadersMap(final PageContext pContext) {
        final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return request.getHeaderNames();
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    final List l = new ArrayList();
                    final Enumeration enum_ = request.getHeaders((String)pKey);
                    if (enum_ != null) {
                        while (enum_.hasMoreElements()) {
                            l.add(enum_.nextElement());
                        }
                    }
                    final String[] ret = l.toArray(new String[l.size()]);
                    return ret;
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return false;
            }
        };
    }
    
    public static Map createInitParamMap(final PageContext pContext) {
        final ServletContext context = pContext.getServletContext();
        return new EnumeratedMap() {
            @Override
            public Enumeration enumerateKeys() {
                return context.getInitParameterNames();
            }
            
            @Override
            public Object getValue(final Object pKey) {
                if (pKey instanceof String) {
                    return context.getInitParameter((String)pKey);
                }
                return null;
            }
            
            @Override
            public boolean isMutable() {
                return false;
            }
        };
    }
    
    public static Map createCookieMap(final PageContext pContext) {
        final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
        final Cookie[] cookies = request.getCookies();
        final Map ret = new HashMap();
        for (int i = 0; cookies != null && i < cookies.length; ++i) {
            final Cookie cookie = cookies[i];
            if (cookie != null) {
                final String name = cookie.getName();
                if (!ret.containsKey(name)) {
                    ret.put(name, cookie);
                }
            }
        }
        return ret;
    }
}
