package com.example.seerfqr;


public class  Model {

    private String history_item_type;
    private String history_item_summary;
    private String history_item_date;

    public Model(String history_item_type, String history_item_summary, String history_item_date) {
        this.history_item_type = history_item_type;
        this.history_item_summary = history_item_summary;
        this.history_item_date = history_item_date;
    }

    public String getHistory_item_type() {
        return history_item_type;
    }

    public void setHistory_item_type(String history_item_type) {
        this.history_item_type = history_item_type;
    }

    public String getHistory_item_summary() {
        return history_item_summary;
    }

    public void setHistory_item_summary(String history_item_summary) {
        this.history_item_summary = history_item_summary;
    }

    public String getHistory_item_date() {
        return history_item_date;
    }

    public void setHistory_item_date(String history_item_date) {
        this.history_item_date = history_item_date;
    }
}
