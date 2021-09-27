#OrderManagement solution

##  Assumptions
* Only one warehouse

##Installation

* During the creation of the stack the first time, there may sometimes be problems with the notificationconfiguration of the S3 bucket. to get through this, just comment out that part during creation and then add it back in and run an update  of the stack.

``` 
  OrderMgmtS3bucket:
    Type: AWS::S3::Bucket
    DependsOn: FileUpdateFunction
    Description: "Bucket where inventory and product lists will be uploaded and where they will be read and stored in DynamoDB.
    After, the processed files will be moved to the history folder"
    Properties:
      Tags:
        - Key: ADMIN
          Value: !Ref Admin
        - Key: PROJECT
          Value: !Ref ProjectName
        - Key: ENVIRONMENT
          Value: !Ref Environment
  #    NotificationConfiguration:
  #      LambdaConfigurations:
  #        - Event: 's3:ObjectCreated:*'
  #          Filter:
  #            S3Key:
  #              Rules:
  #                - Name: prefix
  #                  Value: update/
  #          Function: !GetAtt FileUpdateFunction.Arn

```
* 