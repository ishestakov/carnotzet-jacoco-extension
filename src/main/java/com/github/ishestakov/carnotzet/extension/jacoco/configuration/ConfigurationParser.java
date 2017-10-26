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
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.OUTPUT;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.PORT;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.SESSIONID;

import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class ConfigurationParser {
	private static final Collection<ConfigurationPropParser<?>> props = new CopyOnWriteArraySet<>();

	public enum JacocoPropertyKey {

		APPEND("append"),

		DUMPONEXIT("dumponexit"),

		OUTPUT("output"),

		INCLUDES("includes"),

		EXCLUDES("excludes"),

		EXCLCLASSLOADER("exclclassloader"),

		INCLBOOTSTRAPCLASSES("inclbootstrapclasses"),

		INCLNOLOCATIONCLASSES("inclnolocationclasses"),

		SESSIONID("sessionid"),

		ADDRESS("address"),

		PORT("port"),

		CLASSDUMPDIR("classdumpdir"),

		JMX("jmx"),

		JMX_AUTH("jmxAuth"),

		JMX_SSL("jmxSsl"),

		JMX_PORT("jmxPort");

		private final String key;

		JacocoPropertyKey(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	static {
		registerProp(BooleanValuePropertyParser.forPropertyDefaultTrue(APPEND.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithDefault(OUTPUT.getKey(), "file"));
		registerProp(BooleanValuePropertyParser.forPropertyDefaultTrue(DUMPONEXIT.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(INCLUDES.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(EXCLUDES.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(EXCLCLASSLOADER.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(INCLBOOTSTRAPCLASSES.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(INCLNOLOCATIONCLASSES.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(SESSIONID.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(ADDRESS.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(PORT.getKey()));
		registerProp(StringValuePropertyParser.forPropertyWithNullDefault(CLASSDUMPDIR.getKey()));
		registerProp(BooleanValuePropertyParser.forPropertyDefaultTrue(JMX.getKey()));
		registerProp(BooleanValuePropertyParser.forPropertyDefaultFalse(JMX_AUTH.getKey()));
		registerProp(BooleanValuePropertyParser.forPropertyDefaultFalse(JMX_SSL.getKey()));
		registerProp(IntegerValuePropertyParser.forPropertyDefaultValue(JMX_PORT.getKey(), 8888));
	}

	private ConfigurationParser() {
		super();
	}

	private static void registerProp(ConfigurationPropParser<?> prop) {
		getParsers().add(prop);
	}

	private static Collection<ConfigurationPropParser<?>> getParsers() {
		return props;
	}

	private static Collection<ConfigurationProp<?>> parseProperties(Properties properties) {
		return getParsers().stream().map(parser -> parser.parse(properties)).collect(Collectors.toSet());
	}

	public static String parse(Properties properties) {
		return parseProperties(properties).stream()
				.filter(Objects::nonNull)
				.map(Object::toString)
				.filter(item -> !item.isEmpty())
				.collect(Collectors.joining(","));
	}
}
