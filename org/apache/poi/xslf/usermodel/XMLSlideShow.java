package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdListEntry;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import java.util.Arrays;
import org.apache.poi.util.LittleEndian;
import java.io.FileInputStream;
import java.io.File;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideSize;
import org.apache.poi.util.Units;
import java.awt.Dimension;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdList;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.OptionalLong;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.apache.poi.ooxml.POIXMLRelation;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;
import java.util.stream.Stream;
import java.util.Collections;
import java.util.regex.Pattern;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Iterator;
import java.util.Map;
import org.apache.xmlbeans.XmlException;
import java.util.Collection;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import java.util.HashMap;
import org.openxmlformats.schemas.presentationml.x2006.main.PresentationDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import java.io.IOException;
import org.apache.poi.ooxml.util.PackageHelper;
import java.io.InputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLFactory;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.util.List;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.apache.poi.util.POILogger;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.ooxml.POIXMLDocument;

public class XMLSlideShow extends POIXMLDocument implements SlideShow<XSLFShape, XSLFTextParagraph>
{
    private static final POILogger LOG;
    private static final int MAX_RECORD_LENGTH = 1000000;
    private CTPresentation _presentation;
    private final List<XSLFSlide> _slides;
    private final List<XSLFSlideMaster> _masters;
    private final List<XSLFPictureData> _pictures;
    private final List<XSLFChart> _charts;
    private XSLFTableStyles _tableStyles;
    private XSLFNotesMaster _notesMaster;
    private XSLFCommentAuthors _commentAuthors;
    
    public XMLSlideShow() {
        this(empty());
    }
    
    public XMLSlideShow(final OPCPackage pkg) {
        super(pkg);
        this._slides = new ArrayList<XSLFSlide>();
        this._masters = new ArrayList<XSLFSlideMaster>();
        this._pictures = new ArrayList<XSLFPictureData>();
        this._charts = new ArrayList<XSLFChart>();
        try {
            if (this.getCorePart().getContentType().equals(XSLFRelation.THEME_MANAGER.getContentType())) {
                this.rebase(this.getPackage());
            }
            this.load(XSLFFactory.getInstance());
        }
        catch (final Exception e) {
            throw new POIXMLException(e);
        }
    }
    
    public XMLSlideShow(final InputStream is) throws IOException {
        this(PackageHelper.open(is));
    }
    
    static OPCPackage empty() {
        final InputStream is = XMLSlideShow.class.getResourceAsStream("empty.pptx");
        if (is == null) {
            throw new POIXMLException("Missing resource 'empty.pptx'");
        }
        try {
            return OPCPackage.open(is);
        }
        catch (final Exception e) {
            throw new POIXMLException(e);
        }
        finally {
            IOUtils.closeQuietly((Closeable)is);
        }
    }
    
    protected void onDocumentRead() throws IOException {
        try {
            final PresentationDocument doc = PresentationDocument.Factory.parse(this.getCorePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this._presentation = doc.getPresentation();
            final Map<String, XSLFSlideMaster> masterMap = new HashMap<String, XSLFSlideMaster>();
            final Map<String, XSLFSlide> shIdMap = new HashMap<String, XSLFSlide>();
            final Map<String, XSLFChart> chartMap = new HashMap<String, XSLFChart>();
            for (final RelationPart rp : this.getRelationParts()) {
                final POIXMLDocumentPart p = rp.getDocumentPart();
                if (p instanceof XSLFSlide) {
                    shIdMap.put(rp.getRelationship().getId(), (XSLFSlide)p);
                    for (final POIXMLDocumentPart c : p.getRelations()) {
                        if (c instanceof XSLFChart) {
                            chartMap.put(c.getPackagePart().getPartName().getName(), (XSLFChart)c);
                        }
                    }
                }
                else if (p instanceof XSLFSlideMaster) {
                    masterMap.put(this.getRelationId(p), (XSLFSlideMaster)p);
                }
                else if (p instanceof XSLFTableStyles) {
                    this._tableStyles = (XSLFTableStyles)p;
                }
                else if (p instanceof XSLFNotesMaster) {
                    this._notesMaster = (XSLFNotesMaster)p;
                }
                else {
                    if (!(p instanceof XSLFCommentAuthors)) {
                        continue;
                    }
                    this._commentAuthors = (XSLFCommentAuthors)p;
                }
            }
            this._charts.clear();
            this._charts.addAll(chartMap.values());
            this._masters.clear();
            if (this._presentation.isSetSldMasterIdLst()) {
                this._presentation.getSldMasterIdLst().getSldMasterIdList().forEach(id -> this._masters.add(masterMap.get(id.getId2())));
            }
            this._slides.clear();
            if (this._presentation.isSetSldIdLst()) {
                this._presentation.getSldIdLst().getSldIdList().forEach(id -> {
                    final XSLFSlide sh = shIdMap.get(id.getId2());
                    if (sh == null) {
                        XMLSlideShow.LOG.log(5, new Object[] { "Slide with r:id " + id.getId() + " was defined, but didn't exist in package, skipping" });
                    }
                    else {
                        this._slides.add(sh);
                    }
                });
            }
        }
        catch (final XmlException e) {
            throw new POIXMLException((Throwable)e);
        }
    }
    
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this._presentation.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        out.close();
    }
    
