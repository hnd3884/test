package org.jfree.data.general;

import java.util.EventListener;

public interface DatasetChangeListener extends EventListener
{
    void datasetChanged(final DatasetChangeEvent p0);
}
