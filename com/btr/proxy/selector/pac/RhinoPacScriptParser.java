package com.btr.proxy.selector.pac;

import java.util.Calendar;
import org.mozilla.javascript.Context;
import com.btr.proxy.util.Logger;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RhinoPacScriptParser extends ScriptableObject implements PacScriptParser
{
    private static final long serialVersionUID = 1L;
    private static final String[] JS_FUNCTION_NAMES;
    private Scriptable scope;
    private PacScriptSource source;
    private static final PacScriptMethods SCRIPT_METHODS;
    
    public RhinoPacScriptParser(final PacScriptSource source) throws ProxyEvaluationException {
        this.source = source;
        this.setupEngine();
    }
    
    public void setupEngine() throws ProxyEvaluationException {
        final Context context = new ContextFactory().enterContext();
        try {
            this.defineFunctionProperties(RhinoPacScriptParser.JS_FUNCTION_NAMES, (Class)RhinoPacScriptParser.class, 2);
        }
        catch (final Exception e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "JS Engine setup error.", e);
            throw new ProxyEvaluationException(e.getMessage(), e);
        }
        this.scope = context.initStandardObjects((ScriptableObject)this);
    }
    
    public PacScriptSource getScriptSource() {
        return this.source;
    }
    
    public String evaluate(final String url, final String host) throws ProxyEvaluationException {
        try {
            final StringBuilder script = new StringBuilder(this.source.getScriptContent());
            final String evalMethod = " ;FindProxyForURL (\"" + url + "\",\"" + host + "\")";
            script.append(evalMethod);
            final Context context = Context.enter();
            try {
                final Object result = context.evaluateString(this.scope, script.toString(), "userPacFile", 1, (Object)null);
                return Context.toString(result);
            }
            finally {
                Context.exit();
            }
        }
        catch (final Exception e) {
            Logger.log(this.getClass(), Logger.LogLevel.ERROR, "JS evaluation error.", e);
            throw new ProxyEvaluationException("Error while executing PAC script: " + e.getMessage(), e);
        }
    }
    
    public String getClassName() {
        return this.getClass().getSimpleName();
    }
    
    public static boolean isPlainHostName(final String host) {
        return RhinoPacScriptParser.SCRIPT_METHODS.isPlainHostName(host);
    }
    
    public static boolean dnsDomainIs(final String host, final String domain) {
        return RhinoPacScriptParser.SCRIPT_METHODS.dnsDomainIs(host, domain);
    }
    
    public static boolean localHostOrDomainIs(final String host, final String domain) {
        return RhinoPacScriptParser.SCRIPT_METHODS.localHostOrDomainIs(host, domain);
    }
    
    public static boolean isResolvable(final String host) {
        return RhinoPacScriptParser.SCRIPT_METHODS.isResolvable(host);
    }
    
    public static boolean isInNet(final String host, final String pattern, final String mask) {
        return RhinoPacScriptParser.SCRIPT_METHODS.isInNet(host, pattern, mask);
    }
    
    public static String dnsResolve(final String host) {
        return RhinoPacScriptParser.SCRIPT_METHODS.dnsResolve(host);
    }
    
    public static String myIpAddress() {
        return RhinoPacScriptParser.SCRIPT_METHODS.myIpAddress();
    }
    
    public static int dnsDomainLevels(final String host) {
        return RhinoPacScriptParser.SCRIPT_METHODS.dnsDomainLevels(host);
    }
    
    public static boolean shExpMatch(final String str, final String shexp) {
        return RhinoPacScriptParser.SCRIPT_METHODS.shExpMatch(str, shexp);
    }
    
    public static boolean weekdayRange(final String wd1, final String wd2, final String gmt) {
        return RhinoPacScriptParser.SCRIPT_METHODS.weekdayRange(wd1, wd2, gmt);
    }
    
    static void setCurrentTime(final Calendar cal) {
        RhinoPacScriptParser.SCRIPT_METHODS.setCurrentTime(cal);
    }
    
    public static boolean dateRange(final Object day1, final Object month1, final Object year1, final Object day2, final Object month2, final Object year2, final Object gmt) {
        return RhinoPacScriptParser.SCRIPT_METHODS.dateRange(day1, month1, year1, day2, month2, year2, gmt);
    }
    
    public static boolean timeRange(final Object hour1, final Object min1, final Object sec1, final Object hour2, final Object min2, final Object sec2, final Object gmt) {
        return RhinoPacScriptParser.SCRIPT_METHODS.timeRange(hour1, min1, sec1, hour2, min2, sec2, gmt);
    }
    
    static {
        JS_FUNCTION_NAMES = new String[] { "shExpMatch", "dnsResolve", "isResolvable", "isInNet", "dnsDomainIs", "isPlainHostName", "myIpAddress", "dnsDomainLevels", "localHostOrDomainIs", "weekdayRange", "dateRange", "timeRange" };
        SCRIPT_METHODS = new PacScriptMethods();
    }
}
