package com.ckt.cyl.sensortest.db;

/**
 * Created by D22434 on 2017/7/24.
 */

public class SensorDbSchema {

    public static final class MSensorTable {
        public static final String NAME = "msensor";

        public static final class Cols {

            public static final String ANGLE = "angle";
            public static final String DEVIATION = "deviation";
        }

    }

    public static final class HSensorTable {
        public static final String NAME = "hsensor";

        public static final class Cols {

            public static final String STATUS = "status";
            public static final String INTERVAL = "interval";
        }

    }
}
