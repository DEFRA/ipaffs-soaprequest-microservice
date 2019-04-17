package uk.gov.defra.tracesx.soaprequest.audit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditConfigTest {

  @Test
  void isAuditOnCreate() {
    AuditConfig config = AuditConfig.Builder.anAuditConfig()
        .withAuditOnCreate(true)
        .build();

    assertThat(config.isAuditOnCreate()).isTrue();
    assertThat(config.isAuditOnRead()).isFalse();
    assertThat(config.isAuditOnDelete()).isFalse();
  }

  @Test
  void isAuditOnRead() {
    AuditConfig config = AuditConfig.Builder.anAuditConfig()
        .withAuditOnRead(true)
        .build();

    assertThat(config.isAuditOnCreate()).isFalse();
    assertThat(config.isAuditOnRead()).isTrue();
    assertThat(config.isAuditOnDelete()).isFalse();
  }

  @Test
  void isAuditOnDelete() {
    AuditConfig config = AuditConfig.Builder.anAuditConfig()
        .withAuditOnDelete(true)
        .build();

    assertThat(config.isAuditOnCreate()).isFalse();
    assertThat(config.isAuditOnRead()).isFalse();
    assertThat(config.isAuditOnDelete()).isTrue();
  }

  @Test
  void isAuditOnCreateAndRead() {
    AuditConfig config = AuditConfig.Builder.anAuditConfig()
        .withAuditOnRead(true)
        .withAuditOnCreate(true)
        .build();

    assertThat(config.isAuditOnRead()).isTrue();
    assertThat(config.isAuditOnDelete()).isFalse();
    assertThat(config.isAuditOnCreate()).isTrue();

  }

  @Test
  void isAuditOnCreateAndDelete() {
    AuditConfig config = AuditConfig.Builder.anAuditConfig()
        .withAuditOnDelete(true)
        .withAuditOnCreate(true)
        .build();

    assertThat(config.isAuditOnCreate()).isTrue();
    assertThat(config.isAuditOnRead()).isFalse();
    assertThat(config.isAuditOnDelete()).isTrue();
  }

  @Test
  void isAuditOnReadAndDelete() {
    AuditConfig config = AuditConfig.Builder.anAuditConfig()
        .withAuditOnDelete(true)
        .withAuditOnRead(true)
        .build();

    assertThat(config.isAuditOnCreate()).isFalse();
    assertThat(config.isAuditOnRead()).isTrue();
    assertThat(config.isAuditOnDelete()).isTrue();
  }

  @Test
  void isAuditOnCreateReadAndDelete() {
    AuditConfig config = AuditConfig.Builder.anAuditConfig()
        .withAuditOnDelete(true)
        .withAuditOnRead(true)
        .withAuditOnCreate(true)
        .build();

    assertThat(config.isAuditOnCreate()).isTrue();
    assertThat(config.isAuditOnRead()).isTrue();
    assertThat(config.isAuditOnDelete()).isTrue();
  }
}
