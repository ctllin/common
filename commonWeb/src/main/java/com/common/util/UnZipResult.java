package com.common.util;

public  class UnZipResult{
	private  boolean deleteFlag=false;
	public UnZipResult(){
	}
	public UnZipResult(boolean deleteFlag){
		this.deleteFlag=deleteFlag;
	}
	public boolean isDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
}