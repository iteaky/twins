package com.fit.twins.demo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class UserInfo {
    private List<UserData> data;


    @NoArgsConstructor
    @Getter
    @Setter
    public class UserData {
        private String name;
        private Long id;
    }
}
