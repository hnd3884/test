package org.apache.commons.collections4.trie;

import java.util.Set;
import java.util.Collection;
import java.util.Comparator;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.Map;
import org.apache.commons.collections4.trie.analyzer.StringKeyAnalyzer;

public class PatriciaTrie<E> extends AbstractPatriciaTrie<String, E>
{
    private static final long serialVersionUID = 4446367780901817838L;
    
    public PatriciaTrie() {
        super(new StringKeyAnalyzer());
    }
    
    public PatriciaTrie(final Map<? extends String, ? extends E> m) {
        super(new StringKeyAnalyzer(), m);
    }
}
