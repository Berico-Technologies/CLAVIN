# 5-Minute Tutorial 

This is the simplest possible example of using CLAVIN, following the code provided in `WorkflowDemo.java`. In this example, we will obtain a default instance of the `GeoParser` class to extract and resolve locations from a plain text file located at `src/test/resources/sample-docs/Somalia-doc.txt`.

### Before you start

1. Follow the [Installation](installation.html) instructions for obtaining CLAVIN.

2. Ensure CLAVIN in on your classpath, whether via an Apache Maven dependency or by directly importing the CLAVIN .jar file.

3. This example will make use of the following imports:

```
import java.io.File;
import java.util.List;
import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.GeoParserFactory;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.bericotech.clavin.util.TextUtils;
```

### Step 1. Instantiate the CLAVIN GeoParser

The `GeoParser` class is the core of the CLAVIN geoparsing workflow. To simplify the process of instantiating `GeoParser` objects, we provide a `GeoParserFactory` class that takes care of wiring up all the necessary resources based on the parameters you provide.

    GeoParser parser = GeoParserFactory.getDefault("./IndexDirectory");

Here, we pass the location of an embedded Lucene Index Directory that you built during step 6 of the [Installation](installation.html) instructions.

### Step 2. Load the input text

Now you need to load the text from which you want to extract and resolve location names. In this example, we will read-in a file from the local file system as a String.

```
String input = TextUtils.fileToString(
    new File("src/test/resources/sample-docs/Somalia-doc.txt"));
```

### Step 3. Extract and Resolve location names

This is where we use the `GeoParser` to extract and resolve location names from the text.

    List<ResolvedLocation> resolvedLocations = parser.parse(inputString);

### Step 4. Display the results

Finally, we\'ll iterate over the list of `ResolvedLocation` objects returned by the `GeoParser`, and print a description of each one to the console.

```
for (ResolvedLocation resolvedLocation : resolvedLocations)
    System.out.println(resolvedLocation);
```

### Step 5. Run the program

It's time to put it all together and run it. The complete code for this example is already provided for you in the `WorkflowDemo` class.

If you\'re using Apache Maven, running it is simple:

    MAVEN_OPTS="-Xmx2048M" mvn exec:java -Dexec.mainClass="com.bericotech.clavin.WorkflowDemo"

**Please note:** Loading the worldwide gazetteer uses a non-trivial amount of memory. If you encounter `Java heap space` errors when using CLAVIN, bump up the maximum heap size for your JVM. Allocating 2GB (e.g., `-Xmx2g`) is a good place to start. To do this in Eclipse, right-click on `WorkflowDemo.java` in the **Package Explorer** window, select **Run As** > **Run Configurations...**, then in the **Arguments** tab enter `-Xmx2g` in the **VM Arguments** input box, and finally click **Apply** then **Run**.

If successful, your output should look something like this:

