package com.example.demo.model;

import java.util.Date;

//@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
public class Auditable<U> {

  //  @CreatedBy
  //  @Column(name = "created_by")
    private U createdBy;


  //  @CreatedDate
  //  @Column(name = "created_date")
    private Date createdDate;

  //  @LastModifiedBy
  //  @Column(name = "last_modified_by")
    private U lastModifiedBy;


  //  @LastModifiedDate
  //  @Column(name = "last_modified_date")
    private Date lastModifiedDate;


	public U getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(U createdBy) {
		this.createdBy = createdBy;
	}


	public Date getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


	public U getLastModifiedBy() {
		return lastModifiedBy;
	}


	public void setLastModifiedBy(U lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}


	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}


	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}