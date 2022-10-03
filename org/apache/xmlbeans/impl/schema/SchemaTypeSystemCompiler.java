package org.apache.xmlbeans.impl.schema;

import java.io.Writer;
import java.util.Iterator;
import org.apache.xmlbeans.SchemaType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.xmlbeans.ResourceLoader;
import java.io.File;
import java.util.Map;
import java.net.URI;
import org.apache.xmlbeans.impl.common.XmlErrorWatcher;
import java.util.Collection;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaTypeSystem;

public class SchemaTypeSystemCompiler
{
    public static SchemaTypeSystem compile(final Parameters params) {
        return compileImpl(params.getExistingTypeSystem(), params.getName(), params.getSchemas(), params.getConfig(), params.getLinkTo(), params.getOptions(), params.getErrorListener(), params.isJavaize(), params.getBaseURI(), params.getSourcesToCopyMap(), params.getSchemasDir());
    }
    
    public static SchemaTypeSystemImpl compile(final String name, final SchemaTypeSystem existingSTS, final XmlObject[] input, final BindingConfig config, final SchemaTypeLoader linkTo, final Filer filer, XmlOptions options) throws XmlException {
        options = XmlOptions.maskNull(options);
        final ArrayList schemas = new ArrayList();
        if (input != null) {
            for (int i = 0; i < input.length; ++i) {
                if (input[i] instanceof SchemaDocument.Schema) {
                    schemas.add(input[i]);
                }
                else {
                    if (!(input[i] instanceof SchemaDocument) || ((SchemaDocument)input[i]).getSchema() == null) {
                        throw new XmlException("Thread " + Thread.currentThread().getName() + ": The " + i + "th supplied input is not a schema document: its type is " + input[i].schemaType());
                    }
                    schemas.add(((SchemaDocument)input[i]).getSchema());
                }
            }
        }
        final Collection userErrors = (Collection)options.get("ERROR_LISTENER");
        final XmlErrorWatcher errorWatcher = new XmlErrorWatcher(userErrors);
        final SchemaTypeSystemImpl stsi = compileImpl(existingSTS, name, schemas.toArray(new SchemaDocument.Schema[schemas.size()]), config, linkTo, options, errorWatcher, filer != null, (URI)options.get("BASE_URI"), null, null);
        if (errorWatcher.hasError() && stsi == null) {
            throw new XmlException(errorWatcher.firstError());
        }
        if (stsi != null && !stsi.isIncomplete() && filer != null) {
            stsi.save(filer);
            generateTypes(stsi, filer, options);
        }
        return stsi;
    }
    
