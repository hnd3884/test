package org.apache.tomcat.util.descriptor.web;

import java.io.FileInputStream;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.apache.tomcat.Jar;
import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.JarScannerCallback;

public class FragmentJarScannerCallback implements JarScannerCallback
{
    private static final String FRAGMENT_LOCATION = "META-INF/web-fragment.xml";
    private final WebXmlParser webXmlParser;
    private final boolean delegate;
    private final boolean parseRequired;
    private final Map<String, WebXml> fragments;
    private boolean ok;
    
    public FragmentJarScannerCallback(final WebXmlParser webXmlParser, final boolean delegate, final boolean parseRequired) {
        this.fragments = new HashMap<String, WebXml>();
        this.ok = true;
        this.webXmlParser = webXmlParser;
        this.delegate = delegate;
        this.parseRequired = parseRequired;
    }
    
    public void scan(final Jar jar, final String webappPath, final boolean isWebapp) throws IOException {
        InputStream is = null;
        final WebXml fragment = new WebXml();
        fragment.setWebappJar(isWebapp);
        fragment.setDelegate(this.delegate);
        try {
            if (isWebapp && this.parseRequired) {
                is = jar.getInputStream("META-INF/web-fragment.xml");
            }
            if (is == null) {
                fragment.setDistributable(true);
            }
            else {
                final String fragmentUrl = jar.getURL("META-INF/web-fragment.xml");
                final InputSource source = new InputSource(fragmentUrl);
                source.setByteStream(is);
                if (!this.webXmlParser.parseWebXml(source, fragment, true)) {
                    this.ok = false;
                }
            }
        }
        finally {
            this.addFragment(fragment, jar.getJarFileURL());
        }
    }
    
    private String extractJarFileName(final URL input) {
        String url = input.toString();
        if (url.endsWith("!/")) {
            url = url.substring(0, url.length() - 2);
        }
        return url.substring(url.lastIndexOf(47) + 1);
    }
    
    public void scan(final File file, final String webappPath, final boolean isWebapp) throws IOException {
        final WebXml fragment = new WebXml();
        fragment.setWebappJar(isWebapp);
        fragment.setDelegate(this.delegate);
        final File fragmentFile = new File(file, "META-INF/web-fragment.xml");
        try {
            if (fragmentFile.isFile()) {
                try (final InputStream stream = new FileInputStream(fragmentFile)) {
                    final InputSource source = new InputSource(fragmentFile.toURI().toURL().toString());
                    source.setByteStream(stream);
                    if (!this.webXmlParser.parseWebXml(source, fragment, true)) {
                        this.ok = false;
                    }
                }
            }
            else {
                fragment.setDistributable(true);
            }
        }
        finally {
            this.addFragment(fragment, file.toURI().toURL());
        }
    }
    
    private void addFragment(final WebXml fragment, final URL url) {
        fragment.setURL(url);
        if (fragment.getName() == null) {
            fragment.setName(url.toString());
        }
        fragment.setJarName(this.extractJarFileName(url));
        if (this.fragments.containsKey(fragment.getName())) {
            final String duplicateName = fragment.getName();
            this.fragments.get(duplicateName).setDuplicated(true);
            fragment.setName(url.toString());
        }
        this.fragments.put(fragment.getName(), fragment);
    }
    
    public void scanWebInfClasses() {
    }
    
    public boolean isOk() {
        return this.ok;
    }
    
    public Map<String, WebXml> getFragments() {
        return this.fragments;
    }
}