```
Resolved "European Union" as: "European Union" {European Union (No Man's Land, ) [pop: 0] <6695072>}, position:1590, confidence: 1.0, fuzzy: false
Resolved "London" as: "London" {City of London (United Kingdom, ENG) [pop: 7556900] <2643741>}, position:2306, confidence: 1.0, fuzzy: false
Resolved "Africa" as: "Africa" {Mahdia (Tunisia, 15) [pop: 45977] <2473572>}, position:2586, confidence: 1.0, fuzzy: false
Resolved "Muna" as: "Muna" {Muna (Mexico, 31) [pop: 11363] <3522886>}, position:2957, confidence: 1.0, fuzzy: false
Resolved "Mataban" as: "Mataban" {Matabaan (Somalia, 07) [pop: 0] <54119>}, position:4161, confidence: 1.0, fuzzy: false
Resolved "Birmingham" as: "Birmingham" {Birmingham (United Kingdom, ENG) [pop: 984333] <2655603>}, position:4841, confidence: 1.0, fuzzy: false
Resolved "Hiran" as: "Hiran" {Hirān (Yemen, 20) [pop: 17] <8066871>}, position:5208, confidence: 1.0, fuzzy: false
Resolved "London" as: "London" {City of London (United Kingdom, ENG) [pop: 7556900] <2643741>}, position:6129, confidence: 1.0, fuzzy: false
Resolved "Africa" as: "Africa" {Mahdia (Tunisia, 15) [pop: 45977] <2473572>}, position:6724, confidence: 1.0, fuzzy: false
Resolved "East Africa" as: "Portuguese East Africa" {Republic of Mozambique (Mozambique, 00) [pop: 22061451] <1036973>}, position:7589, confidence: 1.0, fuzzy: false
Resolved "Africa" as: "Africa" {Mahdia (Tunisia, 15) [pop: 45977] <2473572>}, position:7620, confidence: 1.0, fuzzy: false
Resolved "London" as: "London" {City of London (United Kingdom, ENG) [pop: 7556900] <2643741>}, position:7643, confidence: 1.0, fuzzy: false
Resolved "Somali" as: "Somali" {Somalia (Somalia, 00) [pop: 10112453] <51537>}, position:11059, confidence: 1.0, fuzzy: false
Resolved "East" as: "East" {Purwanchal (Nepal, ER) [pop: 5344000] <7289708>}, position:11422, confidence: 1.0, fuzzy: false
Resolved "Kenya" as: "Kenya" {Republic of Kenya (Kenya, 00) [pop: 40046566] <192950>}, position:12822, confidence: 1.0, fuzzy: false
Resolved "Ethiopia" as: "Ethiopia" {Federal Democratic Republic of Ethiopia (Ethiopia, 00) [pop: 88013491] <337996>}, position:12832, confidence: 1.0, fuzzy: false
Resolved "Banadir" as: "Sar Banadir" {Sar Banādir (Afghanistan, 10) [pop: 0] <1127372>}, position:14545, confidence: 1.0, fuzzy: false
Resolved "Muna" as: "Muna" {Muna (Mexico, 31) [pop: 11363] <3522886>}, position:15403, confidence: 1.0, fuzzy: false
Resolved "Kismayo" as: "Kismayo" {Kismayo (Somalia, 09) [pop: 234852] <55671>}, position:15778, confidence: 1.0, fuzzy: false
Resolved "Parliament" as: "Parliament" {Parliament (Canada, 08) [pop: 0] <6098672>}, position:16710, confidence: 1.0, fuzzy: false
Resolved "Puntland" as: "Puntland" {Puntland (Somalia, 00) [pop: 0] <8220543>}, position:17873, confidence: 1.0, fuzzy: false
Resolved "Sool" as: "Sool" {Sool (Switzerland, GL) [pop: 301] <2658552>}, position:18102, confidence: 1.0, fuzzy: false
Resolved "Somalia" as: "Somalia" {Somalia (Somalia, 00) [pop: 10112453] <51537>}, position:18126, confidence: 1.0, fuzzy: false
Resolved "Somalia" as: "Somalia" {Somalia (Somalia, 00) [pop: 10112453] <51537>}, position:18224, confidence: 1.0, fuzzy: false
Resolved "Sool" as: "Sool" {Sool (Switzerland, GL) [pop: 301] <2658552>}, position:18307, confidence: 1.0, fuzzy: false
Resolved "Kenya" as: "Kenya" {Republic of Kenya (Kenya, 00) [pop: 40046566] <192950>}, position:18718, confidence: 1.0, fuzzy: false
Resolved "Kenya" as: "Kenya" {Republic of Kenya (Kenya, 00) [pop: 40046566] <192950>}, position:19151, confidence: 1.0, fuzzy: false
Resolved "Gedo" as: "Gedo" {Gēdo (Ethiopia, 51) [pop: 7499] <337083>}, position:19404, confidence: 1.0, fuzzy: false
Resolved "Kenya" as: "Kenya" {Republic of Kenya (Kenya, 00) [pop: 40046566] <192950>}, position:19428, confidence: 1.0, fuzzy: false
Resolved "Russia" as: "Russia" {Russian Federation (Russia, 00) [pop: 140702000] <2017370>}, position:19476, confidence: 1.0, fuzzy: false
Resolved "Muna" as: "Muna" {Muna (Mexico, 31) [pop: 11363] <3522886>}, position:20487, confidence: 1.0, fuzzy: false
Resolved "Britain" as: "Britain" {United Kingdom of Great Britain and Northern Ireland (United Kingdom, 00) [pop: 62348447] <2635167>}, position:22502, confidence: 1.0, fuzzy: false
Resolved "Africa" as: "Africa" {Mahdia (Tunisia, 15) [pop: 45977] <2473572>}, position:22753, confidence: 1.0, fuzzy: false
Resolved "Britain" as: "Britain" {United Kingdom of Great Britain and Northern Ireland (United Kingdom, 00) [pop: 62348447] <2635167>}, position:23251, confidence: 1.0, fuzzy: false
Resolved "Yemen" as: "Yemen" {Republic of Yemen (Yemen, 00) [pop: 23495361] <69543>}, position:23352, confidence: 1.0, fuzzy: false
Resolved "Nigeria" as: "Nigeria" {Federal Republic of Nigeria (Nigeria, 00) [pop: 154000000] <2328926>}, position:23362, confidence: 1.0, fuzzy: false
Resolved "Iraq" as: "Iraq" {Republic of Iraq (Iraq, 00) [pop: 29671605] <99237>}, position:23404, confidence: 1.0, fuzzy: false
```

#### That\'s it!

That probably didn\'t even take five full minutes, did it?
