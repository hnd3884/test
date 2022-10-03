package org.apache.poi.xssf.model;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentList;
import java.util.HashMap;
import org.apache.poi.util.Removal;
import java.util.TreeMap;
import java.util.Iterator;
import com.microsoft.schemas.vml.CTShape;
import org.apache.poi.xssf.usermodel.XSSFComment;
import java.io.OutputStream;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CommentsDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.apache.poi.ss.util.CellAddress;
import java.util.Map;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComments;
import org.apache.poi.util.Internal;
import org.apache.poi.ooxml.POIXMLDocumentPart;

@Internal
public class CommentsTable extends POIXMLDocumentPart implements Comments
{
    public static final String DEFAULT_AUTHOR = "";
    public static final int DEFAULT_AUTHOR_ID = 0;
    private CTComments comments;
    private Map<CellAddress, CTComment> commentRefs;
    
    public CommentsTable() {
        (this.comments = CTComments.Factory.newInstance()).addNewCommentList();
        this.comments.addNewAuthors().addAuthor("");
    }
    
    public CommentsTable(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            final CommentsDocument doc = CommentsDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.comments = doc.getComments();
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        final CommentsDocument doc = CommentsDocument.Factory.newInstance();
        doc.setComments(this.comments);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.writeTo(out);
        out.close();
    }
    
    public void referenceUpdated(final CellAddress oldReference, final CTComment comment) {
        if (this.commentRefs != null) {
            this.commentRefs.remove(oldReference);
            this.commentRefs.put(new CellAddress(comment.getRef()), comment);
        }
    }
    
    @Override
    public int getNumberOfComments() {
        return this.comments.getCommentList().sizeOfCommentArray();
    }
    
    @Override
    public int getNumberOfAuthors() {
        return this.comments.getAuthors().sizeOfAuthorArray();
    }
    
    @Override
    public String getAuthor(final long authorId) {
        return this.comments.getAuthors().getAuthorArray(Math.toIntExact(authorId));
    }
    
    @Override
    public int findAuthor(final String author) {
        final String[] authorArray = this.comments.getAuthors().getAuthorArray();
        for (int i = 0; i < authorArray.length; ++i) {
            if (authorArray[i].equals(author)) {
                return i;
            }
        }
        return this.addNewAuthor(author);
    }
    
    @Override
    public XSSFComment findCellComment(final CellAddress cellAddress) {
        final CTComment ct = this.getCTComment(cellAddress);
        return (ct == null) ? null : new XSSFComment(this, ct, null);
    }
    
    @Internal
    public CTComment getCTComment(final CellAddress cellRef) {
        this.prepareCTCommentCache();
        return this.commentRefs.get(cellRef);
    }
    
    @Override
    public Iterator<CellAddress> getCellAddresses() {
        this.prepareCTCommentCache();
        return this.commentRefs.keySet().iterator();
    }
    
    @Removal(version = "4.2")
    @Deprecated
    public Map<CellAddress, XSSFComment> getCellComments() {
        this.prepareCTCommentCache();
        final TreeMap<CellAddress, XSSFComment> map = new TreeMap<CellAddress, XSSFComment>();
        for (final Map.Entry<CellAddress, CTComment> e : this.commentRefs.entrySet()) {
            map.put(e.getKey(), new XSSFComment(this, e.getValue(), null));
        }
        return map;
    }
    
    private void prepareCTCommentCache() {
        if (this.commentRefs == null) {
            this.commentRefs = new HashMap<CellAddress, CTComment>();
            for (final CTComment comment : this.comments.getCommentList().getCommentArray()) {
                this.commentRefs.put(new CellAddress(comment.getRef()), comment);
            }
        }
    }
    
    @Internal
    public CTComment newComment(final CellAddress ref) {
        final CTComment ct = this.comments.getCommentList().addNewComment();
        ct.setRef(ref.formatAsString());
        ct.setAuthorId(0L);
        if (this.commentRefs != null) {
            this.commentRefs.put(ref, ct);
        }
        return ct;
    }
    
    @Override
    public boolean removeComment(final CellAddress cellRef) {
        final String stringRef = cellRef.formatAsString();
        final CTCommentList lst = this.comments.getCommentList();
        if (lst != null) {
            final CTComment[] commentArray = lst.getCommentArray();
            for (int i = 0; i < commentArray.length; ++i) {
                final CTComment comment = commentArray[i];
                if (stringRef.equals(comment.getRef())) {
                    lst.removeComment(i);
                    if (this.commentRefs != null) {
                        this.commentRefs.remove(cellRef);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    private int addNewAuthor(final String author) {
        final int index = this.comments.getAuthors().sizeOfAuthorArray();
        this.comments.getAuthors().insertAuthor(index, author);
        return index;
    }
    
    @Internal
    public CTComments getCTComments() {
        return this.comments;
    }
}
