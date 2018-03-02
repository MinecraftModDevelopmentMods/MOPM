package zed.mopm.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Zedlander1001 on 2/26/2018
 * - This class holds data relevant to the mod information
 */
public class References {
    //-Mod Info
    //::Standard Mod information.
    public static final String MODID = "momp";
    public static final String NAME = "More Organized Player Menus";
    public static final String VERSION = "0.0.0.1";

    //-Proxy
    //::Standared Proxy information.
    public static final String CLIENTSIDE = "zed.mopm.ClientProxy";
    //public static final String SERVERSIDE = "zed.bytedrive.CommonProxy";

    //-Misc
    //::
    public static final Logger LOG = LogManager.getLogger(MODID);
}

