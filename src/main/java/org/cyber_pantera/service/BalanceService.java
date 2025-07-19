package org.cyber_pantera.service;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BalanceService {

    @Value("${balance.service.url}")
    private String balanceServiceUrl;

    private final RestTemplate restTemplate;

    public void initUserBalance(User user) {
        var headers = new HttpHeaders();
        var request = new HttpEntity<>(headers);
        var url = balanceServiceUrl + "/init?userId=" + user.getId();

        restTemplate.exchange(url, HttpMethod.POST, request, Void.class);
    }
}
