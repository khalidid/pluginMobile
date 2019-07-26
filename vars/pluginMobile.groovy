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
		}

		stage('release') {
			echo 'stage release'
			// TODO add release task
		}
	}
}