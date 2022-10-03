package org.apache.lucene.index;

import java.util.ArrayList;
import org.apache.lucene.util.SuppressForbidden;
import java.nio.file.Path;
import org.apache.lucene.util.CommandLineUtil;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.util.LongBitSet;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.document.Document;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.document.DocumentStoredFieldVisitor;
import java.util.HashMap;
import java.util.Deque;
import java.util.LinkedList;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.util.Bits;
import java.util.Map;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.store.IndexInput;
import java.util.Iterator;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.Version;
import java.util.Collection;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.store.IOContext;
import java.util.Arrays;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.store.AlreadyClosedException;
import java.io.IOException;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.Directory;
import java.io.PrintStream;
import java.io.Closeable;

public final class CheckIndex implements Closeable
{
    private PrintStream infoStream;
    private Directory dir;
    private Lock writeLock;
    private volatile boolean closed;
    private boolean crossCheckTermVectors;
    private boolean failFast;
    private boolean verbose;
    private boolean checksumsOnly;
    private static boolean assertsOn;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public CheckIndex(final Directory dir) throws IOException {
        this(dir, dir.obtainLock("write.lock"));
    }
    
    public CheckIndex(final Directory dir, final Lock writeLock) throws IOException {
        this.dir = dir;
        this.writeLock = writeLock;
        this.infoStream = null;
    }
    
    private void ensureOpen() {
        if (this.closed) {
            throw new AlreadyClosedException("this instance is closed");
        }
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
        IOUtils.close(this.writeLock);
    }
    
    public void setCrossCheckTermVectors(final boolean v) {
        this.crossCheckTermVectors = v;
    }
    
    public boolean getCrossCheckTermVectors() {
        return this.crossCheckTermVectors;
    }
    
    public void setFailFast(final boolean v) {
        this.failFast = v;
    }
    
    public boolean getFailFast() {
        return this.failFast;
    }
    
    public boolean getChecksumsOnly() {
        return this.checksumsOnly;
    }
    
    public void setChecksumsOnly(final boolean v) {
        this.checksumsOnly = v;
    }
    
    public void setInfoStream(final PrintStream out, final boolean verbose) {
        this.infoStream = out;
        this.verbose = verbose;
    }
    
    public void setInfoStream(final PrintStream out) {
        this.setInfoStream(out, false);
    }
    
