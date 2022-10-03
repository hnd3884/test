package org.apache.commons.text.diff;

import java.util.List;

public interface ReplacementsHandler<T>
{
    void handleReplacement(final int p0, final List<T> p1, final List<T> p2);
}
