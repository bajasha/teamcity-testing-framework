package com.example.teamcity.api;

import com.example.teamcity.api.generators.RandomData;
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
    public void userUnableToCreateTwoProjectsWithSameId() {
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
    public void userUnableToCreateTwoProjectsWithSameName() {
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
    public void unableToCreateProjectWithoutNameTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        testData.getProject().setName("");

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project name cannot be empty"));
    }

    @Test
    public void unableToCreateProjectWithoutIdTest() {
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        testData.getProject().setId("");

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID must not be empty"));
    }
}