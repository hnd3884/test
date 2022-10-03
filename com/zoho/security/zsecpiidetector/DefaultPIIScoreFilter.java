package com.zoho.security.zsecpiidetector;

import com.zoho.security.zsecpiidetector.types.PIIEnum;
import org.json.JSONObject;
import com.zlabs.pii.PIIException;
import java.util.logging.Level;
import com.zoho.security.zsecpiidetector.detector.PIIDetector;

public class DefaultPIIScoreFilter implements PIIScoreFilter
{
    public float userScore;
    
    public DefaultPIIScoreFilter(final float score) throws PIIException {
        if (score < 0.0f || score > 1.0f) {
            throw new PIIException("Invalid filter configuration", PIIDetector.class.getName(), Level.SEVERE);
        }
        this.userScore = score;
    }
    
    @Override
    public int getParserType() {
        return 0;
    }
    
    @Override
    public boolean isValidScore(final float detectedPIIscore) {
        return detectedPIIscore >= this.userScore;
    }
    
    @Override
    public boolean isValidDetectedPIIData(final JSONObject json) {
        return json.getDouble(PIIEnum.JsonKeys.CONFIDENCE_LEVEL.value()) >= this.userScore;
    }
}
