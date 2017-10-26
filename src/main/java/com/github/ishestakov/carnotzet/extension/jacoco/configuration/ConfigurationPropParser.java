package com.github.ishestakov.carnotzet.extension.jacoco.configuration;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ConfigurationPropParser<T> {

	private final String propertyName;
	private final T defaultValue;

	String propertyName() {
		return propertyName;
	}

	T defaultValue() {
		return defaultValue;
	}

	T value(Properties prop) {
		return Optional.ofNullable(prop.getProperty(propertyName())).map(parser()).orElse(defaultValue());
	}

	abstract Function<String, T> parser();

	ConfigurationProp<T> parse(Properties prop) {
		return new ConfigurationProp<>(value(prop), propertyName());
	}
}

