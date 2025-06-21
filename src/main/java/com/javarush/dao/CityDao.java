package com.javarush.dao;

import com.javarush.domain.City;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CityDao {
    private final SessionFactory sessionFactory;

    public CityDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public int getTotalCount() {
        Long cnt = sessionFactory.getCurrentSession()
                .createQuery("select count(c) from City c", Long.class)
                .uniqueResult();
        return cnt == null ? 0 : cnt.intValue();
    }

    public List<City> getItems(int offset, int limit) {
        Query<City> q = sessionFactory
                .getCurrentSession()
                .createQuery("select c from City c", City.class);
        q.setFirstResult(offset);
        q.setMaxResults(limit);
        return q.list();
    }

    public City findById(int id) {
        return sessionFactory.getCurrentSession().get(City.class, id);
    }
}
