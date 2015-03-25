package com.glacier.spider.crawler.pipeline;

import java.util.HashMap;

/**
 * Created by glacier on 14-12-17.
 */
public class WeiboStruct {
    private String weiboText;
    private String weiboSender, senderURL;
    private String weiboForward, forwardReason;
    private String weiboImage;
    private String weiboLikeCount, weiboForwardCount, weiboCommentCount;
    private String weiboDate, weiboFrom;
    private String weiboID;
    private String weiboUser, userLink;

    public String getWeiboID() {
        return weiboID;
    }

    public void setWeiboID(String weiboID) {
        this.weiboID = weiboID;
    }

    public void setWeiboText( String weiboText ) {     this.weiboText = weiboText; }
    public void setWeiboSender( String weiboSender ) {     this.weiboSender = weiboSender; }
    public void setSenderURL( String senderURL ) { this.senderURL = senderURL;  }
    public void setWeiboForward( String weiboForward ) {       this.weiboForward = weiboForward; }
    public void setForwardReason( String forwardReason ) {     this.forwardReason = forwardReason; }
    public void setWeiboImage( String weiboImage ) {    this.weiboImage = weiboImage;   }
    public void setWeiboLikeCount( String weiboLikeCount ) {    this.weiboLikeCount = weiboLikeCount;   }
    public void setWeiboForwardCount( String weiboForwardCount ) {  this.weiboForwardCount = weiboForwardCount; }
    public void setWeiboCommentCount( String weiboCommentCount ) {  this.weiboCommentCount = weiboCommentCount; }
    public void setWeiboDate( String weiboDate ) {  this.weiboDate = weiboDate; }
    public void setWeiboFrom( String weiboFrom ) {  this.weiboFrom = weiboFrom; }

    public String getWeiboText() { return weiboText;    }
    public String getWeiboSender() {       return weiboSender; }
    public String getSenderURL() {     return senderURL;    }
    public String getWeiboForward() {      return weiboForward; }
    public String getForwardReason() {     return forwardReason; }
    public String getWeiboImage() { return weiboImage;  }
    public String getWeiboLikeCount() { return weiboLikeCount;  }
    public String getWeiboForwardCount() {  return weiboForwardCount;   }
    public String getWeiboCommentCount() {  return weiboCommentCount;   }
    public String getWeiboDate() {  return weiboDate;   }
    public String getWeiboFrom() {  return weiboFrom;   }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("weibo id: " + weiboID + "\n");
        buffer.append(weiboSender + " : " + weiboText + "\n");
        if ( !weiboForward.equals("") && !forwardReason.equals("") ) {
            buffer.append(weiboSender + " " + weiboForward + " - " + forwardReason + "\n");
        }
        if ( !weiboImage.equals("") ) {
            buffer.append("image - " + weiboImage + "\n");
        }
        buffer.append("like: " + weiboLikeCount + "\tforward: " + weiboForwardCount + "\tcommment: " + weiboCommentCount + "\n");
        buffer.append("date: " + weiboDate + "\tfrom: " + weiboFrom + "\n");

        return buffer.toString();
    }
}
