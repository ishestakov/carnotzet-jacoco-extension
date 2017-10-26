package com.github.ishestakov.carnotzet.extension.jacoco.configuration;

import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.ADDRESS;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.APPEND;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.CLASSDUMPDIR;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.DUMPONEXIT;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.EXCLCLASSLOADER;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.EXCLUDES;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.INCLBOOTSTRAPCLASSES;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.INCLNOLOCATIONCLASSES;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.INCLUDES;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.JMX;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.JMX_AUTH;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.JMX_PORT;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.JMX_SSL;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.PORT;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.SESSIONID;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.OUTPUT;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class ConfigurationParserTest {

	private static final Set<ConfigurationProp<?>> defaultProps = ImmutableSet.<ConfigurationProp<?>> builder()
			.add(new ConfigurationProp<>(8888, JMX_PORT.getKey()))
			.add(new ConfigurationProp<>("file", OUTPUT.getKey()))
			.add(new ConfigurationProp<>(true, APPEND.getKey()))
			.add(new ConfigurationProp<>(true, DUMPONEXIT.getKey()))
			.add(new ConfigurationProp<>(true, JMX.getKey()))
			.add(new ConfigurationProp<>(false, JMX_AUTH.getKey()))
			.build();

	@Test
	public void testEmptyProps() {
		Properties props = new Properties();
		String result = ConfigurationParser.parse(props);

		assertThat(result, is(notNullValue()));
		verifyDefaultValues(result);
	}

	@Test
	public void testOverrideDefaultValues() {
		Properties props = new Properties();

		Set<ConfigurationProp<?>> configProps = ImmutableSet.<ConfigurationProp<?>> builder()
				.add(new ConfigurationProp<>(9999, JMX_PORT.getKey()))
				.add(new ConfigurationProp<>(false, APPEND.getKey()))
				.add(new ConfigurationProp<>(false, DUMPONEXIT.getKey()))
				.add(new ConfigurationProp<>(true, JMX_AUTH.getKey()))
				.build();

		fillProperteis(props, configProps);

		String result = ConfigurationParser.parse(props);
		verifyPropValues(result, configProps);
	}

	@Test
	public void testAllProps() {
		Properties props = new Properties();

		Set<ConfigurationProp<?>> configProps = ImmutableSet.<ConfigurationProp<?>> builder()
				.add(new ConfigurationProp<>(false, APPEND.getKey()))
				.add(new ConfigurationProp<>("tcp", OUTPUT.getKey()))
				.add(new ConfigurationProp<>(false, DUMPONEXIT.getKey()))
				.add(new ConfigurationProp<>(false, JMX.getKey()))
				.add(new ConfigurationProp<>(true, JMX_AUTH.getKey()))
				.add(new ConfigurationProp<>(true, JMX_SSL.getKey()))
				.add(new ConfigurationProp<>(2233, JMX_PORT.getKey()))
				.add(new ConfigurationProp<>(UUID.randomUUID().toString(), CLASSDUMPDIR.getKey()))
				.add(new ConfigurationProp<>(6666, PORT.getKey()))
				.add(new ConfigurationProp<>(UUID.randomUUID().toString(), ADDRESS.getKey()))
				.add(new ConfigurationProp<>(UUID.randomUUID().toString(), SESSIONID.getKey()))
				.add(new ConfigurationProp<>(UUID.randomUUID().toString(), INCLNOLOCATIONCLASSES.getKey()))
				.add(new ConfigurationProp<>(UUID.randomUUID().toString(), INCLBOOTSTRAPCLASSES.getKey()))
				.add(new ConfigurationProp<>(UUID.randomUUID().toString(), EXCLCLASSLOADER.getKey()))
				.add(new ConfigurationProp<>(UUID.randomUUID().toString(), EXCLUDES.getKey()))
				.add(new ConfigurationProp<>(UUID.randomUUID().toString(), INCLUDES.getKey()))
				.build();

		fillProperteis(props, configProps);

		String result = ConfigurationParser.parse(props);
		verifyPropValues(result, configProps);
	}

	private void fillProperteis(Properties props, Set<ConfigurationProp<?>> configProps) {
		configProps.forEach(prop -> props.put(prop.getKey(), prop.getValue().toString()));
	}

	private void verifyDefaultValues(String string) {
		verifyPropValues(string, defaultProps);
	}

	private void verifyPropValues(String result, Collection<ConfigurationProp<?>> props) {
		props.forEach(prop -> assertThat(result, containsString(prop.toString())));
	}

}