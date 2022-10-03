package org.glassfish.jersey.internal;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableERRORS_AND_WARNINGS_DETECTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("errors.and.warnings.detected", arg0);
    }
    
    public static String ERRORS_AND_WARNINGS_DETECTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERRORS_AND_WARNINGS_DETECTED(arg0));
    }
    
    public static Localizable localizableCOMMITTING_STREAM_BUFFERING_ILLEGAL_STATE() {
        return LocalizationMessages.messageFactory.getMessage("committing.stream.buffering.illegal.state", new Object[0]);
    }
    
    public static String COMMITTING_STREAM_BUFFERING_ILLEGAL_STATE() {
        return LocalizationMessages.localizer.localize(localizableCOMMITTING_STREAM_BUFFERING_ILLEGAL_STATE());
    }
    
    public static Localizable localizableLOCALE_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("locale.is.null", new Object[0]);
    }
    
    public static String LOCALE_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableLOCALE_IS_NULL());
    }
    
    public static Localizable localizableSSL_KMF_PROVIDER_NOT_REGISTERED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.kmf.provider.not.registered", new Object[0]);
    }
    
    public static String SSL_KMF_PROVIDER_NOT_REGISTERED() {
        return LocalizationMessages.localizer.localize(localizableSSL_KMF_PROVIDER_NOT_REGISTERED());
    }
    
    public static Localizable localizableURI_COMPONENT_ENCODED_OCTET_INVALID_DIGIT(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("uri.component.encoded.octet.invalid.digit", arg0, arg1);
    }
    
    public static String URI_COMPONENT_ENCODED_OCTET_INVALID_DIGIT(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableURI_COMPONENT_ENCODED_OCTET_INVALID_DIGIT(arg0, arg1));
    }
    
    public static Localizable localizableURI_PARSER_COMPONENT_DELIMITER(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("uri.parser.component.delimiter", arg0, arg1);
    }
    
    public static String URI_PARSER_COMPONENT_DELIMITER(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableURI_PARSER_COMPONENT_DELIMITER(arg0, arg1));
    }
    
    public static Localizable localizableSSL_KMF_ALGORITHM_NOT_SUPPORTED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.kmf.algorithm.not.supported", new Object[0]);
    }
    
    public static String SSL_KMF_ALGORITHM_NOT_SUPPORTED() {
        return LocalizationMessages.localizer.localize(localizableSSL_KMF_ALGORITHM_NOT_SUPPORTED());
    }
    
    public static Localizable localizableERROR_MBR_ISREADABLE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.mbr.isreadable", arg0);
    }
    
    public static String ERROR_MBR_ISREADABLE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MBR_ISREADABLE(arg0));
    }
    
    public static Localizable localizableSSL_KMF_INIT_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.kmf.init.failed", new Object[0]);
    }
    
    public static String SSL_KMF_INIT_FAILED() {
        return LocalizationMessages.localizer.localize(localizableSSL_KMF_INIT_FAILED());
    }
    
    public static Localizable localizableOVERRIDING_METHOD_CANNOT_BE_FOUND(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("overriding.method.cannot.be.found", arg0, arg1);
    }
    
    public static String OVERRIDING_METHOD_CANNOT_BE_FOUND(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableOVERRIDING_METHOD_CANNOT_BE_FOUND(arg0, arg1));
    }
    
    public static Localizable localizableERROR_INTERCEPTOR_READER_PROCEED() {
        return LocalizationMessages.messageFactory.getMessage("error.interceptor.reader.proceed", new Object[0]);
    }
    
    public static String ERROR_INTERCEPTOR_READER_PROCEED() {
        return LocalizationMessages.localizer.localize(localizableERROR_INTERCEPTOR_READER_PROCEED());
    }
    
    public static Localizable localizableMEDIA_TYPE_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("media.type.is.null", new Object[0]);
    }
    
    public static String MEDIA_TYPE_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableMEDIA_TYPE_IS_NULL());
    }
    
    public static Localizable localizableURI_COMPONENT_ENCODED_OCTET_MALFORMED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("uri.component.encoded.octet.malformed", arg0);
    }
    
    public static String URI_COMPONENT_ENCODED_OCTET_MALFORMED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableURI_COMPONENT_ENCODED_OCTET_MALFORMED(arg0));
    }
    
    public static Localizable localizableSSL_KS_INTEGRITY_ALGORITHM_NOT_FOUND() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ks.integrity.algorithm.not.found", new Object[0]);
    }
    
    public static String SSL_KS_INTEGRITY_ALGORITHM_NOT_FOUND() {
        return LocalizationMessages.localizer.localize(localizableSSL_KS_INTEGRITY_ALGORITHM_NOT_FOUND());
    }
    
    public static Localizable localizableMESSAGE_CONTENT_BUFFER_RESET_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("message.content.buffer.reset.failed", new Object[0]);
    }
    
    public static String MESSAGE_CONTENT_BUFFER_RESET_FAILED() {
        return LocalizationMessages.localizer.localize(localizableMESSAGE_CONTENT_BUFFER_RESET_FAILED());
    }
    
    public static Localizable localizableTEMPLATE_PARAM_NULL() {
        return LocalizationMessages.messageFactory.getMessage("template.param.null", new Object[0]);
    }
    
    public static String TEMPLATE_PARAM_NULL() {
        return LocalizationMessages.localizer.localize(localizableTEMPLATE_PARAM_NULL());
    }
    
    public static Localizable localizableSSL_TMF_INIT_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.tmf.init.failed", new Object[0]);
    }
    
    public static String SSL_TMF_INIT_FAILED() {
        return LocalizationMessages.localizer.localize(localizableSSL_TMF_INIT_FAILED());
    }
    
    public static Localizable localizableURI_BUILDER_CLASS_PATH_ANNOTATION_MISSING(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("uri.builder.class.path.annotation.missing", arg0);
    }
    
    public static String URI_BUILDER_CLASS_PATH_ANNOTATION_MISSING(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableURI_BUILDER_CLASS_PATH_ANNOTATION_MISSING(arg0));
    }
    
    public static Localizable localizableUNHANDLED_EXCEPTION_DETECTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("unhandled.exception.detected", arg0);
    }
    
    public static String UNHANDLED_EXCEPTION_DETECTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableUNHANDLED_EXCEPTION_DETECTED(arg0));
    }
    
    public static Localizable localizableNOT_SUPPORTED_ON_OUTBOUND_MESSAGE() {
        return LocalizationMessages.messageFactory.getMessage("not.supported.on.outbound.message", new Object[0]);
    }
    
    public static String NOT_SUPPORTED_ON_OUTBOUND_MESSAGE() {
        return LocalizationMessages.localizer.localize(localizableNOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
    }
    
    public static Localizable localizableUNABLE_TO_PARSE_HEADER_VALUE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("unable.to.parse.header.value", arg0, arg1);
    }
    
    public static String UNABLE_TO_PARSE_HEADER_VALUE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableUNABLE_TO_PARSE_HEADER_VALUE(arg0, arg1));
    }
    
    public static Localizable localizableERROR_PROVIDER_CONSTRAINED_TO_WRONG_RUNTIME(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.provider.constrainedTo.wrong.runtime", arg0, arg1, arg2);
    }
    
    public static String ERROR_PROVIDER_CONSTRAINED_TO_WRONG_RUNTIME(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_PROVIDER_CONSTRAINED_TO_WRONG_RUNTIME(arg0, arg1, arg2));
    }
    
    public static Localizable localizableSSL_KMF_NO_PASSWORD_SET(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("ssl.kmf.no.password.set", arg0);
    }
    
    public static String SSL_KMF_NO_PASSWORD_SET(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSSL_KMF_NO_PASSWORD_SET(arg0));
    }
    
    public static Localizable localizablePARAM_NULL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("param.null", arg0);
    }
    
    public static String PARAM_NULL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizablePARAM_NULL(arg0));
    }
    
    public static Localizable localizableHTTP_HEADER_UNBALANCED_QUOTED() {
        return LocalizationMessages.messageFactory.getMessage("http.header.unbalanced.quoted", new Object[0]);
    }
    
    public static String HTTP_HEADER_UNBALANCED_QUOTED() {
        return LocalizationMessages.localizer.localize(localizableHTTP_HEADER_UNBALANCED_QUOTED());
    }
    
    public static Localizable localizableLINK_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("link.is.null", new Object[0]);
    }
    
    public static String LINK_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableLINK_IS_NULL());
    }
    
    public static Localizable localizableERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_PART_OF_NAME(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.template.parser.illegal.char.partOf.name", arg0, arg1, arg2);
    }
    
    public static String ERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_PART_OF_NAME(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_PART_OF_NAME(arg0, arg1, arg2));
    }
    
    public static Localizable localizablePROPERTIES_HELPER_DEPRECATED_PROPERTY_NAME(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("properties.helper.deprecated.property.name", arg0, arg1);
    }
    
    public static String PROPERTIES_HELPER_DEPRECATED_PROPERTY_NAME(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizablePROPERTIES_HELPER_DEPRECATED_PROPERTY_NAME(arg0, arg1));
    }
    
    public static Localizable localizableCOMPONENT_CANNOT_BE_NULL() {
        return LocalizationMessages.messageFactory.getMessage("component.cannot.be.null", new Object[0]);
    }
    
    public static String COMPONENT_CANNOT_BE_NULL() {
        return LocalizationMessages.localizer.localize(localizableCOMPONENT_CANNOT_BE_NULL());
    }
    
    public static Localizable localizableURI_BUILDER_ANNOTATEDELEMENT_PATH_ANNOTATION_MISSING(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("uri.builder.annotatedelement.path.annotation.missing", arg0);
    }
    
    public static String URI_BUILDER_ANNOTATEDELEMENT_PATH_ANNOTATION_MISSING(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableURI_BUILDER_ANNOTATEDELEMENT_PATH_ANNOTATION_MISSING(arg0));
    }
    
    public static Localizable localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_CONTEXT(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.service.locator.provider.instance.feature.context", arg0);
    }
    
    public static String ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_CONTEXT(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_CONTEXT(arg0));
    }
    
    public static Localizable localizableERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_START_NAME(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.template.parser.illegal.char.start.name", arg0, arg1, arg2);
    }
    
    public static String ERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_START_NAME(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_START_NAME(arg0, arg1, arg2));
    }
    
    public static Localizable localizableCONFIGURATION_NOT_MODIFIABLE() {
        return LocalizationMessages.messageFactory.getMessage("configuration.not.modifiable", new Object[0]);
    }
    
    public static String CONFIGURATION_NOT_MODIFIABLE() {
        return LocalizationMessages.localizer.localize(localizableCONFIGURATION_NOT_MODIFIABLE());
    }
    
    public static Localizable localizableSSL_TS_CERT_LOAD_ERROR() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ts.cert.load.error", new Object[0]);
    }
    
    public static String SSL_TS_CERT_LOAD_ERROR() {
        return LocalizationMessages.localizer.localize(localizableSSL_TS_CERT_LOAD_ERROR());
    }
    
    public static Localizable localizableERROR_FINDING_EXCEPTION_MAPPER_TYPE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.finding.exception.mapper.type", arg0);
    }
    
    public static String ERROR_FINDING_EXCEPTION_MAPPER_TYPE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_FINDING_EXCEPTION_MAPPER_TYPE(arg0));
    }
    
    public static Localizable localizableERROR_NEWCOOKIE_EXPIRES(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.newcookie.expires", arg0);
    }
    
    public static String ERROR_NEWCOOKIE_EXPIRES(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_NEWCOOKIE_EXPIRES(arg0));
    }
    
    public static Localizable localizableILLEGAL_INITIAL_CAPACITY(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("illegal.initial.capacity", arg0);
    }
    
    public static String ILLEGAL_INITIAL_CAPACITY(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableILLEGAL_INITIAL_CAPACITY(arg0));
    }
    
    public static Localizable localizableSSL_KS_CERT_LOAD_ERROR() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ks.cert.load.error", new Object[0]);
    }
    
    public static String SSL_KS_CERT_LOAD_ERROR() {
        return LocalizationMessages.localizer.localize(localizableSSL_KS_CERT_LOAD_ERROR());
    }
    
    public static Localizable localizableERROR_READING_ENTITY_FROM_INPUT_STREAM() {
        return LocalizationMessages.messageFactory.getMessage("error.reading.entity.from.input.stream", new Object[0]);
    }
    
    public static String ERROR_READING_ENTITY_FROM_INPUT_STREAM() {
        return LocalizationMessages.localizer.localize(localizableERROR_READING_ENTITY_FROM_INPUT_STREAM());
    }
    
    public static Localizable localizableERROR_PROVIDER_CONSTRAINED_TO_IGNORED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.provider.constrainedTo.ignored", arg0);
    }
    
    public static String ERROR_PROVIDER_CONSTRAINED_TO_IGNORED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_PROVIDER_CONSTRAINED_TO_IGNORED(arg0));
    }
    
    public static Localizable localizableHTTP_HEADER_WHITESPACE_NOT_ALLOWED() {
        return LocalizationMessages.messageFactory.getMessage("http.header.whitespace.not.allowed", new Object[0]);
    }
    
    public static String HTTP_HEADER_WHITESPACE_NOT_ALLOWED() {
        return LocalizationMessages.localizer.localize(localizableHTTP_HEADER_WHITESPACE_NOT_ALLOWED());
    }
    
    public static Localizable localizableILLEGAL_CONFIG_SYNTAX() {
        return LocalizationMessages.messageFactory.getMessage("illegal.config.syntax", new Object[0]);
    }
    
    public static String ILLEGAL_CONFIG_SYNTAX() {
        return LocalizationMessages.localizer.localize(localizableILLEGAL_CONFIG_SYNTAX());
    }
    
    public static Localizable localizableSSL_TS_FILE_NOT_FOUND(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("ssl.ts.file.not.found", arg0);
    }
    
    public static String SSL_TS_FILE_NOT_FOUND(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSSL_TS_FILE_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableERROR_CAUGHT_WHILE_LOADING_SPI_PROVIDERS() {
        return LocalizationMessages.messageFactory.getMessage("error.caught.while.loading.spi.providers", new Object[0]);
    }
    
    public static String ERROR_CAUGHT_WHILE_LOADING_SPI_PROVIDERS() {
        return LocalizationMessages.localizer.localize(localizableERROR_CAUGHT_WHILE_LOADING_SPI_PROVIDERS());
    }
    
    public static Localizable localizableMULTIPLE_MATCHING_CONSTRUCTORS_FOUND(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.messageFactory.getMessage("multiple.matching.constructors.found", arg0, arg1, arg2, arg3);
    }
    
    public static String MULTIPLE_MATCHING_CONSTRUCTORS_FOUND(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.localizer.localize(localizableMULTIPLE_MATCHING_CONSTRUCTORS_FOUND(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableMETHOD_NOT_GETTER_NOR_SETTER() {
        return LocalizationMessages.messageFactory.getMessage("method.not.getter.nor.setter", new Object[0]);
    }
    
    public static String METHOD_NOT_GETTER_NOR_SETTER() {
        return LocalizationMessages.localizer.localize(localizableMETHOD_NOT_GETTER_NOR_SETTER());
    }
    
    public static Localizable localizableERROR_PARSING_ENTITY_TAG(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.parsing.entity.tag", arg0);
    }
    
    public static String ERROR_PARSING_ENTITY_TAG(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_PARSING_ENTITY_TAG(arg0));
    }
    
    public static Localizable localizableSSL_CTX_ALGORITHM_NOT_SUPPORTED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ctx.algorithm.not.supported", new Object[0]);
    }
    
    public static String SSL_CTX_ALGORITHM_NOT_SUPPORTED() {
        return LocalizationMessages.localizer.localize(localizableSSL_CTX_ALGORITHM_NOT_SUPPORTED());
    }
    
    public static Localizable localizableERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_AFTER_NAME(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.template.parser.illegal.char.after.name", arg0, arg1, arg2);
    }
    
    public static String ERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_AFTER_NAME(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_AFTER_NAME(arg0, arg1, arg2));
    }
    
    public static Localizable localizableINJECTION_MANAGER_FACTORY_NOT_FOUND() {
        return LocalizationMessages.messageFactory.getMessage("injection.manager.factory.not.found", new Object[0]);
    }
    
    public static String INJECTION_MANAGER_FACTORY_NOT_FOUND() {
        return LocalizationMessages.localizer.localize(localizableINJECTION_MANAGER_FACTORY_NOT_FOUND());
    }
    
    public static Localizable localizableOUTPUT_STREAM_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("output.stream.closed", new Object[0]);
    }
    
    public static String OUTPUT_STREAM_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableOUTPUT_STREAM_CLOSED());
    }
    
    public static Localizable localizableENTITY_TAG_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("entity.tag.is.null", new Object[0]);
    }
    
    public static String ENTITY_TAG_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableENTITY_TAG_IS_NULL());
    }
    
    public static Localizable localizableINPUT_STREAM_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("input.stream.closed", new Object[0]);
    }
    
    public static String INPUT_STREAM_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableINPUT_STREAM_CLOSED());
    }
    
    public static Localizable localizableCOOKIE_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("cookie.is.null", new Object[0]);
    }
    
    public static String COOKIE_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableCOOKIE_IS_NULL());
    }
    
    public static Localizable localizableNEW_COOKIE_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("new.cookie.is.null", new Object[0]);
    }
    
    public static String NEW_COOKIE_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableNEW_COOKIE_IS_NULL());
    }
    
    public static Localizable localizableINJECTION_ERROR_LOCAL_CLASS_NOT_SUPPORTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("injection.error.local.class.not.supported", arg0);
    }
    
    public static String INJECTION_ERROR_LOCAL_CLASS_NOT_SUPPORTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableINJECTION_ERROR_LOCAL_CLASS_NOT_SUPPORTED(arg0));
    }
    
    public static Localizable localizableSSL_TS_PROVIDERS_NOT_REGISTERED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ts.providers.not.registered", new Object[0]);
    }
    
    public static String SSL_TS_PROVIDERS_NOT_REGISTERED() {
        return LocalizationMessages.localizer.localize(localizableSSL_TS_PROVIDERS_NOT_REGISTERED());
    }
    
    public static Localizable localizableINJECTION_ERROR_NONSTATIC_MEMBER_CLASS_NOT_SUPPORTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("injection.error.nonstatic.member.class.not.supported", arg0);
    }
    
    public static String INJECTION_ERROR_NONSTATIC_MEMBER_CLASS_NOT_SUPPORTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableINJECTION_ERROR_NONSTATIC_MEMBER_CLASS_NOT_SUPPORTED(arg0));
    }
    
    public static Localizable localizableUNKNOWN_DESCRIPTOR_TYPE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("unknown.descriptor.type", arg0);
    }
    
    public static String UNKNOWN_DESCRIPTOR_TYPE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableUNKNOWN_DESCRIPTOR_TYPE(arg0));
    }
    
    public static Localizable localizableURI_BUILDER_SCHEME_PART_NULL() {
        return LocalizationMessages.messageFactory.getMessage("uri.builder.scheme.part.null", new Object[0]);
    }
    
    public static String URI_BUILDER_SCHEME_PART_NULL() {
        return LocalizationMessages.localizer.localize(localizableURI_BUILDER_SCHEME_PART_NULL());
    }
    
    public static Localizable localizableMATRIX_PARAM_NULL() {
        return LocalizationMessages.messageFactory.getMessage("matrix.param.null", new Object[0]);
    }
    
    public static String MATRIX_PARAM_NULL() {
        return LocalizationMessages.localizer.localize(localizableMATRIX_PARAM_NULL());
    }
    
    public static Localizable localizableWARNINGS_DETECTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("warnings.detected", arg0);
    }
    
    public static String WARNINGS_DETECTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableWARNINGS_DETECTED(arg0));
    }
    
    public static Localizable localizableHINT_MSG(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("hint.msg", arg0);
    }
    
    public static String HINT_MSG(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHINT_MSG(arg0));
    }
    
    public static Localizable localizableSSL_TS_LOAD_ERROR(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("ssl.ts.load.error", arg0);
    }
    
    public static String SSL_TS_LOAD_ERROR(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSSL_TS_LOAD_ERROR(arg0));
    }
    
    public static Localizable localizableERROR_PROVIDER_REGISTERED_WRONG_RUNTIME(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.provider.registered.wrong.runtime", arg0, arg1);
    }
    
    public static String ERROR_PROVIDER_REGISTERED_WRONG_RUNTIME(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_PROVIDER_REGISTERED_WRONG_RUNTIME(arg0, arg1));
    }
    
    public static Localizable localizableSSL_KMF_NO_PASSWORD_FOR_PROVIDER_BASED_KS() {
        return LocalizationMessages.messageFactory.getMessage("ssl.kmf.no.password.for.provider.based.ks", new Object[0]);
    }
    
    public static String SSL_KMF_NO_PASSWORD_FOR_PROVIDER_BASED_KS() {
        return LocalizationMessages.localizer.localize(localizableSSL_KMF_NO_PASSWORD_FOR_PROVIDER_BASED_KS());
    }
    
    public static Localizable localizableURI_PARSER_SCHEME_EXPECTED(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("uri.parser.scheme.expected", arg0, arg1);
    }
    
    public static String URI_PARSER_SCHEME_EXPECTED(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableURI_PARSER_SCHEME_EXPECTED(arg0, arg1));
    }
    
    public static Localizable localizableTHREAD_POOL_EXECUTOR_PROVIDER_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("thread.pool.executor.provider.closed", new Object[0]);
    }
    
    public static String THREAD_POOL_EXECUTOR_PROVIDER_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableTHREAD_POOL_EXECUTOR_PROVIDER_CLOSED());
    }
    
    public static Localizable localizableMBW_TRYING_TO_CLOSE_STREAM(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("mbw.trying.to.close.stream", arg0);
    }
    
    public static String MBW_TRYING_TO_CLOSE_STREAM(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableMBW_TRYING_TO_CLOSE_STREAM(arg0));
    }
    
    public static Localizable localizableCOMPONENT_CONTRACTS_EMPTY_OR_NULL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("component.contracts.empty.or.null", arg0);
    }
    
    public static String COMPONENT_CONTRACTS_EMPTY_OR_NULL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableCOMPONENT_CONTRACTS_EMPTY_OR_NULL(arg0));
    }
    
    public static Localizable localizablePROVIDER_NOT_FOUND(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("provider.not.found", arg0, arg1);
    }
    
    public static String PROVIDER_NOT_FOUND(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizablePROVIDER_NOT_FOUND(arg0, arg1));
    }
    
    public static Localizable localizableTOO_MANY_HEADER_VALUES(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("too.many.header.values", arg0, arg1);
    }
    
    public static String TOO_MANY_HEADER_VALUES(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableTOO_MANY_HEADER_VALUES(arg0, arg1));
    }
    
    public static Localizable localizableCACHE_CONTROL_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("cache.control.is.null", new Object[0]);
    }
    
    public static String CACHE_CONTROL_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableCACHE_CONTROL_IS_NULL());
    }
    
    public static Localizable localizableHTTP_HEADER_END_OF_HEADER() {
        return LocalizationMessages.messageFactory.getMessage("http.header.end.of.header", new Object[0]);
    }
    
    public static String HTTP_HEADER_END_OF_HEADER() {
        return LocalizationMessages.localizer.localize(localizableHTTP_HEADER_END_OF_HEADER());
    }
    
    public static Localizable localizableUSING_SCHEDULER_PROVIDER(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("using.scheduler.provider", arg0, arg1);
    }
    
    public static String USING_SCHEDULER_PROVIDER(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableUSING_SCHEDULER_PROVIDER(arg0, arg1));
    }
    
    public static Localizable localizableHTTP_HEADER_COMMENTS_NOT_ALLOWED() {
        return LocalizationMessages.messageFactory.getMessage("http.header.comments.not.allowed", new Object[0]);
    }
    
    public static String HTTP_HEADER_COMMENTS_NOT_ALLOWED() {
        return LocalizationMessages.localizer.localize(localizableHTTP_HEADER_COMMENTS_NOT_ALLOWED());
    }
    
    public static Localizable localizableCOMPONENT_CLASS_CANNOT_BE_NULL() {
        return LocalizationMessages.messageFactory.getMessage("component.class.cannot.be.null", new Object[0]);
    }
    
    public static String COMPONENT_CLASS_CANNOT_BE_NULL() {
        return LocalizationMessages.localizer.localize(localizableCOMPONENT_CLASS_CANNOT_BE_NULL());
    }
    
    public static Localizable localizableURI_BUILDER_SCHEMA_PART_OPAQUE() {
        return LocalizationMessages.messageFactory.getMessage("uri.builder.schema.part.opaque", new Object[0]);
    }
    
    public static String URI_BUILDER_SCHEMA_PART_OPAQUE() {
        return LocalizationMessages.localizer.localize(localizableURI_BUILDER_SCHEMA_PART_OPAQUE());
    }
    
    public static Localizable localizableNO_ERROR_PROCESSING_IN_SCOPE() {
        return LocalizationMessages.messageFactory.getMessage("no.error.processing.in.scope", new Object[0]);
    }
    
    public static String NO_ERROR_PROCESSING_IN_SCOPE() {
        return LocalizationMessages.localizer.localize(localizableNO_ERROR_PROCESSING_IN_SCOPE());
    }
    
    public static Localizable localizableCONTRACT_NOT_SUPPORTED(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("contract.not.supported", arg0, arg1);
    }
    
    public static String CONTRACT_NOT_SUPPORTED(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableCONTRACT_NOT_SUPPORTED(arg0, arg1));
    }
    
    public static Localizable localizableINVALID_SPI_CLASSES(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("invalid.spi.classes", arg0, arg1);
    }
    
    public static String INVALID_SPI_CLASSES(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableINVALID_SPI_CLASSES(arg0, arg1));
    }
    
    public static Localizable localizablePROVIDER_COULD_NOT_BE_CREATED(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("provider.could.not.be.created", arg0, arg1, arg2);
    }
    
    public static String PROVIDER_COULD_NOT_BE_CREATED(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizablePROVIDER_COULD_NOT_BE_CREATED(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_NOTFOUND_MESSAGEBODYREADER(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.notfound.messagebodyreader", arg0, arg1, arg2);
    }
    
    public static String ERROR_NOTFOUND_MESSAGEBODYREADER(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_NOTFOUND_MESSAGEBODYREADER(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_READER_INTERCEPTOR_CONTEXT(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.service.locator.provider.instance.feature.reader.interceptor.context", arg0);
    }
    
    public static String ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_READER_INTERCEPTOR_CONTEXT(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_READER_INTERCEPTOR_CONTEXT(arg0));
    }
    
    public static Localizable localizableUSING_EXECUTOR_PROVIDER(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("using.executor.provider", arg0, arg1);
    }
    
    public static String USING_EXECUTOR_PROVIDER(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableUSING_EXECUTOR_PROVIDER(arg0, arg1));
    }
    
    public static Localizable localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_WRITER_INTERCEPTOR_CONTEXT(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.service.locator.provider.instance.feature.writer.interceptor.context", arg0);
    }
    
    public static String ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_WRITER_INTERCEPTOR_CONTEXT(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_WRITER_INTERCEPTOR_CONTEXT(arg0));
    }
    
    public static Localizable localizableIGNORED_SCHEDULER_PROVIDERS(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("ignored.scheduler.providers", arg0, arg1);
    }
    
    public static String IGNORED_SCHEDULER_PROVIDERS(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableIGNORED_SCHEDULER_PROVIDERS(arg0, arg1));
    }
    
    public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("dependent.class.of.provider.not.found", arg0, arg1, arg2);
    }
    
    public static String DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(arg0, arg1, arg2));
    }
    
    public static Localizable localizableHTTP_HEADER_NO_END_SEPARATOR(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("http.header.no.end.separator", arg0);
    }
    
    public static String HTTP_HEADER_NO_END_SEPARATOR(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHTTP_HEADER_NO_END_SEPARATOR(arg0));
    }
    
    public static Localizable localizableSSL_KS_IMPL_NOT_FOUND() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ks.impl.not.found", new Object[0]);
    }
    
    public static String SSL_KS_IMPL_NOT_FOUND() {
        return LocalizationMessages.localizer.localize(localizableSSL_KS_IMPL_NOT_FOUND());
    }
    
    public static Localizable localizableERROR_PROVIDER_AND_RESOURCE_CONSTRAINED_TO_IGNORED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.provider.and.resource.constrainedTo.ignored", arg0);
    }
    
    public static String ERROR_PROVIDER_AND_RESOURCE_CONSTRAINED_TO_IGNORED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_PROVIDER_AND_RESOURCE_CONSTRAINED_TO_IGNORED(arg0));
    }
    
    public static Localizable localizableINVALID_PORT() {
        return LocalizationMessages.messageFactory.getMessage("invalid.port", new Object[0]);
    }
    
    public static String INVALID_PORT() {
        return LocalizationMessages.localizer.localize(localizableINVALID_PORT());
    }
    
    public static Localizable localizableERROR_INTERCEPTOR_WRITER_PROCEED() {
        return LocalizationMessages.messageFactory.getMessage("error.interceptor.writer.proceed", new Object[0]);
    }
    
    public static String ERROR_INTERCEPTOR_WRITER_PROCEED() {
        return LocalizationMessages.localizer.localize(localizableERROR_INTERCEPTOR_WRITER_PROCEED());
    }
    
    public static Localizable localizableHTTP_HEADER_NO_CHARS_BETWEEN_SEPARATORS(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("http.header.no.chars.between.separators", arg0, arg1);
    }
    
    public static String HTTP_HEADER_NO_CHARS_BETWEEN_SEPARATORS(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableHTTP_HEADER_NO_CHARS_BETWEEN_SEPARATORS(arg0, arg1));
    }
    
    public static Localizable localizableILLEGAL_LOAD_FACTOR(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("illegal.load.factor", arg0);
    }
    
    public static String ILLEGAL_LOAD_FACTOR(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableILLEGAL_LOAD_FACTOR(arg0));
    }
    
    public static Localizable localizableSOME_HEADERS_NOT_SENT(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("some.headers.not.sent", arg0, arg1);
    }
    
    public static String SOME_HEADERS_NOT_SENT(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableSOME_HEADERS_NOT_SENT(arg0, arg1));
    }
    
    public static Localizable localizableQUERY_PARAM_NULL() {
        return LocalizationMessages.messageFactory.getMessage("query.param.null", new Object[0]);
    }
    
    public static String QUERY_PARAM_NULL() {
        return LocalizationMessages.localizer.localize(localizableQUERY_PARAM_NULL());
    }
    
    public static Localizable localizableILLEGAL_PROVIDER_CLASS_NAME(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("illegal.provider.class.name", arg0);
    }
    
    public static String ILLEGAL_PROVIDER_CLASS_NAME(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableILLEGAL_PROVIDER_CLASS_NAME(arg0));
    }
    
    public static Localizable localizableSTREAM_PROVIDER_NULL() {
        return LocalizationMessages.messageFactory.getMessage("stream.provider.null", new Object[0]);
    }
    
    public static String STREAM_PROVIDER_NULL() {
        return LocalizationMessages.localizer.localize(localizableSTREAM_PROVIDER_NULL());
    }
    
    public static Localizable localizableINJECTION_MANAGER_STRATEGY_NOT_SUPPORTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("injection.manager.strategy.not.supported", arg0);
    }
    
    public static String INJECTION_MANAGER_STRATEGY_NOT_SUPPORTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableINJECTION_MANAGER_STRATEGY_NOT_SUPPORTED(arg0));
    }
    
    public static Localizable localizableSSL_TMF_PROVIDER_NOT_REGISTERED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.tmf.provider.not.registered", new Object[0]);
    }
    
    public static String SSL_TMF_PROVIDER_NOT_REGISTERED() {
        return LocalizationMessages.localizer.localize(localizableSSL_TMF_PROVIDER_NOT_REGISTERED());
    }
    
    public static Localizable localizableNO_CONTAINER_AVAILABLE() {
        return LocalizationMessages.messageFactory.getMessage("no.container.available", new Object[0]);
    }
    
    public static String NO_CONTAINER_AVAILABLE() {
        return LocalizationMessages.localizer.localize(localizableNO_CONTAINER_AVAILABLE());
    }
    
    public static Localizable localizableERROR_ENTITY_PROVIDER_BASICTYPES_CONSTRUCTOR(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.entity.provider.basictypes.constructor", arg0);
    }
    
    public static String ERROR_ENTITY_PROVIDER_BASICTYPES_CONSTRUCTOR(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_ENTITY_PROVIDER_BASICTYPES_CONSTRUCTOR(arg0));
    }
    
    public static Localizable localizableERROR_NOTFOUND_MESSAGEBODYWRITER(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.notfound.messagebodywriter", arg0, arg1, arg2);
    }
    
    public static String ERROR_NOTFOUND_MESSAGEBODYWRITER(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_NOTFOUND_MESSAGEBODYWRITER(arg0, arg1, arg2));
    }
    
    public static Localizable localizableCONTRACT_NOT_ASSIGNABLE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("contract.not.assignable", arg0, arg1);
    }
    
    public static String CONTRACT_NOT_ASSIGNABLE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableCONTRACT_NOT_ASSIGNABLE(arg0, arg1));
    }
    
    public static Localizable localizableSSL_TMF_ALGORITHM_NOT_SUPPORTED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.tmf.algorithm.not.supported", new Object[0]);
    }
    
    public static String SSL_TMF_ALGORITHM_NOT_SUPPORTED() {
        return LocalizationMessages.localizer.localize(localizableSSL_TMF_ALGORITHM_NOT_SUPPORTED());
    }
    
    public static Localizable localizableOSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("osgi.registry.error.opening.resource.stream", arg0);
    }
    
    public static String OSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableOSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(arg0));
    }
    
    public static Localizable localizableMBR_TRYING_TO_CLOSE_STREAM(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("mbr.trying.to.close.stream", arg0);
    }
    
    public static String MBR_TRYING_TO_CLOSE_STREAM(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableMBR_TRYING_TO_CLOSE_STREAM(arg0));
    }
    
    public static Localizable localizableIGNORED_EXECUTOR_PROVIDERS(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("ignored.executor.providers", arg0, arg1);
    }
    
    public static String IGNORED_EXECUTOR_PROVIDERS(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableIGNORED_EXECUTOR_PROVIDERS(arg0, arg1));
    }
    
    public static Localizable localizableURI_PARSER_NOT_EXECUTED() {
        return LocalizationMessages.messageFactory.getMessage("uri.parser.not.executed", new Object[0]);
    }
    
    public static String URI_PARSER_NOT_EXECUTED() {
        return LocalizationMessages.localizer.localize(localizableURI_PARSER_NOT_EXECUTED());
    }
    
    public static Localizable localizableMESSAGE_CONTENT_BUFFERING_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("message.content.buffering.failed", new Object[0]);
    }
    
    public static String MESSAGE_CONTENT_BUFFERING_FAILED() {
        return LocalizationMessages.localizer.localize(localizableMESSAGE_CONTENT_BUFFERING_FAILED());
    }
    
    public static Localizable localizableRESPONSE_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("response.closed", new Object[0]);
    }
    
    public static String RESPONSE_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableRESPONSE_CLOSED());
    }
    
    public static Localizable localizableSSL_KS_LOAD_ERROR(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("ssl.ks.load.error", arg0);
    }
    
    public static String SSL_KS_LOAD_ERROR(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSSL_KS_LOAD_ERROR(arg0));
    }
    
    public static Localizable localizableCOMMITTING_STREAM_ALREADY_INITIALIZED() {
        return LocalizationMessages.messageFactory.getMessage("committing.stream.already.initialized", new Object[0]);
    }
    
    public static String COMMITTING_STREAM_ALREADY_INITIALIZED() {
        return LocalizationMessages.localizer.localize(localizableCOMMITTING_STREAM_ALREADY_INITIALIZED());
    }
    
    public static Localizable localizableERROR_ENTITY_PROVIDER_BASICTYPES_CHARACTER_MORECHARS() {
        return LocalizationMessages.messageFactory.getMessage("error.entity.provider.basictypes.character.morechars", new Object[0]);
    }
    
    public static String ERROR_ENTITY_PROVIDER_BASICTYPES_CHARACTER_MORECHARS() {
        return LocalizationMessages.localizer.localize(localizableERROR_ENTITY_PROVIDER_BASICTYPES_CHARACTER_MORECHARS());
    }
    
    public static Localizable localizableERROR_ENTITY_STREAM_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("error.entity.stream.closed", new Object[0]);
    }
    
    public static String ERROR_ENTITY_STREAM_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableERROR_ENTITY_STREAM_CLOSED());
    }
    
    public static Localizable localizableMESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("message.content.input.stream.close.failed", new Object[0]);
    }
    
    public static String MESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED() {
        return LocalizationMessages.localizer.localize(localizableMESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED());
    }
    
    public static Localizable localizableERROR_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.provider.constrainedTo.wrong.package", arg0, arg1);
    }
    
    public static String ERROR_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(arg0, arg1));
    }
    
    public static Localizable localizableSSL_KS_PROVIDERS_NOT_REGISTERED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ks.providers.not.registered", new Object[0]);
    }
    
    public static String SSL_KS_PROVIDERS_NOT_REGISTERED() {
        return LocalizationMessages.localizer.localize(localizableSSL_KS_PROVIDERS_NOT_REGISTERED());
    }
    
    public static Localizable localizablePROPERTIES_HELPER_GET_VALUE_NO_TRANSFORM(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("properties.helper.get.value.no.transform", arg0, arg1, arg2);
    }
    
    public static String PROPERTIES_HELPER_GET_VALUE_NO_TRANSFORM(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizablePROPERTIES_HELPER_GET_VALUE_NO_TRANSFORM(arg0, arg1, arg2));
    }
    
    public static Localizable localizableERROR_TEMPLATE_PARSER_INVALID_SYNTAX_TERMINATED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.template.parser.invalid.syntax.terminated", arg0);
    }
    
    public static String ERROR_TEMPLATE_PARSER_INVALID_SYNTAX_TERMINATED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_TEMPLATE_PARSER_INVALID_SYNTAX_TERMINATED(arg0));
    }
    
    public static Localizable localizableURI_BUILDER_URI_PART_FRAGMENT(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("uri.builder.uri.part.fragment", arg0, arg1);
    }
    
    public static String URI_BUILDER_URI_PART_FRAGMENT(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableURI_BUILDER_URI_PART_FRAGMENT(arg0, arg1));
    }
    
    public static Localizable localizableERROR_MBW_ISWRITABLE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.mbw.iswritable", arg0);
    }
    
    public static String ERROR_MBW_ISWRITABLE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MBW_ISWRITABLE(arg0));
    }
    
    public static Localizable localizableERROR_READING_ENTITY_MISSING() {
        return LocalizationMessages.messageFactory.getMessage("error.reading.entity.missing", new Object[0]);
    }
    
    public static String ERROR_READING_ENTITY_MISSING() {
        return LocalizationMessages.localizer.localize(localizableERROR_READING_ENTITY_MISSING());
    }
    
    public static Localizable localizableINVALID_HOST() {
        return LocalizationMessages.messageFactory.getMessage("invalid.host", new Object[0]);
    }
    
    public static String INVALID_HOST() {
        return LocalizationMessages.localizer.localize(localizableINVALID_HOST());
    }
    
    public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("dependent.class.of.provider.format.error", arg0, arg1, arg2);
    }
    
    public static String DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(arg0, arg1, arg2));
    }
    
    public static Localizable localizableEXCEPTION_MAPPER_SUPPORTED_TYPE_UNKNOWN(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("exception.mapper.supported.type.unknown", arg0);
    }
    
    public static String EXCEPTION_MAPPER_SUPPORTED_TYPE_UNKNOWN(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableEXCEPTION_MAPPER_SUPPORTED_TYPE_UNKNOWN(arg0));
    }
    
    public static Localizable localizableINJECTION_MANAGER_NOT_PROVIDED() {
        return LocalizationMessages.messageFactory.getMessage("injection.manager.not.provided", new Object[0]);
    }
    
    public static String INJECTION_MANAGER_NOT_PROVIDED() {
        return LocalizationMessages.localizer.localize(localizableINJECTION_MANAGER_NOT_PROVIDED());
    }
    
    public static Localizable localizableSSL_KMF_NO_PASSWORD_FOR_BYTE_BASED_KS() {
        return LocalizationMessages.messageFactory.getMessage("ssl.kmf.no.password.for.byte.based.ks", new Object[0]);
    }
    
    public static String SSL_KMF_NO_PASSWORD_FOR_BYTE_BASED_KS() {
        return LocalizationMessages.localizer.localize(localizableSSL_KMF_NO_PASSWORD_FOR_BYTE_BASED_KS());
    }
    
    public static Localizable localizableSLOW_SUBSCRIBER(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("slow.subscriber", arg0);
    }
    
    public static String SLOW_SUBSCRIBER(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSLOW_SUBSCRIBER(arg0));
    }
    
    public static Localizable localizableTYPE_TO_CLASS_CONVERSION_NOT_SUPPORTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("type.to.class.conversion.not.supported", arg0);
    }
    
    public static String TYPE_TO_CLASS_CONVERSION_NOT_SUPPORTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableTYPE_TO_CLASS_CONVERSION_NOT_SUPPORTED(arg0));
    }
    
    public static Localizable localizableUNKNOWN_SUBSCRIBER() {
        return LocalizationMessages.messageFactory.getMessage("unknown.subscriber", new Object[0]);
    }
    
    public static String UNKNOWN_SUBSCRIBER() {
        return LocalizationMessages.localizer.localize(localizableUNKNOWN_SUBSCRIBER());
    }
    
    public static Localizable localizableFEATURE_HAS_ALREADY_BEEN_PROCESSED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("feature.has.already.been.processed", arg0);
    }
    
    public static String FEATURE_HAS_ALREADY_BEEN_PROCESSED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableFEATURE_HAS_ALREADY_BEEN_PROCESSED(arg0));
    }
    
    public static Localizable localizableSSL_CTX_INIT_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ctx.init.failed", new Object[0]);
    }
    
    public static String SSL_CTX_INIT_FAILED() {
        return LocalizationMessages.localizer.localize(localizableSSL_CTX_INIT_FAILED());
    }
    
    public static Localizable localizableERROR_TEMPLATE_PARSER_INVALID_SYNTAX(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("error.template.parser.invalid.syntax", arg0, arg1, arg2);
    }
    
    public static String ERROR_TEMPLATE_PARSER_INVALID_SYNTAX(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableERROR_TEMPLATE_PARSER_INVALID_SYNTAX(arg0, arg1, arg2));
    }
    
    public static Localizable localizableURI_BUILDER_SCHEME_PART_UNEXPECTED_COMPONENT(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("uri.builder.scheme.part.unexpected.component", arg0, arg1);
    }
    
    public static String URI_BUILDER_SCHEME_PART_UNEXPECTED_COMPONENT(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableURI_BUILDER_SCHEME_PART_UNEXPECTED_COMPONENT(arg0, arg1));
    }
    
    public static Localizable localizableSSL_TS_IMPL_NOT_FOUND() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ts.impl.not.found", new Object[0]);
    }
    
    public static String SSL_TS_IMPL_NOT_FOUND() {
        return LocalizationMessages.localizer.localize(localizableSSL_TS_IMPL_NOT_FOUND());
    }
    
    public static Localizable localizableWARNING_MSG(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("warning.msg", arg0);
    }
    
    public static String WARNING_MSG(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableWARNING_MSG(arg0));
    }
    
    public static Localizable localizableWARNING_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.messageFactory.getMessage("warning.provider.constrainedTo.wrong.package", arg0, arg1, arg2, arg3);
    }
    
    public static String WARNING_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.localizer.localize(localizableWARNING_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableHINTS_DETECTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("hints.detected", arg0);
    }
    
    public static String HINTS_DETECTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHINTS_DETECTED(arg0));
    }
    
    public static Localizable localizableHTTP_HEADER_UNBALANCED_COMMENTS() {
        return LocalizationMessages.messageFactory.getMessage("http.header.unbalanced.comments", new Object[0]);
    }
    
    public static String HTTP_HEADER_UNBALANCED_COMMENTS() {
        return LocalizationMessages.localizer.localize(localizableHTTP_HEADER_UNBALANCED_COMMENTS());
    }
    
    public static Localizable localizableURI_BUILDER_METHODNAME_NOT_SPECIFIED(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("uri.builder.methodname.not.specified", arg0, arg1);
    }
    
    public static String URI_BUILDER_METHODNAME_NOT_SPECIFIED(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableURI_BUILDER_METHODNAME_NOT_SPECIFIED(arg0, arg1));
    }
    
    public static Localizable localizableSSL_KMF_UNRECOVERABLE_KEY() {
        return LocalizationMessages.messageFactory.getMessage("ssl.kmf.unrecoverable.key", new Object[0]);
    }
    
    public static String SSL_KMF_UNRECOVERABLE_KEY() {
        return LocalizationMessages.localizer.localize(localizableSSL_KMF_UNRECOVERABLE_KEY());
    }
    
    public static Localizable localizableINJECTION_ERROR_SUITABLE_CONSTRUCTOR_NOT_FOUND(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("injection.error.suitable.constructor.not.found", arg0);
    }
    
    public static String INJECTION_ERROR_SUITABLE_CONSTRUCTOR_NOT_FOUND(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableINJECTION_ERROR_SUITABLE_CONSTRUCTOR_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableAUTODISCOVERABLE_CONFIGURATION_FAILED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("autodiscoverable.configuration.failed", arg0);
    }
    
    public static String AUTODISCOVERABLE_CONFIGURATION_FAILED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableAUTODISCOVERABLE_CONFIGURATION_FAILED(arg0));
    }
    
    public static Localizable localizableURI_COMPONENT_INVALID_CHARACTER(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.messageFactory.getMessage("uri.component.invalid.character", arg0, arg1, arg2, arg3);
    }
    
    public static String URI_COMPONENT_INVALID_CHARACTER(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.localizer.localize(localizableURI_COMPONENT_INVALID_CHARACTER(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizableSSL_KS_FILE_NOT_FOUND(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("ssl.ks.file.not.found", arg0);
    }
    
    public static String SSL_KS_FILE_NOT_FOUND(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableSSL_KS_FILE_NOT_FOUND(arg0));
    }
    
    public static Localizable localizableEXCEPTION_CAUGHT_WHILE_LOADING_SPI_PROVIDERS() {
        return LocalizationMessages.messageFactory.getMessage("exception.caught.while.loading.spi.providers", new Object[0]);
    }
    
    public static String EXCEPTION_CAUGHT_WHILE_LOADING_SPI_PROVIDERS() {
        return LocalizationMessages.localizer.localize(localizableEXCEPTION_CAUGHT_WHILE_LOADING_SPI_PROVIDERS());
    }
    
    public static Localizable localizableERROR_MSG(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.msg", arg0);
    }
    
    public static String ERROR_MSG(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_MSG(arg0));
    }
    
    public static Localizable localizableURI_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("uri.is.null", new Object[0]);
    }
    
    public static String URI_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableURI_IS_NULL());
    }
    
    public static Localizable localizableOSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("osgi.registry.error.processing.resource.stream", arg0);
    }
    
    public static String OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableOSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(arg0));
    }
    
    public static Localizable localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("provider.class.could.not.be.loaded", arg0, arg1, arg2);
    }
    
    public static String PROVIDER_CLASS_COULD_NOT_BE_LOADED(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(arg0, arg1, arg2));
    }
    
    public static Localizable localizableCOMPONENT_TYPE_ALREADY_REGISTERED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("component.type.already.registered", arg0);
    }
    
    public static String COMPONENT_TYPE_ALREADY_REGISTERED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableCOMPONENT_TYPE_ALREADY_REGISTERED(arg0));
    }
    
    public static Localizable localizableERROR_ENTITY_PROVIDER_BASICTYPES_UNKWNOWN(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.entity.provider.basictypes.unkwnown", arg0);
    }
    
    public static String ERROR_ENTITY_PROVIDER_BASICTYPES_UNKWNOWN(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_ENTITY_PROVIDER_BASICTYPES_UNKWNOWN(arg0));
    }
    
    public static Localizable localizableSTRING_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("string.is.null", new Object[0]);
    }
    
    public static String STRING_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableSTRING_IS_NULL());
    }
    
    public static Localizable localizableDATE_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("date.is.null", new Object[0]);
    }
    
    public static String DATE_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableDATE_IS_NULL());
    }
    
    public static Localizable localizableERROR_RESOLVING_GENERIC_TYPE_VALUE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.resolving.generic.type.value", arg0, arg1);
    }
    
    public static String ERROR_RESOLVING_GENERIC_TYPE_VALUE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_RESOLVING_GENERIC_TYPE_VALUE(arg0, arg1));
    }
    
    public static Localizable localizableERROR_TEMPLATE_PARSER_NAME_MORE_THAN_ONCE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("error.template.parser.name.more.than.once", arg0, arg1);
    }
    
    public static String ERROR_TEMPLATE_PARSER_NAME_MORE_THAN_ONCE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableERROR_TEMPLATE_PARSER_NAME_MORE_THAN_ONCE(arg0, arg1));
    }
    
    public static Localizable localizableSSL_TS_INTEGRITY_ALGORITHM_NOT_FOUND() {
        return LocalizationMessages.messageFactory.getMessage("ssl.ts.integrity.algorithm.not.found", new Object[0]);
    }
    
    public static String SSL_TS_INTEGRITY_ALGORITHM_NOT_FOUND() {
        return LocalizationMessages.localizer.localize(localizableSSL_TS_INTEGRITY_ALGORITHM_NOT_FOUND());
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.internal.localization");
        localizer = new Localizer();
    }
}
