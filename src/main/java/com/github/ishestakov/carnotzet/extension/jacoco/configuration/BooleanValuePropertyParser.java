package com.github.ishestakov.carnotzet.extension.jacoco.configuration;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

class BooleanValuePropertyParser extends ConfigurationPropParser<Boolean> {

	private BooleanValuePropertyParser(String propertyName, Boolean defaultValue) {
		super(propertyName, defaultValue);
	}

	@Override
	Function<String, Boolean> parser() {
		return Boolean::parseBoolean;
	}

	static BooleanValuePropertyParser forPropertyDefaultFalse(String propertyName) {
		return new BooleanValuePropertyParser(propertyName, false);
	}

	static BooleanValuePropertyParser forPropertyDefaultTrue(String propertyName) {
		return new BooleanValuePropertyParser(propertyName, true);
	}
}
