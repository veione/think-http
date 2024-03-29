package com.think.http.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * config reader parse object.
 *
 * @author veione
 */
public final class Config {

    private static Properties properties = new Properties();
    private static final String configName = "/config.properties";
    private static Config instance;

    private Config() {
        try {
            properties.load(new InputStreamReader(getClass().getResourceAsStream(configName), "UTF-8"));
        } catch (IOException e) {
            throw new IllegalStateException("Read config file error", e);
        }
    }

    private synchronized static Config getInstance() {
        if (null == instance) {
            instance = new Config();
        }
        return instance;
    }

    public static int getInt(String str) {
        try {
            if (null == instance) {
                getInstance();
            }
            return Integer.parseInt(properties.getProperty(str));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long getLong(String str) {
        try {
            if (null == instance) {
                getInstance();
            }
            return Long.parseLong(properties.getProperty(str));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double getDouble(String str) {
        try {
            if (null == instance) {
                getInstance();
            }
            return Double.parseDouble(properties.getProperty(str));

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getString(String str) {
        try {
            if (null == instance) {
                getInstance();
            }
            return properties.getProperty(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean getBoolean(String str) {
        try {
            if (null == instance) {
                getInstance();
            }
            return Boolean.parseBoolean(properties.getProperty(str));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
