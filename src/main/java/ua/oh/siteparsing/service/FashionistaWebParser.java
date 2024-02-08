package ua.oh.siteparsing.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FashionistaWebParser {

  public static final String PRODUCT_TITLE = "product-title";
  @Value("${app.url}")
  private String url;

  //  @PostConstruct
  public Elements parseUrlByClass() throws IOException {

    Document document = Jsoup.connect(url).get();
    String className = PRODUCT_TITLE;
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

  @SneakyThrows
  @PostConstruct
  public List<ProductRecord> parseProductsList() {

    Document document = Jsoup.connect(url).get();
    String query = "article";
    Elements elementsBySelect = document.select(query);

    return elementsBySelect.stream()
        .map(this::mapToProductRecord
        ).toList();

  }

  private ProductRecord mapToProductRecord(Element element) {
    String idString = element.attr("data-id-product");
    String title = element.getElementsByClass(PRODUCT_TITLE).first().text();
    String href = element.getElementsByClass(PRODUCT_TITLE).first().childNodes().get(0)
        .attr("href");
    String description = element.getElementsByClass("product-description-short").first().text();
    String priceString = element.getElementsByClass("price").first().childNodes().get(2)
        .attr("content");
    List<Color> colors = parseProductColors(href);
    ProductRecord productRecord = new ProductRecord(Long.parseLong(idString), title, description,
        href, Double.parseDouble(priceString), colors, parseProductSizes(href),
        parseProductImages(href, colors));
    log.info("Product {}", productRecord);
    return productRecord;
  }

  @SneakyThrows
  private List<Color> parseProductColors(String url) {
    List<Color> list = new ArrayList<>();

    Document document = Jsoup.connect(url).get();
    Elements elements = document.getElementsByClass("product-variants-item");
    elements.forEach(element -> element.getElementsByClass("color")
        .forEach(color -> list.add(new Color(color.text(),
            color.attr("style").substring("background-color: ".length())))));
    return list;

  }

  @SneakyThrows
  private Map<Color, List<String>> parseProductImages(String url, List<Color> colors) {
    final String[] colorString = {""};
    Map<Color, List<String>> images = new HashMap<>();
    List<String> list = new ArrayList<>();

    Document document = Jsoup.connect(url).get();
    Elements elements = document.getElementsByClass("product-thumb-images");
    elements.forEach(element -> element.getElementsByClass("thumb")
        .forEach(image -> {
          list.add(image.attr("data-image-large-src"));
          String[] array = image.attr("alt").split(" ");
          colorString[0] = array[array.length - 1];

        }));
    images.put(colors.stream()
        .filter(color -> color.name.toLowerCase().equals(colorString[0])).findFirst()
        .orElseThrow(), list);
    return images;

  }

  @SneakyThrows
  private List<String> parseProductSizes(String url) {
    List<String> list = new ArrayList<>();

    Document document = Jsoup.connect(url).get();
    Elements elements = document.getElementsByClass("product-variants-item");
    elements.forEach(element -> element.getElementsByClass("input-radio")
        .forEach(size -> list.add(((Element) size.parentNode()).text())));
    return list;

  }


  public record ProductRecord(Long id, String title, String Description, String url, Double price,
                              List<Color> colors, List<String> sizes,
                              Map<Color, List<String>> images) {

  }

  public record Color(String name, String background) {

  }
}
