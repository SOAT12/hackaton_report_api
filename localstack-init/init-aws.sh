#!/bin/bash
awslocal s3 mb s3://reports-bucket
awslocal sqs create-queue --queue-name status-update-queue
