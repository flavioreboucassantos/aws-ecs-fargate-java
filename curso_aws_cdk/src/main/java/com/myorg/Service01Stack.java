package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;

import java.util.HashMap;
import java.util.Map;

public class Service01Stack extends Stack {
	public Service01Stack(final Construct scope, final String id, Cluster cluster) {
		this(scope, id, null, cluster);
	}

	public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
		super(scope, id, props);

		Map<String, String> envVariables = new HashMap<>();
		envVariables.put("SPRING_DATASOURCE_URL", "jdbc:mariadb://"
				+ Fn.importValue("rds-endpoint")
				+ ":3306/aws_project01?createDatabaseIfNotExist=true");
		envVariables.put("SPRING_DATASOURCE_USERNAME", "admin");
		envVariables.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("rds-password"));

		ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService.Builder.create(this, "ALB01")
				.serviceName("service-01")
				.cluster(cluster)
				.cpu(256)
				.memoryLimitMiB(512)
				.desiredCount(2)
				.listenerPort(8080)
				.taskImageOptions(
						ApplicationLoadBalancedTaskImageOptions.builder()
								.containerName("aws_project01")
								.image(ContainerImage.fromRegistry("flavioreboucassantos/curso_aws-ecs-fargate-java_aws_project01:1.3.0"))
								.containerPort(8080)
								.logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
										.logGroup(LogGroup.Builder.create(this, "Service01LogGroup")
												.logGroupName("Service01")
												.removalPolicy(RemovalPolicy.DESTROY)
												.build())
										.streamPrefix("Service01")
										.build()))
								.environment(envVariables)
								.build())
				.publicLoadBalancer(true)
				.build();

		service01.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
				.path("/actuator/health")
				.port("8080")
				.healthyHttpCodes("200")
				.build());

		ScalableTaskCount scalableTaskCount = service01.getService().autoScaleTaskCount(EnableScalingProps.builder()
				.minCapacity(2)
				.maxCapacity(8)
				.build());

		scalableTaskCount.scaleOnCpuUtilization("Service01AutoScaling", CpuUtilizationScalingProps.builder()
				.targetUtilizationPercent(50)
				.scaleInCooldown(Duration.seconds(60))
				.scaleOutCooldown(Duration.seconds(60))
				.build());
	}
}
