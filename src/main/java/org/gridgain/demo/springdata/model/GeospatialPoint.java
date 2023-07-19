package org.gridgain.demo.springdata.model;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

public class GeospatialPoint implements Serializable {

    private int ID;
    /**
     * Coordinates.
     */
//    @QuerySqlField(index = true)
    private Geometry coords;

    /**
     * @param coords Coordinates.
     */
    public GeospatialPoint(int ID, Geometry coords) {
        this.ID = ID;
        this.coords = coords;
    }
//    public GeospatialPoint(Geometry coords) {
//        this.coords = coords;
//    }
}
