package org.apache.poi.xssf.binary;

import org.apache.poi.util.LittleEndian;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.io.InputStream;
import java.util.List;
import java.util.Queue;
import org.apache.poi.ss.util.CellAddress;
import java.util.Map;
import org.apache.poi.util.Internal;

@Internal
public class XSSFBCommentsTable extends XSSFBParser
{
    private Map<CellAddress, XSSFBComment> comments;
    private Queue<CellAddress> commentAddresses;
    private List<String> authors;
    private int authorId;
    private CellAddress cellAddress;
    private XSSFBCellRange cellRange;
    private String comment;
    private StringBuilder authorBuffer;
    
    public XSSFBCommentsTable(final InputStream is) throws IOException {
        super(is);
        this.comments = new TreeMap<CellAddress, XSSFBComment>();
        this.commentAddresses = new LinkedList<CellAddress>();
        this.authors = new ArrayList<String>();
        this.authorId = -1;
        this.authorBuffer = new StringBuilder();
        this.parse();
        this.commentAddresses.addAll((Collection<?>)this.comments.keySet());
    }
    
    @Override
    public void handleRecord(final int id, final byte[] data) throws XSSFBParseException {
        final XSSFBRecordType recordType = XSSFBRecordType.lookup(id);
        switch (recordType) {
            case BrtBeginComment: {
                int offset = 0;
                this.authorId = XSSFBUtils.castToInt(LittleEndian.getUInt(data));
                offset += 4;
                this.cellRange = XSSFBCellRange.parse(data, offset, this.cellRange);
                offset += 16;
                this.cellAddress = new CellAddress(this.cellRange.firstRow, this.cellRange.firstCol);
                break;
            }
            case BrtCommentText: {
                final XSSFBRichStr xssfbRichStr = XSSFBRichStr.build(data, 0);
                this.comment = xssfbRichStr.getString();
                break;
            }
            case BrtEndComment: {
                this.comments.put(this.cellAddress, new XSSFBComment(this.cellAddress, this.authors.get(this.authorId), this.comment));
                this.authorId = -1;
                this.cellAddress = null;
                break;
            }
            case BrtCommentAuthor: {
                this.authorBuffer.setLength(0);
                XSSFBUtils.readXLWideString(data, 0, this.authorBuffer);
                this.authors.add(this.authorBuffer.toString());
                break;
            }
        }
    }
    
    public Queue<CellAddress> getAddresses() {
        return this.commentAddresses;
    }
    
    public XSSFBComment get(final CellAddress cellAddress) {
        if (cellAddress == null) {
            return null;
        }
        return this.comments.get(cellAddress);
    }
}
