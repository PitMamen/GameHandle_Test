package com.app.settingApp.deviceinfo;

public class DeviceInfoBean {

	private String name;
	
	private String info;
	
	public DeviceInfoBean() {
	}

	public DeviceInfoBean(String name, String info) {
		this.name = name;
		this.info = info;
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

	/**
	 * @return the  {@link #info}
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @param info the {@link #info} to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
}
