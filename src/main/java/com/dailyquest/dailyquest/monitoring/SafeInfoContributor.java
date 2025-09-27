package com.dailyquest.dailyquest.monitoring;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SafeInfoContributor implements InfoContributor {
    private  final Environment env;
    private  final BuildProperties build;
    private  final GitProperties git;

    @Override
    public void contribute(Info.Builder builder){
        builder.withDetail("app", Map.of(
                "version",build!=null?build.getVersion():"unknwon",
                "profile",String.join(",",env.getActiveProfiles()),
                "commit",git!=null?git.getShortCommitId():"unknown"
        ));
    }
}
