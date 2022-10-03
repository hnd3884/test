package org.apache.commons.text.similarity;

import java.util.HashMap;
import java.util.Map;

final class Counter
{
    private Counter() {
    }
    
    public static Map<CharSequence, Integer> of(final CharSequence[] tokens) {
        final Map<CharSequence, Integer> innerCounter = new HashMap<CharSequence, Integer>();
        for (final CharSequence token : tokens) {
            if (innerCounter.containsKey(token)) {
                int value = innerCounter.get(token);
                innerCounter.put(token, ++value);
            }
            else {
                innerCounter.put(token, 1);
            }
        }
        return innerCounter;
    }
}
