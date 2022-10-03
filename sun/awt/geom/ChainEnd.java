package sun.awt.geom;

final class ChainEnd
{
    CurveLink head;
    CurveLink tail;
    ChainEnd partner;
    int etag;
    
    public ChainEnd(final CurveLink curveLink, final ChainEnd partner) {
        this.head = curveLink;
        this.tail = curveLink;
        this.partner = partner;
        this.etag = curveLink.getEdgeTag();
    }
    
    public CurveLink getChain() {
        return this.head;
    }
    
    public void setOtherEnd(final ChainEnd partner) {
        this.partner = partner;
    }
    
    public ChainEnd getPartner() {
        return this.partner;
    }
    
    public CurveLink linkTo(final ChainEnd chainEnd) {
        if (this.etag == 0 || chainEnd.etag == 0) {
            throw new InternalError("ChainEnd linked more than once!");
        }
        if (this.etag == chainEnd.etag) {
            throw new InternalError("Linking chains of the same type!");
        }
        ChainEnd chainEnd2;
        ChainEnd chainEnd3;
        if (this.etag == 1) {
            chainEnd2 = this;
            chainEnd3 = chainEnd;
        }
        else {
            chainEnd2 = chainEnd;
            chainEnd3 = this;
        }
        this.etag = 0;
        chainEnd.etag = 0;
        chainEnd2.tail.setNext(chainEnd3.head);
        chainEnd2.tail = chainEnd3.tail;
        if (this.partner == chainEnd) {
            return chainEnd2.head;
        }
        final ChainEnd partner = chainEnd3.partner;
        final ChainEnd partner2 = chainEnd2.partner;
        partner.partner = partner2;
        partner2.partner = partner;
        if (chainEnd2.head.getYTop() < partner.head.getYTop()) {
            chainEnd2.tail.setNext(partner.head);
            partner.head = chainEnd2.head;
        }
        else {
            partner2.tail.setNext(chainEnd2.head);
            partner2.tail = chainEnd2.tail;
        }
        return null;
    }
    
    public void addLink(final CurveLink head) {
        if (this.etag == 1) {
            this.tail.setNext(head);
            this.tail = head;
        }
        else {
            head.setNext(this.head);
            this.head = head;
        }
    }
    
    public double getX() {
        if (this.etag == 1) {
            return this.tail.getXBot();
        }
        return this.head.getXBot();
    }
}
