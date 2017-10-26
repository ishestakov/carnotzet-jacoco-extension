package com.github.ishestakov.carnotzet.extension.jacoco;

import static com.github.ishestakov.carnotzet.extension.jacoco.JaCoCoExtension.SYSTEM_AGENT_PATH;
import static com.github.ishestakov.carnotzet.extension.jacoco.JaCoCoExtension.SYSTEM_JACOCO_JAVA_OPTIONS_FILE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.swissquote.carnotzet.core.Carnotzet;
import com.github.swissquote.carnotzet.core.CarnotzetModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class JaCoCoExtensionTest {

	public static final String APPLICABLE_MODULE = "test-module-apply";
	public static final String NON_APPLICABLE_MODULE = "wrong-test-module";
	public static final String ANOTHER_APPLICABLE_MODULE = "another-test-module-apply";
	private static final String YET_ANOTHER_APPLICABLE_MODULE = "yet-another-module-apply";

	@ClassRule
	public static TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws IOException {
		temporaryFolder.create();
	}

	@After
	public void tearDown() {
		temporaryFolder.delete();
	}

	@Test
	public void testEmptyModules() {
		Set<String> singleModule = Collections.singleton(APPLICABLE_MODULE);
		JaCoCoExtension extension = new JaCoCoExtension(singleModule, "");
		Carnotzet carnotzet = mock(Carnotzet.class);
		when(carnotzet.getResourcesFolder()).thenReturn(temporaryFolder.getRoot().toPath());
		when(carnotzet.getModules()).thenReturn(Lists.newArrayList());
		List<CarnotzetModule> result = extension.apply(carnotzet);

		assertThat(result, empty());
	}

	@Test
	public void testSingleModuleNotAffected() {
		Set<String> singleModule = Collections.singleton(APPLICABLE_MODULE);
		JaCoCoExtension extension = new JaCoCoExtension(singleModule, "");
		Carnotzet carnotzet = mock(Carnotzet.class);
		CarnotzetModule module = CarnotzetModule.builder().name(NON_APPLICABLE_MODULE).build();

		when(carnotzet.getResourcesFolder()).thenReturn(temporaryFolder.getRoot().toPath());
		when(carnotzet.getModules()).thenReturn(Lists.newArrayList(module));

		List<CarnotzetModule> result = extension.apply(carnotzet);

		assertThat(result, hasSize(1));
		CarnotzetModule actual = result.get(0);
		testNotAppliedModule(module, actual);
	}

	private void testNotAppliedModule(CarnotzetModule module, CarnotzetModule actual) {
		assertThat(actual, equalTo(module));
		assertThat(actual.getDockerEnvFiles(), equalTo(module.getDockerEnvFiles()));
		assertThat(actual.getDockerVolumes(), equalTo(module.getDockerVolumes()));
	}

	@Test
	public void testSingleModuleAffected() {
		Set<String> singleModule = Collections.singleton(APPLICABLE_MODULE);
		JaCoCoExtension extension = new JaCoCoExtension(singleModule, "");
		Carnotzet carnotzet = mock(Carnotzet.class);
		CarnotzetModule module = CarnotzetModule.builder().name(APPLICABLE_MODULE).build();

		when(carnotzet.getResourcesFolder()).thenReturn(temporaryFolder.getRoot().toPath());
		when(carnotzet.getModules()).thenReturn(Lists.newArrayList(module));

		List<CarnotzetModule> result = extension.apply(carnotzet);

		assertThat(result, hasSize(1));
		CarnotzetModule resultModule = result.get(0);
		testAppliedModule(module, resultModule);
	}

	@Test
	public void testSingleModuleAffectedDifferentReportingPath() throws IOException {
		Set<String> singleModule = Collections.singleton(APPLICABLE_MODULE);
		File anotherFilePath = temporaryFolder.newFolder("reports");
		JaCoCoExtension extension = new JaCoCoExtension(singleModule, anotherFilePath.toPath().toAbsolutePath().toString(),
				"");
		Carnotzet carnotzet = mock(Carnotzet.class);
		CarnotzetModule module = CarnotzetModule.builder().name(APPLICABLE_MODULE).build();

		when(carnotzet.getResourcesFolder()).thenReturn(temporaryFolder.getRoot().toPath());
		when(carnotzet.getModules()).thenReturn(Lists.newArrayList(module));

		List<CarnotzetModule> result = extension.apply(carnotzet);

		assertThat(result, hasSize(1));
		CarnotzetModule resultModule = result.get(0);
		testAppliedModule(module, resultModule, anotherFilePath.toPath().resolve(getModuleExecutionReportFileName(module)));
	}

	@Test
	public void testReportFileAlreadyExists() throws IOException {
		Set<String> singleModule = Collections.singleton(APPLICABLE_MODULE);
		JaCoCoExtension extension = new JaCoCoExtension(singleModule, "");
		Carnotzet carnotzet = mock(Carnotzet.class);
		CarnotzetModule module = CarnotzetModule.builder().name(APPLICABLE_MODULE).build();
		File reportFile = temporaryFolder.newFile(getModuleExecutionReportFileName(module));

		when(carnotzet.getResourcesFolder()).thenReturn(temporaryFolder.getRoot().toPath());
		when(carnotzet.getModules()).thenReturn(Lists.newArrayList(module));

		List<CarnotzetModule> result = extension.apply(carnotzet);

		assertThat(result, hasSize(1));
		CarnotzetModule resultModule = result.get(0);
		testAppliedModule(module, resultModule);
	}

	@Test
	public void severalModulesTest() {
		Set<String> severalModules = Sets.newHashSet(APPLICABLE_MODULE, ANOTHER_APPLICABLE_MODULE, YET_ANOTHER_APPLICABLE_MODULE);
		JaCoCoExtension extension = new JaCoCoExtension(severalModules, "");
		Carnotzet carnotzet = mock(Carnotzet.class);
		CarnotzetModule module1 = CarnotzetModule.builder().name(APPLICABLE_MODULE).build();
		CarnotzetModule module2 = CarnotzetModule.builder().name(ANOTHER_APPLICABLE_MODULE).build();
		CarnotzetModule module3 = CarnotzetModule.builder().name(YET_ANOTHER_APPLICABLE_MODULE).build();
		CarnotzetModule module4 = CarnotzetModule.builder().name(NON_APPLICABLE_MODULE).build();

		when(carnotzet.getResourcesFolder()).thenReturn(temporaryFolder.getRoot().toPath());
		when(carnotzet.getModules()).thenReturn(Lists.newArrayList(module1, module2, module3, module4));

		List<CarnotzetModule> result = extension.apply(carnotzet);

		assertThat(result, hasSize(4));
		testAppliedModule(module1, result.get(0));
		testAppliedModule(module2, result.get(1));
		testAppliedModule(module3, result.get(2));
		testNotAppliedModule(module4, result.get(3));

	}

	private void testAppliedModule(CarnotzetModule originalModule, CarnotzetModule testModule, Path resourcePath) {
		assertThat(testModule.getName(), equalTo(originalModule.getName()));
		assertThat(testModule.getDockerEnvFiles(),
				containsInAnyOrder(resolveResourceFilePath(SYSTEM_JACOCO_JAVA_OPTIONS_FILE).toString()));
		assertThat(testModule.getDockerVolumes(),
				containsInAnyOrder(
						resolveVolumeMapping(resolveResourceFilePath(SYSTEM_AGENT_PATH), JaCoCoExtension.CONTAINER_AGENT_PATH),
						resolveVolumeMapping(resourcePath, JaCoCoExtension.CONTAINER_EXECUTION_FILE_PATH)));
	}

	public void testAppliedModule(CarnotzetModule originalModule, CarnotzetModule testModule) {
		testAppliedModule(originalModule, testModule, resolveResourceFilePath(getModuleExecutionReportFileName(originalModule)));
	}

	private String getModuleExecutionReportFileName(CarnotzetModule module) {
		return module.getName() + ".exec";
	}

	private Path resolveResourceFilePath(String relativePath) {
		return temporaryFolder.getRoot().toPath().resolve(relativePath);
	}

	private String resolveVolumeMapping(Path localFile, String dockerFile) {
		return String.format("%s:%s", localFile.toAbsolutePath().toString(), dockerFile);
	}

}