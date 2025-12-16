pipeline {
  agent none

  environment {
    IMAGE_NAME = "aibatyr/todo-app"
    IMAGE_TAG  = "1.0"
  }

  stages {

    stage('Build') {
      agent { docker { image 'maven:3.9-eclipse-temurin-17' } }
      steps {
        sh 'mvn -B clean package'
      }
    }

    stage('Test') {
      agent { docker { image 'maven:3.9-eclipse-temurin-17' } }
      steps {
        sh 'mvn -B test'
      }
    }

    stage('Docker Build') {
      agent {
        docker {
          image 'docker:27-cli'
          args "-v /var/run/docker.sock:/var/run/docker.sock"
 вижenkins -w ${WORKSPACE} не нужно, WORKSPACE может быть пустой на старте
        }
      }
      steps {
        sh '''
          export DOCKER_CONFIG="$(pwd)/.docker"
          mkdir -p "$DOCKER_CONFIG"
          docker version
          docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f Dockerfile_BakhitbekovAibatyr .
        '''
      }
    }

    stage('(Optional) Docker Push') {
      when { expression { return env.PUSH_IMAGE == 'true' } }
      agent {
        docker {
          image 'docker:27-cli'
          args "-v /var/run/docker.sock:/var/run/docker.sock"
        }
      }
      steps {
        sh 'export DOCKER_CONFIG="$(pwd)/.docker"; mkdir -p "$DOCKER_CONFIG"'
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
          sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
          sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
        }
      }
    }
  }

  post {
    success { echo 'Pipeline finished successfully' }
    failure { echo 'Pipeline failed' }
  }
}
