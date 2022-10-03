package com.sun.xml.internal.ws.binding;

import com.sun.xml.internal.ws.api.WSFeatureList;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.developer.BindingTypeFeature;
import javax.activation.CommandInfo;
import javax.activation.MailcapCommandMap;
import javax.activation.CommandMap;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import javax.xml.ws.soap.AddressingFeature;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.istack.internal.NotNull;
import java.util.Collection;
import javax.xml.ws.handler.Handler;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.HashSet;
import com.oracle.webservices.internal.api.message.MessageContextFactory;
import javax.xml.ws.Service;
import java.util.Map;
import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.WSBinding;

public abstract class BindingImpl implements WSBinding
{
    protected static final WebServiceFeature[] EMPTY_FEATURES;
    private HandlerConfiguration handlerConfig;
    private final Set<QName> addedHeaders;
    private final Set<QName> knownHeaders;
    private final Set<QName> unmodKnownHeaders;
    private final BindingID bindingId;
    protected final WebServiceFeatureList features;
    protected final Map<QName, WebServiceFeatureList> operationFeatures;
    protected final Map<QName, WebServiceFeatureList> inputMessageFeatures;
    protected final Map<QName, WebServiceFeatureList> outputMessageFeatures;
    protected final Map<MessageKey, WebServiceFeatureList> faultMessageFeatures;
    protected Service.Mode serviceMode;
    protected MessageContextFactory messageContextFactory;
    
    protected BindingImpl(final BindingID bindingId, final WebServiceFeature... features) {
        this.addedHeaders = new HashSet<QName>();
        this.knownHeaders = new HashSet<QName>();
        this.unmodKnownHeaders = Collections.unmodifiableSet((Set<? extends QName>)this.knownHeaders);
        this.operationFeatures = new HashMap<QName, WebServiceFeatureList>();
        this.inputMessageFeatures = new HashMap<QName, WebServiceFeatureList>();
        this.outputMessageFeatures = new HashMap<QName, WebServiceFeatureList>();
        this.faultMessageFeatures = new HashMap<MessageKey, WebServiceFeatureList>();
        this.serviceMode = Service.Mode.PAYLOAD;
        this.bindingId = bindingId;
        this.handlerConfig = new HandlerConfiguration(Collections.emptySet(), (List<Handler>)Collections.emptyList());
        if (this.handlerConfig.getHandlerKnownHeaders() != null) {
            this.knownHeaders.addAll(this.handlerConfig.getHandlerKnownHeaders());
        }
        (this.features = new WebServiceFeatureList(features)).validate();
    }
    
    @NotNull
    @Override
    public List<Handler> getHandlerChain() {
        return this.handlerConfig.getHandlerChain();
    }
    
    public HandlerConfiguration getHandlerConfig() {
        return this.handlerConfig;
    }
    
    protected void setHandlerConfig(final HandlerConfiguration handlerConfig) {
        this.handlerConfig = handlerConfig;
        this.knownHeaders.clear();
        this.knownHeaders.addAll(this.addedHeaders);
        if (handlerConfig != null && handlerConfig.getHandlerKnownHeaders() != null) {
            this.knownHeaders.addAll(handlerConfig.getHandlerKnownHeaders());
        }
    }
    
    public void setMode(@NotNull final Service.Mode mode) {
        this.serviceMode = mode;
    }
    
    @Override
    public Set<QName> getKnownHeaders() {
        return this.unmodKnownHeaders;
    }
    
    @Override
    public boolean addKnownHeader(final QName headerQName) {
        this.addedHeaders.add(headerQName);
        return this.knownHeaders.add(headerQName);
    }
    
    @NotNull
    @Override
    public BindingID getBindingId() {
        return this.bindingId;
    }
    
    @Override
    public final SOAPVersion getSOAPVersion() {
        return this.bindingId.getSOAPVersion();
    }
    
