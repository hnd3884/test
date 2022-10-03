package org.apache.axiom.attachments;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.WritableBlob;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.stream.Field;
import org.apache.axiom.mime.Header;
import java.util.ArrayList;
import java.util.List;
import org.apache.axiom.util.UIDGenerator;
import java.util.Collections;
import java.util.Set;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import javax.activation.DataHandler;
import org.apache.james.mime4j.MimeException;
import java.io.IOException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.RecursionMode;
import org.apache.james.mime4j.stream.MimeConfig;
import java.text.ParseException;
import org.apache.axiom.om.OMException;
import java.util.LinkedHashMap;
import java.io.InputStream;
import java.util.Map;
import org.apache.james.mime4j.stream.MimeTokenStream;
import org.apache.axiom.om.util.DetachableInputStream;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.commons.logging.Log;

class MIMEMessage extends AttachmentsDelegate
{
    private static final Log log;
    private static final WritableBlobFactory rootPartBlobFactory;
    private final ContentType contentType;
    private final int contentLength;
    private final DetachableInputStream filterIS;
    private final MimeTokenStream parser;
    private final Map attachmentsMap;
    private int partIndex;
    private PartImpl currentPart;
    private IncomingAttachmentStreams streams;
    private boolean streamsRequested;
    private boolean partsRequested;
    private String firstPartId;
    private final WritableBlobFactory attachmentBlobFactory;
    
    MIMEMessage(final InputStream inStream, final String contentTypeString, final WritableBlobFactory attachmentBlobFactory, final int contentLength) throws OMException {
        this.attachmentsMap = new LinkedHashMap();
        this.partIndex = 0;
        this.contentLength = contentLength;
        this.attachmentBlobFactory = attachmentBlobFactory;
        if (MIMEMessage.log.isDebugEnabled()) {
            MIMEMessage.log.debug((Object)("Attachments contentLength=" + contentLength + ", contentTypeString=" + contentTypeString));
        }
        try {
            this.contentType = new ContentType(contentTypeString);
        }
        catch (final ParseException e) {
            throw new OMException("Invalid Content Type Field in the Mime Message", e);
        }
        InputStream is = inStream;
        if (contentLength <= 0) {
            this.filterIS = new DetachableInputStream(inStream);
            is = this.filterIS;
        }
        else {
            this.filterIS = null;
        }
        final MimeConfig config = new MimeConfig();
        config.setStrictParsing(true);
        (this.parser = new MimeTokenStream(config)).setRecursionMode(RecursionMode.M_NO_RECURSE);
        this.parser.parseHeadless(is, contentTypeString);
        while (this.parser.getState() != EntityState.T_START_BODYPART) {
            try {
                this.parser.next();
                continue;
            }
            catch (final IOException ex) {
                throw new OMException(ex);
            }
            catch (final MimeException ex2) {
                throw new OMException((Throwable)ex2);
            }
            break;
        }
        this.getDataHandler(this.getRootPartContentID());
        this.partsRequested = false;
    }
    
    @Override
    ContentType getContentType() {
        return this.contentType;
    }
    
    @Override
    DataHandler getDataHandler(final String contentID) {
        do {
            final DataHandler dataHandler = this.attachmentsMap.get(contentID);
            if (dataHandler != null) {
                return dataHandler;
            }
        } while (this.getNextPartDataHandler() != null);
        return null;
    }
    
    @Override
    void addDataHandler(final String contentID, final DataHandler dataHandler) {
        this.attachmentsMap.put(contentID, dataHandler);
    }
    
    @Override
    void removeDataHandler(final String blobContentID) {
        while (this.attachmentsMap.remove(blobContentID) == null) {
            if (this.getNextPartDataHandler() == null) {
                return;
            }
        }
    }
    
    @Override
    InputStream getRootPartInputStream(final boolean preserve) throws OMException {
        try {
            final DataHandler dh = this.getDataHandler(this.getRootPartContentID());
            if (dh == null) {
                throw new OMException("Mandatory root MIME part is missing");
            }
            if (!preserve && dh instanceof DataHandlerExt) {
                return ((DataHandlerExt)dh).readOnce();
            }
            return dh.getInputStream();
        }
        catch (final IOException e) {
            throw new OMException("Problem with DataHandler of the Root Mime Part. ", e);
        }
    }
    
    @Override
    String getRootPartContentID() {
        String rootContentID = this.contentType.getParameter("start");
        if (MIMEMessage.log.isDebugEnabled()) {
            MIMEMessage.log.debug((Object)("getRootPartContentID rootContentID=" + rootContentID));
        }
        if (rootContentID == null) {
            if (this.partIndex == 0) {
                this.getNextPartDataHandler();
            }
            rootContentID = this.firstPartId;
        }
        else {
            rootContentID = rootContentID.trim();
            if (rootContentID.indexOf("<") > -1 & rootContentID.indexOf(">") > -1) {
                rootContentID = rootContentID.substring(1, rootContentID.length() - 1);
            }
        }
        if (rootContentID.length() > 4 && "cid:".equalsIgnoreCase(rootContentID.substring(0, 4))) {
            rootContentID = rootContentID.substring(4);
        }
        return rootContentID;
    }
    
