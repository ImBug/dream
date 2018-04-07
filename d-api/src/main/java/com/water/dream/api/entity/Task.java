package com.water.dream.api.entity;

import java.io.Serializable;

import com.water.dream.api.dic.Hardness;
import com.water.dream.api.dic.Importance;
import com.water.dream.api.dic.Interest;
import com.water.dream.api.dic.Priority;
import com.water.dream.api.dic.TaskState;
import com.water.json.BaseJsonObject;

/**
 * 
 * @author honghm
 *
 */
public class Task extends BaseJsonObject implements Cloneable,Serializable{

	private static final long serialVersionUID = 1L;
	
	private long id;
	private long parentid;
	private String topic;
	private String title;
	private String desc;
	private Interest interest;
	private Importance importance;
	private Hardness hardness;
	private Priority priority;
	private long recordtime;
	private long start;
	private long end;
	private long deadline;
	private TaskState state;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getParentid() {
		return parentid;
	}
	public void setParentid(long parentid) {
		this.parentid = parentid;
	}
	public long getRecordtime() {
		return recordtime;
	}
	public void setRecordtime(long recordtime) {
		this.recordtime = recordtime;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Interest getInterest() {
		return interest;
	}
	public void setInterest(Interest interest) {
		this.interest = interest;
	}
	public Importance getImportance() {
		return importance;
	}
	public void setImportance(Importance importance) {
		this.importance = importance;
	}
	public Hardness getHardness() {
		return hardness;
	}
	public void setHardness(Hardness hardness) {
		this.hardness = hardness;
	}
	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public long getDeadline() {
		return deadline;
	}
	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}
	public TaskState getState() {
		return state;
	}
	public void setState(TaskState state) {
		this.state = state;
	}
	
}
