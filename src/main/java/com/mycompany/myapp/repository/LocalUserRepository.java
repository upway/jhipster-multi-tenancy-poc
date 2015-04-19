package com.mycompany.myapp.repository;

import com.mycompany.myapp.tenancy.domain.User;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository("localUserRepository")
public interface LocalUserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(DateTime dateTime);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);

    void delete(User t);

}
