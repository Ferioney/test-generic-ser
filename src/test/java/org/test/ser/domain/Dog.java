package org.test.ser.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Dog implements Animal {

    private String name;

    private Dog children;

    private Dog parent;

    public void setNewChildren(Dog children) {
        this.children = children;
    }
}
