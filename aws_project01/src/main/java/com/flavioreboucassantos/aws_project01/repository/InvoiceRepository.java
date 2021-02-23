package com.flavioreboucassantos.aws_project01.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.flavioreboucassantos.aws_project01.model.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
	Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

	List<Invoice> findAllByCustomerName(String customerName);
}
