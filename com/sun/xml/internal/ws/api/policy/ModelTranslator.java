package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.resources.ManagementMessages;
import com.sun.xml.internal.ws.config.management.policy.ManagementAssertionCreator;
import java.util.Collection;
import java.util.Arrays;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelTranslator;

public class ModelTranslator extends PolicyModelTranslator
{
    private static final PolicyLogger LOGGER;
    private static final PolicyAssertionCreator[] JAXWS_ASSERTION_CREATORS;
    private static final ModelTranslator translator;
    private static final PolicyException creationException;
    
    private ModelTranslator() throws PolicyException {
        super(Arrays.asList(ModelTranslator.JAXWS_ASSERTION_CREATORS));
    }
    
    public static ModelTranslator getTranslator() throws PolicyException {
        if (ModelTranslator.creationException != null) {
            throw ModelTranslator.LOGGER.logSevereException(ModelTranslator.creationException);
        }
        return ModelTranslator.translator;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(ModelTranslator.class);
        JAXWS_ASSERTION_CREATORS = new PolicyAssertionCreator[] { new ManagementAssertionCreator() };
        ModelTranslator tempTranslator = null;
        PolicyException tempException = null;
        try {
            tempTranslator = new ModelTranslator();
        }
        catch (final PolicyException e) {
            tempException = e;
            ModelTranslator.LOGGER.warning(ManagementMessages.WSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION(), e);
        }
        finally {
            translator = tempTranslator;
            creationException = tempException;
        }
    }
}
