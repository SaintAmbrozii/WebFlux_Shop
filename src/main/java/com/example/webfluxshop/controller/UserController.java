package com.example.webfluxshop.controller;


import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.exception.UserNotFoundException;
import com.example.webfluxshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/profile")
    public Mono<Authentication> profile() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication);
    }

    @GetMapping
    public Flux<User> getAll(){
        return userService.getAll();
    }

    @PatchMapping("{id}")
    public Mono<User> update(@PathVariable(name = "id") Long id,@RequestBody User user) {
        return userService.update(id, user).switchIfEmpty(Mono.error(new UserNotFoundException("нет данного пользователя")));
    }

    @GetMapping("{id}")
    public Mono<User> findById(@PathVariable(name = "id")Long id) {
        return userService.findById(id).switchIfEmpty(Mono.error(new UserNotFoundException("нет данного пользователя")));
    }
    @DeleteMapping("{id}")
    public Mono<Void> deleteById(@PathVariable(name = "id")Long id){
        return userService.deleteById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("нет данного пользователя")));
    }

}
