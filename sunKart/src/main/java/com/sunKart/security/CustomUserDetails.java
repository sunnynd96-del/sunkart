package com.sunKart.security;

import com.sunKart.model.Role;
import com.sunKart.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class CustomUserDetails implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    private final User user;

    public CustomUserDetails(User user) {
        this.user = Objects.requireNonNull(user, "User must not be null");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        Set<GrantedAuthority> authorities = roles.stream()
            .filter(Objects::nonNull)
            .map(Role::getName)
            .filter(Objects::nonNull)
            .map(String::trim)
            .map(name -> name.startsWith("ROLE_") ? name : "ROLE_" + name)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toUnmodifiableSet());

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return this.user;
    }
}
