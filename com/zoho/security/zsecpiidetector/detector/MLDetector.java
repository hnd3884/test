package com.zoho.security.zsecpiidetector.detector;

import com.zoho.security.zsecpiidetector.PIIScoreFilter;
import java.util.Iterator;
import java.util.Collection;
import com.zoho.security.zsecpiidetector.finder.NameFinderMLModel;
import org.json.JSONObject;
import com.zoho.security.zsecpiidetector.PIIUtil;
import com.zoho.security.zsecpiidetector.types.PIIEnum;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.zoho.security.zsecpiidetector.PIIResult;
import org.json.JSONArray;
import com.zoho.security.zsecpiidetector.types.PIIType;
import opennlp.tools.util.Span;
import com.zoho.security.zsecpiidetector.handler.PIIHandler;
import com.zoho.security.zsecpiidetector.PIIClassifier;

public class MLDetector extends PIIDetector
{
    public MLDetector() {
        this(new PIIClassifier(), PIIHandler.DEFAULT_PII_HANDLER);
    }
    
    public MLDetector(final PIIClassifier localPiiClassifier) {
        this(localPiiClassifier, PIIHandler.DEFAULT_PII_HANDLER);
    }
    
    public MLDetector(final PIIClassifier localPiiClassifier, final PIIHandler localPiiHandler) {
        super(localPiiClassifier, localPiiHandler);
    }
    
    public static List<Span> spanToList(final Span[] spans, final String[] data, final PIIType piiType, final String content, final JSONArray array, final PIIResult.InfoFormat infoFormat) {
        final List<Span> listOfSpan = Collections.synchronizedList(new ArrayList<Span>());
        final String[] spanAsStrings = Span.spansToStrings(spans, data);
        int endIndex = 0;
        for (int i = 0; i < spanAsStrings.length; ++i) {
            final int startIndex = content.indexOf(spanAsStrings[i], endIndex);
            if (startIndex != -1) {
                endIndex = startIndex + spanAsStrings[i].length();
                if (infoFormat != PIIResult.InfoFormat.DISABLE) {
                    final JSONObject piiJson = PIIUtil.convertPiiDetectorResultAsJSON(spanAsStrings[i], (float)spans[i].getProb(), startIndex, endIndex, piiType.getCategory(), piiType.getSensitivity(), piiType.getPiiType(), PIIEnum.DetectionType.MACHINE_LEARNING, infoFormat);
                    array.put((Object)piiJson);
                }
                listOfSpan.add(new Span(startIndex, endIndex));
            }
        }
        return listOfSpan;
    }
    
    @Override
    public PIIResult detect(final String data, final boolean enableDetailDetector) {
        final JSONObject resultJson = new JSONObject();
        final List<PIIType> models = this.piiClassifier.classifiedPiiInfoBasedML();
        final List<Span> listOfSpan = Collections.synchronizedList(new ArrayList<Span>());
        final JSONArray detectedPIIs = new JSONArray();
        final PIIResult.InfoFormat infoFormat = this.getResultInfoFormat(enableDetailDetector);
        if (PIIUtil.isValid(data)) {
            for (final PIIType modelName : models) {
                final Span[] detectedPII = NameFinderMLModel.getInstance(false).findPII(data, modelName.getPiiType());
                listOfSpan.addAll(spanToList(detectedPII, PIIUtil.tokenize(data), modelName, data, detectedPIIs, infoFormat));
            }
            if (infoFormat != PIIResult.InfoFormat.DISABLE && detectedPIIs.length() != 0) {
                if (resultJson.has(PIIEnum.JsonKeys.ML_KEY.value())) {
                    resultJson.accumulate(PIIEnum.JsonKeys.ML_KEY.value(), (Object)detectedPIIs);
                }
                else {
                    final JSONArray array = new JSONArray();
                    array.put((Object)detectedPIIs);
                    resultJson.put(PIIEnum.JsonKeys.ML_KEY.value(), (Object)array);
                }
            }
            final String mlBasedMaskedData = this.piiHandler.handlePII(data, listOfSpan);
            resultJson.put(PIIEnum.JsonKeys.ML_BASED_MASKED_DATA.value(), (Object)mlBasedMaskedData);
        }
        return new PIIResult(resultJson);
    }
    
    @Override
    public PIIResult detect(final String data) {
        return this.detect(data, false);
    }
    
    @Deprecated
    @Override
    public PIIResult detect(final String data, final PIIScoreFilter filter) {
        return this.detect(data);
    }
    
    @Deprecated
    @Override
    public PIIResult detect(final String data, final PIIScoreFilter filter, final boolean enableDetailDetector) {
        return this.detect(data, enableDetailDetector);
    }
}
