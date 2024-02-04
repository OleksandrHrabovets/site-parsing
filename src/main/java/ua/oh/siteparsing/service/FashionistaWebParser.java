package ua.oh.siteparsing.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FashionistaWebParser {
  @Value("${app.url}")
  private String url;

  @PostConstruct
  public Elements parseUrl() throws IOException {

    Document document = Jsoup.connect(url).get();
    Elements elementsByClass = document.getElementsByClass("product-title");
    elementsByClass.forEach(element -> log.info("Element: {}", element));
    return elementsByClass;

  }

}
