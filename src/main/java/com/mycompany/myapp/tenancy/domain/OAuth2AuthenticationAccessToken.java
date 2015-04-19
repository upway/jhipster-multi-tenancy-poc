package com.mycompany.myapp.tenancy.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "OAUTH_ACCESS_TOKEN")
public class OAuth2AuthenticationAccessToken implements Serializable {

    @Id
    @Column(name = "token_id")
    private String tokenId;

    @Lob
    @Column(name = "token")
    private byte[] token;

    @Column(name = "authentication_id")
    private String authenticationId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "client_id")
    private String clientId;

    @Lob
    @Column(name = "authentication")
    private byte[] authentication;

    @Column(name = "refresh_token")
    private String refreshToken;


    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public byte[] getAuthentication() {
        return authentication;
    }

    public void setAuthentication(byte[] authentication) {
        this.authentication = authentication;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "OAuthAccessToken [token_id=" + tokenId
            + ",  authentication_id=" + authenticationId + ", user_name="
            + userName + ", client_id=" + clientId + ", refresh_token="
            + refreshToken + "]";
    }
}
