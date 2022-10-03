package com.adventnet.persistence.personality;

import com.zoho.conf.Configuration;
import java.util.Map;
import com.adventnet.ds.query.SelectQuery;
import java.util.LinkedHashMap;
import com.adventnet.persistence.DataObject;
import java.net.URL;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.persistence.personality.internal.PCInfo;

public class PersonalityConfigurationUtil
{
    private static PCInfo pcInfo;
    private static String pcInfoClass;
    
    private PersonalityConfigurationUtil() {
    }
    
    public static List<String> getConstituentTables(final String personalityName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getConstituentTables(personalityName);
    }
    
    public static List<String> getConstituentTables(final List<String> personalityNames) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getConstituentTables(personalityNames);
    }
    
    public static List<String> getContainedPersonalities(final String tableName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getContainedPersonalities(tableName);
    }
    
    public static List<String> getPersonalities(final List<String> tableNames) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getPersonalities(tableNames);
    }
    
    public static List<String> getDominantPersonalities(final List<String> tableNames) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getDominantPersonalities(tableNames);
    }
    
    public static String getDominantTableForPersonality(final String personalityName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getDominantTableForPersonality(personalityName);
    }
    
    public static DataObject initializePersonalityConfiguration(final String moduleName, final URL url) throws DataAccessException {
        DataObject dataObject = null;
        synchronized (PersonalityConfigurationUtil.pcInfo) {
            final PCInfo newPCInfo = (PCInfo)PersonalityConfigurationUtil.pcInfo.clone();
            dataObject = newPCInfo.initializePersonalityConfiguration(moduleName, url);
            PersonalityConfigurationUtil.pcInfo = newPCInfo;
        }
        return dataObject;
    }
    
    public static void addPersonalities(final String moduleName, final DataObject personalityDO) throws DataAccessException {
        final PCInfo newPCInfo = (PCInfo)PersonalityConfigurationUtil.pcInfo.clone();
        newPCInfo.addPersonalities(moduleName, personalityDO);
        PersonalityConfigurationUtil.pcInfo = newPCInfo;
    }
    
    public static boolean removePersonality(final String personalityName, final boolean deleteFromDB) throws DataAccessException {
        final PCInfo newPCInfo = (PCInfo)PersonalityConfigurationUtil.pcInfo.clone();
        final boolean result = newPCInfo.removePersonality(personalityName, deleteFromDB);
        PersonalityConfigurationUtil.pcInfo = newPCInfo;
        return result;
    }
    
    public static void removePersonalityConfiguration(final String moduleName) throws DataAccessException {
        removePersonalityConfiguration(moduleName, true);
    }
    
    public static void removePersonalityConfiguration(final String moduleName, final boolean deleteFromDB) throws DataAccessException {
        final PCInfo newPCInfo = (PCInfo)PersonalityConfigurationUtil.pcInfo.clone();
        newPCInfo.removePersonalityConfiguration(moduleName, deleteFromDB);
        PersonalityConfigurationUtil.pcInfo = newPCInfo;
    }
    
    public static boolean isIndexed(final String tableName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.isIndexed(tableName);
    }
    
    public static List<String> getPersonalityNames(final String moduleName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getPersonalityNames(moduleName);
    }
    
    public static LinkedHashMap getSelectQueryTemplates(final String moduleName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getSelectQueryTemplates(moduleName);
    }
    
    public static boolean isPartOfPersonality(final String tableName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.isPartOfPersonality(tableName);
    }
    
    public static boolean areAllPersonalitiesNotIndexed(final List<String> deepRetrievedPersonalities) throws DataAccessException {
        if (deepRetrievedPersonalities != null) {
            for (int i = 0; i < deepRetrievedPersonalities.size(); ++i) {
                final String persName = deepRetrievedPersonalities.get(i);
                if (persName != null) {
                    final String dominantTable = getDominantTableForPersonality(persName);
                    if (isIndexed(dominantTable)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    @Deprecated
    public static SelectQuery getSelectQuery(final String personalityName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getSelectQuery(personalityName);
    }
    
    public static String getDominantTable(final String tableName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getDominantTable(tableName);
    }
    
    public static boolean isFKPartOfPersonality(final String foreignKeyName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.isFKPartOfPersonality(foreignKeyName);
    }
    
    public static List<String> getMandatoryConstituentTables(final String personalityName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getMandatoryConstituentTables(personalityName);
    }
    
    public static Map<String, String> getFKsForConstituentTables(final String personalityName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.getFKsForConstituentTables(personalityName);
    }
    
    public static boolean isPartOfIndexedPersonality(final String tableName) throws DataAccessException {
        return PersonalityConfigurationUtil.pcInfo.isPartOfIndexedPersonality(tableName);
    }
    
    public static void unloadAllPersonalities() {
        PersonalityConfigurationUtil.pcInfo = getNewPCInfoInstance();
    }
    
    private static PCInfo getNewPCInfoInstance() {
        try {
            return PersonalityConfigurationUtil.pcInfo = (PCInfo)Class.forName(PersonalityConfigurationUtil.pcInfoClass).newInstance();
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception occurred while instantiating the PCInfo class", e);
        }
    }
    
    static {
        PersonalityConfigurationUtil.pcInfo = null;
        PersonalityConfigurationUtil.pcInfoClass = Configuration.getString("PCInfo", "com.adventnet.persistence.personality.internal.LocalPCInfo");
        try {
            PersonalityConfigurationUtil.pcInfo = (PCInfo)Class.forName(PersonalityConfigurationUtil.pcInfoClass).newInstance();
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception occurred while instantiating the PCInfo class", e);
        }
    }
}
