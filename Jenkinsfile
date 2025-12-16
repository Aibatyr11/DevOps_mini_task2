pipeline {
  agent none

  environment {
    IMAGE_NAME = "aibatyr/todo-app"
    IMAGE_TAG  = "1.0"
    MAVEN_OPTS = "-Djava.net.preferIPv4Stack=true"
  }

  stages {
    stage('Build') {
      agent { docker { image 'maven:3.9-eclipse-temurin-17-alpine' } }
      steps {
        sh 'mvn -B clean package -DskipTests'
      }
    }

    stage('Test') {
      agent { docker { image 'maven:3.9-eclipse-temurin-17-alpine' } }
      steps {
        sh 'mvn -B test'
      }
    }

    stage('Docker Build') {
      agent {
        docker {
          image 'docker:27-cli'
          args '-v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.docker:/home/jenkins/.docker'
        }
      }
      steps {
        sh 'docker version'
        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f Dockerfile_BakhitbekovAibatyr ."
      }
    }
  }

  post {
    success { echo 'Pipeline finished successfully' }
    failure { echo 'Pipeline failed' }
  }
}
