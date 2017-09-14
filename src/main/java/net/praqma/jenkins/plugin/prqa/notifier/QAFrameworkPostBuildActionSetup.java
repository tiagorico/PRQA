/*
 * The MIT License
 *
 * Copyright 2013 Praqma.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.praqma.jenkins.plugin.prqa.notifier;

import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.Setter;
import net.praqma.jenkins.plugin.prqa.globalconfig.PRQAGlobalConfig;
import net.praqma.jenkins.plugin.prqa.globalconfig.QAVerifyServerConfiguration;
import net.praqma.jenkins.plugin.prqa.setup.QAFrameworkInstallationConfiguration;
import net.praqma.jenkins.plugin.prqa.threshold.AbstractThreshold;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class QAFrameworkPostBuildActionSetup extends PostBuildActionSetup {

    private String qaInstallation;
    private String qaProject;
    private String unifiedProjectName;
    private boolean useCustomLicenseServer;
    private String customLicenseServerAddress;
    private boolean downloadUnifiedProjectDefinition;
    private boolean performCrossModuleAnalysis;
    private boolean enableDependencyMode;
    private boolean generateReport;
    private boolean publishToQAV;
    private boolean loginToQAV;
    private List<String> chosenServers;
    private boolean uploadWhenStable;
    private String qaVerifyProjectName;
    private String uploadSnapshotName;
    private String buildNumber;
    private String uploadSourceCode;
    private boolean generateCrr;
    private boolean generateMdr;
    private boolean generateSup;
    private boolean analysisSettings;
    private boolean stopWhenFail;
    private boolean generatePreprocess;
    private boolean assembleSupportAnalytics;
    private boolean generateReportOnAnalysisError;

    @DataBoundConstructor
    public QAFrameworkPostBuildActionSetup(String qaInstallation,
                                           String qaProject,
                                           boolean useCustomLicenseServer,
                                           String customLicenseServerAddress,
                                           boolean downloadUnifiedProjectDefinition,
                                           String unifiedProjectName,
                                           boolean performCrossModuleAnalysis,
                                           boolean enableDependencyMode,
                                           boolean generateReport,
                                           boolean publishToQAV,
                                           boolean loginToQAV,
                                           List<String> chosenServers,
                                           boolean uploadWhenStable,
                                           String qaVerifyProjectName,
                                           String uploadSnapshotName,
                                           String buildNumber,
                                           String uploadSourceCode,
                                           boolean generateCrr,
                                           boolean generateMdr,
                                           boolean generateSup,
                                           boolean analysisSettings,
                                           boolean stopWhenFail,
                                           boolean generatePreprocess,
                                           boolean assembleSupportAnalytics,
                                           boolean generateReportOnAnalysisError) {

        this.qaInstallation = qaInstallation;
        this.qaProject = qaProject;
        this.useCustomLicenseServer = useCustomLicenseServer;
        this.customLicenseServerAddress = customLicenseServerAddress;
        this.downloadUnifiedProjectDefinition = downloadUnifiedProjectDefinition;
        this.unifiedProjectName = unifiedProjectName;
        this.performCrossModuleAnalysis = performCrossModuleAnalysis;
        this.enableDependencyMode = enableDependencyMode;
        this.generateReport = generateReport;
        this.publishToQAV = publishToQAV;
        this.loginToQAV = loginToQAV;
        this.chosenServers = chosenServers;
        this.uploadWhenStable = uploadWhenStable;
        this.qaVerifyProjectName = qaVerifyProjectName;
        this.uploadSnapshotName = uploadSnapshotName;
        this.buildNumber = buildNumber;
        this.uploadSourceCode = uploadSourceCode;
        this.generateCrr = generateCrr;
        this.generateMdr = generateMdr;
        this.generateSup = generateSup;
        this.analysisSettings = analysisSettings;
        this.stopWhenFail = stopWhenFail;
        this.generatePreprocess = generatePreprocess;
        this.assembleSupportAnalytics = assembleSupportAnalytics;
        this.generateReportOnAnalysisError = generateReportOnAnalysisError;
    }

    @Extension
    public final static class DescriptorImpl extends PRQAReportSourceDescriptor<QAFrameworkPostBuildActionSetup> {

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            save();
            return super.configure(req, json);
        }

        public DescriptorImpl() {
            super();
            load();
        }

        @Override
        public String getDisplayName() {
            return "PRQA·Framework";
        }

        public FormValidation doCheckCustomLicenseServerAddress(@QueryParameter String customLicenseServerAddress) {
            final String serverRegex = "^(\\d{1,5})@(.+)$";

            if (StringUtils.isBlank(customLicenseServerAddress)) {
                return FormValidation.error("Custom license server address must not be empty");
            } else if (!customLicenseServerAddress.matches(serverRegex)) {
                return FormValidation.error("License server format must be <port>@<host>");
            } else {
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckQAInstallation(@QueryParameter String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error("Error");
            } else {
                return FormValidation.ok();
            }
        }

        public ListBoxModel doFillUploadSourceCodeItems() {
            ListBoxModel SourceOption = new ListBoxModel();
            SourceOption.add("None", "NONE");
            SourceOption.add("All", "ALL");
            SourceOption.add("Only not in VCS", "NOT_IN_VCS");
            return SourceOption;
        }

        public FormValidation doCheckCMAProjectName(@QueryParameter String CMAProjectName) {
            if (StringUtils.isBlank(CMAProjectName)) {
                return FormValidation.errorWithMarkup("CMA project name should not be empty!");
            }
            if (!CMAProjectName.matches("^[a-zA-Z0-9_-]+$")) {
                return FormValidation.errorWithMarkup("CMA project name is not valid [characters allowed: a-zA-Z0-9-_]");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUnifiedProjectName(@QueryParameter String unifiedProjectName) {
            if (StringUtils.isBlank(unifiedProjectName)) {
                return FormValidation.errorWithMarkup("Unified Project name should not be empty!");
            }
            if (!unifiedProjectName.matches("^[a-zA-Z0-9_-{}()$%]+$")) {
                return FormValidation.errorWithMarkup("Unified project name is not valid [characters allowed: a-zA-Z0-9-_{}()$%]");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUploadSnapshotName(@QueryParameter String uploadSnapshotName) {
            if (StringUtils.isBlank(uploadSnapshotName)) {
                return FormValidation.errorWithMarkup("Snapshot name should not be empty!");
            }
            if (!uploadSnapshotName.matches("^[a-zA-Z0-9_-{}()$%]+$")) {
                return FormValidation.errorWithMarkup("Snapshot name is not valid [characters allowed: a-zA-Z0-9-_{}()$%]");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckQaVerifyProjectName(@QueryParameter String qaVerifyProjectName) {
            if (StringUtils.isBlank(qaVerifyProjectName)) {
                return FormValidation.errorWithMarkup("Project name should not be empty!");
            }
            if (!qaVerifyProjectName.matches("^[a-zA-Z0-9_-{}()$%]+$")) {
                return FormValidation.errorWithMarkup("Project name is not valid [characters allowed: a-zA-Z0-9-_{}()$%]");
            }
            return FormValidation.ok();
        }

        public ListBoxModel doFillQaInstallationItems() {
            ListBoxModel model = new ListBoxModel();

            for (QAFrameworkInstallationConfiguration suiteQAFramework : getQAFrameworkTools()) {
                model.add(suiteQAFramework.getName());
            }
            return model;
        }

        public List<QAFrameworkInstallationConfiguration> getQAFrameworkTools() {
            Jenkins jenkins = Jenkins.getInstance();

            if (jenkins == null) {
                throw new RuntimeException("Unable to aquire Jenkins instance");
            }

            QAFrameworkInstallationConfiguration[] prqaInstallations = jenkins
                    .getDescriptorByType(QAFrameworkInstallationConfiguration.DescriptorImpl.class).getInstallations();
            return Arrays.asList(prqaInstallations);
        }

        public List<ThresholdSelectionDescriptor<?>> getThresholdSelections() {
            return AbstractThreshold.getDescriptors();
        }

        public List<QAVerifyServerConfiguration> getServers() {
            return PRQAGlobalConfig.get().getServers();
        }

        public List<PRQAFileProjectSourceDescriptor<?>> getFileProjectSources() {
            return PRQAFileProjectSource.getDescriptors();
        }
    }
}
