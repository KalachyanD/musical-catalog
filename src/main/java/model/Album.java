package model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "album")
public class Album {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull(message = "Не может быть пустым")
    @Size(min = 1, message = "Не может быть пустым")
    @Column(name = "name")
    private String name = "";

    @Column(name = "countSong")
    private int countSong = 1;

    @ManyToOne
    @NotNull(message = "Не может быть пустым")
    @JoinColumn(name = "musicianId")
    private Musician musician;

    @ManyToOne
    @NotNull(message = "Не может быть пустым")
    @JoinColumn(name = "labelId")
    private Label label;

    @Column(name = "yearCreated")
    private int yearCreated = 2017;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Musician getMusician() {
        return musician;
    }

    public void setMusician(Musician musician) {
        this.musician = musician;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCountSong() {
        return countSong;
    }

    public void setCountSong(int countSong) {
        this.countSong = countSong;
    }

    public int getYearCreated() {
        return yearCreated;
    }

    public void setYearCreated(int yearCreated) {
        this.yearCreated = yearCreated;
    }
}