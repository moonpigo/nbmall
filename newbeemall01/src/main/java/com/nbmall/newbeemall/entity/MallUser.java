package com.nbmall.newbeemall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class MallUser {
    private Long userId;
    private String nickName;
    private String loginName;
    private String passwordMd5;
    private String introduceSign;
    private String address;
    private Byte isDeleted; // '注销标识字段(0-正常 1-已注销)',
    private Byte lockedFlag; //'锁定标识字段(0-未锁定 1-已锁定)',

    @JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss",timezone = "GET+8")
    private Date createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPasswordMd5() {
        return passwordMd5;
    }

    public void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }

    public String getIntroduceSign() {
        return introduceSign;
    }

    public void setIntroduceSign(String introduceSign) {
        this.introduceSign = introduceSign;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Byte getLockedFlag() {
        return lockedFlag;
    }

    public void setLockedFlag(Byte lockedFlag) {
        this.lockedFlag = lockedFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", nickName=").append(nickName);
        sb.append(", loginName=").append(loginName);
        sb.append(", passwordMd5=").append(passwordMd5);
        sb.append(", introduceSign=").append(introduceSign);
        sb.append(", address=").append(address);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", lockedFlag=").append(lockedFlag);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}
