package zed.mopm.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class holds data relevant to the mod information.
 * @author Zedlander1001 on 2/26/2018
 */
public class References {
    private References() { }

    //-Mod Info
    //::Standard Mod information.
    public static final String MODID = "mopm";
    public static final String NAME = "More Organized Player Menus";
    public static final String VERSION = "1.3.0";
    public static final String INVALID_FINGERPRINT = "Invalid fingerprint detected!";

    //-Proxy
    //::Standard Proxy information.
    public static final String CLIENTSIDE = "zed.mopm.ClientProxy";

    //-Misc
    //::
    public static final Logger LOG = LogManager.getLogger(MODID);
}

