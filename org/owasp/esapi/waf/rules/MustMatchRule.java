package org.owasp.esapi.waf.rules;

import java.util.Enumeration;
import java.util.Map;
import java.util.Collection;
import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletRequest;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class MustMatchRule extends Rule
{
    private static final String REQUEST_PARAMETERS = "request.parameters.";
    private static final String REQUEST_HEADERS = "request.headers.";
    private static final String REQUEST_URI = "request.uri";
    private static final String REQUEST_URL = "request.url";
    private static final String SESSION_ATTRIBUTES = "session.";
    private Pattern path;
    private String variable;
    private int operator;
    private String value;
    
    public MustMatchRule(final String id, final Pattern path, final String variable, final int operator, final String value) {
        this.path = path;
        this.variable = variable;
        this.operator = operator;
        this.value = value;
        this.setId(id);
    }
    
    @Override
    public Action check(final HttpServletRequest req, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        final InterceptingHTTPServletRequest request = (InterceptingHTTPServletRequest)req;
        final String uri = request.getRequestURI();
        if (!this.path.matcher(uri).matches()) {
            return new DoNothingAction();
        }
        String target = null;
        if (this.variable.startsWith("request.parameters.")) {
            if (this.operator == 3) {
                target = this.variable.substring("request.parameters.".length());
                if (request.getParameter(target) != null) {
                    return new DoNothingAction();
                }
            }
            else if (this.operator != 2) {
                if (this.operator == 0 || this.operator == 1) {
                    target = this.variable.substring("request.parameters.".length());
                    if (target.contains("*") || target.contains("?")) {
                        target = target.replaceAll("*", ".*");
                        final Pattern p = Pattern.compile(target);
                        final Enumeration e = request.getParameterNames();
                        while (e.hasMoreElements()) {
                            final String param = e.nextElement();
                            if (p.matcher(param).matches()) {
                                final String s = request.getParameter(param);
                                if (!RuleUtil.testValue(s, this.value, this.operator)) {
                                    this.log((HttpServletRequest)request, "MustMatch rule failed (operator=" + this.operator + "), value='" + this.value + "', input='" + s + "' parameter='" + param + "'");
                                    return new DefaultAction();
                                }
                                continue;
                            }
                        }
                    }
                    else {
                        final String s2 = request.getParameter(target);
                        if (!RuleUtil.testValue(s2, this.value, this.operator)) {
                            this.log((HttpServletRequest)request, "MustMatch rule failed (operator=" + this.operator + "), value='" + this.value + "', input='" + s2 + "', parameter='" + target + "'");
                            return new DefaultAction();
                        }
                    }
                }
            }
        }
        else if (this.variable.startsWith("request.headers.")) {
            if (this.operator == 3) {
                target = this.variable.substring("request.headers.".length());
                if (request.getHeader(target) != null) {
                    return new DoNothingAction();
                }
            }
            else if (this.operator != 2) {
                if (this.operator == 0 || this.operator == 1) {
                    target = this.variable.substring("request.headers.".length());
                    if (target.contains("*") || target.contains("?")) {
                        target = target.replaceAll("*", ".*");
                        final Pattern p = Pattern.compile(target);
                        final Enumeration e = request.getHeaderNames();
                        while (e.hasMoreElements()) {
                            final String header = e.nextElement();
                            if (p.matcher(header).matches()) {
                                final String s = request.getHeader(header);
                                if (!RuleUtil.testValue(s, this.value, this.operator)) {
                                    this.log((HttpServletRequest)request, "MustMatch rule failed (operator=" + this.operator + "), value='" + this.value + "', input='" + s + "', header='" + header + "'");
                                    return new DefaultAction();
                                }
                                continue;
                            }
                        }
                        return new DoNothingAction();
                    }
                    final String s2 = request.getHeader(target);
                    if (s2 == null || !RuleUtil.testValue(s2, this.value, this.operator)) {
                        this.log((HttpServletRequest)request, "MustMatch rule failed (operator=" + this.operator + "), value='" + this.value + "', input='" + s2 + "', header='" + target + "'");
                        return new DefaultAction();
                    }
                    return new DoNothingAction();
                }
            }
        }
        else if (this.variable.startsWith("session.")) {
            if (request.getSession(false) == null) {
                return new DefaultAction();
            }
            target = this.variable.substring("session.".length() + 1);
            if (this.operator == 2) {
                final Object o = request.getSession(false).getAttribute(target);
                if (o instanceof Collection) {
                    if (RuleUtil.isInList((Collection)o, this.value)) {
                        return new DoNothingAction();
                    }
                    this.log((HttpServletRequest)request, "MustMatch rule failed - looking for value='" + this.value + "', in session Collection attribute '" + target + "']");
                    return new DefaultAction();
                }
                else if (o instanceof Map) {
                    if (RuleUtil.isInList((Map)o, this.value)) {
                        return new DoNothingAction();
                    }
                    this.log((HttpServletRequest)request, "MustMatch rule failed - looking for value='" + this.value + "', in session Map attribute '" + target + "']");
                    return new DefaultAction();
                }
                else if (o instanceof Enumeration) {
                    if (RuleUtil.isInList((Enumeration)o, this.value)) {
                        return new DoNothingAction();
                    }
                    this.log((HttpServletRequest)request, "MustMatch rule failed - looking for value='" + this.value + "', in session Enumeration attribute '" + target + "']");
                    return new DefaultAction();
                }
            }
            else if (this.operator == 3) {
                final Object o = request.getSession(false).getAttribute(target);
                if (o != null) {
                    return new DoNothingAction();
                }
                this.log((HttpServletRequest)request, "MustMatch rule failed - couldn't find required session attribute='" + target + "'");
                return new DefaultAction();
            }
            else if (this.operator == 0 || this.operator == 1) {
                if (target.contains("*") || target.contains("?")) {
                    target = target.replaceAll("\\*", ".*");
                    final Pattern p = Pattern.compile(target);
                    final Enumeration e = request.getSession(false).getAttributeNames();
                    while (e.hasMoreElements()) {
                        final String attr = e.nextElement();
                        if (p.matcher(attr).matches()) {
                            final Object o2 = request.getSession(false).getAttribute(attr);
                            if (!RuleUtil.testValue((String)o2, this.value, this.operator)) {
                                this.log((HttpServletRequest)request, "MustMatch rule failed (operator=" + this.operator + "), value='" + this.value + "', session attribute='" + attr + "', attribute value='" + (String)o2 + "'");
                                return new DefaultAction();
                            }
                            return new DoNothingAction();
                        }
                    }
                }
                else {
                    final Object o = request.getSession(false).getAttribute(target);
                    if (!RuleUtil.testValue((String)o, this.value, this.operator)) {
                        this.log((HttpServletRequest)request, "MustMatch rule failed (operator=" + this.operator + "), value='" + this.value + "', session attribute='" + target + "', attribute value='" + (String)o + "'");
                        return new DefaultAction();
                    }
                    return new DoNothingAction();
                }
            }
        }
        else if (this.variable.equals("request.uri")) {
            if (this.operator == 0 || this.operator == 1) {
                if (RuleUtil.testValue(request.getRequestURI(), this.value, this.operator)) {
                    return new DoNothingAction();
                }
                this.log((HttpServletRequest)request, "MustMatch rule on request URI failed (operator=" + this.operator + "), requestURI='" + request.getRequestURI() + "', value='" + this.value + "'");
                return new DefaultAction();
            }
        }
        else if (this.variable.equals("request.url") && (this.operator == 0 || this.operator == 1)) {
            if (RuleUtil.testValue(request.getRequestURL().toString(), this.value, this.operator)) {
                return new DoNothingAction();
            }
            this.log((HttpServletRequest)request, "MustMatch rule on request URL failed (operator=" + this.operator + "), requestURL='" + (Object)request.getRequestURL() + "', value='" + this.value + "'");
            return new DefaultAction();
        }
        this.log((HttpServletRequest)request, "MustMatch rule failed close on URL '" + (Object)request.getRequestURL() + "'");
        return new DefaultAction();
    }
}
