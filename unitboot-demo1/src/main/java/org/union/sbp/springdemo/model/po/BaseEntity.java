package org.union.sbp.springdemo.model.po;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseEntity<ID extends Serializable>  implements Serializable {

	private static final long serialVersionUID = 2054813493011812469L;

	private ID id;
	private Date createTime;
	private Date updateTime = new Date();

	private String createrid;
	private String updaterid;

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public Date getCreateTime() {
		if(null == createTime){
			createTime = new Date();
		}
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreaterid() {
		return createrid;
	}

	public void setCreaterid(String createrid) {
		this.createrid = createrid;
	}

	public String getUpdaterid() {
		return updaterid;
	}

	public void setUpdaterid(String updaterid) {
		this.updaterid = updaterid;
	}
}