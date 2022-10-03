package org.glassfish.hk2.utilities;

import java.io.Reader;
import java.io.StringReader;
import java.io.ObjectInput;
import java.io.Writer;
import java.io.StringWriter;
import java.io.ObjectOutput;
import java.io.BufferedReader;
import java.io.IOException;
import javax.inject.Singleton;
import java.io.PrintWriter;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import java.util.Iterator;
import java.util.Collections;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.DescriptorType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.Externalizable;
import org.glassfish.hk2.api.Descriptor;

public class DescriptorImpl implements Descriptor, Externalizable
{
    private static final long serialVersionUID = 1558442492395467828L;
    private static final String CONTRACT_KEY = "contract=";
    private static final String NAME_KEY = "name=";
    private static final String SCOPE_KEY = "scope=";
    private static final String QUALIFIER_KEY = "qualifier=";
    private static final String TYPE_KEY = "type=";
    private static final String VISIBILITY_KEY = "visibility=";
    private static final String METADATA_KEY = "metadata=";
    private static final String RANKING_KEY = "rank=";
    private static final String PROXIABLE_KEY = "proxiable=";
    private static final String PROXY_FOR_SAME_SCOPE_KEY = "proxyForSameScope=";
    private static final String ANALYSIS_KEY = "analysis=";
    private static final String PROVIDE_METHOD_DT = "PROVIDE";
    private static final String LOCAL_DT = "LOCAL";
    private static final String START_START = "[";
    private static final String END_START = "]";
    private static final char END_START_CHAR = ']';
    private static final String SINGLETON_DIRECTIVE = "S";
    private static final String NOT_IN_CONTRACTS_DIRECTIVE = "-";
    private static final char SINGLETON_DIRECTIVE_CHAR = 'S';
    private static final char NOT_IN_CONTRACTS_DIRECTIVE_CHAR = '-';
    private static final Set<String> EMPTY_CONTRACTS_SET;
    private static final Set<String> EMPTY_QUALIFIER_SET;
    private static final Map<String, List<String>> EMPTY_METADATAS_MAP;
    private Set<String> contracts;
    private String implementation;
    private String name;
    private String scope;
    private Map<String, List<String>> metadatas;
    private Set<String> qualifiers;
    private DescriptorType descriptorType;
    private DescriptorVisibility descriptorVisibility;
    private transient HK2Loader loader;
    private int rank;
    private Boolean proxiable;
    private Boolean proxyForSameScope;
    private String analysisName;
    private Long id;
    private Long locatorId;
    
    public DescriptorImpl() {
        this.scope = PerLookup.class.getName();
        this.descriptorType = DescriptorType.CLASS;
        this.descriptorVisibility = DescriptorVisibility.NORMAL;
    }
    
    public DescriptorImpl(final Descriptor copyMe) {
        this.scope = PerLookup.class.getName();
        this.descriptorType = DescriptorType.CLASS;
        this.descriptorVisibility = DescriptorVisibility.NORMAL;
        this.name = copyMe.getName();
        this.scope = copyMe.getScope();
        this.implementation = copyMe.getImplementation();
        this.descriptorType = copyMe.getDescriptorType();
        this.descriptorVisibility = copyMe.getDescriptorVisibility();
        this.loader = copyMe.getLoader();
        this.rank = copyMe.getRanking();
        this.proxiable = copyMe.isProxiable();
        this.proxyForSameScope = copyMe.isProxyForSameScope();
        this.id = copyMe.getServiceId();
        this.locatorId = copyMe.getLocatorId();
        this.analysisName = copyMe.getClassAnalysisName();
        if (copyMe.getAdvertisedContracts() != null && !copyMe.getAdvertisedContracts().isEmpty()) {
            (this.contracts = new LinkedHashSet<String>()).addAll(copyMe.getAdvertisedContracts());
        }
        if (copyMe.getQualifiers() != null && !copyMe.getQualifiers().isEmpty()) {
            (this.qualifiers = new LinkedHashSet<String>()).addAll(copyMe.getQualifiers());
        }
        if (copyMe.getMetadata() != null && !copyMe.getMetadata().isEmpty()) {
            (this.metadatas = new LinkedHashMap<String, List<String>>()).putAll(ReflectionHelper.deepCopyMetadata((Map)copyMe.getMetadata()));
        }
    }
    
