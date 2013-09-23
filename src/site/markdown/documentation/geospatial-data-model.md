## Geospatial Data Model

CLAVIN, at its core, is a tool for deriving geospatial information from text data. To best leverage CLAVIN for your needs, it\'s important to understand CLAVIN\'s data model.

### Gazetteer Model

CLAVIN\'s worldwide view of geospatial "ground truth" is represented by the gazetteer model. It contains a massive collection of **named locations** -- the cities, countries, mountains, rivers, airports, etc. that you are most likely using CLAVIN to find. It also contains a semantic model for describing these geospatial entities. CLAVIN's gazetteer model is primarily based on the [GeoNames.org](http://geonames.org) geographical database.

The gazetteer model located in the `com.bericotech.clavin.gazetteer` package contains these elements:

-  `CountryCode` - a enumeration of all countries in the world

-  `FeatureClass` - a top-level category for classifying locations; some classes include administrative regions, populated places, hydrographic features (e.g., lakes, rivers), and "spot" features (e.g., buildings, farms)

-  `FeatureCode` - a more specific category for classifying locations; for instance, spot features are further divided into categories such as schools, restaurants, churches, etc.

-  `GeoName` - the first-class object in CLAVIN's gazetteer model; represents some named location in the world; consists of the three elements listed above, plus many additional attributes including name, latitude/longitude coordinates, population, elevation, etc.

### Extraction Model

CLAVIN\'s extraction model deals with unresolved occurrences of resolvable geospatial entities extracted from text. The root concept of the extraction model is an *occurrence*. An occurrence is that piece of text in the document that represents the geospatial entity (e.g., a name extracted by a named entity recognizer). All occurrences contain the text that was extracted as well as the starting character position for where the text was found in the source document. You can calculate the *end offset* simply by adding the length of the occurrence to the start position:

```
int endOffset = locationOccurrence.position + locationOccurrence.text.length();
```

The extraction model located in the `com.bericotech.clavin.extractor` package contains this element:

-  `LocationOccurrence` - a named location found in the text (e.g., `Boston`, `Armenia`, `River Thames`)

### Resolution Model

Once locations have been extracted from the text, you\'ll want to resolve those locations to geospatial entities (represented by the `GeoName` class). The resolution model is how you interact with these entities once the resolution process is finished.

The resolution model located in the `com.bericotech.clavin.resolver` package contains this element:

-  `ResolvedLocation` - a `LocationOccurrence` paired with a `GeoName`

The gazeetteer may contain many possible `GeoName` entities matching each `LocationOccurrence` extracted from a text document. CLAVIN uses a set of heuristics and optimization techniques to identify the most likely `GeoName` represented by a `LocationOccurrence`. For example, if the location name `Springfield` was extracted from text, CLAVIN's resolution process attempts to find the *correct* "Springfield" (e.g., Illinois, Missouri, Massachusetts) as intended by the author based on the context of the document.

### CLAVIN Services

A number of service interfaces make up the CLAVIN workflow. Implementing these interfaces is a great way to extend CLAVIN to suit your needs without having to reengineer the entire process.

The two primary interfaces are:

-  `LocationExtractor` - takes a `String` and returns a list of `LocationOccurrence` objects

-  `LocationResolver` - takes a list of `LocationOccurrence` objects and returns a list of `ResolvedLocation` objects

The default implementations of these interfaces are:

-  `ApacheExtractor` - uses the **Apache OpenNLP NameFinder** named entity recognizer tool to extract location names from text

-  `LuceneLocationResolver` - uses an **Apache Lucene** document index to resolve location names to `GeoName`-based gazetteer records

### Summary

We hope this document provides some insight into the intent behind CLAVIN\'s data model. Please keep in mind that as CLAVIN continues to mature and evolve, there will be some modifications to the model in future releases.
