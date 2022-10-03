package org.apache.commons.collections.functors;

import org.apache.commons.collections.Predicate;

public interface PredicateDecorator extends Predicate
{
    Predicate[] getPredicates();
}
