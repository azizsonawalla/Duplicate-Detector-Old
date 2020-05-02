package config;

import java.util.Arrays;
import java.util.List;

public class Config {

    public static List<String> SUPPORTED_FILE_TYPES = Arrays.asList("JPEG", "JPG");

    public static Integer POOL_SIZE = 3;  // TODO: calculate based on available vCPUs. Maybe let user configure
}
