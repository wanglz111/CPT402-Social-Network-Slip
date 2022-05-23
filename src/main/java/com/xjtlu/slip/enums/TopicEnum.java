package com.xjtlu.slip.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TopicEnum implements IEnum<Integer> {
    News(1,"news"), //news
    Work(2, "work"), //work
    Interests(3, "interests"), //interests
    Life(4, "life"), //life
    Campus(5, "campus"), //campus
    Questions(6, "questions"), //Q&A
    Other(7, "other"); //other

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
