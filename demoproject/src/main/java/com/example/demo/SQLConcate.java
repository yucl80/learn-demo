package com.example.demo;

import com.zaxxer.hikari.pool.HikariProxyCallableStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class SQLConcate {
    private void sqlExec(Connection conn){
        String sql =  "select  /* " + new Random().nextInt(200) + " */ * from (select position_str, batch_no, entrust_no, exchange_type" + ", op_entrust_way , stock_account, stock_code, stock_type, entrust_bs, entrust_price , entrust_amount, " + "business_amount, business_price, entrust_type, entrust_status , curr_date, entrust_prop, withdraw_amount, " + "business_balance, fund_account , init_date, report_milltime, curr_milltime, order_id, orig_order_id , " + "op_station, sub_stock_type, fare_kind, entrust_reference, extra_type , entrust_extra, return_code, " + "return_info, store_unit, seat_no , prev_balance, compact_id from ses_entrust where (stock_account = ? or trim" + "(?) is null) and (stock_code = ? or trim(?) is null) and (? != 1 or (? = 1 and (instr('02347CW', " + "entrust_status) > 0 and instr(',' || 'SGBG,QPSB,PGRG,ZT01,ZT03,DJJD,DJDJ,DJXD,DJLJ,TPSB' || ',', ',' || " + "entrust_prop || ',') <= 0 and case  when instr('15,16,18', substr(stock_code, 1, 2)) > 0 and stock_type != 'J" + "' and entrust_prop = '3' and exchange_type = '2' and '0' = ? and instr('2347CW', entrust_status) > 0 then 0 " + "else 1 end = 1 or (instr('7,8,', entrust_prop) > 0 and instr('02V', entrust_status) > 0) or (entrust_status =" + " 'V' and ((instr(',' || 'LFS,LFC,LFR,LFT,QNE,QNP' || ',', ',' || entrust_prop || ',') > 0 and exchange_type =" + " '1') or (entrust_prop = '3' and exchange_type = '2' and instr('18', substr(stock_code, 1, 2)) > 0 and " + "stock_type = 'J') or (instr(',' || 'LFP,LFM,W,89,L' || ',', ',' || entrust_prop || ',') > 0 and exchange_type" + " = '2') or (instr('15,16,18', substr(stock_code, 1, 2)) > 0 and stock_type != 'J' and instr(?, substr(" + "stock_code, 0, 3)) = 0 and entrust_prop = '3' and exchange_type = '2' and '1' = ?))) or (instr(',' || 'SGBG," + "QPSB,PGRG,ZT01,ZT03,DJJD,DJDJ,DJXD,DJLJ,TPSB' || ',', ',' || entrust_prop || ',') > 0 and instr('0V', " + "entrust_status) > 0)) and entrust_prop <> 'FBD')) and (? != 1 or (? = 1 and init_date = ?)) and (? != 3 or " + "instr('56789', entrust_status) > 0) and (exchange_type = ? or trim(?) is null) and (trim(?) is null or instr(" + "?, stock_type) > 0) and (trim(?) is null or instr(?, exchange_type) > 0) and (trim(?) is null or instr(?, " + "entrust_type) > 0) and (trim(?) is null or instr(entrust_extra, ?) > 0) and (trim(?) is null or instr(',' || " + "? || ',', ',' || entrust_prop || ',') > 0) and (order_id = ? or trim(?) is null) and (orig_order_id = ? or " + "trim(?) is null) and position_str > ? and (entrust_no = ? or ? = 0) and (batch_no = ? or ? = 0) and ((? = 1 " + "and entrust_type in ( '0',  '5',  '6',  '7',  '9',  'C',  'D' )) or (? != 1 and entrust_type in ( '0',  '5', " + " '6',  '7',  '8',  '9',  'C',  'D' )) or (? = '0' and entrust_type = '2')) and stock_type != '~' and " + "fund_account = ? and client_id = ? and (trim(?) is null or entrust_bs = ?) and (? = '0' or (? = '1' and " + "entrust_prop != 'N') or (? = '2' and (instr(',j,l,T,N,i,k,', ',' || stock_type || ',') > 0 or instr(?, substr" + "(stock_code, 0, 3)) > 0) or (? = '3' and (instr(',j,l,T,N,i,k,', ',' || stock_type || ',') <= 0 and instr(?, " + "substr(stock_code, 0, 3)) <= 0)))) and (trim(?) is null or instr(',' || ? || ',', ',' || entrust_prop || ',')" + " <= 0) and case  when ? = 1 and entrust_prop = 'VTE' then  case  when entrust_status in ('0', 'C') then 1 " + "else 0 end else 1 end = 1 and case  when ? = 1 and exchange_type = '1' and (trim(?) is null or instr(',' || ?" + " || ',', ',' || entrust_prop || ',') > 0) then  case  when entrust_status in ('0') then 1 else 0 end else 1 " + "end = 1 and (entrust_reference = ? or trim(?) is null) order by position_str) where rownum <= ?" ;
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            Statement statement = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void sqlExec(String rownum,Connection conn){
       
        String sql2=  "select * from (select position_str, batch_no, entrust_no, exchange_type, op_entrust_way , stock_account, " + "stock_code, stock_type, entrust_bs, entrust_price , entrust_amount, business_amount, business_price, " + "entrust_type, entrust_status , curr_date, entrust_prop, withdraw_amount, business_balance, fund_account , " + "init_date, report_milltime, curr_milltime, order_id, orig_order_id , op_station, sub_stock_type, fare_kind, " + "entrust_reference, extra_type , entrust_extra, return_code, return_info, store_unit, seat_no , prev_balance, " + "compact_id from ses_entrust where (stock_account = ? or trim(?) is null) and (stock_code = ? or trim(?) is " + "null) and (? != 1 or (? = 1 and (instr('02347CW', entrust_status) > 0 and instr(',' || 'SGBG,QPSB,PGRG,ZT01," + "ZT03,DJJD,DJDJ,DJXD,DJLJ,TPSB' || ',', ',' || entrust_prop || ',') <= 0 and case  when instr('15,16,18', " + "substr(stock_code, 1, 2)) > 0 and stock_type != 'J' and entrust_prop = '3' and exchange_type = '2' and '0' = " + "? and instr('2347CW', entrust_status) > 0 then 0 else 1 end = 1 or (instr('7,8,', entrust_prop) > 0 and instr" + "('02V', entrust_status) > 0) or (entrust_status = 'V' and ((instr(',' || 'LFS,LFC,LFR,LFT,QNE,QNP' || ',', '," + "' || entrust_prop || ',') > 0 and exchange_type = '1') or (entrust_prop = '3' and exchange_type = '2' and " + "instr('18', substr(stock_code, 1, 2)) > 0 and stock_type = 'J') or (instr(',' || 'LFP,LFM,W,89,L' || ',', ','" + " || entrust_prop || ',') > 0 and exchange_type = '2') or (instr('15,16,18', substr(stock_code, 1, 2)) > 0 and" + " stock_type != 'J' and instr(?, substr(stock_code, 0, 3)) = 0 and entrust_prop = '3' and exchange_type = '2' " + "and '1' = ?))) or (instr(',' || 'SGBG,QPSB,PGRG,ZT01,ZT03,DJJD,DJDJ,DJXD,DJLJ,TPSB' || ',', ',' || " + "entrust_prop || ',') > 0 and instr('0V', entrust_status) > 0)) and entrust_prop <> 'FBD')) and (? != 1 or (? " + "= 1 and init_date = ?)) and (? != 3 or instr('56789', entrust_status) > 0) and (exchange_type = ? or trim(?) " + "is null) and (trim(?) is null or instr(?, stock_type) > 0) and (trim(?) is null or instr(?, exchange_type) > " + "0) and (trim(?) is null or instr(?, entrust_type) > 0) and (trim(?) is null or instr(entrust_extra, ?) > 0) " + "and (trim(?) is null or instr(',' || ? || ',', ',' || entrust_prop || ',') > 0) and (order_id = ? or trim(?) " + "is null) and (orig_order_id = ? or trim(?) is null) and position_str > ? and (entrust_no = ? or ? = 0) and (" + "batch_no = ? or ? = 0) and ((? = 1 and entrust_type in ( '0',  '5',  '6',  '7',  '9',  'C',  'D' )) or (? != " + "1 and entrust_type in ( '0',  '5',  '6',  '7',  '8',  '9',  'C',  'D' )) or (? = '0' and entrust_type = '2'))" + " and stock_type != '~' and fund_account = ? and client_id = ? and (trim(?) is null or entrust_bs = ?) and (? " + "= '0' or (? = '1' and entrust_prop != 'N') or (? = '2' and (instr(',j,l,T,N,i,k,', ',' || stock_type || ',') " + "> 0 or instr(?, substr(stock_code, 0, 3)) > 0) or (? = '3' and (instr(',j,l,T,N,i,k,', ',' || stock_type || '" + ",') <= 0 and instr(?, substr(stock_code, 0, 3)) <= 0)))) and (trim(?) is null or instr(',' || ? || ',', ',' " + "|| entrust_prop || ',') <= 0) and case  when ? = 1 and entrust_prop = 'VTE' then  case  when entrust_status " + "in ('0', 'C') then 1 else 0 end else 1 end = 1 and case  when ? = 1 and exchange_type = '1' and (trim(?) is " + "null or instr(',' || ? || ',', ',' || entrust_prop || ',') > 0) then  case  when entrust_status in ('0') then" + " 1 else 0 end else 1 end = 1 and (entrust_reference = ? or trim(?) is null) order by position_str) where " + "rownum <= ?" + rownum;

         JdbcTemplate jdbcTemplate = new JdbcTemplate();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql2);

            Statement statement = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
