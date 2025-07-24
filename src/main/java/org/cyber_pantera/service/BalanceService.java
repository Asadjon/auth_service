package org.cyber_pantera.service;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BalanceService {

    @Value("${balance.service.url}")
    private String balanceServiceUrl;

    private final RestTemplate restTemplate;

    public CompletableFuture<Void> initUserBalance(User user) {
        var headers = new HttpHeaders();
        var request = new HttpEntity<>(headers);
        var url = balanceServiceUrl + "/init?userId=" + user.getId();

        return CompletableFuture.supplyAsync(() ->
                restTemplate.exchange(url, HttpMethod.POST, request, Void.class))
                .exceptionally(ex ->
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
                }).thenApply(ResponseEntity::getBody);
    }
}
