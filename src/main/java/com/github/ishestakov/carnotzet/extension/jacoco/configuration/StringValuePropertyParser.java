package com.github.ishestakov.carnotzet.extension.jacoco.configuration;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

class StringValuePropertyParser extends ConfigurationPropParser<String> {

	private StringValuePropertyParser(String propertyName, String defaultValue) {
		super(propertyName, defaultValue);
	}

	@Override
	Function<String, String> parser() {
		return Function.identity();
	}

	static StringValuePropertyParser forPropertyWithDefault(String propertyName, String defaultValue) {
		return new StringValuePropertyParser(propertyName, defaultValue);
	}

	static StringValuePropertyParser forPropertyWithNullDefault(String propertyName) {
		return new StringValuePropertyParser(propertyName, null);
	}
}
