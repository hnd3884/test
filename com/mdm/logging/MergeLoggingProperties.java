package com.mdm.logging;

import java.util.Iterator;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.LinkedHashMap;

public class MergeLoggingProperties
{
    public static void main(final String[] args) {
        try {
            final String productLoggingProperties = args[0];
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            final Properties properties = new Properties();
            properties.load(new FileInputStream(new File(productLoggingProperties)));
            StringBuilder handlers = new StringBuilder(properties.getProperty("handlers"));
            map = convertPropertiesintoMap(map, productLoggingProperties);
            for (int i = 1; i < args.length; ++i) {
                final String filePath = args[i];
                if (new File(filePath).exists()) {
                    final Properties tempProp = readProperties(filePath);
                    final String producthandlers = tempProp.getProperty("handlers");
                    handlers = handlers.append(producthandlers);
                    map = convertPropertiesintoMap(map, filePath);
                }
            }
            map.put("handlers", handlers.toString());
            writeMapintoFile(productLoggingProperties, map);
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static Properties readProperties(final String fileName) {
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(fileName).exists()) {
                ism = new FileInputStream(fileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
        return props;
    }
    
    private static LinkedHashMap<String, String> convertPropertiesintoMap(final LinkedHashMap<String, String> map, final String filepath) throws FileNotFoundException {
        final Scanner scanner = new Scanner(new FileReader(filepath));
        while (scanner.hasNext()) {
            String line = null;
            if (!(line = scanner.nextLine()).isEmpty() && line.contains("=")) {
                final String[] columns = line.split("=");
                if ("handlers".equalsIgnoreCase(columns[0].trim())) {
                    continue;
                }
                map.put(columns[0], columns[1]);
            }
        }
        return map;
    }
    
    private static void writeMapintoFile(final String productLoggingProperties, final LinkedHashMap<String, String> map) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(productLoggingProperties);
            for (final Map.Entry pair : map.entrySet()) {
                final String property = pair.getKey() + " = " + pair.getValue();
                fos.write(property.getBytes());
                fos.write("\n".getBytes());
            }
            fos.flush();
            fos.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            try {
                fos.close();
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            try {
                fos.close();
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }
}
