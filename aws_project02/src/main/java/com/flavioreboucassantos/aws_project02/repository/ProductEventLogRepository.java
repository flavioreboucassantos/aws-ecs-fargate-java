package com.flavioreboucassantos.aws_project02.repository;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.flavioreboucassantos.aws_project02.model.ProductEventKey;
import com.flavioreboucassantos.aws_project02.model.ProductEventLog;

@EnableScan
public interface ProductEventLogRepository extends CrudRepository<ProductEventLog, ProductEventKey> {

	List<ProductEventLog> findAllByPk(String code);
	
	List<ProductEventLog> findAllByPkAndSkStartsWith(String code, String eventType);
}