    static SchemaTypeSystemImpl compileImpl(final SchemaTypeSystem system, final String name, final SchemaDocument.Schema[] schemas, final BindingConfig config, SchemaTypeLoader linkTo, final XmlOptions options, final Collection outsideErrors, final boolean javaize, final URI baseURI, final Map sourcesToCopyMap, final File schemasDir) {
        if (linkTo == null) {
            throw new IllegalArgumentException("Must supply linkTo");
        }
        final XmlErrorWatcher errorWatcher = new XmlErrorWatcher(outsideErrors);
        final boolean incremental = system != null;
        final StscState state = StscState.start();
        final boolean validate = options == null || !options.hasOption("COMPILE_NO_VALIDATION");
        try {
            state.setErrorListener(errorWatcher);
            state.setBindingConfig(config);
            state.setOptions(options);
            state.setGivenTypeSystemName(name);
            state.setSchemasDir(schemasDir);
            if (baseURI != null) {
                state.setBaseUri(baseURI);
            }
            linkTo = SchemaTypeLoaderImpl.build(new SchemaTypeLoader[] { BuiltinSchemaTypeSystem.get(), linkTo }, null, null);
            state.setImportingTypeLoader(linkTo);
            final List validSchemas = new ArrayList(schemas.length);
            if (validate) {
                final XmlOptions validateOptions = new XmlOptions().setErrorListener(errorWatcher);
                if (options.hasOption("VALIDATE_TREAT_LAX_AS_SKIP")) {
                    validateOptions.setValidateTreatLaxAsSkip();
                }
                for (int i = 0; i < schemas.length; ++i) {
                    if (schemas[i].validate(validateOptions)) {
                        validSchemas.add(schemas[i]);
                    }
                }
            }
            else {
                validSchemas.addAll(Arrays.asList(schemas));
            }
            SchemaDocument.Schema[] startWith = validSchemas.toArray(new SchemaDocument.Schema[validSchemas.size()]);
            if (incremental) {
                final Set namespaces = new HashSet();
                startWith = getSchemasToRecompile((SchemaTypeSystemImpl)system, startWith, namespaces);
                state.initFromTypeSystem((SchemaTypeSystemImpl)system, namespaces);
            }
            else {
                state.setDependencies(new SchemaDependencies());
            }
            final StscImporter.SchemaToProcess[] schemasAndChameleons = StscImporter.resolveImportsAndIncludes(startWith, incremental);
            StscTranslator.addAllDefinitions(schemasAndChameleons);
            StscResolver.resolveAll();
            StscChecker.checkAll();
            StscJavaizer.javaizeAllTypes(javaize);
            StscState.get().sts().loadFromStscState(state);
            if (sourcesToCopyMap != null) {
                sourcesToCopyMap.putAll(state.sourceCopyMap());
            }
            if (errorWatcher.hasError()) {
                if (!state.allowPartial() || state.getRecovered() != errorWatcher.size()) {
                    return null;
                }
                StscState.get().sts().setIncomplete(true);
            }
            if (system != null) {
                ((SchemaTypeSystemImpl)system).setIncomplete(true);
            }
            return StscState.get().sts();
        }
        finally {
            StscState.end();
        }
    }
    
    private static SchemaDocument.Schema[] getSchemasToRecompile(final SchemaTypeSystemImpl system, final SchemaDocument.Schema[] modified, final Set namespaces) {
        final Set modifiedFiles = new HashSet();
        final Map haveFile = new HashMap();
        final List result = new ArrayList();
        for (int i = 0; i < modified.length; ++i) {
            final String fileURL = modified[i].documentProperties().getSourceName();
            if (fileURL == null) {
                throw new IllegalArgumentException("One of the Schema files passed in doesn't have the source set, which prevents it to be incrementally compiled");
            }
            modifiedFiles.add(fileURL);
            haveFile.put(fileURL, modified[i]);
            result.add(modified[i]);
        }
        final SchemaDependencies dep = system.getDependencies();
        final List nss = dep.getNamespacesTouched(modifiedFiles);
        namespaces.addAll(dep.computeTransitiveClosure(nss));
        final List needRecompilation = dep.getFilesTouched(namespaces);
        StscState.get().setDependencies(new SchemaDependencies(dep, namespaces));
        for (int j = 0; j < needRecompilation.size(); ++j) {
            final String url = needRecompilation.get(j);
            final SchemaDocument.Schema have = haveFile.get(url);
            if (have == null) {
                try {
                    final XmlObject xdoc = StscImporter.DownloadTable.downloadDocument(StscState.get().getS4SLoader(), null, url);
                    final XmlOptions voptions = new XmlOptions();
                    voptions.setErrorListener(StscState.get().getErrorListener());
                    if (!(xdoc instanceof SchemaDocument) || !xdoc.validate(voptions)) {
                        StscState.get().error("Referenced document is not a valid schema, URL = " + url, 56, null);
                    }
                    else {
                        final SchemaDocument sDoc = (SchemaDocument)xdoc;
                        result.add(sDoc.getSchema());
                    }
                }
                catch (final MalformedURLException mfe) {
                    StscState.get().error("exception.loading.url", new Object[] { "MalformedURLException", url, mfe.getMessage() }, null);
                }
                catch (final IOException ioe) {
                    StscState.get().error("exception.loading.url", new Object[] { "IOException", url, ioe.getMessage() }, null);
                }
                catch (final XmlException xmle) {
                    StscState.get().error("exception.loading.url", new Object[] { "XmlException", url, xmle.getMessage() }, null);
                }
            }
        }
        return result.toArray(new SchemaDocument.Schema[result.size()]);
    }
    
