package com.example.teamcity.api.models.ServerAuthSettings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectConfiguration {
    private boolean perProjectPermissions;
    private Modules modules;
}

