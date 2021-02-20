package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;

import java.util.HashMap;
import java.util.Map;

public class Service02Stack extends Stack {
	public Service02Stack(final Construct scope, final String id, Cluster cluster) {
		this(scope, id, null, cluster);
	}

	public Service02Stack(final Construct scope, final String id, final StackProps props, Cluster cluster) {
		super(scope, id, props);

		Map<String, String> envVariables = new HashMap<>();
		
		envVariables.put("AWS_REGION", "sa-east-1");
		
		ApplicationLoadBalancedFargateService service02 = ApplicationLoadBalancedFargateService.Builder.create(this, "ALB02")
				.serviceName("service-02")
				.cluster(cluster)
				.cpu(256)
				.memoryLimitMiB(512)
				.desiredCount(2)
				.listenerPort(9090)
				.taskImageOptions(
						ApplicationLoadBalancedTaskImageOptions.builder()
								.containerName("aws_project02")
								.image(ContainerImage.fromRegistry("flavioreboucassantos/curso_aws-ecs-fargate-java_aws_project02:1.0.0"))
								.containerPort(9090)
								.logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
										.logGroup(LogGroup.Builder.create(this, "Service02LogGroup")
												.logGroupName("Service02")
												.removalPolicy(RemovalPolicy.DESTROY)
												.build())
										.streamPrefix("Service02")
										.build()))
								.environment(envVariables)
								.build())
				.publicLoadBalancer(true)
				.build();

		service02.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
				.path("/actuator/health")
				.port("9090")
				.healthyHttpCodes("200")
				.build());

		ScalableTaskCount scalableTaskCount = service02.getService().autoScaleTaskCount(EnableScalingProps.builder()
				.minCapacity(2)
				.maxCapacity(4)
				.build());

		scalableTaskCount.scaleOnCpuUtilization("Service02AutoScaling", CpuUtilizationScalingProps.builder()
				.targetUtilizationPercent(50)
				.scaleInCooldown(Duration.seconds(60))
				.scaleOutCooldown(Duration.seconds(60))
				.build());
	}
}
