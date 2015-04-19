package com.mycompany.myapp.tenancy.repository;


import com.mycompany.myapp.tenancy.domain.OAuth2AuthenticationRefreshToken;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data MongoDB repository for the OAuth2AuthenticationRefreshToken entity.
 */
public interface OAuth2RefreshTokenRepository extends JpaRepository<OAuth2AuthenticationRefreshToken, String> {

    public OAuth2AuthenticationRefreshToken findByTokenId(String tokenId);
}
