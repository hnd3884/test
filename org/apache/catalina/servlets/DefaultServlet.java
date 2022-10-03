package org.apache.catalina.servlets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import org.xml.sax.InputSource;
import org.xml.sax.ext.EntityResolver2;
import java.io.Serializable;
import org.apache.tomcat.util.http.parser.EntityTag;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import org.xml.sax.EntityResolver;
import java.util.Locale;
import org.apache.catalina.Context;
import org.apache.catalina.util.IOTools;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.apache.catalina.util.ServerInfo;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.catalina.Globals;
import org.apache.tomcat.util.security.Escape;
import javax.xml.transform.Source;
import java.util.Iterator;
import org.apache.tomcat.util.http.parser.Ranges;
import org.apache.tomcat.util.http.parser.ContentRange;
import java.io.StringReader;
import java.util.Enumeration;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;
import org.apache.catalina.webresources.CachedResource;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.apache.coyote.Constants;
import org.apache.catalina.connector.ResponseFacade;
import javax.servlet.ServletResponseWrapper;
import org.apache.tomcat.util.http.ResponseUtil;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.util.URLEncoder;
import java.io.BufferedInputStream;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.InputStream;
import org.apache.catalina.WebResource;
import java.io.FileInputStream;
import org.apache.catalina.connector.RequestFacade;
import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import javax.servlet.UnavailableException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import org.apache.tomcat.util.buf.B2CConverter;
import java.nio.charset.Charset;
import org.apache.catalina.WebResourceRoot;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.tomcat.util.res.StringManager;
import javax.servlet.http.HttpServlet;

