# Overview

### Description

<img src="images/clavin_logo.png" style="float:right; height: 14em; margin: 25px" />

CLAVIN (Cartographic Location And Vicinity INdexer) is an award-winning open source software package for document geotagging and geoparsing that employs context-based geographic entity resolution.

It extracts location names from unstructured text and resolves them against a gazetteer to produce data-rich geographic entities.

CLAVIN does not simply "look up" location names – it uses intelligent heuristics to identify exactly which "Springfield" (for example) was intended by the author, based on the context of the document. CLAVIN also employs fuzzy search to handle incorrectly-spelled location names, and it recognizes alternative names (e.g., "Ivory Coast" and "Côte d\'Ivoire") as referring to the same geographic entity.

By enriching text documents with structured geo data, CLAVIN enables hierarchical geospatial search and advanced geospatial analytics on unstructured data.

### History

CLAVIN got its start as an attempt to address a requirement to "geo-rectify" plain text location names for a customer of Berico Technologies. At the time, we were building the first incarnation of Rivium4, a full-text search & discovery platform that uses geospatial entities as data facets. Originally, another organization was given the responsibility for extracting & resolving geospatial locations from our source data, but for multiple reasons, that other organization was unable to deliver this capability. Since geospatial search was an indispensable requirement for the platform we were building, Charlie Greenbacker and other Berico developers started building their own geoparser in earnest. Within two months, the team had successfully developed the first iteration of what came to be known as CLAVIN.

CLAVIN was deployed into production at the customer site in the summer of 2012, as a key component of the customer's analytic pipeline built on top of Apache Hadoop. CLAVIN has since been deployed into three more of Berico's customer environments, and it also in use at several other companies and academic institutions.
 
**Origin of the Name**  

CLAVIN is named in honor of a lovable, know-it-all postal worker from a famous 1980s sitcom. CLAVIN is also a **backronym**, "a phrase specially constructed so that an acronym fits an existing word" [Wikipedia ~ Backronym](http://en.wikipedia.org/wiki/Backronym). As a bonus, the name gave us an excuse to use a postmark with a mustache as the logo.	
 
### License

Copyright (C) 2012-2013 Berico Technologies

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.