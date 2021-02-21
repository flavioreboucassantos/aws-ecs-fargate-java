package com.flavioreboucassantos.aws_project02.model;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.flavioreboucassantos.aws_project02.enums.EventType;

@DynamoDBTable(tableName = "product-events")
public class ProductEventLog {
	public ProductEventLog() {
	}

	@Id
	private ProductEventKey productEventKey;
	
	@DynamoDBTypeConvertedEnum
	@DynamoDBAttribute(attributeName = "eventType")
	private EventType eventType;
	
	@DynamoDBAttribute(attributeName = "productId")
	private long productId;
	
	@DynamoDBAttribute(attributeName = "username")
	private String username;

	@DynamoDBAttribute(attributeName = "timestamp")
	private long timestamp;
	
	@DynamoDBAttribute(attributeName = "ttl")
	private long ttl;
	
	@DynamoDBHashKey(attributeName = "pk")
	public String getPk() {
		return productEventKey != null ? productEventKey.getPk() : null;
	}
	
	public void setPk(String pk) {
		if(productEventKey == null) {
			productEventKey = new ProductEventKey();
		}
		productEventKey.setPk(pk);
	}
	
	@DynamoDBHashKey(attributeName = "sk")
	public String getSk() {
		return productEventKey != null ? productEventKey.getSk() : null;
	}
	
	public void setSk(String sk) {
		if(productEventKey == null) {
			productEventKey = new ProductEventKey();
		}
		productEventKey.setSk(sk);
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}
	
}