    @Override
    public AddressingVersion getAddressingVersion() {
        AddressingVersion addressingVersion;
        if (this.features.isEnabled(AddressingFeature.class)) {
            addressingVersion = AddressingVersion.W3C;
        }
        else if (this.features.isEnabled(MemberSubmissionAddressingFeature.class)) {
            addressingVersion = AddressingVersion.MEMBER;
        }
        else {
            addressingVersion = null;
        }
        return addressingVersion;
    }
    
    @NotNull
    public final Codec createCodec() {
        initializeJavaActivationHandlers();
        return this.bindingId.createEncoder(this);
    }
    
    public static void initializeJavaActivationHandlers() {
        try {
            final CommandMap map = CommandMap.getDefaultCommandMap();
            if (map instanceof MailcapCommandMap) {
                final MailcapCommandMap mailMap = (MailcapCommandMap)map;
                if (!cmdMapInitialized(mailMap)) {
                    mailMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
                    mailMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
                    mailMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.ws.encoding.ImageDataContentHandler");
                    mailMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.ws.encoding.StringDataContentHandler");
                }
            }
        }
        catch (final Throwable t) {}
    }
    
    private static boolean cmdMapInitialized(final MailcapCommandMap mailMap) {
        final CommandInfo[] commands = mailMap.getAllCommands("text/xml");
        if (commands == null || commands.length == 0) {
            return false;
        }
        final String saajClassName = "com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler";
        final String jaxwsClassName = "com.sun.xml.internal.ws.encoding.XmlDataContentHandler";
        for (final CommandInfo command : commands) {
            final String commandClass = command.getCommandClass();
            if (saajClassName.equals(commandClass) || jaxwsClassName.equals(commandClass)) {
                return true;
            }
        }
        return false;
    }
    
    public static BindingImpl create(@NotNull final BindingID bindingId) {
        if (bindingId.equals(BindingID.XML_HTTP)) {
            return new HTTPBindingImpl();
        }
        return new SOAPBindingImpl(bindingId);
    }
    
    public static BindingImpl create(@NotNull BindingID bindingId, final WebServiceFeature[] features) {
        for (final WebServiceFeature feature : features) {
            if (feature instanceof BindingTypeFeature) {
                final BindingTypeFeature f = (BindingTypeFeature)feature;
                bindingId = BindingID.parse(f.getBindingId());
            }
        }
        if (bindingId.equals(BindingID.XML_HTTP)) {
            return new HTTPBindingImpl(features);
        }
        return new SOAPBindingImpl(bindingId, features);
    }
    
    public static WSBinding getDefaultBinding() {
        return new SOAPBindingImpl(BindingID.SOAP11_HTTP);
    }
    
    @Override
    public String getBindingID() {
        return this.bindingId.toString();
    }
    
    @Nullable
    @Override
    public <F extends WebServiceFeature> F getFeature(@NotNull final Class<F> featureType) {
        return this.features.get(featureType);
    }
    
    @Nullable
    @Override
    public <F extends WebServiceFeature> F getOperationFeature(@NotNull final Class<F> featureType, @NotNull final QName operationName) {
        final WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        return FeatureListUtil.mergeFeature(featureType, operationFeatureList, this.features);
    }
    
    @Override
    public boolean isFeatureEnabled(@NotNull final Class<? extends WebServiceFeature> feature) {
        return this.features.isEnabled(feature);
    }
    
    @Override
    public boolean isOperationFeatureEnabled(@NotNull final Class<? extends WebServiceFeature> featureType, @NotNull final QName operationName) {
        final WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        return FeatureListUtil.isFeatureEnabled(featureType, operationFeatureList, this.features);
    }
    
    @NotNull
    @Override
    public WebServiceFeatureList getFeatures() {
        if (!this.isFeatureEnabled(EnvelopeStyleFeature.class)) {
            final WebServiceFeature[] f = { this.getSOAPVersion().toFeature() };
            this.features.mergeFeatures(f, false);
        }
        return this.features;
    }
    
    @NotNull
    @Override
    public WebServiceFeatureList getOperationFeatures(@NotNull final QName operationName) {
        final WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        return FeatureListUtil.mergeList(operationFeatureList, this.features);
    }
    
