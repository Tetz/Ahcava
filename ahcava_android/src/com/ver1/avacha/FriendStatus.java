package com.ver1.avacha;

import android.graphics.Bitmap;

public class FriendStatus {
	private String name;
	private String text;
	private Bitmap png;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getPng() {
		return png;
	}

	public void setPng(Bitmap png) {
		this.png = png;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