    public DescriptorImpl(final Set<String> contracts, final String name, final String scope, final String implementation, final Map<String, List<String>> metadatas, final Set<String> qualifiers, final DescriptorType descriptorType, final DescriptorVisibility descriptorVisibility, final HK2Loader loader, final int rank, final Boolean proxiable, final Boolean proxyForSameScope, final String analysisName, final Long id, final Long locatorId) {
        this.scope = PerLookup.class.getName();
        this.descriptorType = DescriptorType.CLASS;
        this.descriptorVisibility = DescriptorVisibility.NORMAL;
        if (contracts != null && !contracts.isEmpty()) {
            (this.contracts = new LinkedHashSet<String>()).addAll(contracts);
        }
        this.implementation = implementation;
        this.name = name;
        this.scope = scope;
        if (metadatas != null && !metadatas.isEmpty()) {
            (this.metadatas = new LinkedHashMap<String, List<String>>()).putAll(ReflectionHelper.deepCopyMetadata((Map)metadatas));
        }
        if (qualifiers != null && !qualifiers.isEmpty()) {
            (this.qualifiers = new LinkedHashSet<String>()).addAll(qualifiers);
        }
        this.descriptorType = descriptorType;
        this.descriptorVisibility = descriptorVisibility;
        this.id = id;
        this.rank = rank;
        this.proxiable = proxiable;
        this.proxyForSameScope = proxyForSameScope;
        this.analysisName = analysisName;
        this.locatorId = locatorId;
        this.loader = loader;
    }
    
    @Override
    public synchronized Set<String> getAdvertisedContracts() {
        if (this.contracts == null) {
            return DescriptorImpl.EMPTY_CONTRACTS_SET;
        }
        return Collections.unmodifiableSet((Set<? extends String>)this.contracts);
    }
    
    public synchronized void addAdvertisedContract(final String addMe) {
        if (addMe == null) {
            return;
        }
        if (this.contracts == null) {
            this.contracts = new LinkedHashSet<String>();
        }
        this.contracts.add(addMe);
    }
    
    public synchronized boolean removeAdvertisedContract(final String removeMe) {
        return removeMe != null && this.contracts != null && this.contracts.remove(removeMe);
    }
    
    @Override
    public synchronized String getImplementation() {
        return this.implementation;
    }
    
    public synchronized void setImplementation(final String implementation) {
        this.implementation = implementation;
    }
    
    @Override
    public synchronized String getScope() {
        return this.scope;
    }
    
    public synchronized void setScope(final String scope) {
        this.scope = scope;
    }
    
    @Override
    public synchronized String getName() {
        return this.name;
    }
    
    public synchronized void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public synchronized Set<String> getQualifiers() {
        if (this.qualifiers == null) {
            return DescriptorImpl.EMPTY_QUALIFIER_SET;
        }
        return Collections.unmodifiableSet((Set<? extends String>)this.qualifiers);
    }
    
    public synchronized void addQualifier(final String addMe) {
        if (addMe == null) {
            return;
        }
        if (this.qualifiers == null) {
            this.qualifiers = new LinkedHashSet<String>();
        }
        this.qualifiers.add(addMe);
    }
    
    public synchronized boolean removeQualifier(final String removeMe) {
        return removeMe != null && this.qualifiers != null && this.qualifiers.remove(removeMe);
    }
    
    @Override
    public synchronized DescriptorType getDescriptorType() {
        return this.descriptorType;
    }
    
    public synchronized void setDescriptorType(final DescriptorType descriptorType) {
        if (descriptorType == null) {
            throw new IllegalArgumentException();
        }
        this.descriptorType = descriptorType;
    }
    
    @Override
    public synchronized DescriptorVisibility getDescriptorVisibility() {
        return this.descriptorVisibility;
    }
    
