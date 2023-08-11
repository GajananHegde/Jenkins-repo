def for_loop_test(String numbers)
{
    numbers.tokenize(",").each { num ->
        echo "$num"
    }
    // String command=""
    // for ( str in stringList )
    // {
    //     command= command+str+" "
    // }
    // env.command1=command
}

// def inject_env(){
//     env.HEY_YO_THIS_IS_THE_PLACE_HOLDER = '--firstArg first --secondArg second'
//     env.deploy_test_var1='Var 1 - Hello'
//     env.deploy_test_var2='Var 2 - World'
//     env.environ_file='.Build-Dir/project-Jenkins/.build/env'
//     env.nginx_file_path = '.Build-Dir/project-Jenkins/.build/'
//     env.nginx_configs_path='.Build-Dir/project-Jenkins/.build/nginx'
//     env.maintenance_page_dir = '.Build-Dir/project-Jenkins/.build/maintenance'
//     env.ssh_username = 'gajanan.hegde'
//     env.nginx_conf_dir = '/home/gajanan.hegde/Work/projects/bmo'
//     deploy_ssh_host = 'devopsworkspace-d.gale-services-default.g43labs.net'
    // env.env_file_name="\'p-bmo-commercial-nginx-us-redirect-prod-app-1\' \'p-bmo-commercial-nginx-us-redirect-prod-app-2\'"
    // env.aws_region='us-west-2'
    // switch (variable1) {
    //     case 'first':
    //         env.build_environment = 'cft-qa'
    //         break
    //     case 'second':
    //         env.build_environment = 'stable'
    //         break
    // }
    // test_cli_command(env.env_file_name)
    // env.stringArray="one,two,three"
    // for_loop_test(env.stringArray)
    // print(env.stringArray instanceof String

    // switch(build_branch) {
    //     case 'develop':
    //         env.deploy_test_var2='Var 2 - World - Develop branch'
    //         stage('Stage: Testing Stage execution in switch Statement'){
    //             echo "Hello I m executing within a stage statement inside a switch case! Yaay!!!!"
    //             sh 'echo helloshscript'
    //         }
    //         break
    // }
// }

def inject_env(deploy_environment){
    switch (deploy_environment){
        case 'wells':
            env.build_env = deploy_environment
            env.environment_starting_letter = 'p'
            env.subnets = 'subnet-009b9198c8c676ea5'
            env.security_groups = 'sg-0cf66b11b856e9af5'
            env.environment = 'prod'
            break
        case 'cft-qa':
            env.build_env = deploy_environment
            env.environment_starting_letter = 'q'
            env.subnets = '\'subnet-0a1807c169d4ba548\',\'subnet-06163ecaf0273e89d\',\'subnet-0922504ca7a88b1f7\''
            env.security_groups = 'sg-0d68a6694858e166a'
            env.environment = 'dev'
            break
        case 'markey':
            env.build_env = deploy_environment
            env.environment_starting_letter = 'q'
            env.subnets = '\'subnet-0a1807c169d4ba548\',\'subnet-06163ecaf0273e89d\',\'subnet-0922504ca7a88b1f7\''
            env.security_groups = 'sg-0d68a6694858e166a'
            env.environment = 'dev'
            break
        case 'hnr-qa':
            env.build_env = deploy_environment
            env.environment_starting_letter = 'q'
            env.subnets = '\'subnet-0a1807c169d4ba548\',\'subnet-06163ecaf0273e89d\',\'subnet-0922504ca7a88b1f7\''
            env.security_groups = 'sg-0d68a6694858e166a'
            env.environment = 'dev'
    }
}

def second_function (){
    stage('step after nginx'){
        sh """
        if [ ${build_environment} == 'cft-qa']; then
         echo "env is ${build_environment} - inside if"
        else
         echo "env is ${build_environment} - inside else"
        fi
        """
    }
}

def test_cli_command(String env_file)
{
    sh """
    aws s3 ls
    aws ssm get-parameters --with-decryption --names ${env_file} --region ${aws_region} | jq -r '.Parameters[].Value'
    """
}

def inject_stage (String build_branch){
    def brr="${env.build_branch}"
    echo "${build_branch}"
    echo "${env.build_branch}"
    switch(build_branch){
        case 'develop':
            echo "We are in the switch condition of inject_stage method!"
            echo "brr"
            echo '${brr}'
            echo '"${brr}"'
            break
    }
}

def switch_inside_switch(String first, String second)
{
    switch (first){
        case 'one':
            sh """
                echo "this is in first switch"
            """
            switch(second){
                case 'two':
                sh """
                    echo "this is in second"
                """
                break
            }
            break
    }
}

def scp_load_test()
{
    sh """
        rsync -e \"ssh -o StrictHostKeyChecking=no\" -avh ${maintenance_page_dir}/ ${ssh_username}@${deploy_ssh_host}:${nginx_conf_dir}/maintenance/ --delete
    """
    // ssh -o \"StrictHostKeyChecking=no\" ${ssh_username}@${deploy_ssh_host} \"cd .Build-Dir/project-Jenkins/.build/ && rm -rf maintenance
}

