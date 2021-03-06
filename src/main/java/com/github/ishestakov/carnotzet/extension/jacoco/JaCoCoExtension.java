package com.github.ishestakov.carnotzet.extension.jacoco;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ishestakov.carnotzet.extension.jacoco.configuration.ConfigurationParser;
import com.github.swissquote.carnotzet.core.Carnotzet;
import com.github.swissquote.carnotzet.core.CarnotzetExtension;
import com.github.swissquote.carnotzet.core.CarnotzetModule;
import com.google.common.collect.Sets;

public class JaCoCoExtension implements CarnotzetExtension {
	private static final Logger LOGGER = LoggerFactory.getLogger(JaCoCoExtension.class);

	public static final String SYSTEM_AGENT_PATH = "jacoco/jacocoagent.jar";
	public static final String CONTAINER_AGENT_PATH = "/jacoco/jacocoagent.jar";
	public static final String SYSTEM_JACOCO_JAVA_OPTIONS_FILE = "jacoco/java.env";
	public static final String CONTAINER_EXECUTION_FILE_PATH = "/jacoco/report";
	public static final String CONTAINER_JACOCO_AGENT_PATH = "/jacoco/jacocoagent.jar";

	private final Set<String> requiredModules;
	private final Path executionReportsPath;
	private final String configuration;

	public Set<String> getRequiredModules() {
		return Sets.newHashSet(requiredModules);
	}

	public Path getExecutionReportsPath() {
		return executionReportsPath;
	}

	public JaCoCoExtension(Set<String> moduleNames, String executionReportsPath, String configuration) {
		this.executionReportsPath = Optional.ofNullable(executionReportsPath).map(Paths::get).orElse(null);
		this.requiredModules = new HashSet<>(moduleNames);
		this.configuration = configuration;
	}

	public JaCoCoExtension(Set<String> moduleNames, String executionReportsPath) {
		this(moduleNames, executionReportsPath, ConfigurationParser.parse(new Properties()));
	}

	public JaCoCoExtension(Set<String> moduleNames) {
		this(moduleNames, null);
	}

	@Override
	public List<CarnotzetModule> apply(Carnotzet carnotzet) {
		List<CarnotzetModule> modules = carnotzet.getModules();
		prepareResources(carnotzet);
		return modules.stream().map(applyJaCoCoAgent(carnotzet)).collect(Collectors.toList());
	}

	private Function<CarnotzetModule, CarnotzetModule> applyJaCoCoAgent(Carnotzet carnotzet) {
		return module -> {
			if (requiredModules.contains(module.getName())) {
				Path resourcesFolder = carnotzet.getResourcesFolder();
				Set<String> envFiles = populateModuleEnvFiles(module, resourcesFolder);
				Set<String> volumes = populateModuleVolumes(carnotzet, module, resourcesFolder);
				return module.toBuilder()
						.dockerEnvFiles(envFiles)
						.dockerVolumes(volumes)
						.build();
			}
			return module;

		};
	}

	private Set<String> populateModuleVolumes(Carnotzet carnotzet, CarnotzetModule module, Path resourcesFolder) {
		Set<String> volumes = new HashSet<>(Optional.ofNullable(module.getDockerVolumes()).orElse(Collections.emptySet()));

		Path systemExecutionFilePath = getExecutionFilePath(carnotzet, module);
		volumes.add(systemExecutionFilePath.toString() + ":" + CONTAINER_EXECUTION_FILE_PATH);
		String from = resourcesFolder.resolve(SYSTEM_AGENT_PATH).toAbsolutePath().toString();
		volumes.add(from + ":" + CONTAINER_AGENT_PATH);
		return volumes;
	}

	private Set<String> populateModuleEnvFiles(CarnotzetModule module, Path resourcesFolder) {
		Set<String> envFiles = new HashSet<>(Optional.ofNullable(module.getDockerEnvFiles()).orElse(Collections.emptySet()));
		envFiles.add(resourcesFolder.resolve(SYSTEM_JACOCO_JAVA_OPTIONS_FILE).toAbsolutePath().toString());
		return envFiles;
	}

	private Path getExecutionFilePath(Carnotzet carnotzet, CarnotzetModule module) {
		Path reportFile = Optional.ofNullable(this.executionReportsPath)
				.orElse(carnotzet.getResourcesFolder())
				.resolve(module.getName() + ".exec");
		try {
			Files.createFile(reportFile);
		}
		catch (FileAlreadyExistsException e) {
			LOGGER.warn(String.format("File %s already exists. The execution statistics will be appended", reportFile));
		}
		catch (IOException e) {
			LOGGER.error(String.format("Cannot stat the file %s", reportFile));
			throw new UncheckedIOException(e);
		}
		return reportFile;
	}

	private void prepareResources(Carnotzet carnotzet) {
		JaCoCoEnvFileBuilder.build(carnotzet.getResourcesFolder().resolve("jacoco/java.env"), this.configuration);
		extractJacocoAgent(carnotzet.getResourcesFolder().resolve("jacoco/jacocoagent.jar"));
	}

	private static void extractJacocoAgent(Path destination) {
		try {
			URL inputUrl = JaCoCoExtension.class.getResource(CONTAINER_JACOCO_AGENT_PATH);
			FileUtils.copyURLToFile(inputUrl, destination.toFile());
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
