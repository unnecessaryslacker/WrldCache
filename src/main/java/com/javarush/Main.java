package com.javarush;

import com.google.gson.Gson;
import com.javarush.config.RedisConfig;
import com.javarush.dao.CityDao;
import com.javarush.dao.CountryDao;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final SessionFactory sessionFactory;
    private final CityDao cityDao;
    private final CountryDao countryDao;
    private final Jedis jedis;
    private final Gson gson;

    public Main() {
        sessionFactory = prepareRelationalDb();
        cityDao       = new CityDao(sessionFactory);
        countryDao    = new CountryDao(sessionFactory);
        jedis         = RedisConfig.getJedis();
        gson          = new Gson();
    }

    private SessionFactory prepareRelationalDb() {
        Properties props = new Properties();
        props.put(Environment.DRIVER,   "com.p6spy.engine.spy.P6SpyDriver");
        props.put(Environment.URL,      "jdbc:p6spy:mysql://localhost:3306/world");
        props.put(Environment.USER,     "root");
        props.put(Environment.PASS,     "Juice999wrld!");
        props.put(Environment.DIALECT,  "org.hibernate.dialect.MySQL8Dialect");
        props.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        //props.put(Environment.HBM2DDL_AUTO, "validate");


        return new Configuration()
                .addProperties(props)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
    }

    private void shutdown() {
        if (sessionFactory != null) sessionFactory.close();
        if (jedis != null) jedis.close();
    }

    public List<City> fetchData() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            countryDao.getAll();

            int total = cityDao.getTotalCount();
            int step  = 500;
            List<City> all = new ArrayList<>(total);
            for (int i = 0; i < total; i += step) {
                all.addAll(cityDao.getItems(i, step));
            }

            session.getTransaction().commit();
            return all;
        }
    }

    public List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(c -> {
            CityCountry cc = new CityCountry();
            cc.setId(c.getId());
            cc.setName(c.getName());
            cc.setDistrict(c.getDistrict());
            cc.setPopulation(c.getPopulation());

            Country country = c.getCountry();
            cc.setCountryCode(country.getCode());
            cc.setAlternativeCountryCode(country.getAlternativeCode());
            cc.setCountryName(country.getName());
            cc.setContinent(country.getContinent());
            cc.setCountryRegion(country.getRegion());
            cc.setCountrySurfaceArea(country.getSurfaceArea());
            cc.setCountryPopulation(country.getPopulation());

            Set<Language> langs = country.getLanguages().stream().map(cl -> {
                Language l = new Language();
                l.setLanguage(cl.getLanguage());
                l.setOfficial(cl.getIsOfficial());
                l.setPercentage(cl.getPercentage());
                return l;
            }).collect(Collectors.toSet());
            cc.setLanguages(langs);

            return cc;
        }).collect(Collectors.toList());
    }

    public void pushToRedis(List<CityCountry> data) {
        data.forEach(cc ->
                jedis.set(
                        String.valueOf(cc.getId()),
                        gson.toJson(cc)
                )
        );
    }

    public void testRedisData(List<Integer> ids) {
        for (Integer id : ids) {
            String json = jedis.get(String.valueOf(id));
            gson.fromJson(json, CityCountry.class);
        }
    }

    public void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityDao.findById(id);
                city.getCountry().getLanguages().size();
            }
            session.getTransaction().commit();
        }
    }

    public static void main(String[] args) {
        Main app = new Main();

        List<City> allCities                 = app.fetchData();
        List<CityCountry> preparedForRedis   = app.transformData(allCities);
        app.pushToRedis(preparedForRedis);

        List<Integer> ids = Arrays.asList(3,2545,123,4,189,89,3458,1189,10,102);

        long t0 = System.currentTimeMillis();
        app.testRedisData(ids);
        long t1 = System.currentTimeMillis();

        app.testMysqlData(ids);
        long t2 = System.currentTimeMillis();

        System.out.printf("Redis:\t%d ms%n", (t1 - t0));
        System.out.printf("MySQL:\t%d ms%n", (t2 - t1));

        app.shutdown();
    }
}
