package org.apache.catalina.valves;

import org.apache.catalina.connector.Response;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringJoiner;
import org.apache.catalina.connector.Request;
import com.adventnet.iam.security.SecurityLogRequestWrapper;
import javax.servlet.ServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.LogRecord;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Arrays;
import java.io.IOException;
import java.util.logging.Level;
import java.io.CharArrayWriter;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.Set;
import java.util.List;
import java.util.logging.Logger;

public class MEAccessLogValve extends ExtendedAccessLogValve
{
    private static final Logger LOGGER;
    private static final List<String> EXCLUDE_PARAMS;
    private static final Set<String> EXCLUDE_HEADERS;
    
    protected synchronized void open() {
    }
    
    public MEAccessLogValve() {
        final Handler[] ha = MEAccessLogValve.LOGGER.getHandlers();
        for (int i = 0; i < ha.length; ++i) {
            ha[i].setFormatter(new NoFormat());
        }
    }
    
    public void log(final CharArrayWriter message) {
        MEAccessLogValve.LOGGER.info(message.append('\n').toString());
    }
    
    protected AbstractAccessLogValve.AccessLogElement getXParameterElement(final ExtendedAccessLogValve.PatternTokenizer tokenizer) throws IOException {
        if (!tokenizer.hasSubToken()) {
            MEAccessLogValve.LOGGER.log(Level.SEVERE, MEAccessLogValve.sm.getString("extendedAccessLogValve.badXParam"));
            return null;
        }
        final String token = tokenizer.getToken();
        if ("threadname".equals(token)) {
            return (AbstractAccessLogValve.AccessLogElement)new AbstractAccessLogValve.ThreadNameElement();
        }
        if (!tokenizer.hasParameter()) {
            MEAccessLogValve.LOGGER.log(Level.SEVERE, MEAccessLogValve.sm.getString("extendedAccessLogValve.badXParam"));
            return null;
        }
        final String parameter = tokenizer.getParameter();
        if (parameter == null) {
            MEAccessLogValve.LOGGER.log(Level.SEVERE, MEAccessLogValve.sm.getString("extendedAccessLogValve.noClosing"));
            return null;
        }
        if ("A".equals(token)) {
            return (AbstractAccessLogValve.AccessLogElement)new ExtendedAccessLogValve.ServletContextElement(parameter);
        }
        if ("C".equals(token)) {
            return (AbstractAccessLogValve.AccessLogElement)new ExtendedAccessLogValve.CookieElement(parameter);
        }
        if ("R".equals(token)) {
            return (AbstractAccessLogValve.AccessLogElement)new ExtendedAccessLogValve.RequestAttributeElement(parameter);
        }
        if ("S".equals(token)) {
            return (AbstractAccessLogValve.AccessLogElement)new ExtendedAccessLogValve.SessionAttributeElement(parameter);
        }
        if ("H".equals(token)) {
            return this.getServletRequestElement(parameter);
        }
        if ("P".equals(token)) {
            return (AbstractAccessLogValve.AccessLogElement)new RequestParameterElement(parameter);
        }
        if ("O".equals(token)) {
            return (AbstractAccessLogValve.AccessLogElement)new ExtendedAccessLogValve.ResponseAllHeaderElement(parameter);
        }
        MEAccessLogValve.LOGGER.log(Level.SEVERE, MEAccessLogValve.sm.getString("extendedAccessLogValve.badXParamValue", new Object[] { token }));
        return null;
    }
    
    protected AbstractAccessLogValve.AccessLogElement getClientToServerElement(final ExtendedAccessLogValve.PatternTokenizer tokenizer) throws IOException {
        if (tokenizer.hasSubToken()) {
            return super.getClientToServerElement(tokenizer);
        }
        if (!tokenizer.hasParameter()) {
            MEAccessLogValve.LOGGER.log(Level.SEVERE, MEAccessLogValve.sm.getString("extendedAccessLogValve.decodeError", new Object[] { tokenizer.getRemains() }));
            return null;
        }
        final String parameter = tokenizer.getParameter();
        if (parameter == null) {
            MEAccessLogValve.LOGGER.log(Level.SEVERE, MEAccessLogValve.sm.getString("extendedAccessLogValve.noClosing"));
            return null;
        }
        return (AbstractAccessLogValve.AccessLogElement)new RequestHeaderElement(parameter);
    }
    
