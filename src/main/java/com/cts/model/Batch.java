package com.cts.model;

public class Batch {

	private String id;
	private int chequeCount;
	private String totalAmount;
	private String status; // PENDING, SENT, APPROVED, REJECTED
	private String dateReceived;
	private String approvedOn;
	private String rejectedOn;

	public Batch() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getChequeCount() {
		return chequeCount;
	}

	public void setChequeCount(int chequeCount) {
		this.chequeCount = chequeCount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(String dateReceived) {
		this.dateReceived = dateReceived;
	}

	public String getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(String approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getRejectedOn() {
		return rejectedOn;
	}

	public void setRejectedOn(String rejectedOn) {
		this.rejectedOn = rejectedOn;
	}
}