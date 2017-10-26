package com.github.ishestakov.carnotzet.extension.jacoco.configuration;

import java.util.function.Function;

class IntegerValuePropertyParser extends ConfigurationPropParser<Integer> {

	private IntegerValuePropertyParser(String propertyName, Integer defaultValue) {
		super(propertyName, defaultValue);
	}

	@Override
	Function<String, Integer> parser() {
		return Integer::parseInt;
	}

	static IntegerValuePropertyParser forPropertyDefaultValue(PropertyKey propertyName, Integer defaultValue) {
		return new IntegerValuePropertyParser(propertyName.getKey(), defaultValue);
	}

}
