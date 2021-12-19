package com.ust.wordmaster.service.fetching;

import java.net.URI;

public interface HttpClient {

    String fetchHtml(URI url);
}
