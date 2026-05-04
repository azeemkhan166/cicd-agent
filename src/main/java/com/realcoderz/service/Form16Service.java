package com.realcoderz.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.realcoderz.model.Form16;

@Service
public interface Form16Service {

	public Map save(Form16 data);
	public Map get(Form16 data);

}
