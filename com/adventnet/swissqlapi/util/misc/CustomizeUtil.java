package com.adventnet.swissqlapi.util.misc;

import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;

public class CustomizeUtil
{
    public static String objectNamesToQuotedIdentifier(String objectName, final String[] keywords, final ModifiedObjectAttr modifiedAttr, final int targetdb) {
        final String[] specChars = { "%", "+", "-", "!", "*", "~", ":", ";", "{", "}", ",", "^", "<", ">", "'", "|", "/", "\\" };
        if (SwisSQLAPI.enableObjectMapping && objectName != null) {
            checkAndResetObjectNameFromMapping(objectName);
        }
        if (objectName != null && (!objectName.startsWith("\"") || !objectName.endsWith("\"")) && (!objectName.startsWith("'") || !objectName.endsWith("'")) && !objectName.startsWith(":")) {
            int i = 0;
            while (i < specChars.length) {
                if ((objectName.indexOf(specChars[i]) != -1 || isStartsWithNum(objectName)) && objectName.startsWith("[")) {
                    objectName = "\"" + objectName.trim().substring(1, objectName.length() - 1) + "\"";
                    if (modifiedAttr != null) {
                        modifiedAttr.setModifiedType(2);
                        break;
                    }
                    break;
                }
                else if (objectName.indexOf(specChars[i]) != -1 || isStartsWithNum(objectName)) {
                    objectName = "\"" + objectName + "\"";
                    if (modifiedAttr != null) {
                        modifiedAttr.setModifiedType(2);
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
            if (!objectName.startsWith("\"") && keywords != null) {
                for (i = 0; i < keywords.length; ++i) {
                    if (objectName.startsWith("[") || objectName.startsWith("`")) {
                        if (keywords[i].trim().equalsIgnoreCase(objectName.trim().substring(1, objectName.length() - 1))) {
                            if (!SwisSQLOptions.doNotModifyVariableName) {
                                if (targetdb == 1) {
                                    if (SwisSQLAPI.quotedOracleIdentifier) {
                                        objectName = "\"" + objectName.trim().substring(1, objectName.length() - 1) + "\"";
                                    }
                                    else {
                                        objectName = objectName.trim().substring(1, objectName.length() - 1) + "_";
                                    }
                                }
                                else {
                                    objectName = "\"" + objectName.trim().substring(1, objectName.length() - 1) + "\"";
                                }
                            }
                            if (modifiedAttr != null) {
                                modifiedAttr.setModifiedType(3);
                                break;
                            }
                            break;
                        }
                    }
                    else if (keywords[i].trim().equalsIgnoreCase(objectName.trim()) && !SwisSQLOptions.doNotModifyVariableName) {
                        if (targetdb == 1) {
                            if (SwisSQLAPI.quotedOracleIdentifier) {
                                objectName = "\"" + objectName + "\"";
                            }
                            else {
                                objectName += "_";
                            }
                        }
                        else if (targetdb == 4) {
                            objectName = objectName;
                        }
                        else {
                            objectName = "\"" + objectName + "\"";
                        }
                        if (modifiedAttr != null) {
                            modifiedAttr.setModifiedType(3);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        if (objectName != null && objectName.startsWith("\"") && objectName.endsWith("\"") && targetdb == 11) {
            boolean isKeyword = false;
            for (int j = 0; j < keywords.length; ++j) {
                if (keywords[j].trim().equalsIgnoreCase(objectName.trim().substring(1, objectName.length() - 1))) {
                    isKeyword = true;
                    break;
                }
            }
            if (SwisSQLOptions.setObjectNamesToLowerCase) {
                if ((StringFunctions.isUpperCase(objectName.substring(1, objectName.length() - 1)) || StringFunctions.isLowerCase(objectName.substring(1, objectName.length() - 1))) && !isKeyword && objectName.substring(1, objectName.length() - 1).trim().length() > 0) {
                    objectName = objectName.substring(1, objectName.length() - 1).toLowerCase();
                }
            }
            else if (SwisSQLOptions.setObjectNamesToUpperCase && (StringFunctions.isUpperCase(objectName.substring(1, objectName.length() - 1)) || StringFunctions.isLowerCase(objectName.substring(1, objectName.length() - 1))) && !isKeyword && objectName.substring(1, objectName.length() - 1).trim().length() > 0) {
                objectName = objectName.substring(1, objectName.length() - 1).toUpperCase();
            }
        }
        return objectName;
    }
    
    public static String objectNamesToBracedIdentifier(String objectName, final String[] keywords, final ModifiedObjectAttr modifiedAttr) {
        final String[] specChars = { "%", "+", "-", "!", "*", "~", ":", ";", "{", "}", ",", "^", "<", ">", "'", "|" };
        if (SwisSQLAPI.enableObjectMapping && objectName != null) {
            checkAndResetObjectNameFromMapping(objectName);
        }
        if (objectName != null && (!objectName.startsWith("[") || !objectName.endsWith("]")) && (!objectName.startsWith("'") || !objectName.endsWith("'")) && !objectName.startsWith(":")) {
            int i = 0;
            while (i < specChars.length) {
                if ((objectName.indexOf(specChars[i]) != -1 || isStartsWithNum(objectName)) && objectName.startsWith("\"")) {
                    objectName = "[" + objectName.trim().substring(1, objectName.length() - 1) + "]";
                    if (modifiedAttr != null) {
                        modifiedAttr.setModifiedType(2);
                        break;
                    }
                    break;
                }
                else if (objectName.indexOf(specChars[i]) != -1 || isStartsWithNum(objectName)) {
                    objectName = "[" + objectName + "]";
                    if (modifiedAttr != null) {
                        modifiedAttr.setModifiedType(2);
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
            if (!objectName.startsWith("[") && keywords != null) {
                for (i = 0; i < keywords.length; ++i) {
                    if (objectName.startsWith("\"") || objectName.startsWith("`")) {
                        if (keywords[i].trim().equalsIgnoreCase(objectName.trim().substring(1, objectName.length() - 1))) {
                            objectName = "[" + objectName.trim().substring(1, objectName.length() - 1) + "]";
                            if (modifiedAttr != null) {
                                modifiedAttr.setModifiedType(3);
                                break;
                            }
                            break;
                        }
                    }
                    else if (keywords[i].trim().equalsIgnoreCase(objectName.trim())) {
                        objectName = "[" + objectName + "]";
                        if (modifiedAttr != null && modifiedAttr != null) {
                            modifiedAttr.setModifiedType(3);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        return objectName;
    }
    
    public static boolean isStartsWithNum(String objectName) {
        for (int i = 0; i < 10; ++i) {
            if (objectName.startsWith("[") || objectName.startsWith("\"")) {
                objectName = objectName.substring(1, objectName.length() - 1);
            }
            if (objectName.startsWith(new String(i + ""))) {
                return true;
            }
        }
        return false;
    }
    
    private static void checkAndResetObjectNameFromMapping(String objectName) {
        String objectNameToCheckMapping = "";
        if ((objectName.startsWith("[") && objectName.endsWith("]")) || (objectName.startsWith("'") && objectName.endsWith("'")) || (objectName.startsWith("\"") && objectName.endsWith("\"")) || (objectName.startsWith("`") && objectName.endsWith("`"))) {
            objectNameToCheckMapping = objectName.trim().substring(1, objectName.length() - 1);
        }
        else if (objectName.startsWith(":")) {
            objectNameToCheckMapping = objectName.trim().substring(1, objectName.length());
        }
        else {
            objectNameToCheckMapping = objectName.trim();
        }
        final String targetObjName = SwisSQLUtils.getObjectNameFromMapping(objectNameToCheckMapping);
        if (targetObjName != null) {
            objectName = objectName.replaceAll(objectNameToCheckMapping, targetObjName);
        }
    }
    
    public static boolean compareQuotedIdentifiers(String identifier, String comparedIdentifier, final boolean ignoreCase) {
        if (identifier != null && (identifier.startsWith("`") || identifier.startsWith("\"") || identifier.startsWith("["))) {
            identifier = identifier.substring(1, identifier.length() - 1);
        }
        if (comparedIdentifier != null && (comparedIdentifier.startsWith("`") || comparedIdentifier.startsWith("\"") || comparedIdentifier.startsWith("["))) {
            comparedIdentifier = comparedIdentifier.substring(1, comparedIdentifier.length() - 1);
        }
        if (ignoreCase) {
            return identifier.equalsIgnoreCase(comparedIdentifier);
        }
        return identifier.equals(comparedIdentifier);
    }
}
