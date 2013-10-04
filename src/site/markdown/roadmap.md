# Roadmap

There is a laundry list of features we plan on adding to the open source distribution of CLAVIN. This list, while not necessarily exhaustive, should include most of them.

### Analytic Improvements
-  Filter out demonyms (e.g., American, British, Chinese)
-  Bias resolution towards popular locations
-  Improve handling of abbreviations (like US states)
-  Incorporate proximity between locations as a weight for resolving location names

### API Changes
-  Simplify GeoParserFactory and/or GeoParser constructors
-  Add support for multiple Extractors and Resolvers as plugins/extensions

### Feature Enhancements
-  Return output as a ranked list of locations
-  Resolve IP addresses
-  Resolve telephone numbers by area code
-  Configure IndexBuilder to work with the OpenStreetMap gazetteer
-  Better workflow for resolving from structured data (e.g., Excel, CSV)
