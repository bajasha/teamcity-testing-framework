package com.example.teamcity.api;

import com.example.teamcity.api.requests.checked.CheckedBuildConfig;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.requests.checked.CheckedUser;
import com.example.teamcity.api.requests.unchecked.UncheckedBuildConfig;
import com.example.teamcity.api.requests.unchecked.UncheckedProject;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static java.lang.String.format;

public class BuildConfigurationTest extends BaseApiTest{
    @Test
    public void createBuildConfigurationTest(){
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        new CheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject());

        var buildConfig = new CheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType());

        softy.assertThat(buildConfig.getId()).isEqualTo(testData.getBuildType().getId());
        softy.assertThat(buildConfig.getName()).isEqualTo(testData.getBuildType().getName());
        softy.assertThat(buildConfig.getProject().getId()).isEqualTo(testData.getBuildType().getProject().getId());
        softy.assertThat(buildConfig.getProject().getName()).isEqualTo(testData.getBuildType().getProject().getName());
    }

    @Test
    public void userUnableToCreateTwoBuildConfigurationWithSameIdTest(){
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        new CheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(format("The build configuration / template ID \"%s\" is already used by another configuration", testData.getBuildType().getId())));
    }

    @Test
    public void userUnableToCreateTwoBuildConfigurationWithSameNameTest(){
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(firstTestData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(firstTestData.getUser())).create(firstTestData.getProject());

        new UncheckedProject(Specifications.getSpec().authSpec(firstTestData.getUser())).create(secondTestData.getProject());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(firstTestData.getUser()))
                .create(firstTestData.getBuildType());

        firstTestData.getBuildType().setId(firstTestData.getBuildType().getName());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(firstTestData.getUser()))
                .create(firstTestData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(format("Build configuration with name \"%s\" already exists", firstTestData.getBuildType().getName())));
    }

    @Test
    public void userUnableToCreateBuildConfigurationWithNonexistentProjectTest(){
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("No project found by locator" +
                        " 'count:1,id:"+ testData.getProject().getId() + "'"));
    }

    @Test
    public void userCanCreateBuildConfigurationWithoutIdTest(){
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject());

        testData.getBuildType().setId(null);

        var buildConfig = new CheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType());

        softy.assertThat(buildConfig.getId()).isNotEmpty();
        softy.assertThat(buildConfig.getName()).isEqualTo(testData.getBuildType().getName());
    }

    @Test
    public void userCanCreateBuildConfigurationWithoutNameTest(){
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject());

        testData.getBuildType().setName(null);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(format("When creating a build type, non empty name should be provided.")));
    }

    @Test
    public void userCanCreateBuildConfigurationWithoutProjectTest(){
        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec()).create(testData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser())).create(testData.getProject());

        testData.getBuildType().setProject(null);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(format("Build type creation request should contain project node.")));
    }
}