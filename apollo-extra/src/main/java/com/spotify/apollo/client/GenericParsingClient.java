package com.spotify.apollo.client;

import static org.slf4j.LoggerFactory.getLogger;

import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import okio.ByteString;
import org.slf4j.Logger;

public class GenericParsingClient<T> implements ParsingClient<T> {

  private static final Logger LOG = getLogger(GenericParsingClient.class);
  private final Client delegate;
  private final Function<Response<ByteString>, T> parsingFunction;
  private final T fallbackValue;
  private final Duration timeout;


  public GenericParsingClient<T> create(Client delegate,
                                        Function<Response<ByteString>, T> parsingFunction) {
    return new GenericParsingClient<>(delegate, parsingFunction, null, null);
  }

  public GenericParsingClient<T> withFallbackValue(T fallbackValue) {
    return new GenericParsingClient<>(this.delegate, this.parsingFunction, fallbackValue, null);
  }

  public GenericParsingClient<T> withTimeout(Duration timeout) {
    return new GenericParsingClient<>(this.delegate, this.parsingFunction, null, timeout);
  }

  private GenericParsingClient(
      Client client, Function<Response<ByteString>, T> parsingFunction, T fallbackValue,
      Duration timeout) {
    this.delegate = client;
    this.parsingFunction = parsingFunction;
    this.fallbackValue = fallbackValue;
    this.timeout = timeout;
  }

  @Override
  public CompletionStage<T> send(Request request) {

    // Optionally set a timeout
    final Request actualRequest;
    if (timeout != null) {
      actualRequest = request.withTtl(timeout);
    } else {
      actualRequest = request;
    }

    CompletionStage<T> parsedResult = delegate.send(actualRequest)
        .thenApply(parsingFunction);

    // Optionally set a fallback value
    if (fallbackValue == null) {
      return parsedResult;
    }

    return parsedResult.exceptionally(throwable -> {
      LOG.warn("Error while requesting {}. Using fallback value", request.uri(), throwable);
      return fallbackValue;
    });
  }
}
