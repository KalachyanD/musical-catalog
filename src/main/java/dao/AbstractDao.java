package dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractDao<T> {

    private final Class<T> typeParameterClass;

    @PersistenceContext(unitName = "main")
    EntityManager entityManager;

    AbstractDao(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public T create(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.refresh(entity);
        return entity;
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public void delete(Object id) {
        T entity = entityManager.find(typeParameterClass, id);
        entityManager.remove(entity);
        entityManager.flush();
    }

    public T read(Object id){
        return entityManager.find(typeParameterClass, id);
    }

}
