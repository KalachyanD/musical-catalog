package model;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "musician")
public class Musician {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull(message = "Не может быть пустым")
    @Size(min = 1, message = "Не может быть пустым")
    @Column(name = "name")
    private String name = "";

    @Column(name = "yearCreated")
    private int yearCreated = 2017;

    @NotNull(message = "Не может быть пустым")
    @Column(name = "countMembers")
    private int countMembers = 1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYearCreated() {
        return yearCreated;
    }

    public void setYearCreated(int yearCreated) {
        this.yearCreated = yearCreated;
    }

    public int getCountMembers() {
        return countMembers;
    }

    public void setCountMembers(int countMembers) {
        this.countMembers = countMembers;
    }

    @Override
    public String toString() {
        return name;
    }
}
