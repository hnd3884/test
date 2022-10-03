package com.adventnet.sym.server.mdm.apps.android.apkextractor;

import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import org.json.JSONArray;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.logging.Logger;

public class ApkResourceParser
{
    private String restrictionsFilePathId;
    private String resourcesFilePath;
    private String androidManifestPath;
    private static Logger logger;
    
    public ApkResourceParser(final String folderPath) {
        this.restrictionsFilePathId = null;
        this.resourcesFilePath = null;
        this.androidManifestPath = null;
        this.androidManifestPath = folderPath + File.separator + "AndroidManifest.txt";
        this.resourcesFilePath = folderPath + File.separator + "resources.txt";
    }
    
    public boolean isArrayTypeResource(final String resourceId) throws IOException {
        String line = null;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(this.resourcesFilePath);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(resourceId) && line.contains("array/")) {
                    return true;
                }
            }
        }
        finally {
            if (fileReader != null) {
                fileReader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        fileReader.close();
        bufferedReader.close();
        return false;
    }
    
    public JSONArray getArrayResource(final String resourceId) throws IOException {
        String line = null;
        final JSONArray array = new JSONArray();
        final Pattern countPattern = Pattern.compile("Count=([0-9]+)");
        final Pattern entryPattern = Pattern.compile("\\(([a-z0-9]+)\\) (.+)");
        final Pattern resourceReferencePattern = Pattern.compile("\\(reference\\) ([0-9a-fA-FxX()]+)");
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(this.resourcesFilePath);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(resourceId)) {
                    final String nextLine = bufferedReader.readLine();
                    if (nextLine.contains("Parent")) {
                        final Matcher m = countPattern.matcher(nextLine);
                        m.find();
                        for (int arraySize = Integer.parseInt(m.group(1)), i = 0; i < arraySize; ++i) {
                            String entryLine = bufferedReader.readLine();
                            entryLine = entryLine.replaceAll("\"", "");
                            final Matcher resourceReferenceMatcher = resourceReferencePattern.matcher(entryLine);
                            final Matcher m2 = entryPattern.matcher(entryLine);
                            if (resourceReferenceMatcher.find()) {
                                final String resourceId2 = resourceReferenceMatcher.group(1);
                                final String value = String.valueOf(this.getResourceForAllType(resourceId2));
                                array.put((Object)value);
                            }
                            else if (m2.find()) {
                                final String value2 = m2.group(2);
                                array.put((Object)value2);
                            }
                        }
                        break;
                    }
                    continue;
                }
            }
        }
        catch (final FileNotFoundException e) {
            ApkResourceParser.logger.severe("Could not find file : " + this.resourcesFilePath);
        }
        catch (final IOException e2) {
            ApkResourceParser.logger.severe("Error reading file");
        }
        finally {
            if (fileReader != null) {
                fileReader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return array;
    }
    
    public String getStringResource(final String resourceId) throws IOException {
        return this.getStringResource(resourceId, true);
    }
    
    public String getStringResource(final String resourceId, final boolean getFirst) throws IOException {
        String line = null;
        String resourceString = null;
        final Pattern p = Pattern.compile("[ a-z0-9()]+\"(.*)\"");
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(this.resourcesFilePath);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(resourceId)) {
                    final String temp = bufferedReader.readLine();
                    final Matcher m = p.matcher(temp);
                    if (!m.find()) {
                        continue;
                    }
                    resourceString = m.group(1);
                    if (getFirst) {
                        break;
                    }
                    continue;
                }
            }
        }
        catch (final FileNotFoundException e) {
            ApkResourceParser.logger.severe("Could not find file : " + this.resourcesFilePath);
        }
        catch (final IOException e2) {
            ApkResourceParser.logger.severe("Error reading file");
        }
        finally {
            if (fileReader != null) {
                fileReader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return resourceString;
    }
    
    public Object getResourceForAllType(final String resourceId) throws IOException {
        String line = null;
        final Pattern resourceTypePattern = Pattern.compile("spec resource [0-9a-fA-FxX()]+.+:(.+)/.+");
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String type = null;
        Object resourceValue = new Object();
        try {
            fileReader = new FileReader(this.resourcesFilePath);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(resourceId)) {
                    final Matcher m = resourceTypePattern.matcher(line);
                    if (m.find()) {
                        type = m.group(1);
                        break;
                    }
                    continue;
                }
            }
            if (type.equals("bool")) {
                resourceValue = this.getBooleanResource(bufferedReader, resourceId);
            }
            else if (type.equals("string")) {
                resourceValue = this.getStringResource(bufferedReader, resourceId);
            }
            if (type.equals("integer")) {
                resourceValue = this.getIntegerResource(bufferedReader, resourceId);
            }
        }
        catch (final FileNotFoundException e) {
            ApkResourceParser.logger.severe("Could not find file : " + this.resourcesFilePath);
        }
        catch (final IOException e2) {
            ApkResourceParser.logger.severe("Error reading file");
        }
        finally {
            if (fileReader != null) {
                fileReader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return resourceValue;
    }
    
    public Object getIntegerResource(final BufferedReader bufferedReader, final String resourceId) {
        final String integerValue = this.getBooleanOrInteger(bufferedReader, resourceId);
        final Long decimalWithoutTwoSComplement = Long.valueOf(integerValue, 16);
        final Pattern p = Pattern.compile("^[0-7f]");
        final Matcher m = p.matcher(integerValue);
        if (integerValue.length() <= 4 && m.find()) {
            return decimalWithoutTwoSComplement.shortValue();
        }
        return decimalWithoutTwoSComplement.intValue();
    }
    
    public Boolean getBooleanResource(final BufferedReader bufferedReader, final String resourceId) {
        return this.getBooleanOrInteger(bufferedReader, resourceId).equals("ffffffff");
    }
    
    private String getBooleanOrInteger(final BufferedReader bufferedReader, final String resourceId) {
        String resourceString = null;
        String line = null;
        final Pattern resourceTypePattern = Pattern.compile(".+d=0x(.+) \\(");
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(resourceId)) {
                    final Matcher m = resourceTypePattern.matcher(line);
                    if (m.find()) {
                        resourceString = m.group(1);
                        break;
                    }
                    continue;
                }
            }
        }
        catch (final FileNotFoundException e) {
            ApkResourceParser.logger.severe("Could not find file : " + this.resourcesFilePath);
        }
        catch (final IOException e2) {
            ApkResourceParser.logger.severe("Error reading file");
        }
        return resourceString;
    }
    
    private String getStringResource(final BufferedReader bufferedReader, final String resourceId) throws IOException {
        String line = null;
        String resourceString = null;
        final Pattern p = Pattern.compile("[ a-z0-9()]+\"(.*)\"");
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(resourceId)) {
                    final String temp = bufferedReader.readLine();
                    final Matcher m = p.matcher(temp);
                    if (m.find()) {
                        resourceString = m.group(1);
                        break;
                    }
                    continue;
                }
            }
        }
        catch (final IOException e) {
            ApkResourceParser.logger.severe("Error reading file");
        }
        return resourceString;
    }
    
    public void findRestrictions() {
        String restrictionsFilePathId = null;
        String line = null;
        try {
            final FileReader fileReader = new FileReader(this.androidManifestPath);
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.matches(".*android.content.APP_RESTRICTIONS.*")) {
                    final String temp = bufferedReader.readLine();
                    final Pattern p = Pattern.compile("A:\\s.*@([0-9a-fA-Fx]+)");
                    final Matcher m = p.matcher(temp);
                    if (m.find()) {
                        restrictionsFilePathId = m.group(1);
                        break;
                    }
                    break;
                }
            }
            fileReader.close();
            bufferedReader.close();
        }
        catch (final FileNotFoundException ex) {
            ApkResourceParser.logger.severe("Unable to open file '" + this.androidManifestPath + "'");
        }
        catch (final IOException ex2) {
            ApkResourceParser.logger.severe("Error reading file '" + this.androidManifestPath + "'");
        }
        this.restrictionsFilePathId = restrictionsFilePathId;
    }
    
    public String getRestrictionsFilePathId() {
        return this.restrictionsFilePathId;
    }
    
    public String getAndroidManifestPath() {
        return this.androidManifestPath;
    }
    
    public boolean hasRestrictions() {
        return this.restrictionsFilePathId != null;
    }
    
    static {
        ApkResourceParser.logger = ApkExtractionUtilities.getLogger();
    }
}
