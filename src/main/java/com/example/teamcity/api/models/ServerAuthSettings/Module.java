package com.example.teamcity.api.models.ServerAuthSettings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Module {
    private String name;
    private Properties properties;
}
