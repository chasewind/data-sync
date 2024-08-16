package org.example.common;

import com.google.gson.GsonBuilder;
import org.example.common.model.SearchTable;
import org.example.common.shard.NormalModShard;
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
    public void testShard(){
        //16个表截取[9,11]位置的三个数据，最大长度为2，小于10不补零，直接取模，不用hash处理
        NormalModShard shard = new NormalModShard(16,2,9,11);

        shard.setFillZero(false);
        shard.setUseHash(false);
        shard.setUseCut(true);
        System.out.println(shard.getShardIndex("24030300947906337404"));
        //1024个表，直接hash取模，小于10则补前置0
        NormalModShard shard2= new NormalModShard(256,4,0,0);
        shard2.setFillZero(true);
        shard2.setUseHash(true);
        shard2.setUseCut(false);
        System.out.println(shard2.getShardIndex("24030300947906337404"));

    }

    @Test
    public void testTree(){
        SearchTable t1 = new SearchTable();
        t1.setParentId(0);
        t1.setId(1);

        SearchTable t2 = new SearchTable();
        t2.setParentId(0);
        t2.setId(2);


        SearchTable t3 = new SearchTable();
        t3.setParentId(1);
        t3.setId(3);

        SearchTable t4 = new SearchTable();
        t4.setParentId(1);
        t4.setId(4);


        SearchTable t5 = new SearchTable();
        t5.setParentId(2);
        t5.setId(5);


        SearchTable t6 = new SearchTable();
        t6.setParentId(2);
        t6.setId(6);


        SearchTable t7 = new SearchTable();
        t7.setParentId(6);
        t7.setId(7);
        List<SearchTable>resultList= new ArrayList<>();
        resultList.add(t1);
        resultList.add(t2);
        resultList.add(t3);
        resultList.add(t4);
        resultList.add(t5);
        resultList.add(t6);
        resultList.add(t7);
        List<SearchTable> treeNodeList = resultList.stream()
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
