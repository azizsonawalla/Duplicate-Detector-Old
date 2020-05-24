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

    public static final class SearchStrategyDescription {

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
    public static final Logger.Level LOG_LEVEL = Logger.Level.DEBUG;

    /* Model */
    public static final List<String> SUPPORTED_FILE_TYPES = Arrays.asList("JPEG", "JPG", "PNG");
    public static final Integer POOL_SIZE = 3;

    /* UI */
    public static final double SCENE_WIDTH = 1536;
    public static final double SCENE_HEIGHT = 864;

    public static final double STAGE_MIN_WIDTH = 400;
    public static final double STAGE_MIN_HEIGHT = 400;
    public static final String STAGE_TITLE = "Duplicate Detector";

    public static final int PRE_SCAN_WAIT_POLL_INTERVAL_MS = 100;
    public static final int PRE_SCAN_WAIT_TIMEOUT_MS = 2000;
    public static final int PRE_SCAN_POLL_INTERVAL_MS = 100;
    public static final long PRE_SCAN_TIMEOUT_MS = Long.MAX_VALUE;

    /* File Paths */
    public static final String PARENT_FRAME = "layouts/ParentFrame.fxml";                                               // TODO: create file objects from rel paths and then get abs path
    public static final String DARK_THEME_CSS = "style/darkTheme.css";
    public static final String LAYOUTS_CONFIGURE_SCAN_FXML = "../layouts/ConfigureScan.fxml";
    public static final String LAYOUTS_PREPARE_TO_SCAN_FXML = "../layouts/PrepareToScan.fxml";
    public static final String LAYOUTS_NEW_SCAN_FXML = "../layouts/NewScan.fxml";
    public static final String LAYOUTS_RUN_SCAN_FXML = "../layouts/RunScan.fxml";
    
    /* Search Strategies */
    public static final SearchStrategyDescription quick = new SearchStrategyDescription(MetadataStrategy.class,
            "Quick Scan", "Finds photos with the same name and size.");
}
