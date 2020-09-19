package com.nbmall.newbeemall.entity;

public class AdminUser {
    private Integer adminUserId;
    private String adminUserName;
    private String adminPassword;
    private String nickName;
    private Boolean locked;

    public Integer getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Integer adminUserId) {
        this.adminUserId = adminUserId;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "adminUserId='" + adminUserId + '\'' +
                ", adminUserName='" + adminUserName + '\'' +
                ", adminPassword='" + adminPassword + '\'' +
                ", nickName='" + nickName + '\'' +
                ", locked=" + locked +
                '}';
    }
}
