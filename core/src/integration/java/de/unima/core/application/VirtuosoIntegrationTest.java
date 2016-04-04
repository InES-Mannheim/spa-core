package de.unima.core.application;

import de.unima.core.BaseIntegrationTest;
import de.unima.core.domain.model.DataPool;
import de.unima.core.domain.model.Project;
import de.unima.core.domain.model.Schema;
import org.apache.jena.query.*;
import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.VirtuosoContainer;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;

public class VirtuosoIntegrationTest extends BaseIntegrationTest {

	@ClassRule
	public static VirtuosoContainer virtuosoContainer = createVirtuosoDockerContainer();

	private static VirtuosoContainer createVirtuosoDockerContainer(){
		try {
			return new VirtuosoContainer();
		} catch(Exception ex) {
			LoggerFactory.getLogger(SPAIntegrationTest.class).warn("Could not initialize Virtuoso container.", ex);
			return null;
		}
	}

	@Test
	public void employeeActivitiesInVirtuosoShouldBeReceivableOverTheWire() throws Exception {
		assumeNotNull(VirtuosoIntegrationTest.virtuosoContainer);
		assumeThat(VirtuosoIntegrationTest.virtuosoContainer.isRunning(), is(true));

		final SPA virtuosoSpa = SPABuilder.remote().virtuoso()
				.url(VirtuosoIntegrationTest.virtuosoContainer.getJdbcUrl())
				.username(VirtuosoIntegrationTest.virtuosoContainer.getUsername())
				.password(VirtuosoIntegrationTest.virtuosoContainer.getPassword())
				.build();
		loadFixturesIntoVirtuoso(virtuosoSpa);

		assertThat(actualActivitiesOfBob(), is(expectedActivitiesOfBob()));
	}

	private void loadFixturesIntoVirtuoso(SPA remoteSpa) {
		final Project project = remoteSpa.createProject("Mail Project");
		final Schema bpmnSchema = remoteSpa.importSchema(getFilePath("BPMN_2.0_ontology.owl").toFile(), "RDF", "BPMN2 ontology");
		project.linkSchema(bpmnSchema);

		final Schema xesSchema = remoteSpa.importSchema(getFilePath("xes.owl").toFile(), "RDF", "XES ontology");
		project.linkSchema(xesSchema);

		final DataPool dataPool = remoteSpa.createDataPool(project, "Mail Data Pool");
		remoteSpa.importData(getFilePath("MailProcess.bpmn").toFile(), "BPMN2", "Mail Process", dataPool);
		remoteSpa.importData(getFilePath("MailProcess.xes").toFile(), "XES", "Mail Process Instance", dataPool);

		remoteSpa.saveProject(project);
	}

	@NotNull
	private Set<String> expectedActivitiesOfBob() {
		final Set<String> expected = new HashSet<>();
		expected.add("Delete Email");
		expected.add("Read Email");
		expected.add("Answer Email");
		return expected;
	}

	private Set<String> actualActivitiesOfBob() {
		final Set<String> results = new HashSet<>();
		final Query query = VirtuosoIntegrationTest.createQueryForRetrievingActivitiesOfEmployee("Bob");
		try(QueryExecution queryExecution = VirtuosoQueryExecutionFactory.sparqlService(VirtuosoIntegrationTest.virtuosoContainer.getSparqlUrl(), query)) {
			ResultSet resultSet = queryExecution.execSelect();
			while(resultSet.hasNext()) {
				final QuerySolution solution = resultSet.nextSolution();
				results.add(solution.get("?activityDesc").toString());
			}
		}
		return results;
	}

	public static Query createQueryForRetrievingActivitiesOfEmployee(String workerName) {
		ParameterizedSparqlString queryBuilder = new ParameterizedSparqlString();
		queryBuilder.setNsPrefix("bpmn", "http://dkm.fbk.eu/index.php/BPMN2_Ontology#");
		queryBuilder.setNsPrefix("xes", "http://www.xes-standard.org/#");
		queryBuilder.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

		queryBuilder.append("SELECT DISTINCT ?activityDesc\n");
		queryBuilder.append("WHERE {\n");
		queryBuilder.append("	?eventResourceInstance\n");
		queryBuilder.append("		xes:key     \"org:resource\"^^xsd:Name ;\n");
		queryBuilder.append("		xes:value   ?workerName .\n");
		queryBuilder.append("	?eventInstance\n");
		queryBuilder.append("		xes:string  ?eventResourceInstance ;\n");
		queryBuilder.append("		xes:id      ?eventIdInstance .\n");
		queryBuilder.append("	?eventIdInstance\n");
		queryBuilder.append("		xes:value   ?bpmnId .\n");
		queryBuilder.append("	?activity  bpmn:id    ?bpmnId ;\n");
		queryBuilder.append("		bpmn:name   ?activityDesc\n");
		queryBuilder.append("}\n");

		queryBuilder.setLiteral("?workerName", workerName);

		return queryBuilder.asQuery();
	}
}
