package com.heriec.blogmaster.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitCount {

    Integer id;
    Integer uv;
    Integer pv;
}
