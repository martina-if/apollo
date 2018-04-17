package com.spotify.apollo.client;

import com.spotify.apollo.Request;
import java.util.concurrent.CompletionStage;

/**
 * A parameterized client that returns parsed objects instead of raw responses.
 */
public interface ParsingClient<T> {

  CompletionStage<T> send(Request request);

}
