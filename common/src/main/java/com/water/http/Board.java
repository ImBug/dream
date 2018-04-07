package com.water.http;

import com.water.json.BaseJsonObject;

/**
 * {
  "height": 220, // 在移动端展示的卡片高度
  "code": "alert-chart", // 看板的code（不能重复）（可以看下数据库里的数据格式）
  "name": "看板标题", // 看板名称
  "defaultSort": 1, // 看板默认排序
  "api": "/chatops/public/board/alert.html", // 展示该看板要请求的页面
  "targetUrl": "点击看板跳转的url页面地址", // 看板需要的webview link；若为空，则无下钻看板
  "isShow": true // 看板是否显示
},
 * @author honghm
 *
 */
public class Board extends BaseJsonObject{
	
	private int height=220;
	private String code;
	private String name;
	private int defaultSort=1;
	private String api;
	private String targetUrl = "";
	private boolean isShow = true;
	private String groupName="it";
	private String groupCode="it_perf";
	
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDefaultSort() {
		return defaultSort;
	}
	public void setDefaultSort(int defaultSort) {
		this.defaultSort = defaultSort;
	}
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	public boolean getIsShow() {
		return isShow;
	}
	public void setIsShow(boolean isShow) {
		this.isShow = isShow;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	
}
