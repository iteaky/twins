package com.fit.twins;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;

@AllArgsConstructor
@org.springframework.web.bind.annotation.RestController
public class RestController {
    private MessageBean messageBean;

    @GetMapping("/winner")
    public String getWinner(@RequestParam String link) throws IOException {

        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        return messageBean.getMessage(details, link, new ArrayList<>());
    }

}
