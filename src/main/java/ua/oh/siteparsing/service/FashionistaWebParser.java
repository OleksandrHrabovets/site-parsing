package ua.oh.siteparsing.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
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

  //  @PostConstruct
  public Elements parseUrlByClass() throws IOException {

    Document document = Jsoup.connect(url).get();
    String className = "product-title";
    Elements elementsByClass = document.getElementsByClass(className);
    elementsByClass.forEach(element -> log.info("Element by class '{}': {}", className, element));
    return elementsByClass;

  }

  //  @PostConstruct
  public Elements parseUrlByAttribute() throws IOException {

    Document document = Jsoup.connect(url).get();
    String attr = "href";
    Elements elementsByClass = document.getElementsByAttribute(attr);
    elementsByClass.forEach(element -> log.info("Element by attribute '{}': {}", attr, element));
    return elementsByClass;

  }

  @PostConstruct
  public List<ProductRecord> parseProductsList() throws IOException {

    Document document = Jsoup.connect(url).get();
    String query = "div.ajax_block_product";
    Elements elementsBySelect = document.select(query);

    return elementsBySelect.stream().map(element -> {
          String idString = element.getElementsByAttribute("data-id-product").first()
              .attr("data-id-product");
          String title = element.getElementsByClass("product-title").first().text();
          String url = element.getElementsByClass("product-title").first().childNodes().get(0)
              .attr("href");
          String description = element.getElementsByClass("product-description-short").first().text();
          String priceString = element.getElementsByClass("price").first().childNodes().get(2)
              .attr("content");
          ProductRecord productRecord = new ProductRecord(Long.parseLong(idString), title, description,
              url, Double.parseDouble(priceString));
          log.info("Product {}", productRecord);
          return productRecord;
        }
    ).toList();

  }

  public record ProductRecord(Long id, String title, String Description, String url, Double price) {

  }
}
