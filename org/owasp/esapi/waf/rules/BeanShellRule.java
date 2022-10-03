package org.owasp.esapi.waf.rules;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import org.owasp.esapi.waf.actions.DoNothingAction;
import org.owasp.esapi.waf.actions.Action;
import javax.servlet.http.HttpServletResponse;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletRequest;
import bsh.EvalError;
import java.io.IOException;
import org.owasp.esapi.ESAPI;
import java.util.regex.Pattern;
import bsh.Interpreter;

public class BeanShellRule extends Rule
{
    private Interpreter i;
    private String script;
    private Pattern path;
    
    public BeanShellRule(final String fileLocation, final String id, final Pattern path) throws IOException, EvalError {
        (this.i = new Interpreter()).set("logger", (Object)BeanShellRule.logger);
        this.script = this.getFileContents(ESAPI.securityConfiguration().getResourceFile(fileLocation));
        this.id = id;
        this.path = path;
    }
    
    @Override
    public Action check(final HttpServletRequest request, final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) {
        if (this.path != null && !this.path.matcher(request.getRequestURI()).matches()) {
            return new DoNothingAction();
        }
        try {
            Action a = null;
            this.i.set("action", (Object)a);
            this.i.set("request", (Object)request);
            if (response != null) {
                this.i.set("response", (Object)response);
            }
            else {
                this.i.set("response", (Object)httpResponse);
            }
            this.i.set("session", (Object)request.getSession());
            this.i.eval(this.script);
            a = (Action)this.i.get("action");
            if (a != null) {
                return a;
            }
        }
        catch (final EvalError e) {
            this.log(request, "Error running custom beanshell rule (" + this.id + ") - " + e.getMessage());
        }
        return new DoNothingAction();
    }
    
    private String getFileContents(final File f) throws IOException {
        final FileReader fr = new FileReader(f);
        final StringBuffer sb = new StringBuffer();
        final BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + System.getProperty("line.separator"));
        }
        return sb.toString();
    }
}
