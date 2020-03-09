package com.fit.twins.demo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class IgComments {
    private List<IgComment2> data = new ArrayList<>();
    private Paging paging;


    @NoArgsConstructor
    @Getter
    @Setter
    public class Paging {

        Cursors cursors;
        String next;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public class IgComment2 {
        private String username;
        private String text;
        private String id;
    }


    @NoArgsConstructor
    @Getter
    @Setter
    private class Cursors {
        String after;
    }
}
