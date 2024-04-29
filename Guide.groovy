I. DEFINITION 

1. What is Secret Manager?

. Secrets Manager is a service provided by Aws to store secrets.

.Aws Secrets Manager helps you Manage, Retrieve, and Rotate Database credentials,
Application Credentials, OAuth Tokens, ApI Keys, and other Secrets throughout 
their lifecycles. Many AWS services store and use secrets in Secrets Manager.

2. Benefits of Secrets Manager.

- Secrets Manager Improve Security Posture.

[[because you no longer need hard-coded credentials in application source code. 
Storing the credentials in Secrets Manager helps avoid possible compromise by anyone 
who can inspect your application or the components. You replace hard-coded credentials 
with a runtime call to the Secrets Manager service to Retrieve Credentials Dynamically 
when you need them.]]

- Ability Of Configuring  Automatic Rotation Schedule for your Secrets.

[[This enables you to replace long-term secrets with short-term ones, significantly reducing 
the risk of compromise. Since the credentials are no longer stored with the application, 
rotating credentials no longer requires updating your applications and deploying changes 
to application clients.]]

II. DATABASE 

How to move Database secrets in Aws?

1. need 2 Iam Role to Manage Permission to your secret:

- A Role that Manages the Secrets in your Organization.( Admin Roles)

SecretsManagerReadWrite
IAMFullAccess

- Another Role that can use the credentials at runtime, RoleToRetrieveSecretAtRuntime

2. Creation Steps on the Console:

Step 1: Create the secret

The first step is to copy the existing hardcoded credentials into a secret in Secrets Manager.
For the lowest latency, store the secret in the same Region as the database.

To create a secret
Open the Secrets Manager console at https://console.aws.amazon.com/secretsmanager/.

.Choose Store a new secret.

.On the Choose secret type page, do the following:

For Secret type, choose the type of database credentials to store:

Amazon RDS database

Amazon DocumentDB database

Amazon Redshift data warehouse.

For other types of secrets, see Replace hardcoded secrets .

For Credentials, enter the existing hardcoded credentials for the database.

For Encryption key, choose aws/secretsmanager to use the AWS managed key for Secrets Manager. There is no cost for using this key. You can also use your own customer managed key, for example to access the secret from another AWS account. For information about the costs of using a customer managed key, see Pricing.

For Database, choose your database.

.Choose Next.

.On the Configure secret page, do the following:

Enter a descriptive Secret name and Description.

.In Resource permissions, choose Edit permissions. 
Paste the following policy, 
which allows RoleToRetrieveSecretAtRuntime to retrieve the secret, and then choose Save.

{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::AccountId:role/RoleToRetrieveSecretAtRuntime"
      },
      "Action": "secretsmanager:GetSecretValue",
      "Resource": "*"
    }
  ]
}
At the bottom of the page, choose Next

.On the Configure rotation page, keep rotation off for now. You will turn it on later. 
Choose Next.

.On the Review page, review your secret details, and then choose Store.


2.  Update your code

Your code must assume the IAM role RoleToRetrieveSecretAtRuntime to be able to retrieve 
the secret. For more information, see Switching to an IAM role (AWS API).

Next, you update your code to retrieve the secret from Secrets Manager 
using the sample code provided by Secrets Manager.

To find the sample code

.Open the Secrets Manager console at https://console.aws.amazon.com/secretsmanager/
.On the Secrets page, choose your secret.
.Scroll down to Sample code. Choose your language, and then copy the code snippet.

P.S:
In your application, remove the hardcoded credentials and paste the code snippet. 
Depending on your code language, you might need to add a call to the function or 
method in the snippet.

3. Rotate the Secret:
The last step is to revoke the hardcoded credentials by rotating the secret. 
Rotation is the process of periodically updating a secret. When you rotate a secret, 
you update the credentials in both the secret and the database.
Secrets Manager can automatically rotate a secret for you on a schedule you set.

The last step is to revoke the hardcoded credentials by rotating the secret. 
Rotation is the process of periodically updating a secret. 
When you rotate a secret, you update the credentials in both the secret and the database. 
Secrets Manager can automatically rotate a secret for you on a schedule you set.

Part of setting up rotation is ensuring that the [[Lambda Rotation Function]] can access both 
[[Secrets Manager]] and your [[Database]]. When you turn on automatic rotation, 
Secrets Manager creates the Lambda rotation function in the same VPC as your database so that 
it has network access to the database. The Lambda rotation function must also be able to make 
calls to Secrets Manager to update the secret. We recommend that you create a Secrets Manager 
endpoint in the VPC so that calls from Lambda to Secrets Manager dont leave AWS infrastructure.
For instructions, see Using an AWS Secrets Manager VPC endpoint.

3.Turn On Rotation.

.Open the Secrets Manager console at https://console.aws.amazon.com/secretsmanager/.

.On the Secrets page, choose your secret.

.On the Secret details page, in the Rotation configuration section, choose Edit rotation.

.In the Edit rotation configuration dialog box, do the following:

.Turn on Automatic rotation.

.Under Rotation schedule, enter your schedule in UTC time zone.

.Choose Rotate immediately when the secret is stored to rotate your secret when you save your changes.

.Under Rotation function, choose Create a new Lambda function and enter a name for your new function. Secrets Manager adds "SecretsManager" to the beginning of your function name.

.For Rotation strategy, choose Single user.

Choose Save.

To check that the secret rotated
.Open the Secrets Manager console at https://console.aws.amazon.com/secretsmanager/.

.Choose Secrets, and then choose the secret.

.On the Secret details page, scroll down and choose Retrieve secret value.

If the secret value changed, then rotation succeeded. If the secret value didnT change, 
you need to Troubleshoot rotation by looking at the CloudWatch Logs for the rotation function.

Test that your application works as expected with the rotated secret.