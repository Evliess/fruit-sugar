package evliess.io.config;

import evliess.io.controller.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CustomFilter extends OncePerRequestFilter {

  private final AuthenticationManager authenticationManager;
  private final List<String> whitelistPatterns;

  public CustomFilter(AuthenticationManager authenticationManager, String[] whitelistPatterns) {
    this.authenticationManager = authenticationManager;
    this.whitelistPatterns = Arrays.asList(whitelistPatterns);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();
    if (isWhitelisted(path)) {
      filterChain.doFilter(request, response);
      return;
    }
    String token = request.getHeader(Constants.HEAD_AUTH);
    if (token == null || token.trim().isEmpty()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    try {
      if (token.startsWith(Constants.BEARER)) {
        token = token.substring(Constants.BEARER.length()).trim();
      }
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    Authentication authentication = new UsernamePasswordAuthenticationToken(token, null);
    Authentication verifiedAuth = this.authenticationManager.authenticate(authentication);
    if (verifiedAuth == null || !verifiedAuth.isAuthenticated()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    SecurityContextHolder.getContext().setAuthentication(verifiedAuth);
    filterChain.doFilter(request, response);
  }

  private boolean isWhitelisted(String path) {
    for (String pattern : whitelistPatterns) {
      if (matchesPattern(path, pattern)) {
        return true;
      }
    }
    return false;
  }

  private boolean matchesPattern(String path, String pattern) {
    path = path.replace("/sugar-dict", "");
    if (pattern.endsWith("/**")) {
      String prefix = pattern.substring(0, pattern.length() - 3);
      return path.startsWith(prefix);
    }
    return path.equals(pattern);
  }
}
