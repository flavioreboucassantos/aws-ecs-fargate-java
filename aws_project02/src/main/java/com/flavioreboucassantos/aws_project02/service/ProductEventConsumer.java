package com.flavioreboucassantos.aws_project02.service;

import java.io.IOException;
import java.time.Duration;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flavioreboucassantos.aws_project02.model.Envelope;
import com.flavioreboucassantos.aws_project02.model.ProductEvent;
import com.flavioreboucassantos.aws_project02.model.ProductEventLog;
import com.flavioreboucassantos.aws_project02.model.SnsMessage;
import com.flavioreboucassantos.aws_project02.repository.ProductEventLogRepository;

@Service
public class ProductEventConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(
			ProductEventConsumer.class);

	private ObjectMapper objectMapper;
	private ProductEventLogRepository productEventLogRepository;

	@Autowired
	public ProductEventConsumer(ObjectMapper objectMapper, ProductEventLogRepository productEventLogRepository) {
		this.objectMapper = objectMapper;
		this.productEventLogRepository = productEventLogRepository;
	}

	@JmsListener(destination = "${aws.sqs.queue.product.events.name}")
	//@JmsListener(destination = "${aws.sqs.queue.product.events.name}", concurrency = "4")
	public void receiveProductEvent(TextMessage textMessage) throws JMSException, IOException {
		
		SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
		
		Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);
		
		ProductEvent productEvent = objectMapper.readValue(envelope.getData(), ProductEvent.class);
		
		LOG.info("Product event received - MessageId: {} - Event: {} -  ProductId: {} ",
				snsMessage.getMessageId(),
				envelope.getEventType(),
				productEvent.getProductId());
		
		ProductEventLog productEventLog = buildProductEventLog(envelope, productEvent);
		productEventLogRepository.save(productEventLog);
	}
	
	private ProductEventLog buildProductEventLog(Envelope envelope, ProductEvent productEvent) {
		long timestamp = Instant.now().getMillis();
		ProductEventLog productEventLog = new ProductEventLog();
		productEventLog.setPk(productEvent.getCode());
		productEventLog.setSk(envelope.getEventType() + "_" + timestamp);
		productEventLog.setEventType(envelope.getEventType());
		productEventLog.setProductId(productEvent.getProductId());
		productEventLog.setUsername(productEvent.getUsername());
		productEventLog.setTimestamp(timestamp);
		productEventLog.setTtl(
				Instant.now().plus(Duration.ofMinutes(10).toMillis())
				.getMillis());
		
		return productEventLog;
	}
}
