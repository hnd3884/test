package com.zoho.security.zsecpiidetector.detector;

import com.zlabs.pii.PIIException;
import java.util.logging.Level;
import com.zoho.security.zsecpiidetector.DefaultPIIScoreFilter;
import opennlp.tools.util.Span;
import com.zoho.security.zsecpiidetector.types.PIIEnum;
import com.zoho.security.zsecpiidetector.finder.ExtendedPIIQuery;
import com.zoho.security.zsecpiidetector.PIIUtil;
import org.json.JSONObject;
import com.zoho.security.zsecpiidetector.types.PIIType;
import java.util.List;
import com.zoho.security.zsecpiidetector.PIIResult;
import com.zoho.security.zsecpiidetector.PIIScoreFilter;
import com.zoho.security.zsecpiidetector.handler.PIIHandler;
import com.zoho.security.zsecpiidetector.PIIClassifier;
import java.util.logging.Logger;

public class RegexDetector extends PIIDetector
{
    private static final Logger LOGGER;
    
    public RegexDetector() {
        this(new PIIClassifier(), PIIHandler.DEFAULT_PII_HANDLER);
    }
    
    public RegexDetector(final PIIClassifier classifier) {
        this(classifier, PIIHandler.DEFAULT_PII_HANDLER);
    }
    
    public RegexDetector(final PIIClassifier classifier, final PIIHandler handler) {
        super(classifier, handler);
    }
    
    public PIIResult detectOnRegex(final String data, final PIIScoreFilter piiFilter, final boolean enableDetailDetector) {
        return this.detect(data, this.piiClassifier.classifiedPiiInfoBasedRegex(), piiFilter, enableDetailDetector);
    }
    
    public PIIResult detectOnDictionary(final String data, final PIIScoreFilter piiFilter, final boolean enableDetailDetector) {
        return this.detect(data, this.piiClassifier.classifiedPiiInfoBasedDictionary(), piiFilter, enableDetailDetector);
    }
    
    public PIIResult detectOnRegexAndDictionary(final String data, final PIIScoreFilter piiFilter, final boolean enableDetailDetector) {
        return this.detect(data, this.piiClassifier.classifiedPiiInfoBasedRegexAndDictionary(), piiFilter, enableDetailDetector);
    }
    
    private PIIResult detect(final String data, final List<PIIType> classifiedPiiInfo, final PIIScoreFilter piiFilter, final boolean enableDetailDetector) {
        final JSONObject resultJson = new JSONObject();
        final PIIResult.InfoFormat infoFormat = this.getResultInfoFormat(enableDetailDetector);
        if (PIIUtil.isValid(data) && piiFilter != null) {
            final List<Span> listOfSpan = ExtendedPIIQuery.findPII(data, classifiedPiiInfo, piiFilter, resultJson, infoFormat);
            final String regexBasedMaskedData = this.piiHandler.handlePII(data, listOfSpan);
            resultJson.put(PIIEnum.JsonKeys.REGEX_BASED_MASKED_DATA.value(), (Object)regexBasedMaskedData);
        }
        return new PIIResult(resultJson);
    }
    
    @Override
    public PIIResult detect(final String data, final boolean enableDetailDetector) {
        PIIResult result = null;
        try {
            if (this.piiClassifier.getDetectionType() == null || this.piiClassifier.getDetectionType().contains(PIIEnum.DetectionType.DICTIONARY)) {
                result = this.detectOnDictionary(data, new DefaultPIIScoreFilter(0.8f), enableDetailDetector);
            }
            if (this.piiClassifier.getDetectionType() == null || this.piiClassifier.getDetectionType().contains(PIIEnum.DetectionType.REGEX)) {
                final PIIResult regexResult = this.detectOnRegex((result != null && result.toString() != null) ? result.toString() : data, new DefaultPIIScoreFilter(0.8f), enableDetailDetector);
                if (result != null) {
                    result.bindRegexResults(regexResult);
                }
                else {
                    result = regexResult;
                }
            }
            if (this.piiClassifier.getDetectionType() == null || this.piiClassifier.getDetectionType().contains(PIIEnum.DetectionType.REGEX_AND_DICTIONARY)) {
                final PIIResult regexAndDicResult = this.detectOnRegexAndDictionary((result != null && result.toString() != null) ? result.toString() : data, new DefaultPIIScoreFilter(0.8f), enableDetailDetector);
                if (result != null) {
                    result.bindRegexResults(regexAndDicResult);
                }
                else {
                    result = regexAndDicResult;
                }
            }
        }
        catch (final PIIException ex) {
            RegexDetector.LOGGER.log(Level.SEVERE, ex.getMessage());
        }
        return result;
    }
    
    @Override
    public PIIResult detect(final String data) {
        return this.detect(data, false);
    }
    
    @Override
    public PIIResult detect(final String data, final PIIScoreFilter filter, final boolean enableDetailDetector) {
        PIIResult result = null;
        if (this.piiClassifier.getDetectionType() == null || this.piiClassifier.getDetectionType().contains(PIIEnum.DetectionType.DICTIONARY)) {
            result = this.detectOnDictionary(data, filter, enableDetailDetector);
        }
        if (this.piiClassifier.getDetectionType() == null || this.piiClassifier.getDetectionType().contains(PIIEnum.DetectionType.REGEX)) {
            final PIIResult regexResult = this.detectOnRegex((result != null && result.toString() != null) ? result.toString() : data, filter, enableDetailDetector);
            if (result != null) {
                result.bindRegexResults(regexResult);
            }
            else {
                result = regexResult;
            }
        }
        if (this.piiClassifier.getDetectionType() == null || this.piiClassifier.getDetectionType().contains(PIIEnum.DetectionType.REGEX_AND_DICTIONARY)) {
            final PIIResult regexAndDicResult = this.detectOnRegexAndDictionary((result != null && result.toString() != null) ? result.toString() : data, filter, enableDetailDetector);
            if (result != null) {
                result.bindRegexResults(regexAndDicResult);
            }
            else {
                result = regexAndDicResult;
            }
        }
        return result;
    }
    
    @Override
    public PIIResult detect(final String data, final PIIScoreFilter filter) {
        return this.detect(data, filter, false);
    }
    
    static {
        LOGGER = Logger.getLogger(RegexDetector.class.getName());
    }
}
