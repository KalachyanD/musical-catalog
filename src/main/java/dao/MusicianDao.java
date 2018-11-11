package dao;

import model.Musician;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class MusicianDao extends AbstractDao<Musician> {

    private final String HQL_GET_ALL = "select user from Musician user";



    public MusicianDao() {
        super(Musician.class);
    }

    public List<Musician> getAll(){
        return entityManager.createQuery(HQL_GET_ALL, Musician.class).getResultList();
    }


}
