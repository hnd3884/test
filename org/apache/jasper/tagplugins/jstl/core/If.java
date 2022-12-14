package org.apache.jasper.tagplugins.jstl.core;

import org.apache.jasper.compiler.tagplugin.TagPluginContext;
import org.apache.jasper.compiler.tagplugin.TagPlugin;

public final class If implements TagPlugin
{
    @Override
    public void doTag(final TagPluginContext ctxt) {
        final String condV = ctxt.getTemporaryVariableName();
        ctxt.generateJavaSource("boolean " + condV + "=");
        ctxt.generateAttribute("test");
        ctxt.generateJavaSource(";");
        if (ctxt.isAttributeSpecified("var")) {
            String scope = "PageContext.PAGE_SCOPE";
            if (ctxt.isAttributeSpecified("scope")) {
                final String scopeStr = ctxt.getConstantAttribute("scope");
                if ("request".equals(scopeStr)) {
                    scope = "PageContext.REQUEST_SCOPE";
                }
                else if ("session".equals(scopeStr)) {
                    scope = "PageContext.SESSION_SCOPE";
                }
                else if ("application".equals(scopeStr)) {
                    scope = "PageContext.APPLICATION_SCOPE";
                }
            }
            ctxt.generateJavaSource("_jspx_page_context.setAttribute(");
            ctxt.generateAttribute("var");
            ctxt.generateJavaSource(", Boolean.valueOf(" + condV + ")," + scope + ");");
        }
        ctxt.generateJavaSource("if (" + condV + "){");
        ctxt.generateBody();
        ctxt.generateJavaSource("}");
    }
}
