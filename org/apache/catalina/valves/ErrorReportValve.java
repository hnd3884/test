package org.apache.catalina.valves;

import java.io.Writer;
import org.apache.catalina.util.ServerInfo;
import java.util.Enumeration;
import org.apache.tomcat.util.res.StringManager;
import java.util.Scanner;
import org.apache.tomcat.util.security.Escape;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.coyote.ActionCode;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;

public class ErrorReportValve extends ValveBase
{
    private boolean showReport;
    private boolean showServerInfo;
    
    public ErrorReportValve() {
        super(true);
        this.showReport = true;
        this.showServerInfo = true;
    }
    
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        this.getNext().invoke(request, response);
        if (response.isCommitted()) {
            if (response.setErrorReported()) {
                final AtomicBoolean ioAllowed = new AtomicBoolean(true);
                response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, (Object)ioAllowed);
                if (ioAllowed.get()) {
                    try {
                        response.flushBuffer();
                    }
                    catch (final Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                    }
                    response.getCoyoteResponse().action(ActionCode.CLOSE_NOW, request.getAttribute("javax.servlet.error.exception"));
                }
            }
            return;
        }
        final Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
        if (request.isAsync() && !request.isAsyncCompleting()) {
            return;
        }
        if (throwable != null && !response.isError()) {
            response.reset();
            response.sendError(500);
        }
        response.setSuspended(false);
        try {
            this.report(request, response, throwable);
        }
        catch (final Throwable tt) {
            ExceptionUtils.handleThrowable(tt);
        }
    }
    
    protected void report(final Request request, final Response response, final Throwable throwable) {
        final int statusCode = response.getStatus();
        if (statusCode < 400 || response.getContentWritten() > 0L || !response.setErrorReported()) {
            return;
        }
        final AtomicBoolean result = new AtomicBoolean(false);
        response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, (Object)result);
        if (!result.get()) {
            return;
        }
        String message = Escape.htmlElementContent(response.getMessage());
        if (message == null) {
            if (throwable != null) {
                final String exceptionMessage = throwable.getMessage();
                if (exceptionMessage != null && exceptionMessage.length() > 0) {
                    try (final Scanner scanner = new Scanner(exceptionMessage)) {
                        message = Escape.htmlElementContent(scanner.nextLine());
                    }
                }
            }
            if (message == null) {
                message = "";
            }
        }
        String reason = null;
        String description = null;
        final StringManager smClient = StringManager.getManager("org.apache.catalina.valves", (Enumeration)request.getLocales());
        response.setLocale(smClient.getLocale());
        try {
            reason = smClient.getString("http." + statusCode + ".reason");
            description = smClient.getString("http." + statusCode + ".desc");
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        if (reason == null || description == null) {
            if (message.isEmpty()) {
                return;
            }
            reason = smClient.getString("errorReportValve.unknownReason");
            description = smClient.getString("errorReportValve.noDescription");
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html><html lang=\"");
        sb.append(smClient.getLocale().getLanguage()).append("\">");
        sb.append("<head>");
        sb.append("<title>");
        sb.append(smClient.getString("errorReportValve.statusHeader", new Object[] { String.valueOf(statusCode), reason }));
        sb.append("</title>");
        sb.append("<style type=\"text/css\">");
        sb.append("body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}");
        sb.append("</style>");
        sb.append("</head><body>");
        sb.append("<h1>");
        sb.append(smClient.getString("errorReportValve.statusHeader", new Object[] { String.valueOf(statusCode), reason })).append("</h1>");
        if (this.isShowReport()) {
            sb.append("<hr class=\"line\" />");
            sb.append("<p><b>");
            sb.append(smClient.getString("errorReportValve.type"));
            sb.append("</b> ");
            if (throwable != null) {
                sb.append(smClient.getString("errorReportValve.exceptionReport"));
            }
            else {
                sb.append(smClient.getString("errorReportValve.statusReport"));
            }
            sb.append("</p>");
            if (!message.isEmpty()) {
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.message"));
                sb.append("</b> ");
                sb.append(message).append("</p>");
            }
            sb.append("<p><b>");
            sb.append(smClient.getString("errorReportValve.description"));
            sb.append("</b> ");
            sb.append(description);
            sb.append("</p>");
            if (throwable != null) {
                String stackTrace = this.getPartialServletStackTrace(throwable);
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.exception"));
                sb.append("</b></p><pre>");
                sb.append(Escape.htmlElementContent(stackTrace));
                sb.append("</pre>");
                int loops = 0;
                for (Throwable rootCause = throwable.getCause(); rootCause != null && loops < 10; rootCause = rootCause.getCause(), ++loops) {
                    stackTrace = this.getPartialServletStackTrace(rootCause);
                    sb.append("<p><b>");
                    sb.append(smClient.getString("errorReportValve.rootCause"));
                    sb.append("</b></p><pre>");
                    sb.append(Escape.htmlElementContent(stackTrace));
                    sb.append("</pre>");
                }
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.note"));
                sb.append("</b> ");
                sb.append(smClient.getString("errorReportValve.rootCauseInLogs"));
                sb.append("</p>");
            }
            sb.append("<hr class=\"line\" />");
        }
        if (this.isShowServerInfo()) {
            sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        }
        sb.append("</body></html>");
        try {
            try {
                response.setContentType("text/html");
                response.setCharacterEncoding("utf-8");
            }
            catch (final Throwable t2) {
                ExceptionUtils.handleThrowable(t2);
                if (this.container.getLogger().isDebugEnabled()) {
                    this.container.getLogger().debug((Object)"status.setContentType", t2);
                }
            }
            final Writer writer = response.getReporter();
            if (writer != null) {
                writer.write(sb.toString());
                response.finishResponse();
            }
        }
        catch (final IOException | IllegalStateException ex) {}
    }
    
    protected String getPartialServletStackTrace(final Throwable t) {
        final StringBuilder trace = new StringBuilder();
        trace.append(t.toString()).append(System.lineSeparator());
        final StackTraceElement[] elements = t.getStackTrace();
        int pos = elements.length;
        for (int i = elements.length - 1; i >= 0; --i) {
            if (elements[i].getClassName().startsWith("org.apache.catalina.core.ApplicationFilterChain") && elements[i].getMethodName().equals("internalDoFilter")) {
                pos = i;
                break;
            }
        }
        for (int i = 0; i < pos; ++i) {
            if (!elements[i].getClassName().startsWith("org.apache.catalina.core.")) {
                trace.append('\t').append(elements[i].toString()).append(System.lineSeparator());
            }
        }
        return trace.toString();
    }
    
    public void setShowReport(final boolean showReport) {
        this.showReport = showReport;
    }
    
    public boolean isShowReport() {
        return this.showReport;
    }
    
    public void setShowServerInfo(final boolean showServerInfo) {
        this.showServerInfo = showServerInfo;
    }
    
    public boolean isShowServerInfo() {
        return this.showServerInfo;
    }
}
