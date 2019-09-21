package org.doble.adr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.io.BufferedReader;;

public class ADRProperties extends Properties {

	public static final String defaultPathToAdrFiles = "doc/adr";  //TODO is this the right place for this constant?
	public static final String secondaryPathToAdrFiles = "docs/adr";

	public static final String defaultTemplateName = "default_template.md";
	public static final String defaultInitialTemplateName = "default_initial_template.md";

	private static final long serialVersionUID = 1L;

	Environment env;

	/**
	 * @param env The environment in which the tool is run in
	 */
	public ADRProperties(Environment env) {
		Objects.requireNonNull(env);
		this.env = env;
	}

	/**
	 * Reads the .adr properties file at the root directory of the project. In case the file does not exist, sensible defaults are assumed.
	 */
	public void load() throws ADRException {
		// Get the root directory by looking for an .adr directory

		Path rootPath = env.pathOfCallOfAdrTool;

		Path propertiesRelPath = env.fileSystem.getPath(ADR.ADR_DIR_NAME, "adr.properties");

		Path propertiesPath = rootPath.resolve(propertiesRelPath);

		if (Files.exists(propertiesPath)) {
			try (BufferedReader propertiesReader = Files.newBufferedReader(propertiesPath)) {
				load(propertiesReader);
			} catch (Exception e) {
				throw new ADRException("FATAL: The properties file could not be read.", e);
			}
		}

		// set default path if it does not exist
		if (getProperty("docPath") == null) {
			if (Files.exists(rootPath.resolve("template.md"))) {
				// adr was called in the ADR directory
				setProperty("docPath", ".");
			} else if (Files.exists(rootPath.resolve(secondaryPathToAdrFiles))) {
				setProperty("docPath", secondaryPathToAdrFiles);
			} else {
				// fallback: defaultPathToAdrFiles
				setProperty("docPath", defaultPathToAdrFiles);
			}
		}
	}

}
