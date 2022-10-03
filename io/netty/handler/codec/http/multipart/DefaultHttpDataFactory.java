package io.netty.handler.codec.http.multipart;

import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import io.netty.handler.codec.http.HttpConstants;
import java.util.List;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;
import java.nio.charset.Charset;

public class DefaultHttpDataFactory implements HttpDataFactory
{
    public static final long MINSIZE = 16384L;
    public static final long MAXSIZE = -1L;
    private final boolean useDisk;
    private final boolean checkSize;
    private long minSize;
    private long maxSize;
    private Charset charset;
    private String baseDir;
    private boolean deleteOnExit;
    private final Map<HttpRequest, List<HttpData>> requestFileDeleteMap;
    
    public DefaultHttpDataFactory() {
        this.maxSize = -1L;
        this.charset = HttpConstants.DEFAULT_CHARSET;
        this.requestFileDeleteMap = Collections.synchronizedMap(new IdentityHashMap<HttpRequest, List<HttpData>>());
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = 16384L;
    }
    
    public DefaultHttpDataFactory(final Charset charset) {
        this();
        this.charset = charset;
    }
    
    public DefaultHttpDataFactory(final boolean useDisk) {
        this.maxSize = -1L;
        this.charset = HttpConstants.DEFAULT_CHARSET;
        this.requestFileDeleteMap = Collections.synchronizedMap(new IdentityHashMap<HttpRequest, List<HttpData>>());
        this.useDisk = useDisk;
        this.checkSize = false;
    }
    
    public DefaultHttpDataFactory(final boolean useDisk, final Charset charset) {
        this(useDisk);
        this.charset = charset;
    }
    
    public DefaultHttpDataFactory(final long minSize) {
        this.maxSize = -1L;
        this.charset = HttpConstants.DEFAULT_CHARSET;
        this.requestFileDeleteMap = Collections.synchronizedMap(new IdentityHashMap<HttpRequest, List<HttpData>>());
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = minSize;
    }
    
    public DefaultHttpDataFactory(final long minSize, final Charset charset) {
        this(minSize);
        this.charset = charset;
    }
    
    public void setBaseDir(final String baseDir) {
        this.baseDir = baseDir;
    }
    
    public void setDeleteOnExit(final boolean deleteOnExit) {
        this.deleteOnExit = deleteOnExit;
    }
    
    @Override
    public void setMaxLimit(final long maxSize) {
        this.maxSize = maxSize;
    }
    
    private List<HttpData> getList(final HttpRequest request) {
        List<HttpData> list = this.requestFileDeleteMap.get(request);
        if (list == null) {
            list = new ArrayList<HttpData>();
            this.requestFileDeleteMap.put(request, list);
        }
        return list;
    }
    
