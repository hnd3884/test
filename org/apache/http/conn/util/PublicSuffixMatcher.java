package org.apache.http.conn.util;

import java.net.IDN;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.util.Args;
import java.util.Collection;
import java.util.Map;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.SAFE)
public final class PublicSuffixMatcher
{
    private final Map<String, DomainType> rules;
    private final Map<String, DomainType> exceptions;
    
    public PublicSuffixMatcher(final Collection<String> rules, final Collection<String> exceptions) {
        this(DomainType.UNKNOWN, rules, exceptions);
    }
    
    public PublicSuffixMatcher(final DomainType domainType, final Collection<String> rules, final Collection<String> exceptions) {
        Args.notNull((Object)domainType, "Domain type");
        Args.notNull((Object)rules, "Domain suffix rules");
        this.rules = new ConcurrentHashMap<String, DomainType>(rules.size());
        for (final String rule : rules) {
            this.rules.put(rule, domainType);
        }
        this.exceptions = new ConcurrentHashMap<String, DomainType>();
        if (exceptions != null) {
            for (final String exception : exceptions) {
                this.exceptions.put(exception, domainType);
            }
        }
    }
    
    public PublicSuffixMatcher(final Collection<PublicSuffixList> lists) {
        Args.notNull((Object)lists, "Domain suffix lists");
        this.rules = new ConcurrentHashMap<String, DomainType>();
        this.exceptions = new ConcurrentHashMap<String, DomainType>();
        for (final PublicSuffixList list : lists) {
            final DomainType domainType = list.getType();
            final List<String> rules = list.getRules();
            for (final String rule : rules) {
                this.rules.put(rule, domainType);
            }
            final List<String> exceptions = list.getExceptions();
            if (exceptions != null) {
                for (final String exception : exceptions) {
                    this.exceptions.put(exception, domainType);
                }
            }
        }
    }
    
    private static DomainType findEntry(final Map<String, DomainType> map, final String rule) {
        if (map == null) {
            return null;
        }
        return map.get(rule);
    }
    
    private static boolean match(final DomainType domainType, final DomainType expectedType) {
        return domainType != null && (expectedType == null || domainType.equals(expectedType));
    }
    
    public String getDomainRoot(final String domain) {
        return this.getDomainRoot(domain, null);
    }
    
    public String getDomainRoot(final String domain, final DomainType expectedType) {
        if (domain == null) {
            return null;
        }
        if (domain.startsWith(".")) {
            return null;
        }
        String segment;
        final String normalized = segment = DnsUtils.normalize(domain);
        String result = null;
        while (segment != null) {
            final String key = IDN.toUnicode(segment);
            final DomainType exceptionRule = findEntry(this.exceptions, key);
            if (match(exceptionRule, expectedType)) {
                return segment;
            }
            final DomainType domainRule = findEntry(this.rules, key);
            if (match(domainRule, expectedType)) {
                if (domainRule == DomainType.PRIVATE) {
                    return segment;
                }
                return result;
            }
            else {
                final int nextdot = segment.indexOf(46);
                final String nextSegment = (nextdot != -1) ? segment.substring(nextdot + 1) : null;
                if (nextSegment != null) {
                    final DomainType wildcardDomainRule = findEntry(this.rules, "*." + IDN.toUnicode(nextSegment));
                    if (match(wildcardDomainRule, expectedType)) {
                        if (wildcardDomainRule == DomainType.PRIVATE) {
                            return segment;
                        }
                        return result;
                    }
                }
                result = segment;
                segment = nextSegment;
            }
        }
        if (expectedType == null || expectedType == DomainType.UNKNOWN) {
            return result;
        }
        return null;
    }
    
    public boolean matches(final String domain) {
        return this.matches(domain, null);
    }
    
    public boolean matches(final String domain, final DomainType expectedType) {
        if (domain == null) {
            return false;
        }
        final String domainRoot = this.getDomainRoot(domain.startsWith(".") ? domain.substring(1) : domain, expectedType);
        return domainRoot == null;
    }
}