    public static boolean generateTypes(final SchemaTypeSystem system, final Filer filer, final XmlOptions options) {
        if (system instanceof SchemaTypeSystemImpl && ((SchemaTypeSystemImpl)system).isIncomplete()) {
            return false;
        }
        boolean success = true;
        final List types = new ArrayList();
        types.addAll(Arrays.asList(system.globalTypes()));
        types.addAll(Arrays.asList(system.documentTypes()));
        types.addAll(Arrays.asList(system.attributeTypes()));
        for (final SchemaType type : types) {
            if (type.isBuiltinType()) {
                continue;
            }
            if (type.getFullJavaName() == null) {
                continue;
            }
            String fjn = type.getFullJavaName();
            Writer writer = null;
            try {
                writer = filer.createSourceFile(fjn);
                SchemaTypeCodePrinter.printType(writer, type, options);
            }
            catch (final IOException e) {
                System.err.println("IO Error " + e);
                success = false;
            }
            finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                }
                catch (final IOException ex) {}
            }
            try {
                fjn = type.getFullJavaImplName();
                writer = filer.createSourceFile(fjn);
                SchemaTypeCodePrinter.printTypeImpl(writer, type, options);
            }
            catch (final IOException e) {
                System.err.println("IO Error " + e);
                success = false;
            }
            finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                }
                catch (final IOException ex2) {}
            }
        }
        return success;
    }
    
    public static class Parameters
    {
        private SchemaTypeSystem existingSystem;
        private String name;
        private SchemaDocument.Schema[] schemas;
        private BindingConfig config;
        private SchemaTypeLoader linkTo;
        private XmlOptions options;
        private Collection errorListener;
        private boolean javaize;
        private URI baseURI;
        private Map sourcesToCopyMap;
        private File schemasDir;
        
        public SchemaTypeSystem getExistingTypeSystem() {
            return this.existingSystem;
        }
        
        public void setExistingTypeSystem(final SchemaTypeSystem system) {
            this.existingSystem = system;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public SchemaDocument.Schema[] getSchemas() {
            return this.schemas;
        }
        
        public void setSchemas(final SchemaDocument.Schema[] schemas) {
            this.schemas = schemas;
        }
        
        public BindingConfig getConfig() {
            return this.config;
        }
        
        public void setConfig(final BindingConfig config) {
            this.config = config;
        }
        
        public SchemaTypeLoader getLinkTo() {
            return this.linkTo;
        }
        
        public void setLinkTo(final SchemaTypeLoader linkTo) {
            this.linkTo = linkTo;
        }
        
        public XmlOptions getOptions() {
            return this.options;
        }
        
        public void setOptions(final XmlOptions options) {
            this.options = options;
        }
        
        public Collection getErrorListener() {
            return this.errorListener;
        }
        
        public void setErrorListener(final Collection errorListener) {
            this.errorListener = errorListener;
        }
        
        public boolean isJavaize() {
            return this.javaize;
        }
        
        public void setJavaize(final boolean javaize) {
            this.javaize = javaize;
        }
        
        public URI getBaseURI() {
            return this.baseURI;
        }
        
        public void setBaseURI(final URI baseURI) {
            this.baseURI = baseURI;
        }
        
        public Map getSourcesToCopyMap() {
            return this.sourcesToCopyMap;
        }
        
        public void setSourcesToCopyMap(final Map sourcesToCopyMap) {
            this.sourcesToCopyMap = sourcesToCopyMap;
        }
        
        public File getSchemasDir() {
            return this.schemasDir;
        }
        
        public void setSchemasDir(final File schemasDir) {
            this.schemasDir = schemasDir;
        }
    }
}
