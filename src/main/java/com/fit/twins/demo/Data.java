package com.fit.twins.demo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Data {
    private List<Post> data;
    private IgComments.Paging paging;
}
