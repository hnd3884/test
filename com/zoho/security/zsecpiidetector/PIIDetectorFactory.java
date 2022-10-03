package com.zoho.security.zsecpiidetector;

import java.util.HashMap;
import org.w3c.dom.Document;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.zoho.security.zsecpiidetector.finder.PIIRegex;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.io.InputStream;
import java.util.Iterator;
import com.zoho.security.zsecpiidetector.detector.MLDetector;
import com.zoho.security.zsecpiidetector.detector.RegexDetector;
import com.zoho.security.zsecpiidetector.detector.PIIDetector;
import com.zoho.security.zsecpiidetector.handler.PIIHandler;
import com.zlabs.pii.PIIException;
import java.util.logging.Level;
import java.util.Arrays;
import com.zoho.security.zsecpiidetector.types.PIIEnum;
import com.zoho.security.zsecpiidetector.types.PIIRegexType;
import com.zoho.security.zsecpiidetector.types.PIIType;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PIIDetectorFactory
{
    private static final Logger LOGGER;
    private static final String CONFIG_FILE = "zsecpii-info.xml";
    private static final String CONFIG_SCHEMA = "zsecpii-info.xsd";
    private static final Map<String, List<PIIType>> PII_INFO_LIST;
    private static final List<PIIRegexType> REGEX_LIST;
    private static final Map<String, String> MODEL_LIST;
    private static boolean isRegexResourceOnly;
    private static PIIDetectorFactory piiDetectorFactory;
    
    private PIIDetectorFactory() throws PIIException {
        if (!loadModels(true, Arrays.asList(PIIEnum.DetectionType.COMMON, PIIEnum.DetectionType.REGEX_AND_DICTIONARY, PIIEnum.DetectionType.REGEX))) {
            throw new PIIException("Error occured at resource load", PIIDetectorFactory.class.getName(), Level.SEVERE);
        }
    }
    
    private PIIDetectorFactory(final String modelDir, final List<PIIEnum.DetectionType> allowedDetectionTypes) throws PIIException {
        if (!loadModels(false, allowedDetectionTypes)) {
            throw new PIIException("Error occured at resource load", PIIDetectorFactory.class.getName(), Level.SEVERE);
        }
    }
    
    public static PIIDetectorFactory createFactoryInstance() throws PIIException {
        return createFactoryInstance(null, null);
    }
    
    public static PIIDetectorFactory createFactoryInstance(final String modelDir) throws PIIException {
        return createFactoryInstance(modelDir, PIIEnum.DetectionType.getAllDetectionTypes());
    }
    
    public static PIIDetectorFactory createFactoryInstance(final String modelDir, final List<PIIEnum.DetectionType> allowedDetectionTypes) throws PIIException {
        if (PIIDetectorFactory.piiDetectorFactory == null) {
            if (modelDir == null && allowedDetectionTypes == null) {
                PIIDetectorFactory.piiDetectorFactory = new PIIDetectorFactory();
                PIIDetectorFactory.LOGGER.log(Level.INFO, "Resource successfully loaded from stream");
            }
            else {
                System.setProperty("models.dir", modelDir);
                PIIDetectorFactory.piiDetectorFactory = new PIIDetectorFactory(modelDir, allowedDetectionTypes);
                PIIDetectorFactory.LOGGER.log(Level.INFO, "Resource successfully loaded from specified directory {0} ", modelDir);
            }
        }
        else {
            PIIDetectorFactory.LOGGER.log(Level.INFO, "Resource are already loaded");
        }
        return PIIDetectorFactory.piiDetectorFactory;
    }
    
    public static PIIDetectorFactory getFactoryInstance() throws PIIException {
        if (PIIDetectorFactory.piiDetectorFactory == null) {
            createFactoryInstance();
        }
        return PIIDetectorFactory.piiDetectorFactory;
    }
    
    public PIIDetector getDetector(final Detector detector, final PIIClassifier classifier, final PIIHandler handler) {
        switch (detector) {
            case REGEX_DETECTOR: {
                return new RegexDetector(classifier, handler);
            }
            case ML_DETECTOR: {
                if (isRegexResourceOnly()) {
                    throw new RuntimeException("ML resource can't load from stream");
                }
                return new MLDetector(classifier, handler);
            }
            default: {
                return null;
            }
        }
    }
    
    public static List<PIIRegexType> getPiiRegexList() {
        return PIIDetectorFactory.REGEX_LIST;
    }
    
    public static Map<String, List<PIIType>> getPiiInfoList() {
        return PIIDetectorFactory.PII_INFO_LIST;
    }
    
    public static Map<String, String> getModelsList() {
        return PIIDetectorFactory.MODEL_LIST;
    }
    
    public static boolean isRegexResourceOnly() {
        return PIIDetectorFactory.isRegexResourceOnly;
    }
    
    private static boolean loadModels(final boolean loadFromStream, final List<PIIEnum.DetectionType> allowedDetectionTypes) {
        PIIDetectorFactory.isRegexResourceOnly = loadFromStream;
        initPiiDetectorResources(PIIUtil.class.getClassLoader().getResourceAsStream("zsecpii-info.xml"), allowedDetectionTypes);
        final Map<String, String> models = getModelsList();
        try {
            for (final String modelName : models.keySet()) {
                if (allowedDetectionTypes.contains(PIIEnum.DetectionType.valueOf(modelName.substring(modelName.indexOf(58) + 1).toUpperCase()))) {
                    final Class<?> piiClass = Class.forName(models.get(modelName));
                    piiClass.getMethod("getInstance", Boolean.TYPE).invoke(null, loadFromStream);
                }
            }
        }
        catch (final Exception ex) {
            PIIDetectorFactory.LOGGER.log(Level.SEVERE, "Exception occured at loading of models: {0}", ex.getMessage());
            return false;
        }
        return true;
    }
    
    private static void initPiiDetectorResources(final InputStream piiInfo, final List<PIIEnum.DetectionType> allowedDetectionType) {
        try {
            PIIDetectorFactory.PII_INFO_LIST.clear();
            PIIDetectorFactory.REGEX_LIST.clear();
            PIIDetectorFactory.MODEL_LIST.clear();
            final Document doc = PIIUtil.getDocumentBuilder().parse(piiInfo);
            final Element piiDetectorElement = doc.getDocumentElement();
            final Element piis = PIIUtil.getFirstChildNodeByTagName(piiDetectorElement, "piis");
            for (final Element pii : PIIUtil.getChildNodesByTagName(piis, "pii")) {
                for (final Element datas : PIIUtil.getChildNodesByTagName(pii, "datas")) {
                    for (final Element data : PIIUtil.getChildNodesByTagName(datas, "data")) {
                        final String detAttribute = data.getAttribute("detectionType").toUpperCase();
                        final PIIEnum.DetectionType det = PIIEnum.DetectionType.valueOf(detAttribute);
                        if (allowedDetectionType.contains(det)) {
                            final String catAttribute = pii.getAttribute("category").toUpperCase();
                            final String sensAttribute = datas.getAttribute("sensitivity").toUpperCase();
                            final PIIEnum.Category cat = PIIEnum.Category.valueOf(catAttribute);
                            final PIIEnum.Sensitivity sens = PIIEnum.Sensitivity.valueOf(sensAttribute);
                            final String uniqueKey = PIIUtil.createUniqueKeyFromStrings(catAttribute, sensAttribute, detAttribute);
                            if (PIIDetectorFactory.PII_INFO_LIST.get(uniqueKey) == null) {
                                PIIDetectorFactory.PII_INFO_LIST.put(uniqueKey, new ArrayList<PIIType>());
                            }
                            PIIDetectorFactory.PII_INFO_LIST.get(uniqueKey).add(new PIIType(data.getAttribute("value"), cat, sens, det));
                        }
                    }
                }
            }
            final Element regexesElement = PIIUtil.getFirstChildNodeByTagName(piiDetectorElement, "regexes");
            PIIRegex.init(regexesElement);
            final Element modelesElement = PIIUtil.getFirstChildNodeByTagName(piiDetectorElement, "models");
            for (final Element model : PIIUtil.getChildNodesByTagName(modelesElement, "model")) {
                final String modelName = model.getAttribute("name");
                final String modelType = model.getAttribute("model-type");
                PIIDetectorFactory.MODEL_LIST.put(PIIUtil.createUniqueKeyFromStrings(modelName, modelType), model.getAttribute("class-name"));
            }
        }
        catch (final SAXException | IOException e) {
            PIIDetectorFactory.LOGGER.log(Level.WARNING, e.getMessage());
        }
    }
    
    static {
        LOGGER = Logger.getLogger(PIIDetectorFactory.class.getName());
        PII_INFO_LIST = new HashMap<String, List<PIIType>>();
        REGEX_LIST = new ArrayList<PIIRegexType>();
        MODEL_LIST = new HashMap<String, String>();
    }
    
    public enum Detector
    {
        REGEX_DETECTOR, 
        ML_DETECTOR;
    }
}
