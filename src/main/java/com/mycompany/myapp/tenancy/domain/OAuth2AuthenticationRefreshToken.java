package com.mycompany.myapp.tenancy.domain;


import javax.persistence.*;

@Entity
@Table(name = "OAUTH_REFRESH_TOKEN")
public class OAuth2AuthenticationRefreshToken {

    @Id
    @Column(name = "token_id")
    private String tokenId;

    @Lob
    private byte[] token;

    @Lob
    private byte[] authentication;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    public byte[] getAuthentication() {
        return authentication;
    }

    public void setAuthentication(byte[] authentication) {
        this.authentication = authentication;
    }

    @Override
    public String toString() {
        return "OAuthRefreshToken [token_id=" + tokenId + "]";
    }
}
