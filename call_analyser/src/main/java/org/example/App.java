package org.example;

import org.archguard.scanner.analyser.DiffChangesAnalyser;
import org.archguard.scanner.analyser.GitAnalyser;
import org.archguard.scanner.analyser.ScaAnalyser;
import org.archguard.scanner.core.client.ArchGuardClient;
import org.archguard.scanner.core.context.AnalyserType;
import org.archguard.scanner.core.context.Context;
import org.archguard.scanner.core.diffchanges.ChangedCall;
import org.archguard.scanner.core.diffchanges.DiffChangesContext;
import org.archguard.scanner.core.git.GitContext;
import org.archguard.scanner.core.git.GitLogs;

import org.archguard.scanner.core.sca.CompositionDependency;
import org.archguard.scanner.core.sca.ScaContext;
import org.jetbrains.annotations.NotNull;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       try {
            Git.cloneRepository()
                    .setURI("https://github.com/archguard/ddd-monolithic-code-sample")
                    .setDirectory(new File("d:/tmp/kkkk1"))
                    .call();
        }catch (Exception e){
            e.printStackTrace();
        }
        Context context = new GitDiffChangesContext();
        classDiff((DiffChangesContext) context);
/*
        ScaAnalyser analyser =new ScaAnalyser((ScaContext) context);
        List<CompositionDependency> deps = analyser.analyse();
        deps.forEach(compositionDependency -> {
            System.out.println(compositionDependency);
        });*/


    }

    private static void classDiff(DiffChangesContext context) {
        DiffChangesAnalyser diffChangesAnalyser = new DiffChangesAnalyser(context);
        List<ChangedCall> list = diffChangesAnalyser.analyse();
        list.forEach(changedCall -> {
            System.out.println(changedCall.getPackageName() + "." +  changedCall.getClassName() + " : " + changedCall.getPath());
            changedCall.getRelations().forEach(changeRelation -> {
                System.out.println("......"+ changeRelation.getSource() + "  : " + changeRelation.getTarget());
            });
        });
    }

    private static void listGitDiff(GitContext context) {
        GitAnalyser analyser = new GitAnalyser(context);
        List<GitLogs> gitLogs = analyser.analyse();
        gitLogs.forEach(gitLogs1 -> {
            gitLogs1.getCommitLog().forEach(commitLog -> {
                System.out.println(commitLog);
            });

        });
    }
}
