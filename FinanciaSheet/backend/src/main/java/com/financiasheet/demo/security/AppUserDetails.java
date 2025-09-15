package com.financiasheet.demo.security;

import com.financiasheet.demo.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class AppUserDetails implements UserDetails {
    private final User user;
    public AppUserDetails(User u){ this.user = u; }
    @Override public List<SimpleGrantedAuthority> getAuthorities(){ return List.of(new SimpleGrantedAuthority("ROLE_USER")); }
    @Override public String getPassword(){ return user.getPasswordHash(); }
    @Override public String getUsername(){ return user.getEmail(); }
    @Override public boolean isAccountNonExpired(){ return true; }
    @Override public boolean isAccountNonLocked(){ return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled(){ return true; }
}
