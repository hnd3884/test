package com.zoho.security.zsecpiidetector.finder;

import org.json.JSONArray;
import com.zlabs.pii.utils.NLPUtils;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import com.zlabs.pii.PII;
import java.util.Iterator;
import java.util.Set;
import com.zoho.security.zsecpiidetector.types.PIIEnum;
import com.zoho.security.zsecpiidetector.PIIUtil;
import com.zlabs.pii.utils.PIIUtils;
import java.util.ArrayList;
import opennlp.tools.util.Span;
import com.zoho.security.zsecpiidetector.PIIResult;
import org.json.JSONObject;
import com.zoho.security.zsecpiidetector.PIIScoreFilter;
import com.zoho.security.zsecpiidetector.types.PIIType;
import java.util.List;
import java.util.logging.Logger;

public class ExtendedPIIQuery
{
    private static final Logger LOGGER;
    
    public static List<Span> findPII(final String content, final List<PIIType> classifiedPiiInfo, final PIIScoreFilter filter, final JSONObject resultJson, final PIIResult.InfoFormat infoFormat) {
        final List<Span> listOfSpan = new ArrayList<Span>();
        final Set<String> piiMap = PIIUtils.piiMap.keySet();
        final String[] data = PIIUtil.tokenize(content);
        for (final PIIType piiInfo : classifiedPiiInfo) {
            final PIIEnum.DetectionType detectionType = piiInfo.getDetectionType();
            final String piiKey = piiInfo.getPiiType();
            if ((detectionType == PIIEnum.DetectionType.REGEX_AND_DICTIONARY || detectionType == PIIEnum.DetectionType.DICTIONARY) && piiMap.contains(piiKey)) {
                findPIIUsingRegexAndDictionaryClass(data, content, piiInfo, filter, resultJson, listOfSpan, detectionType, infoFormat);
            }
            else {
                if (detectionType != PIIEnum.DetectionType.REGEX) {
                    continue;
                }
                findPIIUsingRegexClass(content, piiInfo, filter, resultJson, listOfSpan, detectionType, infoFormat);
            }
        }
        return listOfSpan;
    }
    
    private static void findPIIUsingRegexAndDictionaryClass(final String[] data, final String content, final PIIType piiType, final PIIScoreFilter filter, final JSONObject resultJson, final List<Span> listOfSpan, final PIIEnum.DetectionType detectionType, final PIIResult.InfoFormat infoFormat) {
        final String key = piiType.getPiiType();
        try {
            final Class<?> piiClass = Class.forName(PIIUtils.piiMap.get(key));
            final Constructor<?> constructor = piiClass.getConstructor(String.class, String[].class, Boolean.TYPE);
            final Object instance = constructor.newInstance(content, data, false);
            final List<PII> list = (List<PII>)piiClass.getMethod("findPII", (Class<?>[])null).invoke(instance, (Object[])null);
            int index = 0;
            for (final PII pii : list) {
                final String text = pii.getText();
                final int start = content.indexOf(text, index);
                if (start == -1) {
                    continue;
                }
                final int end = index = start + text.length();
                if (infoFormat == PIIResult.InfoFormat.DISABLE) {
                    reservePIIDetailInJson(null, start, end, pii.getScore(), null, filter, listOfSpan);
                }
                else {
                    final JSONObject piiJson = PIIUtil.convertPiiDetectorResultAsJSON(text, pii.getScore(), start, end, piiType.getCategory(), piiType.getSensitivity(), piiType.getPiiType(), detectionType, infoFormat);
                    reservePIIDetailInJson(resultJson, start, end, null, piiJson, filter, listOfSpan);
                }
            }
        }
        catch (final Exception e) {
            ExtendedPIIQuery.LOGGER.log(Level.SEVERE, "{0} Exception occured at zpii detector", e.getMessage());
        }
    }
    
    private static void findPIIUsingRegexClass(final String content, final PIIType piiTypes, final PIIScoreFilter filter, final JSONObject resultJson, final List<Span> listOfSpan, final PIIEnum.DetectionType detectionType, final PIIResult.InfoFormat infoFormat) {
        final String key = piiTypes.getPiiType();
        final Span[] spans = PIIRegex.findPII(content, key);
        for (int i = 0; i < spans.length; ++i) {
            if (infoFormat == PIIResult.InfoFormat.DISABLE) {
                reservePIIDetailInJson(null, spans[i].getStart(), spans[i].getEnd(), Float.parseFloat(NLPUtils.decimalFormat.format(spans[i].getProb())), null, filter, listOfSpan);
            }
            else {
                final JSONObject piiJson = PIIUtil.convertPiiDetectorResultAsJSON(content.substring(spans[i].getStart(), spans[i].getEnd()), (float)spans[i].getProb(), spans[i].getStart(), spans[i].getEnd(), piiTypes.getCategory(), piiTypes.getSensitivity(), spans[i].getType(), detectionType, infoFormat);
                reservePIIDetailInJson(resultJson, spans[i].getStart(), spans[i].getEnd(), null, piiJson, filter, listOfSpan);
            }
        }
    }
    
    private static void reservePIIDetailInJson(final JSONObject resultJson, final int start, final int end, final Float detectedPIIscore, final JSONObject piiJson, final PIIScoreFilter filter, final List<Span> listOfSpan) {
        if (resultJson != null && isValidPiiScore(piiJson, filter)) {
            listOfSpan.add(new Span(start, end));
            if (resultJson.has(PIIEnum.JsonKeys.REGEX_KEY.value())) {
                resultJson.accumulate(PIIEnum.JsonKeys.REGEX_KEY.value(), (Object)piiJson);
            }
            else {
                final JSONArray array = new JSONArray();
                array.put((Object)piiJson);
                resultJson.put(PIIEnum.JsonKeys.REGEX_KEY.value(), (Object)array);
            }
        }
        else if (resultJson == null && detectedPIIscore != null && filter.isValidScore(detectedPIIscore)) {
            listOfSpan.add(new Span(start, end));
        }
    }
    
    private static boolean isValidPiiScore(final JSONObject piiData, final PIIScoreFilter filter) {
        return (filter.getParserType() == 0 && filter.isValidScore((float)piiData.getDouble(PIIEnum.JsonKeys.CONFIDENCE_LEVEL.value()))) || (filter.getParserType() == 1 && filter.isValidDetectedPIIData(piiData));
    }
    
    static {
        LOGGER = Logger.getLogger(ExtendedPIIQuery.class.getName());
    }
}
