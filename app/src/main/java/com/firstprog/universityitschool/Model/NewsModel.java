package com.firstprog.universityitschool.Model;

public class NewsModel {
    String newsMainChildName, newsTitle, newsUrl, news;

    public NewsModel(){}

    public NewsModel(String newsMainChildName, String newsTitle, String newsUrl, String news) {
        this.newsMainChildName = newsMainChildName;
        this.newsTitle = newsTitle;
        this.newsUrl = newsUrl;
        this.news = news;
    }

    public String getNewsMainChildName() {
        return newsMainChildName;
    }

    public void setNewsMainChildName(String newsMainChildName) {
        this.newsMainChildName = newsMainChildName;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }
}
