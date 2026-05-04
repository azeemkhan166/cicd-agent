package com.realcoderz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realcoderz.model.Form16;

@Repository
public interface Form16Repository extends JpaRepository<Form16, Long> {

	Form16 findByEmployeeIdAndYear(Long employeeId, int year);
	Form16 findByCertificateNoAndYear(String certificateNo, int year);
}
