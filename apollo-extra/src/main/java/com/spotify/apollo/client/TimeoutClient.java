package com.spotify.apollo.client;

import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import okio.ByteString;

public final class TimeoutClient implements Client {

  private final Client delegate;
  private final Duration timeout;

  public TimeoutClient create(Client delegate, Duration timeout) {
    return new TimeoutClient(delegate, timeout);
  }

  private TimeoutClient(Client delegate, Duration timeout) {
    this.delegate = delegate;
    this.timeout = timeout;
  }

  @Override
  public CompletionStage<Response<ByteString>> send(Request request) {
    final Request requestWithTimeout = request.withTtl(timeout);
    return delegate.send(requestWithTimeout);
  }
}
