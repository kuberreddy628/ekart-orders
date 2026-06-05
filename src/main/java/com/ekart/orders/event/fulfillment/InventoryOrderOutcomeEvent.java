package com.ekart.orders.event.fulfillment;

public class InventoryOrderOutcomeEvent {

	private Long orderId;
	private String failureReason;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
}
