package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.shared.Utils;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends Payload
{
    private String userId;
    private String name;
    private UserRole role;
    private Boolean system;
    private Long createTime;
    private transient String password;
    
    public String getUserId() {
        return this.userId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public UserRole getRole() {
        return this.role;
    }
    
    public Long getCreateTime() {
        return this.createTime;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public Boolean isSystem() {
        return this.system;
    }
    
    public void setUserId(final String userId) {
        this.userId = Utils.lower(userId);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setRole(final UserRole role) {
        this.role = role;
    }
    
    public void setCreateTime(final Long createTime) {
        this.createTime = createTime;
    }
    
    public void setSystem(final Boolean system) {
        this.system = system;
    }
    
    public User userId(final String userId) {
        this.setUserId(userId);
        return this;
    }
    
    public User name(final String name) {
        this.setName(name);
        return this;
    }
    
    public User password(final String password) {
        this.setPassword(password);
        return this;
    }
    
    public User role(final UserRole role) {
        this.setRole(role);
        return this;
    }
    
    public User role(final String role) {
        return this.role(UserRole.valueOf(role));
    }
    
    public User system(final Boolean system) {
        this.setSystem(system);
        return this;
    }
    
    public User createTime(final Long createTime) {
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
        final User user = (User)o;
        return Objects.equals(this.userId, user.userId) && Objects.equals(this.name, user.name) && this.role == user.role && Objects.equals(this.system, user.system) && Objects.equals(this.createTime, user.createTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.name, this.role, this.system, this.createTime);
    }
}
