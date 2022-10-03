package com.zoho.security.eventfw.pojoconverter;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.context.Context;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.zoho.security.eventfw.type.Log;
import com.zoho.security.eventfw.type.Event;
import java.io.FileOutputStream;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import java.util.logging.Level;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import java.util.logging.Logger;

public class EventJavaFileGenerator
{
    public static final Logger LOGGER;
    private Template template;
    VelocityContext context;
    
    public EventJavaFileGenerator(final String templatePath) throws Exception {
        this.template = null;
        this.context = null;
        final VelocityEngine ve = new VelocityEngine();
        ve.setProperty("file.resource.loader.class", (Object)"com.zoho.security.eventfw.pojoconverter.EventTemplateFileLoader");
        ve.init();
        try {
            this.template = ve.getTemplate(templatePath);
        }
        catch (final ResourceNotFoundException ex) {
            EventJavaFileGenerator.LOGGER.log(Level.SEVERE, "EventJavaFileGenerator : error : cannot find template: {0}, exception : {1}", new Object[] { templatePath, ex.getMessage() });
            throw ex;
        }
        catch (final ParseErrorException ex2) {
            EventJavaFileGenerator.LOGGER.log(Level.SEVERE, "EventJavaFileGenerator : Syntax error in template : {0}, exception : {1}", new Object[] { templatePath, ex2.getMessage() });
            throw ex2;
        }
    }
    
    public void merge(final FileOutputStream stream, final Object dataMembers, final String ename, final String packageName, final String eventType) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
        (this.context = new VelocityContext()).put("pkg", (Object)packageName);
        this.context.put("type", (Object)eventType);
        this.context.put("name", (Object)ename);
        if (dataMembers instanceof Event) {
            this.context.put("params", (Object)dataMembers);
        }
        else if (dataMembers instanceof Log) {
            this.context.put("params", (Object)dataMembers);
        }
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        if (this.template != null) {
            this.template.merge((Context)this.context, (Writer)writer);
        }
        writer.flush();
        writer.close();
    }
    
    static {
        LOGGER = Logger.getLogger(EventJavaFileGenerator.class.getName());
    }
}
