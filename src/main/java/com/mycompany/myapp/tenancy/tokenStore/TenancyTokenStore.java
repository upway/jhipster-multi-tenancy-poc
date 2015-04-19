package com.mycompany.myapp.tenancy.tokenStore;

import com.mycompany.myapp.tenancy.domain.OAuth2AuthenticationAccessToken;
import com.mycompany.myapp.tenancy.domain.OAuth2AuthenticationRefreshToken;
import com.mycompany.myapp.tenancy.repository.OAuth2AccessTokenRepository;
import com.mycompany.myapp.tenancy.repository.OAuth2RefreshTokenRepository;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AdamS on 2015-03-30.
 */
public class TenancyTokenStore implements TokenStore {
    private final OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public TenancyTokenStore(final OAuth2AccessTokenRepository oAuth2AccessTokenRepository,
                               final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository) {
        this.oAuth2AccessTokenRepository = oAuth2AccessTokenRepository;
        this.oAuth2RefreshTokenRepository = oAuth2RefreshTokenRepository;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String tokenId) {
        //oAuth2AccessTokenRepository.findByTokenId(tokenId)
        OAuth2AuthenticationAccessToken accessToken = oAuth2AccessTokenRepository.findByTokenId(extractTokenKey(tokenId));
        if(accessToken != null) {
            byte[] auth = accessToken.getAuthentication();
            return SerializationUtils.deserialize(auth);
        }
        return null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
//        OAuth2AuthenticationAccessToken oAuth2AuthenticationAccessToken = new OAuth2AuthenticationAccessToken(token,
//            authentication, authenticationKeyGenerator.extractKey(authentication));

        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (readAccessToken(token.getValue())!=null) {
            removeAccessToken(token);
        }
        OAuth2AuthenticationAccessToken oAuth2AuthenticationAccessToken = new OAuth2AuthenticationAccessToken();
        oAuth2AuthenticationAccessToken.setTokenId(extractTokenKey(token.getValue()));
        oAuth2AuthenticationAccessToken.setToken(SerializationUtils.serialize(token));
        String userName = authentication.isClientOnly() ? null : authentication.getName();
        oAuth2AuthenticationAccessToken.setUserName(userName);
        oAuth2AuthenticationAccessToken.setClientId(authentication.getOAuth2Request().getClientId());
        oAuth2AuthenticationAccessToken.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        oAuth2AuthenticationAccessToken.setAuthentication(SerializationUtils.serialize(authentication));
        oAuth2AuthenticationAccessToken.setRefreshToken(refreshToken);
        oAuth2AccessTokenRepository.save(oAuth2AuthenticationAccessToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;

        OAuth2AuthenticationAccessToken tokenDb = oAuth2AccessTokenRepository.findByTokenId(extractTokenKey(tokenValue));
        if(tokenDb != null){
            accessToken =SerializationUtils.deserialize(tokenDb.getToken());
        }
        return accessToken;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        OAuth2AuthenticationAccessToken accessToken = oAuth2AccessTokenRepository.findByTokenId(extractTokenKey(token.getValue()));
        if(accessToken != null) {
            oAuth2AccessTokenRepository.delete(accessToken);
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        OAuth2AuthenticationRefreshToken oAuth2AuthenticationRefreshToken =new OAuth2AuthenticationRefreshToken();
        oAuth2AuthenticationRefreshToken.setTokenId( extractTokenKey(refreshToken.getValue()));
        oAuth2AuthenticationRefreshToken.setToken(SerializationUtils.serialize(refreshToken));
        oAuth2AuthenticationRefreshToken.setAuthentication(SerializationUtils.serialize(authentication));
        oAuth2RefreshTokenRepository.save(oAuth2AuthenticationRefreshToken);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        OAuth2RefreshToken refreshToken = null;
        OAuth2AuthenticationRefreshToken oAuth2AuthenticationRefreshToken = oAuth2RefreshTokenRepository.findByTokenId(extractTokenKey(tokenValue));
        if(oAuth2AuthenticationRefreshToken != null) {
            refreshToken = SerializationUtils.deserialize(oAuth2AuthenticationRefreshToken.getToken());
        }
        return refreshToken;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.deserialize(oAuth2RefreshTokenRepository.findByTokenId(extractTokenKey(token.getValue())).getAuthentication());
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        OAuth2AuthenticationRefreshToken oAuth2AuthenticationRefreshToken = oAuth2RefreshTokenRepository.findByTokenId(extractTokenKey(token.getValue()));
        if(oAuth2AuthenticationRefreshToken != null) {
            oAuth2RefreshTokenRepository.delete(oAuth2RefreshTokenRepository.findByTokenId(extractTokenKey(token.getValue())));
        }
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        oAuth2AccessTokenRepository.delete(oAuth2AccessTokenRepository.findByRefreshToken(extractTokenKey(refreshToken.getValue())));
    }

    /// tutaj w oryginale byl jaki warunek
    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;
        OAuth2AuthenticationAccessToken token =  oAuth2AccessTokenRepository.findByAuthenticationId(authenticationKeyGenerator.extractKey(authentication));

//        for(OAuth2AuthenticationAccessToken token : tokens){
//            oAuth2AccessTokenRepository.delete(token);
//        }
        if(token != null) {
            accessToken = SerializationUtils.deserialize(token.getToken());
        }
/*
        if (accessToken != null
            && !authentication.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken);
// Keep the store consistent (maybe the same user is represented by this authentication but the details have
// changed)
            storeAccessToken(accessToken, authentication);
        }*/

        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OAuth2AuthenticationAccessToken> tokens = oAuth2AccessTokenRepository.findByClientId(clientId);
        return extractAccessTokens(tokens);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<OAuth2AuthenticationAccessToken> tokens = oAuth2AccessTokenRepository.findByClientIdAndUserName(clientId, userName);
        return extractAccessTokens(tokens);
    }

    private Collection<OAuth2AccessToken> extractAccessTokens(List<OAuth2AuthenticationAccessToken> tokens) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        for(OAuth2AuthenticationAccessToken token : tokens) {
            accessTokens.add(SerializationUtils.deserialize(token.getToken()));
        }
        return accessTokens;
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }
}
