package com.github.ishestakov.carnotzet.extension.jacoco;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.swissquote.carnotzet.core.CarnotzetExtension;

public class JaCoCoExtensionFactoryTest {

	private final JaCoCoExtensionFactory factory = new JaCoCoExtensionFactory();

	@ClassRule
	public static TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testNoConfiguration() {

		CarnotzetExtension carnotzetExtension = factory.create(new Properties());
		assertThat(carnotzetExtension, is(JaCoCoExtensionFactory.NO_OP_EXTENSION));
	}

	@Test
	public void testNoModuleConfiguration() throws IOException {
		File root = createResourceFolder();
		Properties props = new Properties();
		props.put(JaCoCoExtensionFactory.EXECUTION_FILE, root.toPath().toAbsolutePath().toString());
		CarnotzetExtension carnotzetExtension = factory.create(props);
		assertThat(carnotzetExtension, is(JaCoCoExtensionFactory.NO_OP_EXTENSION));
	}

	private File createResourceFolder() throws IOException {
		temporaryFolder.create();
		return temporaryFolder.getRoot();
	}

	@Test
	public void testSingleModuleConfiguration() throws IOException {
		Properties props = new Properties();
		String moduleName = "module1";
		props.put(JaCoCoExtensionFactory.MODULE_NAME_PROPERTY_PREFIX + "1", moduleName);
		JaCoCoExtension carnotzetExtension = (JaCoCoExtension) factory.create(props);

		assertThat(carnotzetExtension.getRequiredModules(), containsInAnyOrder(moduleName));
		assertThat(carnotzetExtension.getExecutionReportsPath(), nullValue());
	}

	@Test
	public void testSeveralModuleConfiguration() {
		Properties props = new Properties();
		String moduleName1 = "module-1";
		String moduleName2 = "module-2";

		props.put(JaCoCoExtensionFactory.MODULE_NAME_PROPERTY_PREFIX + "1", moduleName1);
		props.put(JaCoCoExtensionFactory.MODULE_NAME_PROPERTY_PREFIX + "2", moduleName2);

		JaCoCoExtension carnotzetExtension = (JaCoCoExtension) factory.create(props);

		assertThat(carnotzetExtension.getRequiredModules(), containsInAnyOrder(moduleName1, moduleName2));
		assertThat(carnotzetExtension.getExecutionReportsPath(), nullValue());

	}

	@Test
	public void testSeveralModuleWithReportPathConfiguration() {
		Properties props = new Properties();
		String moduleName1 = "module-1";
		String moduleName2 = "module-2";

		props.put(JaCoCoExtensionFactory.MODULE_NAME_PROPERTY_PREFIX + "1", moduleName1);
		props.put(JaCoCoExtensionFactory.MODULE_NAME_PROPERTY_PREFIX + "2", moduleName2);
		props.put(JaCoCoExtensionFactory.EXECUTION_FILE, temporaryFolder.getRoot().toPath().toAbsolutePath().toString());

		JaCoCoExtension carnotzetExtension = (JaCoCoExtension) factory.create(props);

		assertThat(carnotzetExtension.getRequiredModules(), containsInAnyOrder(moduleName1, moduleName2));
		assertThat(carnotzetExtension.getExecutionReportsPath(), equalTo(temporaryFolder.getRoot().toPath().toAbsolutePath()));


	}

}