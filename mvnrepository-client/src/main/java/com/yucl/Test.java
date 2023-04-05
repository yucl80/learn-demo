package com.yucl;

import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.agent.DependencyCheckScanAgent;
import org.owasp.dependencycheck.data.nvdcve.DatabaseException;
import org.owasp.dependencycheck.dependency.Confidence;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.EvidenceType;
import org.owasp.dependencycheck.dependency.Vulnerability;
import org.owasp.dependencycheck.exception.ExceptionCollection;
import org.owasp.dependencycheck.exception.ReportException;
import org.owasp.dependencycheck.exception.ScanAgentException;
import org.owasp.dependencycheck.reporting.ReportGenerator;
import org.owasp.dependencycheck.utils.FileUtils;
import org.owasp.dependencycheck.utils.SeverityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Test {
    public static void main(String[] args) throws ScanAgentException {
        List<Dependency> dependencies = new ArrayList<Dependency>();
        Dependency dependency = new Dependency(new File(FileUtils.getBitBucket()));

        dependency.addEvidence(EvidenceType.PRODUCT, "my-datasource", "name", "Jetty", Confidence.HIGH);
        dependency.addEvidence(EvidenceType.VERSION, "my-datasource", "version", "5.1.10", Confidence.HIGH);
        dependency.addEvidence(EvidenceType.VENDOR, "my-datasource", "vendor", "mortbay", Confidence.HIGH);
        dependencies.add(dependency);

        DependencyCheckScanAgent scan = new DependencyCheckScanAgent();
        scan.setDataDirectory("L:\\workspaces\\IdeaProjects\\dependency-check-8.2.1-release\\dependency-check\\data");
        System.out.println(scan.getDataDirectory());
        scan.setDependencies(dependencies);
        scan.setReportFormat(ReportGenerator.Format.HTML);
        scan.setReportOutputDirectory(System.getProperty("user.home"));
        scan.execute();
    }




}
