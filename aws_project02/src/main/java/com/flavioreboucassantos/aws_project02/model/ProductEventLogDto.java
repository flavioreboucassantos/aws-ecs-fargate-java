package com.flavioreboucassantos.aws_project02.model;

import com.flavioreboucassantos.aws_project02.enums.EventType;

public class ProductEventLogDto {

	private final String messageId;
	private final String code;
	private final EventType eventType;
	private final long productId;
	private final String username;
	private final long timestamp;

	public ProductEventLogDto(ProductEventLog productEventLog) {
		this.messageId = productEventLog.getMessageId();
		this.code = productEventLog.getPk();
		this.eventType = productEventLog.getEventType();
		this.productId = productEventLog.getProductId();
		this.username = productEventLog.getUsername();
		this.timestamp = productEventLog.getTimestamp();
	}

	public String getMessageId() {
		return messageId;
	}

	public String getCode() {
		return code;
	}

	public EventType getEventType() {
		return eventType;
	}

	public long getProductId() {
		return productId;
	}

	public String getUsername() {
		return username;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
}
