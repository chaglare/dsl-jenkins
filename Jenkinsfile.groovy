pipeline{
    agent any
    parameters {string(defaultValue: "plan", description: "plan, apply, destroy", name: 'USER_ACTION')}
    stages{
        stage("Run Command"){
            steps{
                sh '''
                set +xe
                echo Hello
                ech  Error
                sudo yum install httpd wget unzip -y
                ping -c 4 google.com
                '''
            }
        }
        stage("Download Terraform"){
            steps{
                ws("tmp/"){
                    script {
                        def exists = fileExists 'terraform_0.12.7_linux_amd64.zip'
                        if (exists) {
                            sh "unzip -o terraform_0.12.7_linux_amd64.zip"
                            sh "sudo mv -f terraform /bin"
                            sh "terraform version"
                        } else {
                            sh "wget https://releases.hashicorp.com/terraform/0.12.7/terraform_0.12.7_linux_amd64.zip"
                            sh "unzip -o terraform_0.12.7_linux_amd64.zip"
                            sh "sudo mv -f terraform /bin"
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
                    sh "cat TestFile"
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
        stage("Build Image"){
            steps{
                //sh "packer build updated/updated.json"
                echo "Hello"
            }
        }
        stage("Clone VPC Repo"){
            steps{
                ws("terraform/"){
                    git "https://github.com/farrukh90/infrastructure_april.git"
                }
            }
        }
        stage("Get module"){
            steps{
                ws("terraform/"){
                    sh "terraform get"
                }
            }
        }
        stage("initialize terraform"){
            steps{
                ws("terraform/"){
                    sh "terraform init"
                }
            }
        }
         stage("Build VPC "){
            steps{
                ws("terraform/"){
                    sh "terraform  ${USER_ACTION} -var-file=dev.tfvars -auto-approve"
                }
            }
        }
    }
    post{
        success {
            echo "Done"
        }
        failure {
            mail to:  "farrukhsadykov@gmail.com", subject: "job", body: "job completed"
        }
    }
}