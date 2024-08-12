package com.example.webfluxshop.service;


import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;



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


}