    @Override
    public List<PackagePart> getAllEmbeddedParts() {
        return Collections.unmodifiableList((List<? extends PackagePart>)this.getPackage().getPartsByName(Pattern.compile("/ppt/embeddings/.*?")));
    }
    
    public List<XSLFPictureData> getPictureData() {
        if (this._pictures.isEmpty()) {
            this.getPackage().getPartsByName(Pattern.compile("/ppt/media/.*?")).forEach(part -> {
                final XSLFPictureData pd = new XSLFPictureData(part);
                pd.setIndex(this._pictures.size());
                this._pictures.add(pd);
                return;
            });
        }
        return Collections.unmodifiableList((List<? extends XSLFPictureData>)this._pictures);
    }
    
    public XSLFSlide createSlide(final XSLFSlideLayout layout) {
        final CTSlideIdList slideList = this._presentation.isSetSldIdLst() ? this._presentation.getSldIdLst() : this._presentation.addNewSldIdLst();
        final OptionalLong maxId = Stream.of(slideList.getSldIdArray()).mapToLong(CTSlideIdListEntry::getId).max();
        final XSLFRelation relationType = XSLFRelation.SLIDE;
        final int slideNumber = (int)(Math.max(maxId.orElse(0L), 255L) + 1L);
        final int cnt = this.findNextAvailableFileNameIndex(relationType);
        final RelationPart rp = this.createRelationship(relationType, XSLFFactory.getInstance(), cnt, false);
        final XSLFSlide slide = rp.getDocumentPart();
        final CTSlideIdListEntry slideId = slideList.addNewSldId();
        slideId.setId((long)slideNumber);
        slideId.setId2(rp.getRelationship().getId());
        layout.copyLayout(slide);
        slide.getPackagePart().clearRelationships();
        slide.addRelation(null, XSLFRelation.SLIDE_LAYOUT, layout);
        this._slides.add(slide);
        return slide;
    }
    
