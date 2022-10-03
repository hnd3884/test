package org.htmlparser;

import java.io.Serializable;

public interface NodeFilter extends Serializable, Cloneable
{
    boolean accept(final Node p0);
}
