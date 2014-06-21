package com.micromate.mreader.database;

public class Feed {
	private int _id;
	private String title;
	private String link;
	private String rssLink;
	private String description;
	private int unreadQuantity;
	
	
	public Feed(){}
	
	public Feed(String title, String link, String rssLink, String description) {
		this.title = title;
		this.link = link;
		this.rssLink = rssLink;
		this.description = description;
	}
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getRssLink() {
		return rssLink;
	}
	public void setRssLink(String rssLink) {
		this.rssLink = rssLink;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int getUnreadQuantity() {
		return unreadQuantity;
	}

	public void setUnreadQuantity(int unreadQuantity) {
		this.unreadQuantity = unreadQuantity;
	}
	
	

}
