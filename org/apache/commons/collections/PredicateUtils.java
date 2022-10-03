package org.apache.commons.collections;

import org.apache.commons.collections.functors.TransformedPredicate;
import org.apache.commons.collections.functors.NullIsTruePredicate;
import org.apache.commons.collections.functors.NullIsFalsePredicate;
import org.apache.commons.collections.functors.NullIsExceptionPredicate;
import org.apache.commons.collections.functors.TransformerPredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.NonePredicate;
import org.apache.commons.collections.functors.OnePredicate;
import org.apache.commons.collections.functors.AnyPredicate;
import org.apache.commons.collections.functors.OrPredicate;
import java.util.Collection;
import org.apache.commons.collections.functors.AllPredicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.functors.IdentityPredicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.apache.commons.collections.functors.NullPredicate;
import org.apache.commons.collections.functors.FalsePredicate;
import org.apache.commons.collections.functors.TruePredicate;
import org.apache.commons.collections.functors.ExceptionPredicate;

public class PredicateUtils
{
    public static Predicate exceptionPredicate() {
        return ExceptionPredicate.INSTANCE;
    }
    
    public static Predicate truePredicate() {
        return TruePredicate.INSTANCE;
    }
    
    public static Predicate falsePredicate() {
        return FalsePredicate.INSTANCE;
    }
    
    public static Predicate nullPredicate() {
        return NullPredicate.INSTANCE;
    }
    
    public static Predicate notNullPredicate() {
        return NotNullPredicate.INSTANCE;
    }
    
    public static Predicate equalPredicate(final Object value) {
        return EqualPredicate.getInstance(value);
    }
    
    public static Predicate identityPredicate(final Object value) {
        return IdentityPredicate.getInstance(value);
    }
    
    public static Predicate instanceofPredicate(final Class type) {
        return InstanceofPredicate.getInstance(type);
    }
    
    public static Predicate uniquePredicate() {
        return UniquePredicate.getInstance();
    }
    
    public static Predicate invokerPredicate(final String methodName) {
        return asPredicate(InvokerTransformer.getInstance(methodName));
    }
    
    public static Predicate invokerPredicate(final String methodName, final Class[] paramTypes, final Object[] args) {
        return asPredicate(InvokerTransformer.getInstance(methodName, paramTypes, args));
    }
    
    public static Predicate andPredicate(final Predicate predicate1, final Predicate predicate2) {
        return AndPredicate.getInstance(predicate1, predicate2);
    }
    
    public static Predicate allPredicate(final Predicate[] predicates) {
        return AllPredicate.getInstance(predicates);
    }
    
    public static Predicate allPredicate(final Collection predicates) {
        return AllPredicate.getInstance(predicates);
    }
    
    public static Predicate orPredicate(final Predicate predicate1, final Predicate predicate2) {
        return OrPredicate.getInstance(predicate1, predicate2);
    }
    
    public static Predicate anyPredicate(final Predicate[] predicates) {
        return AnyPredicate.getInstance(predicates);
    }
    
    public static Predicate anyPredicate(final Collection predicates) {
        return AnyPredicate.getInstance(predicates);
    }
    
    public static Predicate eitherPredicate(final Predicate predicate1, final Predicate predicate2) {
        return onePredicate(new Predicate[] { predicate1, predicate2 });
    }
    
    public static Predicate onePredicate(final Predicate[] predicates) {
        return OnePredicate.getInstance(predicates);
    }
    
    public static Predicate onePredicate(final Collection predicates) {
        return OnePredicate.getInstance(predicates);
    }
    
    public static Predicate neitherPredicate(final Predicate predicate1, final Predicate predicate2) {
        return nonePredicate(new Predicate[] { predicate1, predicate2 });
    }
    
    public static Predicate nonePredicate(final Predicate[] predicates) {
        return NonePredicate.getInstance(predicates);
    }
    
    public static Predicate nonePredicate(final Collection predicates) {
        return NonePredicate.getInstance(predicates);
    }
    
    public static Predicate notPredicate(final Predicate predicate) {
        return NotPredicate.getInstance(predicate);
    }
    
    public static Predicate asPredicate(final Transformer transformer) {
        return TransformerPredicate.getInstance(transformer);
    }
    
    public static Predicate nullIsExceptionPredicate(final Predicate predicate) {
        return NullIsExceptionPredicate.getInstance(predicate);
    }
    
    public static Predicate nullIsFalsePredicate(final Predicate predicate) {
        return NullIsFalsePredicate.getInstance(predicate);
    }
    
    public static Predicate nullIsTruePredicate(final Predicate predicate) {
        return NullIsTruePredicate.getInstance(predicate);
    }
    
    public static Predicate transformedPredicate(final Transformer transformer, final Predicate predicate) {
        return TransformedPredicate.getInstance(transformer, predicate);
    }
}
