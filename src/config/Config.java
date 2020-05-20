package config;

import model.searchModel.searchStrategies.ISearchStrategy;
import model.searchModel.searchStrategies.MetadataStrategy;
import util.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Program configuration constants
 */
public class Config {
    
    public static class SearchStrategyDescription {

        private final Class<? extends ISearchStrategy> strategy;
        private final String uiName;
        private final String desc;

        SearchStrategyDescription(Class<? extends ISearchStrategy> strategy, String uiName, String desc) {
            this.strategy = strategy;
            this.uiName = uiName;
            this.desc = desc; 
        }

        public Class<? extends ISearchStrategy> getStrategy() {
            return strategy;
        }

        public String getUiName() {
            return uiName;
        }

        public String getDesc() {
            return desc;
        }

    }

    /* General */
    public static Logger.Level LOG_LEVEL = Logger.Level.DEBUG;

    /* Model */
    public static List<String> SUPPORTED_FILE_TYPES = Arrays.asList("JPEG", "JPG", "PNG");                              // TODO: Add list of supported file types  // TODO: move this to individual strategies
    public static Integer POOL_SIZE = 3;                                                                                // TODO: calculate based on available vCPUs. Maybe let user configure

    /* UI */
    public static double SCENE_WIDTH = 1536;
    public static double SCENE_HEIGHT = 864;

    public static double STAGE_MIN_WIDTH = 400;
    public static double STAGE_MIN_HEIGHT = 400;
    public static String STAGE_TITLE = "Duplicate Detector";

    /* File Paths */
    public static String PARENT_FRAME = "layouts/ParentFrame.fxml";
    public static String DARK_THEME_CSS = "style/darkTheme.css";
    
    /* Search Strategies */
    public static SearchStrategyDescription quick = new SearchStrategyDescription(MetadataStrategy.class,
            "Quick Scan", "Finds photos with the same name and size.");
}
