/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.auditable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 *
 * @author lalit ragahv
 */
    @Configuration
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class AuditConfig {

    @Bean
    AuditorAware<String> auditorProvider(TokenProvider tokenProvider) {
        return new AuditorAwareImpl(tokenProvider);
    }
}