    public synchronized void setDescriptorVisibility(final DescriptorVisibility descriptorVisibility) {
        if (descriptorVisibility == null) {
            throw new IllegalArgumentException();
        }
        this.descriptorVisibility = descriptorVisibility;
    }
    
    @Override
    public synchronized Map<String, List<String>> getMetadata() {
        if (this.metadatas == null) {
            return DescriptorImpl.EMPTY_METADATAS_MAP;
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends List<String>>)this.metadatas);
    }
    
    public synchronized void setMetadata(final Map<String, List<String>> metadata) {
        if (this.metadatas == null) {
            this.metadatas = new LinkedHashMap<String, List<String>>();
        }
        else {
            this.metadatas.clear();
        }
        this.metadatas.putAll(ReflectionHelper.deepCopyMetadata((Map)metadata));
    }
    
    public synchronized void addMetadata(final Map<String, List<String>> metadata) {
        if (this.metadatas == null) {
            this.metadatas = new LinkedHashMap<String, List<String>>();
        }
        this.metadatas.putAll(ReflectionHelper.deepCopyMetadata((Map)metadata));
    }
    
    public synchronized void addMetadata(final String key, final String value) {
        if (this.metadatas == null) {
            this.metadatas = new LinkedHashMap<String, List<String>>();
        }
        ReflectionHelper.addMetadata((Map)this.metadatas, key, value);
    }
    
    public synchronized boolean removeMetadata(final String key, final String value) {
        return this.metadatas != null && ReflectionHelper.removeMetadata((Map)this.metadatas, key, value);
    }
    
    public synchronized boolean removeAllMetadata(final String key) {
        return this.metadatas != null && ReflectionHelper.removeAllMetadata((Map)this.metadatas, key);
    }
    
    public synchronized void clearMetadata() {
        this.metadatas = null;
    }
    
    @Override
    public synchronized HK2Loader getLoader() {
        return this.loader;
    }
    
    public synchronized void setLoader(final HK2Loader loader) {
        this.loader = loader;
    }
    
    @Override
    public synchronized int getRanking() {
        return this.rank;
    }
    
    @Override
    public synchronized int setRanking(final int ranking) {
        final int retVal = this.rank;
        this.rank = ranking;
        return retVal;
    }
    
    @Override
    public synchronized Long getServiceId() {
        return this.id;
    }
    
    public synchronized void setServiceId(final Long id) {
        this.id = id;
    }
    
    @Override
    public Boolean isProxiable() {
        return this.proxiable;
    }
    
    public void setProxiable(final Boolean proxiable) {
        this.proxiable = proxiable;
    }
    
    @Override
    public Boolean isProxyForSameScope() {
        return this.proxyForSameScope;
    }
    
    public void setProxyForSameScope(final Boolean proxyForSameScope) {
        this.proxyForSameScope = proxyForSameScope;
    }
    
    @Override
    public String getClassAnalysisName() {
        return this.analysisName;
    }
    
    public void setClassAnalysisName(final String name) {
        this.analysisName = name;
    }
    
    @Override
    public synchronized Long getLocatorId() {
        return this.locatorId;
    }
    
    public synchronized void setLocatorId(final Long locatorId) {
        this.locatorId = locatorId;
    }
    
    @Override
    public int hashCode() {
        int retVal = 0;
        if (this.implementation != null) {
            retVal ^= this.implementation.hashCode();
        }
        if (this.contracts != null) {
            for (final String contract : this.contracts) {
                retVal ^= contract.hashCode();
            }
        }
        if (this.name != null) {
            retVal ^= this.name.hashCode();
        }
        if (this.scope != null) {
            retVal ^= this.scope.hashCode();
        }
        if (this.qualifiers != null) {
            for (final String qualifier : this.qualifiers) {
                retVal ^= qualifier.hashCode();
            }
        }
        if (this.descriptorType != null) {
            retVal ^= this.descriptorType.hashCode();
        }
        if (this.descriptorVisibility != null) {
            retVal ^= this.descriptorVisibility.hashCode();
        }
        if (this.metadatas != null) {
            for (final Map.Entry<String, List<String>> entries : this.metadatas.entrySet()) {
                retVal ^= entries.getKey().hashCode();
                for (final String value : entries.getValue()) {
                    retVal ^= value.hashCode();
                }
            }
        }
        if (this.proxiable != null) {
            if (this.proxiable) {
                retVal ^= 0x1;
            }
            else {
                retVal ^= -1;
            }
        }
        if (this.proxyForSameScope != null) {
            if (this.proxyForSameScope) {
                retVal ^= 0x2;
            }
            else {
                retVal ^= 0xFFFFFFFE;
            }
        }
        if (this.analysisName != null) {
            retVal ^= this.analysisName.hashCode();
        }
        return retVal;
    }
    
    private static <T> boolean equalOrderedCollection(final Collection<T> a, final Collection<T> b) {
        if (a == b) {
            return true;
        }
        if (a == null) {
            return false;
        }
        if (b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            return false;
        }
        final Object[] aAsArray = a.toArray();
        final Object[] bAsArray = b.toArray();
        for (int lcv = 0; lcv < a.size(); ++lcv) {
            if (!GeneralUtilities.safeEquals(aAsArray[lcv], bAsArray[lcv])) {
                return false;
            }
        }
        return true;
    }
    
    private static <T> boolean equalMetadata(final Map<String, List<String>> a, final Map<String, List<String>> b) {
        if (a == b) {
            return true;
        }
        if (a == null) {
            return false;
        }
        if (b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            return false;
        }
        for (final Map.Entry<String, List<String>> entry : a.entrySet()) {
            final String aKey = entry.getKey();
            final List<String> aValue = entry.getValue();
            final List<String> bValue = b.get(aKey);
            if (bValue == null) {
                return false;
            }
            if (!equalOrderedCollection(aValue, bValue)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean descriptorEquals(final Descriptor a, final Descriptor b) {
        return (a == null && b == null) || (a != null && b != null && GeneralUtilities.safeEquals((Object)a.getImplementation(), (Object)b.getImplementation()) && equalOrderedCollection(a.getAdvertisedContracts(), b.getAdvertisedContracts()) && GeneralUtilities.safeEquals((Object)a.getName(), (Object)b.getName()) && GeneralUtilities.safeEquals((Object)a.getScope(), (Object)b.getScope()) && equalOrderedCollection(a.getQualifiers(), b.getQualifiers()) && GeneralUtilities.safeEquals((Object)a.getDescriptorType(), (Object)b.getDescriptorType()) && GeneralUtilities.safeEquals((Object)a.getDescriptorVisibility(), (Object)b.getDescriptorVisibility()) && equalMetadata(a.getMetadata(), b.getMetadata()) && GeneralUtilities.safeEquals((Object)a.isProxiable(), (Object)b.isProxiable()) && GeneralUtilities.safeEquals((Object)a.isProxyForSameScope(), (Object)b.isProxyForSameScope()) && GeneralUtilities.safeEquals((Object)a.getClassAnalysisName(), (Object)b.getClassAnalysisName()));
    }
    
    @Override
    public boolean equals(final Object a) {
        if (a == null) {
            return false;
        }
        if (!(a instanceof Descriptor)) {
            return false;
        }
        final Descriptor d = (Descriptor)a;
        return descriptorEquals(this, d);
    }
    
    public static void pretty(final StringBuffer sb, final Descriptor d) {
        if (sb == null || d == null) {
            return;
        }
        sb.append("\n\timplementation=" + d.getImplementation());
        if (d.getName() != null) {
            sb.append("\n\tname=" + d.getName());
        }
        sb.append("\n\tcontracts=");
        sb.append(ReflectionHelper.writeSet((Set)d.getAdvertisedContracts()));
        sb.append("\n\tscope=" + d.getScope());
        sb.append("\n\tqualifiers=");
        sb.append(ReflectionHelper.writeSet((Set)d.getQualifiers()));
        sb.append("\n\tdescriptorType=" + d.getDescriptorType());
        sb.append("\n\tdescriptorVisibility=" + d.getDescriptorVisibility());
        sb.append("\n\tmetadata=");
        sb.append(ReflectionHelper.writeMetadata((Map)d.getMetadata()));
        sb.append("\n\trank=" + d.getRanking());
        sb.append("\n\tloader=" + d.getLoader());
        sb.append("\n\tproxiable=" + d.isProxiable());
        sb.append("\n\tproxyForSameScope=" + d.isProxyForSameScope());
        sb.append("\n\tanalysisName=" + d.getClassAnalysisName());
        sb.append("\n\tid=" + d.getServiceId());
        sb.append("\n\tlocatorId=" + d.getLocatorId());
        sb.append("\n\tidentityHashCode=" + System.identityHashCode(d));
    }
    
    @Override
    public synchronized String toString() {
        final StringBuffer sb = new StringBuffer("Descriptor(");
        pretty(sb, this);
        sb.append(")");
        return sb.toString();
    }
    
    public void writeObject(final PrintWriter out) throws IOException {
        out.print("[");
        if (this.implementation != null) {
            out.print(this.implementation);
        }
        out.print("]");
        if (this.scope != null && this.scope.equals(Singleton.class.getName())) {
            out.print("S");
        }
        boolean implementationInContracts = true;
        if (this.contracts != null && this.implementation != null && !this.contracts.contains(this.implementation)) {
            out.print("-");
            implementationInContracts = false;
        }
        out.println();
        if (this.contracts != null && !this.contracts.isEmpty() && (!implementationInContracts || this.contracts.size() > 1)) {
            final String excluded = implementationInContracts ? this.implementation : null;
            out.println("contract=" + ReflectionHelper.writeSet((Set)this.contracts, (Object)excluded));
        }
        if (this.name != null) {
            out.println("name=" + this.name);
        }
        if (this.scope != null && !this.scope.equals(PerLookup.class.getName()) && !this.scope.equals(Singleton.class.getName())) {
            out.println("scope=" + this.scope);
        }
        if (this.qualifiers != null && !this.qualifiers.isEmpty()) {
            out.println("qualifier=" + ReflectionHelper.writeSet((Set)this.qualifiers));
        }
        if (this.descriptorType != null && this.descriptorType.equals(DescriptorType.PROVIDE_METHOD)) {
            out.println("type=PROVIDE");
        }
        if (this.descriptorVisibility != null && this.descriptorVisibility.equals(DescriptorVisibility.LOCAL)) {
            out.println("visibility=LOCAL");
        }
        if (this.rank != 0) {
            out.println("rank=" + this.rank);
        }
        if (this.proxiable != null) {
            out.println("proxiable=" + (boolean)this.proxiable);
        }
        if (this.proxyForSameScope != null) {
            out.println("proxyForSameScope=" + (boolean)this.proxyForSameScope);
        }
        if (this.analysisName != null && !"default".equals(this.analysisName)) {
            out.println("analysis=" + this.analysisName);
        }
        if (this.metadatas != null && !this.metadatas.isEmpty()) {
            out.println("metadata=" + ReflectionHelper.writeMetadata((Map)this.metadatas));
        }
        out.println();
    }
    
    private void reinitialize() {
        this.contracts = null;
        this.implementation = null;
        this.name = null;
        this.scope = PerLookup.class.getName();
        this.metadatas = null;
        this.qualifiers = null;
        this.descriptorType = DescriptorType.CLASS;
        this.descriptorVisibility = DescriptorVisibility.NORMAL;
        this.loader = null;
        this.rank = 0;
        this.proxiable = null;
        this.proxyForSameScope = null;
        this.analysisName = null;
        this.id = null;
        this.locatorId = null;
    }
    
    public boolean readObject(final BufferedReader in) throws IOException {
        this.reinitialize();
        String line = in.readLine();
        boolean sectionStarted = false;
        while (line != null) {
            final String trimmed = line.trim();
            if (!sectionStarted) {
                if (trimmed.startsWith("[")) {
                    sectionStarted = true;
                    final int endStartIndex = trimmed.indexOf(93, 1);
                    if (endStartIndex < 0) {
                        throw new IOException("Start of implementation ends without ] character: " + trimmed);
                    }
                    if (endStartIndex > 1) {
                        this.implementation = trimmed.substring(1, endStartIndex);
                    }
                    final String directives = trimmed.substring(endStartIndex + 1);
                    boolean doesNotContainImplementation = false;
                    if (directives != null) {
                        for (int lcv = 0; lcv < directives.length(); ++lcv) {
                            final char charAt = directives.charAt(lcv);
                            if (charAt == 'S') {
                                this.scope = Singleton.class.getName();
                            }
                            else if (charAt == '-') {
                                doesNotContainImplementation = true;
                            }
                        }
                    }
                    if (!doesNotContainImplementation && this.implementation != null) {
                        if (this.contracts == null) {
                            this.contracts = new LinkedHashSet<String>();
                        }
                        this.contracts.add(this.implementation);
                    }
                }
            }
            else {
                if (trimmed.length() <= 0) {
                    return true;
                }
                final int equalsIndex = trimmed.indexOf(61);
                if (equalsIndex >= 1) {
                    final String leftHandSide = trimmed.substring(0, equalsIndex + 1);
                    final String rightHandSide = trimmed.substring(equalsIndex + 1);
                    if (leftHandSide.equalsIgnoreCase("contract=")) {
                        if (this.contracts == null) {
                            this.contracts = new LinkedHashSet<String>();
                        }
                        ReflectionHelper.readSet(rightHandSide, (Collection)this.contracts);
                    }
                    else if (leftHandSide.equals("qualifier=")) {
                        final LinkedHashSet<String> localQualifiers = new LinkedHashSet<String>();
                        ReflectionHelper.readSet(rightHandSide, (Collection)localQualifiers);
                        if (!localQualifiers.isEmpty()) {
                            this.qualifiers = localQualifiers;
                        }
                    }
                    else if (leftHandSide.equals("name=")) {
                        this.name = rightHandSide;
                    }
                    else if (leftHandSide.equals("scope=")) {
                        this.scope = rightHandSide;
                    }
                    else if (leftHandSide.equals("type=")) {
                        if (rightHandSide.equals("PROVIDE")) {
                            this.descriptorType = DescriptorType.PROVIDE_METHOD;
                        }
                    }
                    else if (leftHandSide.equals("visibility=")) {
                        if (rightHandSide.equals("LOCAL")) {
                            this.descriptorVisibility = DescriptorVisibility.LOCAL;
                        }
                    }
                    else if (leftHandSide.equals("metadata=")) {
                        final LinkedHashMap<String, List<String>> localMetadatas = new LinkedHashMap<String, List<String>>();
                        ReflectionHelper.readMetadataMap(rightHandSide, (Map)localMetadatas);
                        if (!localMetadatas.isEmpty()) {
                            this.metadatas = localMetadatas;
                        }
                    }
                    else if (leftHandSide.equals("rank=")) {
                        this.rank = Integer.parseInt(rightHandSide);
                    }
                    else if (leftHandSide.equals("proxiable=")) {
                        this.proxiable = Boolean.parseBoolean(rightHandSide);
                    }
                    else if (leftHandSide.equals("proxyForSameScope=")) {
                        this.proxyForSameScope = Boolean.parseBoolean(rightHandSide);
                    }
                    else if (leftHandSide.equals("analysis=")) {
                        this.analysisName = rightHandSide;
                    }
                }
            }
            line = in.readLine();
        }
        return sectionStarted;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        final StringWriter sw = new StringWriter();
        this.writeObject(new PrintWriter(sw));
        out.writeObject(sw.toString());
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final String descriptorString = (String)in.readObject();
        this.readObject(new BufferedReader(new StringReader(descriptorString)));
    }
    
    static {
        EMPTY_CONTRACTS_SET = Collections.emptySet();
        EMPTY_QUALIFIER_SET = Collections.emptySet();
        EMPTY_METADATAS_MAP = Collections.emptyMap();
    }
}
