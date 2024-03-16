package org.test.ser.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Cat implements Animal {

    private String name;

    private Cat children;

    private Cat parent;

    public void setNewChildren(Cat children) {
        this.children = children;
    }
}
