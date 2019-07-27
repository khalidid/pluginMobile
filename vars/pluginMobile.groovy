def call(Closure context) {

	def config = [:]

	context.resolveStrategy = Closure.DELEGATE_FIRST
	context.delegate = config
	context()

	node {
		
		stage('clean') {
			echo "Clean projet : ${config.projectName}"
			deleteDir()
		}

		
		stage('checkout') {
		
			echo 'stage checkout'
			def gitUrl = config.gitUrl
			def credentialsId = config.credentialsId

			echo "GIT URL $gitUrl"
			echo "CREDENTIALS ID $credentialsId"

			
			def branch = "master"
			
			if (env.getEnvironment().containsKey("BRANCH")) {
				branch = BRANCH
			}

			if (credentialsId) {
				git branch: branch, credentialsId: credentialsId, url: gitUrl
			} else {
				git branch: branch, url: gitUrl
			}
		}

		
		stage('configure') {
			echo 'stage configure'
			def branches = "master\ndevelop"

			properties([
				parameters([
					choice(choices: branches, description: 'Branche de build', name: 'BRANCH'),
					booleanParam(defaultValue: false, description: 'Démarrer une release', name: 'RELEASE')
				]),
				disableConcurrentBuilds(),
				buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')),
				pipelineTriggers([pollSCM('@hourly')])
			])

			// Retrieve the value of the property use :
			// example : env.getEnvironment().containsKey(PROPERTY_NAME)
			// env.getEnvironment().containsKey('BRANCH')
			// or
			// env.getEnvironment().containsKey('RELEASE')
		}

		stage('build') {
			echo 'stage build'
			// TODO add build task
			// check branch selecreenv.getEnvironment().containsKey("VERSION")
			
				 withMaven(
        // Maven installation declared in the Jenkins "Global Tool Configuration"
         maven: 'Maven',
        // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
        // We recommend to define Maven settings.xml globally at the folder level using
        // navigating to the folder configuration in the section "Pipeline Maven Configuration / Override global Maven configuration"
        // or globally to the entire master navigating to  "Manage Jenkins / Global Tools Configuration"
        mavenSettingsConfig: 'adcc38ae-a77e-488f-8de9-569e537ec582') {
 
      // Run the maven build
      sh "mvn clean verify"
 
    } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe & FindBugs & SpotBugs reports...
			
			
		}

		stage('release') {
			echo 'stage release'
			// TODO add release task
		}
	}
}