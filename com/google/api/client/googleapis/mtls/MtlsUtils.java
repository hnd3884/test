package com.google.api.client.googleapis.mtls;

import com.google.api.client.json.JsonParser;
import com.google.api.client.googleapis.util.Utils;
import java.security.GeneralSecurityException;
import java.util.List;
import java.io.InputStream;
import java.io.FileNotFoundException;
import com.google.api.client.util.SecurityUtils;
import java.io.IOException;
import java.io.FileInputStream;
import java.security.KeyStore;
import com.google.common.annotations.VisibleForTesting;
import com.google.api.client.util.Beta;

@Beta
public class MtlsUtils
{
    private static final MtlsProvider MTLS_PROVIDER;
    
    public static MtlsProvider getDefaultMtlsProvider() {
        return MtlsUtils.MTLS_PROVIDER;
    }
    
    static {
        MTLS_PROVIDER = new DefaultMtlsProvider();
    }
    
    @VisibleForTesting
    static class DefaultMtlsProvider implements MtlsProvider
    {
        private static final String DEFAULT_CONTEXT_AWARE_METADATA_PATH;
        public static final String GOOGLE_API_USE_CLIENT_CERTIFICATE = "GOOGLE_API_USE_CLIENT_CERTIFICATE";
        private EnvironmentProvider envProvider;
        private String metadataPath;
        
        DefaultMtlsProvider() {
            this(new SystemEnvironmentProvider(), DefaultMtlsProvider.DEFAULT_CONTEXT_AWARE_METADATA_PATH);
        }
        
        @VisibleForTesting
        DefaultMtlsProvider(final EnvironmentProvider envProvider, final String metadataPath) {
            this.envProvider = envProvider;
            this.metadataPath = metadataPath;
        }
        
        @Override
        public boolean useMtlsClientCertificate() {
            final String useClientCertificate = this.envProvider.getenv("GOOGLE_API_USE_CLIENT_CERTIFICATE");
            return "true".equals(useClientCertificate);
        }
        
        @Override
        public String getKeyStorePassword() {
            return "";
        }
        
        @Override
        public KeyStore getKeyStore() throws IOException, GeneralSecurityException {
            try {
                final InputStream stream = new FileInputStream(this.metadataPath);
                final List<String> command = extractCertificateProviderCommand(stream);
                final Process process = new ProcessBuilder(command).start();
                final int exitCode = runCertificateProviderCommand(process, 1000L);
                if (exitCode != 0) {
                    throw new IOException("Cert provider command failed with exit code: " + exitCode);
                }
                return SecurityUtils.createMtlsKeyStore(process.getInputStream());
            }
            catch (final FileNotFoundException ignored) {
                return null;
            }
            catch (final InterruptedException e) {
                throw new IOException("Interrupted executing certificate provider command", e);
            }
        }
        
        @VisibleForTesting
        static List<String> extractCertificateProviderCommand(final InputStream contextAwareMetadata) throws IOException {
            final JsonParser parser = Utils.getDefaultJsonFactory().createJsonParser(contextAwareMetadata);
            final ContextAwareMetadataJson json = (ContextAwareMetadataJson)parser.parse((Class)ContextAwareMetadataJson.class);
            return json.getCommands();
        }
        
        @VisibleForTesting
        static int runCertificateProviderCommand(final Process commandProcess, final long timeoutMilliseconds) throws IOException, InterruptedException {
            final long startTime = System.currentTimeMillis();
            long remainTime = timeoutMilliseconds;
            boolean terminated = false;
            while (true) {
                try {
                    commandProcess.exitValue();
                    terminated = true;
                }
                catch (final IllegalThreadStateException ex) {
                    if (remainTime > 0L) {
                        Thread.sleep(Math.min(remainTime + 1L, 100L));
                    }
                    remainTime -= System.currentTimeMillis() - startTime;
                    if (remainTime > 0L) {
                        continue;
                    }
                }
                break;
            }
            if (!terminated) {
                commandProcess.destroy();
                throw new IOException("cert provider command timed out");
            }
            return commandProcess.exitValue();
        }
        
        static {
            DEFAULT_CONTEXT_AWARE_METADATA_PATH = System.getProperty("user.home") + "/.secureConnect/context_aware_metadata.json";
        }
        
        static class SystemEnvironmentProvider implements EnvironmentProvider
        {
            @Override
            public String getenv(final String name) {
                return System.getenv(name);
            }
        }
        
        interface EnvironmentProvider
        {
            String getenv(final String p0);
        }
    }
}
