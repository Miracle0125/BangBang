package com.yzx.bangbang.module;

public class ReplierInfo {
	public String user_name, asm_title, message, date;
	public int user_id, asm_id;
	public float price;

	public ReplierInfo(String user_name, String asm_title, String message, String date, int user_id, int asm_id,
			float price) {
		this.user_name = user_name;
		this.asm_title = asm_title;
		this.message = message;
		this.date = date;
		this.user_id = user_id;
		this.asm_id = asm_id;
		this.price = price;
	}

	public String getUserName() {
		return user_name;
	}

	public String getAsmTitle() {
		return asm_title;
	}

	public String getMessage() {
		return message;
	}

	public String getDate() {
		return date;
	}

	public int getUserId() {
		return user_id;
	}

	public int getAsmId() {
		return asm_id;
	}
	
	public float getPrice(){
		return price;
	}
}
