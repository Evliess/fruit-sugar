package evliess.io.config;

import evliess.io.util.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Value("${ucode}")
  private String uCode;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (authentication != null) {
      String principal = authentication.getPrincipal().toString();
      if (uCode.equals(principal)) {
        return new UsernamePasswordAuthenticationToken("", "", List.of());
      }
      if (TokenUtils.isValidToken(principal)) {
        return new UsernamePasswordAuthenticationToken("", "", List.of());
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
