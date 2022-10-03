package com.zoho.security.zsecpiidetector;

import java.util.Arrays;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import org.w3c.dom.Element;
import com.zoho.security.zsecpiidetector.types.PIIType;
import com.zoho.security.zsecpiidetector.types.PIIEnum;
import java.util.List;

public class PIIClassifier
{
    public static final List<PIIEnum.Category> DEFAULT_CATEGORY;
    public static final List<PIIEnum.Sensitivity> DEFAULT_SENSITIVITY;
    public static final List<PIIEnum.DetectionType> DEFAULT_DETECTION_TYPE;
    private List<PIIEnum.Category> category;
    private List<PIIEnum.Sensitivity> sensitivity;
    private List<PIIEnum.DetectionType> detectionType;
    private List<PIIType> piiDataDefault;
    private List<PIIType> regexList;
    private List<PIIType> regexAndDictionaryList;
    private List<PIIType> dictionaryList;
    private List<PIIType> mlList;
    
    public PIIClassifier() {
        this(PIIClassifier.DEFAULT_CATEGORY, PIIClassifier.DEFAULT_SENSITIVITY, PIIClassifier.DEFAULT_DETECTION_TYPE);
    }
    
    public PIIClassifier(final List<PIIEnum.Category> category, final List<PIIEnum.Sensitivity> sensitivity, final List<PIIEnum.DetectionType> detectionTypes) {
        this.category = category;
        this.sensitivity = sensitivity;
        this.detectionType = detectionTypes;
    }
    
    public PIIClassifier(final Element categories, final Element sensitivities, final Element detectionTypes) {
        List<PIIEnum.Category> listCategory = null;
        List<PIIEnum.Sensitivity> listSensitivity = null;
        final List<PIIEnum.DetectionType> listDetectionType = null;
        for (final Element catElement : PIIUtil.getChildNodesByTagName(categories, "category")) {
            listCategory = ((listCategory != null) ? listCategory : new ArrayList<PIIEnum.Category>());
            listCategory.add(PIIEnum.Category.valueOf(catElement.getAttribute("value").toUpperCase()));
        }
        for (final Element sensElement : PIIUtil.getChildNodesByTagName(sensitivities, "sensitivity")) {
            listSensitivity = ((listSensitivity != null) ? listSensitivity : new ArrayList<PIIEnum.Sensitivity>());
            listSensitivity.add(PIIEnum.Sensitivity.valueOf(sensElement.getAttribute("value").toUpperCase()));
        }
        this.category = listCategory;
        this.sensitivity = listSensitivity;
        this.detectionType = listDetectionType;
    }
    
    public List<PIIType> piiInfoClassifierQuery(List<PIIEnum.Category> category, List<PIIEnum.Sensitivity> sensitivity, List<PIIEnum.DetectionType> detectionType) {
        final List<PIIType> listPII = new ArrayList<PIIType>();
        category = ((category == null) ? PIIEnum.Category.getAllCategory() : category);
        sensitivity = ((sensitivity == null) ? PIIEnum.Sensitivity.getAllSensitivities() : sensitivity);
        detectionType = ((detectionType == null) ? PIIEnum.DetectionType.getAllDetectionTypes() : detectionType);
        final Map<String, List<PIIType>> piiInfoLookUpMap = PIIDetectorFactory.getPiiInfoList();
        for (final PIIEnum.Category cat : category) {
            for (final PIIEnum.Sensitivity sens : sensitivity) {
                for (final PIIEnum.DetectionType type : detectionType) {
                    final String uniqueKey = PIIUtil.createUniqueKeyFromStrings(cat.toString(), sens.toString(), type.toString());
                    if (piiInfoLookUpMap.get(uniqueKey) != null) {
                        listPII.addAll(piiInfoLookUpMap.get(uniqueKey));
                    }
                }
            }
        }
        return Collections.unmodifiableList((List<? extends PIIType>)listPII);
    }
    
    public List<PIIType> getClassifiedPiiInfo() {
        return (this.piiDataDefault == null) ? (this.piiDataDefault = this.piiInfoClassifierQuery(this.category, this.sensitivity, this.detectionType)) : this.piiDataDefault;
    }
    
    public List<PIIType> classifiedPiiInfoBasedML() {
        return (this.mlList == null) ? (this.mlList = this.piiInfoClassifierQuery(this.category, this.sensitivity, Arrays.asList(PIIEnum.DetectionType.MACHINE_LEARNING))) : this.mlList;
    }
    
    public List<PIIType> classifiedPiiInfoBasedRegex() {
        return (this.regexList == null) ? (this.regexList = this.piiInfoClassifierQuery(this.category, this.sensitivity, Arrays.asList(PIIEnum.DetectionType.REGEX))) : this.regexList;
    }
    
    public List<PIIType> classifiedPiiInfoBasedRegexAndDictionary() {
        return (this.regexAndDictionaryList == null) ? (this.regexAndDictionaryList = this.piiInfoClassifierQuery(this.category, this.sensitivity, Arrays.asList(PIIEnum.DetectionType.REGEX_AND_DICTIONARY))) : this.regexAndDictionaryList;
    }
    
    public List<PIIType> classifiedPiiInfoBasedDictionary() {
        return (this.dictionaryList == null) ? (this.dictionaryList = this.piiInfoClassifierQuery(this.category, this.sensitivity, Arrays.asList(PIIEnum.DetectionType.DICTIONARY))) : this.dictionaryList;
    }
    
    public List<PIIEnum.Category> getCategory() {
        return this.category;
    }
    
    public List<PIIEnum.Sensitivity> getSensitivity() {
        return this.sensitivity;
    }
    
    public List<PIIEnum.DetectionType> getDetectionType() {
        return this.detectionType;
    }
    
    static {
        DEFAULT_CATEGORY = Arrays.asList(PIIEnum.Category.PERSONAL, PIIEnum.Category.IDENTITY, PIIEnum.Category.AUTHENTICATION_AND_AUTHORIZATION, PIIEnum.Category.FINANCIAL, PIIEnum.Category.ACCOUNT, PIIEnum.Category.ZOHOPII, PIIEnum.Category.DEVICE_INFORMATION, PIIEnum.Category.INTERNET_AND_TELECOMMUNICATION);
        DEFAULT_SENSITIVITY = Arrays.asList(PIIEnum.Sensitivity.HIGH, PIIEnum.Sensitivity.MEDIUM);
        DEFAULT_DETECTION_TYPE = null;
    }
}
