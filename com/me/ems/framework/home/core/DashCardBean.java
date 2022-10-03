package com.me.ems.framework.home.core;

public class DashCardBean
{
    private long cardID;
    private String cardName;
    private String cardDescription;
    private String cardType;
    private String cardDisplayName;
    private long cardPosition;
    
    public long getCardID() {
        return this.cardID;
    }
    
    public void setCardID(final long cardID) {
        this.cardID = cardID;
    }
    
    public String getCardName() {
        return this.cardName;
    }
    
    public void setCardName(final String cardName) {
        this.cardName = cardName;
    }
    
    public String getCardDescription() {
        return this.cardDescription;
    }
    
    public void setCardDescription(final String cardDescription) {
        this.cardDescription = cardDescription;
    }
    
    public String getCardType() {
        return this.cardType;
    }
    
    public void setCardType(final String cardType) {
        this.cardType = cardType;
    }
    
    public String getCardDisplayName() {
        return this.cardDisplayName;
    }
    
    public void setCardDisplayName(final String cardDisplayName) {
        this.cardDisplayName = cardDisplayName;
    }
    
    public long getCardPosition() {
        return this.cardPosition;
    }
    
    public void setCardPosition(final long cardPosition) {
        this.cardPosition = cardPosition;
    }
}
