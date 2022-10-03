package com.zoho.security.eventfw.pojoconverter;

import java.util.Iterator;
import java.io.FileOutputStream;
import java.util.Map;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.zoho.security.eventfw.config.EventFWConstants;
import java.io.File;
import com.zoho.security.eventfw.config.EventConfigParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class EventConfigurationReader
{
    Options options;
    private String packageName;
    
    public EventConfigurationReader(final String[] args) throws Exception {
        this.options = null;
        final Options options = new Options();
        options.addOption("help", false, "print this message");
        options.addRequiredOption("eventxml", (String)null, true, "DataDictionary File to be parsed (*)");
        options.addRequiredOption("template", (String)null, true, "File location of the Template to be used for file generation (*)");
        options.addRequiredOption("outdir", (String)null, true, "Root directory in which out files needs to be created (*)");
        final CommandLineParser parser = (CommandLineParser)new DefaultParser();
        try {
            final CommandLine cmd = parser.parse(options, args);
            this.init(cmd.getOptionValue("eventxml"), cmd.getOptionValue("outdir"), cmd.getOptionValue("template"));
        }
        catch (final ParseException e) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Event-ConfigTool", options);
        }
        catch (final Exception e2) {
            throw e2;
        }
    }
    
    private void init(final String eventxmlPath, String outPutDir, String templatePath) throws SAXException, IOException, Exception {
        final EventConfigParser parser = new EventConfigParser(false);
        parser.parseFromPojoConverter(new File(eventxmlPath));
        this.packageName = parser.getPackageName();
        if (outPutDir.lastIndexOf(File.separator) == outPutDir.length() - 1) {
            outPutDir = outPutDir.substring(0, outPutDir.length() - 1);
        }
        if (templatePath.lastIndexOf(File.separator) == templatePath.length() - 1) {
            templatePath = templatePath.substring(0, templatePath.length() - 1);
        }
        final String tempPath = templatePath + File.separator + "eventframework.vtl";
        this.generateJavaFile(tempPath, parser.getLogs(), EventFWConstants.TYPE.LOG, outPutDir);
        this.generateJavaFile(tempPath, parser.getEvents(), EventFWConstants.TYPE.EVENT, outPutDir);
    }
    
    private void generateJavaFile(final String templatePath, final Object mapObj, final EventFWConstants.TYPE eventType, String outPutDir) throws Exception {
        String packageName = this.packageName;
        if (mapObj instanceof Map) {
            final Map<String, Object> map = (Map<String, Object>)mapObj;
            if (map.size() > 0) {
                final EventJavaFileGenerator fileGenerator = new EventJavaFileGenerator(templatePath);
                packageName = packageName + "." + eventType.value().replace("-", "");
                final String packagePath = packageName.replaceAll("\\.", File.separator);
                outPutDir = outPutDir + File.separator + packagePath;
                final File dir = new File(outPutDir);
                dir.mkdirs();
                for (final String eventName : map.keySet()) {
                    final String key = eventName;
                    final String fileName = "ZSEC_" + eventName + ".java";
                    fileGenerator.merge(new FileOutputStream(new File(outPutDir + File.separator, fileName)), map.get(key), eventName, packageName, eventType.value());
                }
            }
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final EventConfigurationReader reader = new EventConfigurationReader(args);
    }
}