public class DefaultServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    protected static final StringManager sm;
    private static final DocumentBuilderFactory factory;
    private static final SecureEntityResolver secureEntityResolver;
    protected static final ArrayList<Range> FULL;
    private static final Range IGNORE;
    protected static final String mimeSeparation = "CATALINA_MIME_BOUNDARY";
    @Deprecated
    protected static final String RESOURCES_JNDI_NAME = "java:/comp/Resources";
    protected static final int BUFFER_SIZE = 4096;
    protected int debug;
    protected int input;
    protected boolean listings;
    protected boolean readOnly;
    protected CompressionFormat[] compressionFormats;
    protected int output;
    protected String localXsltFile;
    protected String contextXsltFile;
    protected String globalXsltFile;
    protected String readmeFile;
    protected transient WebResourceRoot resources;
    protected String fileEncoding;
    private transient Charset fileEncodingCharset;
    private BomConfig useBomIfPresent;
    protected int sendfileSize;
    protected boolean useAcceptRanges;
    protected boolean showServerInfo;
    protected boolean sortListings;
    protected transient SortManager sortManager;
    private boolean allowPartialPut;
    
    public DefaultServlet() {
        this.debug = 0;
        this.input = 2048;
        this.listings = false;
        this.readOnly = true;
        this.output = 2048;
        this.localXsltFile = null;
        this.contextXsltFile = null;
        this.globalXsltFile = null;
        this.readmeFile = null;
        this.resources = null;
        this.fileEncoding = null;
        this.fileEncodingCharset = null;
        this.useBomIfPresent = null;
        this.sendfileSize = 49152;
        this.useAcceptRanges = true;
        this.showServerInfo = true;
        this.sortListings = false;
        this.allowPartialPut = true;
    }
    
    public void destroy() {
    }
    
    public void init() throws ServletException {
        if (this.getServletConfig().getInitParameter("debug") != null) {
            this.debug = Integer.parseInt(this.getServletConfig().getInitParameter("debug"));
        }
        if (this.getServletConfig().getInitParameter("input") != null) {
            this.input = Integer.parseInt(this.getServletConfig().getInitParameter("input"));
        }
        if (this.getServletConfig().getInitParameter("output") != null) {
            this.output = Integer.parseInt(this.getServletConfig().getInitParameter("output"));
        }
        this.listings = Boolean.parseBoolean(this.getServletConfig().getInitParameter("listings"));
        if (this.getServletConfig().getInitParameter("readonly") != null) {
            this.readOnly = Boolean.parseBoolean(this.getServletConfig().getInitParameter("readonly"));
        }
        this.compressionFormats = this.parseCompressionFormats(this.getServletConfig().getInitParameter("precompressed"), this.getServletConfig().getInitParameter("gzip"));
        if (this.getServletConfig().getInitParameter("sendfileSize") != null) {
            this.sendfileSize = Integer.parseInt(this.getServletConfig().getInitParameter("sendfileSize")) * 1024;
        }
        this.fileEncoding = this.getServletConfig().getInitParameter("fileEncoding");
        if (this.fileEncoding == null) {
            this.fileEncodingCharset = Charset.defaultCharset();
            this.fileEncoding = this.fileEncodingCharset.name();
        }
        else {
            try {
                this.fileEncodingCharset = B2CConverter.getCharset(this.fileEncoding);
            }
            catch (final UnsupportedEncodingException e) {
                throw new ServletException((Throwable)e);
            }
        }
        final String useBomIfPresent = this.getServletConfig().getInitParameter("useBomIfPresent");
        if (useBomIfPresent == null) {
            this.useBomIfPresent = BomConfig.TRUE;
        }
        else {
            for (final BomConfig bomConfig : BomConfig.values()) {
                if (bomConfig.configurationValue.equalsIgnoreCase(useBomIfPresent)) {
                    this.useBomIfPresent = bomConfig;
                    break;
                }
            }
            if (this.useBomIfPresent == null) {
                final IllegalArgumentException iae = new IllegalArgumentException(DefaultServlet.sm.getString("defaultServlet.unknownBomConfig", new Object[] { useBomIfPresent }));
                throw new ServletException((Throwable)iae);
            }
        }
        this.globalXsltFile = this.getServletConfig().getInitParameter("globalXsltFile");
        this.contextXsltFile = this.getServletConfig().getInitParameter("contextXsltFile");
        this.localXsltFile = this.getServletConfig().getInitParameter("localXsltFile");
        this.readmeFile = this.getServletConfig().getInitParameter("readmeFile");
        if (this.getServletConfig().getInitParameter("useAcceptRanges") != null) {
            this.useAcceptRanges = Boolean.parseBoolean(this.getServletConfig().getInitParameter("useAcceptRanges"));
        }
        if (this.input < 256) {
            this.input = 256;
        }
        if (this.output < 256) {
            this.output = 256;
        }
        if (this.debug > 0) {
            this.log("DefaultServlet.init:  input buffer size=" + this.input + ", output buffer size=" + this.output);
        }
        this.resources = (WebResourceRoot)this.getServletContext().getAttribute("org.apache.catalina.resources");
        if (this.resources == null) {
            throw new UnavailableException(DefaultServlet.sm.getString("defaultServlet.noResources"));
        }
        if (this.getServletConfig().getInitParameter("showServerInfo") != null) {
            this.showServerInfo = Boolean.parseBoolean(this.getServletConfig().getInitParameter("showServerInfo"));
        }
        if (this.getServletConfig().getInitParameter("sortListings") != null) {
            this.sortListings = Boolean.parseBoolean(this.getServletConfig().getInitParameter("sortListings"));
            if (this.sortListings) {
                final boolean sortDirectoriesFirst = this.getServletConfig().getInitParameter("sortDirectoriesFirst") != null && Boolean.parseBoolean(this.getServletConfig().getInitParameter("sortDirectoriesFirst"));
                this.sortManager = new SortManager(sortDirectoriesFirst);
            }
        }
        if (this.getServletConfig().getInitParameter("allowPartialPut") != null) {
            this.allowPartialPut = Boolean.parseBoolean(this.getServletConfig().getInitParameter("allowPartialPut"));
        }
    }
    
    private CompressionFormat[] parseCompressionFormats(final String precompressed, final String gzip) {
        final List<CompressionFormat> ret = new ArrayList<CompressionFormat>();
        if (precompressed != null && precompressed.indexOf(61) > 0) {
            for (final String pair : precompressed.split(",")) {
                final String[] setting = pair.split("=");
                final String encoding = setting[0];
                final String extension = setting[1];
                ret.add(new CompressionFormat(extension, encoding));
            }
        }
        else if (precompressed != null) {
            if (Boolean.parseBoolean(precompressed)) {
                ret.add(new CompressionFormat(".br", "br"));
                ret.add(new CompressionFormat(".gz", "gzip"));
            }
        }
        else if (Boolean.parseBoolean(gzip)) {
            ret.add(new CompressionFormat(".gz", "gzip"));
        }
        return ret.toArray(new CompressionFormat[0]);
    }
    
    protected String getRelativePath(final HttpServletRequest request) {
        return this.getRelativePath(request, false);
    }
    
    protected String getRelativePath(final HttpServletRequest request, final boolean allowEmptyPath) {
        String pathInfo;
        String servletPath;
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
            servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
        }
        else {
            pathInfo = request.getPathInfo();
            servletPath = request.getServletPath();
        }
        final StringBuilder result = new StringBuilder();
        if (servletPath.length() > 0) {
            result.append(servletPath);
        }
        if (pathInfo != null) {
            result.append(pathInfo);
        }
        if (result.length() == 0 && !allowEmptyPath) {
            result.append('/');
        }
        return result.toString();
    }
    
    protected String getPathPrefix(final HttpServletRequest request) {
        return request.getContextPath();
    }
    
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        if (req.getDispatcherType() == DispatcherType.ERROR) {
            this.doGet(req, resp);
        }
        else {
            super.service(req, resp);
        }
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.serveResource(request, response, true, this.fileEncoding);
    }
    
    protected void doHead(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final boolean serveContent = DispatcherType.INCLUDE.equals((Object)request.getDispatcherType());
        this.serveResource(request, response, serveContent, this.fileEncoding);
    }
    
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Allow", this.determineMethodsAllowed(req));
    }
    
    protected String determineMethodsAllowed(final HttpServletRequest req) {
        final StringBuilder allow = new StringBuilder();
        allow.append("OPTIONS, GET, HEAD, POST");
        if (!this.readOnly) {
            allow.append(", PUT, DELETE");
        }
        if (req instanceof RequestFacade && ((RequestFacade)req).getAllowTrace()) {
            allow.append(", TRACE");
        }
        return allow.toString();
    }
    
    protected void sendNotAllowed(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.addHeader("Allow", this.determineMethodsAllowed(req));
        resp.sendError(405);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.doGet(request, response);
    }
    
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            this.sendNotAllowed(req, resp);
            return;
        }
        final String path = this.getRelativePath(req);
        final WebResource resource = this.resources.getResource(path);
        final Range range = this.parseContentRange(req, resp);
        if (range == null) {
            return;
        }
        InputStream resourceInputStream = null;
        try {
            if (range == DefaultServlet.IGNORE) {
                resourceInputStream = (InputStream)req.getInputStream();
            }
            else {
                final File contentFile = this.executePartialPut(req, range, path);
                resourceInputStream = new FileInputStream(contentFile);
            }
            if (this.resources.write(path, resourceInputStream, true)) {
                if (resource.exists()) {
                    resp.setStatus(204);
                }
                else {
                    resp.setStatus(201);
                }
            }
            else {
                resp.sendError(409);
            }
        }
        finally {
            if (resourceInputStream != null) {
                try {
                    resourceInputStream.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    protected File executePartialPut(final HttpServletRequest req, final Range range, final String path) throws IOException {
        final File tempDir = (File)this.getServletContext().getAttribute("javax.servlet.context.tempdir");
        final String convertedResourcePath = path.replace('/', '.');
        final File contentFile = new File(tempDir, convertedResourcePath);
        if (contentFile.createNewFile()) {
            contentFile.deleteOnExit();
        }
        try (final RandomAccessFile randAccessContentFile = new RandomAccessFile(contentFile, "rw")) {
            final WebResource oldResource = this.resources.getResource(path);
            if (oldResource.isFile()) {
                try (final BufferedInputStream bufOldRevStream = new BufferedInputStream(oldResource.getInputStream(), 4096)) {
                    final byte[] copyBuffer = new byte[4096];
                    int numBytesRead;
                    while ((numBytesRead = bufOldRevStream.read(copyBuffer)) != -1) {
                        randAccessContentFile.write(copyBuffer, 0, numBytesRead);
                    }
                }
            }
            randAccessContentFile.setLength(range.length);
            randAccessContentFile.seek(range.start);
            final byte[] transferBuffer = new byte[4096];
            try (final BufferedInputStream requestBufInStream = new BufferedInputStream((InputStream)req.getInputStream(), 4096)) {
                int numBytesRead2;
                while ((numBytesRead2 = requestBufInStream.read(transferBuffer)) != -1) {
                    randAccessContentFile.write(transferBuffer, 0, numBytesRead2);
                }
            }
        }
        return contentFile;
    }
    
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            this.sendNotAllowed(req, resp);
            return;
        }
        final String path = this.getRelativePath(req);
        final WebResource resource = this.resources.getResource(path);
        if (resource.exists()) {
            if (resource.delete()) {
                resp.setStatus(204);
            }
            else {
                resp.sendError(405);
            }
        }
        else {
            resp.sendError(404);
        }
    }
    
    protected boolean checkIfHeaders(final HttpServletRequest request, final HttpServletResponse response, final WebResource resource) throws IOException {
        return this.checkIfMatch(request, response, resource) && this.checkIfModifiedSince(request, response, resource) && this.checkIfNoneMatch(request, response, resource) && this.checkIfUnmodifiedSince(request, response, resource);
    }
    
    protected String rewriteUrl(final String path) {
        return URLEncoder.DEFAULT.encode(path, StandardCharsets.UTF_8);
    }
    
    protected void serveResource(final HttpServletRequest request, final HttpServletResponse response, final boolean content, String inputEncoding) throws IOException, ServletException {
        boolean serveContent = content;
        final String path = this.getRelativePath(request, true);
        if (this.debug > 0) {
            if (serveContent) {
                this.log("DefaultServlet.serveResource:  Serving resource '" + path + "' headers and data");
            }
            else {
                this.log("DefaultServlet.serveResource:  Serving resource '" + path + "' headers only");
            }
        }
        if (path.length() == 0) {
            this.doDirectoryRedirect(request, response);
            return;
        }
        WebResource resource = this.resources.getResource(path);
        final boolean isError = DispatcherType.ERROR == request.getDispatcherType();
        if (!resource.exists()) {
            String requestUri = (String)request.getAttribute("javax.servlet.include.request_uri");
            if (requestUri == null) {
                requestUri = request.getRequestURI();
                if (isError) {
                    response.sendError((int)request.getAttribute("javax.servlet.error.status_code"));
                }
                else {
                    response.sendError(404, DefaultServlet.sm.getString("defaultServlet.missingResource", new Object[] { requestUri }));
                }
                return;
            }
            throw new FileNotFoundException(DefaultServlet.sm.getString("defaultServlet.missingResource", new Object[] { requestUri }));
        }
        else {
            if (resource.canRead()) {
                boolean included = false;
                if (resource.isFile()) {
                    included = (request.getAttribute("javax.servlet.include.context_path") != null);
                    if (!included && !isError && !this.checkIfHeaders(request, response, resource)) {
                        return;
                    }
                }
                String contentType = resource.getMimeType();
                if (contentType == null) {
                    contentType = this.getServletContext().getMimeType(resource.getName());
                    resource.setMimeType(contentType);
                }
                String eTag = null;
                String lastModifiedHttp = null;
                if (resource.isFile() && !isError) {
                    eTag = this.generateETag(resource);
                    lastModifiedHttp = resource.getLastModifiedHttp();
                }
                boolean usingPrecompressedVersion = false;
                if (this.compressionFormats.length > 0 && !included && resource.isFile() && !this.pathEndsWithCompressedExtension(path)) {
                    final List<PrecompressedResource> precompressedResources = this.getAvailablePrecompressedResources(path);
                    if (!precompressedResources.isEmpty()) {
                        ResponseUtil.addVaryFieldName(response, "accept-encoding");
                        final PrecompressedResource bestResource = this.getBestPrecompressedResource(request, precompressedResources);
                        if (bestResource != null) {
                            response.addHeader("Content-Encoding", bestResource.format.encoding);
                            resource = bestResource.resource;
                            usingPrecompressedVersion = true;
                        }
                    }
                }
                ArrayList<Range> ranges = DefaultServlet.FULL;
                long contentLength = -1L;
                if (resource.isDirectory()) {
                    if (!path.endsWith("/")) {
                        this.doDirectoryRedirect(request, response);
                        return;
                    }
                    if (!this.listings) {
                        response.sendError(404, DefaultServlet.sm.getString("defaultServlet.missingResource", new Object[] { request.getRequestURI() }));
                        return;
                    }
                    contentType = "text/html;charset=UTF-8";
                }
                else {
                    if (!isError) {
                        if (this.useAcceptRanges) {
                            response.setHeader("Accept-Ranges", "bytes");
                        }
                        ranges = this.parseRange(request, response, resource);
                        if (ranges == null) {
                            return;
                        }
                        response.setHeader("ETag", eTag);
                        response.setHeader("Last-Modified", lastModifiedHttp);
                    }
                    contentLength = resource.getContentLength();
                    if (contentLength == 0L) {
                        serveContent = false;
                    }
                }
                ServletOutputStream ostream = null;
                PrintWriter writer = null;
                if (serveContent) {
                    try {
                        ostream = response.getOutputStream();
                    }
                    catch (final IllegalStateException e) {
                        if (usingPrecompressedVersion || !isText(contentType)) {
                            throw e;
                        }
                        writer = response.getWriter();
                        ranges = DefaultServlet.FULL;
                    }
                }
                ServletResponse r = (ServletResponse)response;
                long contentWritten = 0L;
                while (r instanceof ServletResponseWrapper) {
                    r = ((ServletResponseWrapper)r).getResponse();
                }
                if (r instanceof ResponseFacade) {
                    contentWritten = ((ResponseFacade)r).getContentWritten();
                }
                if (contentWritten > 0L) {
                    ranges = DefaultServlet.FULL;
                }
                final String outputEncoding = response.getCharacterEncoding();
                final Charset charset = B2CConverter.getCharset(outputEncoding);
                final boolean outputEncodingSpecified = outputEncoding != Constants.DEFAULT_BODY_CHARSET.name() && outputEncoding != this.resources.getContext().getResponseCharacterEncoding();
                boolean conversionRequired;
                if (!usingPrecompressedVersion && isText(contentType) && outputEncodingSpecified && !charset.equals(this.fileEncodingCharset)) {
                    conversionRequired = true;
                    ranges = DefaultServlet.FULL;
                }
                else {
                    conversionRequired = false;
                }
                if (resource.isDirectory() || isError || ranges == DefaultServlet.FULL) {
                    if (contentType != null) {
                        if (this.debug > 0) {
                            this.log("DefaultServlet.serveFile:  contentType='" + contentType + "'");
                        }
                        if (response.getContentType() == null) {
                            response.setContentType(contentType);
                        }
                    }
                    if (resource.isFile() && contentLength >= 0L && (!serveContent || ostream != null)) {
                        if (this.debug > 0) {
                            this.log("DefaultServlet.serveFile:  contentLength=" + contentLength);
                        }
                        if (contentWritten == 0L && !conversionRequired) {
                            response.setContentLengthLong(contentLength);
                        }
                    }
                    if (serveContent) {
                        try {
                            response.setBufferSize(this.output);
                        }
                        catch (final IllegalStateException ex) {}
                        InputStream renderResult = null;
                        if (ostream == null) {
                            if (resource.isDirectory()) {
                                renderResult = this.render(request, this.getPathPrefix(request), resource, inputEncoding);
                            }
                            else {
                                renderResult = resource.getInputStream();
                                if (included) {
                                    if (!renderResult.markSupported()) {
                                        renderResult = new BufferedInputStream(renderResult);
                                    }
                                    final Charset bomCharset = processBom(renderResult, this.useBomIfPresent.stripBom);
                                    if (bomCharset != null && this.useBomIfPresent.useBomEncoding) {
                                        inputEncoding = bomCharset.name();
                                    }
                                }
                            }
                            this.copy(renderResult, writer, inputEncoding);
                        }
                        else {
                            if (resource.isDirectory()) {
                                renderResult = this.render(request, this.getPathPrefix(request), resource, inputEncoding);
                            }
                            else if (conversionRequired || included) {
                                InputStream source = resource.getInputStream();
                                if (!source.markSupported()) {
                                    source = new BufferedInputStream(source);
                                }
                                final Charset bomCharset2 = processBom(source, this.useBomIfPresent.stripBom);
                                if (bomCharset2 != null && this.useBomIfPresent.useBomEncoding) {
                                    inputEncoding = bomCharset2.name();
                                }
                                if (outputEncodingSpecified) {
                                    final OutputStreamWriter osw = new OutputStreamWriter((OutputStream)ostream, charset);
                                    final PrintWriter pw = new PrintWriter(osw);
                                    this.copy(source, pw, inputEncoding);
                                    pw.flush();
                                }
                                else {
                                    renderResult = source;
                                }
                            }
                            else if (!this.checkSendfile(request, response, resource, contentLength, null)) {
                                byte[] resourceBody = null;
                                if (resource instanceof CachedResource) {
                                    resourceBody = resource.getContent();
                                }
                                if (resourceBody == null) {
                                    renderResult = resource.getInputStream();
                                }
                                else {
                                    ostream.write(resourceBody);
                                }
                            }
                            if (renderResult != null) {
                                this.copy(renderResult, ostream);
                            }
                        }
                    }
                }
                else {
                    if (ranges == null || ranges.isEmpty()) {
                        return;
                    }
                    response.setStatus(206);
                    if (ranges.size() == 1) {
                        final Range range = ranges.get(0);
                        response.addHeader("Content-Range", "bytes " + range.start + "-" + range.end + "/" + range.length);
                        final long length = range.end - range.start + 1L;
                        response.setContentLengthLong(length);
                        if (contentType != null) {
                            if (this.debug > 0) {
                                this.log("DefaultServlet.serveFile:  contentType='" + contentType + "'");
                            }
                            response.setContentType(contentType);
                        }
                        if (serveContent) {
                            try {
                                response.setBufferSize(this.output);
                            }
                            catch (final IllegalStateException ex2) {}
                            if (ostream == null) {
                                throw new IllegalStateException();
                            }
                            if (!this.checkSendfile(request, response, resource, range.end - range.start + 1L, range)) {
                                this.copy(resource, ostream, range);
                            }
                        }
                    }
                    else {
                        response.setContentType("multipart/byteranges; boundary=CATALINA_MIME_BOUNDARY");
                        if (serveContent) {
                            try {
                                response.setBufferSize(this.output);
                            }
                            catch (final IllegalStateException ex3) {}
                            if (ostream == null) {
                                throw new IllegalStateException();
                            }
                            this.copy(resource, ostream, ranges.iterator(), contentType);
                        }
                    }
                }
                return;
            }
            String requestUri = (String)request.getAttribute("javax.servlet.include.request_uri");
            if (requestUri == null) {
                requestUri = request.getRequestURI();
                if (isError) {
                    response.sendError((int)request.getAttribute("javax.servlet.error.status_code"));
                }
                else {
                    response.sendError(403, requestUri);
                }
                return;
            }
            throw new FileNotFoundException(DefaultServlet.sm.getString("defaultServlet.missingResource", new Object[] { requestUri }));
        }
    }
    
    private static Charset processBom(final InputStream is, final boolean stripBom) throws IOException {
        final byte[] bom = new byte[4];
        is.mark(bom.length);
        final int count = is.read(bom);
        if (count < 2) {
            skip(is, 0, stripBom);
            return null;
        }
        final int b0 = bom[0] & 0xFF;
        final int b2 = bom[1] & 0xFF;
        if (b0 == 254 && b2 == 255) {
            skip(is, 2, stripBom);
            return StandardCharsets.UTF_16BE;
        }
        if (count == 2 && b0 == 255 && b2 == 254) {
            skip(is, 2, stripBom);
            return StandardCharsets.UTF_16LE;
        }
        if (count < 3) {
            skip(is, 0, stripBom);
            return null;
        }
        final int b3 = bom[2] & 0xFF;
        if (b0 == 239 && b2 == 187 && b3 == 191) {
            skip(is, 3, stripBom);
            return StandardCharsets.UTF_8;
        }
        if (count < 4) {
            skip(is, 0, stripBom);
            return null;
        }
        final int b4 = bom[3] & 0xFF;
        if (b0 == 0 && b2 == 0 && b3 == 254 && b4 == 255) {
            return Charset.forName("UTF-32BE");
        }
        if (b0 == 255 && b2 == 254 && b3 == 0 && b4 == 0) {
            return Charset.forName("UTF-32LE");
        }
        if (b0 == 255 && b2 == 254) {
            skip(is, 2, stripBom);
            return StandardCharsets.UTF_16LE;
        }
        skip(is, 0, stripBom);
        return null;
    }
    
    private static void skip(final InputStream is, int skip, final boolean stripBom) throws IOException {
        is.reset();
        if (stripBom) {
            while (skip-- > 0) {
                is.read();
            }
        }
    }
    
    private static boolean isText(final String contentType) {
        return contentType == null || contentType.startsWith("text") || contentType.endsWith("xml") || contentType.contains("/javascript");
    }
    
    private boolean pathEndsWithCompressedExtension(final String path) {
        for (final CompressionFormat format : this.compressionFormats) {
            if (path.endsWith(format.extension)) {
                return true;
            }
        }
        return false;
    }
    
    private List<PrecompressedResource> getAvailablePrecompressedResources(final String path) {
        final List<PrecompressedResource> ret = new ArrayList<PrecompressedResource>(this.compressionFormats.length);
        for (final CompressionFormat format : this.compressionFormats) {
            final WebResource precompressedResource = this.resources.getResource(path + format.extension);
            if (precompressedResource.exists() && precompressedResource.isFile()) {
                ret.add(new PrecompressedResource(precompressedResource, format));
            }
        }
        return ret;
    }
    
    private PrecompressedResource getBestPrecompressedResource(final HttpServletRequest request, final List<PrecompressedResource> precompressedResources) {
        final Enumeration<String> headers = request.getHeaders("Accept-Encoding");
        PrecompressedResource bestResource = null;
        double bestResourceQuality = 0.0;
        int bestResourcePreference = Integer.MAX_VALUE;
        while (headers.hasMoreElements()) {
            final String header = headers.nextElement();
            for (final String preference : header.split(",")) {
                double quality = 1.0;
                final int qualityIdx = preference.indexOf(59);
                Label_0300: {
                    if (qualityIdx > 0) {
                        final int equalsIdx = preference.indexOf(61, qualityIdx + 1);
                        if (equalsIdx == -1) {
                            break Label_0300;
                        }
                        quality = Double.parseDouble(preference.substring(equalsIdx + 1).trim());
                    }
                    if (quality >= bestResourceQuality) {
                        String encoding = preference;
                        if (qualityIdx > 0) {
                            encoding = encoding.substring(0, qualityIdx);
                        }
                        encoding = encoding.trim();
                        if ("identity".equals(encoding)) {
                            bestResource = null;
                            bestResourceQuality = quality;
                            bestResourcePreference = Integer.MAX_VALUE;
                        }
                        else if ("*".equals(encoding)) {
                            bestResource = precompressedResources.get(0);
                            bestResourceQuality = quality;
                            bestResourcePreference = 0;
                        }
                        else {
                            int i = 0;
                            while (i < precompressedResources.size()) {
                                final PrecompressedResource resource = precompressedResources.get(i);
                                if (encoding.equals(resource.format.encoding)) {
                                    if (quality > bestResourceQuality || i < bestResourcePreference) {
                                        bestResource = resource;
                                        bestResourceQuality = quality;
                                        bestResourcePreference = i;
                                        break;
                                    }
                                    break;
                                }
                                else {
                                    ++i;
                                }
                            }
                        }
                    }
                }
            }
        }
        return bestResource;
    }
    
    private void doDirectoryRedirect(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final StringBuilder location = new StringBuilder(request.getRequestURI());
        location.append('/');
        if (request.getQueryString() != null) {
            location.append('?');
            location.append(request.getQueryString());
        }
        while (location.length() > 1 && location.charAt(1) == '/') {
            location.deleteCharAt(0);
        }
        response.sendRedirect(response.encodeRedirectURL(location.toString()));
    }
    
    protected Range parseContentRange(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String contentRangeHeader = request.getHeader("Content-Range");
        if (contentRangeHeader == null) {
            return DefaultServlet.IGNORE;
        }
        if (!this.allowPartialPut) {
            response.sendError(400);
            return null;
        }
        final ContentRange contentRange = ContentRange.parse(new StringReader(contentRangeHeader));
        if (contentRange == null) {
            response.sendError(400);
            return null;
        }
        if (!contentRange.getUnits().equals("bytes")) {
            response.sendError(400);
            return null;
        }
        final Range range = new Range();
        range.start = contentRange.getStart();
        range.end = contentRange.getEnd();
        range.length = contentRange.getLength();
        if (!range.validate()) {
            response.sendError(400);
            return null;
        }
        return range;
    }
    
    protected ArrayList<Range> parseRange(final HttpServletRequest request, final HttpServletResponse response, final WebResource resource) throws IOException {
        final String headerValue = request.getHeader("If-Range");
        if (headerValue != null) {
            long headerValueTime = -1L;
            try {
                headerValueTime = request.getDateHeader("If-Range");
            }
            catch (final IllegalArgumentException ex) {}
            final String eTag = this.generateETag(resource);
            final long lastModified = resource.getLastModified();
            if (headerValueTime == -1L) {
                if (!eTag.equals(headerValue.trim())) {
                    return DefaultServlet.FULL;
                }
            }
            else if (Math.abs(lastModified - headerValueTime) > 1000L) {
                return DefaultServlet.FULL;
            }
        }
        final long fileLength = resource.getContentLength();
        if (fileLength == 0L) {
            return DefaultServlet.FULL;
        }
        final String rangeHeader = request.getHeader("Range");
        if (rangeHeader == null) {
            return DefaultServlet.FULL;
        }
        final Ranges ranges = Ranges.parse(new StringReader(rangeHeader));
        if (ranges == null) {
            response.addHeader("Content-Range", "bytes */" + fileLength);
            response.sendError(416);
            return null;
        }
        if (!ranges.getUnits().equals("bytes")) {
            return DefaultServlet.FULL;
        }
        final ArrayList<Range> result = new ArrayList<Range>();
        for (final Ranges.Entry entry : ranges.getEntries()) {
            final Range currentRange = new Range();
            if (entry.getStart() == -1L) {
                currentRange.start = fileLength - entry.getEnd();
                if (currentRange.start < 0L) {
                    currentRange.start = 0L;
                }
                currentRange.end = fileLength - 1L;
            }
            else if (entry.getEnd() == -1L) {
                currentRange.start = entry.getStart();
                currentRange.end = fileLength - 1L;
            }
            else {
                currentRange.start = entry.getStart();
                currentRange.end = entry.getEnd();
            }
            currentRange.length = fileLength;
            if (!currentRange.validate()) {
                response.addHeader("Content-Range", "bytes */" + fileLength);
                response.sendError(416);
                return null;
            }
            result.add(currentRange);
        }
        return result;
    }
    
    @Deprecated
    protected InputStream render(final String contextPath, final WebResource resource) throws IOException, ServletException {
        return this.render(contextPath, resource, null);
    }
    
    @Deprecated
    protected InputStream render(final String contextPath, final WebResource resource, final String encoding) throws IOException, ServletException {
        return this.render(null, contextPath, resource, encoding);
    }
    
    protected InputStream render(final HttpServletRequest request, final String contextPath, final WebResource resource, final String encoding) throws IOException, ServletException {
        final Source xsltSource = this.findXsltSource(resource);
        if (xsltSource == null) {
            return this.renderHtml(request, contextPath, resource, encoding);
        }
        return this.renderXml(request, contextPath, resource, xsltSource, encoding);
    }
    
    @Deprecated
    protected InputStream renderXml(final String contextPath, final WebResource resource, final Source xsltSource) throws IOException, ServletException {
        return this.renderXml(contextPath, resource, xsltSource, null);
    }
    
    @Deprecated
    protected InputStream renderXml(final String contextPath, final WebResource resource, final Source xsltSource, final String encoding) throws ServletException, IOException {
        return this.renderXml(null, contextPath, resource, xsltSource, encoding);
    }
    
    protected InputStream renderXml(final HttpServletRequest request, final String contextPath, final WebResource resource, final Source xsltSource, final String encoding) throws IOException, ServletException {
        final StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<listing ");
        sb.append(" contextPath='");
        sb.append(contextPath);
        sb.append('\'');
        sb.append(" directory='");
        sb.append(resource.getName());
        sb.append("' ");
        sb.append(" hasParent='").append(!resource.getName().equals("/"));
        sb.append("'>");
        sb.append("<entries>");
        final String[] entries = this.resources.list(resource.getWebappPath());
        final String rewrittenContextPath = this.rewriteUrl(contextPath);
        final String directoryWebappPath = resource.getWebappPath();
        for (final String entry : entries) {
            if (!entry.equalsIgnoreCase("WEB-INF") && !entry.equalsIgnoreCase("META-INF")) {
                if (!entry.equalsIgnoreCase(this.localXsltFile)) {
                    if (!(directoryWebappPath + entry).equals(this.contextXsltFile)) {
                        final WebResource childResource = this.resources.getResource(directoryWebappPath + entry);
                        if (childResource.exists()) {
                            sb.append("<entry");
                            sb.append(" type='").append(childResource.isDirectory() ? "dir" : "file").append('\'');
                            sb.append(" urlPath='").append(rewrittenContextPath).append(this.rewriteUrl(directoryWebappPath + entry)).append(childResource.isDirectory() ? "/" : "").append('\'');
                            if (childResource.isFile()) {
                                sb.append(" size='").append(this.renderSize(childResource.getContentLength())).append('\'');
                            }
                            sb.append(" date='").append(childResource.getLastModifiedHttp()).append('\'');
                            sb.append('>');
                            sb.append(Escape.htmlElementContent(entry));
                            if (childResource.isDirectory()) {
                                sb.append('/');
                            }
                            sb.append("</entry>");
                        }
                    }
                }
            }
        }
        sb.append("</entries>");
        final String readme = this.getReadme(resource, encoding);
        if (readme != null) {
            sb.append("<readme><![CDATA[");
            sb.append(readme);
            sb.append("]]></readme>");
        }
        sb.append("</listing>");
        ClassLoader original;
        if (Globals.IS_SECURITY_ENABLED) {
            final PrivilegedGetTccl pa = new PrivilegedGetTccl();
            original = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)pa);
        }
        else {
            original = Thread.currentThread().getContextClassLoader();
        }
        try {
            if (Globals.IS_SECURITY_ENABLED) {
                final PrivilegedSetTccl pa2 = new PrivilegedSetTccl(DefaultServlet.class.getClassLoader());
                AccessController.doPrivileged((PrivilegedAction<Object>)pa2);
            }
            else {
                Thread.currentThread().setContextClassLoader(DefaultServlet.class.getClassLoader());
            }
            final TransformerFactory tFactory = TransformerFactory.newInstance();
            final Source xmlSource = new StreamSource(new StringReader(sb.toString()));
            final Transformer transformer = tFactory.newTransformer(xsltSource);
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final OutputStreamWriter osWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
            final StreamResult out = new StreamResult(osWriter);
            transformer.transform(xmlSource, out);
            osWriter.flush();
            return new ByteArrayInputStream(stream.toByteArray());
        }
        catch (final TransformerException e) {
            throw new ServletException(DefaultServlet.sm.getString("defaultServlet.xslError"), (Throwable)e);
        }
        finally {
            if (Globals.IS_SECURITY_ENABLED) {
                final PrivilegedSetTccl pa3 = new PrivilegedSetTccl(original);
                AccessController.doPrivileged((PrivilegedAction<Object>)pa3);
            }
            else {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
    }
    
    @Deprecated
    protected InputStream renderHtml(final String contextPath, final WebResource resource) throws IOException {
        return this.renderHtml(contextPath, resource, null);
    }
    
    @Deprecated
    protected InputStream renderHtml(final String contextPath, final WebResource resource, final String encoding) throws IOException {
        return this.renderHtml(null, contextPath, resource, encoding);
    }
    
    protected InputStream renderHtml(final HttpServletRequest request, final String contextPath, final WebResource resource, final String encoding) throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final OutputStreamWriter osWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        final PrintWriter writer = new PrintWriter(osWriter);
        final StringBuilder sb = new StringBuilder();
        final String directoryWebappPath = resource.getWebappPath();
        final WebResource[] entries = this.resources.listResources(directoryWebappPath);
        final String rewrittenContextPath = this.rewriteUrl(contextPath);
        sb.append("<!doctype html><html>\r\n");
        sb.append("<head>\r\n");
        sb.append("<title>");
        sb.append(DefaultServlet.sm.getString("directory.title", new Object[] { directoryWebappPath }));
        sb.append("</title>\r\n");
        sb.append("<style>");
        sb.append("body {font-family:Tahoma,Arial,sans-serif;} h1, h2, h3, b {color:white;background-color:#525D76;} h1 {font-size:22px;} h2 {font-size:16px;} h3 {font-size:14px;} p {font-size:12px;} a {color:black;} .line {height:1px;background-color:#525D76;border:none;}");
        sb.append("</style> ");
        sb.append("</head>\r\n");
        sb.append("<body>");
        sb.append("<h1>");
        sb.append(DefaultServlet.sm.getString("directory.title", new Object[] { directoryWebappPath }));
        String parentDirectory = directoryWebappPath;
        if (parentDirectory.endsWith("/")) {
            parentDirectory = parentDirectory.substring(0, parentDirectory.length() - 1);
        }
        final int slash = parentDirectory.lastIndexOf(47);
        if (slash >= 0) {
            String parent = directoryWebappPath.substring(0, slash);
            sb.append(" - <a href=\"");
            sb.append(rewrittenContextPath);
            if (parent.equals("")) {
                parent = "/";
            }
            sb.append(this.rewriteUrl(parent));
            if (!parent.endsWith("/")) {
                sb.append('/');
            }
            sb.append("\">");
            sb.append("<b>");
            sb.append(DefaultServlet.sm.getString("directory.parent", new Object[] { parent }));
            sb.append("</b>");
            sb.append("</a>");
        }
        sb.append("</h1>");
        sb.append("<hr class=\"line\">");
        sb.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\">\r\n");
        SortManager.Order order;
        if (this.sortListings && null != request) {
            order = this.sortManager.getOrder(request.getQueryString());
        }
        else {
            order = null;
        }
        sb.append("<tr>\r\n");
        sb.append("<td align=\"left\"><font size=\"+1\"><strong>");
        if (this.sortListings && null != request) {
            sb.append("<a href=\"?C=N;O=");
            sb.append(this.getOrderChar(order, 'N'));
            sb.append("\">");
            sb.append(DefaultServlet.sm.getString("directory.filename"));
            sb.append("</a>");
        }
        else {
            sb.append(DefaultServlet.sm.getString("directory.filename"));
        }
        sb.append("</strong></font></td>\r\n");
        sb.append("<td align=\"center\"><font size=\"+1\"><strong>");
        if (this.sortListings && null != request) {
            sb.append("<a href=\"?C=S;O=");
            sb.append(this.getOrderChar(order, 'S'));
            sb.append("\">");
            sb.append(DefaultServlet.sm.getString("directory.size"));
            sb.append("</a>");
        }
        else {
            sb.append(DefaultServlet.sm.getString("directory.size"));
        }
        sb.append("</strong></font></td>\r\n");
        sb.append("<td align=\"right\"><font size=\"+1\"><strong>");
        if (this.sortListings && null != request) {
            sb.append("<a href=\"?C=M;O=");
            sb.append(this.getOrderChar(order, 'M'));
            sb.append("\">");
            sb.append(DefaultServlet.sm.getString("directory.lastModified"));
            sb.append("</a>");
        }
        else {
            sb.append(DefaultServlet.sm.getString("directory.lastModified"));
        }
        sb.append("</strong></font></td>\r\n");
        sb.append("</tr>");
        if (null != this.sortManager && null != request) {
            this.sortManager.sort(entries, request.getQueryString());
        }
        boolean shade = false;
        for (final WebResource childResource : entries) {
            final String filename = childResource.getName();
            if (!filename.equalsIgnoreCase("WEB-INF")) {
                if (!filename.equalsIgnoreCase("META-INF")) {
                    if (childResource.exists()) {
                        sb.append("<tr");
                        if (shade) {
                            sb.append(" bgcolor=\"#eeeeee\"");
                        }
                        sb.append(">\r\n");
                        shade = !shade;
                        sb.append("<td align=\"left\">&nbsp;&nbsp;\r\n");
                        sb.append("<a href=\"");
                        sb.append(rewrittenContextPath);
                        sb.append(this.rewriteUrl(childResource.getWebappPath()));
                        if (childResource.isDirectory()) {
                            sb.append('/');
                        }
                        sb.append("\"><tt>");
                        sb.append(Escape.htmlElementContent(filename));
                        if (childResource.isDirectory()) {
                            sb.append('/');
                        }
                        sb.append("</tt></a></td>\r\n");
                        sb.append("<td align=\"right\"><tt>");
                        if (childResource.isDirectory()) {
                            sb.append("&nbsp;");
                        }
                        else {
                            sb.append(this.renderSize(childResource.getContentLength()));
                        }
                        sb.append("</tt></td>\r\n");
                        sb.append("<td align=\"right\"><tt>");
                        sb.append(childResource.getLastModifiedHttp());
                        sb.append("</tt></td>\r\n");
                        sb.append("</tr>\r\n");
                    }
                }
            }
        }
        sb.append("</table>\r\n");
        sb.append("<hr class=\"line\">");
        final String readme = this.getReadme(resource, encoding);
        if (readme != null) {
            sb.append(readme);
            sb.append("<hr class=\"line\">");
        }
        if (this.showServerInfo) {
            sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        }
        sb.append("</body>\r\n");
        sb.append("</html>\r\n");
        writer.write(sb.toString());
        writer.flush();
        return new ByteArrayInputStream(stream.toByteArray());
    }
    
    protected String renderSize(final long size) {
        final long leftSide = size / 1024L;
        long rightSide = size % 1024L / 103L;
        if (leftSide == 0L && rightSide == 0L && size > 0L) {
            rightSide = 1L;
        }
        return "" + leftSide + "." + rightSide + " kb";
    }
    
    @Deprecated
    protected String getReadme(final WebResource directory) {
        return this.getReadme(directory, null);
    }
    
    protected String getReadme(final WebResource directory, final String encoding) {
        if (this.readmeFile == null) {
            return null;
        }
        final WebResource resource = this.resources.getResource(directory.getWebappPath() + this.readmeFile);
        if (resource.isFile()) {
            final StringWriter buffer = new StringWriter();
            InputStreamReader reader = null;
            try (final InputStream is = resource.getInputStream()) {
                if (encoding != null) {
                    reader = new InputStreamReader(is, encoding);
                }
                else {
                    reader = new InputStreamReader(is);
                }
                this.copyRange(reader, new PrintWriter(buffer));
            }
            catch (final IOException e) {
                this.log(DefaultServlet.sm.getString("defaultServlet.readerCloseFailed"), (Throwable)e);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException ex) {}
                }
            }
            return buffer.toString();
        }
        if (this.debug > 10) {
            this.log("readme '" + this.readmeFile + "' not found");
        }
        return null;
    }
    
    protected Source findXsltSource(final WebResource directory) throws IOException {
        if (this.localXsltFile != null) {
            final WebResource resource = this.resources.getResource(directory.getWebappPath() + this.localXsltFile);
            if (resource.isFile()) {
                final InputStream is = resource.getInputStream();
                if (is != null) {
                    if (Globals.IS_SECURITY_ENABLED) {
                        return this.secureXslt(is);
                    }
                    return new StreamSource(is);
                }
            }
            if (this.debug > 10) {
                this.log("localXsltFile '" + this.localXsltFile + "' not found");
            }
        }
        if (this.contextXsltFile != null) {
            final InputStream is2 = this.getServletContext().getResourceAsStream(this.contextXsltFile);
            if (is2 != null) {
                if (Globals.IS_SECURITY_ENABLED) {
                    return this.secureXslt(is2);
                }
                return new StreamSource(is2);
            }
            else if (this.debug > 10) {
                this.log("contextXsltFile '" + this.contextXsltFile + "' not found");
            }
        }
        if (this.globalXsltFile != null) {
            final File f = this.validateGlobalXsltFile();
            if (f != null) {
                final long globalXsltFileSize = f.length();
                if (globalXsltFileSize > 2147483647L) {
                    this.log("globalXsltFile [" + f.getAbsolutePath() + "] is too big to buffer");
                }
                else {
                    try (final FileInputStream fis = new FileInputStream(f)) {
                        final byte[] b = new byte[(int)f.length()];
                        IOTools.readFully(fis, b);
                        return new StreamSource(new ByteArrayInputStream(b));
                    }
                }
            }
        }
        return null;
    }
    
    private File validateGlobalXsltFile() {
        final Context context = this.resources.getContext();
        final File baseConf = new File(context.getCatalinaBase(), "conf");
        File result = this.validateGlobalXsltFile(baseConf);
        if (result == null) {
            final File homeConf = new File(context.getCatalinaHome(), "conf");
            if (!baseConf.equals(homeConf)) {
                result = this.validateGlobalXsltFile(homeConf);
            }
        }
        return result;
    }
    
    private File validateGlobalXsltFile(final File base) {
        File candidate = new File(this.globalXsltFile);
        if (!candidate.isAbsolute()) {
            candidate = new File(base, this.globalXsltFile);
        }
        if (!candidate.isFile()) {
            return null;
        }
        try {
            if (!candidate.getCanonicalFile().toPath().startsWith(base.getCanonicalFile().toPath())) {
                return null;
            }
        }
        catch (final IOException ioe) {
            return null;
        }
        final String nameLower = candidate.getName().toLowerCase(Locale.ENGLISH);
        if (!nameLower.endsWith(".xslt") && !nameLower.endsWith(".xsl")) {
            return null;
        }
        return candidate;
    }
    
    private Source secureXslt(final InputStream is) {
        Source result = null;
        try {
            final DocumentBuilder builder = DefaultServlet.factory.newDocumentBuilder();
            builder.setEntityResolver(DefaultServlet.secureEntityResolver);
            final Document document = builder.parse(is);
            result = new DOMSource(document);
        }
        catch (final ParserConfigurationException | SAXException | IOException e) {
            if (this.debug > 0) {
                this.log(e.getMessage(), (Throwable)e);
            }
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final IOException ex) {}
            }
        }
        return result;
    }
    
    protected boolean checkSendfile(final HttpServletRequest request, final HttpServletResponse response, final WebResource resource, final long length, final Range range) {
        final String canonicalPath;
        if (this.sendfileSize > 0 && length > this.sendfileSize && Boolean.TRUE.equals(request.getAttribute("org.apache.tomcat.sendfile.support")) && request.getClass().getName().equals("org.apache.catalina.connector.RequestFacade") && response.getClass().getName().equals("org.apache.catalina.connector.ResponseFacade") && resource.isFile() && (canonicalPath = resource.getCanonicalPath()) != null) {
            request.setAttribute("org.apache.tomcat.sendfile.filename", (Object)canonicalPath);
            if (range == null) {
                request.setAttribute("org.apache.tomcat.sendfile.start", (Object)0L);
                request.setAttribute("org.apache.tomcat.sendfile.end", (Object)length);
            }
            else {
                request.setAttribute("org.apache.tomcat.sendfile.start", (Object)range.start);
                request.setAttribute("org.apache.tomcat.sendfile.end", (Object)(range.end + 1L));
            }
            return true;
        }
        return false;
    }
    
    protected boolean checkIfMatch(final HttpServletRequest request, final HttpServletResponse response, final WebResource resource) throws IOException {
        final String headerValue = request.getHeader("If-Match");
        if (headerValue != null) {
            boolean conditionSatisfied;
            if (!headerValue.equals("*")) {
                final String resourceETag = this.generateETag(resource);
                if (resourceETag == null) {
                    conditionSatisfied = false;
                }
                else {
                    final Boolean matched = EntityTag.compareEntityTag(new StringReader(headerValue), false, resourceETag);
                    if (matched == null) {
                        if (this.debug > 10) {
                            this.log("DefaultServlet.checkIfMatch:  Invalid header value [" + headerValue + "]");
                        }
                        response.sendError(400);
                        return false;
                    }
                    conditionSatisfied = matched;
                }
            }
            else {
                conditionSatisfied = true;
            }
            if (!conditionSatisfied) {
                response.sendError(412);
                return false;
            }
        }
        return true;
    }
    
    protected boolean checkIfModifiedSince(final HttpServletRequest request, final HttpServletResponse response, final WebResource resource) {
        try {
            final long headerValue = request.getDateHeader("If-Modified-Since");
            final long lastModified = resource.getLastModified();
            if (headerValue != -1L && request.getHeader("If-None-Match") == null && lastModified < headerValue + 1000L) {
                response.setStatus(304);
                response.setHeader("ETag", this.generateETag(resource));
                return false;
            }
        }
        catch (final IllegalArgumentException illegalArgument) {
            return true;
        }
        return true;
    }
    
    protected boolean checkIfNoneMatch(final HttpServletRequest request, final HttpServletResponse response, final WebResource resource) throws IOException {
        final String headerValue = request.getHeader("If-None-Match");
        if (headerValue != null) {
            final String resourceETag = this.generateETag(resource);
            boolean conditionSatisfied;
            if (!headerValue.equals("*")) {
                if (resourceETag == null) {
                    conditionSatisfied = false;
                }
                else {
                    final Boolean matched = EntityTag.compareEntityTag(new StringReader(headerValue), true, resourceETag);
                    if (matched == null) {
                        if (this.debug > 10) {
                            this.log("DefaultServlet.checkIfNoneMatch:  Invalid header value [" + headerValue + "]");
                        }
                        response.sendError(400);
                        return false;
                    }
                    conditionSatisfied = matched;
                }
            }
            else {
                conditionSatisfied = true;
            }
            if (conditionSatisfied) {
                if ("GET".equals(request.getMethod()) || "HEAD".equals(request.getMethod())) {
                    response.setStatus(304);
                    response.setHeader("ETag", resourceETag);
                }
                else {
                    response.sendError(412);
                }
                return false;
            }
        }
        return true;
    }
    
    protected boolean checkIfUnmodifiedSince(final HttpServletRequest request, final HttpServletResponse response, final WebResource resource) throws IOException {
        try {
            final long lastModified = resource.getLastModified();
            final long headerValue = request.getDateHeader("If-Unmodified-Since");
            if (headerValue != -1L && lastModified >= headerValue + 1000L) {
                response.sendError(412);
                return false;
            }
        }
        catch (final IllegalArgumentException illegalArgument) {
            return true;
        }
        return true;
    }
    
    protected String generateETag(final WebResource resource) {
        return resource.getETag();
    }
    
    @Deprecated
    protected void copy(final WebResource resource, final InputStream is, final ServletOutputStream ostream) throws IOException {
        this.copy(is, ostream);
    }
    
    protected void copy(final InputStream is, final ServletOutputStream ostream) throws IOException {
        IOException exception = null;
        final InputStream istream = new BufferedInputStream(is, this.input);
        exception = this.copyRange(istream, ostream);
        istream.close();
        if (exception != null) {
            throw exception;
        }
    }
    
    @Deprecated
    protected void copy(final WebResource resource, final InputStream is, final PrintWriter writer, final String encoding) throws IOException {
        this.copy(is, writer, encoding);
    }
    
    protected void copy(final InputStream is, final PrintWriter writer, final String encoding) throws IOException {
        IOException exception = null;
        Reader reader;
        if (encoding == null) {
            reader = new InputStreamReader(is);
        }
        else {
            reader = new InputStreamReader(is, encoding);
        }
        exception = this.copyRange(reader, writer);
        reader.close();
        if (exception != null) {
            throw exception;
        }
    }
    
    protected void copy(final WebResource resource, final ServletOutputStream ostream, final Range range) throws IOException {
        IOException exception = null;
        final InputStream resourceInputStream = resource.getInputStream();
        final InputStream istream = new BufferedInputStream(resourceInputStream, this.input);
        exception = this.copyRange(istream, ostream, range.start, range.end);
        istream.close();
        if (exception != null) {
            throw exception;
        }
    }
    
    protected void copy(final WebResource resource, final ServletOutputStream ostream, final Iterator<Range> ranges, final String contentType) throws IOException {
        IOException exception = null;
        while (exception == null && ranges.hasNext()) {
            final InputStream resourceInputStream = resource.getInputStream();
            try (final InputStream istream = new BufferedInputStream(resourceInputStream, this.input)) {
                final Range currentRange = ranges.next();
                ostream.println();
                ostream.println("--CATALINA_MIME_BOUNDARY");
                if (contentType != null) {
                    ostream.println("Content-Type: " + contentType);
                }
                ostream.println("Content-Range: bytes " + currentRange.start + "-" + currentRange.end + "/" + currentRange.length);
                ostream.println();
                exception = this.copyRange(istream, ostream, currentRange.start, currentRange.end);
            }
        }
        ostream.println();
        ostream.print("--CATALINA_MIME_BOUNDARY--");
        if (exception != null) {
            throw exception;
        }
    }
    
    protected IOException copyRange(final InputStream istream, final ServletOutputStream ostream) {
        IOException exception = null;
        final byte[] buffer = new byte[this.input];
        int len = buffer.length;
        try {
            while (true) {
                len = istream.read(buffer);
                if (len == -1) {
                    break;
                }
                ostream.write(buffer, 0, len);
            }
        }
        catch (final IOException e) {
            exception = e;
            len = -1;
        }
        return exception;
    }
    
    protected IOException copyRange(final Reader reader, final PrintWriter writer) {
        IOException exception = null;
        final char[] buffer = new char[this.input];
        int len = buffer.length;
        try {
            while (true) {
                len = reader.read(buffer);
                if (len == -1) {
                    break;
                }
                writer.write(buffer, 0, len);
            }
        }
        catch (final IOException e) {
            exception = e;
            len = -1;
        }
        return exception;
    }
    
    protected IOException copyRange(final InputStream istream, final ServletOutputStream ostream, final long start, final long end) {
        if (this.debug > 10) {
            this.log("Serving bytes:" + start + "-" + end);
        }
        long skipped = 0L;
        try {
            skipped = istream.skip(start);
        }
        catch (final IOException e) {
            return e;
        }
        if (skipped < start) {
            return new IOException(DefaultServlet.sm.getString("defaultServlet.skipfail", new Object[] { skipped, start }));
        }
        IOException exception = null;
        long bytesToRead = end - start + 1L;
        final byte[] buffer = new byte[this.input];
        int len = buffer.length;
        while (bytesToRead > 0L && len >= buffer.length) {
            try {
                len = istream.read(buffer);
                if (bytesToRead >= len) {
                    ostream.write(buffer, 0, len);
                    bytesToRead -= len;
                }
                else {
                    ostream.write(buffer, 0, (int)bytesToRead);
                    bytesToRead = 0L;
                }
            }
            catch (final IOException e2) {
                exception = e2;
                len = -1;
            }
            if (len < buffer.length) {
                break;
            }
        }
        return exception;
    }
    
    private char getOrderChar(final SortManager.Order order, final char column) {
        if (column != order.column) {
            return 'D';
        }
        if (order.ascending) {
            return 'D';
        }
        return 'A';
    }
    
    static {
        sm = StringManager.getManager("org.apache.catalina.servlets");
        FULL = new ArrayList<Range>();
        IGNORE = new Range();
        if (Globals.IS_SECURITY_ENABLED) {
            (factory = DocumentBuilderFactory.newInstance()).setNamespaceAware(true);
            DefaultServlet.factory.setValidating(false);
            secureEntityResolver = new SecureEntityResolver();
        }
        else {
            factory = null;
            secureEntityResolver = null;
        }
    }
    
    protected static class Range
    {
        public long start;
        public long end;
        public long length;
        
        public boolean validate() {
            if (this.end >= this.length) {
                this.end = this.length - 1L;
            }
            return this.start >= 0L && this.end >= 0L && this.start <= this.end && this.length > 0L;
        }
    }
    
    protected static class CompressionFormat implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public final String extension;
        public final String encoding;
        
        public CompressionFormat(final String extension, final String encoding) {
            this.extension = extension;
            this.encoding = encoding;
        }
    }
    
    private static class PrecompressedResource
    {
        public final WebResource resource;
        public final CompressionFormat format;
        
        private PrecompressedResource(final WebResource resource, final CompressionFormat format) {
            this.resource = resource;
            this.format = format;
        }
    }
    
    private static class SecureEntityResolver implements EntityResolver2
    {
        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
            throw new SAXException(DefaultServlet.sm.getString("defaultServlet.blockExternalEntity", new Object[] { publicId, systemId }));
        }
        
        @Override
        public InputSource getExternalSubset(final String name, final String baseURI) throws SAXException, IOException {
            throw new SAXException(DefaultServlet.sm.getString("defaultServlet.blockExternalSubset", new Object[] { name, baseURI }));
        }
        
        @Override
        public InputSource resolveEntity(final String name, final String publicId, final String baseURI, final String systemId) throws SAXException, IOException {
            throw new SAXException(DefaultServlet.sm.getString("defaultServlet.blockExternalEntity2", new Object[] { name, publicId, baseURI, systemId }));
        }
    }
    
    private static class SortManager
    {
        protected Comparator<WebResource> defaultResourceComparator;
        protected Comparator<WebResource> resourceNameComparator;
        protected Comparator<WebResource> resourceNameComparatorAsc;
        protected Comparator<WebResource> resourceSizeComparator;
        protected Comparator<WebResource> resourceSizeComparatorAsc;
        protected Comparator<WebResource> resourceLastModifiedComparator;
        protected Comparator<WebResource> resourceLastModifiedComparatorAsc;
        
        public SortManager(final boolean directoriesFirst) {
            this.resourceNameComparator = new ResourceNameComparator();
            this.resourceNameComparatorAsc = Collections.reverseOrder(this.resourceNameComparator);
            this.resourceSizeComparator = new ResourceSizeComparator(this.resourceNameComparator);
            this.resourceSizeComparatorAsc = Collections.reverseOrder(this.resourceSizeComparator);
            this.resourceLastModifiedComparator = new ResourceLastModifiedDateComparator(this.resourceNameComparator);
            this.resourceLastModifiedComparatorAsc = Collections.reverseOrder(this.resourceLastModifiedComparator);
            if (directoriesFirst) {
                this.resourceNameComparator = new DirsFirstComparator(this.resourceNameComparator);
                this.resourceNameComparatorAsc = new DirsFirstComparator(this.resourceNameComparatorAsc);
                this.resourceSizeComparator = new DirsFirstComparator(this.resourceSizeComparator);
                this.resourceSizeComparatorAsc = new DirsFirstComparator(this.resourceSizeComparatorAsc);
                this.resourceLastModifiedComparator = new DirsFirstComparator(this.resourceLastModifiedComparator);
                this.resourceLastModifiedComparatorAsc = new DirsFirstComparator(this.resourceLastModifiedComparatorAsc);
            }
            this.defaultResourceComparator = this.resourceNameComparator;
        }
        
        public void sort(final WebResource[] resources, final String order) {
            final Comparator<WebResource> comparator = this.getComparator(order);
            if (null != comparator) {
                Arrays.sort(resources, comparator);
            }
        }
        
        public Comparator<WebResource> getComparator(final String order) {
            return this.getComparator(this.getOrder(order));
        }
        
        public Comparator<WebResource> getComparator(final Order order) {
            if (null == order) {
                return this.defaultResourceComparator;
            }
            if ('N' == order.column) {
                if (order.ascending) {
                    return this.resourceNameComparatorAsc;
                }
                return this.resourceNameComparator;
            }
            else if ('S' == order.column) {
                if (order.ascending) {
                    return this.resourceSizeComparatorAsc;
                }
                return this.resourceSizeComparator;
            }
            else {
                if ('M' != order.column) {
                    return this.defaultResourceComparator;
                }
                if (order.ascending) {
                    return this.resourceLastModifiedComparatorAsc;
                }
                return this.resourceLastModifiedComparator;
            }
        }
        
        public Order getOrder(final String order) {
            if (null == order || 0 == order.trim().length()) {
                return Order.DEFAULT;
            }
            final String[] options = order.split(";");
            if (0 == options.length) {
                return Order.DEFAULT;
            }
            char column = '\0';
            boolean ascending = false;
            for (String option : options) {
                option = option.trim();
                if (2 < option.length()) {
                    final char opt = option.charAt(0);
                    if ('C' == opt) {
                        column = option.charAt(2);
                    }
                    else if ('O' == opt) {
                        ascending = ('A' == option.charAt(2));
                    }
                }
            }
            if ('N' == column) {
                if (ascending) {
                    return Order.NAME_ASC;
                }
                return Order.NAME;
            }
            else if ('S' == column) {
                if (ascending) {
                    return Order.SIZE_ASC;
                }
                return Order.SIZE;
            }
            else {
                if ('M' != column) {
                    return Order.DEFAULT;
                }
                if (ascending) {
                    return Order.LAST_MODIFIED_ASC;
                }
                return Order.LAST_MODIFIED;
            }
        }
        
        public static class Order
        {
            final char column;
            final boolean ascending;
            public static final Order NAME;
            public static final Order NAME_ASC;
            public static final Order SIZE;
            public static final Order SIZE_ASC;
            public static final Order LAST_MODIFIED;
            public static final Order LAST_MODIFIED_ASC;
            public static final Order DEFAULT;
            
            public Order(final char column, final boolean ascending) {
                this.column = column;
                this.ascending = ascending;
            }
            
            static {
                NAME = new Order('N', false);
                NAME_ASC = new Order('N', true);
                SIZE = new Order('S', false);
                SIZE_ASC = new Order('S', true);
                LAST_MODIFIED = new Order('M', false);
                LAST_MODIFIED_ASC = new Order('M', true);
                DEFAULT = Order.NAME;
            }
        }
    }
    
    private static class DirsFirstComparator implements Comparator<WebResource>
    {
        private final Comparator<WebResource> base;
        
        public DirsFirstComparator(final Comparator<WebResource> core) {
            this.base = core;
        }
        
        @Override
        public int compare(final WebResource r1, final WebResource r2) {
            if (r1.isDirectory()) {
                if (r2.isDirectory()) {
                    return this.base.compare(r1, r2);
                }
                return -1;
            }
            else {
                if (r2.isDirectory()) {
                    return 1;
                }
                return this.base.compare(r1, r2);
            }
        }
    }
    
    private static class ResourceNameComparator implements Comparator<WebResource>
    {
        @Override
        public int compare(final WebResource r1, final WebResource r2) {
            return r1.getName().compareTo(r2.getName());
        }
    }
    
    private static class ResourceSizeComparator implements Comparator<WebResource>
    {
        private Comparator<WebResource> base;
        
        public ResourceSizeComparator(final Comparator<WebResource> base) {
            this.base = base;
        }
        
        @Override
        public int compare(final WebResource r1, final WebResource r2) {
            final int c = Long.compare(r1.getContentLength(), r2.getContentLength());
            if (0 == c) {
                return this.base.compare(r1, r2);
            }
            return c;
        }
    }
    
    private static class ResourceLastModifiedDateComparator implements Comparator<WebResource>
    {
        private Comparator<WebResource> base;
        
        public ResourceLastModifiedDateComparator(final Comparator<WebResource> base) {
            this.base = base;
        }
        
        @Override
        public int compare(final WebResource r1, final WebResource r2) {
            final int c = Long.compare(r1.getLastModified(), r2.getLastModified());
            if (0 == c) {
                return this.base.compare(r1, r2);
            }
            return c;
        }
    }
    
    enum BomConfig
    {
        TRUE("true", true, true), 
        FALSE("false", true, false), 
        PASS_THROUGH("pass-through", false, false);
        
        final String configurationValue;
        final boolean stripBom;
        final boolean useBomEncoding;
        
        private BomConfig(final String configurationValue, final boolean stripBom, final boolean useBomEncoding) {
            this.configurationValue = configurationValue;
            this.stripBom = stripBom;
            this.useBomEncoding = useBomEncoding;
        }
    }
}