    @Override
    public Attribute createAttribute(final HttpRequest request, final String name) {
        if (this.useDisk) {
            final Attribute attribute = new DiskAttribute(name, this.charset, this.baseDir, this.deleteOnExit);
            attribute.setMaxSize(this.maxSize);
            final List<HttpData> list = this.getList(request);
            list.add(attribute);
            return attribute;
        }
        if (this.checkSize) {
            final Attribute attribute = new MixedAttribute(name, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
            attribute.setMaxSize(this.maxSize);
            final List<HttpData> list = this.getList(request);
            list.add(attribute);
            return attribute;
        }
        final MemoryAttribute attribute2 = new MemoryAttribute(name);
        attribute2.setMaxSize(this.maxSize);
        return attribute2;
    }
    
    @Override
    public Attribute createAttribute(final HttpRequest request, final String name, final long definedSize) {
        if (this.useDisk) {
            final Attribute attribute = new DiskAttribute(name, definedSize, this.charset, this.baseDir, this.deleteOnExit);
            attribute.setMaxSize(this.maxSize);
            final List<HttpData> list = this.getList(request);
            list.add(attribute);
            return attribute;
        }
        if (this.checkSize) {
            final Attribute attribute = new MixedAttribute(name, definedSize, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
            attribute.setMaxSize(this.maxSize);
            final List<HttpData> list = this.getList(request);
            list.add(attribute);
            return attribute;
        }
        final MemoryAttribute attribute2 = new MemoryAttribute(name, definedSize);
        attribute2.setMaxSize(this.maxSize);
        return attribute2;
    }
    
    private static void checkHttpDataSize(final HttpData data) {
        try {
            data.checkSize(data.length());
        }
        catch (final IOException ignored) {
            throw new IllegalArgumentException("Attribute bigger than maxSize allowed");
        }
    }
    
    @Override
    public Attribute createAttribute(final HttpRequest request, final String name, final String value) {
        if (this.useDisk) {
            Attribute attribute;
            try {
                attribute = new DiskAttribute(name, value, this.charset, this.baseDir, this.deleteOnExit);
                attribute.setMaxSize(this.maxSize);
            }
            catch (final IOException e) {
                attribute = new MixedAttribute(name, value, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
                attribute.setMaxSize(this.maxSize);
            }
            checkHttpDataSize(attribute);
            final List<HttpData> list = this.getList(request);
            list.add(attribute);
            return attribute;
        }
        if (this.checkSize) {
            final Attribute attribute = new MixedAttribute(name, value, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
            attribute.setMaxSize(this.maxSize);
            checkHttpDataSize(attribute);
            final List<HttpData> list = this.getList(request);
            list.add(attribute);
            return attribute;
        }
        try {
            final MemoryAttribute attribute2 = new MemoryAttribute(name, value, this.charset);
            attribute2.setMaxSize(this.maxSize);
            checkHttpDataSize(attribute2);
            return attribute2;
        }
        catch (final IOException e2) {
            throw new IllegalArgumentException(e2);
        }
    }
    
    @Override
    public FileUpload createFileUpload(final HttpRequest request, final String name, final String filename, final String contentType, final String contentTransferEncoding, final Charset charset, final long size) {
        if (this.useDisk) {
            final FileUpload fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, this.baseDir, this.deleteOnExit);
            fileUpload.setMaxSize(this.maxSize);
            checkHttpDataSize(fileUpload);
            final List<HttpData> list = this.getList(request);
            list.add(fileUpload);
            return fileUpload;
        }
        if (this.checkSize) {
            final FileUpload fileUpload = new MixedFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, this.minSize, this.baseDir, this.deleteOnExit);
            fileUpload.setMaxSize(this.maxSize);
            checkHttpDataSize(fileUpload);
            final List<HttpData> list = this.getList(request);
            list.add(fileUpload);
            return fileUpload;
        }
        final MemoryFileUpload fileUpload2 = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
        fileUpload2.setMaxSize(this.maxSize);
        checkHttpDataSize(fileUpload2);
        return fileUpload2;
    }
    
    @Override
    public void removeHttpDataFromClean(final HttpRequest request, final InterfaceHttpData data) {
        if (!(data instanceof HttpData)) {
            return;
        }
        final List<HttpData> list = this.requestFileDeleteMap.get(request);
        if (list == null) {
            return;
        }
        final Iterator<HttpData> i = list.iterator();
        while (i.hasNext()) {
            final HttpData n = i.next();
            if (n == data) {
                i.remove();
                if (list.isEmpty()) {
                    this.requestFileDeleteMap.remove(request);
                }
            }
        }
    }
    
    @Override
    public void cleanRequestHttpData(final HttpRequest request) {
        final List<HttpData> list = this.requestFileDeleteMap.remove(request);
        if (list != null) {
            for (final HttpData data : list) {
                data.release();
            }
        }
    }
    
    @Override
    public void cleanAllHttpData() {
        final Iterator<Map.Entry<HttpRequest, List<HttpData>>> i = this.requestFileDeleteMap.entrySet().iterator();
        while (i.hasNext()) {
            final Map.Entry<HttpRequest, List<HttpData>> e = i.next();
            final List<HttpData> list = e.getValue();
            for (final HttpData data : list) {
                data.release();
            }
            i.remove();
        }
    }
    
    @Override
    public void cleanRequestHttpDatas(final HttpRequest request) {
        this.cleanRequestHttpData(request);
    }
    
    @Override
    public void cleanAllHttpDatas() {
        this.cleanAllHttpData();
    }
}
