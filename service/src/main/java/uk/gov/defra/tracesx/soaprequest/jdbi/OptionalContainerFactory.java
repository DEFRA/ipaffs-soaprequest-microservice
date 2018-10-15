package uk.gov.defra.tracesx.soaprequest.jdbi;

import java.util.Optional;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

public class OptionalContainerFactory implements ContainerFactory<Optional<?>> {

  public boolean accepts(Class<?> type) {
    return Optional.class.isAssignableFrom(type);
  }

  public ContainerBuilder<Optional<?>> newContainerBuilderFor(Class<?> type) {
    return new OptionalContainerFactory.OptionalContainerBuilder();
  }

  private static class OptionalContainerBuilder implements ContainerBuilder<Optional<?>> {

    private Optional<?> optional;

    private OptionalContainerBuilder() {
      this.optional = Optional.empty();
    }

    public ContainerBuilder<Optional<?>> add(Object it) {
      this.optional = Optional.ofNullable(it);
      return this;
    }

    public Optional<?> build() {
      return this.optional;
    }
  }
}
