package com.spotify.apollo.client;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectReader;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import java.io.IOException;
import java.util.concurrent.CompletionStage;
import okio.ByteString;
import org.slf4j.Logger;

public class JsonParsingClient<T> implements ParsingClient<T> {

  private static final Logger LOG = getLogger(JsonParsingClient.class);
  private final ParsingClient<T> delegate;
  private final ObjectReader objectReader;

  public JsonParsingClient create(Client client, ObjectReader objectReader) {
    final GenericParsingClient<T> delegate = GenericParsingClient.create(client, this::parse);
    return new JsonParsingClient<>(delegate, objectReader);
  }

  public JsonParsingClient create(ParsingClient<T> delegate, ObjectReader objectReader) {
    return new JsonParsingClient<>(delegate, objectReader);
  }

  private JsonParsingClient(ParsingClient<T> delegate, ObjectReader objectReader) {
    this.delegate = delegate;
    this.objectReader = objectReader;
  }

  private T parse(Response<ByteString> response) {
    if (!response.payload().isPresent()) {
      LOG.debug("No value in response to parse");
      return null;
    }

    try {
      return objectReader.readValue(response.payload().get().utf8());
    } catch (IOException e) {
      LOG.warn("Unable to parse response from {}", response.toString(), e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public CompletionStage<T> send(Request request) {
    return delegate.send(request);
  }
}
