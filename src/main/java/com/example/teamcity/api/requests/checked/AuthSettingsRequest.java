package com.example.teamcity.api.requests.checked;

import com.example.teamcity.api.models.ServerAuthSettings.Module;
import com.example.teamcity.api.models.ServerAuthSettings.Modules;
import com.example.teamcity.api.models.ServerAuthSettings.ProjectConfiguration;
import com.example.teamcity.api.models.ServerAuthSettings.Property;
import com.example.teamcity.api.models.ServerAuthSettings.Properties;
import com.example.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;

public class AuthSettingsRequest {

    ProjectConfiguration projectConfig = ProjectConfiguration.builder()
            .perProjectPermissions(true)
            .modules(Modules.builder()
                    .module(Arrays.asList(
                            Module.builder()
                                    .name("HTTP-Basic").build(),
                            Module.builder()
                                    .name("Default")
                                    .properties(Properties.builder()
                                            .property(Arrays.asList(
                                                    Property.builder()
                                                            .name("usersCanResetOwnPasswords")
                                                            .value("true").build(),
                                                    Property.builder()
                                                            .name("usersCanChangeOwnPasswords")
                                                            .value("true").build(),
                                                    Property.builder()
                                                            .name("freeRegistrationAllowed")
                                                            .value("true").build(),
                                                    Property.builder()
                                                            .name("usersCanChangeOwnPasswords")
                                                            .value("false").build()
                                            )).build()
                                    ).build(),
                            Module.builder()
                                    .name("Token-Auth").build(),
                            Module.builder()
                                    .name("LDAP")
                                    .properties(Properties.builder()
                                            .property(Collections.singletonList(
                                                    Property.builder()
                                                            .name("allowCreatingNewUsersByLogin")
                                                            .value("true").build()
                                            )).build()
                                    ).build()
                    )).build()
            ).build();

    public String putAuthSettings() {
        return RestAssured
                .given()
                .spec(Specifications.getSpec().superUserSpec())
                .body(projectConfig)
                .put("/app/rest/server/authSettings")
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().asString();
    }
}
