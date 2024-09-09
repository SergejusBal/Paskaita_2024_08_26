package Renginys.Paskaita_2024_08_26.Models;

public class MailResponse {

    private int id;
    private String to;
    private String title;
    private String responseCode;
    private String responseBody;
    private String responseHeader;

    public MailResponse() {
    }

    public int getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public String getTitle() {
        return title;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }
}
