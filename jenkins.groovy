#!/usr/bin/env groovy

jobDsl scriptText:
"""
job("DEV/autopatch_check_ami") {
	description("This job is to check AMI and create DocGen Custom AMI")
    parameters {
		stringParam("Branch", "integration", "EDCM-6328")
	}
	configure { project ->
        project / 'properties' << 'jenkins.plugins.office365connector.WebhookJobProperty' {
            webhooks {
                'jenkins.plugins.office365connector.Webhook' {
                    name('DocGen - DevOps')
                    url('https://outlook.office.com/webhook/01ca9574-d65f-.........')
                    startNotification(false)
                    notifySuccess(true)
                    notifyAborted(false)
                    notifyNotBuilt(false)
                    notifyUnstable(true)
                    notifyFailure(true)
                    notifyBackToNormal(true)
                    notifyRepeatedFailure(false)
                    timeout(30000)
                    macros(class: 'empty-list')
                }
            }
        }
    }
	keepDependencies(false)
	environmentVariables {
		env("Environment", "dev")
		groovy()
		loadFilesFromMaster(false)
		keepSystemVariables(true)
		keepBuildVariables(true)
		overrideBuildParameters(false)
	}
	disabled(false)
	concurrentBuild(true)
	triggers {
        cron('30 07 * * 1,2,3,4,5')
  	}
	wrappers
	{
		timestamps()
		buildUserVars()
		injectPasswords {
            injectGlobalPasswords()
        }
		maskPasswords()
	}
    scm {
        git {
            remote {
                    url("git@github.aus.thenational.com:EDCM/docgen_infraprovisioning.git")
                    credentials("svc-account")
                }
                branches('\${Branch}')
                extensions {
                    cleanBeforeCheckout()
                    }
            }
    }
	steps {
		shell('''set +x

echo "Hello world
''')
	}
	wrappers {
		preBuildCleanup {
			deleteDirectories(false)
			cleanupParameter()
		}
	}
	configure {
		it / 'properties' / 'com.sonyericsson.rebuild.RebuildSettings' {
			'autoRebuild'('false')
			'rebuildDisabled'('false')
		}
	}
}
"""
