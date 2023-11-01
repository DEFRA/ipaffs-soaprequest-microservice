package uk.gov.defra.tracesx.soaprequest.logging;

import static ch.qos.logback.classic.Level.INFO;
import static ch.qos.logback.classic.Level.WARN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class ConversationLoggingFilterTest {

  private static final String CONVERSATION_ID_HEADER_NAME = "INS-ConversationId";
  private static final String CONVERSATION_ID = "ConversationId";
  private static final String CONVERSATION_ID_KEY = "ConversationIdKey";
  private final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;
  @Mock
  private FilterConfig filterConfig;
  @Mock
  private Appender<ILoggingEvent> mockAppender;
  @Captor
  private ArgumentCaptor<LoggingEvent> captorLoggingEvent;
  @InjectMocks
  private ConversationLoggingFilter conversationLoggingFilter;

  @BeforeEach
  void setup() {
    logger.addAppender(mockAppender);
  }

  @AfterEach
  void teardown() {
    logger.detachAppender(mockAppender);
  }

  @Test
  void initializingFilterMessageIsLogged() {
    conversationLoggingFilter.init(filterConfig);

    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel()).isEqualTo(INFO);
    assertThat(loggingEvent.getFormattedMessage()).containsSequence("Initializing filter");
  }

  @Test
  void filterChecksHeaderAndContinuesChain() throws Exception {
    when(request.getHeader(CONVERSATION_ID_HEADER_NAME)).thenReturn(CONVERSATION_ID);
    conversationLoggingFilter.doFilter(request, response, filterChain);

    verify(request).getHeader(CONVERSATION_ID_HEADER_NAME);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void destroyingFilterMessageIsLogged() {
    conversationLoggingFilter.destroy();

    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel()).isEqualTo(WARN);
    assertThat(loggingEvent.getFormattedMessage()).containsSequence("Destroying filter");
  }

  @Test
  void conversationIdIsNotRegisteredWithMDCWhenIdIsNull()
      throws Exception {
    when(request.getHeader(CONVERSATION_ID_HEADER_NAME)).thenReturn(CONVERSATION_ID);
    when(request.getHeader(CONVERSATION_ID_HEADER_NAME)).thenReturn(null);
    try (MockedStatic<MDC> staticMock = Mockito.mockStatic(MDC.class)) {
      conversationLoggingFilter.doFilter(request, response, filterChain);
      staticMock.verify(() -> MDC.put(CONVERSATION_ID_KEY, CONVERSATION_ID), never());
      staticMock.verify(() -> MDC.remove(CONVERSATION_ID_KEY), never());
    }
  }
}
