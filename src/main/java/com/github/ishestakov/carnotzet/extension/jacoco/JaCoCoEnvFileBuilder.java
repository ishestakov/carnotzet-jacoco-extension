package com.github.ishestakov.carnotzet.extension.jacoco;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

final class JaCoCoEnvFileBuilder {

	private static final String STANDARD_PART = "_JAVA_OPTIONS=-javaagent:/jacoco/jacocoagent.jar=destfile=/jacoco/report,";

	private JaCoCoEnvFileBuilder() {
		super();
	}

	static void build(Path path, String configuration) {
		try {
			FileUtils.writeStringToFile(path.toFile(), STANDARD_PART + configuration);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
