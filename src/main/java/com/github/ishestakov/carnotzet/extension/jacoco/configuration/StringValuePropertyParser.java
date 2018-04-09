package com.github.ishestakov.carnotzet.extension.jacoco.configuration;

import java.util.function.Function;

class StringValuePropertyParser extends ConfigurationPropParser<String> {

	private StringValuePropertyParser(String propertyName, String defaultValue) {
		super(propertyName, defaultValue);
	}

	@Override
	Function<String, String> parser() {
		return Function.identity();
	}

	static StringValuePropertyParser forPropertyWithDefault(PropertyKey propertyName, String defaultValue) {
		return new StringValuePropertyParser(propertyName.getKey(), defaultValue);
	}

	static StringValuePropertyParser forPropertyWithNullDefault(PropertyKey propertyName) {
		return forPropertyWithDefault(propertyName, null);
	}
}