    private static void msg(final PrintStream out, final String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
    
    public Status checkIndex() throws IOException {
        return this.checkIndex(null);
    }
    
    public Status checkIndex(final List<String> onlySegments) throws IOException {
        this.ensureOpen();
        final long startNS = System.nanoTime();
        final NumberFormat nf = NumberFormat.getInstance(Locale.ROOT);
        SegmentInfos sis = null;
        final Status result = new Status();
        result.dir = this.dir;
        final String[] files = this.dir.listAll();
        final String lastSegmentsFile = SegmentInfos.getLastCommitSegmentsFileName(files);
        if (lastSegmentsFile == null) {
            throw new IndexNotFoundException("no segments* file found in " + this.dir + ": files: " + Arrays.toString(files));
        }
        try {
            sis = SegmentInfos.readCommit(this.dir, lastSegmentsFile);
        }
        catch (final Throwable t) {
            if (this.failFast) {
                IOUtils.reThrow(t);
            }
            msg(this.infoStream, "ERROR: could not read any segments file in directory");
            result.missingSegments = true;
            if (this.infoStream != null) {
                t.printStackTrace(this.infoStream);
            }
            return result;
        }
        Version oldest = null;
        Version newest = null;
        String oldSegs = null;
        for (final SegmentCommitInfo si : sis) {
            final Version version = si.info.getVersion();
            if (version == null) {
                oldSegs = "pre-3.1";
            }
            else {
                if (oldest == null || !version.onOrAfter(oldest)) {
                    oldest = version;
                }
                if (newest != null && !version.onOrAfter(newest)) {
                    continue;
                }
                newest = version;
            }
        }
        final int numSegments = sis.size();
        final String segmentsFileName = sis.getSegmentsFileName();
        IndexInput input = null;
        try {
            input = this.dir.openInput(segmentsFileName, IOContext.READONCE);
        }
        catch (final Throwable t2) {
            if (this.failFast) {
                IOUtils.reThrow(t2);
            }
            msg(this.infoStream, "ERROR: could not open segments file in directory");
            if (this.infoStream != null) {
                t2.printStackTrace(this.infoStream);
            }
            result.cantOpenSegments = true;
            return result;
        }
        try {
            input.readInt();
        }
        catch (final Throwable t2) {
            if (this.failFast) {
                IOUtils.reThrow(t2);
            }
            msg(this.infoStream, "ERROR: could not read segment file version in directory");
            if (this.infoStream != null) {
                t2.printStackTrace(this.infoStream);
            }
            result.missingSegmentVersion = true;
            return result;
        }
        finally {
            if (input != null) {
                input.close();
            }
        }
        final String sFormat = "";
        result.segmentsFileName = segmentsFileName;
        result.numSegments = numSegments;
        result.userData = sis.getUserData();
        String userDataString;
        if (sis.getUserData().size() > 0) {
            userDataString = " userData=" + sis.getUserData();
        }
        else {
            userDataString = "";
        }
        String versionString = "";
        if (oldSegs != null) {
            if (newest != null) {
                versionString = "versions=[" + oldSegs + " .. " + newest + "]";
            }
            else {
                versionString = "version=" + oldSegs;
            }
        }
        else if (newest != null) {
            versionString = (oldest.equals(newest) ? ("version=" + oldest) : ("versions=[" + oldest + " .. " + newest + "]"));
        }
        msg(this.infoStream, "Segments file=" + segmentsFileName + " numSegments=" + numSegments + " " + versionString + " id=" + StringHelper.idToString(sis.getId()) + " format=" + sFormat + userDataString);
        if (onlySegments != null) {
            result.partial = true;
            if (this.infoStream != null) {
                this.infoStream.print("\nChecking only these segments:");
                for (final String s : onlySegments) {
                    this.infoStream.print(" " + s);
                }
            }
            result.segmentsChecked.addAll(onlySegments);
            msg(this.infoStream, ":");
        }
        (result.newSegments = sis.clone()).clear();
        result.maxSegmentName = -1;
        for (int i = 0; i < numSegments; ++i) {
            final SegmentCommitInfo info = sis.info(i);
            final int segmentName = Integer.parseInt(info.info.name.substring(1), 36);
            if (segmentName > result.maxSegmentName) {
                result.maxSegmentName = segmentName;
            }
            if (onlySegments == null || onlySegments.contains(info.info.name)) {
                final Status.SegmentInfoStatus segInfoStat = new Status.SegmentInfoStatus();
                result.segmentInfos.add(segInfoStat);
                msg(this.infoStream, "  " + (1 + i) + " of " + numSegments + ": name=" + info.info.name + " maxDoc=" + info.info.maxDoc());
                segInfoStat.name = info.info.name;
                segInfoStat.maxDoc = info.info.maxDoc();
                segInfoStat.version = info.info.getVersion();
                if (info.info.maxDoc() <= 0 && segInfoStat.version.onOrAfter(Version.LUCENE_4_5_0)) {
                    throw new RuntimeException("illegal number of documents: maxDoc=" + info.info.maxDoc());
                }
                int toLoseDocCount = info.info.maxDoc();
                SegmentReader reader = null;
                try {
                    msg(this.infoStream, "    version=" + segInfoStat.version);
                    msg(this.infoStream, "    id=" + StringHelper.idToString(info.info.getId()));
                    final Codec codec = info.info.getCodec();
                    msg(this.infoStream, "    codec=" + codec);
                    segInfoStat.codec = codec;
                    msg(this.infoStream, "    compound=" + info.info.getUseCompoundFile());
                    segInfoStat.compound = info.info.getUseCompoundFile();
                    msg(this.infoStream, "    numFiles=" + info.files().size());
                    segInfoStat.numFiles = info.files().size();
                    segInfoStat.sizeMB = info.sizeInBytes() / 1048576.0;
                    msg(this.infoStream, "    size (MB)=" + nf.format(segInfoStat.sizeMB));
                    final Map<String, String> diagnostics = info.info.getDiagnostics();
                    segInfoStat.diagnostics = diagnostics;
                    if (diagnostics.size() > 0) {
                        msg(this.infoStream, "    diagnostics = " + diagnostics);
                    }
                    if (!info.hasDeletions()) {
                        msg(this.infoStream, "    no deletions");
                        segInfoStat.hasDeletions = false;
                    }
                    else {
                        msg(this.infoStream, "    has deletions [delGen=" + info.getDelGen() + "]");
                        segInfoStat.hasDeletions = true;
                        segInfoStat.deletionsGen = info.getDelGen();
                    }
                    final long startOpenReaderNS = System.nanoTime();
                    if (this.infoStream != null) {
                        this.infoStream.print("    test: open reader.........");
                    }
                    reader = new SegmentReader(info, IOContext.DEFAULT);
                    msg(this.infoStream, String.format(Locale.ROOT, "OK [took %.3f sec]", nsToSec(System.nanoTime() - startOpenReaderNS)));
                    segInfoStat.openReaderPassed = true;
                    final long startIntegrityNS = System.nanoTime();
                    if (this.infoStream != null) {
                        this.infoStream.print("    test: check integrity.....");
                    }
                    reader.checkIntegrity();
                    msg(this.infoStream, String.format(Locale.ROOT, "OK [took %.3f sec]", nsToSec(System.nanoTime() - startIntegrityNS)));
                    if (reader.maxDoc() != info.info.maxDoc()) {
                        throw new RuntimeException("SegmentReader.maxDoc() " + reader.maxDoc() + " != SegmentInfo.maxDoc " + info.info.maxDoc());
                    }
                    final int numDocs = toLoseDocCount = reader.numDocs();
                    if (reader.hasDeletions()) {
                        if (reader.numDocs() != info.info.maxDoc() - info.getDelCount()) {
                            throw new RuntimeException("delete count mismatch: info=" + (info.info.maxDoc() - info.getDelCount()) + " vs reader=" + reader.numDocs());
                        }
                        if (info.info.maxDoc() - reader.numDocs() > reader.maxDoc()) {
                            throw new RuntimeException("too many deleted docs: maxDoc()=" + reader.maxDoc() + " vs del count=" + (info.info.maxDoc() - reader.numDocs()));
                        }
                        if (info.info.maxDoc() - reader.numDocs() != info.getDelCount()) {
                            throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs reader=" + (info.info.maxDoc() - reader.numDocs()));
                        }
                    }
                    else if (info.getDelCount() != 0) {
                        throw new RuntimeException("delete count mismatch: info=" + info.getDelCount() + " vs reader=" + (info.info.maxDoc() - reader.numDocs()));
                    }
                    if (!this.checksumsOnly) {
                        segInfoStat.liveDocStatus = testLiveDocs(reader, this.infoStream, this.failFast);
                        segInfoStat.fieldInfoStatus = testFieldInfos(reader, this.infoStream, this.failFast);
                        segInfoStat.fieldNormStatus = testFieldNorms(reader, this.infoStream, this.failFast);
                        segInfoStat.termIndexStatus = testPostings(reader, this.infoStream, this.verbose, this.failFast);
                        segInfoStat.storedFieldStatus = testStoredFields(reader, this.infoStream, this.failFast);
                        segInfoStat.termVectorStatus = testTermVectors(reader, this.infoStream, this.verbose, this.crossCheckTermVectors, this.failFast);
                        segInfoStat.docValuesStatus = testDocValues(reader, this.infoStream, this.failFast);
                        if (segInfoStat.liveDocStatus.error != null) {
                            throw new RuntimeException("Live docs test failed");
                        }
                        if (segInfoStat.fieldInfoStatus.error != null) {
                            throw new RuntimeException("Field Info test failed");
                        }
                        if (segInfoStat.fieldNormStatus.error != null) {
                            throw new RuntimeException("Field Norm test failed");
                        }
                        if (segInfoStat.termIndexStatus.error != null) {
                            throw new RuntimeException("Term Index test failed");
                        }
                        if (segInfoStat.storedFieldStatus.error != null) {
                            throw new RuntimeException("Stored Field test failed");
                        }
                        if (segInfoStat.termVectorStatus.error != null) {
                            throw new RuntimeException("Term Vector test failed");
                        }
                        if (segInfoStat.docValuesStatus.error != null) {
                            throw new RuntimeException("DocValues test failed");
                        }
                    }
                    msg(this.infoStream, "");
                    if (this.verbose) {
                        msg(this.infoStream, "detailed segment RAM usage: ");
                        msg(this.infoStream, Accountables.toString(reader));
                    }
                }
                catch (final Throwable t3) {
                    if (this.failFast) {
                        IOUtils.reThrow(t3);
                    }
                    msg(this.infoStream, "FAILED");
                    final String comment = "exorciseIndex() would remove reference to this segment";
                    msg(this.infoStream, "    WARNING: " + comment + "; full exception:");
                    if (this.infoStream != null) {
                        t3.printStackTrace(this.infoStream);
                    }
                    msg(this.infoStream, "");
                    final Status status = result;
                    status.totLoseDocCount += toLoseDocCount;
                    final Status status2 = result;
                    ++status2.numBadSegments;
                    continue;
                }
                finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
                result.newSegments.add(info.clone());
            }
        }
        if (0 == result.numBadSegments) {
            result.clean = true;
        }
        else {
            msg(this.infoStream, "WARNING: " + result.numBadSegments + " broken segments (containing " + result.totLoseDocCount + " documents) detected");
        }
        if (!(result.validCounter = (result.maxSegmentName < sis.counter))) {
            result.clean = false;
            result.newSegments.counter = result.maxSegmentName + 1;
            msg(this.infoStream, "ERROR: Next segment name counter " + sis.counter + " is not greater than max segment name " + result.maxSegmentName);
        }
        if (this.getChecksumsOnly()) {
            boolean old = false;
            boolean ancient = false;
            for (final Status.SegmentInfoStatus segment : result.segmentInfos) {
                old |= !segment.version.onOrAfter(Version.LUCENE_5_0_0);
                ancient |= !segment.version.onOrAfter(Version.LUCENE_4_8_0);
            }
            if (ancient) {
                msg(this.infoStream, "WARNING: Some segments are older than 4.8 and have no checksums. Run checkindex without -fast for full verification.");
            }
            else if (old) {
                msg(this.infoStream, "WARNING: Some segments are older than 5.0 and have no identifiers. Run checkindex without -fast for full verification.");
            }
        }
        if (result.clean) {
            msg(this.infoStream, "No problems were detected with this index.\n");
        }
        msg(this.infoStream, String.format(Locale.ROOT, "Took %.3f sec total.", nsToSec(System.nanoTime() - startNS)));
        return result;
    }
    
    public static Status.LiveDocStatus testLiveDocs(final CodecReader reader, final PrintStream infoStream, final boolean failFast) throws IOException {
        final long startNS = System.nanoTime();
        final Status.LiveDocStatus status = new Status.LiveDocStatus();
        try {
            if (infoStream != null) {
                infoStream.print("    test: check live docs.....");
            }
            final int numDocs = reader.numDocs();
            if (reader.hasDeletions()) {
                final Bits liveDocs = reader.getLiveDocs();
                if (liveDocs == null) {
                    throw new RuntimeException("segment should have deletions, but liveDocs is null");
                }
                int numLive = 0;
                for (int j = 0; j < liveDocs.length(); ++j) {
                    if (liveDocs.get(j)) {
                        ++numLive;
                    }
                }
                if (numLive != numDocs) {
                    throw new RuntimeException("liveDocs count mismatch: info=" + numDocs + ", vs bits=" + numLive);
                }
                status.numDeleted = reader.numDeletedDocs();
                msg(infoStream, String.format(Locale.ROOT, "OK [%d deleted docs] [took %.3f sec]", status.numDeleted, nsToSec(System.nanoTime() - startNS)));
            }
            else {
                final Bits liveDocs = reader.getLiveDocs();
                if (liveDocs != null) {
                    for (int i = 0; i < liveDocs.length(); ++i) {
                        if (!liveDocs.get(i)) {
                            throw new RuntimeException("liveDocs mismatch: info says no deletions but doc " + i + " is deleted.");
                        }
                    }
                }
                msg(infoStream, String.format(Locale.ROOT, "OK [took %.3f sec]", nsToSec(System.nanoTime() - startNS)));
            }
        }
        catch (final Throwable e) {
            if (failFast) {
                IOUtils.reThrow(e);
            }
            msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
            status.error = e;
            if (infoStream != null) {
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }
    
    public static Status.FieldInfoStatus testFieldInfos(final CodecReader reader, final PrintStream infoStream, final boolean failFast) throws IOException {
        final long startNS = System.nanoTime();
        final Status.FieldInfoStatus status = new Status.FieldInfoStatus();
        try {
            if (infoStream != null) {
                infoStream.print("    test: field infos.........");
            }
            final FieldInfos fieldInfos = reader.getFieldInfos();
            for (final FieldInfo f : fieldInfos) {
                f.checkConsistency();
            }
            msg(infoStream, String.format(Locale.ROOT, "OK [%d fields] [took %.3f sec]", fieldInfos.size(), nsToSec(System.nanoTime() - startNS)));
            status.totFields = fieldInfos.size();
        }
        catch (final Throwable e) {
            if (failFast) {
                IOUtils.reThrow(e);
            }
            msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
            status.error = e;
            if (infoStream != null) {
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }
    
    public static Status.FieldNormStatus testFieldNorms(final CodecReader reader, final PrintStream infoStream, final boolean failFast) throws IOException {
        final long startNS = System.nanoTime();
        final Status.FieldNormStatus status = new Status.FieldNormStatus();
        try {
            if (infoStream != null) {
                infoStream.print("    test: field norms.........");
            }
            NormsProducer normsReader = reader.getNormsReader();
            if (normsReader != null) {
                normsReader = normsReader.getMergeInstance();
            }
            for (final FieldInfo info : reader.getFieldInfos()) {
                if (info.hasNorms()) {
                    checkNumericDocValues(info.name, reader.maxDoc(), normsReader.getNorms(info), new Bits.MatchAllBits(reader.maxDoc()));
                    final Status.FieldNormStatus fieldNormStatus = status;
                    ++fieldNormStatus.totFields;
                }
            }
            msg(infoStream, String.format(Locale.ROOT, "OK [%d fields] [took %.3f sec]", status.totFields, nsToSec(System.nanoTime() - startNS)));
        }
        catch (final Throwable e) {
            if (failFast) {
                IOUtils.reThrow(e);
            }
            msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
            status.error = e;
            if (infoStream != null) {
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }
    
    private static long getDocsFromTermRange(final String field, final int maxDoc, final TermsEnum termsEnum, final FixedBitSet docsSeen, final BytesRef minTerm, final BytesRef maxTerm, final boolean isIntersect) throws IOException {
        docsSeen.clear(0, docsSeen.length());
        long termCount = 0L;
        PostingsEnum postingsEnum = null;
        BytesRefBuilder lastTerm = null;
        while (true) {
            BytesRef term;
            if (isIntersect || termCount != 0L) {
                term = termsEnum.next();
            }
            else {
                term = termsEnum.term();
            }
            if (term == null) {
                if (!isIntersect) {
                    throw new RuntimeException("didn't see max term field=" + field + " term=" + maxTerm);
                }
                return termCount;
            }
            else {
                assert term.isValid();
                if (lastTerm == null) {
                    lastTerm = new BytesRefBuilder();
                    lastTerm.copyBytes(term);
                }
                else {
                    if (lastTerm.get().compareTo(term) >= 0) {
                        throw new RuntimeException("terms out of order: lastTerm=" + lastTerm.get() + " term=" + term);
                    }
                    lastTerm.copyBytes(term);
                }
                if (term.compareTo(minTerm) < 0) {
                    throw new RuntimeException("saw term before min term field=" + field + " term=" + minTerm);
                }
                if (!isIntersect) {
                    final int cmp = term.compareTo(maxTerm);
                    if (cmp == 0) {
                        return termCount;
                    }
                    if (cmp > 0) {
                        throw new RuntimeException("didn't see end term field=" + field + " term=" + maxTerm);
                    }
                }
                postingsEnum = termsEnum.postings(postingsEnum, 0);
                int lastDoc = -1;
                while (true) {
                    final int doc = postingsEnum.nextDoc();
                    if (doc == Integer.MAX_VALUE) {
                        ++termCount;
                        break;
                    }
                    if (doc <= lastDoc) {
                        throw new RuntimeException("term " + term + ": doc " + doc + " <= lastDoc " + lastDoc);
                    }
                    if (doc >= maxDoc) {
                        throw new RuntimeException("term " + term + ": doc " + doc + " >= maxDoc " + maxDoc);
                    }
                    docsSeen.set(doc);
                    lastDoc = doc;
                }
            }
        }
    }
    
    private static boolean checkSingleTermRange(final String field, final int maxDoc, final Terms terms, final BytesRef minTerm, final BytesRef maxTerm, final FixedBitSet normalDocs, final FixedBitSet intersectDocs) throws IOException {
        assert minTerm.compareTo(maxTerm) <= 0;
        final TermsEnum termsEnum = terms.iterator();
        final TermsEnum.SeekStatus status = termsEnum.seekCeil(minTerm);
        if (status != TermsEnum.SeekStatus.FOUND) {
            throw new RuntimeException("failed to seek to existing term field=" + field + " term=" + minTerm);
        }
        final long normalTermCount = getDocsFromTermRange(field, maxDoc, termsEnum, normalDocs, minTerm, maxTerm, false);
        final long intersectTermCount = getDocsFromTermRange(field, maxDoc, terms.intersect(new CompiledAutomaton(Automata.makeBinaryInterval(minTerm, true, maxTerm, false), true, false, Integer.MAX_VALUE, true), null), intersectDocs, minTerm, maxTerm, true);
        if (intersectTermCount > normalTermCount) {
            throw new RuntimeException("intersect returned too many terms: field=" + field + " intersectTermCount=" + intersectTermCount + " normalTermCount=" + normalTermCount);
        }
        if (!normalDocs.equals(intersectDocs)) {
            throw new RuntimeException("intersect visited different docs than straight terms enum: " + normalDocs.cardinality() + " for straight enum, vs " + intersectDocs.cardinality() + " for intersect, minTerm=" + minTerm + " maxTerm=" + maxTerm);
        }
        return intersectTermCount != normalTermCount;
    }
    
    private static void checkTermRanges(final String field, final int maxDoc, final Terms terms, final long numTerms) throws IOException {
        double currentInterval = (double)numTerms;
        final FixedBitSet normalDocs = new FixedBitSet(maxDoc);
        final FixedBitSet intersectDocs = new FixedBitSet(maxDoc);
        while (currentInterval >= 10.0) {
            final TermsEnum termsEnum = terms.iterator();
            long termCount = 0L;
            final Deque<BytesRef> termBounds = new LinkedList<BytesRef>();
            long lastTermAdded = Long.MIN_VALUE;
            BytesRefBuilder lastTerm = null;
            while (true) {
                final BytesRef term = termsEnum.next();
                if (term == null) {
                    if (lastTerm != null && !termBounds.isEmpty()) {
                        final BytesRef minTerm = termBounds.removeFirst();
                        final BytesRef maxTerm = lastTerm.get();
                        checkSingleTermRange(field, maxDoc, terms, minTerm, maxTerm, normalDocs, intersectDocs);
                    }
                    currentInterval *= 0.75;
                    break;
                }
                if (termCount >= lastTermAdded + currentInterval / 4.0) {
                    termBounds.add(BytesRef.deepCopyOf(term));
                    lastTermAdded = termCount;
                    if (termBounds.size() == 5) {
                        final BytesRef minTerm2 = termBounds.removeFirst();
                        final BytesRef maxTerm2 = termBounds.getLast();
                        checkSingleTermRange(field, maxDoc, terms, minTerm2, maxTerm2, normalDocs, intersectDocs);
                    }
                }
                ++termCount;
                if (lastTerm == null) {
                    lastTerm = new BytesRefBuilder();
                    lastTerm.copyBytes(term);
                }
                else {
                    if (lastTerm.get().compareTo(term) >= 0) {
                        throw new RuntimeException("terms out of order: lastTerm=" + lastTerm.get() + " term=" + term);
                    }
                    lastTerm.copyBytes(term);
                }
            }
        }
    }
    
    private static Status.TermIndexStatus checkFields(final Fields fields, final Bits liveDocs, final int maxDoc, final FieldInfos fieldInfos, final boolean doPrint, final boolean isVectors, final PrintStream infoStream, final boolean verbose) throws IOException {
        long startNS;
        if (doPrint) {
            startNS = System.nanoTime();
        }
        else {
            startNS = 0L;
        }
        final Status.TermIndexStatus status = new Status.TermIndexStatus();
        int computedFieldCount = 0;
        PostingsEnum postings = null;
        String lastField = null;
        for (final String field : fields) {
            if (lastField != null && field.compareTo(lastField) <= 0) {
                throw new RuntimeException("fields out of order: lastField=" + lastField + " field=" + field);
            }
            lastField = field;
            final FieldInfo fieldInfo = fieldInfos.fieldInfo(field);
            if (fieldInfo == null) {
                throw new RuntimeException("fieldsEnum inconsistent with fieldInfos, no fieldInfos for: " + field);
            }
            if (fieldInfo.getIndexOptions() == IndexOptions.NONE) {
                throw new RuntimeException("fieldsEnum inconsistent with fieldInfos, isIndexed == false for: " + field);
            }
            ++computedFieldCount;
            final Terms terms = fields.terms(field);
            if (terms == null) {
                continue;
            }
            final boolean hasFreqs = terms.hasFreqs();
            final boolean hasPositions = terms.hasPositions();
            final boolean hasPayloads = terms.hasPayloads();
            final boolean hasOffsets = terms.hasOffsets();
            BytesRef maxTerm;
            BytesRef minTerm;
            if (isVectors) {
                maxTerm = null;
                minTerm = null;
            }
            else {
                BytesRef bb = terms.getMin();
                if (bb != null) {
                    assert bb.isValid();
                    minTerm = BytesRef.deepCopyOf(bb);
                }
                else {
                    minTerm = null;
                }
                bb = terms.getMax();
                if (bb != null) {
                    assert bb.isValid();
                    maxTerm = BytesRef.deepCopyOf(bb);
                    if (minTerm == null) {
                        throw new RuntimeException("field \"" + field + "\" has null minTerm but non-null maxTerm");
                    }
                }
                else {
                    maxTerm = null;
                    if (minTerm != null) {
                        throw new RuntimeException("field \"" + field + "\" has non-null minTerm but null maxTerm");
                    }
                }
            }
            final boolean expectedHasFreqs = isVectors || fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS) >= 0;
            if (hasFreqs != expectedHasFreqs) {
                throw new RuntimeException("field \"" + field + "\" should have hasFreqs=" + expectedHasFreqs + " but got " + hasFreqs);
            }
            if (!hasFreqs && terms.getSumTotalTermFreq() != -1L) {
                throw new RuntimeException("field \"" + field + "\" hasFreqs is false, but Terms.getSumTotalTermFreq()=" + terms.getSumTotalTermFreq() + " (should be -1)");
            }
            if (!isVectors) {
                final boolean expectedHasPositions = fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
                if (hasPositions != expectedHasPositions) {
                    throw new RuntimeException("field \"" + field + "\" should have hasPositions=" + expectedHasPositions + " but got " + hasPositions);
                }
                final boolean expectedHasPayloads = fieldInfo.hasPayloads();
                if (hasPayloads != expectedHasPayloads) {
                    throw new RuntimeException("field \"" + field + "\" should have hasPayloads=" + expectedHasPayloads + " but got " + hasPayloads);
                }
                final boolean expectedHasOffsets = fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
                if (hasOffsets != expectedHasOffsets) {
                    throw new RuntimeException("field \"" + field + "\" should have hasOffsets=" + expectedHasOffsets + " but got " + hasOffsets);
                }
            }
            final TermsEnum termsEnum = terms.iterator();
            boolean hasOrd = true;
            final long termCountStart = status.delTermCount + status.termCount;
            BytesRefBuilder lastTerm = null;
            long sumTotalTermFreq = 0L;
            long sumDocFreq = 0L;
            final FixedBitSet visitedDocs = new FixedBitSet(maxDoc);
            while (true) {
                final BytesRef term = termsEnum.next();
                if (term == null) {
                    if (minTerm != null && status.termCount + status.delTermCount == 0L) {
                        throw new RuntimeException("field=\"" + field + "\": minTerm is non-null yet we saw no terms: " + minTerm);
                    }
                    final Terms fieldTerms = fields.terms(field);
                    if (fieldTerms != null) {
                        final long fieldTermCount = status.delTermCount + status.termCount - termCountStart;
                        final Object stats = fieldTerms.getStats();
                        assert stats != null;
                        if (status.blockTreeStats == null) {
                            status.blockTreeStats = new HashMap<String, Object>();
                        }
                        status.blockTreeStats.put(field, stats);
                        if (sumTotalTermFreq != 0L) {
                            final long v = fields.terms(field).getSumTotalTermFreq();
                            if (v != -1L && sumTotalTermFreq != v) {
                                throw new RuntimeException("sumTotalTermFreq for field " + field + "=" + v + " != recomputed sumTotalTermFreq=" + sumTotalTermFreq);
                            }
                        }
                        if (sumDocFreq != 0L) {
                            final long v = fields.terms(field).getSumDocFreq();
                            if (v != -1L && sumDocFreq != v) {
                                throw new RuntimeException("sumDocFreq for field " + field + "=" + v + " != recomputed sumDocFreq=" + sumDocFreq);
                            }
                        }
                        final int v2 = fieldTerms.getDocCount();
                        if (v2 != -1 && visitedDocs.cardinality() != v2) {
                            throw new RuntimeException("docCount for field " + field + "=" + v2 + " != recomputed docCount=" + visitedDocs.cardinality());
                        }
                        if (lastTerm != null) {
                            if (termsEnum.seekCeil(lastTerm.get()) != TermsEnum.SeekStatus.FOUND) {
                                throw new RuntimeException("seek to last term " + lastTerm.get() + " failed");
                            }
                            if (!termsEnum.term().equals(lastTerm.get())) {
                                throw new RuntimeException("seek to last term " + lastTerm.get() + " returned FOUND but seeked to the wrong term " + termsEnum.term());
                            }
                            final int expectedDocFreq = termsEnum.docFreq();
                            final PostingsEnum d = termsEnum.postings(null, 0);
                            int docFreq = 0;
                            while (d.nextDoc() != Integer.MAX_VALUE) {
                                ++docFreq;
                            }
                            if (docFreq != expectedDocFreq) {
                                throw new RuntimeException("docFreq for last term " + lastTerm.get() + "=" + expectedDocFreq + " != recomputed docFreq=" + docFreq);
                            }
                        }
                        long termCount = -1L;
                        if (fieldTermCount > 0L) {
                            termCount = fields.terms(field).size();
                            if (termCount != -1L && termCount != fieldTermCount) {
                                throw new RuntimeException("termCount mismatch " + termCount + " vs " + fieldTermCount);
                            }
                        }
                        if (hasOrd && status.termCount - termCountStart > 0L) {
                            final int seekCount = (int)Math.min(10000L, termCount);
                            if (seekCount > 0) {
                                final BytesRef[] seekTerms = new BytesRef[seekCount];
                                for (int i = seekCount - 1; i >= 0; --i) {
                                    final long ord = i * (termCount / seekCount);
                                    termsEnum.seekExact(ord);
                                    final long actualOrd = termsEnum.ord();
                                    if (actualOrd != ord) {
                                        throw new RuntimeException("seek to ord " + ord + " returned ord " + actualOrd);
                                    }
                                    seekTerms[i] = BytesRef.deepCopyOf(termsEnum.term());
                                }
                                for (int i = seekCount - 1; i >= 0; --i) {
                                    if (termsEnum.seekCeil(seekTerms[i]) != TermsEnum.SeekStatus.FOUND) {
                                        throw new RuntimeException("seek to existing term " + seekTerms[i] + " failed");
                                    }
                                    if (!termsEnum.term().equals(seekTerms[i])) {
                                        throw new RuntimeException("seek to existing term " + seekTerms[i] + " returned FOUND but seeked to the wrong term " + termsEnum.term());
                                    }
                                    postings = termsEnum.postings(postings, 0);
                                    if (postings == null) {
                                        throw new RuntimeException("null DocsEnum from to existing term " + seekTerms[i]);
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                else {
                    assert term.isValid();
                    if (lastTerm == null) {
                        lastTerm = new BytesRefBuilder();
                        lastTerm.copyBytes(term);
                    }
                    else {
                        if (lastTerm.get().compareTo(term) >= 0) {
                            throw new RuntimeException("terms out of order: lastTerm=" + lastTerm.get() + " term=" + term);
                        }
                        lastTerm.copyBytes(term);
                    }
                    if (!isVectors) {
                        if (minTerm == null) {
                            assert maxTerm == null;
                            throw new RuntimeException("field=\"" + field + "\": invalid term: term=" + term + ", minTerm=" + minTerm);
                        }
                        else {
                            if (term.compareTo(minTerm) < 0) {
                                throw new RuntimeException("field=\"" + field + "\": invalid term: term=" + term + ", minTerm=" + minTerm);
                            }
                            if (term.compareTo(maxTerm) > 0) {
                                throw new RuntimeException("field=\"" + field + "\": invalid term: term=" + term + ", maxTerm=" + maxTerm);
                            }
                        }
                    }
                    final int docFreq2 = termsEnum.docFreq();
                    if (docFreq2 <= 0) {
                        throw new RuntimeException("docfreq: " + docFreq2 + " is out of bounds");
                    }
                    sumDocFreq += docFreq2;
                    postings = termsEnum.postings(postings, 120);
                    if (!hasFreqs && termsEnum.totalTermFreq() != -1L) {
                        throw new RuntimeException("field \"" + field + "\" hasFreqs is false, but TermsEnum.totalTermFreq()=" + termsEnum.totalTermFreq() + " (should be -1)");
                    }
                    if (hasOrd) {
                        long ord2 = -1L;
                        try {
                            ord2 = termsEnum.ord();
                        }
                        catch (final UnsupportedOperationException uoe) {
                            hasOrd = false;
                        }
                        if (hasOrd) {
                            final long ordExpected = status.delTermCount + status.termCount - termCountStart;
                            if (ord2 != ordExpected) {
                                throw new RuntimeException("ord mismatch: TermsEnum has ord=" + ord2 + " vs actual=" + ordExpected);
                            }
                        }
                    }
                    int lastDoc = -1;
                    int docCount = 0;
                    boolean hasNonDeletedDocs = false;
                    long totalTermFreq = 0L;
                    while (true) {
                        final int doc = postings.nextDoc();
                        if (doc == Integer.MAX_VALUE) {
                            if (hasNonDeletedDocs) {
                                final Status.TermIndexStatus termIndexStatus = status;
                                ++termIndexStatus.termCount;
                            }
                            else {
                                final Status.TermIndexStatus termIndexStatus2 = status;
                                ++termIndexStatus2.delTermCount;
                            }
                            final long totalTermFreq2 = termsEnum.totalTermFreq();
                            final boolean hasTotalTermFreq = hasFreqs && totalTermFreq2 != -1L;
                            if (docCount != docFreq2) {
                                throw new RuntimeException("term " + term + " docFreq=" + docFreq2 + " != tot docs w/o deletions " + docCount);
                            }
                            if (hasTotalTermFreq) {
                                if (totalTermFreq2 <= 0L) {
                                    throw new RuntimeException("totalTermFreq: " + totalTermFreq2 + " is out of bounds");
                                }
                                sumTotalTermFreq += totalTermFreq;
                                if (totalTermFreq != totalTermFreq2) {
                                    throw new RuntimeException("term " + term + " totalTermFreq=" + totalTermFreq2 + " != recomputed totalTermFreq=" + totalTermFreq);
                                }
                            }
                            if (hasPositions) {
                                for (int idx = 0; idx < 7; ++idx) {
                                    final int skipDocID = (int)((idx + 1) * (long)maxDoc / 8L);
                                    postings = termsEnum.postings(postings, 120);
                                    final int docID = postings.advance(skipDocID);
                                    if (docID == Integer.MAX_VALUE) {
                                        break;
                                    }
                                    if (docID < skipDocID) {
                                        throw new RuntimeException("term " + term + ": advance(docID=" + skipDocID + ") returned docID=" + docID);
                                    }
                                    final int freq = postings.freq();
                                    if (freq <= 0) {
                                        throw new RuntimeException("termFreq " + freq + " is out of bounds");
                                    }
                                    int lastPosition = -1;
                                    int lastOffset = 0;
                                    for (int posUpto = 0; posUpto < freq; ++posUpto) {
                                        final int pos = postings.nextPosition();
                                        if (pos < 0) {
                                            throw new RuntimeException("position " + pos + " is out of bounds");
                                        }
                                        if (pos < lastPosition) {
                                            throw new RuntimeException("position " + pos + " is < lastPosition " + lastPosition);
                                        }
                                        lastPosition = pos;
                                        if (hasOffsets) {
                                            final int startOffset = postings.startOffset();
                                            final int endOffset = postings.endOffset();
                                            if (!isVectors) {
                                                if (startOffset < 0) {
                                                    throw new RuntimeException("term " + term + ": doc " + docID + ": pos " + pos + ": startOffset " + startOffset + " is out of bounds");
                                                }
                                                if (startOffset < lastOffset) {
                                                    throw new RuntimeException("term " + term + ": doc " + docID + ": pos " + pos + ": startOffset " + startOffset + " < lastStartOffset " + lastOffset);
                                                }
                                                if (endOffset < 0) {
                                                    throw new RuntimeException("term " + term + ": doc " + docID + ": pos " + pos + ": endOffset " + endOffset + " is out of bounds");
                                                }
                                                if (endOffset < startOffset) {
                                                    throw new RuntimeException("term " + term + ": doc " + docID + ": pos " + pos + ": endOffset " + endOffset + " < startOffset " + startOffset);
                                                }
                                            }
                                            lastOffset = startOffset;
                                        }
                                    }
                                    final int nextDocID = postings.nextDoc();
                                    if (nextDocID == Integer.MAX_VALUE) {
                                        break;
                                    }
                                    if (nextDocID <= docID) {
                                        throw new RuntimeException("term " + term + ": advance(docID=" + skipDocID + "), then .next() returned docID=" + nextDocID + " vs prev docID=" + docID);
                                    }
                                    if (isVectors) {
                                        break;
                                    }
                                }
                            }
                            else {
                                for (int idx = 0; idx < 7; ++idx) {
                                    final int skipDocID = (int)((idx + 1) * (long)maxDoc / 8L);
                                    postings = termsEnum.postings(postings, 0);
                                    final int docID = postings.advance(skipDocID);
                                    if (docID == Integer.MAX_VALUE) {
                                        break;
                                    }
                                    if (docID < skipDocID) {
                                        throw new RuntimeException("term " + term + ": advance(docID=" + skipDocID + ") returned docID=" + docID);
                                    }
                                    final int nextDocID2 = postings.nextDoc();
                                    if (nextDocID2 == Integer.MAX_VALUE) {
                                        break;
                                    }
                                    if (nextDocID2 <= docID) {
                                        throw new RuntimeException("term " + term + ": advance(docID=" + skipDocID + "), then .next() returned docID=" + nextDocID2 + " vs prev docID=" + docID);
                                    }
                                    if (isVectors) {
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        else {
                            visitedDocs.set(doc);
                            int freq2 = -1;
                            if (hasFreqs) {
                                freq2 = postings.freq();
                                if (freq2 <= 0) {
                                    throw new RuntimeException("term " + term + ": doc " + doc + ": freq " + freq2 + " is out of bounds");
                                }
                                totalTermFreq += freq2;
                            }
                            else if (postings.freq() != 1) {
                                throw new RuntimeException("term " + term + ": doc " + doc + ": freq " + freq2 + " != 1 when Terms.hasFreqs() is false");
                            }
                            if (liveDocs == null || liveDocs.get(doc)) {
                                hasNonDeletedDocs = true;
                                final Status.TermIndexStatus termIndexStatus3 = status;
                                ++termIndexStatus3.totFreq;
                                if (freq2 >= 0) {
                                    final Status.TermIndexStatus termIndexStatus4 = status;
                                    termIndexStatus4.totPos += freq2;
                                }
                            }
                            ++docCount;
                            if (doc <= lastDoc) {
                                throw new RuntimeException("term " + term + ": doc " + doc + " <= lastDoc " + lastDoc);
                            }
                            if (doc >= maxDoc) {
                                throw new RuntimeException("term " + term + ": doc " + doc + " >= maxDoc " + maxDoc);
                            }
                            lastDoc = doc;
                            int lastPos = -1;
                            int lastOffset2 = 0;
                            if (!hasPositions) {
                                continue;
                            }
                            for (int j = 0; j < freq2; ++j) {
                                final int pos2 = postings.nextPosition();
                                if (pos2 < 0) {
                                    throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos2 + " is out of bounds");
                                }
                                if (pos2 > 2147483519) {
                                    throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos2 + " > IndexWriter.MAX_POSITION=" + 2147483519);
                                }
                                if (pos2 < lastPos) {
                                    throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos2 + " < lastPos " + lastPos);
                                }
                                lastPos = pos2;
                                final BytesRef payload = postings.getPayload();
                                if (payload != null && !CheckIndex.$assertionsDisabled && !payload.isValid()) {
                                    throw new AssertionError();
                                }
                                if (payload != null && payload.length < 1) {
                                    throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos2 + " payload length is out of bounds " + payload.length);
                                }
                                if (hasOffsets) {
                                    final int startOffset2 = postings.startOffset();
                                    final int endOffset2 = postings.endOffset();
                                    if (!isVectors) {
                                        if (startOffset2 < 0) {
                                            throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos2 + ": startOffset " + startOffset2 + " is out of bounds");
                                        }
                                        if (startOffset2 < lastOffset2) {
                                            throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos2 + ": startOffset " + startOffset2 + " < lastStartOffset " + lastOffset2);
                                        }
                                        if (endOffset2 < 0) {
                                            throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos2 + ": endOffset " + endOffset2 + " is out of bounds");
                                        }
                                        if (endOffset2 < startOffset2) {
                                            throw new RuntimeException("term " + term + ": doc " + doc + ": pos " + pos2 + ": endOffset " + endOffset2 + " < startOffset " + startOffset2);
                                        }
                                    }
                                    lastOffset2 = startOffset2;
                                }
                            }
                        }
                    }
                }
            }
        }
        final int fieldCount = fields.size();
        if (fieldCount != -1) {
            if (fieldCount < 0) {
                throw new RuntimeException("invalid fieldCount: " + fieldCount);
            }
            if (fieldCount != computedFieldCount) {
                throw new RuntimeException("fieldCount mismatch " + fieldCount + " vs recomputed field count " + computedFieldCount);
            }
        }
        if (doPrint) {
            msg(infoStream, String.format(Locale.ROOT, "OK [%d terms; %d terms/docs pairs; %d tokens] [took %.3f sec]", status.termCount, status.totFreq, status.totPos, nsToSec(System.nanoTime() - startNS)));
        }
        if (verbose && status.blockTreeStats != null && infoStream != null && status.termCount > 0L) {
            for (final Map.Entry<String, Object> ent : status.blockTreeStats.entrySet()) {
                infoStream.println("      field \"" + ent.getKey() + "\":");
                infoStream.println("      " + ent.getValue().toString().replace("\n", "\n      "));
            }
        }
        return status;
    }
    
    public static Status.TermIndexStatus testPostings(final CodecReader reader, final PrintStream infoStream) throws IOException {
        return testPostings(reader, infoStream, false, false);
    }
    
    public static Status.TermIndexStatus testPostings(final CodecReader reader, final PrintStream infoStream, final boolean verbose, final boolean failFast) throws IOException {
        final int maxDoc = reader.maxDoc();
        Status.TermIndexStatus status;
        try {
            if (infoStream != null) {
                infoStream.print("    test: terms, freq, prox...");
            }
            final Fields fields = reader.getPostingsReader().getMergeInstance();
            final FieldInfos fieldInfos = reader.getFieldInfos();
            status = checkFields(fields, reader.getLiveDocs(), maxDoc, fieldInfos, true, false, infoStream, verbose);
        }
        catch (final Throwable e) {
            if (failFast) {
                IOUtils.reThrow(e);
            }
            msg(infoStream, "ERROR: " + e);
            status = new Status.TermIndexStatus();
            status.error = e;
            if (infoStream != null) {
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }
    
    public static Status.StoredFieldStatus testStoredFields(final CodecReader reader, final PrintStream infoStream, final boolean failFast) throws IOException {
        final long startNS = System.nanoTime();
        final Status.StoredFieldStatus status = new Status.StoredFieldStatus();
        try {
            if (infoStream != null) {
                infoStream.print("    test: stored fields.......");
            }
            final Bits liveDocs = reader.getLiveDocs();
            final StoredFieldsReader storedFields = reader.getFieldsReader().getMergeInstance();
            for (int j = 0; j < reader.maxDoc(); ++j) {
                final DocumentStoredFieldVisitor visitor = new DocumentStoredFieldVisitor();
                storedFields.visitDocument(j, visitor);
                final Document doc = visitor.getDocument();
                if (liveDocs == null || liveDocs.get(j)) {
                    final Status.StoredFieldStatus storedFieldStatus = status;
                    ++storedFieldStatus.docCount;
                    final Status.StoredFieldStatus storedFieldStatus2 = status;
                    storedFieldStatus2.totFields += doc.getFields().size();
                }
            }
            if (status.docCount != reader.numDocs()) {
                throw new RuntimeException("docCount=" + status.docCount + " but saw " + status.docCount + " undeleted docs");
            }
            msg(infoStream, String.format(Locale.ROOT, "OK [%d total field count; avg %.1f fields per doc] [took %.3f sec]", status.totFields, status.totFields / (float)status.docCount, nsToSec(System.nanoTime() - startNS)));
        }
        catch (final Throwable e) {
            if (failFast) {
                IOUtils.reThrow(e);
            }
            msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
            status.error = e;
            if (infoStream != null) {
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }
    
    public static Status.DocValuesStatus testDocValues(final CodecReader reader, final PrintStream infoStream, final boolean failFast) throws IOException {
        final long startNS = System.nanoTime();
        final Status.DocValuesStatus status = new Status.DocValuesStatus();
        try {
            if (infoStream != null) {
                infoStream.print("    test: docvalues...........");
            }
            DocValuesProducer dvReader = reader.getDocValuesReader();
            if (dvReader != null) {
                dvReader = dvReader.getMergeInstance();
            }
            for (final FieldInfo fieldInfo : reader.getFieldInfos()) {
                if (fieldInfo.getDocValuesType() != DocValuesType.NONE) {
                    final Status.DocValuesStatus docValuesStatus = status;
                    ++docValuesStatus.totalValueFields;
                    checkDocValues(fieldInfo, dvReader, reader.maxDoc(), infoStream, status);
                }
            }
            msg(infoStream, String.format(Locale.ROOT, "OK [%d docvalues fields; %d BINARY; %d NUMERIC; %d SORTED; %d SORTED_NUMERIC; %d SORTED_SET] [took %.3f sec]", status.totalValueFields, status.totalBinaryFields, status.totalNumericFields, status.totalSortedFields, status.totalSortedNumericFields, status.totalSortedSetFields, nsToSec(System.nanoTime() - startNS)));
        }
        catch (final Throwable e) {
            if (failFast) {
                IOUtils.reThrow(e);
            }
            msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
            status.error = e;
            if (infoStream != null) {
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }
    
    private static void checkBinaryDocValues(final String fieldName, final int maxDoc, final BinaryDocValues dv, final Bits docsWithField) {
        for (int i = 0; i < maxDoc; ++i) {
            final BytesRef term = dv.get(i);
            assert term.isValid();
            if (!docsWithField.get(i) && term.length > 0) {
                throw new RuntimeException("dv for field: " + fieldName + " is missing but has value=" + term + " for doc: " + i);
            }
        }
    }
    
    private static void checkSortedDocValues(final String fieldName, final int maxDoc, final SortedDocValues dv, final Bits docsWithField) {
        checkBinaryDocValues(fieldName, maxDoc, dv, docsWithField);
        final int maxOrd = dv.getValueCount() - 1;
        final FixedBitSet seenOrds = new FixedBitSet(dv.getValueCount());
        int maxOrd2 = -1;
        for (int i = 0; i < maxDoc; ++i) {
            final int ord = dv.getOrd(i);
            if (ord == -1) {
                if (docsWithField.get(i)) {
                    throw new RuntimeException("dv for field: " + fieldName + " has -1 ord but is not marked missing for doc: " + i);
                }
            }
            else {
                if (ord < -1 || ord > maxOrd) {
                    throw new RuntimeException("ord out of bounds: " + ord);
                }
                if (!docsWithField.get(i)) {
                    throw new RuntimeException("dv for field: " + fieldName + " is missing but has ord=" + ord + " for doc: " + i);
                }
                maxOrd2 = Math.max(maxOrd2, ord);
                seenOrds.set(ord);
            }
        }
        if (maxOrd != maxOrd2) {
            throw new RuntimeException("dv for field: " + fieldName + " reports wrong maxOrd=" + maxOrd + " but this is not the case: " + maxOrd2);
        }
        if (seenOrds.cardinality() != dv.getValueCount()) {
            throw new RuntimeException("dv for field: " + fieldName + " has holes in its ords, valueCount=" + dv.getValueCount() + " but only used: " + seenOrds.cardinality());
        }
        BytesRef lastValue = null;
        for (int j = 0; j <= maxOrd; ++j) {
            final BytesRef term = dv.lookupOrd(j);
            assert term.isValid();
            if (lastValue != null && term.compareTo(lastValue) <= 0) {
                throw new RuntimeException("dv for field: " + fieldName + " has ords out of order: " + lastValue + " >=" + term);
            }
            lastValue = BytesRef.deepCopyOf(term);
        }
    }
    
    private static void checkSortedSetDocValues(final String fieldName, final int maxDoc, final SortedSetDocValues dv, final Bits docsWithField) {
        final long maxOrd = dv.getValueCount() - 1L;
        final LongBitSet seenOrds = new LongBitSet(dv.getValueCount());
        long maxOrd2 = -1L;
        for (int i = 0; i < maxDoc; ++i) {
            dv.setDocument(i);
            long lastOrd = -1L;
            if (docsWithField.get(i)) {
                int ordCount = 0;
                long ord;
                while ((ord = dv.nextOrd()) != -1L) {
                    if (ord <= lastOrd) {
                        throw new RuntimeException("ords out of order: " + ord + " <= " + lastOrd + " for doc: " + i);
                    }
                    if (ord < 0L || ord > maxOrd) {
                        throw new RuntimeException("ord out of bounds: " + ord);
                    }
                    if (dv instanceof RandomAccessOrds) {
                        final long ord2 = ((RandomAccessOrds)dv).ordAt(ordCount);
                        if (ord != ord2) {
                            throw new RuntimeException("ordAt(" + ordCount + ") inconsistent, expected=" + ord + ",got=" + ord2 + " for doc: " + i);
                        }
                    }
                    lastOrd = ord;
                    maxOrd2 = Math.max(maxOrd2, ord);
                    seenOrds.set(ord);
                    ++ordCount;
                }
                if (ordCount == 0) {
                    throw new RuntimeException("dv for field: " + fieldName + " has no ordinals but is not marked missing for doc: " + i);
                }
                if (dv instanceof RandomAccessOrds) {
                    final long ordCount2 = ((RandomAccessOrds)dv).cardinality();
                    if (ordCount != ordCount2) {
                        throw new RuntimeException("cardinality inconsistent, expected=" + ordCount + ",got=" + ordCount2 + " for doc: " + i);
                    }
                }
            }
            else {
                final long o = dv.nextOrd();
                if (o != -1L) {
                    throw new RuntimeException("dv for field: " + fieldName + " is marked missing but has ord=" + o + " for doc: " + i);
                }
                if (dv instanceof RandomAccessOrds) {
                    final long ordCount3 = ((RandomAccessOrds)dv).cardinality();
                    if (ordCount3 != 0L) {
                        throw new RuntimeException("dv for field: " + fieldName + " is marked missing but has cardinality " + ordCount3 + " for doc: " + i);
                    }
                }
            }
        }
        if (maxOrd != maxOrd2) {
            throw new RuntimeException("dv for field: " + fieldName + " reports wrong maxOrd=" + maxOrd + " but this is not the case: " + maxOrd2);
        }
        if (seenOrds.cardinality() != dv.getValueCount()) {
            throw new RuntimeException("dv for field: " + fieldName + " has holes in its ords, valueCount=" + dv.getValueCount() + " but only used: " + seenOrds.cardinality());
        }
        BytesRef lastValue = null;
        for (long j = 0L; j <= maxOrd; ++j) {
            final BytesRef term = dv.lookupOrd(j);
            assert term.isValid();
            if (lastValue != null && term.compareTo(lastValue) <= 0) {
                throw new RuntimeException("dv for field: " + fieldName + " has ords out of order: " + lastValue + " >=" + term);
            }
            lastValue = BytesRef.deepCopyOf(term);
        }
    }
    
    private static void checkSortedNumericDocValues(final String fieldName, final int maxDoc, final SortedNumericDocValues ndv, final Bits docsWithField) {
        for (int i = 0; i < maxDoc; ++i) {
            ndv.setDocument(i);
            final int count = ndv.count();
            if (docsWithField.get(i)) {
                if (count == 0) {
                    throw new RuntimeException("dv for field: " + fieldName + " is not marked missing but has zero count for doc: " + i);
                }
                long previous = Long.MIN_VALUE;
                for (int j = 0; j < count; ++j) {
                    final long value = ndv.valueAt(j);
                    if (value < previous) {
                        throw new RuntimeException("values out of order: " + value + " < " + previous + " for doc: " + i);
                    }
                    previous = value;
                }
            }
            else if (count != 0) {
                throw new RuntimeException("dv for field: " + fieldName + " is marked missing but has count=" + count + " for doc: " + i);
            }
        }
    }
    
    private static void checkNumericDocValues(final String fieldName, final int maxDoc, final NumericDocValues ndv, final Bits docsWithField) {
        for (int i = 0; i < maxDoc; ++i) {
            final long value = ndv.get(i);
            if (!docsWithField.get(i) && value != 0L) {
                throw new RuntimeException("dv for field: " + fieldName + " is marked missing but has value=" + value + " for doc: " + i);
            }
        }
    }
    
    private static void checkDocValues(final FieldInfo fi, final DocValuesProducer dvReader, final int maxDoc, final PrintStream infoStream, final Status.DocValuesStatus status) throws Exception {
        final Bits docsWithField = dvReader.getDocsWithField(fi);
        if (docsWithField == null) {
            throw new RuntimeException(fi.name + " docsWithField does not exist");
        }
        if (docsWithField.length() != maxDoc) {
            throw new RuntimeException(fi.name + " docsWithField has incorrect length: " + docsWithField.length() + ",expected: " + maxDoc);
        }
        switch (fi.getDocValuesType()) {
            case SORTED: {
                ++status.totalSortedFields;
                checkSortedDocValues(fi.name, maxDoc, dvReader.getSorted(fi), docsWithField);
                break;
            }
            case SORTED_NUMERIC: {
                ++status.totalSortedNumericFields;
                checkSortedNumericDocValues(fi.name, maxDoc, dvReader.getSortedNumeric(fi), docsWithField);
                break;
            }
            case SORTED_SET: {
                ++status.totalSortedSetFields;
                checkSortedSetDocValues(fi.name, maxDoc, dvReader.getSortedSet(fi), docsWithField);
                break;
            }
            case BINARY: {
                ++status.totalBinaryFields;
                checkBinaryDocValues(fi.name, maxDoc, dvReader.getBinary(fi), docsWithField);
                break;
            }
            case NUMERIC: {
                ++status.totalNumericFields;
                checkNumericDocValues(fi.name, maxDoc, dvReader.getNumeric(fi), docsWithField);
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    public static Status.TermVectorStatus testTermVectors(final CodecReader reader, final PrintStream infoStream) throws IOException {
        return testTermVectors(reader, infoStream, false, false, false);
    }
    
    public static Status.TermVectorStatus testTermVectors(final CodecReader reader, final PrintStream infoStream, final boolean verbose, final boolean crossCheckTermVectors, final boolean failFast) throws IOException {
        final long startNS = System.nanoTime();
        final Status.TermVectorStatus status = new Status.TermVectorStatus();
        final FieldInfos fieldInfos = reader.getFieldInfos();
        try {
            if (infoStream != null) {
                infoStream.print("    test: term vectors........");
            }
            PostingsEnum postings = null;
            PostingsEnum postingsDocs = null;
            final Bits liveDocs = reader.getLiveDocs();
            Fields postingsFields;
            if (crossCheckTermVectors) {
                postingsFields = reader.getPostingsReader().getMergeInstance();
            }
            else {
                postingsFields = null;
            }
            TermVectorsReader vectorsReader = reader.getTermVectorsReader();
            if (vectorsReader != null) {
                vectorsReader = vectorsReader.getMergeInstance();
                for (int j = 0; j < reader.maxDoc(); ++j) {
                    final Fields tfv = vectorsReader.get(j);
                    if (tfv != null) {
                        checkFields(tfv, null, 1, fieldInfos, false, true, infoStream, verbose);
                        final boolean doStats = liveDocs == null || liveDocs.get(j);
                        if (doStats) {
                            final Status.TermVectorStatus termVectorStatus = status;
                            ++termVectorStatus.docCount;
                        }
                        for (final String field : tfv) {
                            if (doStats) {
                                final Status.TermVectorStatus termVectorStatus2 = status;
                                ++termVectorStatus2.totVectors;
                            }
                            final FieldInfo fieldInfo = fieldInfos.fieldInfo(field);
                            if (!fieldInfo.hasVectors()) {
                                throw new RuntimeException("docID=" + j + " has term vectors for field=" + field + " but FieldInfo has storeTermVector=false");
                            }
                            if (!crossCheckTermVectors) {
                                continue;
                            }
                            final Terms terms = tfv.terms(field);
                            final TermsEnum termsEnum = terms.iterator();
                            final boolean postingsHasFreq = fieldInfo.getIndexOptions().compareTo(IndexOptions.DOCS_AND_FREQS) >= 0;
                            final boolean postingsHasPayload = fieldInfo.hasPayloads();
                            final boolean vectorsHasPayload = terms.hasPayloads();
                            final Terms postingsTerms = postingsFields.terms(field);
                            if (postingsTerms == null) {
                                throw new RuntimeException("vector field=" + field + " does not exist in postings; doc=" + j);
                            }
                            final TermsEnum postingsTermsEnum = postingsTerms.iterator();
                            final boolean hasProx = terms.hasOffsets() || terms.hasPositions();
                            BytesRef term = null;
                            while ((term = termsEnum.next()) != null) {
                                postings = termsEnum.postings(postings, 120);
                                assert postings != null;
                                if (!postingsTermsEnum.seekExact(term)) {
                                    throw new RuntimeException("vector term=" + term + " field=" + field + " does not exist in postings; doc=" + j);
                                }
                                postingsDocs = postingsTermsEnum.postings(postingsDocs, 120);
                                assert postingsDocs != null;
                                final int advanceDoc = postingsDocs.advance(j);
                                if (advanceDoc != j) {
                                    throw new RuntimeException("vector term=" + term + " field=" + field + ": doc=" + j + " was not found in postings (got: " + advanceDoc + ")");
                                }
                                final int doc = postings.nextDoc();
                                if (doc != 0) {
                                    throw new RuntimeException("vector for doc " + j + " didn't return docID=0: got docID=" + doc);
                                }
                                if (!postingsHasFreq) {
                                    continue;
                                }
                                final int tf = postings.freq();
                                if (postingsHasFreq && postingsDocs.freq() != tf) {
                                    throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + ": freq=" + tf + " differs from postings freq=" + postingsDocs.freq());
                                }
                                if (!hasProx) {
                                    continue;
                                }
                                for (int i = 0; i < tf; ++i) {
                                    final int pos = postings.nextPosition();
                                    if (postingsTerms.hasPositions()) {
                                        final int postingsPos = postingsDocs.nextPosition();
                                        if (terms.hasPositions() && pos != postingsPos) {
                                            throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + ": pos=" + pos + " differs from postings pos=" + postingsPos);
                                        }
                                    }
                                    final int startOffset = postings.startOffset();
                                    final int endOffset = postings.endOffset();
                                    if (startOffset != -1 && endOffset != -1 && postingsTerms.hasOffsets()) {
                                        final int postingsStartOffset = postingsDocs.startOffset();
                                        final int postingsEndOffset = postingsDocs.endOffset();
                                        if (startOffset != postingsStartOffset) {
                                            throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + ": startOffset=" + startOffset + " differs from postings startOffset=" + postingsStartOffset);
                                        }
                                        if (endOffset != postingsEndOffset) {
                                            throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + ": endOffset=" + endOffset + " differs from postings endOffset=" + postingsEndOffset);
                                        }
                                    }
                                    final BytesRef payload = postings.getPayload();
                                    if (payload != null && !CheckIndex.$assertionsDisabled && !vectorsHasPayload) {
                                        throw new AssertionError();
                                    }
                                    if (postingsHasPayload && vectorsHasPayload) {
                                        if (payload == null) {
                                            if (postingsDocs.getPayload() != null) {
                                                throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + " has no payload but postings does: " + postingsDocs.getPayload());
                                            }
                                        }
                                        else {
                                            if (postingsDocs.getPayload() == null) {
                                                throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + " has payload=" + payload + " but postings does not.");
                                            }
                                            final BytesRef postingsPayload = postingsDocs.getPayload();
                                            if (!payload.equals(postingsPayload)) {
                                                throw new RuntimeException("vector term=" + term + " field=" + field + " doc=" + j + " has payload=" + payload + " but differs from postings payload=" + postingsPayload);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final float vectorAvg = (status.docCount == 0) ? 0.0f : (status.totVectors / (float)status.docCount);
            msg(infoStream, String.format(Locale.ROOT, "OK [%d total term vector count; avg %.1f term/freq vector fields per doc] [took %.3f sec]", status.totVectors, vectorAvg, nsToSec(System.nanoTime() - startNS)));
        }
        catch (final Throwable e) {
            if (failFast) {
                IOUtils.reThrow(e);
            }
            msg(infoStream, "ERROR [" + String.valueOf(e.getMessage()) + "]");
            status.error = e;
            if (infoStream != null) {
                e.printStackTrace(infoStream);
            }
        }
        return status;
    }
    
    public void exorciseIndex(final Status result) throws IOException {
        this.ensureOpen();
        if (result.partial) {
            throw new IllegalArgumentException("can only exorcise an index that was fully checked (this status checked a subset of segments)");
        }
        result.newSegments.changed();
        result.newSegments.commit(result.dir);
    }
    
    private static boolean testAsserts() {
        return CheckIndex.assertsOn = true;
    }
    
    public static boolean assertsOn() {
        assert testAsserts();
        return CheckIndex.assertsOn;
    }
    
    public static void main(final String[] args) throws IOException, InterruptedException {
        final int exitCode = doMain(args);
        System.exit(exitCode);
    }
    
    @SuppressForbidden(reason = "System.out required: command line tool")
    private static int doMain(final String[] args) throws IOException, InterruptedException {
        Options opts;
        try {
            opts = parseOptions(args);
        }
        catch (final IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return 1;
        }
        if (!assertsOn()) {
            System.out.println("\nNOTE: testing will be more thorough if you run java with '-ea:org.apache.lucene...', so assertions are enabled");
        }
        System.out.println("\nOpening index @ " + opts.indexPath + "\n");
        Directory directory = null;
        final Path path = Paths.get(opts.indexPath, new String[0]);
        try {
            if (opts.dirImpl == null) {
                directory = FSDirectory.open(path);
            }
            else {
                directory = CommandLineUtil.newFSDirectory(opts.dirImpl, path);
            }
        }
        catch (final Throwable t) {
            System.out.println("ERROR: could not open directory \"" + opts.indexPath + "\"; exiting");
            t.printStackTrace(System.out);
            return 1;
        }
        try (final Directory dir = directory;
             final CheckIndex checker = new CheckIndex(dir)) {
            opts.out = System.out;
            return checker.doCheck(opts);
        }
    }
    
    public static Options parseOptions(final String[] args) {
        final Options opts = new Options();
        for (int i = 0; i < args.length; ++i) {
            final String arg = args[i];
            if ("-fast".equals(arg)) {
                opts.doChecksumsOnly = true;
            }
            else if ("-exorcise".equals(arg)) {
                opts.doExorcise = true;
            }
            else if ("-crossCheckTermVectors".equals(arg)) {
                opts.doCrossCheckTermVectors = true;
            }
            else if (arg.equals("-verbose")) {
                opts.verbose = true;
            }
            else if (arg.equals("-segment")) {
                if (i == args.length - 1) {
                    throw new IllegalArgumentException("ERROR: missing name for -segment option");
                }
                ++i;
                opts.onlySegments.add(args[i]);
            }
            else if ("-dir-impl".equals(arg)) {
                if (i == args.length - 1) {
                    throw new IllegalArgumentException("ERROR: missing value for -dir-impl option");
                }
                ++i;
                opts.dirImpl = args[i];
            }
            else {
                if (opts.indexPath != null) {
                    throw new IllegalArgumentException("ERROR: unexpected extra argument '" + args[i] + "'");
                }
                opts.indexPath = args[i];
            }
        }
        if (opts.indexPath == null) {
            throw new IllegalArgumentException("\nERROR: index path not specified\nUsage: java org.apache.lucene.index.CheckIndex pathToIndex [-exorcise] [-crossCheckTermVectors] [-segment X] [-segment Y] [-dir-impl X]\n\n  -exorcise: actually write a new segments_N file, removing any problematic segments\n  -fast: just verify file checksums, omitting logical integrity checks\n  -crossCheckTermVectors: verifies that term vectors match postings; THIS IS VERY SLOW!\n  -codec X: when exorcising, codec to write the new segments_N file with\n  -verbose: print additional details\n  -segment X: only check the specified segments.  This can be specified multiple\n              times, to check more than one segment, eg '-segment _2 -segment _a'.\n              You can't use this with the -exorcise option\n  -dir-impl X: use a specific " + FSDirectory.class.getSimpleName() + " implementation. " + "If no package is specified the " + FSDirectory.class.getPackage().getName() + " package will be used.\n" + "\n" + "**WARNING**: -exorcise *LOSES DATA*. This should only be used on an emergency basis as it will cause\n" + "documents (perhaps many) to be permanently removed from the index.  Always make\n" + "a backup copy of your index before running this!  Do not run this tool on an index\n" + "that is actively being written to.  You have been warned!\n" + "\n" + "Run without -exorcise, this tool will open the index, report version information\n" + "and report any exceptions it hits and what action it would take if -exorcise were\n" + "specified.  With -exorcise, this tool will remove any segments that have issues and\n" + "write a new segments_N file.  This means all documents contained in the affected\n" + "segments will be removed.\n" + "\n" + "This tool exits with exit code 1 if the index cannot be opened or has any\n" + "corruption, else 0.\n");
        }
        if (opts.onlySegments.size() == 0) {
            opts.onlySegments = null;
        }
        else if (opts.doExorcise) {
            throw new IllegalArgumentException("ERROR: cannot specify both -exorcise and -segment");
        }
        if (opts.doChecksumsOnly && opts.doCrossCheckTermVectors) {
            throw new IllegalArgumentException("ERROR: cannot specify both -fast and -crossCheckTermVectors");
        }
        return opts;
    }
    
    public int doCheck(final Options opts) throws IOException, InterruptedException {
        this.setCrossCheckTermVectors(opts.doCrossCheckTermVectors);
        this.setChecksumsOnly(opts.doChecksumsOnly);
        this.setInfoStream(opts.out, opts.verbose);
        final Status result = this.checkIndex(opts.onlySegments);
        if (result.missingSegments) {
            return 1;
        }
        if (!result.clean) {
            if (!opts.doExorcise) {
                opts.out.println("WARNING: would write new segments file, and " + result.totLoseDocCount + " documents would be lost, if -exorcise were specified\n");
            }
            else {
                opts.out.println("WARNING: " + result.totLoseDocCount + " documents will be lost\n");
                opts.out.println("NOTE: will write new segments file in 5 seconds; this will remove " + result.totLoseDocCount + " docs from the index. YOU WILL LOSE DATA. THIS IS YOUR LAST CHANCE TO CTRL+C!");
                for (int s = 0; s < 5; ++s) {
                    Thread.sleep(1000L);
                    opts.out.println("  " + (5 - s) + "...");
                }
                opts.out.println("Writing...");
                this.exorciseIndex(result);
                opts.out.println("OK");
                opts.out.println("Wrote new segments file \"" + result.newSegments.getSegmentsFileName() + "\"");
            }
        }
        opts.out.println("");
        if (result.clean) {
            return 0;
        }
        return 1;
    }
    
    private static double nsToSec(final long ns) {
        return ns / 1.0E9;
    }
    
    public static class Status
    {
        public boolean clean;
        public boolean missingSegments;
        public boolean cantOpenSegments;
        public boolean missingSegmentVersion;
        public String segmentsFileName;
        public int numSegments;
        public List<String> segmentsChecked;
        public boolean toolOutOfDate;
        public List<SegmentInfoStatus> segmentInfos;
        public Directory dir;
        SegmentInfos newSegments;
        public int totLoseDocCount;
        public int numBadSegments;
        public boolean partial;
        public int maxSegmentName;
        public boolean validCounter;
        public Map<String, String> userData;
        
        Status() {
            this.segmentsChecked = new ArrayList<String>();
            this.segmentInfos = new ArrayList<SegmentInfoStatus>();
        }
        
        public static class SegmentInfoStatus
        {
            public String name;
            public Codec codec;
            public int maxDoc;
            public boolean compound;
            public int numFiles;
            public double sizeMB;
            public boolean hasDeletions;
            public long deletionsGen;
            public boolean openReaderPassed;
            public Map<String, String> diagnostics;
            public LiveDocStatus liveDocStatus;
            public FieldInfoStatus fieldInfoStatus;
            public FieldNormStatus fieldNormStatus;
            public TermIndexStatus termIndexStatus;
            public StoredFieldStatus storedFieldStatus;
            public TermVectorStatus termVectorStatus;
            public DocValuesStatus docValuesStatus;
            public Version version;
            
            SegmentInfoStatus() {
            }
        }
        
        public static final class LiveDocStatus
        {
            public int numDeleted;
            public Throwable error;
            
            private LiveDocStatus() {
                this.error = null;
            }
        }
        
        public static final class FieldInfoStatus
        {
            public long totFields;
            public Throwable error;
            
            private FieldInfoStatus() {
                this.totFields = 0L;
                this.error = null;
            }
        }
        
        public static final class FieldNormStatus
        {
            public long totFields;
            public Throwable error;
            
            private FieldNormStatus() {
                this.totFields = 0L;
                this.error = null;
            }
        }
        
        public static final class TermIndexStatus
        {
            public long termCount;
            public long delTermCount;
            public long totFreq;
            public long totPos;
            public Throwable error;
            public Map<String, Object> blockTreeStats;
            
            TermIndexStatus() {
                this.termCount = 0L;
                this.delTermCount = 0L;
                this.totFreq = 0L;
                this.totPos = 0L;
                this.error = null;
                this.blockTreeStats = null;
            }
        }
        
        public static final class StoredFieldStatus
        {
            public int docCount;
            public long totFields;
            public Throwable error;
            
            StoredFieldStatus() {
                this.docCount = 0;
                this.totFields = 0L;
                this.error = null;
            }
        }
        
        public static final class TermVectorStatus
        {
            public int docCount;
            public long totVectors;
            public Throwable error;
            
            TermVectorStatus() {
                this.docCount = 0;
                this.totVectors = 0L;
                this.error = null;
            }
        }
        
        public static final class DocValuesStatus
        {
            public long totalValueFields;
            public long totalNumericFields;
            public long totalBinaryFields;
            public long totalSortedFields;
            public long totalSortedNumericFields;
            public long totalSortedSetFields;
            public Throwable error;
            
            DocValuesStatus() {
                this.error = null;
            }
        }
    }
    
    public static class Options
    {
        boolean doExorcise;
        boolean doCrossCheckTermVectors;
        boolean verbose;
        boolean doChecksumsOnly;
        List<String> onlySegments;
        String indexPath;
        String dirImpl;
        PrintStream out;
        
        public Options() {
            this.doExorcise = false;
            this.doCrossCheckTermVectors = false;
            this.verbose = false;
            this.doChecksumsOnly = false;
            this.onlySegments = new ArrayList<String>();
            this.indexPath = null;
            this.dirImpl = null;
            this.out = null;
        }
        
        public String getDirImpl() {
            return this.dirImpl;
        }
        
        public String getIndexPath() {
            return this.indexPath;
        }
        
        public void setOut(final PrintStream out) {
            this.out = out;
        }
    }
}
