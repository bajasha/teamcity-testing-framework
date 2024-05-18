package com.example.teamcity.api;

import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.requests.checked.CheckedUser;
import com.example.teamcity.api.requests.unchecked.UncheckedProject;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static java.lang.String.format;

public class ProjectsTest extends BaseApiTest {

    @Test
    public void userUnableToCreateTwoProjectsWithSameIdTest() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        testData.getProject().setName(RandomData.getString());

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(format("Project ID \"%s\" is already used by another project", testData.getProject().getId())));
    }

    @Test
    public void userUnableToCreateTwoProjectsWithSameNameTest() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        testData.getProject().setId(RandomData.getString());

        new UncheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(format("Project with this name already exists: %s", testData.getProject().getName())));
    }

    @Test
    public void createProjectTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        var project = new CheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject());

        softy.assertThat(project.getId()).isEqualTo(testData.getProject().getId());
        softy.assertThat(project.getName()).isEqualTo(testData.getProject().getName());
        softy.assertThat(project.getLocator()).isEqualTo(testData.getProject().getParentProject().getParentProjectId());
    }

    @Test
    public void userUnableToCreateProjectWithoutNameTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        testData.getProject().setName(null);

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project name cannot be empty"));
    }

    @Test
    public void userUnableToCreateProjectWithoutIdTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        testData.getProject().setId(null);

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID must not be empty"));
    }

    @Test
    public void userUnableToCreateProjectWithNonexistentParentProjectTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        testData.getProject().setParentProject(TestDataGenerator.generateParentProject(RandomData.getString()));

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("No project found by name or internal/external id"));
    }

    @Test
    public void userUnableToCreateProjectWithoutParentProjectTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        testData.getProject().setParentProject(TestDataGenerator.generateParentProject(null));

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("No project specified. Either 'id', 'internalId' or 'locator' attribute should be present."));
    }

    @Test
    public void userCanUseCreatedProjectAsParentProjectTest() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        new UncheckedProject(Specifications.getSpec().superUserSpec()).create(firstTestData.getProject());

        secondTestData.getProject().setParentProject(TestDataGenerator.generateParentProject(firstTestData.getProject().getId()));

        var childProject = new CheckedProject(Specifications.getSpec().superUserSpec()).create(secondTestData.getProject());

        softy.assertThat(childProject.getParentProjectId()).isEqualTo(firstTestData.getProject().getId());
    }

    @Test
    public void userCanCreateProjectsWithSameNameUnderDifferentParentProjectsTest() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        new UncheckedProject(Specifications.getSpec().superUserSpec()).create(firstTestData.getProject());

        secondTestData.getProject().setParentProject(TestDataGenerator.generateParentProject(firstTestData.getProject().getId()));

        secondTestData.getProject().setName(firstTestData.getProject().getName());

        var secondProject = new CheckedProject(Specifications.getSpec().superUserSpec()).create(secondTestData.getProject());

        softy.assertThat(secondProject.getName()).isEqualTo(firstTestData.getProject().getName());
    }
}