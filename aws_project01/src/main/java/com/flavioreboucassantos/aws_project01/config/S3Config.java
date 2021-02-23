package com.flavioreboucassantos.aws_project01.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Config {

	@Value("${aws.region}")
	private String awsRegion;

	@Bean
	public AmazonS3 amazonS3Client() {
		return AmazonS3ClientBuilder.standard()
				.withRegion(awsRegion)
				.withCredentials(new DefaultAWSCredentialsProviderChain())
				.build();
	}

}
