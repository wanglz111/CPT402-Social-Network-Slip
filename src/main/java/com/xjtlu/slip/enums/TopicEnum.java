package com.xjtlu.slip.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TopicEnum implements IEnum<Integer> {
    News(1,"news"), //新闻
    Work(2, "work"), //工作
    Interests(3, "interests"), //兴趣
    Life(4, "life"), //生活
    Campus(5, "campus"), //校园
    Other(6, "other"); //其他

    private final String name;
    private final int index;

    TopicEnum(int index, String name) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public Integer getValue() {
        return this.index;
    }

}
