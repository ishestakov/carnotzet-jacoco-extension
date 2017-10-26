package com.github.ishestakov.carnotzet.extension.jacoco.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConfigurationProp<T> {
	private final T value;
	private final String key;

	public String toString() {
		if (value == null) {
			return "";
		}
		return String.format("%s=%s", key, value);
	}

}
