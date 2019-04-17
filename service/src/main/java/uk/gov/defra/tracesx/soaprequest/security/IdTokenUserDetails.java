package uk.gov.defra.tracesx.soaprequest.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@Builder
public class IdTokenUserDetails implements UserDetails {

  private List<SimpleGrantedAuthority> authorities;
  private String username; // upn
  private String idToken;
  private String displayName; // name
  private String userObjectId; // oid

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

  @Override
  public String getPassword() {
    return null;
  }
}
