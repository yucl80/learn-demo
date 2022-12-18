package com.yucl.learn.demo;

import org.archguard.scanner.core.client.ArchGuardClient;
import org.archguard.scanner.core.context.AnalyserType;
import org.archguard.scanner.core.diffchanges.DiffChangesContext;
import org.archguard.scanner.core.git.GitContext;
import org.archguard.scanner.core.sca.ScaContext;
import org.jetbrains.annotations.NotNull;

public class GitDiffChangesContext implements DiffChangesContext, GitContext, ScaContext {
    @NotNull
    @Override
    public ArchGuardClient getClient() {
        return new TestArchGuardClient();
    }

    @NotNull
    @Override
    public String getBranch() {
        return "test";
    }

    @Override
    public int getDepth() {
        return 7;
    }

    @NotNull
    @Override
    public String getPath() {
        return "d:/tmp/kkkk1";
        //return "d:\\Downloads\\demo\\demo";
    }

    @NotNull
    @Override
    public String getSince() {
        return "9409f53e";
    }

    @NotNull
    @Override
    public AnalyserType getType() {
        return AnalyserType.DIFF_CHANGES;
    }

    @NotNull
    @Override
    public String getUntil() {
        return "49f81b30";
    }

    @NotNull
    @Override
    public String getRepoId() {
        return "ddd-monolithic-code-sample";
    }

    @Override
    public long getStartedAt() {
        return 0;
    }

    @NotNull
    @Override
    public String getLanguage() {
        return "java";
    }
}
