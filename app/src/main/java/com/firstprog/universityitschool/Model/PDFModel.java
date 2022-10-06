package com.firstprog.universityitschool.Model;

public class PDFModel {

    String pdfName, pdfUploadDate, pdfUrl, pdfsMainChildID;

    public PDFModel(){}

    public PDFModel(String pdfName, String pdfUploadDate, String pdfUrl, String pdfsMainChildID) {
        this.pdfName = pdfName;
        this.pdfUploadDate = pdfUploadDate;
        this.pdfUrl = pdfUrl;
        this.pdfsMainChildID = pdfsMainChildID;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getPdfUploadDate() {
        return pdfUploadDate;
    }

    public void setPdfUploadDate(String pdfUploadDate) {
        this.pdfUploadDate = pdfUploadDate;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfsMainChildID() {
        return pdfsMainChildID;
    }

    public void setPdfsMainChildID(String pdfsMainChildID) {
        this.pdfsMainChildID = pdfsMainChildID;
    }
}
