package com.flavioreboucassantos.aws_project02.service;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flavioreboucassantos.aws_project02.model.Envelope;
import com.flavioreboucassantos.aws_project02.model.ProductEvent;
import com.flavioreboucassantos.aws_project02.model.SnsMessage;

@Service
public class ProductEventConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(
			ProductEventConsumer.class);

	private ObjectMapper objectMapper;

	@Autowired
	public ProductEventConsumer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
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
	}
}
