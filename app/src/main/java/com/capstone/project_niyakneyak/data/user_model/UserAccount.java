package com.capstone.project_niyakneyak.data.user_model;

/**
 * 사용자 계정 정보 모델 클래스
 */
public class UserAccount {

    private String idToken;      // Firebase Uid (고유 토큰정보)
    private String emailId;
    private String password;

    private String name;        // Renamed from 'userName'
    private String phoneNumber;

    public UserAccount() { }    // 파이어베이스에서는 빈 생성자를 만들어줘야 한다. 모델클래스를 통해서 가져오는데 빈 생성자 반드시 필요하다.

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNumber;
    }

    public void setPhoneNum(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }
}
