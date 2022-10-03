package com.adventnet.client.util.web;

import com.adventnet.client.ClientException;
import javax.servlet.jsp.JspException;
import javax.servlet.ServletException;
import com.adventnet.iam.xss.IAMEncoder;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils implements WebConstants
{
    public static String getStackTrace(final Throwable thrown) {
        final StringWriter writer = new StringWriter();
        final PrintWriter pWriter = new PrintWriter(writer, true);
        thrown.printStackTrace(pWriter);
        pWriter.close();
        String str = writer.toString();
        str = IAMEncoder.encodeHTML(str);
        str = str.replaceAll("\n", "<br/>");
        return str;
    }
    
    public static Throwable getCause(final Throwable base) {
        Throwable child = null;
        if (base instanceof ServletException) {
            child = ((ServletException)base).getRootCause();
        }
        else if (base instanceof JspException) {
            child = ((JspException)base).getRootCause();
        }
        if (child == null) {
            child = base.getCause();
        }
        if (base == child) {
            child = null;
        }
        return child;
    }
    
    public static Throwable isCausedBy(Throwable thrown, final String errorCode) {
        while (!(thrown instanceof ClientException) || !((ClientException)thrown).getErrorCode().equals(errorCode)) {
            thrown = getCause(thrown);
            if (thrown == null) {
                return null;
            }
        }
        return thrown;
    }
}
