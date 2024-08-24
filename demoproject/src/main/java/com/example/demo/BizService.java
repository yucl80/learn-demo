package com.example.demo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BizService {
    void test();
    List<List<BizDto>> test(List<String> list);
    List<String> hello(String name,int a);
    int getCount(List<BizDto> bizS, Map<String,BizDto> map, List<List<BizDto>> ndata, byte bdata, int idata, long ldata, Date date, char cdata, double ddata, float fdata);
}
