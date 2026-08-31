package com.certify.model;

public class FieldPosition {
    private long id;
    private long seminarId;
    private String fieldKey;
    private double xPercent;
    private double yPercent;
    private double widthPercent;
    private int fontSize;
    private String fontColor;
    private boolean fontBold;
    private String textAlign;

    public static FieldPosition defaults(long seminarId, FormField field, double yPercent) {
        FieldPosition p = new FieldPosition();
        p.seminarId = seminarId;
        p.fieldKey = field.key();
        p.xPercent = 15;
        p.yPercent = yPercent;
        p.widthPercent = 70;
        p.fontSize = field == FormField.FULL_NAME ? 48 : 22;
        p.fontColor = "#1a2744";
        p.fontBold = field == FormField.FULL_NAME;
        p.textAlign = "center";
        return p;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getSeminarId() { return seminarId; }
    public void setSeminarId(long seminarId) { this.seminarId = seminarId; }
    public String getFieldKey() { return fieldKey; }
    public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }
    public double getXPercent() { return xPercent; }
    public void setXPercent(double xPercent) { this.xPercent = xPercent; }
    public double getYPercent() { return yPercent; }
    public void setYPercent(double yPercent) { this.yPercent = yPercent; }
    public double getWidthPercent() { return widthPercent; }
    public void setWidthPercent(double widthPercent) { this.widthPercent = widthPercent; }
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    public String getFontColor() { return fontColor; }
    public void setFontColor(String fontColor) { this.fontColor = fontColor; }
    public boolean isFontBold() { return fontBold; }
    public void setFontBold(boolean fontBold) { this.fontBold = fontBold; }
    public String getTextAlign() { return textAlign; }
    public void setTextAlign(String textAlign) { this.textAlign = textAlign; }
}
