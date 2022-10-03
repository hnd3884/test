package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.shared.Utils;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mailbox extends Payload
{
    public static final boolean ENABLED = true;
    public static final boolean DISABLED = false;
    private String mailboxId;
    private String userId;
    private Boolean enabled;
    private Long createTime;
    
    public String getMailboxId() {
        return this.mailboxId;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public Boolean isEnabled() {
        return this.enabled;
    }
    
    public Long getCreateTime() {
        return this.createTime;
    }
    
    public void setMailboxId(final String mailboxId) {
        this.mailboxId = Utils.lower(mailboxId);
    }
    
    public void setUserId(final String userId) {
        this.userId = Utils.lower(userId);
    }
    
    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setCreateTime(final Long createTime) {
        this.createTime = createTime;
    }
    
    public Mailbox mailboxId(final String mailboxId) {
        this.setMailboxId(mailboxId);
        return this;
    }
    
    public Mailbox userId(final String userId) {
        this.setUserId(userId);
        return this;
    }
    
    public Mailbox enabled(final Boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }
    
    public Mailbox createTime(final Long createTime) {
        this.setCreateTime(createTime);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Mailbox mailbox = (Mailbox)o;
        return Objects.equals(this.mailboxId, mailbox.mailboxId) && Objects.equals(this.userId, mailbox.userId) && Objects.equals(this.enabled, mailbox.enabled) && Objects.equals(this.createTime, mailbox.createTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.mailboxId, this.userId, this.enabled, this.createTime);
    }
    
    public Mailbox copy() {
        return new Mailbox().mailboxId(this.mailboxId).userId(this.userId).enabled(this.enabled).createTime(this.createTime);
    }
}