    static {
        LOGGER = Logger.getLogger("TomcatAccessLog");
        EXCLUDE_PARAMS = Arrays.asList("j_username", "j_password");
        (EXCLUDE_HEADERS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER)).add("cookie");
    }
    
    public static class NoFormat extends Formatter
    {
        @Override
        public synchronized String format(final LogRecord record) {
            return record.getMessage();
        }
    }
    
    protected static class RequestParameterElement implements AbstractAccessLogValve.AccessLogElement
    {
        private final String parameter;
        
        public RequestParameterElement(final String parameter) {
            this.parameter = parameter;
        }
        
        private String urlEncode(final String value) {
            if (null == value || value.length() == 0) {
                return null;
            }
            try {
                return URLEncoder.encode(value, "UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                return null;
            }
        }
        
        private void logRequestParam(final CharArrayWriter buf, final ServletRequest request) {
            if (!this.parameter.equals("all")) {
                buf.append(ExtendedAccessLogValve.wrap((Object)this.urlEncode(request.getParameter(this.parameter))));
                return;
            }
            if (!(request instanceof SecurityLogRequestWrapper) && ((Request)request).getRemoteUser() != null) {
                buf.append(ExtendedAccessLogValve.wrap((Object)"-"));
                return;
            }
            final StringJoiner paramList = new StringJoiner(", ", "{", "}");
            paramList.setEmptyValue("-");
            final Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                final String paramName = params.nextElement();
                if (MEAccessLogValve.EXCLUDE_PARAMS.contains(paramName)) {
                    paramList.add(paramName + " : *****");
                }
                else {
                    paramList.add(this.urlEncode(paramName) + " : " + this.urlEncode(request.getParameter(paramName)));
                }
            }
            buf.append(paramList.toString());
        }
        
        public void addElement(final CharArrayWriter buf, final Date date, final Request request, final Response response, final long time) {
            if (request.getAttribute("com.adventnet.iam.security.SecurityLogRequestWrapper") != null) {
                final SecurityLogRequestWrapper requestWrapper = (SecurityLogRequestWrapper)request.getAttribute("com.adventnet.iam.security.SecurityLogRequestWrapper");
                this.logRequestParam(buf, (ServletRequest)requestWrapper);
            }
            else {
                this.logRequestParam(buf, (ServletRequest)request);
            }
        }
    }
    
    protected static class RequestHeaderElement implements AbstractAccessLogValve.AccessLogElement
    {
        private final String header;
        
        public RequestHeaderElement(final String header) {
            this.header = header;
        }
        
        public void addElement(final CharArrayWriter buf, final Date date, final Request request, final Response response, final long time) {
            if (request.getAttribute("com.adventnet.iam.security.SecurityLogRequestWrapper") != null) {
                final StringJoiner headers = new StringJoiner(", ", "{", "}");
                headers.setEmptyValue("-");
                final SecurityLogRequestWrapper requestWrapper = (SecurityLogRequestWrapper)request.getAttribute("com.adventnet.iam.security.SecurityLogRequestWrapper");
                if (this.header.equals("all")) {
                    final Enumeration<String> headerNames = requestWrapper.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        final String headerName = headerNames.nextElement();
                        headers.add(headerName + " : " + ExtendedAccessLogValve.wrap((Object)requestWrapper.getHeader(headerName)));
                    }
                    buf.append(headers.toString());
                    return;
                }
                buf.append(ExtendedAccessLogValve.wrap((Object)requestWrapper.getHeader(this.header)));
            }
            else {
                if (request.getUserPrincipal() != null) {
                    buf.append(ExtendedAccessLogValve.wrap((Object)"-"));
                    return;
                }
                final StringJoiner headers = new StringJoiner(", ", "{", "}");
                headers.setEmptyValue("-");
                if (this.header.equals("all")) {
                    final Enumeration<String> headerNames2 = request.getHeaderNames();
                    while (headerNames2.hasMoreElements()) {
                        final String headerName2 = headerNames2.nextElement();
                        if (MEAccessLogValve.EXCLUDE_HEADERS.contains(headerName2)) {
                            headers.add(headerName2 + " : *****");
                        }
                        else {
                            headers.add(headerName2 + " : " + ExtendedAccessLogValve.wrap((Object)request.getHeader(headerName2)));
                        }
                    }
                    buf.append(headers.toString());
                    return;
                }
                buf.append(ExtendedAccessLogValve.wrap((Object)request.getHeader(this.header)));
            }
        }
    }
}
