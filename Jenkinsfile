pipeline {
  agent none

  environment {
    IMAGE_NAME = "aibatyr/todo-app"
    IMAGE_TAG  = "1.0"
  }

  stages {

    stage('Checkout') {
      agent any
      steps {
        checkout scm
      }
    }

    stage('Build (Maven)') {
      agent {
        docker {
          image 'maven:3.9-eclipse-temurin-17-alpine'
          args  '-u root:root'
          reuseNode true
        }
      }
      steps {
        sh 'mvn -B -Djava.net.preferIPv4Stack=true clean package -DskipTests'
      }
    }

    stage('Test (Maven)') {
      agent {
        docker {
          image 'maven:3.9-eclipse-temurin-17-alpine'
          args  '-u root:root'
          reuseNode true
        }
      }
      steps {
        sh 'mvn -B -Djava.net.preferIPv4Stack=true test'
      }
    }

    stage('Docker Build') {
      agent {
        docker {
          image 'docker:27-cli'
          args '--entrypoint="" -u root:root -v /var/run/docker.sock:/var/run/docker.sock'
          reuseNode true
        }
      }
      steps {
        sh 'docker version'
        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f Dockerfile_BakhitbekovAibatyr ."
      }
    }

    stage('Docker Login') {
      agent {
        docker {
          image 'docker:27-cli'
          args '--entrypoint="" -u root:root -v /var/run/docker.sock:/var/run/docker.sock'
          reuseNode true
        }
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
          sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
        }
      }
    }

    stage('Docker Push') {
      agent {
        docker {
          image 'docker:27-cli'
          args '--entrypoint="" -u root:root -v /var/run/docker.sock:/var/run/docker.sock'
          reuseNode true
        }
      }
      steps {
        sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
      }
      post {
        always {
          // тут есть agent -> sh работает
          sh 'docker logout || true'
        }
      }
    }

    stage('Deploy (docker compose)') {
      when {
        expression { fileExists('docker-compose.yml') || fileExists('docker-compose.yaml') }
      }
      agent {
        docker {
          image 'docker:27-cli'
          args '--entrypoint="" -u root:root -v /var/run/docker.sock:/var/run/docker.sock'
          reuseNode true
        }
      }
      steps {
        sh 'apk add --no-cache docker-cli-compose || true'
        sh 'docker compose version || true'
        sh 'docker compose pull || true'
        sh 'docker compose up -d'
        sh 'docker ps'
      }
    }
  }

  post {
    success {
      echo '✅ Pipeline success: image built + tested + pushed (+ deployed if compose exists)'
    }
    failure {
      echo '❌ Pipeline failed'
    }
  }
}
