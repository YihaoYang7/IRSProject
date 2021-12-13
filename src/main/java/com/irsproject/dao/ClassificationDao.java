package com.irsproject.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class ClassificationDao
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public LinkedList<String>  queryCatalogues()
    {
        String sql = "select catalog_name from catalog";
        LinkedList<String> results = new LinkedList<>();
        jdbcTemplate.query(sql, resultSet -> {
            results.add((String) resultSet.getObject("catalog_name"));
        });
        return results;
    }
}
