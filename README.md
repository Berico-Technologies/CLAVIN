CLAVIN
======

[![Build Status](https://travis-ci.org/Berico-Technologies/CLAVIN.png?branch=master)](https://travis-ci.org/Berico-Technologies/CLAVIN)

CLAVIN (*Cartographic Location And Vicinity INdexer*) is an open source software package for document geotagging and geoparsing that employs context-based geographic entity resolution. It combines a variety of open source tools with natural language processing techniques to extract location names from unstructured text documents and resolve them against gazetteer records. Importantly, CLAVIN does not simply "look up" location names; rather, it uses intelligent heuristics in an attempt to identify precisely which "Springfield" (for example) was intended by the author, based on the context of the document. CLAVIN also employs fuzzy search to handle incorrectly-spelled location names, and it recognizes alternative names (e.g., "Ivory Coast" and "Côte d'Ivoire") as referring to the same geographic entity. By enriching text documents with structured geo data, CLAVIN enables hierarchical geospatial search and advanced geospatial analytics on unstructured data.

How to use a CLAVIN release:
---------------------------

1. Download the latest stable release
        > `curl -L -O https://github.com/Berico-Technologies/CLAVIN/releases/download/1.0.0rc6/clavin-1.0.0rc6-bin.tar.gz`

2. Extract the release to a directory of your choice
        > `tar xzvf clavin-1.0.0rc6-bin.tar.gz`

3. Configure the environment variables CLAVIN_HOME, CLAVIN_OPTS and PATH  
        > `export CLAVIN_HOME=/type-in-your-path-to/clavin-1.0.0rc6`
        > `export CLAVIN_OPTS=-Xmx2048M`
        > `export PATH=$PATH:$CLAVIN_HOME/bin`  
        > `chmod u+x $CLAVIN_HOME/bin/clavin`

3. Download the latest version of allCountries.zip gazetteer file from GeoNames.org:
        > `curl -O http://download.geonames.org/export/dump/allCountries.zip`

4. Unzip the GeoNames gazetteer file:
        > `unzip allCountries.zip`

5. Create the Lucene Index (this one-time process will take several minutes):
        > `clavin exec index.IndexDirectoryBuilder` 
         
6. Run example program:
        > `clavin exec WorkflowDemo`


How to build & use CLAVIN:
--------------------------

1. Check out a copy of the source code:
	> `git clone https://github.com/Berico-Technologies/CLAVIN.git`

2. Move into the newly-created CLAVIN directory:
	> `cd CLAVIN`

3. Download the latest version of allCountries.zip gazetteer file from GeoNames.org:
	> `curl -O http://download.geonames.org/export/dump/allCountries.zip`

4. Unzip the GeoNames gazetteer file:
	> `unzip allCountries.zip`

5. Compile the source code:
	> `mvn compile`

6. Create the Lucene Index (this one-time process will take several minutes):
	> `MAVEN_OPTS="-Xmx2048M" mvn exec:java -Dexec.mainClass="com.bericotech.clavin.index.IndexDirectoryBuilder"`

7. Run the example program:
	> `MAVEN_OPTS="-Xmx2048M" mvn exec:java -Dexec.mainClass="com.bericotech.clavin.WorkflowDemo"`
	
	If you encounter an error that looks like this:
	> `... InvocationTargetException: Java heap space ...`
	
	set the appropriate environmental variable controlling Maven's memory usage, and increase the size with `export MAVEN_OPTS=-Xmx3g` or similar.

Once that all runs successfully, feel free to modify the CLAVIN source code to suit your needs.

**N.B.**: Loading the worldwide gazetteer uses a non-trivial amount of memory. When using CLAVIN in your own programs, if you encounter `Java heap space` errors (like the one described in Step 8), bump up the maximum heap size for your JVM. Allocating 2GB (e.g., `-Xmx2g`) is a good place to start.

* Add a dependency on the CLAVIN project:

```xml
<dependency>
   <groupId>com.bericotech</groupId>
   <artifactId>clavin</artifactId>
   <version>1.0.0rc6</version>
</dependency>
```

>  You will still need to build the GeoNames Lucene Index as described in steps 3, 4, and 6 in "How to build & use CLAVIN".

License:
--------

Copyright (C) 2012-2013 Berico Technologies

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
