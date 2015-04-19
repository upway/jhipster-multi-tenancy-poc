package com.mycompany.myapp.tenancy.repository;

import com.mycompany.myapp.tenancy.domain.OAuth2AuthenticationAccessToken;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the OAuth2AuthenticationAccessToken entity.
 */
public interface OAuth2AccessTokenRepository extends JpaRepository<OAuth2AuthenticationAccessToken, String> {

    public OAuth2AuthenticationAccessToken findByTokenId(String tokenId);

    public OAuth2AuthenticationAccessToken findByRefreshToken(String refreshToken);

    public OAuth2AuthenticationAccessToken findByAuthenticationId(String authenticationId);

    public List<OAuth2AuthenticationAccessToken> findByClientIdAndUserName(String clientId, String userName);

    public List<OAuth2AuthenticationAccessToken> findByClientId(String clientId);
}
