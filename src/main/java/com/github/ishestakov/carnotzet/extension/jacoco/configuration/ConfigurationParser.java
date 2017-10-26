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
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.OUTPUT;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.PORT;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JacocoPropertyKey.SESSIONID;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JavaKeys.JMX_AUTH;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JavaKeys.JMX_PORT;
import static com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser.JavaKeys.JMX_SSL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ConfigurationParser {

	public enum JacocoPropertyKey implements PropertyKey {

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

		JMX("jmx");

		private final String key;

		JacocoPropertyKey(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	enum JavaKeys implements PropertyKey {
		JMX_AUTH("-Dcom.sun.management.jmxremote.authenticate"),

		JMX_SSL("-Dcom.sun.management.jmxremote.ssl"),

		JMX_PORT("-Dcom.sun.management.jmxremote.port");

		private final String key;

		JavaKeys(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	private static final Collection<ConfigurationPropParser<?>> jacocoProps = new ArrayList<>(JacocoPropertyKey.values().length);
	private static final Collection<ConfigurationPropParser<?>> javaProps = new ArrayList<>(JavaKeys.values().length);

	static {
		registerJacocoParser(BooleanValuePropertyParser.forPropertyDefaultTrue(APPEND));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithDefault(OUTPUT, "file"));
		registerJacocoParser(BooleanValuePropertyParser.forPropertyDefaultTrue(DUMPONEXIT));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(INCLUDES));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(EXCLUDES));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(EXCLCLASSLOADER));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(INCLBOOTSTRAPCLASSES));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(INCLNOLOCATIONCLASSES));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(SESSIONID));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(ADDRESS));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(PORT));
		registerJacocoParser(StringValuePropertyParser.forPropertyWithNullDefault(CLASSDUMPDIR));
		registerJacocoParser(BooleanValuePropertyParser.forPropertyDefaultTrue(JMX));

		registerJavaParser(BooleanValuePropertyParser.forPropertyDefaultFalse(JMX_AUTH));
		registerJavaParser(BooleanValuePropertyParser.forPropertyDefaultFalse(JMX_SSL));
		registerJavaParser(IntegerValuePropertyParser.forPropertyDefaultValue(JMX_PORT, 8888));
	}

	private ConfigurationParser() {
		super();
	}

	private static void registerJacocoParser(ConfigurationPropParser<?> prop) {
		jacocoProps.add(prop);
	}

	private static void registerJavaParser(ConfigurationPropParser<?> prop) {
		javaProps.add(prop);
	}

	public static String parse(Properties properties) {
		return parseJacocoProperties(properties) + " " + parseJavaProperties(properties);
	}

	private static String parseJacocoProperties(Properties properties) {
		return applyParsers(jacocoProps, properties, Collectors.joining(","));
	}

	private static String parseJavaProperties(Properties properties) {
		return applyParsers(javaProps, properties, Collectors.joining(" "));
	}

	private static String applyParsers(Collection<ConfigurationPropParser<?>> parsers, Properties properties,
			Collector<CharSequence, ?, String> joining) {
		return parsers.stream()
				.map(parser -> parser.parse(properties))
				.filter(Objects::nonNull)
				.map(Object::toString)
				.filter(item -> !item.isEmpty())
				.collect(joining);
	}
}
