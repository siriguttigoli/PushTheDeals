package com.virtualkarma.pushthedeals.parser;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Parses an RSS feed and adds the information to a new
 * RSSFeed object. Has the ability to report progress to a
 * ProgressBar if one is passed to the constructor.
 *
 * @author Isaac Whitfield
 * @version 06/08/2013
 */
public class DOMParser {

    // Create a new RSS feed
    private RSSFeed feed = new RSSFeed();

    public RSSFeed parseXML(String feedURL) {

        // Create a new URL
        URL url = null;
        try {
            // Find the new URL from the given URL
            url = new URL(feedURL);
        } catch (MalformedURLException e) {
            // Throw an exception
            e.printStackTrace();
        }

        try {
            // Create a new DocumentBuilder
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            // Parse the XML
            Document doc = builder.parse(new InputSource(url.openStream()));
            // Normalize the data
            doc.getDocumentElement().normalize();

            // Get all <item> tags.
            NodeList list = doc.getElementsByTagName("item");
            // Get size of the list
            int length = list.getLength();

            // For all the items in the feed
            for (int i = 0; i < length; i++) {
                // Create a new node of the first item
                Node currentNode = list.item(i);
                // Create a new RSS item
                RSSItem item = new RSSItem();

                // Get the child nodes of the first item
                NodeList nodeChild = currentNode.getChildNodes();
                // Get size of the child list
                int cLength = nodeChild.getLength();

                // For all the children of a node
                for (int j = 0; j < cLength; j++) {
                    // Get the name of the child
                    String nodeName = nodeChild.item(j).getNodeName(), nodeString = null;
                    // If there is at least one child element
                    if (nodeChild.item(j).getFirstChild() != null) {
                        // Set the string to be the value of the node
                        nodeString = nodeChild.item(j).getFirstChild().getNodeValue();
                    }
                    // If the string isn't null
                    if (nodeString != null) {
                        // Set the appropriate value
                        if ("title".equals(nodeName)) {
                            item.setTitle(nodeString);
                        } else if (("content:encoded".equals(nodeName)) || ("description".equals(nodeName))) {
                            //Parse the html description to get the image url
                            String html = nodeString;
                            org.jsoup.nodes.Document docHtml = Jsoup.parse(html);
                            Elements imgEle = docHtml.select("img");
                            item.setImgUrl(imgEle.attr("src"));
                            if (imgEle.attr("height").equals("") || imgEle.attr("width").equals("")) {
                                item.setImgHeight(0);
                                item.setImgWidth(0);
                            } else {
                                item.setImgHeight(Integer.parseInt(imgEle.attr("height")));
                                item.setImgWidth(Integer.parseInt(imgEle.attr("width")));
                            }
                            //Parse only text from description
                            String text = Jsoup.parse(html).text();
                            item.setDescription(text);
                        } else if (("pubDate".equals(nodeName)) || ("dc:date".equals(nodeName))) {
                            item.setDate(nodeString.replace(" +0000", ""));
                        } else if ("author".equals(nodeName) || "dc:creator".equals(nodeName)) {
                            item.setAuthor(nodeString);
                        } else if ("link".equals(nodeName)) {
                            item.setURL(nodeString);
                        } else if ("thumbnail".equals(nodeName)) {
                            item.setThumb(nodeString);
                        }
                    }
                }
                // Add the new item to the RSS feed
                feed.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Return the feed
        return feed;
    }
}