    private int findNextAvailableFileNameIndex(final XSLFRelation relationType) {
        try {
            return this.getPackage().getUnusedPartIndex(relationType.getDefaultFileName());
        }
        catch (final InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }
    
    public XSLFSlide createSlide() {
        final XSLFSlideMaster sm = this._masters.get(0);
        XSLFSlideLayout layout = sm.getLayout(SlideLayout.BLANK);
        if (layout == null) {
            XMLSlideShow.LOG.log(5, new Object[] { "Blank layout was not found - defaulting to first slide layout in master" });
            final XSLFSlideLayout[] sl = sm.getSlideLayouts();
            if (sl.length == 0) {
                throw new POIXMLException("SlideMaster must contain a SlideLayout.");
            }
            layout = sl[0];
        }
        return this.createSlide(layout);
    }
    
    public XSLFChart createChart(final XSLFSlide slide) {
        final XSLFChart chart = this.createChart();
        slide.addRelation(null, XSLFRelation.CHART, chart);
        return chart;
    }
    
    public XSLFChart createChart() {
        final int chartIdx = this.findNextAvailableFileNameIndex(XSLFRelation.CHART);
        final XSLFChart chart = this.createRelationship(XSLFRelation.CHART, XSLFFactory.getInstance(), chartIdx, true).getDocumentPart();
        chart.setChartIndex(chartIdx);
        this._charts.add(chart);
        return chart;
    }
    
    public XSLFNotes getNotesSlide(final XSLFSlide slide) {
        XSLFNotes notesSlide = slide.getNotes();
        if (notesSlide == null) {
            notesSlide = this.createNotesSlide(slide);
        }
        return notesSlide;
    }
    
    private XSLFNotes createNotesSlide(final XSLFSlide slide) {
        if (this._notesMaster == null) {
            this.createNotesMaster();
        }
        final XSLFRelation relationType = XSLFRelation.NOTES;
        final int slideIndex = this.findNextAvailableFileNameIndex(relationType);
        final XSLFNotes notesSlide = (XSLFNotes)this.createRelationship(relationType, XSLFFactory.getInstance(), slideIndex);
        slide.addRelation(null, relationType, notesSlide);
        notesSlide.addRelation(null, XSLFRelation.NOTES_MASTER, this._notesMaster);
        notesSlide.addRelation(null, XSLFRelation.SLIDE, slide);
        notesSlide.importContent(this._notesMaster);
        return notesSlide;
    }
    
    public void createNotesMaster() {
        final RelationPart rp = this.createRelationship(XSLFRelation.NOTES_MASTER, XSLFFactory.getInstance(), 1, false);
        this._notesMaster = rp.getDocumentPart();
        final CTNotesMasterIdList notesMasterIdList = this._presentation.addNewNotesMasterIdLst();
        final CTNotesMasterIdListEntry notesMasterId = notesMasterIdList.addNewNotesMasterId();
        notesMasterId.setId(rp.getRelationship().getId());
        int themeIndex = 1;
        final List<Integer> themeIndexList = new ArrayList<Integer>();
        for (final POIXMLDocumentPart p : this.getRelations()) {
            if (p instanceof XSLFTheme) {
                themeIndexList.add(XSLFRelation.THEME.getFileNameIndex(p));
            }
        }
        if (!themeIndexList.isEmpty()) {
            boolean found = false;
            for (int i = 1; i <= themeIndexList.size(); ++i) {
                if (!themeIndexList.contains(i)) {
                    found = true;
                    themeIndex = i;
                }
            }
            if (!found) {
                themeIndex = themeIndexList.size() + 1;
            }
        }
        final XSLFTheme theme = (XSLFTheme)this.createRelationship(XSLFRelation.THEME, XSLFFactory.getInstance(), themeIndex);
        theme.importTheme(this.getSlides().get(0).getTheme());
        this._notesMaster.addRelation(null, XSLFRelation.THEME, theme);
    }
    
    public XSLFNotesMaster getNotesMaster() {
        return this._notesMaster;
    }
    
    public List<XSLFSlideMaster> getSlideMasters() {
        return this._masters;
    }
    
    public List<XSLFSlide> getSlides() {
        return this._slides;
    }
    
    public List<XSLFChart> getCharts() {
        return Collections.unmodifiableList((List<? extends XSLFChart>)this._charts);
    }
    
    public XSLFCommentAuthors getCommentAuthors() {
        return this._commentAuthors;
    }
    
    public void setSlideOrder(final XSLFSlide slide, final int newIndex) {
        final int oldIndex = this._slides.indexOf(slide);
        if (oldIndex == -1) {
            throw new IllegalArgumentException("Slide not found");
        }
        if (oldIndex == newIndex) {
            return;
        }
        this._slides.add(newIndex, this._slides.remove(oldIndex));
        final CTSlideIdList sldIdLst = this._presentation.getSldIdLst();
        final CTSlideIdListEntry[] entries = sldIdLst.getSldIdArray();
        final CTSlideIdListEntry oldEntry = entries[oldIndex];
        if (oldIndex < newIndex) {
            System.arraycopy(entries, oldIndex + 1, entries, oldIndex, newIndex - oldIndex);
        }
        else {
            System.arraycopy(entries, newIndex, entries, newIndex + 1, oldIndex - newIndex);
        }
        entries[newIndex] = oldEntry;
        sldIdLst.setSldIdArray(entries);
    }
    
    public XSLFSlide removeSlide(final int index) {
        final XSLFSlide slide = this._slides.remove(index);
        this.removeRelation(slide);
        this._presentation.getSldIdLst().removeSldId(index);
        for (final POIXMLDocumentPart p : slide.getRelations()) {
            if (p instanceof XSLFChart) {
                final XSLFChart chart = (XSLFChart)p;
                slide.removeChartRelation(chart);
                this._charts.remove(chart);
            }
            else {
                if (!(p instanceof XSLFSlideLayout)) {
                    continue;
                }
                final XSLFSlideLayout layout = (XSLFSlideLayout)p;
                slide.removeLayoutRelation(layout);
            }
        }
        return slide;
    }
    
    public Dimension getPageSize() {
        final CTSlideSize sz = this._presentation.getSldSz();
        final int cx = sz.getCx();
        final int cy = sz.getCy();
        return new Dimension((int)Units.toPoints((long)cx), (int)Units.toPoints((long)cy));
    }
    
    public void setPageSize(final Dimension pgSize) {
        final CTSlideSize sz = CTSlideSize.Factory.newInstance();
        sz.setCx(Units.toEMU(pgSize.getWidth()));
        sz.setCy(Units.toEMU(pgSize.getHeight()));
        this._presentation.setSldSz(sz);
    }
    
    @Internal
    public CTPresentation getCTPresentation() {
        return this._presentation;
    }
    
    public XSLFPictureData addPicture(final byte[] pictureData, final PictureData.PictureType format) {
        XSLFPictureData img = this.findPictureData(pictureData);
        if (img != null) {
            return img;
        }
        final XSLFRelation relType = XSLFPictureData.getRelationForType(format);
        if (relType == null) {
            throw new IllegalArgumentException("Picture type " + format + " is not supported.");
        }
        int imageNumber;
        try {
            imageNumber = this.getPackage().getUnusedPartIndex("/ppt/media/image#\\..+");
        }
        catch (final InvalidFormatException e) {
            imageNumber = this._pictures.size() + 1;
        }
        img = this.createRelationship(relType, XSLFFactory.getInstance(), imageNumber, true).getDocumentPart();
        img.setIndex(this._pictures.size());
        this._pictures.add(img);
        try (final OutputStream out = img.getPackagePart().getOutputStream()) {
            out.write(pictureData);
        }
        catch (final IOException e2) {
            throw new POIXMLException(e2);
        }
        return img;
    }
    
    public XSLFPictureData addPicture(final InputStream is, final PictureData.PictureType format) throws IOException {
        return this.addPicture(IOUtils.toByteArray(is), format);
    }
    
    public XSLFPictureData addPicture(final File pict, final PictureData.PictureType format) throws IOException {
        final byte[] data = IOUtils.safelyAllocate(pict.length(), 1000000);
        try (final InputStream is = new FileInputStream(pict)) {
            IOUtils.readFully(is, data);
        }
        return this.addPicture(data, format);
    }
    
    public XSLFPictureData findPictureData(final byte[] pictureData) {
        final long checksum = IOUtils.calculateChecksum(pictureData);
        final byte[] cs = new byte[8];
        LittleEndian.putLong(cs, 0, checksum);
        for (final XSLFPictureData pic : this.getPictureData()) {
            if (Arrays.equals(pic.getChecksum(), cs)) {
                return pic;
            }
        }
        return null;
    }
    
    public XSLFSlideLayout findLayout(final String name) {
        for (final XSLFSlideMaster master : this.getSlideMasters()) {
            final XSLFSlideLayout layout = master.getLayout(name);
            if (layout != null) {
                return layout;
            }
        }
        return null;
    }
    
    public XSLFTableStyles getTableStyles() {
        return this._tableStyles;
    }
    
    CTTextParagraphProperties getDefaultParagraphStyle(final int level) {
        final XmlObject[] o = this._presentation.selectPath("declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' .//p:defaultTextStyle/a:lvl" + (level + 1) + "pPr");
        if (o.length == 1) {
            return (CTTextParagraphProperties)o[0];
        }
        return null;
    }
    
    public MasterSheet<XSLFShape, XSLFTextParagraph> createMasterSheet() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public POIXMLPropertiesTextExtractor getMetadataTextExtractor() {
        return new POIXMLPropertiesTextExtractor(this);
    }
    
    public Object getPersistDocument() {
        return this;
    }
    
    public XSLFFontInfo addFont(final InputStream fontStream) throws IOException {
        return XSLFFontInfo.addFontToSlideShow(this, fontStream);
    }
    
    public List<XSLFFontInfo> getFonts() {
        return XSLFFontInfo.getFonts(this);
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XMLSlideShow.class);
    }
}
