package org.example.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.empire.db.DBContext;
import org.apache.empire.db.list.DataBean;
import org.example.common.BaseDb;

import java.time.LocalDateTime;

public class BaseModel  implements DataBean<BaseDb> {

    /**自增id*/
    private Integer id;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createdTime;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updatedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    @Override
    public void initialize(BaseDb baseDb, DBContext dbContext, int i, Object o) {

    }
}
