package com.spotify.apollo.client;

import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.spotify.apollo.Response;
import java.io.IOException;
import java.util.function.Function;
import okio.ByteString;
import org.slf4j.Logger;

/**
 * A generic JSON parser
 * @param <T> object type of the parsed response
 */
class JsonParser<T> implements Function<Response<ByteString>, T> {

  private static final Logger LOG = getLogger(JsonParser.class);
  private final ObjectReader objectReader;

  JsonParser(ObjectMapper objectMapper, Class<T> returnType) {
    this.objectReader = objectMapper.readerFor(returnType);
  }

  JsonParser(ObjectMapper objectMapper, TypeReference<T> returnType) {
    this.objectReader = objectMapper.readerFor(returnType);
  }

  @Override
  public T apply(Response<ByteString> response) {
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

}
