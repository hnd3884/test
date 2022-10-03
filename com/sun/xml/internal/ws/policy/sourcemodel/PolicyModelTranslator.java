package com.sun.xml.internal.ws.policy.sourcemodel;

import java.util.ArrayList;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.List;
import java.util.Queue;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import java.util.Iterator;
import java.util.Collections;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.HashMap;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Collection;
import java.util.Map;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class PolicyModelTranslator
{
    private static final PolicyLogger LOGGER;
    private static final PolicyAssertionCreator defaultCreator;
    private final Map<String, PolicyAssertionCreator> assertionCreators;
    
    private PolicyModelTranslator() throws PolicyException {
        this(null);
    }
    
    protected PolicyModelTranslator(final Collection<PolicyAssertionCreator> creators) throws PolicyException {
        PolicyModelTranslator.LOGGER.entering(creators);
        final Collection<PolicyAssertionCreator> allCreators = new LinkedList<PolicyAssertionCreator>();
        final PolicyAssertionCreator[] array;
        final PolicyAssertionCreator[] discoveredCreators = array = PolicyUtils.ServiceProvider.load(PolicyAssertionCreator.class);
        for (final PolicyAssertionCreator creator : array) {
            allCreators.add(creator);
        }
        if (creators != null) {
            for (final PolicyAssertionCreator creator2 : creators) {
                allCreators.add(creator2);
            }
        }
        final Map<String, PolicyAssertionCreator> pacMap = new HashMap<String, PolicyAssertionCreator>();
        for (final PolicyAssertionCreator creator3 : allCreators) {
            final String[] supportedURIs = creator3.getSupportedDomainNamespaceURIs();
            final String creatorClassName = creator3.getClass().getName();
            if (supportedURIs == null || supportedURIs.length == 0) {
                PolicyModelTranslator.LOGGER.warning(LocalizationMessages.WSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI(creatorClassName));
            }
            else {
                for (final String supportedURI : supportedURIs) {
                    PolicyModelTranslator.LOGGER.config(LocalizationMessages.WSP_0078_ASSERTION_CREATOR_DISCOVERED(creatorClassName, supportedURI));
                    if (supportedURI == null || supportedURI.length() == 0) {
                        throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR(creatorClassName)));
                    }
                    final PolicyAssertionCreator oldCreator = pacMap.put(supportedURI, creator3);
                    if (oldCreator != null) {
                        throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE(supportedURI, oldCreator.getClass().getName(), creator3.getClass().getName())));
                    }
                }
            }
        }
        this.assertionCreators = Collections.unmodifiableMap((Map<? extends String, ? extends PolicyAssertionCreator>)pacMap);
        PolicyModelTranslator.LOGGER.exiting();
    }
    
    public static PolicyModelTranslator getTranslator() throws PolicyException {
        return new PolicyModelTranslator();
    }
    
    public Policy translate(final PolicySourceModel model) throws PolicyException {
        PolicyModelTranslator.LOGGER.entering(model);
        if (model == null) {
            throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL()));
        }
        PolicySourceModel localPolicyModelCopy;
        try {
            localPolicyModelCopy = model.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL(), e));
        }
        final String policyId = localPolicyModelCopy.getPolicyId();
        final String policyName = localPolicyModelCopy.getPolicyName();
        final Collection<AssertionSet> alternatives = this.createPolicyAlternatives(localPolicyModelCopy);
        PolicyModelTranslator.LOGGER.finest(LocalizationMessages.WSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED(alternatives.size()));
        Policy policy = null;
        if (alternatives.size() == 0) {
            policy = Policy.createNullPolicy(model.getNamespaceVersion(), policyName, policyId);
            PolicyModelTranslator.LOGGER.finest(LocalizationMessages.WSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED());
        }
        else if (alternatives.size() == 1 && alternatives.iterator().next().isEmpty()) {
            policy = Policy.createEmptyPolicy(model.getNamespaceVersion(), policyName, policyId);
            PolicyModelTranslator.LOGGER.finest(LocalizationMessages.WSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED());
        }
        else {
            policy = Policy.createPolicy(model.getNamespaceVersion(), policyName, policyId, alternatives);
            PolicyModelTranslator.LOGGER.finest(LocalizationMessages.WSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED(alternatives.size(), policy.getNumberOfAssertionSets()));
        }
        PolicyModelTranslator.LOGGER.exiting(policy);
        return policy;
    }
    
    private Collection<AssertionSet> createPolicyAlternatives(final PolicySourceModel model) throws PolicyException {
        final ContentDecomposition decomposition = new ContentDecomposition();
        final Queue<RawPolicy> policyQueue = new LinkedList<RawPolicy>();
        final Queue<Collection<ModelNode>> contentQueue = new LinkedList<Collection<ModelNode>>();
        RawPolicy processedPolicy;
        final RawPolicy rootPolicy = processedPolicy = new RawPolicy(model.getRootNode(), new LinkedList<RawAlternative>());
        do {
            Collection<ModelNode> processedContent = processedPolicy.originalContent;
            do {
                this.decompose(processedContent, decomposition);
                if (decomposition.exactlyOneContents.isEmpty()) {
                    final RawAlternative alternative = new RawAlternative(decomposition.assertions);
                    processedPolicy.alternatives.add(alternative);
                    if (alternative.allNestedPolicies.isEmpty()) {
                        continue;
                    }
                    policyQueue.addAll((Collection<?>)alternative.allNestedPolicies);
                }
                else {
                    final Collection<Collection<ModelNode>> combinations = PolicyUtils.Collections.combine(decomposition.assertions, decomposition.exactlyOneContents, false);
                    if (combinations == null || combinations.isEmpty()) {
                        continue;
                    }
                    contentQueue.addAll((Collection<?>)combinations);
                }
            } while ((processedContent = contentQueue.poll()) != null);
        } while ((processedPolicy = policyQueue.poll()) != null);
        final Collection<AssertionSet> assertionSets = new LinkedList<AssertionSet>();
        for (final RawAlternative rootAlternative : rootPolicy.alternatives) {
            final Collection<AssertionSet> normalizedAlternatives = this.normalizeRawAlternative(rootAlternative);
            assertionSets.addAll(normalizedAlternatives);
        }
        return assertionSets;
    }
    
    private void decompose(final Collection<ModelNode> content, final ContentDecomposition decomposition) throws PolicyException {
        decomposition.reset();
        final Queue<ModelNode> allContentQueue = new LinkedList<ModelNode>(content);
        ModelNode node;
        while ((node = allContentQueue.poll()) != null) {
            switch (node.getType()) {
                case POLICY:
                case ALL: {
                    allContentQueue.addAll((Collection<?>)node.getChildren());
                    continue;
                }
                case POLICY_REFERENCE: {
                    allContentQueue.addAll((Collection<?>)getReferencedModelRootNode(node).getChildren());
                    continue;
                }
                case EXACTLY_ONE: {
                    decomposition.exactlyOneContents.add(this.expandsExactlyOneContent(node.getChildren()));
                    continue;
                }
                case ASSERTION: {
                    decomposition.assertions.add(node);
                    continue;
                }
                default: {
                    throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND(node.getType())));
                }
            }
        }
    }
    
    private static ModelNode getReferencedModelRootNode(final ModelNode policyReferenceNode) throws PolicyException {
        final PolicySourceModel referencedModel = policyReferenceNode.getReferencedModel();
        if (referencedModel != null) {
            return referencedModel.getRootNode();
        }
        final PolicyReferenceData refData = policyReferenceNode.getPolicyReferenceData();
        if (refData == null) {
            throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT()));
        }
        throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING(refData.getReferencedModelUri())));
    }
    
    private Collection<ModelNode> expandsExactlyOneContent(final Collection<ModelNode> content) throws PolicyException {
        final Collection<ModelNode> result = new LinkedList<ModelNode>();
        final Queue<ModelNode> eoContentQueue = new LinkedList<ModelNode>(content);
        ModelNode node;
        while ((node = eoContentQueue.poll()) != null) {
            switch (node.getType()) {
                case POLICY:
                case ALL:
                case ASSERTION: {
                    result.add(node);
                    continue;
                }
                case POLICY_REFERENCE: {
                    result.add(getReferencedModelRootNode(node));
                    continue;
                }
                case EXACTLY_ONE: {
                    eoContentQueue.addAll((Collection<?>)node.getChildren());
                    continue;
                }
                default: {
                    throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0001_UNSUPPORTED_MODEL_NODE_TYPE(node.getType())));
                }
            }
        }
        return result;
    }
    
    private List<AssertionSet> normalizeRawAlternative(final RawAlternative alternative) throws AssertionCreationException, PolicyException {
        final List<PolicyAssertion> normalizedContentBase = new LinkedList<PolicyAssertion>();
        final Collection<List<PolicyAssertion>> normalizedContentOptions = new LinkedList<List<PolicyAssertion>>();
        if (!alternative.nestedAssertions.isEmpty()) {
            final Queue<RawAssertion> nestedAssertionsQueue = new LinkedList<RawAssertion>(alternative.nestedAssertions);
            RawAssertion rawAssertion;
            while ((rawAssertion = nestedAssertionsQueue.poll()) != null) {
                final List<PolicyAssertion> normalized = this.normalizeRawAssertion(rawAssertion);
                if (normalized.size() == 1) {
                    normalizedContentBase.addAll(normalized);
                }
                else {
                    normalizedContentOptions.add(normalized);
                }
            }
        }
        final List<AssertionSet> options = new LinkedList<AssertionSet>();
        if (normalizedContentOptions.isEmpty()) {
            options.add(AssertionSet.createAssertionSet(normalizedContentBase));
        }
        else {
            final Collection<Collection<PolicyAssertion>> contentCombinations = PolicyUtils.Collections.combine(normalizedContentBase, normalizedContentOptions, true);
            for (final Collection<PolicyAssertion> contentOption : contentCombinations) {
                options.add(AssertionSet.createAssertionSet(contentOption));
            }
        }
        return options;
    }
    
    private List<PolicyAssertion> normalizeRawAssertion(final RawAssertion assertion) throws AssertionCreationException, PolicyException {
        List<PolicyAssertion> parameters;
        if (assertion.parameters.isEmpty()) {
            parameters = null;
        }
        else {
            parameters = new ArrayList<PolicyAssertion>(assertion.parameters.size());
            for (final ModelNode parameterNode : assertion.parameters) {
                parameters.add(this.createPolicyAssertionParameter(parameterNode));
            }
        }
        final List<AssertionSet> nestedAlternatives = new LinkedList<AssertionSet>();
        if (assertion.nestedAlternatives != null && !assertion.nestedAlternatives.isEmpty()) {
            final Queue<RawAlternative> nestedAlternativeQueue = new LinkedList<RawAlternative>(assertion.nestedAlternatives);
            RawAlternative rawAlternative;
            while ((rawAlternative = nestedAlternativeQueue.poll()) != null) {
                nestedAlternatives.addAll(this.normalizeRawAlternative(rawAlternative));
            }
        }
        final List<PolicyAssertion> assertionOptions = new LinkedList<PolicyAssertion>();
        final boolean nestedAlternativesAvailable = !nestedAlternatives.isEmpty();
        if (nestedAlternativesAvailable) {
            for (final AssertionSet nestedAlternative : nestedAlternatives) {
                assertionOptions.add(this.createPolicyAssertion(assertion.originalNode.getNodeData(), parameters, nestedAlternative));
            }
        }
        else {
            assertionOptions.add(this.createPolicyAssertion(assertion.originalNode.getNodeData(), parameters, null));
        }
        return assertionOptions;
    }
    
    private PolicyAssertion createPolicyAssertionParameter(final ModelNode parameterNode) throws AssertionCreationException, PolicyException {
        if (parameterNode.getType() != ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            throw PolicyModelTranslator.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL(parameterNode.getType())));
        }
        List<PolicyAssertion> childParameters = null;
        if (parameterNode.hasChildren()) {
            childParameters = new ArrayList<PolicyAssertion>(parameterNode.childrenSize());
            for (final ModelNode childParameterNode : parameterNode) {
                childParameters.add(this.createPolicyAssertionParameter(childParameterNode));
            }
        }
        return this.createPolicyAssertion(parameterNode.getNodeData(), childParameters, null);
    }
    
    private PolicyAssertion createPolicyAssertion(final AssertionData data, final Collection<PolicyAssertion> assertionParameters, final AssertionSet nestedAlternative) throws AssertionCreationException {
        final String assertionNamespace = data.getName().getNamespaceURI();
        final PolicyAssertionCreator domainSpecificPAC = this.assertionCreators.get(assertionNamespace);
        if (domainSpecificPAC == null) {
            return PolicyModelTranslator.defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, null);
        }
        return domainSpecificPAC.createAssertion(data, assertionParameters, nestedAlternative, PolicyModelTranslator.defaultCreator);
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyModelTranslator.class);
        defaultCreator = new DefaultPolicyAssertionCreator();
    }
    
    private static final class ContentDecomposition
    {
        final List<Collection<ModelNode>> exactlyOneContents;
        final List<ModelNode> assertions;
        
        private ContentDecomposition() {
            this.exactlyOneContents = new LinkedList<Collection<ModelNode>>();
            this.assertions = new LinkedList<ModelNode>();
        }
        
        void reset() {
            this.exactlyOneContents.clear();
            this.assertions.clear();
        }
    }
    
    private static final class RawAssertion
    {
        ModelNode originalNode;
        Collection<RawAlternative> nestedAlternatives;
        final Collection<ModelNode> parameters;
        
        RawAssertion(final ModelNode originalNode, final Collection<ModelNode> parameters) {
            this.nestedAlternatives = null;
            this.parameters = parameters;
            this.originalNode = originalNode;
        }
    }
    
    private static final class RawAlternative
    {
        private static final PolicyLogger LOGGER;
        final List<RawPolicy> allNestedPolicies;
        final Collection<RawAssertion> nestedAssertions;
        
        RawAlternative(final Collection<ModelNode> assertionNodes) throws PolicyException {
            this.allNestedPolicies = new LinkedList<RawPolicy>();
            this.nestedAssertions = new LinkedList<RawAssertion>();
            for (final ModelNode node : assertionNodes) {
                final RawAssertion assertion = new RawAssertion(node, new LinkedList<ModelNode>());
                this.nestedAssertions.add(assertion);
                for (final ModelNode assertionNodeChild : assertion.originalNode.getChildren()) {
                    switch (assertionNodeChild.getType()) {
                        case ASSERTION_PARAMETER_NODE: {
                            assertion.parameters.add(assertionNodeChild);
                            continue;
                        }
                        case POLICY:
                        case POLICY_REFERENCE: {
                            if (assertion.nestedAlternatives == null) {
                                assertion.nestedAlternatives = new LinkedList<RawAlternative>();
                                RawPolicy nestedPolicy;
                                if (assertionNodeChild.getType() == ModelNode.Type.POLICY) {
                                    nestedPolicy = new RawPolicy(assertionNodeChild, assertion.nestedAlternatives);
                                }
                                else {
                                    nestedPolicy = new RawPolicy(getReferencedModelRootNode(assertionNodeChild), assertion.nestedAlternatives);
                                }
                                this.allNestedPolicies.add(nestedPolicy);
                                continue;
                            }
                            throw RawAlternative.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES()));
                        }
                        default: {
                            throw RawAlternative.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0008_UNEXPECTED_CHILD_MODEL_TYPE(assertionNodeChild.getType())));
                        }
                    }
                }
            }
        }
        
        static {
            LOGGER = PolicyLogger.getLogger(RawAlternative.class);
        }
    }
    
    private static final class RawPolicy
    {
        final Collection<ModelNode> originalContent;
        final Collection<RawAlternative> alternatives;
        
        RawPolicy(final ModelNode policyNode, final Collection<RawAlternative> alternatives) {
            this.originalContent = policyNode.getChildren();
            this.alternatives = alternatives;
        }
    }
}
