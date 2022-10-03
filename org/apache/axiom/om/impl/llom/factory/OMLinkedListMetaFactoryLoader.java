package org.apache.axiom.om.impl.llom.factory;

import org.apache.axiom.om.OMMetaFactory;
import java.util.Map;
import org.apache.axiom.locator.loader.OMMetaFactoryLoader;

public class OMLinkedListMetaFactoryLoader implements OMMetaFactoryLoader
{
    public OMMetaFactory load(final Map properties) {
        return (OMMetaFactory)OMLinkedListMetaFactory.INSTANCE;
    }
}
