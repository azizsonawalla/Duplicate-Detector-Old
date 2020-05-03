package config;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static List<String> SUPPORTED_FILE_TYPES = new ArrayList<>();                                                // TODO: Add list of supported file types

    public static Integer POOL_SIZE = 3;                                                                                // TODO: calculate based on available vCPUs. Maybe let user configure
}
