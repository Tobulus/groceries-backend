package rmbr.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import rmbr.service.UserPrincipal;

public interface BasicController {
    default UserPrincipal getUserPrincipalOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Please log in");
        }

        return (UserPrincipal) auth.getPrincipal();
    }
}
