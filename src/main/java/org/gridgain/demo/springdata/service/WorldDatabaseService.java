/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.gridgain.demo.springdata.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.cache.Cache;

import com.vividsolutions.jts.geom.Point;
import org.apache.ignite.configuration.SqlConfiguration;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.gridgain.demo.springdata.dao.CityRepository;
import org.gridgain.demo.springdata.dao.CountryRepository;
import org.gridgain.demo.springdata.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.gridgain.demo.springdata.config.Constants.GEOSPATIAL_CACHE;

@Service
public class WorldDatabaseService {
    @Autowired
    CountryRepository countryDao;

    @Autowired
    CityRepository cityDao;

    @Autowired
    Ignite ignite;

    public List<CountryDTO> getCountriesByPopulation(int population) {
        List<CountryDTO> countries = new ArrayList<>();

        for (Cache.Entry<String, Country> entry : countryDao.findByPopulationGreaterThanEqualOrderByPopulationDesc(population))
            countries.add(new CountryDTO(entry.getKey(), entry.getValue()));

        return countries;
    }

    public List<CityDTO> getCitiesByPopulation(int population) {
        List<CityDTO> cities = new ArrayList<>();

        for (Cache.Entry<CityKey, City> entry : cityDao.findAllByPopulationGreaterThanEqualOrderByPopulation(population))
            cities.add(new CityDTO(entry.getKey(), entry.getValue()));

        return cities;
    }

    public List<List<?>> getMostPopulatedCities(Integer limit) {
        return cityDao.findMostPopulatedCities(limit == null ? 5 : limit);
    }

    public CityDTO updateCityPopulation(int cityId, int population) {
        Cache.Entry<CityKey, City> entry = cityDao.findById(cityId);

        entry.getValue().setPopulation(population);

        cityDao.save(entry.getKey(), entry.getValue());

        return new CityDTO(entry.getKey(), entry.getValue());
    }

    public void testGeospatialService() throws ParseException {
        CacheConfiguration<Integer, GeospatialPoint> cc = new CacheConfiguration<>(GEOSPATIAL_CACHE);
//        CacheConfiguration<Integer, Geometry> cc = new CacheConfiguration<>("GEOMETRY");
        cc.setIndexedTypes(Integer.class, GeospatialPoint.class);

        IgniteCache<Integer, GeospatialPoint> igniteCache = ignite.getOrCreateCache(cc).withKeepBinary();
        Random rnd = new Random();

        WKTReader r = new WKTReader();
        // Adding geometry points into the cache.
        for (int i = 0; i < 1000; i++) {
            int x = rnd.nextInt(10000);
            int y = rnd.nextInt(10000);

            Geometry geo = r.read("POINT(" + x + " " + y + ")");

            igniteCache.put(i, new GeospatialPoint(i, geo));
//            igniteCache.put(i, geo);
            if (i % 100 == 0) {
                System.out.println("lol");
                System.out.println(geo.toString());
            }
        }
        SqlQuery<Integer, GeospatialPoint> query = new SqlQuery<>(GeospatialPoint.class, "coords && ?");
        for (int i = 0; i < 10; i++) {
            Geometry cond = r.read("POLYGON((0 0, 0 " + rnd.nextInt(10000) + ", " +
                    rnd.nextInt(10000) + " " + rnd.nextInt(10000) + ", " +
                    rnd.nextInt(10000) + " 0, 0 0))");
            query.setArgs(cond);
            Collection<Cache.Entry<Integer, GeospatialPoint>> entries = igniteCache.query(query).getAll();
            System.out.println("Fetched points [cond=" + cond + ", cnt=" + entries.size() + ']');
        }

    }
}
