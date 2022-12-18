package com.yucl.learn.demo;

import chapi.domain.core.CodeDataStruct;
import org.archguard.rule.core.Issue;
import org.archguard.scanner.core.client.ArchGuardClient;
import org.archguard.scanner.core.diffchanges.ChangedCall;
import org.archguard.scanner.core.git.GitLogs;
import org.archguard.scanner.core.sca.CompositionDependency;
import org.archguard.scanner.core.sourcecode.CodeDatabaseRelation;
import org.archguard.scanner.core.sourcecode.ContainerService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestArchGuardClient implements ArchGuardClient {
    @Override
    public void saveApi(@NotNull List<ContainerService> list) {

    }

    @Override
    public void saveDataStructure(@NotNull List<? extends CodeDataStruct> list) {

    }

    @Override
    public void saveDependencies(@NotNull List<CompositionDependency> list) {

    }

    @Override
    public void saveDiffs(@NotNull List<ChangedCall> list) {
        System.out.println("List<ChangedCall> size : "+list.size());
    }

    @Override
    public void saveGitLogs(@NotNull List<GitLogs> list) {
        System.out.println(list.size());
    }

    @Override
    public void saveRelation(@NotNull List<CodeDatabaseRelation> list) {

    }

    @Override
    public void saveRuleIssues(@NotNull List<Issue> list) {

    }
}
