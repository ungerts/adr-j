/**
 *
 */
package org.doble.commands;


import java.util.concurrent.Callable;

import org.doble.adr.Environment;

import picocli.CommandLine.*;


/**
 * Subcommand print out the version number.
 *
 * @author adoble
 */

@Command(name = "version",
         description = "Prints the version of adr-j.")
public class CommandVersion implements Callable<Integer> {


	@ParentCommand
	CommandADR commandADR;

	@Override
	public Integer call() {
		int exitCode = 0;

		Environment env = commandADR.getEnvironment();
		/*******************************************************************************************
		 *                                  VERSION NUMBER                                         *
		 *                                                                                         *
		 * Version numbers adhere to to Semantic Versioning:  https://semver.org/spec/v2.0.0.html  *
		 *                                                                                         *
		 *******************************************************************************************/
		// Minor release, backwards compatible
		String version = "3.1.0";
		String msg = "Version " + version;
		env.err.println(msg);

		return exitCode;
	}

}
