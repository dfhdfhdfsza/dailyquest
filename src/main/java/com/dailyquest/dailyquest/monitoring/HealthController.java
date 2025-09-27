package com.dailyquest.dailyquest.monitoring;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final Environment env;
    private final BuildProperties build;
    private final GitProperties git;

    @GetMapping("/health")
    public ResponseEntity<?> health(){
        String[] profiles=env.getActiveProfiles();
        String activeProfiles=profiles.length>0?String.join(",",profiles):"default";

        String version=(build !=null&& StringUtils.hasText(build.getVersion()))
                ? build.getVersion():"unknown";

        String commit=(git!=null&&StringUtils.hasText(git.getShortCommitId()))
                ?git.getShortCommitId():"unknown";

        Map<String, Object>body=Map.of(
                "success",true,
                "status","UP",
                "timestamp", OffsetDateTime.now().toString(),
                "version",version,
                "profile",activeProfiles,
                "commit",commit
        );
        return ResponseEntity.ok(body);
    }







}
