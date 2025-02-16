package com.project.shopapp.authentication;

import com.nimbusds.jwt.JWT;
import com.project.shopapp.exception.AppException;
import com.project.shopapp.exception.ErrorCode;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.text.ParseException;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class UserRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private static final UriTemplate USER_URI_TEMPLATE = new UriTemplate("/users/{userId}");

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        // Extract the userId from the request URI: /users/userId
        Map<String, String> uriVariables = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
        String userIdFromRequestUri = uriVariables.get("userId");

        // Extract the userId from the Authentication object, which is a Jwt object
        Authentication authentication = authenticationSupplier.get();
        String userIdFromJwt = ((Jwt) authentication.getPrincipal()).getClaim("userId").toString();

        // Check if the user has the role "ROLE_ADMIN"
        boolean hasAdminRole = authentication.getAuthorities()
                .stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_ADMIN"));

        // Check if the user has the role "ROLE_USER"
        boolean hasUserRole = authentication.getAuthorities()
                .stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_USER"));

        // Compare the two userIds
        boolean userIdsMatch = userIdFromRequestUri != null && userIdFromRequestUri.equals(userIdFromJwt);

        return new AuthorizationDecision(hasAdminRole || (hasUserRole && userIdsMatch));
    }
}
