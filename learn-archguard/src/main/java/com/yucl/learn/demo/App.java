package com.yucl.learn.demo;

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
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        if(! new File("d:/tmp/kkkk2").exists()) {
            try {
                setProxy();
                Git.cloneRepository()
                        .setURI("https://github.com/archguard/ddd-monolithic-code-sample")
                        .setDirectory(new File("d:/tmp/kkkk2"))
                        .call();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private static void setProxy() {
        ProxySelector.setDefault(new ProxySelector() {
            final ProxySelector delegate = ProxySelector.getDefault();

            @Override
            public List<Proxy> select(URI uri) {
                // Filter the URIs to be proxied
                if (uri.toString().contains("github")
                        && uri.toString().contains("https")) {
                    return Arrays.asList(new Proxy(Proxy.Type.HTTP, InetSocketAddress
                            .createUnresolved("localhost", 61293)));
                }
                if (uri.toString().contains("github")
                        && uri.toString().contains("http")) {
                    return Arrays.asList(new Proxy(Proxy.Type.HTTP, InetSocketAddress
                            .createUnresolved("localhost", 61293)));
                }
                // revert to the default behaviour
                return delegate == null ? Arrays.asList(Proxy.NO_PROXY)
                        : delegate.select(uri);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                if (uri == null || sa == null || ioe == null) {
                    throw new IllegalArgumentException(
                            "Arguments can't be null.");
                }
            }
        });
    }

    private static void classDiff(DiffChangesContext context) {
        DiffChangesAnalyser diffChangesAnalyser = new DiffChangesAnalyser(context);
        List<ChangedCall> list = diffChangesAnalyser.analyse();
        list.forEach(changedCall -> {
            System.out.println(changedCall.getPackageName() + "." + changedCall.getClassName() + " : " + changedCall.getPath());
            changedCall.getRelations().forEach(changeRelation -> {
                System.out.println("......" + changeRelation.getSource() + "  : " + changeRelation.getTarget());
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
