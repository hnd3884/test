package org.glassfish.jersey.server.internal.inject;

import java.util.Iterator;
import java.util.Set;
import java.io.UnsupportedEncodingException;
import javax.ws.rs.ProcessingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
import org.glassfish.jersey.message.internal.ReaderWriter;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.internal.util.collection.NullableMultivaluedHashMap;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.ParamException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.Encoded;
import org.glassfish.jersey.message.internal.MediaTypes;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.ws.rs.core.Form;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
final class FormParamValueParamProvider extends AbstractValueParamProvider
{
    public FormParamValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep) {
        super(mpep, new Parameter.Source[] { Parameter.Source.FORM });
    }
    
    public Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
        final String parameterName = parameter.getSourceName();
        if (parameterName == null || parameterName.isEmpty()) {
            return null;
        }
        final MultivaluedParameterExtractor e = this.get(parameter);
        if (e == null) {
            return null;
        }
        return new FormParamValueProvider(e, !parameter.isEncoded());
    }
    
    private static final class FormParamValueProvider implements Function<ContainerRequest, Object>
    {
        private static final Annotation encodedAnnotation;
        private final MultivaluedParameterExtractor<?> extractor;
        private final boolean decode;
        
        FormParamValueProvider(final MultivaluedParameterExtractor<?> extractor, final boolean decode) {
            this.extractor = extractor;
            this.decode = decode;
        }
        
        private static Form getCachedForm(final ContainerRequest request, final boolean decode) {
            return (Form)request.getProperty(decode ? "jersey.config.server.representation.decoded.form" : "jersey.config.server.representation.form");
        }
        
        private static ContainerRequest ensureValidRequest(final ContainerRequest request) throws IllegalStateException {
            if (request.getMethod().equals("GET")) {
                throw new IllegalStateException(LocalizationMessages.FORM_PARAM_METHOD_ERROR());
            }
            if (!MediaTypes.typeEqual(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
                throw new IllegalStateException(LocalizationMessages.FORM_PARAM_CONTENT_TYPE_ERROR());
            }
            return request;
        }
        
        private static Annotation getEncodedAnnotation() {
            @Encoded
            final class EncodedAnnotationTemp
            {
            }
            return EncodedAnnotationTemp.class.getAnnotation((Class<Annotation>)Encoded.class);
        }
        
        @Override
        public Object apply(final ContainerRequest request) {
            Form form = getCachedForm(request, this.decode);
            if (form == null) {
                final Form otherForm = getCachedForm(request, !this.decode);
                if (otherForm != null) {
                    form = this.switchUrlEncoding(request, otherForm);
                    this.cacheForm(request, form);
                }
                else {
                    form = this.getForm(request);
                    this.cacheForm(request, form);
                }
            }
            try {
                return this.extractor.extract((MultivaluedMap<String, String>)form.asMap());
            }
            catch (final ExtractorException e) {
                throw new ParamException.FormParamException(e.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
            }
        }
        
        private Form switchUrlEncoding(final ContainerRequest request, final Form otherForm) {
            final Set<Map.Entry<String, List<String>>> entries = otherForm.asMap().entrySet();
            final MultivaluedMap<String, String> formMap = (MultivaluedMap<String, String>)new NullableMultivaluedHashMap();
            for (final Map.Entry<String, List<String>> entry : entries) {
                final String charsetName = ReaderWriter.getCharset(MediaType.valueOf(request.getHeaderString("Content-Type"))).name();
                try {
                    final String key = this.decode ? URLDecoder.decode(entry.getKey(), charsetName) : URLEncoder.encode(entry.getKey(), charsetName);
                    for (final String value : entry.getValue()) {
                        if (value != null) {
                            formMap.add((Object)key, (Object)(this.decode ? URLDecoder.decode(value, charsetName) : URLEncoder.encode(value, charsetName)));
                        }
                        else {
                            formMap.add((Object)key, (Object)null);
                        }
                    }
                }
                catch (final UnsupportedEncodingException uee) {
                    throw new ProcessingException(LocalizationMessages.ERROR_UNSUPPORTED_ENCODING(charsetName, this.extractor.getName()), (Throwable)uee);
                }
            }
            return new Form((MultivaluedMap)formMap);
        }
        
        private void cacheForm(final ContainerRequest request, final Form form) {
            request.setProperty(this.decode ? "jersey.config.server.representation.decoded.form" : "jersey.config.server.representation.form", form);
        }
        
        private Form getForm(final ContainerRequest request) {
            return this.getFormParameters(ensureValidRequest(request));
        }
        
        private Form getFormParameters(final ContainerRequest request) {
            if (MediaTypes.typeEqual(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
                request.bufferEntity();
                Form form;
                if (this.decode) {
                    form = request.readEntity(Form.class);
                }
                else {
                    final Annotation[] annotations = { FormParamValueProvider.encodedAnnotation };
                    form = request.readEntity(Form.class, annotations);
                }
                return (form == null) ? new Form() : form;
            }
            return new Form();
        }
        
        static {
            encodedAnnotation = getEncodedAnnotation();
        }
    }
}
