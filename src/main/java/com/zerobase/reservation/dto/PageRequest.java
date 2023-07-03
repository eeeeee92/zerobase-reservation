package com.zerobase.reservation.dto;

import com.zerobase.reservation.dto.Sort;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PageRequest {

    private int pageNum;
    private int pageSize;
    List<Sort> sort = new ArrayList<>();

}
