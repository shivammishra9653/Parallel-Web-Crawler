package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path
   *
   * @return the loaded {@link CrawlerConfiguration}.
   */
  public CrawlerConfiguration load() {
    // TODO: Fill in this method.

//    create Filereader Object
       try(Reader reader = new FileReader(path.toFile())){
         return read(reader);

       }catch(Exception ex){
         return new CrawlerConfiguration.Builder().build();
       }


  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  public static CrawlerConfiguration read(Reader reader) {
    // This is here to get rid of the unused variable warning.
    Objects.requireNonNull(reader);
    // TODO: Fill in this method

    ObjectMapper mapper = new ObjectMapper();
    try{
      mapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
      CrawlerConfiguration crcf = mapper.readValue(reader, CrawlerConfiguration.class);
      return crcf;
    }catch(Exception ex){
      ex.printStackTrace();
      return new CrawlerConfiguration.Builder().build();
    }
  }
}
