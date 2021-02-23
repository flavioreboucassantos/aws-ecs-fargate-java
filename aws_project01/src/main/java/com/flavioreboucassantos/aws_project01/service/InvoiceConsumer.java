package com.flavioreboucassantos.aws_project01.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flavioreboucassantos.aws_project01.model.Invoice;
import com.flavioreboucassantos.aws_project01.model.SnsMessage;
import com.flavioreboucassantos.aws_project01.repository.InvoiceRepository;

@Service
public class InvoiceConsumer {
	
    private static final Logger log = LoggerFactory.getLogger(
            InvoiceConsumer.class);
    
    private ObjectMapper objectMapper;
    private InvoiceRepository invoiceRepository;
    private AmazonS3 amazonS3;

    @Autowired
    public InvoiceConsumer(ObjectMapper objectMapper, InvoiceRepository invoiceRepository, AmazonS3 amazonS3) {
        this.objectMapper = objectMapper;
        this.invoiceRepository = invoiceRepository;
        this.amazonS3 = amazonS3;
    }

    @JmsListener(destination = "${aws.sqs.queue.invoice.events.name}")
    public void receiveS3Event(TextMessage textMessage)
            throws JMSException, IOException {
        SnsMessage snsMessage = objectMapper.readValue(textMessage.getText(),
                SnsMessage.class);
        
        S3EventNotification s3EventNotification = objectMapper
                .readValue(snsMessage.getMessage(), S3EventNotification.class);

        processInvoiceNotification(s3EventNotification);
    }

    private void processInvoiceNotification(S3EventNotification
                                                    s3EventNotification) throws IOException {
        for (S3EventNotification.S3EventNotificationRecord
                s3EventNotificationRecord : s3EventNotification.getRecords()) {
            S3EventNotification.S3Entity s3Entity =
                    s3EventNotificationRecord.getS3();

            String bucketName = s3Entity.getBucket().getName();
            String objectKey = s3Entity.getObject().getKey();

            String invoiceFile = downloadObject(bucketName, objectKey);

            Invoice invoice = objectMapper.readValue(invoiceFile, Invoice.class);
            log.info("Invoice received: {}", invoice.getInvoiceNumber());

            invoiceRepository.save(invoice);

            amazonS3.deleteObject(bucketName, objectKey);
        }
    }

    private String downloadObject(String bucketName, String objectKey)
            throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, objectKey);

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(s3Object.getObjectContent()));
        String content = null;
        while ((content = bufferedReader.readLine()) != null) {
            stringBuilder.append(content);
        }
        return stringBuilder.toString();
    }
}
