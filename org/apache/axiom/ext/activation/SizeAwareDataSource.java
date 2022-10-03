package org.apache.axiom.ext.activation;

import javax.activation.DataSource;

public interface SizeAwareDataSource extends DataSource
{
    long getSize();
}
