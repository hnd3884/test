package org.apache.poi.xslf.util;

import java.util.Iterator;
import org.apache.poi.sl.usermodel.ObjectData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.ObjectShape;
import org.apache.poi.sl.draw.EmbeddedExtractor;
import java.util.stream.IntStream;
import org.apache.poi.common.usermodel.GenericRecord;
import java.awt.Graphics2D;
import java.util.Spliterator;
import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.Set;
import java.awt.geom.Dimension2D;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.sl.usermodel.SlideShowFactory;
import java.io.File;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.util.Internal;

@Internal
class PPTHandler extends MFProxy
{
    private SlideShow<?, ?> ppt;
    private Slide<?, ?> slide;
    private static final String RANGE_PATTERN = "(^|,)(?<from>\\d+)?(-(?<to>\\d+))?";
    
    public void parse(final File file) throws IOException {
        try {
            this.ppt = (SlideShow<?, ?>)SlideShowFactory.create(file, (String)null, true);
        }
        catch (final IOException e) {
            if (e.getMessage().contains("scratchpad")) {
                throw new PPTX2PNG.NoScratchpadException(e);
            }
            throw e;
        }
        this.slide = (Slide<?, ?>)this.ppt.getSlides().get(0);
    }
    
    public void parse(final InputStream is) throws IOException {
        try {
            this.ppt = (SlideShow<?, ?>)SlideShowFactory.create(is, (String)null);
        }
        catch (final IOException e) {
            if (e.getMessage().contains("scratchpad")) {
                throw new PPTX2PNG.NoScratchpadException(e);
            }
            throw e;
        }
        this.slide = (Slide<?, ?>)this.ppt.getSlides().get(0);
    }
    
    public Dimension2D getSize() {
        return this.ppt.getPageSize();
    }
    
    public int getSlideCount() {
        return this.ppt.getSlides().size();
    }
    
    public void setSlideNo(final int slideNo) {
        this.slide = (Slide<?, ?>)this.ppt.getSlides().get(slideNo - 1);
    }
    
    public String getTitle() {
        return this.slide.getTitle();
    }
    
    public Set<Integer> slideIndexes(final String range) {
        final Matcher matcher = Pattern.compile("(^|,)(?<from>\\d+)?(-(?<to>\\d+))?").matcher(range);
        final Spliterator<Matcher> sp = new Spliterators.AbstractSpliterator<Matcher>((long)range.length(), 272) {
            @Override
            public boolean tryAdvance(final Consumer<? super Matcher> action) {
                final boolean b = matcher.find();
                if (b) {
                    action.accept(matcher);
                }
                return b;
            }
        };
        return StreamSupport.stream(sp, false).flatMap((Function<? super Matcher, ? extends Stream<?>>)this::range).collect((Collector<? super Object, ?, Set<Integer>>)Collectors.toCollection((Supplier<R>)TreeSet::new));
    }
    
    public void draw(final Graphics2D ctx) {
        this.slide.draw(ctx);
    }
    
    @Override
    public void close() throws IOException {
        if (this.ppt != null) {
            this.ppt.close();
        }
    }
    
    public GenericRecord getRoot() {
        return (this.ppt instanceof GenericRecord) ? this.ppt : null;
    }
    
    private Stream<Integer> range(final Matcher m) {
        final int slideCount = this.ppt.getSlides().size();
        final String fromStr = m.group("from");
        final String toStr = m.group("to");
        final int from = (fromStr == null || fromStr.isEmpty()) ? 1 : Integer.parseInt(fromStr);
        final int to = (toStr == null) ? from : ((toStr.isEmpty() || ((fromStr == null || fromStr.isEmpty()) && "1".equals(toStr))) ? slideCount : Integer.parseInt(toStr));
        return IntStream.rangeClosed(from, to).filter(i -> i <= slideCount).boxed();
    }
    
    public Iterable<EmbeddedExtractor.EmbeddedPart> getEmbeddings(final int slideNo) {
        return () -> this.ppt.getSlides().get(slideNo).getShapes().stream().filter(s -> s instanceof ObjectShape).map(PPTHandler::fromObjectShape).iterator();
    }
    
    private static EmbeddedExtractor.EmbeddedPart fromObjectShape(final Shape s) {
        final ObjectShape os = (ObjectShape)s;
        final ObjectData od = os.getObjectData();
        final EmbeddedExtractor.EmbeddedPart embed = new EmbeddedExtractor.EmbeddedPart();
        embed.setName(od.getFileName());
        embed.setData(() -> {
            try {
                final InputStream is = od.getInputStream();
                try {
                    IOUtils.toByteArray(is);
                    return;
                }
                catch (final Throwable t) {
                    throw t;
                }
                finally {
                    if (is != null) {
                        final Throwable t2;
                        if (t2 != null) {
                            try {
                                is.close();
                            }
                            catch (final Throwable t3) {
                                t2.addSuppressed(t3);
                            }
                        }
                        else {
                            is.close();
                        }
                    }
                }
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        return embed;
    }
}
