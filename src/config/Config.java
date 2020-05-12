package config;

import java.util.ArrayList;
import java.util.List;

public class Config {

    /* Model */
    public static List<String> SUPPORTED_FILE_TYPES = new ArrayList<>();                                                // TODO: Add list of supported file types  // TODO: move this to individual strategies
    public static Integer POOL_SIZE = 3;                                                                                // TODO: calculate based on available vCPUs. Maybe let user configure

    /* UI */
    public static double SCENE_WIDTH = 1536;
    public static double SCENE_HEIGHT = 864;

    public static double STAGE_MIN_WIDTH = 400;
    public static double STAGE_MIN_HEIGHT = 400;
    public static String STAGE_TITLE = "Duplicate Detector";

    /* File Paths */
    public static String PARENT_FRAME_PATH = "layouts/ParentFrame.fxml";
    public static String DARK_THEME_CSS_PATH = "layouts/darkTheme.css";

}
