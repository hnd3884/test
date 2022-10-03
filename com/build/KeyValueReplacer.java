package com.build;

import java.util.stream.Stream;
import java.nio.file.Path;
import java.nio.file.OpenOption;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.nio.file.LinkOption;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class KeyValueReplacer
{
    private static final Logger LOGGER;
    
    public static void main(final String[] args) throws Exception {
        final String buildDir = args[0];
        final String actualDir = buildDir + File.separator + args[1];
        KeyValueReplacer.LOGGER.log(Level.INFO, "Actual Dir path for configuration: {0}", actualDir);
        final JSONArray inputObject = new JSONArray(new String(Files.readAllBytes(Paths.get(actualDir, new String[0]))));
        for (int index = 0; index < inputObject.length(); ++index) {
            final JSONObject inputData = inputObject.getJSONObject(index);
            final String sourcePath = buildDir + File.separator + inputData.getString("filePath");
            List<String> fileNames = Arrays.asList(new File(sourcePath).list());
            if (inputData.optString("fileName") != null && !inputData.optString("fileName").isEmpty()) {
                fileNames = Arrays.asList(inputData.getString("fileName").split(","));
            }
            List<String> excludeFiles = new ArrayList<String>();
            if (inputData.optString("excludes") != null && !inputData.optString("excludes").isEmpty()) {
                excludeFiles = Arrays.asList(inputData.getString("excludes").split(","));
            }
            for (final String fileName : fileNames) {
                if (excludeFiles.contains(fileName)) {
                    continue;
                }
                final String filePath = sourcePath + File.separator + fileName;
                final JSONArray replaceProperties = inputData.getJSONArray("replaceProperty");
                final Map<String, String> keyValuePair = new HashMap<String, String>();
                for (int propertyIndex = 0; propertyIndex < replaceProperties.length(); ++propertyIndex) {
                    final JSONObject replaceProperty = replaceProperties.getJSONObject(propertyIndex);
                    final String key = replaceProperty.getString("key");
                    final String value = replaceProperty.getString("value");
                    keyValuePair.put(key, value);
                }
                KeyValueReplacer.LOGGER.log(Level.INFO, "File path : {0} and Key value Pair : {1}", new Object[] { filePath, keyValuePair });
                findAndReplaceInFile(filePath, keyValuePair);
            }
        }
    }
    
    public static boolean findAndReplaceInFile(final String filePath, final Map<String, String> keyValuePair) throws Exception {
        if (Files.isDirectory(Paths.get(filePath, new String[0]), new LinkOption[0]) || !Files.exists(Paths.get(filePath, new String[0]), new LinkOption[0])) {
            System.out.println(filePath);
            return false;
        }
        for (final Map.Entry<String, String> replaceproperty : keyValuePair.entrySet()) {
            final String key = "(\\{" + replaceproperty.getKey() + "\\})";
            final String value = replaceproperty.getValue();
            final Path path = Paths.get(filePath, new String[0]);
            if (Files.readAllBytes(path).length > 0) {
                try (final Stream<String> lines = Files.lines(path)) {
                    final List<String> replaced = lines.map(line -> line.replaceAll(s, s2)).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
                    Files.write(path, replaced, new OpenOption[0]);
                }
            }
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(KeyValueReplacer.class.getName());
    }
}
