package org.example.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.common.model.SchemaSyncTable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest{

    @Test
    public void testTree(){
        SchemaSyncTable t1 = new SchemaSyncTable();
        t1.setParentId(0);
        t1.setId(1);

        SchemaSyncTable t2 = new SchemaSyncTable();
        t2.setParentId(0);
        t2.setId(2);


        SchemaSyncTable t3 = new SchemaSyncTable();
        t3.setParentId(1);
        t3.setId(3);

        SchemaSyncTable t4 = new SchemaSyncTable();
        t4.setParentId(1);
        t4.setId(4);


        SchemaSyncTable t5 = new SchemaSyncTable();
        t5.setParentId(2);
        t5.setId(5);


        SchemaSyncTable t6 = new SchemaSyncTable();
        t6.setParentId(2);
        t6.setId(6);


        SchemaSyncTable t7 = new SchemaSyncTable();
        t7.setParentId(6);
        t7.setId(7);
        List<SchemaSyncTable>resultList= new ArrayList<>();
        resultList.add(t1);
        resultList.add(t2);
        resultList.add(t3);
        resultList.add(t4);
        resultList.add(t5);
        resultList.add(t6);
        resultList.add(t7);
        List<SchemaSyncTable> treeNodeList = resultList.stream()
                .filter(item -> {
                    item.setChildren(
                            resultList.stream()
                                    .filter(e -> Objects.equals(e.getParentId(), item.getId()))
                                    .collect(Collectors.toList()));
                    return Objects.equals(item.getParentId(), 0);
                })
                .collect(Collectors.toList());

        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(treeNodeList));
    }
}