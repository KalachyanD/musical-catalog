package dao;

import model.Label;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class LabelDao extends AbstractDao<Label>{
    private final String HQL_GET_ALL = "select project from Label project";

    public LabelDao() {
        super(Label.class);
    }

    public List<Label> getAll(){
        return entityManager.createQuery(HQL_GET_ALL, Label.class).getResultList();
    }
}
