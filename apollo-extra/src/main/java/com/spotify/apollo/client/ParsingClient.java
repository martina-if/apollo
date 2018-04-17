package com.spotify.apollo.client;

import com.spotify.apollo.Request;
import java.util.concurrent.CompletionStage;

public interface ParsingClient<T> {

  CompletionStage<T> send(Request request);

}
