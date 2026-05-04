/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.auditable;



import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;

/**
 *
 * @author lalit raghav
 */
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final Logger logger = LoggerFactory.getLogger(AuditorAwareImpl.class);

    private final TokenProvider tokenProvider;


    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String token = BearerTokenUtil.getBearerTokenHeader();
            long userId = tokenProvider.getUserIdFromToken(token.substring(7, token.length()));
            System.out.println("TokenProvider"+userId);
            return Optional.of(String.valueOf(userId));
        } catch (Exception ex) {
            logger.error("Problem in AuditorAwareImpl :: getCurrentAuditor() => " + ex);
            return Optional.of("0");
        }
    }

}