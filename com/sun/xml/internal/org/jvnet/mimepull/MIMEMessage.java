package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.io.InputStream;
import java.util.logging.Logger;

public class MIMEMessage
{
    private static final Logger LOGGER;
    MIMEConfig config;
    private final InputStream in;
    private final List<MIMEPart> partsList;
    private final Map<String, MIMEPart> partsMap;
    private final Iterator<MIMEEvent> it;
    private boolean parsed;
    private MIMEPart currentPart;
    private int currentIndex;
    
    public MIMEMessage(final InputStream in, final String boundary) {
        this(in, boundary, new MIMEConfig());
    }
    
    public MIMEMessage(final InputStream in, final String boundary, final MIMEConfig config) {
        this.in = in;
        this.config = config;
        final MIMEParser parser = new MIMEParser(in, boundary, config);
        this.it = parser.iterator();
        this.partsList = new ArrayList<MIMEPart>();
        this.partsMap = new HashMap<String, MIMEPart>();
        if (config.isParseEagerly()) {
            this.parseAll();
        }
    }
    
    public List<MIMEPart> getAttachments() {
        if (!this.parsed) {
            this.parseAll();
        }
        return this.partsList;
    }
    
    public MIMEPart getPart(final int index) {
        MIMEMessage.LOGGER.log(Level.FINE, "index={0}", index);
        MIMEPart part = (index < this.partsList.size()) ? this.partsList.get(index) : null;
        if (this.parsed && part == null) {
            throw new MIMEParsingException("There is no " + index + " attachment part ");
        }
        if (part == null) {
            part = new MIMEPart(this);
            this.partsList.add(index, part);
        }
        MIMEMessage.LOGGER.log(Level.FINE, "Got attachment at index={0} attachment={1}", new Object[] { index, part });
        return part;
    }
    
    public MIMEPart getPart(final String contentId) {
        MIMEMessage.LOGGER.log(Level.FINE, "Content-ID={0}", contentId);
        MIMEPart part = this.getDecodedCidPart(contentId);
        if (this.parsed && part == null) {
            throw new MIMEParsingException("There is no attachment part with Content-ID = " + contentId);
        }
        if (part == null) {
            part = new MIMEPart(this, contentId);
            this.partsMap.put(contentId, part);
        }
        MIMEMessage.LOGGER.log(Level.FINE, "Got attachment for Content-ID={0} attachment={1}", new Object[] { contentId, part });
        return part;
    }
    
    private MIMEPart getDecodedCidPart(final String cid) {
        MIMEPart part = this.partsMap.get(cid);
        if (part == null && cid.indexOf(37) != -1) {
            try {
                final String tempCid = URLDecoder.decode(cid, "utf-8");
                part = this.partsMap.get(tempCid);
            }
            catch (final UnsupportedEncodingException ex) {}
        }
        return part;
    }
    
    public final void parseAll() {
        while (this.makeProgress()) {}
    }
    
    public synchronized boolean makeProgress() {
        if (!this.it.hasNext()) {
            return false;
        }
        final MIMEEvent event = this.it.next();
        switch (event.getEventType()) {
            case START_MESSAGE: {
                MIMEMessage.LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.START_MESSAGE);
                return true;
            }
            case START_PART: {
                MIMEMessage.LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.START_PART);
                return true;
            }
            case HEADERS: {
                MIMEMessage.LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.HEADERS);
                final MIMEEvent.Headers headers = (MIMEEvent.Headers)event;
                final InternetHeaders ih = headers.getHeaders();
                final List<String> cids = ih.getHeader("content-id");
                String cid = (cids != null) ? cids.get(0) : (this.currentIndex + "");
                if (cid.length() > 2 && cid.charAt(0) == '<') {
                    cid = cid.substring(1, cid.length() - 1);
                }
                final MIMEPart listPart = (this.currentIndex < this.partsList.size()) ? this.partsList.get(this.currentIndex) : null;
                final MIMEPart mapPart = this.getDecodedCidPart(cid);
                if (listPart == null && mapPart == null) {
                    this.currentPart = this.getPart(cid);
                    this.partsList.add(this.currentIndex, this.currentPart);
                }
                else if (listPart == null) {
                    this.currentPart = mapPart;
                    this.partsList.add(this.currentIndex, mapPart);
                }
                else if (mapPart == null) {
                    (this.currentPart = listPart).setContentId(cid);
                    this.partsMap.put(cid, this.currentPart);
                }
                else if (listPart != mapPart) {
                    throw new MIMEParsingException("Created two different attachments using Content-ID and index");
                }
                this.currentPart.setHeaders(ih);
                return true;
            }
            case CONTENT: {
                MIMEMessage.LOGGER.log(Level.FINER, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.CONTENT);
                final MIMEEvent.Content content = (MIMEEvent.Content)event;
                final ByteBuffer buf = content.getData();
                this.currentPart.addBody(buf);
                return true;
            }
            case END_PART: {
                MIMEMessage.LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.END_PART);
                this.currentPart.doneParsing();
                ++this.currentIndex;
                return true;
            }
            case END_MESSAGE: {
                MIMEMessage.LOGGER.log(Level.FINE, "MIMEEvent={0}", MIMEEvent.EVENT_TYPE.END_MESSAGE);
                this.parsed = true;
                try {
                    this.in.close();
                    return true;
                }
                catch (final IOException ioe) {
                    throw new MIMEParsingException(ioe);
                }
                break;
            }
        }
        throw new MIMEParsingException("Unknown Parser state = " + event.getEventType());
    }
    
    static {
        LOGGER = Logger.getLogger(MIMEMessage.class.getName());
    }
}
