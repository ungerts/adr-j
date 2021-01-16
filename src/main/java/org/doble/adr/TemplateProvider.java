package org.doble.adr;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * This class streams the contents if templates independent of if they are normal files or
 * are in the packaged JAR file. If the template is not specified then a default template
 * in the resources is streamed.
 *
 * Example use:
 * <code>
 *      FileSystem fs = templatePath.getFileSystem();
 *      TemplateStreamer templateStreamer = new TemplateStreamer(fs, defaultTemplateFileName, env);
 *		Optional<String> templateName = Optional.of("/usr/adoble/dev/templates/my_template.md");
 *	    try (Stream<String> lines = templateStreamer.lines(Optional<String> templateName)) {
 *             lines.forEach(System.out::println);
 *       }
 * </code>
 *
 * See https://stackoverflow.com/questions/22605666/java-access-files-in-jar-causes-java-nio-file-filesystemnotfoundexception
 *
 * @author adoble
 *
 */

public class TemplateProvider  {
	String defaultTemplateName;
	FileSystem fileSystem;  // The file system for the normal files

	FileSystem jarFileSystem = null;

	Stream<String> lineStream;


	/**
	 * @param fileSystem  The file system being used to get the "normal" files
	 * @param defaultTemplateFileName  The name of the resource
	 */
	public TemplateProvider(FileSystem fileSystem, String defaultTemplateFileName) {
		super();
		this.defaultTemplateName = defaultTemplateFileName;
		this.fileSystem  = fileSystem;
	}

	/**
	 * Get a Path from the template file specified in the file <code>templateFileName</code>. A Path is
	 * returned independent of if the template file is a normal file or a resource and
	 * independent of if the resource file is packaged in a JAR or not.
	 * Rules are:
	 * - If the template specified is a normal file (i.e. a normal file path) then a Path to that is
	 * returned.
	 * - If the template is defined using "rsrc:" at the start then a Path to the resource is
	 * returned. For instance, if the template is defined as <code>rsrc:default_init_file.md</code>
	 * then a Path to the resource file (under src/main/resources) is returned, independent of
	 * if the resource file is in a JAR or not.
	 * - If a template is not specified (the optional field template is empty), then a Path to the default
	 * template (defined in ADRProperties) is given, independent of
	 * if the resource file is in a JAR or not.
	 */
   Path getPath(Optional<String> templateFileName) throws IOException, URISyntaxException{

	   Path templatePath;
		if (templateFileName.isPresent() && !templateFileName.get().startsWith("rsrc:")) {
			// Template has been specified by user and is a normal file
			templatePath =  fileSystem.getPath(templateFileName.get());

		} else if (templateFileName.isPresent() && templateFileName.get().startsWith("rsrc:")) {
			// Template is user specified resource.
			String templateResourceName = templateFileName.get().substring(5); // Remove the 'resource" indicator

			templatePath = getResourcePath(templateResourceName);


		} else {
			// Template has not been specified so get an input stream to the default template resource
			templatePath = getResourcePath(ADRProperties.defaultTemplateName);

		}

		return templatePath;

   }


private Path getResourcePath(String templateFileName) throws URISyntaxException, IOException {
	Path templatePath;
	URI uri = ClassLoader.getSystemResource(templateFileName).toURI();

	// Now check if the system resource is either a normal file (e.g. we are running in an IDE) or
	// the system resource is is a packaged JAR (e.g. we are running a packaged JAR file)
	if (uri.getScheme().equalsIgnoreCase("jar")) {
		// Running as a packaged JAR so set up a file system to read this jar using the first
		// part of the URI (separated with '!') as the file to use
		String[] uriParts = uri.toString().split("!");

		try {
		   jarFileSystem = FileSystems.getFileSystem(URI.create(uriParts[0]));
		}
		catch(FileSystemNotFoundException e) {
			jarFileSystem = FileSystems.newFileSystem(URI.create(uriParts[0]), new HashMap<>());
		}
		templatePath = jarFileSystem.getPath(uriParts[1]);
	} else {
		// Assume that the system resource is a normal file (e.g. we are running in an IDE).
		String pathName = uri.getSchemeSpecificPart();
		// If there is a leading '/' then remove it so we have a correct path specification.
		if (pathName.startsWith("/")) {
			pathName = pathName.substring(1);
		}
		// The resource file is in the default file system and not any file system
		// that is being used for e.g. test. Returned path should therefore be
		// associated with the default file system.
		FileSystem fs = FileSystems.getDefault();
		templatePath = fs.getPath(pathName);
	}
	return templatePath;
}


}
