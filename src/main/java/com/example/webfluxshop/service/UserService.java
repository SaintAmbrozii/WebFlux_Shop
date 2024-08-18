package com.example.webfluxshop.service;


import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.exception.UserNotAuthorizedException;
import com.example.webfluxshop.exception.UserNotFoundException;
import com.example.webfluxshop.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;


    public Mono<User> getUserInSession() {
        return getPrincipalFromSecurityContext();
    }



    public Flux<User> getAll() {
        return userRepo.findAll();
    }

    public Mono<User> update(Long id,User user) {
        return userRepo.findById(id).
                flatMap(updUser->{
            updUser.setName(user.getName());
            updUser.setEmail(user.getEmail());
            updUser.setPassword(user.getPassword());
            return userRepo.save(updUser);
        });
    }

    public Mono<User> findByUsername(String email) {
        return userRepo.findByEmail(email);
    }

    public Mono<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public Mono<Void> deleteById(Long id) {
       return userRepo.deleteById(id);
    }

    private Mono<User> getPrincipalFromSecurityContext() {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotAuthorizedException("Security context not found"))))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(String.class)
                .flatMap(this::findByUsername)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotFoundException("User not found"))));
    }


}
