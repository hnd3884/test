package org.apache.commons.collections4.collection;

import java.util.Collections;
import org.apache.commons.collections4.queue.PredicatedQueue;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.commons.collections4.bag.PredicatedBag;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.multiset.PredicatedMultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.set.PredicatedSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections4.list.PredicatedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.collections4.functors.NotNullPredicate;
import org.apache.commons.collections4.Predicate;

public class PredicatedCollection<E> extends AbstractCollectionDecorator<E>
{
    private static final long serialVersionUID = -5259182142076705162L;
    protected final Predicate<? super E> predicate;
    
    public static <E> Builder<E> builder(final Predicate<? super E> predicate) {
        return new Builder<E>(predicate);
    }
    
    public static <E> Builder<E> notNullBuilder() {
        return new Builder<E>(NotNullPredicate.notNullPredicate());
    }
    
    public static <T> PredicatedCollection<T> predicatedCollection(final Collection<T> coll, final Predicate<? super T> predicate) {
        return new PredicatedCollection<T>(coll, predicate);
    }
    
    protected PredicatedCollection(final Collection<E> coll, final Predicate<? super E> predicate) {
        super(coll);
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null.");
        }
        this.predicate = predicate;
        for (final E item : coll) {
            this.validate(item);
        }
    }
    
    protected void validate(final E object) {
        if (!this.predicate.evaluate(object)) {
            throw new IllegalArgumentException("Cannot add Object '" + object + "' - Predicate '" + this.predicate + "' rejected it");
        }
    }
    
    @Override
    public boolean add(final E object) {
        this.validate(object);
        return this.decorated().add(object);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        for (final E item : coll) {
            this.validate(item);
        }
        return this.decorated().addAll(coll);
    }
    
    public static class Builder<E>
    {
        private final Predicate<? super E> predicate;
        private final List<E> accepted;
        private final List<E> rejected;
        
        public Builder(final Predicate<? super E> predicate) {
            this.accepted = new ArrayList<E>();
            this.rejected = new ArrayList<E>();
            if (predicate == null) {
                throw new NullPointerException("Predicate must not be null");
            }
            this.predicate = predicate;
        }
        
        public Builder<E> add(final E item) {
            if (this.predicate.evaluate(item)) {
                this.accepted.add(item);
            }
            else {
                this.rejected.add(item);
            }
            return this;
        }
        
        public Builder<E> addAll(final Collection<? extends E> items) {
            if (items != null) {
                for (final E item : items) {
                    this.add(item);
                }
            }
            return this;
        }
        
        public List<E> createPredicatedList() {
            return this.createPredicatedList(new ArrayList<E>());
        }
        
        public List<E> createPredicatedList(final List<E> list) {
            if (list == null) {
                throw new NullPointerException("List must not be null.");
            }
            final List<E> predicatedList = PredicatedList.predicatedList(list, this.predicate);
            predicatedList.addAll((Collection<? extends E>)this.accepted);
            return predicatedList;
        }
        
        public Set<E> createPredicatedSet() {
            return this.createPredicatedSet(new HashSet<E>());
        }
        
        public Set<E> createPredicatedSet(final Set<E> set) {
            if (set == null) {
                throw new NullPointerException("Set must not be null.");
            }
            final PredicatedSet<E> predicatedSet = PredicatedSet.predicatedSet(set, this.predicate);
            predicatedSet.addAll((Collection<?>)this.accepted);
            return predicatedSet;
        }
        
        public MultiSet<E> createPredicatedMultiSet() {
            return this.createPredicatedMultiSet(new HashMultiSet<E>());
        }
        
        public MultiSet<E> createPredicatedMultiSet(final MultiSet<E> multiset) {
            if (multiset == null) {
                throw new NullPointerException("MultiSet must not be null.");
            }
            final PredicatedMultiSet<E> predicatedMultiSet = PredicatedMultiSet.predicatedMultiSet(multiset, this.predicate);
            predicatedMultiSet.addAll((Collection<?>)this.accepted);
            return predicatedMultiSet;
        }
        
        public Bag<E> createPredicatedBag() {
            return this.createPredicatedBag(new HashBag<E>());
        }
        
        public Bag<E> createPredicatedBag(final Bag<E> bag) {
            if (bag == null) {
                throw new NullPointerException("Bag must not be null.");
            }
            final PredicatedBag<E> predicatedBag = PredicatedBag.predicatedBag(bag, this.predicate);
            predicatedBag.addAll((Collection<?>)this.accepted);
            return predicatedBag;
        }
        
        public Queue<E> createPredicatedQueue() {
            return this.createPredicatedQueue(new LinkedList<E>());
        }
        
        public Queue<E> createPredicatedQueue(final Queue<E> queue) {
            if (queue == null) {
                throw new NullPointerException("queue must not be null");
            }
            final PredicatedQueue<E> predicatedQueue = PredicatedQueue.predicatedQueue(queue, this.predicate);
            predicatedQueue.addAll((Collection<?>)this.accepted);
            return predicatedQueue;
        }
        
        public Collection<E> rejectedElements() {
            return Collections.unmodifiableCollection((Collection<? extends E>)this.rejected);
        }
    }
}
