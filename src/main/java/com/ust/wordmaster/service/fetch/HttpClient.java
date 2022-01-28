package com.ust.wordmaster.service.fetch;

import java.net.URI;

public interface HttpClient {

    String fetchHtml(URI url);
}
