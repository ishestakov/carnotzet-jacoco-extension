package com.github.ishestakov.carnotzet.extension.jacoco;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.swissquote.carnotzet.core.Carnotzet;
import com.github.swissquote.carnotzet.core.CarnotzetExtension;
import com.github.swissquote.carnotzet.maven.plugin.spi.CarnotzetExtensionsFactory;

public class JaCoCoExtensionFactory implements CarnotzetExtensionsFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(JaCoCoExtensionFactory.class);
	protected static final CarnotzetExtension NO_OP_EXTENSION = Carnotzet::getModules;

	public static final String EXECUTION_FILE = "executionFileDir";
	public static final String MODULE_NAME_PROPERTY_PREFIX = "moduleName";

	@Override
	public CarnotzetExtension create(Properties configuration) {
		String executionFile = configuration.getProperty(EXECUTION_FILE);
		Set<String> modules = configuration.stringPropertyNames().stream()
				.filter(name -> name.contains(MODULE_NAME_PROPERTY_PREFIX))
				.map(configuration::getProperty)
				.collect(Collectors.toSet());

		if (modules.isEmpty()) {
			LOGGER.warn("No moduleName% properties were specified. Do not applying JaCoCo to any module.");
			return NO_OP_EXTENSION;
		}
		return new JaCoCoExtension(modules, executionFile);
	}
}