    @Override
    String getRootPartContentType() {
        final String rootPartContentID = this.getRootPartContentID();
        if (rootPartContentID == null) {
            throw new OMException("Unable to determine the content ID of the root part");
        }
        final DataHandler rootPart = this.getDataHandler(rootPartContentID);
        if (rootPart == null) {
            throw new OMException("Unable to locate the root part; content ID was " + rootPartContentID);
        }
        return rootPart.getContentType();
    }
    
    @Override
    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        if (this.partsRequested) {
            throw new IllegalStateException("The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        this.streamsRequested = true;
        if (this.streams == null) {
            this.streams = new MultipartAttachmentStreams(this.parser);
        }
        return this.streams;
    }
    
    private void fetchAllParts() {
        while (this.getNextPartDataHandler() != null) {}
    }
    
    @Override
    Set getContentIDs(final boolean fetchAll) {
        if (fetchAll) {
            this.fetchAllParts();
        }
        return this.attachmentsMap.keySet();
    }
    
    @Override
    Map getMap() {
        this.fetchAllParts();
        return Collections.unmodifiableMap((Map<?, ?>)this.attachmentsMap);
    }
    
    @Override
    long getContentLength() throws IOException {
        if (this.contentLength > 0) {
            return this.contentLength;
        }
        this.fetchAllParts();
        return this.filterIS.length();
    }
    
    private DataHandler getNextPartDataHandler() throws OMException {
        if (this.currentPart != null) {
            this.currentPart.fetch();
            this.currentPart = null;
        }
        if (this.parser.getState() == EntityState.T_END_MULTIPART) {
            return null;
        }
        final Part nextPart = this.getPart();
        String partContentID = nextPart.getContentID();
        if (partContentID == null & this.partIndex == 1) {
            final String id = "firstPart_" + UIDGenerator.generateContentId();
            this.firstPartId = id;
            final DataHandler dataHandler = nextPart.getDataHandler();
            this.addDataHandler(id, dataHandler);
            return dataHandler;
        }
        if (partContentID == null) {
            throw new OMException("Part content ID cannot be blank for non root MIME parts");
        }
        if (partContentID.indexOf("<") > -1 & partContentID.indexOf(">") > -1) {
            partContentID = partContentID.substring(1, partContentID.length() - 1);
        }
        if (this.partIndex == 1) {
            this.firstPartId = partContentID;
        }
        if (this.attachmentsMap.containsKey(partContentID)) {
            throw new OMException("Two MIME parts with the same Content-ID not allowed.");
        }
        final DataHandler dataHandler2 = nextPart.getDataHandler();
        this.addDataHandler(partContentID, dataHandler2);
        return dataHandler2;
    }
    
    private Part getPart() throws OMException {
        if (this.streamsRequested) {
            throw new IllegalStateException("The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        this.partsRequested = true;
        final boolean isRootPart = this.partIndex == 0;
        try {
            final List headers = this.readHeaders();
            ++this.partIndex;
            return this.currentPart = new PartImpl(isRootPart ? MIMEMessage.rootPartBlobFactory : this.attachmentBlobFactory, headers, this.parser);
        }
        catch (final IOException ex) {
            throw new OMException(ex);
        }
        catch (final MimeException ex2) {
            throw new OMException((Throwable)ex2);
        }
    }
    
    private List readHeaders() throws IOException, MimeException {
        if (MIMEMessage.log.isDebugEnabled()) {
            MIMEMessage.log.debug((Object)"readHeaders");
        }
        checkParserState(this.parser.next(), EntityState.T_START_HEADER);
        final List headers = new ArrayList();
        while (this.parser.next() == EntityState.T_FIELD) {
            final Field field = this.parser.getField();
            final String name = field.getName();
            final String value = field.getBody();
            if (MIMEMessage.log.isDebugEnabled()) {
                MIMEMessage.log.debug((Object)("addHeader: (" + name + ") value=(" + value + ")"));
            }
            headers.add(new Header(name, value));
        }
        checkParserState(this.parser.next(), EntityState.T_BODY);
        return headers;
    }
    
    private static void checkParserState(final EntityState state, final EntityState expected) throws IllegalStateException {
        if (expected != state) {
            throw new IllegalStateException("Internal error: expected parser to be in state " + expected + ", but got " + state);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)MIMEMessage.class);
        rootPartBlobFactory = new WritableBlobFactory() {
            public WritableBlob createBlob() {
                return Blobs.createMemoryBlob();
            }
        };
    }
}
