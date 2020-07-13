package zed.mopm.util;

import com.google.gson.*;

/**
 * <p>This class contains the json tags that can be found in any of the files used
 * by this program. <br>
 * <b>Those files include:</b>
 * <ul>
 *     <li>servers.dat</li>
 *     <li>world_vfs.json</li>
 *     <li>server_vfs.json</li>
 *     <li>vfs_path.json</li>
 * </ul>
 * {@link zed.mopm.systems.disk.SaveLoadManager} provides descriptions and examples
 * of each file.
 * <br><br>
 * <b>The following tags are used by this program in either json or NBT format:</b>
 * <br>
 * <i>See each of the tags for the description of their uses.</i>
 * <ul>
 * 		<li>{@link JsonLiterals#PATH_TAG}</li>
 * 		<li>{@link JsonLiterals#DIR_NAME}</li>
 * 		<li>{@link JsonLiterals#DIR_CHILDREN}</li>
 * </ul>
 * </p>
 * @see zed.mopm.systems.disk.SaveLoadManager for more information about the different
 * files.
 */
public class JsonLiterals {
	private JsonLiterals() {}

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * <p><b>value: </b> "vfs_path" <br>
	 * This tag is used in both <i>vfs_path.json</i> and <i>servers.dat</i>.
	 * It marks a world or server's path location within a directory tree.</p>
	 * @see zed.mopm.systems.disk.SaveLoadManager for example usage.
	 */
	public static final String PATH_TAG = "vfs_path";

	/**
	 * <p><b>value: </b> "directory_name" <br>
	 * This tag is used in both <i>world_vfs.json</i> and <i>server_vfs.json</i>.
	 * It marks the name for each directory found within a directory tree.</p>
	 * @see zed.mopm.systems.disk.SaveLoadManager for example usage.
	 */
	public static final String DIR_NAME = "directory_name";

	/**
	 * <p><b>value: </b> "children" <br>
	 * This tag is used to contains the children of each directory found within
	 * a directory tree.</p>
	 * @see zed.mopm.systems.disk.SaveLoadManager for example usage.
	 */
	public static final String DIR_CHILDREN = "children";

	/**
	 * //todo : comment this
	 */
	public static final String SERVERS_TAG = "servers";

	/**
	 * <p>vfs_path.json may be set to DEFAULT_VFS_PATH_JSON to reset world to the
	 * root directory.</p>
	 */
	public static final String DEFAULT_VFS_PATH_JSON =
		String.format("{\"%s\" : \"%s\"}", PATH_TAG, ModLiterals.ROOT_UNAME);

	/**
	 * <p>world_vfs.json and server_vfs.json may be set to DEFAULT_VFS_STRUCT to reset
	 * the directory tree back to just a root directory.</p>
	 */
	public static final String DEFAULT_VFS_STRUCT =
		String.format(
			"{\"%s\" : \"%s\", \"%s\" : []}",
			DIR_NAME,
			ModLiterals.ROOT_NAME,
			DIR_CHILDREN
		);

	public static final String formatStringJson(final String stringJson) {
		return gson.toJson(new JsonParser().parse(stringJson));
	}
}
