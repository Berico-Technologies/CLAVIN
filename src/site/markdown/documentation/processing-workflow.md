## Processing Workflow

If you\'re curious about how CLAVIN works, this section will describe the primary workflow: extracting and resolving location names from text.

CLAVIN\'s process for processing text consists of two distinct phases:

**Extraction** - CLAVIN uses a `LocationExtractor` to extract location name strings from text, producing a list a `LocationOccurrence` objects

**Resolution** - Taking the results of the extractor, CLAVIN\'s `LocationResolver` resolves each `LocationOccurrence` to the most likely `GeoName` it represents, yielding a `ResolvedLocation`

The Extraction and Resolution phases can be broken down even futher. These are the discrete steps comprising the entire processing workflow

1.  Plain text input is passed to CLAVIN

2.  Location names are extracted from the text as `LocationOccurrence` objects by the `LocationExtractor`

3.  For each `LocationOccurrence` object, the location name index is searched for all possible matches

4.  Using the collection of `LocationOccurrence` objects extracted from the text, along with the set of all possible matches for each `LocationOccurrence` in the location name index, the best `GeoName` object is selected for `LocationOccurrence`

5.  Resolution results are returned as a list of `ResolvedLocation` objects
