package com.app.settingApp.languageKeyboard;

public class KeyboardBean {

	private boolean isCheck;
	
	private String name;

	public KeyboardBean() {
		
	}

	public KeyboardBean(boolean isCheck, String name) {
		this.isCheck = isCheck;
		this.name = name;
	}

	/**
	 * @return the  {@link #isCheck}
	 */
	public boolean isCheck() {
		return isCheck;
	}

	/**
	 * @param isCheck the {@link #isCheck} to set
	 */
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	/**
	 * @return the  {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the {@link #name} to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
}
