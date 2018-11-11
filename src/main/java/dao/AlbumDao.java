package dao;

import model.Label;
import model.Musician;
import model.Album;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class AlbumDao extends AbstractDao<Album>{
    private final String HQL_GET_ALL = "select album from Album album";

    private final String HQL_GET_ALL_BY_MUSICIAN = "select album from Album album where album.musician=:musician";

    private final String HQL_GET_ALL_BY_LABEL = "select album from Album album where album.label.name=:label";

    private final String HQL_GET_ALL_BY_YEAR = "select album from Album album where album.yearCreated=:year ";

    public AlbumDao() {
        super(Album.class);
    }

    public List<Album> getAll(){
        return entityManager.createQuery(HQL_GET_ALL, Album.class).getResultList();
    }

    public List<Album> getAllByMusician(Musician musician){
        return entityManager.createQuery(HQL_GET_ALL_BY_MUSICIAN, Album.class)
                .setParameter("musician", musician)
                .getResultList();
    }

    public List<Album> getAllByLabel(Label label){
        return entityManager.createQuery(HQL_GET_ALL_BY_LABEL, Album.class)
                .setParameter("label", label.getName())
                .getResultList();
    }
    public List<Album> getAllByYear(int year){
        return entityManager.createQuery(HQL_GET_ALL_BY_YEAR, Album.class)
                .setParameter("year", year)
                .getResultList();
    }



}
