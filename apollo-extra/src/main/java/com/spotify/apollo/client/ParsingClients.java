package com.spotify.apollo.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.apollo.Client;
import java.time.Duration;

/**
 * Factory for creating ParsingClients
 */
public class ParsingClients {

  /**
   * Create a JsonParsingClient from a Class
   */
  public static <T> ParsingClientBuilder<T> newJsonParsingClient(Client client,
                                                                 ObjectMapper objectMapper,
                                                                 Class<T> returnType) {

    JsonParser<T> jsonParser = new JsonParser<>(objectMapper, returnType);
    return new ParsingClientBuilder<T>(client, jsonParser, null);
  }

  /**
   * Create a JsonParsingClient from a TypeReference
   */
  public static <T> ParsingClientBuilder<T> newJsonParsingClient(Client client,
                                                                 ObjectMapper objectMapper,
                                                                 TypeReference<T> returnType) {

    JsonParser<T> jsonParser = new JsonParser<>(objectMapper, returnType);
    return new ParsingClientBuilder<T>(client, jsonParser, null);
  }

  static class ParsingClientBuilder<T> {

    private final Client client;
    private final JsonParser<T> jsonParser;
    private final T fallbackValue;

    ParsingClientBuilder(Client client, JsonParser<T> jsonParser, T fallbackValue) {

      this.client = client;
      this.jsonParser = jsonParser;
      this.fallbackValue = fallbackValue;
    }

    ParsingClientBuilder<T> withFallback(T fallbackValue) {
      return new ParsingClientBuilder<>(this.client, this.jsonParser, fallbackValue);
    }

    ParsingClientBuilder<T> withTimeout(Duration timeout) {
      final Client client = TimeoutClient.create(this.client, timeout);
      return new ParsingClientBuilder<>(client, this.jsonParser, this.fallbackValue);
    }

    ParsingClient<T> build() {
      return new GenericParsingClient<>(client, jsonParser, fallbackValue);
    }
  }


}
