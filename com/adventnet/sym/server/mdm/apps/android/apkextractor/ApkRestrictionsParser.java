package com.adventnet.sym.server.mdm.apps.android.apkextractor;

import java.util.regex.Matcher;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.json.JSONArray;
import java.util.logging.Logger;

public class ApkRestrictionsParser
{
    private static Logger logger;
    private String[] restrictionTypes;
    private JSONArray restrictionsArray;
    private String restrictionsFilePath;
    private String folderPath;
    
    public ApkRestrictionsParser(final String restrictionsFilePath, final String folderPath) {
        this.restrictionTypes = new String[] { "null", "bool", "choice", "hidden", "multiselect", "integer", "string", "bundle", "bundle_array" };
        this.restrictionsArray = new JSONArray();
        this.restrictionsFilePath = null;
        this.folderPath = null;
        this.restrictionsFilePath = restrictionsFilePath;
        this.folderPath = folderPath;
    }
    
    public void parse() throws FileNotFoundException, IOException, JSONException {
        final FileReader fileReader = new FileReader(this.restrictionsFilePath);
        final BufferedReader bufferedReader = new BufferedReader(fileReader);
        this.restrictionsArray = this.formJSONArray(bufferedReader);
        fileReader.close();
        bufferedReader.close();
    }
    
    public int getInitialElementPosition(final BufferedReader bufferedReader) throws IOException {
        bufferedReader.mark(1000);
        String line;
        for (line = bufferedReader.readLine(); !line.contains("E: restrictions "); line = bufferedReader.readLine()) {}
        final int position = line.indexOf("E: restrictions ");
        bufferedReader.reset();
        return position;
    }
    
    public JSONArray formJSONArray(final BufferedReader bufferedReader) throws IOException, JSONException {
        return this.getJsonForRestriction(bufferedReader, this.getInitialElementPosition(bufferedReader));
    }
    
