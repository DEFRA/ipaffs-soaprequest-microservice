package uk.gov.defra.tracesx.soaprequest.logging;

import static ch.qos.logback.classic.Level.INFO;
import static ch.qos.logback.classic.Level.WARN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class ConversationLoggingFilterTest {

  private static final String CONVERSATION_ID_HEADER_NAME = "INS-ConversationId";
  private static final String CONVERSATION_ID = "ConversationId";
  private static final String CONVERSATION_ID_KEY = "ConversationIdKey";

  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;
  @Mock
  private FilterConfig filterConfig;
  @Mock
  private Appender mockAppender;

  @Captor
  private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

  @InjectMocks
  private ConversationLoggingFilter conversationLoggingFilter;

  private final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

  @Before
  public void setup() {
    when(request.getHeader(CONVERSATION_ID_HEADER_NAME)).thenReturn(CONVERSATION_ID);
    logger.addAppender(mockAppender);
  }

  @After
  public void teardown() {
    logger.detachAppender(mockAppender);
  }

  @Test
  public void initializingFilterMessageIsLogged() {
    conversationLoggingFilter.init(filterConfig);

    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(INFO));
    assertThat(loggingEvent.getFormattedMessage(),
        containsString("Initializing filter"));
  }

  @Test
  public void filterChecksHeaderAndContinuesChain() throws Exception {
    conversationLoggingFilter.doFilter(request, response, filterChain);

    verify(request).getHeader(CONVERSATION_ID_HEADER_NAME);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void destroyingFilterMessageIsLogged() {
    conversationLoggingFilter.destroy();

    verify(mockAppender).doAppend(captorLoggingEvent.capture());
    final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
    assertThat(loggingEvent.getLevel(), is(WARN));
    assertThat(loggingEvent.getFormattedMessage(),
        containsString("Destroying filter"));
  }

  @Test
  public void conversationIdIsNotRegisteredWithMDCWhenIdIsNull()
      throws Exception {
    when(request.getHeader(CONVERSATION_ID_HEADER_NAME)).thenReturn(null);
    try(MockedStatic<MDC> staticMock = Mockito.mockStatic(MDC.class)) {
      conversationLoggingFilter.doFilter(request, response, filterChain);
      staticMock.verify(() -> MDC.put(CONVERSATION_ID_KEY, CONVERSATION_ID), never());
      staticMock.verify(() -> MDC.remove(CONVERSATION_ID_KEY), never());
    }
  }
}
