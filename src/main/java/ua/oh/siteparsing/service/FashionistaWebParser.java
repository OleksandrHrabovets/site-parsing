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
  public Elements parseUrlByClass() throws IOException {

    Document document = Jsoup.connect(url).get();
    String className = "product-title";
    Elements elementsByClass = document.getElementsByClass(className);
    elementsByClass.forEach(element -> log.info("Element by class '{}': {}", className, element));
    return elementsByClass;

  }

  @PostConstruct
  public Elements parseUrlByAttribute() throws IOException {

    Document document = Jsoup.connect(url).get();
    String attr = "href";
    Elements elementsByClass = document.getElementsByAttribute(attr);
    elementsByClass.forEach(element -> log.info("Element by attribute '{}': {}", attr,  element));
    return elementsByClass;

  }

}