    public JSONArray getJsonForRestriction(final BufferedReader bufferedReader, final int elementposition) throws IOException, JSONException {
        final int BUFFER_SIZE = 1000;
        String line = null;
        final JSONArray jsonArray = new JSONArray();
        final ApkResourceParser resourceParser = new ApkResourceParser(this.folderPath);
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("E: restriction ")) {
                if (line.indexOf("E: restriction ") <= elementposition) {
                    break;
                }
                final int eposition = line.indexOf("E: restriction ");
                final JSONObject restriction = new JSONObject();
                bufferedReader.mark(BUFFER_SIZE);
                String restrictionType = null;
                JSONObject defaultValue = null;
                while ((line = bufferedReader.readLine()) != null && line.contains("A:")) {
                    final Pattern p1 = Pattern.compile("[ ]+A:.+android:([a-zA-Z]+)[0-9a-fA-FxX()]+=@([0-9a-fA-FxX]*)");
                    final Pattern p2 = Pattern.compile(".+android:([a-zA-Z]+)[0-9a-fA-FxX()]+.+Raw: \"(.+)\"");
                    final Pattern p3 = Pattern.compile("[ ]+A: [a-z:/\\.]*android:([a-zA-Z]+)[ 0-9a-fA-FxX()]+=\\(type 0x([0-9]+)\\)0x([a-fA-F0-9]*)");
                    final Pattern p4 = Pattern.compile(".+android:([a-zA-Z]+)[0-9a-fA-FxX()]+=\"\".+\\(Raw: (\"\")\\)");
                    final Matcher m1 = p1.matcher(line);
                    final Matcher m2 = p2.matcher(line);
                    final Matcher m3 = p3.matcher(line);
                    final Matcher m4 = p4.matcher(line);
                    if (m1.find()) {
                        final String attribute = m1.group(1);
                        final String attributeResourceId = m1.group(2);
                        final Object attributeValue = resourceParser.getResourceForAllType(attributeResourceId);
                        final boolean isArrayTypeResource = resourceParser.isArrayTypeResource(attributeResourceId);
                        if (!isArrayTypeResource) {
                            if (attribute.equals("defaultValue")) {
                                defaultValue = new JSONObject();
                                defaultValue.put("value", attributeValue);
                            }
                            else {
                                restriction.put(attribute, attributeValue);
                            }
                        }
                        else {
                            final JSONArray entries = resourceParser.getArrayResource(attributeResourceId);
                            if (attribute.equals("defaultValue")) {
                                final JSONObject temp = new JSONObject();
                                temp.put("type", (Object)"multiselect");
                                temp.put("valueMultiselect", (Object)entries);
                                restriction.put(attribute, (Object)temp);
                            }
                            else if (attribute.equals("entries")) {
                                restriction.put("entry", (Object)entries);
                            }
                            else if (attribute.equals("entryValues")) {
                                restriction.put("entryValue", (Object)entries);
                            }
                            else {
                                restriction.put(attribute, (Object)entries);
                            }
                        }
                    }
                    else if (m2.find()) {
                        final String attribute = m2.group(1);
                        final String attributeValue2 = m2.group(2);
                        if (attribute.equals("defaultValue")) {
                            defaultValue = new JSONObject();
                            defaultValue.put("value", (Object)attributeValue2);
                        }
                        else {
                            restriction.put(attribute, (Object)attributeValue2);
                        }
                    }
                    else if (m3.find()) {
                        final String attribute = m3.group(1);
                        final String attributeType = m3.group(2);
                        final String attributeValue3 = m3.group(3);
                        if (attribute.equals("restrictionType")) {
                            restrictionType = this.restrictionTypes[Integer.parseInt(attributeValue3, 16)];
                            if (restrictionType == "null") {
                                restrictionType = "hidden";
                            }
                            if (restrictionType.equals("bundle_array") || restrictionType.equals("bundle")) {
                                bufferedReader.reset();
                                restriction.put("nestedRestriction", (Object)this.getJsonForRestriction(bufferedReader, eposition));
                                bufferedReader.reset();
                            }
                            if (restrictionType.equals("bundle_array")) {
                                restriction.put(attribute, (Object)"bundleArray");
                            }
                            else {
                                restriction.put(attribute, (Object)restrictionType);
                            }
                        }
                        else {
                            defaultValue = new JSONObject();
                            final String s = attributeType;
                            switch (s) {
                                case "03": {
                                    defaultValue.put("value", (Object)attributeValue3);
                                    break;
                                }
                                case "12": {
                                    defaultValue.put("value", attributeValue3.equals("ffffffff"));
                                    break;
                                }
                                case "10": {
                                    final Long decimalWithoutTwoSComplement = Long.valueOf(attributeValue3, 16);
                                    final Pattern p5 = Pattern.compile("^[0-7f]");
                                    final Matcher i = p5.matcher(attributeValue3);
                                    if (attributeValue3.length() <= 4 && i.find()) {
                                        defaultValue.put("value", (Object)(long)decimalWithoutTwoSComplement.shortValue());
                                        break;
                                    }
                                    defaultValue.put("value", (Object)(long)decimalWithoutTwoSComplement.intValue());
                                    break;
                                }
                                case "00": {
                                    defaultValue.put("value", (Object)null);
                                    break;
                                }
                                case "11": {
                                    defaultValue.put("value", (Object)attributeValue3);
                                    break;
                                }
                                default: {
                                    ApkRestrictionsParser.logger.log(Level.WARNING, "Couldnt find default value");
                                    break;
                                }
                            }
                        }
                    }
                    else if (m4.find()) {
                        final String attribute = m4.group(1);
                        final String attributeValue2 = m4.group(2);
                        if (attribute.equals("defaultValue")) {
                            defaultValue = new JSONObject();
                            defaultValue.put("value", (Object)"");
                        }
                    }
                    else {
                        ApkRestrictionsParser.logger.log(Level.WARNING, "couldnt match{0}", line);
                    }
                    bufferedReader.mark(BUFFER_SIZE);
                }
                if (defaultValue != null) {
                    final Object temp2 = defaultValue.get("value");
                    defaultValue.remove("value");
                    defaultValue.put("type", (Object)restrictionType);
                    if (restrictionType.equals("string") || restrictionType.equals("choice") || restrictionType.equals("hidden")) {
                        defaultValue.put("valueString", (Object)String.valueOf(temp2));
                    }
                    else if (restrictionType.equals("integer")) {
                        defaultValue.put("valueInteger", temp2);
                    }
                    else if (restrictionType.equals("bool")) {
                        defaultValue.put("valueBool", temp2);
                    }
                    restriction.put("defaultValue", (Object)defaultValue);
                }
                jsonArray.put((Object)restriction);
                bufferedReader.reset();
            }
        }
        return jsonArray;
    }
    
    public JSONArray getRestrictionsArray() {
        return this.restrictionsArray;
    }
    
    public String getRestrictionsFilePath() {
        return this.restrictionsFilePath;
    }
    
    static {
        ApkRestrictionsParser.logger = ApkExtractionUtilities.getLogger();
    }
}
