package uk.gov.defra.tracesx.soaprequest.dao.entities;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest.ChedCertificateHash;

class CacheRequestTest {

  private static final String ID = "test_id";
  private static final String VALUE = "test_value";

  @Test
  void verifyEqualsAndHashcode() {
    // Given
    CacheRequest instance1 = new CacheRequest(ID, new ChedCertificateHash(VALUE), LocalDateTime.now());
    CacheRequest instance2 = new CacheRequest(ID, new ChedCertificateHash(VALUE), LocalDateTime.now());

    //When

    //Then
    assertThat(instance1.equals(instance2)).isTrue();
    assertThat(instance1).hasSameHashCodeAs(instance2);
  }

  @Test
  void verifyNotEqual() {
    // Given
    CacheRequest nullInstance = null;
    CacheRequest instance1 = new CacheRequest(ID, new ChedCertificateHash(VALUE), LocalDateTime.now());
    CacheRequest instance2 = new CacheRequest("Test_ID_2", new ChedCertificateHash(VALUE), LocalDateTime.now());
    CacheRequest instance3 = new CacheRequest(ID, new ChedCertificateHash("RANDOM_VALUE"), LocalDateTime.now());

    //Then
    assertThat(instance1.equals(nullInstance)).isFalse();
    assertThat(instance1.equals(instance2)).isFalse();
    assertThat(instance1.equals(instance3)).isFalse();

    assertThat(instance1.hashCode()).isNotEqualTo(instance2.hashCode());
    assertThat(instance1.hashCode()).isNotEqualTo(instance2.hashCode());
  }
}
