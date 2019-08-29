pipeline{
    agent any
    stages{
        stage("Run Command"){
            steps{
                sh '''
                set +xe
                echo Hello
                ech Error
                sudo yum install httpd wget unzip -y
                ping -c 4 google.com
                '''
            }
        }
        stage("Download Terraform"){
            steps{
                ws("tmp/"){
                    script {
                        def exists = fileExists 'terraform_0.11.9_linux_amd64.zip'
                        if (exists) {
                            sh "unzip -o terraform_0.11.9_linux_amd64.zip"
                            sh "sudo mv terraform /bin"
                            sh "terraform version"
                        } else {
                            sh "wget https://releases.hashicorp.com/terraform/0.11.9/terraform_0.11.9_linux_amd64.zip"
                            sh "unzip -o terraform_0.11.9_linux_amd64.zip"
                            sh "sudo mv terraform /bin"
                            sh "terraform version"
                        }
                    }
                }
            }
        }
        stage("Write to a file"){
            steps{
                ws("tmp/"){
                    writeFile text: "Test", file: "TestFile"
                }
            }
        }
        stage("Download Packer"){
            steps{
                ws("tmp/"){
                    script {
                        def exists = fileExists 'packer_1.4.3_linux_amd64.zip'
                        if (exists) {
                            sh "unzip -o packer_1.4.3_linux_amd64.zip"
                            sh "sudo mv packer /bin"
                            sh "packer version"
                        } else {
                            sh "wget https://releases.hashicorp.com/packer/1.4.3/packer_1.4.3_linux_amd64.zip"
                            sh "unzip -o packer_1.4.3_linux_amd64.zip"
                            sh "sudo mv packer /bin"
                            sh "packer version"
                        }
                    }
                }
            }
        }
        stage("Pull Repo"){
            steps{
                git("https://github.com/chaglare/packerdev.git")
            }
        }
        stage("Build Image"){
            steps{
                //sh "packer build updated/updated.json"
                echo "Hello"
            }
        }
    }
    post{
        success {
            echo "Done"
        }
        failure {
            mail to:  "chaglare@gmail.com", subject: "job", body: "job completed"
        }
          
    }
}