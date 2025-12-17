pipeline {
  agent none

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  environment {
    IMAGE_NAME = "aibatyr/todo-app"
    // IMAGE_TAG зададим после checkout
  }

  stages {

    stage('Checkout') {
      agent any
      steps {
        checkout scm
        script {
          def shortCommit = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
          env.IMAGE_TAG = "${env.BUILD_NUMBER}-${shortCommit}"
          echo "Tag: ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
        }
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
      post {
        success {
          // артефакт (jar)
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
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
      post {
        always {
          // отчёты тестов (даже если тесты упали)
          junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
      }
    }

    stage('Static Analysis (optional)') {
      when {
        expression { fileExists('checkstyle.xml') }
      }
      agent {
        docker {
          image 'maven:3.9-eclipse-temurin-17-alpine'
          args  '-u root:root'
          reuseNode true
        }
      }
      steps {
        sh 'mvn -B checkstyle:check'
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
        // страховка: если по какой-то причине IMAGE_TAG пустой
        sh '[ -n "$IMAGE_TAG" ] || (echo "IMAGE_TAG is empty" && exit 1)'
        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest -f Dockerfile_BakhitbekovAibatyr ."
        sh "docker images | head -n 20"
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
        // сброс на всякий
        sh 'docker logout || true'

        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
          sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
        }

        sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
        sh "docker push ${IMAGE_NAME}:latest"
      }
      post {
        always {
          sh 'docker logout || true'
        }
      }
    }

    stage('Deploy (docker compose)') {
      when {
        allOf {
          branch 'main'
          expression { fileExists('docker-compose_BakhitbekovAibatyr.yml') }
        }
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
        sh 'docker compose -f docker-compose_BakhitbekovAibatyr.yml up -d'
        sh 'docker ps'
      }
    }
  }

  post {
    success {
      echo "✅ SUCCESS: pushed ${IMAGE_NAME}:${IMAGE_TAG} (and latest)"
    }
    failure {
      echo "❌ FAILED: check build/test/docker build/push logs above"
    }
  }
}
