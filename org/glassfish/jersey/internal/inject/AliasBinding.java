package org.glassfish.jersey.internal.inject;

import java.util.LinkedHashSet;
import java.util.OptionalInt;
import java.util.Optional;
import java.lang.annotation.Annotation;
import java.util.Set;

public class AliasBinding
{
    private final Class<?> contract;
    private final Set<Annotation> qualifiers;
    private Optional<String> scope;
    private OptionalInt rank;
    
    AliasBinding(final Class<?> contract) {
        this.qualifiers = new LinkedHashSet<Annotation>();
        this.scope = Optional.empty();
        this.rank = OptionalInt.empty();
        this.contract = contract;
    }
    
    public Class<?> getContract() {
        return this.contract;
    }
    
    public Optional<String> getScope() {
        return this.scope;
    }
    
    public AliasBinding in(final String scope) {
        this.scope = Optional.of(scope);
        return this;
    }
    
    public OptionalInt getRank() {
        return this.rank;
    }
    
    public AliasBinding ranked(final int rank) {
        this.rank = OptionalInt.of(rank);
        return this;
    }
    
    public Set<Annotation> getQualifiers() {
        return this.qualifiers;
    }
    
    public AliasBinding qualifiedBy(final Annotation annotation) {
        if (annotation != null) {
            this.qualifiers.add(annotation);
        }
        return this;
    }
}
