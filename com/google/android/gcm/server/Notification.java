package com.google.android.gcm.server;

import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public final class Notification implements Serializable
{
    private final String title;
    private final String body;
    private final String icon;
    private final String sound;
    private final Integer badge;
    private final String tag;
    private final String color;
    private final String clickAction;
    private final String bodyLocKey;
    private final List<String> bodyLocArgs;
    private final String titleLocKey;
    private final List<String> titleLocArgs;
    
    private Notification(final Builder builder) {
        this.title = builder.title;
        this.body = builder.body;
        this.icon = builder.icon;
        this.sound = builder.sound;
        this.badge = builder.badge;
        this.tag = builder.tag;
        this.color = builder.color;
        this.clickAction = builder.clickAction;
        this.bodyLocKey = builder.bodyLocKey;
        this.bodyLocArgs = builder.bodyLocArgs;
        this.titleLocKey = builder.titleLocKey;
        this.titleLocArgs = builder.titleLocArgs;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getBody() {
        return this.body;
    }
    
    public String getIcon() {
        return this.icon;
    }
    
    public String getSound() {
        return this.sound;
    }
    
    public Integer getBadge() {
        return this.badge;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public String getColor() {
        return this.color;
    }
    
    public String getClickAction() {
        return this.clickAction;
    }
    
    public String getBodyLocKey() {
        return this.bodyLocKey;
    }
    
    public List<String> getBodyLocArgs() {
        return this.bodyLocArgs;
    }
    
    public String getTitleLocKey() {
        return this.titleLocKey;
    }
    
    public List<String> getTitleLocArgs() {
        return this.titleLocArgs;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Notification(");
        if (this.title != null) {
            builder.append("title=").append(this.title).append(", ");
        }
        if (this.body != null) {
            builder.append("body=").append(this.body).append(", ");
        }
        if (this.icon != null) {
            builder.append("icon=").append(this.icon).append(", ");
        }
        if (this.sound != null) {
            builder.append("sound=").append(this.sound).append(", ");
        }
        if (this.badge != null) {
            builder.append("badge=").append(this.badge).append(", ");
        }
        if (this.tag != null) {
            builder.append("tag=").append(this.tag).append(", ");
        }
        if (this.color != null) {
            builder.append("color=").append(this.color).append(", ");
        }
        if (this.clickAction != null) {
            builder.append("clickAction=").append(this.clickAction).append(", ");
        }
        if (this.bodyLocKey != null) {
            builder.append("bodyLocKey=").append(this.bodyLocKey).append(", ");
        }
        if (this.bodyLocArgs != null) {
            builder.append("bodyLocArgs=").append(this.bodyLocArgs).append(", ");
        }
        if (this.titleLocKey != null) {
            builder.append("titleLocKey=").append(this.titleLocKey).append(", ");
        }
        if (this.titleLocArgs != null) {
            builder.append("titleLocArgs=").append(this.titleLocArgs).append(", ");
        }
        if (builder.charAt(builder.length() - 1) == ' ') {
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append(")");
        return builder.toString();
    }
    
    public static final class Builder
    {
        private final String icon;
        private String title;
        private String body;
        private String sound;
        private Integer badge;
        private String tag;
        private String color;
        private String clickAction;
        private String bodyLocKey;
        private List<String> bodyLocArgs;
        private String titleLocKey;
        private List<String> titleLocArgs;
        
        public Builder(final String icon) {
            this.icon = icon;
            this.sound = "default";
        }
        
        public Builder title(final String value) {
            this.title = value;
            return this;
        }
        
        public Builder body(final String value) {
            this.body = value;
            return this;
        }
        
        public Builder sound(final String value) {
            this.sound = value;
            return this;
        }
        
        public Builder badge(final int value) {
            this.badge = value;
            return this;
        }
        
        public Builder tag(final String value) {
            this.tag = value;
            return this;
        }
        
        public Builder color(final String value) {
            this.color = value;
            return this;
        }
        
        public Builder clickAction(final String value) {
            this.clickAction = value;
            return this;
        }
        
        public Builder bodyLocKey(final String value) {
            this.bodyLocKey = value;
            return this;
        }
        
        public Builder bodyLocArgs(final List<String> value) {
            this.bodyLocArgs = Collections.unmodifiableList((List<? extends String>)value);
            return this;
        }
        
        public Builder titleLocKey(final String value) {
            this.titleLocKey = value;
            return this;
        }
        
        public Builder titleLocArgs(final List<String> value) {
            this.titleLocArgs = Collections.unmodifiableList((List<? extends String>)value);
            return this;
        }
        
        public Notification build() {
            return new Notification(this, null);
        }
    }
}
