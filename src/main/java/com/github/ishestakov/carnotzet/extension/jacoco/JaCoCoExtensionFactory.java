package com.github.ishestakov.carnotzet.extension.jacoco;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser;
import com.github.swissquote.carnotzet.core.Carnotzet;
import com.github.swissquote.carnotzet.core.CarnotzetExtension;
import com.github.swissquote.carnotzet.maven.plugin.spi.CarnotzetExtensionsFactory;
import com.google.common.collect.Maps;

public class JaCoCoExtensionFactory implements CarnotzetExtensionsFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(JaCoCoExtensionFactory.class);
	static final CarnotzetExtension NO_OP_EXTENSION = Carnotzet::getModules;

	static final String EXECUTION_FILE = "executionFileDir";
	static final String MODULE_NAME_PROPERTY_PREFIX = "moduleName";
	public static final String JACOCO_AGENT_PART = "jacoco.agent.";

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

		Properties jacocoProps = getJaCoCoProperties(configuration);
		return new JaCoCoExtension(modules, executionFile, ConfigurationParser.parse(jacocoProps));
	}

	private Properties getJaCoCoProperties(Properties configuration) {
		Properties jacocoProps = new Properties();
		configuration.entrySet().stream()
				.filter(entry -> entry.getKey().toString().startsWith(JACOCO_AGENT_PART))
				.map(entry -> Maps.immutableEntry(entry.getKey().toString().replace(JACOCO_AGENT_PART, ""), entry.getValue().toString()))
				.forEach(entry -> jacocoProps.setProperty(entry.getKey(), entry.getValue()));
		return jacocoProps;
	}
}
