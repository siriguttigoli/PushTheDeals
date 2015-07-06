package com.virtualkarma.pushthedeals.domain;

/**
 * Created by sirig on 6/16/15.
 */
public class DealSite {

    private String name;
    private String link;
    private int numOfDeals;
    private boolean enableNotification;

    public DealSite() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getNumOfDeals() {
        return numOfDeals;
    }

    public void setNumOfDeals(int numOfDeals) {
        this.numOfDeals = numOfDeals;
    }

    public boolean isEnableNotification() {
        return enableNotification;
    }

    public void setEnableNotification(boolean enableNotification) {
        this.enableNotification = enableNotification;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        result.append("Site name - " + name + NEW_LINE);
        result.append("Link - " + link);

        return result.toString();
    }
}
