package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableSUBRES_LOC_CACHE_INVALID_SIZE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("subres.loc.cache.invalid.size", new Object[] { arg0, arg1 });
    }
    
    public static String SUBRES_LOC_CACHE_INVALID_SIZE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableSUBRES_LOC_CACHE_INVALID_SIZE(arg0, arg1));
    }
    
    public static Localizable localizableLOGGING_GLOBAL_REQUEST_FILTERS() {
        return LocalizationMessages.messageFactory.getMessage("logging.global.request.filters", new Object[0]);
    }
    
    public static String LOGGING_GLOBAL_REQUEST_FILTERS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_GLOBAL_REQUEST_FILTERS());
    }
    
    public static Localizable localizableERROR_PROCESSING_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.processing.method", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_PROCESSING_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_PROCESSING_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableCOLLECTION_EXTRACTOR_TYPE_UNSUPPORTED() {
        return LocalizationMessages.messageFactory.getMessage("collection.extractor.type.unsupported", new Object[0]);
    }
    
    public static String COLLECTION_EXTRACTOR_TYPE_UNSUPPORTED() {
        return LocalizationMessages.localizer.localize(localizableCOLLECTION_EXTRACTOR_TYPE_UNSUPPORTED());
    }
    
    public static Localizable localizableLOGGING_ROOT_RESOURCE_CLASSES() {
        return LocalizationMessages.messageFactory.getMessage("logging.root.resource.classes", new Object[0]);
    }
    
    public static String LOGGING_ROOT_RESOURCE_CLASSES() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_ROOT_RESOURCE_CLASSES());
    }
    
    public static Localizable localizableERROR_UNMARSHALLING_JAXB(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.unmarshalling.jaxb", new Object[] { arg0 });
    }
    
    public static String ERROR_UNMARSHALLING_JAXB(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_UNMARSHALLING_JAXB(arg0));
    }
    
    public static Localizable localizableJAR_SCANNER_UNABLE_TO_CLOSE_FILE() {
        return LocalizationMessages.messageFactory.getMessage("jar.scanner.unable.to.close.file", new Object[0]);
    }
    
    public static String JAR_SCANNER_UNABLE_TO_CLOSE_FILE() {
        return LocalizationMessages.localizer.localize(localizableJAR_SCANNER_UNABLE_TO_CLOSE_FILE());
    }
    
    public static Localizable localizableERROR_WADL_JAXB_CONTEXT() {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.jaxb.context", new Object[0]);
    }
    
    public static String ERROR_WADL_JAXB_CONTEXT() {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_JAXB_CONTEXT());
    }
    
    public static Localizable localizableERROR_WRITING_RESPONSE_ENTITY_CHUNK() {
        return LocalizationMessages.messageFactory.getMessage("error.writing.response.entity.chunk", new Object[0]);
    }
    
    public static String ERROR_WRITING_RESPONSE_ENTITY_CHUNK() {
        return LocalizationMessages.localizer.localize(localizableERROR_WRITING_RESPONSE_ENTITY_CHUNK());
    }
    
    public static Localizable localizableSUBRES_LOC_RETURNS_VOID(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("subres.loc.returns.void", new Object[] { arg0 });
    }
    
    public static String SUBRES_LOC_RETURNS_VOID(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSUBRES_LOC_RETURNS_VOID(arg0));
    }
    
    public static Localizable localizableERROR_WADL_GENERATOR_CONFIGURE() {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.generator.configure", new Object[0]);
    }
    
    public static String ERROR_WADL_GENERATOR_CONFIGURE() {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_GENERATOR_CONFIGURE());
    }
    
    public static Localizable localizableNON_INSTANTIABLE_COMPONENT(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("non.instantiable.component", new Object[] { arg0 });
    }
    
    public static String NON_INSTANTIABLE_COMPONENT(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNON_INSTANTIABLE_COMPONENT(arg0));
    }
    
    public static Localizable localizableCHUNKED_OUTPUT_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("chunked.output.closed", new Object[0]);
    }
    
    public static String CHUNKED_OUTPUT_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableCHUNKED_OUTPUT_CLOSED());
    }
    
    public static Localizable localizableERROR_ASYNC_CALLBACK_FAILED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.async.callback.failed", new Object[] { arg0 });
    }
    
    public static String ERROR_ASYNC_CALLBACK_FAILED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_ASYNC_CALLBACK_FAILED(arg0));
    }
    
    public static Localizable localizableWARNING_MONITORING_MBEANS_BEAN_ALREADY_REGISTERED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("warning.monitoring.mbeans.bean.already.registered", new Object[] { arg0 });
    }
    
    public static String WARNING_MONITORING_MBEANS_BEAN_ALREADY_REGISTERED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableWARNING_MONITORING_MBEANS_BEAN_ALREADY_REGISTERED(arg0));
    }
    
    public static Localizable localizableERROR_EXCEPTION_MAPPING_PROCESSED_RESPONSE_ERROR() {
        return LocalizationMessages.messageFactory.getMessage("error.exception.mapping.processed.response.error", new Object[0]);
    }
    
    public static String ERROR_EXCEPTION_MAPPING_PROCESSED_RESPONSE_ERROR() {
        return LocalizationMessages.localizer.localize(localizableERROR_EXCEPTION_MAPPING_PROCESSED_RESPONSE_ERROR());
    }
    
    public static Localizable localizableINVALID_MAPPING_TYPE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("invalid.mapping.type", new Object[] { arg0 });
    }
    
    public static String INVALID_MAPPING_TYPE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableINVALID_MAPPING_TYPE(arg0));
    }
    
    public static Localizable localizablePREMATCHING_ALSO_NAME_BOUND(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("prematching.also.name.bound", new Object[] { arg0 });
    }
    
    public static String PREMATCHING_ALSO_NAME_BOUND(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizablePREMATCHING_ALSO_NAME_BOUND(arg0));
    }
    
    public static Localizable localizableERROR_WADL_RESOURCE_EXTERNAL_GRAMMAR() {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.resource.external.grammar", new Object[0]);
    }
    
    public static String ERROR_WADL_RESOURCE_EXTERNAL_GRAMMAR() {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_RESOURCE_EXTERNAL_GRAMMAR());
    }
    
    public static Localizable localizableERROR_WADL_EXTERNAL_GRAMMAR() {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.external.grammar", new Object[0]);
    }
    
    public static String ERROR_WADL_EXTERNAL_GRAMMAR() {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_EXTERNAL_GRAMMAR());
    }
    
    public static Localizable localizableRESOURCE_ADD_CHILD_ALREADY_CHILD() {
        return LocalizationMessages.messageFactory.getMessage("resource.add.child.already.child", new Object[0]);
    }
    
    public static String RESOURCE_ADD_CHILD_ALREADY_CHILD() {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_ADD_CHILD_ALREADY_CHILD());
    }
    
    public static Localizable localizableRESOURCE_CONFIG_ERROR_NULL_APPLICATIONCLASS() {
        return LocalizationMessages.messageFactory.getMessage("resource.config.error.null.applicationclass", new Object[0]);
    }
    
    public static String RESOURCE_CONFIG_ERROR_NULL_APPLICATIONCLASS() {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_CONFIG_ERROR_NULL_APPLICATIONCLASS());
    }
    
    public static Localizable localizableRESOURCE_MULTIPLE_SCOPE_ANNOTATIONS(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("resource.multiple.scope.annotations", new Object[] { arg0 });
    }
    
    public static String RESOURCE_MULTIPLE_SCOPE_ANNOTATIONS(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_MULTIPLE_SCOPE_ANNOTATIONS(arg0));
    }
    
    public static Localizable localizableEXCEPTION_MAPPING_START() {
        return LocalizationMessages.messageFactory.getMessage("exception.mapping.start", new Object[0]);
    }
    
    public static String EXCEPTION_MAPPING_START() {
        return LocalizationMessages.localizer.localize(localizableEXCEPTION_MAPPING_START());
    }
    
    public static Localizable localizableLOGGING_GLOBAL_READER_INTERCEPTORS() {
        return LocalizationMessages.messageFactory.getMessage("logging.global.reader.interceptors", new Object[0]);
    }
    
    public static String LOGGING_GLOBAL_READER_INTERCEPTORS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_GLOBAL_READER_INTERCEPTORS());
    }
    
    public static Localizable localizableMULTIPLE_EVENT_SINK_INJECTION(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("multiple.event.sink.injection", new Object[] { arg0 });
    }
    
    public static String MULTIPLE_EVENT_SINK_INJECTION(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableMULTIPLE_EVENT_SINK_INJECTION(arg0));
    }
    
    public static Localizable localizableRESOURCE_LOOKUP_FAILED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("resource.lookup.failed", new Object[] { arg0 });
    }
    
    public static String RESOURCE_LOOKUP_FAILED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_LOOKUP_FAILED(arg0));
    }
    
    public static Localizable localizableERROR_WADL_BUILDER_GENERATION_RESOURCE_LOCATOR(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.builder.generation.resource.locator", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_WADL_BUILDER_GENERATION_RESOURCE_LOCATOR(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_BUILDER_GENERATION_RESOURCE_LOCATOR(arg0, arg1));
    }
    
    public static Localizable localizableERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE() {
        return LocalizationMessages.messageFactory.getMessage("error.request.set.security.context.in.response.phase", new Object[0]);
    }
    
    public static String ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE() {
        return LocalizationMessages.localizer.localize(localizableERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE());
    }
    
    public static Localizable localizablePARAM_NULL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("param.null", new Object[] { arg0 });
    }
    
    public static String PARAM_NULL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizablePARAM_NULL(arg0));
    }
    
    public static Localizable localizableEXCEPTION_MAPPING_WAE_ENTITY(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("exception.mapping.wae.entity", new Object[] { arg0 });
    }
    
    public static String EXCEPTION_MAPPING_WAE_ENTITY(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableEXCEPTION_MAPPING_WAE_ENTITY(arg0));
    }
    
    public static Localizable localizableILLEGAL_CLIENT_CONFIG_CLASS_PROPERTY_VALUE(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("illegal.client.config.class.property.value", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String ILLEGAL_CLIENT_CONFIG_CLASS_PROPERTY_VALUE(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableILLEGAL_CLIENT_CONFIG_CLASS_PROPERTY_VALUE(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_PROCESSING_RESPONSE_FROM_ALREADY_MAPPED_EXCEPTION() {
        return LocalizationMessages.messageFactory.getMessage("error.processing.response.from.already.mapped.exception", new Object[0]);
    }
    
    public static String ERROR_PROCESSING_RESPONSE_FROM_ALREADY_MAPPED_EXCEPTION() {
        return LocalizationMessages.localizer.localize(localizableERROR_PROCESSING_RESPONSE_FROM_ALREADY_MAPPED_EXCEPTION());
    }
    
    public static Localizable localizableLOGGING_MESSAGE_BODY_READERS() {
        return LocalizationMessages.messageFactory.getMessage("logging.message.body.readers", new Object[0]);
    }
    
    public static String LOGGING_MESSAGE_BODY_READERS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_MESSAGE_BODY_READERS());
    }
    
    public static Localizable localizableERROR_SUSPENDING_ASYNC_REQUEST() {
        return LocalizationMessages.messageFactory.getMessage("error.suspending.async.request", new Object[0]);
    }
    
    public static String ERROR_SUSPENDING_ASYNC_REQUEST() {
        return LocalizationMessages.localizer.localize(localizableERROR_SUSPENDING_ASYNC_REQUEST());
    }
    
    public static Localizable localizableERROR_SUSPENDING_CHUNKED_OUTPUT_RESPONSE() {
        return LocalizationMessages.messageFactory.getMessage("error.suspending.chunked.output.response", new Object[0]);
    }
    
    public static String ERROR_SUSPENDING_CHUNKED_OUTPUT_RESPONSE() {
        return LocalizationMessages.localizer.localize(localizableERROR_SUSPENDING_CHUNKED_OUTPUT_RESPONSE());
    }
    
    public static Localizable localizableAMBIGUOUS_PARAMETER(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("ambiguous.parameter", new Object[] { arg0, arg1 });
    }
    
    public static String AMBIGUOUS_PARAMETER(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableAMBIGUOUS_PARAMETER(arg0, arg1));
    }
    
    public static Localizable localizableERROR_VALIDATION_SUBRESOURCE() {
        return LocalizationMessages.messageFactory.getMessage("error.validation.subresource", new Object[0]);
    }
    
    public static String ERROR_VALIDATION_SUBRESOURCE() {
        return LocalizationMessages.localizer.localize(localizableERROR_VALIDATION_SUBRESOURCE());
    }
    
    public static Localizable localizableAMBIGUOUS_RMS_OUT(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.messageFactory.getMessage("ambiguous.rms.out", new Object[] { arg0, arg1, arg2, arg3 });
    }
    
    public static String AMBIGUOUS_RMS_OUT(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.localizer.localize(localizableAMBIGUOUS_RMS_OUT(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableERROR_SUB_RESOURCE_LOCATOR_MORE_RESOURCES(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.sub.resource.locator.more.resources", new Object[] { arg0 });
    }
    
    public static String ERROR_SUB_RESOURCE_LOCATOR_MORE_RESOURCES(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_SUB_RESOURCE_LOCATOR_MORE_RESOURCES(arg0));
    }
    
    public static Localizable localizableAMBIGUOUS_RMS_IN(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.messageFactory.getMessage("ambiguous.rms.in", new Object[] { arg0, arg1, arg2, arg3 });
    }
    
    public static String AMBIGUOUS_RMS_IN(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.localizer.localize(localizableAMBIGUOUS_RMS_IN(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableERROR_MONITORING_QUEUE_RESPONSE() {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.queue.response", new Object[0]);
    }
    
    public static String ERROR_MONITORING_QUEUE_RESPONSE() {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_QUEUE_RESPONSE());
    }
    
    public static Localizable localizableINVALID_MAPPING_VALUE_EMPTY(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("invalid.mapping.value.empty", new Object[] { arg0, arg1 });
    }
    
    public static String INVALID_MAPPING_VALUE_EMPTY(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableINVALID_MAPPING_VALUE_EMPTY(arg0, arg1));
    }
    
    public static Localizable localizableERROR_MONITORING_SCHEDULER_DESTROY_TIMEOUT() {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.scheduler.destroy.timeout", new Object[0]);
    }
    
    public static String ERROR_MONITORING_SCHEDULER_DESTROY_TIMEOUT() {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_SCHEDULER_DESTROY_TIMEOUT());
    }
    
    public static Localizable localizableNON_PUB_RES_METHOD(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("non.pub.res.method", new Object[] { arg0 });
    }
    
    public static String NON_PUB_RES_METHOD(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNON_PUB_RES_METHOD(arg0));
    }
    
    public static Localizable localizableERROR_MONITORING_STATISTICS_LISTENER(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.statistics.listener", new Object[] { arg0 });
    }
    
    public static String ERROR_MONITORING_STATISTICS_LISTENER(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_STATISTICS_LISTENER(arg0));
    }
    
    public static Localizable localizableAMBIGUOUS_RESOURCE_METHOD(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("ambiguous.resource.method", new Object[] { arg0 });
    }
    
    public static String AMBIGUOUS_RESOURCE_METHOD(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableAMBIGUOUS_RESOURCE_METHOD(arg0));
    }
    
    public static Localizable localizableCLOSEABLE_INJECTED_REQUEST_CONTEXT_NULL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("closeable.injected.request.context.null", new Object[] { arg0 });
    }
    
    public static String CLOSEABLE_INJECTED_REQUEST_CONTEXT_NULL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableCLOSEABLE_INJECTED_REQUEST_CONTEXT_NULL(arg0));
    }
    
    public static Localizable localizableSUSPEND_SCHEDULING_ERROR() {
        return LocalizationMessages.messageFactory.getMessage("suspend.scheduling.error", new Object[0]);
    }
    
    public static String SUSPEND_SCHEDULING_ERROR() {
        return LocalizationMessages.localizer.localize(localizableSUSPEND_SCHEDULING_ERROR());
    }
    
    public static Localizable localizableERROR_WADL_RESOURCE_APPLICATION_WADL() {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.resource.application.wadl", new Object[0]);
    }
    
    public static String ERROR_WADL_RESOURCE_APPLICATION_WADL() {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_RESOURCE_APPLICATION_WADL());
    }
    
    public static Localizable localizableWADL_DOC_EXTENDED_WADL(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("wadl.doc.extended.wadl", new Object[] { arg0, arg1 });
    }
    
    public static String WADL_DOC_EXTENDED_WADL(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableWADL_DOC_EXTENDED_WADL(arg0, arg1));
    }
    
    public static Localizable localizableJAR_SCANNER_UNABLE_TO_READ_ENTRY() {
        return LocalizationMessages.messageFactory.getMessage("jar.scanner.unable.to.read.entry", new Object[0]);
    }
    
    public static String JAR_SCANNER_UNABLE_TO_READ_ENTRY() {
        return LocalizationMessages.localizer.localize(localizableJAR_SCANNER_UNABLE_TO_READ_ENTRY());
    }
    
    public static Localizable localizableINIT_MSG(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("init.msg", new Object[] { arg0 });
    }
    
    public static String INIT_MSG(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableINIT_MSG(arg0));
    }
    
    public static Localizable localizableRESOURCE_CONFIG_UNABLE_TO_PROCESS(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("resource.config.unable.to.process", new Object[] { arg0 });
    }
    
    public static String RESOURCE_CONFIG_UNABLE_TO_PROCESS(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_CONFIG_UNABLE_TO_PROCESS(arg0));
    }
    
    public static Localizable localizableSUBRES_LOC_HAS_ENTITY_PARAM(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("subres.loc.has.entity.param", new Object[] { arg0 });
    }
    
    public static String SUBRES_LOC_HAS_ENTITY_PARAM(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSUBRES_LOC_HAS_ENTITY_PARAM(arg0));
    }
    
    public static Localizable localizableWADL_RESOURCEDOC_AMBIGUOUS_METHOD_ENTRIES(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.messageFactory.getMessage("wadl.resourcedoc.ambiguous.method.entries", new Object[] { arg0, arg1, arg2, arg3 });
    }
    
    public static String WADL_RESOURCEDOC_AMBIGUOUS_METHOD_ENTRIES(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.localizer.localize(localizableWADL_RESOURCEDOC_AMBIGUOUS_METHOD_ENTRIES(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableSUSPEND_HANDLER_EXECUTION_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("suspend.handler.execution.failed", new Object[0]);
    }
    
    public static String SUSPEND_HANDLER_EXECUTION_FAILED() {
        return LocalizationMessages.localizer.localize(localizableSUSPEND_HANDLER_EXECUTION_FAILED());
    }
    
    public static Localizable localizableERROR_UNSUPPORTED_ENCODING(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.unsupported.encoding", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_UNSUPPORTED_ENCODING(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_UNSUPPORTED_ENCODING(arg0, arg1));
    }
    
    public static Localizable localizableEXCEPTION_MAPPER_FAILED_FOR_EXCEPTION() {
        return LocalizationMessages.messageFactory.getMessage("exception.mapper.failed.for.exception", new Object[0]);
    }
    
    public static String EXCEPTION_MAPPER_FAILED_FOR_EXCEPTION() {
        return LocalizationMessages.localizer.localize(localizableEXCEPTION_MAPPER_FAILED_FOR_EXCEPTION());
    }
    
    public static Localizable localizableSINGLETON_INJECTS_PARAMETER(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("singleton.injects.parameter", new Object[] { arg0, arg1 });
    }
    
    public static String SINGLETON_INJECTS_PARAMETER(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableSINGLETON_INJECTS_PARAMETER(arg0, arg1));
    }
    
    public static Localizable localizableERROR_MONITORING_QUEUE_APP() {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.queue.app", new Object[0]);
    }
    
    public static String ERROR_MONITORING_QUEUE_APP() {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_QUEUE_APP());
    }
    
    public static Localizable localizableRESOURCE_REPLACED_CHILD_DOES_NOT_EXIST(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("resource.replaced.child.does.not.exist", new Object[] { arg0 });
    }
    
    public static String RESOURCE_REPLACED_CHILD_DOES_NOT_EXIST(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_REPLACED_CHILD_DOES_NOT_EXIST(arg0));
    }
    
    public static Localizable localizableCONTRACT_CANNOT_BE_BOUND_TO_RESOURCE_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("contract.cannot.be.bound.to.resource.method", new Object[] { arg0, arg1 });
    }
    
    public static String CONTRACT_CANNOT_BE_BOUND_TO_RESOURCE_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableCONTRACT_CANNOT_BE_BOUND_TO_RESOURCE_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableERROR_MONITORING_MBEANS_UNREGISTRATION_DESTROY() {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.mbeans.unregistration.destroy", new Object[0]);
    }
    
    public static String ERROR_MONITORING_MBEANS_UNREGISTRATION_DESTROY() {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_MBEANS_UNREGISTRATION_DESTROY());
    }
    
    public static Localizable localizableUNSUPPORTED_URI_INJECTION_TYPE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("unsupported.uri.injection.type", new Object[] { arg0 });
    }
    
    public static String UNSUPPORTED_URI_INJECTION_TYPE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableUNSUPPORTED_URI_INJECTION_TYPE(arg0));
    }
    
    public static Localizable localizableERROR_MONITORING_STATISTICS_GENERATION() {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.statistics.generation", new Object[0]);
    }
    
    public static String ERROR_MONITORING_STATISTICS_GENERATION() {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_STATISTICS_GENERATION());
    }
    
    public static Localizable localizableINVALID_MAPPING_KEY_EMPTY(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("invalid.mapping.key.empty", new Object[] { arg0, arg1 });
    }
    
    public static String INVALID_MAPPING_KEY_EMPTY(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableINVALID_MAPPING_KEY_EMPTY(arg0, arg1));
    }
    
    public static Localizable localizableMETHOD_UNEXPECTED_ANNOTATION(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("method.unexpected.annotation", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String METHOD_UNEXPECTED_ANNOTATION(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableMETHOD_UNEXPECTED_ANNOTATION(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_RESOURCE_JAVA_METHOD_INVOCATION() {
        return LocalizationMessages.messageFactory.getMessage("error.resource.java.method.invocation", new Object[0]);
    }
    
    public static String ERROR_RESOURCE_JAVA_METHOD_INVOCATION() {
        return LocalizationMessages.localizer.localize(localizableERROR_RESOURCE_JAVA_METHOD_INVOCATION());
    }
    
    public static Localizable localizableERROR_SCANNING_CLASS_NOT_FOUND(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.scanning.class.not.found", new Object[] { arg0 });
    }
    
    public static String ERROR_SCANNING_CLASS_NOT_FOUND(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_SCANNING_CLASS_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableGET_RETURNS_VOID(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("get.returns.void", new Object[] { arg0 });
    }
    
    public static String GET_RETURNS_VOID(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableGET_RETURNS_VOID(arg0));
    }
    
    public static Localizable localizableERROR_PRIMITIVE_TYPE_NULL() {
        return LocalizationMessages.messageFactory.getMessage("error.primitive.type.null", new Object[0]);
    }
    
    public static String ERROR_PRIMITIVE_TYPE_NULL() {
        return LocalizationMessages.localizer.localize(localizableERROR_PRIMITIVE_TYPE_NULL());
    }
    
    public static Localizable localizableWARNING_MONITORING_FEATURE_ENABLED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("warning.monitoring.feature.enabled", new Object[] { arg0 });
    }
    
    public static String WARNING_MONITORING_FEATURE_ENABLED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableWARNING_MONITORING_FEATURE_ENABLED(arg0));
    }
    
    public static Localizable localizableERROR_WADL_BUILDER_GENERATION_RESOURCE_PATH(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.builder.generation.resource.path", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_WADL_BUILDER_GENERATION_RESOURCE_PATH(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_BUILDER_GENERATION_RESOURCE_PATH(arg0, arg1));
    }
    
    public static Localizable localizableERROR_CLOSING_FINDER(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.closing.finder", new Object[] { arg0 });
    }
    
    public static String ERROR_CLOSING_FINDER(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_CLOSING_FINDER(arg0));
    }
    
    public static Localizable localizableLOGGING_NAME_BOUND_REQUEST_FILTERS() {
        return LocalizationMessages.messageFactory.getMessage("logging.name.bound.request.filters", new Object[0]);
    }
    
    public static String LOGGING_NAME_BOUND_REQUEST_FILTERS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_NAME_BOUND_REQUEST_FILTERS());
    }
    
    public static Localizable localizableEXCEPTION_MAPPER_THROWS_EXCEPTION(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("exception.mapper.throws.exception", new Object[] { arg0 });
    }
    
    public static String EXCEPTION_MAPPER_THROWS_EXCEPTION(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableEXCEPTION_MAPPER_THROWS_EXCEPTION(arg0));
    }
    
    public static Localizable localizableCALLBACK_ARRAY_NULL() {
        return LocalizationMessages.messageFactory.getMessage("callback.array.null", new Object[0]);
    }
    
    public static String CALLBACK_ARRAY_NULL() {
        return LocalizationMessages.localizer.localize(localizableCALLBACK_ARRAY_NULL());
    }
    
    public static Localizable localizableERROR_PARAMETER_INVALID_CHAR_VALUE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.parameter.invalid.char.value", new Object[] { arg0 });
    }
    
    public static String ERROR_PARAMETER_INVALID_CHAR_VALUE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_PARAMETER_INVALID_CHAR_VALUE(arg0));
    }
    
    public static Localizable localizableERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE() {
        return LocalizationMessages.messageFactory.getMessage("error.request.set.entity.stream.in.response.phase", new Object[0]);
    }
    
    public static String ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE() {
        return LocalizationMessages.localizer.localize(localizableERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE());
    }
    
    public static Localizable localizableRESOURCE_UPDATED_METHOD_DOES_NOT_EXIST(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("resource.updated.method.does.not.exist", new Object[] { arg0 });
    }
    
    public static String RESOURCE_UPDATED_METHOD_DOES_NOT_EXIST(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_UPDATED_METHOD_DOES_NOT_EXIST(arg0));
    }
    
    public static Localizable localizableRESOURCE_CONTAINS_RES_METHODS_AND_LOCATOR(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("resource.contains.res.methods.and.locator", new Object[] { arg0, arg1 });
    }
    
    public static String RESOURCE_CONTAINS_RES_METHODS_AND_LOCATOR(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_CONTAINS_RES_METHODS_AND_LOCATOR(arg0, arg1));
    }
    
    public static Localizable localizableINJECTED_WEBTARGET_URI_INVALID(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("injected.webtarget.uri.invalid", new Object[] { arg0 });
    }
    
    public static String INJECTED_WEBTARGET_URI_INVALID(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableINJECTED_WEBTARGET_URI_INVALID(arg0));
    }
    
    public static Localizable localizableRESOURCE_AMBIGUOUS(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("resource.ambiguous", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String RESOURCE_AMBIGUOUS(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_AMBIGUOUS(arg0, arg1, arg2));
    }
    
    public static Localizable localizableAMBIGUOUS_SRLS(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("ambiguous.srls", new Object[] { arg0, arg1 });
    }
    
    public static String AMBIGUOUS_SRLS(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableAMBIGUOUS_SRLS(arg0, arg1));
    }
    
    public static Localizable localizableEVENT_SINK_RETURNS_TYPE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("event.sink.returns.type", new Object[] { arg0 });
    }
    
    public static String EVENT_SINK_RETURNS_TYPE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableEVENT_SINK_RETURNS_TYPE(arg0));
    }
    
    public static Localizable localizableLOGGING_NAME_BOUND_RESPONSE_FILTERS() {
        return LocalizationMessages.messageFactory.getMessage("logging.name.bound.response.filters", new Object[0]);
    }
    
    public static String LOGGING_NAME_BOUND_RESPONSE_FILTERS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_NAME_BOUND_RESPONSE_FILTERS());
    }
    
    public static Localizable localizableERROR_COMMITTING_OUTPUT_STREAM() {
        return LocalizationMessages.messageFactory.getMessage("error.committing.output.stream", new Object[0]);
    }
    
    public static String ERROR_COMMITTING_OUTPUT_STREAM() {
        return LocalizationMessages.localizer.localize(localizableERROR_COMMITTING_OUTPUT_STREAM());
    }
    
    public static Localizable localizableLOGGING_NAME_BOUND_READER_INTERCEPTORS() {
        return LocalizationMessages.messageFactory.getMessage("logging.name.bound.reader.interceptors", new Object[0]);
    }
    
    public static String LOGGING_NAME_BOUND_READER_INTERCEPTORS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_NAME_BOUND_READER_INTERCEPTORS());
    }
    
    public static Localizable localizableLOGGING_PRE_MATCH_FILTERS() {
        return LocalizationMessages.messageFactory.getMessage("logging.pre.match.filters", new Object[0]);
    }
    
    public static String LOGGING_PRE_MATCH_FILTERS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_PRE_MATCH_FILTERS());
    }
    
    public static Localizable localizableERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER() {
        return LocalizationMessages.messageFactory.getMessage("error.exception.mapping.thrown.to.container", new Object[0]);
    }
    
    public static String ERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER() {
        return LocalizationMessages.localizer.localize(localizableERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER());
    }
    
    public static Localizable localizableERROR_MARSHALLING_JAXB(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.marshalling.jaxb", new Object[] { arg0 });
    }
    
    public static String ERROR_MARSHALLING_JAXB(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MARSHALLING_JAXB(arg0));
    }
    
    public static Localizable localizableGET_CONSUMES_ENTITY(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("get.consumes.entity", new Object[] { arg0 });
    }
    
    public static String GET_CONSUMES_ENTITY(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableGET_CONSUMES_ENTITY(arg0));
    }
    
    public static Localizable localizableRESOURCE_EMPTY(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("resource.empty", new Object[] { arg0, arg1 });
    }
    
    public static String RESOURCE_EMPTY(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_EMPTY(arg0, arg1));
    }
    
    public static Localizable localizableFORM_PARAM_METHOD_ERROR() {
        return LocalizationMessages.messageFactory.getMessage("form.param.method.error", new Object[0]);
    }
    
    public static String FORM_PARAM_METHOD_ERROR() {
        return LocalizationMessages.localizer.localize(localizableFORM_PARAM_METHOD_ERROR());
    }
    
    public static Localizable localizablePARAMETER_UNRESOLVABLE(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("parameter.unresolvable", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String PARAMETER_UNRESOLVABLE(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizablePARAMETER_UNRESOLVABLE(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_WADL_BUILDER_GENERATION_RESOURCE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.builder.generation.resource", new Object[] { arg0 });
    }
    
    public static String ERROR_WADL_BUILDER_GENERATION_RESOURCE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_BUILDER_GENERATION_RESOURCE(arg0));
    }
    
    public static Localizable localizableGET_CONSUMES_FORM_PARAM(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("get.consumes.form.param", new Object[] { arg0 });
    }
    
    public static String GET_CONSUMES_FORM_PARAM(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableGET_CONSUMES_FORM_PARAM(arg0));
    }
    
    public static Localizable localizableUNABLE_TO_LOAD_CLASS(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("unable.to.load.class", new Object[] { arg0 });
    }
    
    public static String UNABLE_TO_LOAD_CLASS(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableUNABLE_TO_LOAD_CLASS(arg0));
    }
    
    public static Localizable localizableTYPE_OF_METHOD_NOT_RESOLVABLE_TO_CONCRETE_TYPE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("type.of.method.not.resolvable.to.concrete.type", new Object[] { arg0, arg1 });
    }
    
    public static String TYPE_OF_METHOD_NOT_RESOLVABLE_TO_CONCRETE_TYPE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableTYPE_OF_METHOD_NOT_RESOLVABLE_TO_CONCRETE_TYPE(arg0, arg1));
    }
    
    public static Localizable localizableLOGGING_APPLICATION_INITIALIZED() {
        return LocalizationMessages.messageFactory.getMessage("logging.application.initialized", new Object[0]);
    }
    
    public static String LOGGING_APPLICATION_INITIALIZED() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_APPLICATION_INITIALIZED());
    }
    
    public static Localizable localizableNON_PUB_SUB_RES_METHOD(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("non.pub.sub.res.method", new Object[] { arg0 });
    }
    
    public static String NON_PUB_SUB_RES_METHOD(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNON_PUB_SUB_RES_METHOD(arg0));
    }
    
    public static Localizable localizableERROR_WRITING_RESPONSE_ENTITY() {
        return LocalizationMessages.messageFactory.getMessage("error.writing.response.entity", new Object[0]);
    }
    
    public static String ERROR_WRITING_RESPONSE_ENTITY() {
        return LocalizationMessages.localizer.localize(localizableERROR_WRITING_RESPONSE_ENTITY());
    }
    
    public static Localizable localizableERROR_PARAMETER_TYPE_PROCESSING(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.parameter.type.processing", new Object[] { arg0 });
    }
    
    public static String ERROR_PARAMETER_TYPE_PROCESSING(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_PARAMETER_TYPE_PROCESSING(arg0));
    }
    
    public static Localizable localizableERROR_MONITORING_QUEUE_REQUEST() {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.queue.request", new Object[0]);
    }
    
    public static String ERROR_MONITORING_QUEUE_REQUEST() {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_QUEUE_REQUEST());
    }
    
    public static Localizable localizableWARNING_MONITORING_FEATURE_DISABLED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("warning.monitoring.feature.disabled", new Object[] { arg0 });
    }
    
    public static String WARNING_MONITORING_FEATURE_DISABLED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableWARNING_MONITORING_FEATURE_DISABLED(arg0));
    }
    
    public static Localizable localizableINVALID_MAPPING_FORMAT(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("invalid.mapping.format", new Object[] { arg0, arg1 });
    }
    
    public static String INVALID_MAPPING_FORMAT(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableINVALID_MAPPING_FORMAT(arg0, arg1));
    }
    
    public static Localizable localizableNON_PUB_SUB_RES_LOC(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("non.pub.sub.res.loc", new Object[] { arg0 });
    }
    
    public static String NON_PUB_SUB_RES_LOC(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNON_PUB_SUB_RES_LOC(arg0));
    }
    
    public static Localizable localizableLOGGING_GLOBAL_WRITER_INTERCEPTORS() {
        return LocalizationMessages.messageFactory.getMessage("logging.global.writer.interceptors", new Object[0]);
    }
    
    public static String LOGGING_GLOBAL_WRITER_INTERCEPTORS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_GLOBAL_WRITER_INTERCEPTORS());
    }
    
    public static Localizable localizableERROR_MONITORING_QUEUE_FLOODED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.queue.flooded", new Object[] { arg0 });
    }
    
    public static String ERROR_MONITORING_QUEUE_FLOODED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_QUEUE_FLOODED(arg0));
    }
    
    public static Localizable localizableMETHOD_PARAMETER_CANNOT_BE_NULL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("method.parameter.cannot.be.null", new Object[] { arg0 });
    }
    
    public static String METHOD_PARAMETER_CANNOT_BE_NULL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableMETHOD_PARAMETER_CANNOT_BE_NULL(arg0));
    }
    
    public static Localizable localizableLOGGING_MESSAGE_BODY_WRITERS() {
        return LocalizationMessages.messageFactory.getMessage("logging.message.body.writers", new Object[0]);
    }
    
    public static String LOGGING_MESSAGE_BODY_WRITERS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_MESSAGE_BODY_WRITERS());
    }
    
    public static Localizable localizableAMBIGUOUS_FATAL_RMS(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.messageFactory.getMessage("ambiguous.fatal.rms", new Object[] { arg0, arg1, arg2, arg3 });
    }
    
    public static String AMBIGUOUS_FATAL_RMS(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.localizer.localize(localizableAMBIGUOUS_FATAL_RMS(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableAMBIGUOUS_SRLS_PATH_PATTERN(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("ambiguous.srls.pathPattern", new Object[] { arg0 });
    }
    
    public static String AMBIGUOUS_SRLS_PATH_PATTERN(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableAMBIGUOUS_SRLS_PATH_PATTERN(arg0));
    }
    
    public static Localizable localizableSUSPEND_NOT_SUSPENDED() {
        return LocalizationMessages.messageFactory.getMessage("suspend.not.suspended", new Object[0]);
    }
    
    public static String SUSPEND_NOT_SUSPENDED() {
        return LocalizationMessages.localizer.localize(localizableSUSPEND_NOT_SUSPENDED());
    }
    
    public static Localizable localizablePROPERTY_VALUE_TOSTRING_THROWS_EXCEPTION(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("property.value.tostring.throws.exception", new Object[] { arg0, arg1 });
    }
    
    public static String PROPERTY_VALUE_TOSTRING_THROWS_EXCEPTION(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizablePROPERTY_VALUE_TOSTRING_THROWS_EXCEPTION(arg0, arg1));
    }
    
    public static Localizable localizableERROR_WADL_GENERATOR_CONFIG_LOADER(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.generator.config.loader", new Object[] { arg0 });
    }
    
    public static String ERROR_WADL_GENERATOR_CONFIG_LOADER(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_GENERATOR_CONFIG_LOADER(arg0));
    }
    
    public static Localizable localizableERROR_MONITORING_MBEANS_REGISTRATION(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.mbeans.registration", new Object[] { arg0 });
    }
    
    public static String ERROR_MONITORING_MBEANS_REGISTRATION(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_MBEANS_REGISTRATION(arg0));
    }
    
    public static Localizable localizableERROR_WADL_BUILDER_GENERATION_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.builder.generation.method", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_WADL_BUILDER_GENERATION_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_BUILDER_GENERATION_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableERROR_MONITORING_QUEUE_MAPPER() {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.queue.mapper", new Object[0]);
    }
    
    public static String ERROR_MONITORING_QUEUE_MAPPER() {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_QUEUE_MAPPER());
    }
    
    public static Localizable localizableERROR_PARAMETER_MISSING_VALUE_PROVIDER(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.parameter.missing.value.provider", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_PARAMETER_MISSING_VALUE_PROVIDER(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_PARAMETER_MISSING_VALUE_PROVIDER(arg0, arg1));
    }
    
    public static Localizable localizableERROR_EXCEPTION_MAPPING_ORIGINAL_EXCEPTION() {
        return LocalizationMessages.messageFactory.getMessage("error.exception.mapping.original.exception", new Object[0]);
    }
    
    public static String ERROR_EXCEPTION_MAPPING_ORIGINAL_EXCEPTION() {
        return LocalizationMessages.localizer.localize(localizableERROR_EXCEPTION_MAPPING_ORIGINAL_EXCEPTION());
    }
    
    public static Localizable localizableERROR_REQUEST_ABORT_IN_RESPONSE_PHASE() {
        return LocalizationMessages.messageFactory.getMessage("error.request.abort.in.response.phase", new Object[0]);
    }
    
    public static String ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE() {
        return LocalizationMessages.localizer.localize(localizableERROR_REQUEST_ABORT_IN_RESPONSE_PHASE());
    }
    
    public static Localizable localizableERROR_WADL_BUILDER_GENERATION_RESPONSE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.builder.generation.response", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_WADL_BUILDER_GENERATION_RESPONSE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_BUILDER_GENERATION_RESPONSE(arg0, arg1));
    }
    
    public static Localizable localizableERROR_MONITORING_SHUTDOWN_INTERRUPTED() {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.shutdown.interrupted", new Object[0]);
    }
    
    public static String ERROR_MONITORING_SHUTDOWN_INTERRUPTED() {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_SHUTDOWN_INTERRUPTED());
    }
    
    public static Localizable localizableERROR_WADL_GRAMMAR_ALREADY_CONTAINS() {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.grammar.already.contains", new Object[0]);
    }
    
    public static String ERROR_WADL_GRAMMAR_ALREADY_CONTAINS() {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_GRAMMAR_ALREADY_CONTAINS());
    }
    
    public static Localizable localizableRESOURCE_MODEL_VALIDATION_FAILED_AT_INIT() {
        return LocalizationMessages.messageFactory.getMessage("resource.model.validation.failed.at.init", new Object[0]);
    }
    
    public static String RESOURCE_MODEL_VALIDATION_FAILED_AT_INIT() {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_MODEL_VALIDATION_FAILED_AT_INIT());
    }
    
    public static Localizable localizableSUBRES_LOC_CACHE_LOAD_FAILED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("subres.loc.cache.load.failed", new Object[] { arg0 });
    }
    
    public static String SUBRES_LOC_CACHE_LOAD_FAILED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSUBRES_LOC_CACHE_LOAD_FAILED(arg0));
    }
    
    public static Localizable localizableMETHOD_EMPTY_PATH_ANNOTATION(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("method.empty.path.annotation", new Object[] { arg0, arg1 });
    }
    
    public static String METHOD_EMPTY_PATH_ANNOTATION(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableMETHOD_EMPTY_PATH_ANNOTATION(arg0, arg1));
    }
    
    public static Localizable localizableUSER_NOT_AUTHORIZED() {
        return LocalizationMessages.messageFactory.getMessage("user.not.authorized", new Object[0]);
    }
    
    public static String USER_NOT_AUTHORIZED() {
        return LocalizationMessages.localizer.localize(localizableUSER_NOT_AUTHORIZED());
    }
    
    public static Localizable localizableCLOSEABLE_UNABLE_TO_CLOSE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("closeable.unable.to.close", new Object[] { arg0 });
    }
    
    public static String CLOSEABLE_UNABLE_TO_CLOSE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableCLOSEABLE_UNABLE_TO_CLOSE(arg0));
    }
    
    public static Localizable localizableWARNING_TOO_MANY_EXTERNAL_REQ_SCOPES(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("warning.too.many.external.req.scopes", new Object[] { arg0 });
    }
    
    public static String WARNING_TOO_MANY_EXTERNAL_REQ_SCOPES(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableWARNING_TOO_MANY_EXTERNAL_REQ_SCOPES(arg0));
    }
    
    public static Localizable localizableNEW_AR_CREATED_BY_INTROSPECTION_MODELER(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("new.ar.created.by.introspection.modeler", new Object[] { arg0 });
    }
    
    public static String NEW_AR_CREATED_BY_INTROSPECTION_MODELER(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNEW_AR_CREATED_BY_INTROSPECTION_MODELER(arg0));
    }
    
    public static Localizable localizableERROR_WADL_RESOURCE_MARSHAL() {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.resource.marshal", new Object[0]);
    }
    
    public static String ERROR_WADL_RESOURCE_MARSHAL() {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_RESOURCE_MARSHAL());
    }
    
    public static Localizable localizableERROR_WADL_GENERATOR_CONFIG_LOADER_PROPERTY(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.generator.config.loader.property", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_WADL_GENERATOR_CONFIG_LOADER_PROPERTY(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_GENERATOR_CONFIG_LOADER_PROPERTY(arg0, arg1));
    }
    
    public static Localizable localizableERROR_WADL_BUILDER_GENERATION_REQUEST_MEDIA_TYPE(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.builder.generation.request.media.type", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String ERROR_WADL_BUILDER_GENERATION_REQUEST_MEDIA_TYPE(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_BUILDER_GENERATION_REQUEST_MEDIA_TYPE(arg0, arg1, arg2));
    }
    
    public static Localizable localizableRELEASING_REQUEST_PROCESSING_RESOURCES_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("releasing.request.processing.resources.failed", new Object[0]);
    }
    
    public static String RELEASING_REQUEST_PROCESSING_RESOURCES_FAILED() {
        return LocalizationMessages.localizer.localize(localizableRELEASING_REQUEST_PROCESSING_RESOURCES_FAILED());
    }
    
    public static Localizable localizableINVALID_CONFIG_PROPERTY_VALUE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("invalid.config.property.value", new Object[] { arg0, arg1 });
    }
    
    public static String INVALID_CONFIG_PROPERTY_VALUE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableINVALID_CONFIG_PROPERTY_VALUE(arg0, arg1));
    }
    
    public static Localizable localizableEXCEPTION_MAPPING_WAE_NO_ENTITY(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("exception.mapping.wae.no.entity", new Object[] { arg0 });
    }
    
    public static String EXCEPTION_MAPPING_WAE_NO_ENTITY(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableEXCEPTION_MAPPING_WAE_NO_ENTITY(arg0));
    }
    
    public static Localizable localizableDEFAULT_COULD_NOT_PROCESS_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("default.could.not.process.method", new Object[] { arg0, arg1 });
    }
    
    public static String DEFAULT_COULD_NOT_PROCESS_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableDEFAULT_COULD_NOT_PROCESS_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableLOGGING_PROVIDER_BOUND(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("logging.provider.bound", new Object[] { arg0, arg1 });
    }
    
    public static String LOGGING_PROVIDER_BOUND(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableLOGGING_PROVIDER_BOUND(arg0, arg1));
    }
    
    public static Localizable localizableRC_NOT_MODIFIABLE() {
        return LocalizationMessages.messageFactory.getMessage("rc.not.modifiable", new Object[0]);
    }
    
    public static String RC_NOT_MODIFIABLE() {
        return LocalizationMessages.localizer.localize(localizableRC_NOT_MODIFIABLE());
    }
    
    public static Localizable localizableSUB_RES_METHOD_TREATED_AS_RES_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("sub.res.method.treated.as.res.method", new Object[] { arg0, arg1 });
    }
    
    public static String SUB_RES_METHOD_TREATED_AS_RES_METHOD(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableSUB_RES_METHOD_TREATED_AS_RES_METHOD(arg0, arg1));
    }
    
    public static Localizable localizableERROR_WADL_GENERATOR_LOAD() {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.generator.load", new Object[0]);
    }
    
    public static String ERROR_WADL_GENERATOR_LOAD() {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_GENERATOR_LOAD());
    }
    
    public static Localizable localizableSUBRES_LOC_URI_PATH_INVALID(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("subres.loc.uri.path.invalid", new Object[] { arg0, arg1 });
    }
    
    public static String SUBRES_LOC_URI_PATH_INVALID(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableSUBRES_LOC_URI_PATH_INVALID(arg0, arg1));
    }
    
    public static Localizable localizableFORM_PARAM_CONTENT_TYPE_ERROR() {
        return LocalizationMessages.messageFactory.getMessage("form.param.content-type.error", new Object[0]);
    }
    
    public static String FORM_PARAM_CONTENT_TYPE_ERROR() {
        return LocalizationMessages.localizer.localize(localizableFORM_PARAM_CONTENT_TYPE_ERROR());
    }
    
    public static Localizable localizableERROR_WADL_BUILDER_GENERATION_REQUEST(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.builder.generation.request", new Object[] { arg0, arg1 });
    }
    
    public static String ERROR_WADL_BUILDER_GENERATION_REQUEST(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_BUILDER_GENERATION_REQUEST(arg0, arg1));
    }
    
    public static Localizable localizableSECURITY_CONTEXT_WAS_NOT_SET() {
        return LocalizationMessages.messageFactory.getMessage("security.context.was.not.set", new Object[0]);
    }
    
    public static String SECURITY_CONTEXT_WAS_NOT_SET() {
        return LocalizationMessages.localizer.localize(localizableSECURITY_CONTEXT_WAS_NOT_SET());
    }
    
    public static Localizable localizableRESOURCE_IMPLEMENTS_PROVIDER(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("resource.implements.provider", new Object[] { arg0, arg1 });
    }
    
    public static String RESOURCE_IMPLEMENTS_PROVIDER(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_IMPLEMENTS_PROVIDER(arg0, arg1));
    }
    
    public static Localizable localizableLOGGING_GLOBAL_RESPONSE_FILTERS() {
        return LocalizationMessages.messageFactory.getMessage("logging.global.response.filters", new Object[0]);
    }
    
    public static String LOGGING_GLOBAL_RESPONSE_FILTERS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_GLOBAL_RESPONSE_FILTERS());
    }
    
    public static Localizable localizableERROR_WADL_BUILDER_GENERATION_PARAM(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.wadl.builder.generation.param", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String ERROR_WADL_BUILDER_GENERATION_PARAM(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_WADL_BUILDER_GENERATION_PARAM(arg0, arg1, arg2));
    }
    
    public static Localizable localizableWARNING_MSG(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("warning.msg", new Object[] { arg0 });
    }
    
    public static String WARNING_MSG(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableWARNING_MSG(arg0));
    }
    
    public static Localizable localizableBROADCASTER_LISTENER_EXCEPTION(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("broadcaster.listener.exception", new Object[] { arg0 });
    }
    
    public static String BROADCASTER_LISTENER_EXCEPTION(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableBROADCASTER_LISTENER_EXCEPTION(arg0));
    }
    
    public static Localizable localizableRESOURCE_MERGE_CONFLICT_LOCATORS(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("resource.merge.conflict.locators", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String RESOURCE_MERGE_CONFLICT_LOCATORS(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableRESOURCE_MERGE_CONFLICT_LOCATORS(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_RESOURCES_CANNOT_MERGE() {
        return LocalizationMessages.messageFactory.getMessage("error.resources.cannot.merge", new Object[0]);
    }
    
    public static String ERROR_RESOURCES_CANNOT_MERGE() {
        return LocalizationMessages.localizer.localize(localizableERROR_RESOURCES_CANNOT_MERGE());
    }
    
    public static Localizable localizableERROR_MONITORING_STATISTICS_LISTENER_DESTROY(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.monitoring.statistics.listener.destroy", new Object[] { arg0 });
    }
    
    public static String ERROR_MONITORING_STATISTICS_LISTENER_DESTROY(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MONITORING_STATISTICS_LISTENER_DESTROY(arg0));
    }
    
    public static Localizable localizableWADL_JAXB_CONTEXT_FALLBACK() {
        return LocalizationMessages.messageFactory.getMessage("wadl.jaxb.context.fallback", new Object[0]);
    }
    
    public static String WADL_JAXB_CONTEXT_FALLBACK() {
        return LocalizationMessages.localizer.localize(localizableWADL_JAXB_CONTEXT_FALLBACK());
    }
    
    public static Localizable localizableMETHOD_INVOCABLE_FROM_PREMATCH_FILTERS_ONLY() {
        return LocalizationMessages.messageFactory.getMessage("method.invocable.from.prematch.filters.only", new Object[0]);
    }
    
    public static String METHOD_INVOCABLE_FROM_PREMATCH_FILTERS_ONLY() {
        return LocalizationMessages.localizer.localize(localizableMETHOD_INVOCABLE_FROM_PREMATCH_FILTERS_ONLY());
    }
    
    public static Localizable localizableLOGGING_NAME_BOUND_WRITER_INTERCEPTORS() {
        return LocalizationMessages.messageFactory.getMessage("logging.name.bound.writer.interceptors", new Object[0]);
    }
    
    public static String LOGGING_NAME_BOUND_WRITER_INTERCEPTORS() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_NAME_BOUND_WRITER_INTERCEPTORS());
    }
    
    public static Localizable localizableCALLBACK_ARRAY_ELEMENT_NULL() {
        return LocalizationMessages.messageFactory.getMessage("callback.array.element.null", new Object[0]);
    }
    
    public static String CALLBACK_ARRAY_ELEMENT_NULL() {
        return LocalizationMessages.localizer.localize(localizableCALLBACK_ARRAY_ELEMENT_NULL());
    }
    
    public static Localizable localizableERROR_CLOSING_COMMIT_OUTPUT_STREAM() {
        return LocalizationMessages.messageFactory.getMessage("error.closing.commit.output.stream", new Object[0]);
    }
    
    public static String ERROR_CLOSING_COMMIT_OUTPUT_STREAM() {
        return LocalizationMessages.localizer.localize(localizableERROR_CLOSING_COMMIT_OUTPUT_STREAM());
    }
    
    public static Localizable localizableERROR_MSG(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.msg", new Object[] { arg0 });
    }
    
    public static String ERROR_MSG(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MSG(arg0));
    }
    
    public static Localizable localizableWADL_DOC_SIMPLE_WADL(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("wadl.doc.simple.wadl", new Object[] { arg0, arg1 });
    }
    
    public static String WADL_DOC_SIMPLE_WADL(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableWADL_DOC_SIMPLE_WADL(arg0, arg1));
    }
    
    public static Localizable localizableLOGGING_DYNAMIC_FEATURES() {
        return LocalizationMessages.messageFactory.getMessage("logging.dynamic.features", new Object[0]);
    }
    
    public static String LOGGING_DYNAMIC_FEATURES() {
        return LocalizationMessages.localizer.localize(localizableLOGGING_DYNAMIC_FEATURES());
    }
    
    public static Localizable localizableMETHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("method.parameter.cannot.be.null.or.empty", new Object[] { arg0 });
    }
    
    public static String METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableMETHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY(arg0));
    }
    
    public static Localizable localizableAMBIGUOUS_NON_ANNOTATED_PARAMETER(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("ambiguous.non.annotated.parameter", new Object[] { arg0, arg1 });
    }
    
    public static String AMBIGUOUS_NON_ANNOTATED_PARAMETER(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableAMBIGUOUS_NON_ANNOTATED_PARAMETER(arg0, arg1));
    }
    
    public static Localizable localizableMULTIPLE_HTTP_METHOD_DESIGNATORS(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("multiple.http.method.designators", new Object[] { arg0, arg1 });
    }
    
    public static String MULTIPLE_HTTP_METHOD_DESIGNATORS(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableMULTIPLE_HTTP_METHOD_DESIGNATORS(arg0, arg1));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.server.internal.localization");
        localizer = new Localizer();
    }
}
