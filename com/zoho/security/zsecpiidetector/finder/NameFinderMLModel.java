package com.zoho.security.zsecpiidetector.finder;

import opennlp.tools.namefind.NameFinderME;
import com.zoho.security.zsecpiidetector.PIIUtil;
import opennlp.tools.util.Span;
import java.util.Iterator;
import java.util.logging.Level;
import java.io.File;
import java.io.IOException;
import com.zoho.security.zsecpiidetector.types.PIIType;
import java.util.List;
import java.util.Arrays;
import com.zoho.security.zsecpiidetector.types.PIIEnum;
import com.zoho.security.zsecpiidetector.PIIClassifier;
import java.util.HashMap;
import opennlp.tools.namefind.TokenNameFinderModel;
import java.util.Map;
import java.util.logging.Logger;

public class NameFinderMLModel
{
    private static final Logger LOGGER;
    private static final String ML_MODEL_DIR;
    private static final String NER_FINER_DIR;
    private static NameFinderMLModel ml;
    private Map<String, TokenNameFinderModel> mlModel;
    
    protected NameFinderMLModel(final boolean resourceFromStream) {
        this.mlModel = new HashMap<String, TokenNameFinderModel>();
        for (final PIIType model : new PIIClassifier().piiInfoClassifierQuery(null, null, Arrays.asList(PIIEnum.DetectionType.MACHINE_LEARNING))) {
            try {
                final String path = NameFinderMLModel.ML_MODEL_DIR + model.getPiiType() + ".bin";
                if (resourceFromStream) {
                    if (NameFinderMLModel.class.getResource(NameFinderMLModel.NER_FINER_DIR + path) == null) {
                        throw new IOException("ML resouce not found: " + path);
                    }
                    this.setMLModel(model.getPiiType(), new TokenNameFinderModel(NameFinderMLModel.class.getResource(NameFinderMLModel.NER_FINER_DIR + path)));
                }
                else {
                    this.setMLModel(model.getPiiType(), new TokenNameFinderModel(new File(System.getProperty("models.dir") + path)));
                }
            }
            catch (final IOException e) {
                NameFinderMLModel.LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }
    
    public TokenNameFinderModel getMLModel(final String modelName) {
        return this.mlModel.get(modelName);
    }
    
    public void setMLModel(final String modelName, final TokenNameFinderModel model) {
        this.mlModel.put(modelName, model);
    }
    
    public Span[] findPII(final String data, final String modelName) {
        final String[] tokens = PIIUtil.tokenize(data);
        return new NameFinderME(this.getMLModel(modelName)).find(tokens);
    }
    
    public static NameFinderMLModel getInstance(final boolean resourceFromStream) {
        if (NameFinderMLModel.ml == null) {
            NameFinderMLModel.ml = new NameFinderMLModel(resourceFromStream);
        }
        return NameFinderMLModel.ml;
    }
    
    static {
        LOGGER = Logger.getLogger(NameFinderMLModel.class.getName());
        ML_MODEL_DIR = File.separator + "mlModels" + File.separator;
        NER_FINER_DIR = File.separator + "nerfinder";
    }
}
