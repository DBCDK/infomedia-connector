/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.infomedia;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Article {

    @JsonProperty("Heading")
    private String heading;

    @JsonProperty("SubHeading")
    private String subHeading;

    @JsonProperty("BodyText")
    private String bodyText;

    @JsonProperty("PageIds")
    @JacksonXmlProperty(localName = "PageId")
    @JacksonXmlElementWrapper(localName = "PageIds")
    private List<String> pageIds;

    @JsonProperty("PublishDate")
    private String publishDate;

    @JsonProperty("Authors")
    @JacksonXmlProperty(localName = "Author")
    @JacksonXmlElementWrapper(localName = "Authors")
    private List<String> authors;

    @JsonProperty("Captions")
    @JacksonXmlProperty(localName = "Caption")
    @JacksonXmlElementWrapper(localName = "Captions")
    private List<String> captions;

    @JsonProperty("ArticleUrl")
    private String articleUrl;

    @JsonProperty("Paragraph")
    private String paragraph;

    @JsonProperty("Source")
    private String source;

    @JsonProperty("WordCount")
    private Integer wordCount;

    @JsonProperty("ArticleId")
    private String articleId;

    @JsonProperty("Section")
    private Section section;

    @JsonProperty("Lead")
    private String lead;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public List<String> getPageIds() {
        return pageIds;
    }

    public void setPageIds(List<String> pageIds) {
        this.pageIds = pageIds;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getCaptions() {
        return captions;
    }

    public void setCaptions(List<String> captions) {
        this.captions = captions;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    @Override
    public String toString() {
        return "Article{" +
                "heading='" + heading + '\'' +
                ", subHeading='" + subHeading + '\'' +
                ", bodyText='" + bodyText + '\'' +
                ", pageIds=" + pageIds +
                ", publishDate='" + publishDate + '\'' +
                ", authors=" + authors +
                ", captions=" + captions +
                ", articleUrl='" + articleUrl + '\'' +
                ", paragraph='" + paragraph + '\'' +
                ", source='" + source + '\'' +
                ", wordCount=" + wordCount +
                ", articleId='" + articleId + '\'' +
                ", section=" + section +
                ", lead='" + lead + '\'' +
                '}';
    }
}
