package com.bericotech.clavin;

import java.io.IOException;
import java.util.List;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Simple tool that fetches a web page, strips the html and prints the tags.
 */
public class WebPageTagger {
    private static final Logger LOG = LoggerFactory.getLogger(WebPageTagger.class);

    private final GeoParser parser;
    private final HttpClient httpClient;

    public WebPageTagger(HttpClient httpClient, GeoParser parser) {
        this.httpClient = httpClient;
        this.parser = parser;
    }

    private String toText(String html) {
        Source segment = new Source(html);
        TextExtractor textExtractor = new TextExtractor(segment);
        textExtractor.setConvertNonBreakingSpaces(true);
        textExtractor.setExcludeNonHTMLElements(true);
        textExtractor.setIncludeAttributes(false);
        String text = textExtractor.toString();
        return text;
    }

    private  String fetchHtml(String url) throws IOException {
        String html = httpClient.execute(new HttpGet(url), new ResponseHandler<String>() {

            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                if(response.getStatusLine().getStatusCode() < 300) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    LOG.warn("error " + response.getStatusLine() + "\n" + EntityUtils.toString(response.getEntity()));
                    return null;
                }
            }
        });
        LOG.trace("fetched {}, {}", url, html);
        return html;
    }

    public List<ResolvedLocation> parse(String url) throws Exception {
        LOG.info("fetching {}", url);
        String html = fetchHtml(url);

        if(html != null) {
            String text = toText(html);
            LOG.trace("raw text {}", html);
            LOG.info("extracting locations");
            List<ResolvedLocation> resolvedLocations = parser.parse(text);
            LOG.info("DONE");
            return resolvedLocations;
        } else {
            throw new IllegalStateException("server returned no response");
        }
    }

    public static void main(String[] args) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnPerRoute(5)
                .setUserAgent("Clavin WebPageTagger")
                .build();
        GeoParser parser = GeoParserFactory.getDefault("./IndexDirectory");
        WebPageTagger urlTagger = new WebPageTagger(httpClient, parser);

        String url;
        if(args.length > 0) {
            url=args[0];
            List<ResolvedLocation> resolvedLocations = urlTagger.parse(url);

            for (ResolvedLocation resolvedLocation : resolvedLocations) {
                System.out.println(resolvedLocation);
            }
        } else {
            System.err.println("Please provide a url as the first parameter.");
        }
    }
}
