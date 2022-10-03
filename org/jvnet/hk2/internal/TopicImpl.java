package org.jvnet.hk2.internal;

import java.util.Collection;
import java.util.HashSet;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.api.messaging.TopicDistributionService;
import java.util.Collections;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import org.glassfish.hk2.api.messaging.Topic;

public class TopicImpl<T> implements Topic<T>
{
    private final ServiceLocatorImpl locator;
    private final Type topicType;
    private final Set<Annotation> requiredQualifiers;
    
    TopicImpl(final ServiceLocatorImpl locator, final Type topicType, final Set<Annotation> requiredQualifiers) {
        this.locator = locator;
        this.topicType = topicType;
        this.requiredQualifiers = Collections.unmodifiableSet((Set<? extends Annotation>)requiredQualifiers);
    }
    
    public void publish(final T message) {
        if (message == null) {
            throw new IllegalArgumentException();
        }
        final TopicDistributionService distributor = this.locator.getService(TopicDistributionService.class, new Annotation[0]);
        if (distributor == null) {
            throw new IllegalStateException("There is no implementation of the TopicDistributionService to distribute the message");
        }
        distributor.distributeMessage((Topic)this, (Object)message);
    }
    
    public Topic<T> named(final String name) {
        return this.qualifiedWith((Annotation)new NamedImpl(name));
    }
    
    public <U> Topic<U> ofType(final Type type) {
        return (Topic<U>)new TopicImpl(this.locator, type, this.requiredQualifiers);
    }
    
    public Topic<T> qualifiedWith(final Annotation... qualifiers) {
        final HashSet<Annotation> moreAnnotations = new HashSet<Annotation>(this.requiredQualifiers);
        for (final Annotation qualifier : qualifiers) {
            moreAnnotations.add(qualifier);
        }
        return (Topic<T>)new TopicImpl(this.locator, this.topicType, moreAnnotations);
    }
    
    public Type getTopicType() {
        return this.topicType;
    }
    
    public Set<Annotation> getTopicQualifiers() {
        return this.requiredQualifiers;
    }
}
