package com.example.market.security;

import com.example.market.Models.Person;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PersonDetails implements UserDetails {

    private final Person person;

    public PersonDetails(Person person) {
        this.person = person;
    }

    public Person getPerson(){
        return this.person;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(person.getRole()));
    }

    @Override
    public String getPassword() {

        return this.person.getPassword();
    }

    @Override
    public String getUsername() {
        return this.person.getLogin();
    }

    // Аккаунт не протух
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Аккаунт не заблокирован
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // пароль не протух
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // активен ли эккаунт
    @Override
    public boolean isEnabled() {
        return true;
    }
}