def db_sync_from()
{
    sh """
    /opt/homebrew/bin/aws ecs run-task \
       --cluster p-dash-rdsbackup \
       --task-definition rdsbackup-taskdefinitions:3 \
       --launch-type FARGATE \
       --overrides '{"containerOverrides": [{"name": "rdsbackup", "environment": [{ "name": "IS_DOWNSYNC_DB", "value": "True"},{"name": "ISFROM", "value": "True"},{"name": "UPSTREAM_CLIENT_NAME_BUILD", "value": "${build_env}"},{"name": "UPSTREAM_ENV_LETTER_BUILD", "value": "${environment_starting_letter}"},{"name": "TARGETPATH", "value": "${environment}/${build_env}/${build_env}-${env.BUILD_NUMBER}"}]}]}' \
       --network-configuration "awsvpcConfiguration={subnets=["${subnets}"],securityGroups=["${security_groups}"],assignPublicIp='ENABLED'}"
    """
}

def db_sync_to()
{
    sh """
    /opt/homebrew/bin/aws ecs run-task \
        --cluster p-dash-rdsbackup \
        --task-definition rdsbackup-taskdefinitions:3 \
        --launch-type FARGATE \
        --overrides '{"containerOverrides": [{"name": "rdsbackup", "environment": [{ "name": "IS_DOWNSYNC_DB", "value": "True"},{"name": "ISFROM", "value": "False"},{"name": "DOWNSTREAM_CLIENT_NAME_BUILD", "value": "${build_env}"},{"name":"DOWNSTREAM_ENV_LETTER_BUILD", "value": "${environment_starting_letter}"},{"name": "SOURCEPATH", "value": "${upstream_environment}/${upstream_build_env}/${upstream_build_env}-${env.BUILD_NUMBER}"},{"name": "TARGETPATH", "value": "${environment}/${build_env}/${build_env}-${env.BUILD_NUMBER}"}]}]}' \
        --network-configuration "awsvpcConfiguration={subnets=["${subnets}"],securityGroups=["${security_groups}"],assignPublicIp='ENABLED'}"
    """
}


// def mainfunc(String build_branch, String build_number, String build_job, String build_url) {
def mainfunc(String from_db, String to_db){
    inject_env(from_db)
    sh """
    echo "build_env :${build_env}"
    """
    db_sync_from()
    env.upstream_environment = "${env.environment}"
    env.upstream_build_env = "${env.build_env}"
    inject_env(to_db)
    sh """
    sleep 20
    echo --network-configuration "awsvpcConfiguration={subnets=["${subnets}"],securityGroups=["${security_groups}"],assignPublicIp='ENABLED'}"
    """
    db_sync_to()
    // switch_inside_switch(from_db,to_db)
    // echo "-- ENV for building - ${param}"
    // env.variable1 = 'Hohoho'
    // env.variable2 = 'Hehehe'
    // switch(param){
    //     case 'Frontend':
    //         withCredentials([usernamePassword(credentialsId: 'bad9031b-235c-4373-be6f-4448ea28e601', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
    //         def encodedPassword = URLEncoder.encode("$GIT_PASSWORD",'UTF-8')
    //         sh '''
    //         echo hey this is the p[assword] "${GIT_PASSWORD}"
    //         '''
    //         }
    //         echo "We are in the frontend section"
    //         inject_env('first')
    //         // second_function()
    //         // inject_env('second')
    //         // second_function()
    //         scp_load_test()
    //         // for ( str in choices){
    //         //     print(str)
    //         // }
    //         // sh """
    //         //     sed -i '' -e "s/<% DEPLOY_AIRFLOW_DB_USER %>/${param}-${param}/g" ${environ_file}
    //         //     sed -i '' -e "s/<% Hey_yo_this_is_the_place_holder %>/${HEY_YO_THIS_IS_THE_PLACE_HOLDER}/g" ${environ_file}
    //         //     cat ${environ_file}
    //         // """
    //         // def stringArray=["one","two","three"]
    //         // String command = ""
    //         // for ( str in stringArray )
    //         // {
    //         //     command = command + str + " "
    //         // }
    //         // env.stringSize=stringArray.size()
    //         // echo command
    //         // echo param
    //         // echo "${env.command1}"
    //         // sh """
    //         // cat ${env.environ_file}
    //         // """
    //         break
    //     case 'Backend':
    //         echo "We are in the Backend section"
    //         echo "HELLO BACKEND"
    //         sleep(15)
    //         break
    // }

    // inject_env(build_branch)
    // inject_stage(build_branch)   
    // sleep 15     
}

def mainfunc2(String build_branch, String build_number, String build_job, String build_url){
    inject_env(build_branch)    
    stage('Only stage'){

        echo "hello-World"
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'alchemy_core_mock_deploy_django_db_creds_develop' , usernameVariable: 'deploy_django_db_user', passwordVariable: 'deploy_django_db_password']]) {
        sh """
            sed -i "s/<% DEPLOY_DJANGO_DB_PASSWORD %>/${deploy_django_db_password}/g" ${environ_file}
            sed -i "s/<% DEPLOY_DJANGO_DB_USER %>/${deploy_django_db_user}/g" ${environ_file}
        """
        }

        sh """

        cat ${environ_file}
        ls -larth
        """
        sleep 20
    }
    stage('Parallel stage'){
        echo "hello-World - Parallel"

        sh """
        echo 'This is the parallel stage'
        ls -larth
        """  
    }
}

return this