    @NotNull
    @Override
    public WebServiceFeatureList getInputMessageFeatures(@NotNull final QName operationName) {
        final WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        final WebServiceFeatureList messageFeatureList = this.inputMessageFeatures.get(operationName);
        return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
    }
    
    @NotNull
    @Override
    public WebServiceFeatureList getOutputMessageFeatures(@NotNull final QName operationName) {
        final WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        final WebServiceFeatureList messageFeatureList = this.outputMessageFeatures.get(operationName);
        return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
    }
    
    @NotNull
    @Override
    public WebServiceFeatureList getFaultMessageFeatures(@NotNull final QName operationName, @NotNull final QName messageName) {
        final WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        final WebServiceFeatureList messageFeatureList = this.faultMessageFeatures.get(new MessageKey(operationName, messageName));
        return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
    }
    
    public void setOperationFeatures(@NotNull final QName operationName, final WebServiceFeature... newFeatures) {
        if (newFeatures != null) {
            WebServiceFeatureList featureList = this.operationFeatures.get(operationName);
            if (featureList == null) {
                featureList = new WebServiceFeatureList();
            }
            for (final WebServiceFeature f : newFeatures) {
                featureList.add(f);
            }
            this.operationFeatures.put(operationName, featureList);
        }
    }
    
    public void setInputMessageFeatures(@NotNull final QName operationName, final WebServiceFeature... newFeatures) {
        if (newFeatures != null) {
            WebServiceFeatureList featureList = this.inputMessageFeatures.get(operationName);
            if (featureList == null) {
                featureList = new WebServiceFeatureList();
            }
            for (final WebServiceFeature f : newFeatures) {
                featureList.add(f);
            }
            this.inputMessageFeatures.put(operationName, featureList);
        }
    }
    
    public void setOutputMessageFeatures(@NotNull final QName operationName, final WebServiceFeature... newFeatures) {
        if (newFeatures != null) {
            WebServiceFeatureList featureList = this.outputMessageFeatures.get(operationName);
            if (featureList == null) {
                featureList = new WebServiceFeatureList();
            }
            for (final WebServiceFeature f : newFeatures) {
                featureList.add(f);
            }
            this.outputMessageFeatures.put(operationName, featureList);
        }
    }
    
    public void setFaultMessageFeatures(@NotNull final QName operationName, @NotNull final QName messageName, final WebServiceFeature... newFeatures) {
        if (newFeatures != null) {
            final MessageKey key = new MessageKey(operationName, messageName);
            WebServiceFeatureList featureList = this.faultMessageFeatures.get(key);
            if (featureList == null) {
                featureList = new WebServiceFeatureList();
            }
            for (final WebServiceFeature f : newFeatures) {
                featureList.add(f);
            }
            this.faultMessageFeatures.put(key, featureList);
        }
    }
    
    @NotNull
    @Override
    public synchronized MessageContextFactory getMessageContextFactory() {
        if (this.messageContextFactory == null) {
            this.messageContextFactory = MessageContextFactory.createFactory(this.getFeatures().toArray());
        }
        return this.messageContextFactory;
    }
    
    static {
        EMPTY_FEATURES = new WebServiceFeature[0];
    }
    
    protected static class MessageKey
    {
        private final QName operationName;
        private final QName messageName;
        
        public MessageKey(final QName operationName, final QName messageName) {
            this.operationName = operationName;
            this.messageName = messageName;
        }
        
        @Override
        public int hashCode() {
            final int hashFirst = (this.operationName != null) ? this.operationName.hashCode() : 0;
            final int hashSecond = (this.messageName != null) ? this.messageName.hashCode() : 0;
            return (hashFirst + hashSecond) * hashSecond + hashFirst;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final MessageKey other = (MessageKey)obj;
            return (this.operationName == other.operationName || (this.operationName != null && this.operationName.equals(other.operationName))) && (this.messageName == other.messageName || (this.messageName != null && this.messageName.equals(other.messageName)));
        }
        
        @Override
        public String toString() {
            return "(" + this.operationName + ", " + this.messageName + ")";
        }
    }
}
